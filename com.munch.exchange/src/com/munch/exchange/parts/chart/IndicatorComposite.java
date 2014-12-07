package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.parts.composite.CollectionRemovedListener;
import com.munch.exchange.services.IExchangeRateProvider;

public abstract class IndicatorComposite extends Composite {
	
	private static Logger logger = Logger.getLogger(IndicatorComposite.class);
	
	@Inject
	IEclipseContext context;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	EPartService partService;
	
	@Inject
	private MApplication application;
	
	@Inject
	private Shell shell;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	protected ExchangeRate rate;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	
	private Button activatorBtn;
	protected HashMap<String,IndicatorParameter> paramMap=new HashMap<String, IndicatorParameter>();
	
	//the period
	private int[] period=new int[2];
	//The renderers
	protected XYLineAndShapeRenderer mainPlotRenderer;
	protected XYLineAndShapeRenderer secondPlotrenderer;
	protected XYLineAndShapeRenderer percentPlotrenderer;
	protected XYErrorRenderer errorPlotRenderer;
	protected DeviationRenderer deviationPercentPlotRenderer;
	protected DeviationRenderer deviationRenderer ;
				
	//Series Collections
	//The Series Collections
	protected XYSeriesCollection mainCollection;
	protected XYSeriesCollection secondCollection;
	protected XYSeriesCollection percentCollection;
	protected YIntervalSeriesCollection errorCollection;
	protected YIntervalSeriesCollection deviationPercentCollection;
	protected YIntervalSeriesCollection deviationCollection;
		
	
	
	@Inject
	public IndicatorComposite(String name, Composite parent) {
		super(parent, SWT.NONE);
		this.setLayout(new GridLayout(3, false));
		
		activatorBtn = new Button(this, SWT.CHECK);
		activatorBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(IndicatorParameter param:paramMap.values())
					param.getSlider().setEnabled(activatorBtn.getSelection());
				
				resetChartDataSet();
				
				if(!activatorBtn.getSelection())
					fireCollectionRemoved();
				
			}
		});
		activatorBtn.setText(name);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		//Create the parameters
		createParameters();
		
	}
	
	protected abstract void createParameters();
	
	
	public Button getActivatorBtn() {
		return activatorBtn;
	}
	
	public void setRenderers(XYLineAndShapeRenderer mainPlotRenderer,
			XYLineAndShapeRenderer secondPlotrenderer,
			XYLineAndShapeRenderer percentPlotrenderer,
			XYErrorRenderer errorPlotRenderer,
			DeviationRenderer deviationPercentPlotRenderer,
			DeviationRenderer deviationRenderer){
		this.mainPlotRenderer=mainPlotRenderer;
		this.secondPlotrenderer=secondPlotrenderer;
		this.percentPlotrenderer=percentPlotrenderer;
		this.errorPlotRenderer=errorPlotRenderer;
		this.deviationPercentPlotRenderer=deviationPercentPlotRenderer;
		this.deviationRenderer=deviationRenderer;
	}
	
	public void setSeriesCollections(XYSeriesCollection mainCollection,
			XYSeriesCollection secondCollection,
			XYSeriesCollection percentCollection,
			YIntervalSeriesCollection errorCollection,
			YIntervalSeriesCollection deviationPercentCollection,
			YIntervalSeriesCollection deviationCollection){
		this.mainCollection=mainCollection;
		this.secondCollection=secondCollection;
		this.percentCollection=percentCollection;
		this.errorCollection=errorCollection;
		this.deviationPercentCollection=deviationPercentCollection;
		this.deviationCollection=deviationCollection;
	}

	public void setPeriod(int[] period){
		this.period=period;
		
		resetChartDataSet();
	}
	
	// ///////////////////////////
	// // EVENT REACTIONS ////
	// ///////////////////////////
	private boolean isCompositeAbleToReact(String rate_uuid) {
		if (this.isDisposed())
			return false;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || activatorBtn == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;

		return true;
	}
	
	protected abstract void clearSeries();
	
	protected void removeSerie(XYSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}
	
	protected void removeDevSerie(YIntervalSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}
	
	private void resetChartDataSet() {
		clearSeries();
		if(!activatorBtn.getSelection())return;
		
		createSeries();
	}
	
	protected abstract void createSeries();
	
	
	
	protected XYSeries  createSerieFromPeriod(String name,double[] price){
		XYSeries r_series =new XYSeries(name);
		int pos=1;
		for(int i=0;i<price.length;i++){
			if(i>=period[0] && i<period[1]){
				r_series.add(pos, price[i]);
				pos++;
			}
		}
		
		return 	r_series;
	}
	
	
	protected void addDeviationSerie(DeviationRenderer rend,YIntervalSeriesCollection col,YIntervalSeries series,Color color ){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			rend.setSeriesPaint(pos, color);
			rend.setSeriesStroke(pos, new BasicStroke(0.3f, BasicStroke.CAP_ROUND,
	                BasicStroke.JOIN_ROUND));
			rend.setSeriesFillPaint(pos, color);
		}
		
		
	}
	
	protected void addSeriesAsLine(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Color color){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			rend.setSeriesShapesVisible(pos, false);
			rend.setSeriesLinesVisible(pos, true);
			rend.setSeriesStroke(pos,new BasicStroke(2.0f));
			rend.setSeriesPaint(pos, color);
		}
	}
	
	protected void addSeriesAsShape(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Shape shape,Color color){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			
			rend.setSeriesShapesVisible(pos, true);
			rend.setSeriesLinesVisible(pos, false);
			rend.setSeriesShape(pos,shape);
			rend.setSeriesShapesFilled(pos, true);
			rend.setSeriesPaint(pos, color);
			rend.setSeriesOutlinePaint(pos, Color.BLACK);
			rend.setSeriesOutlineStroke(pos, new BasicStroke(1.0f));
			rend.setUseOutlinePaint(true);
		}
	}

	// ///////////////////////////
	// // LISTERNER ////
	// ///////////////////////////
	private List<CollectionRemovedListener> listeners = new LinkedList<CollectionRemovedListener>();

	public void addCollectionRemovedListener(CollectionRemovedListener l) {
		listeners.add(l);
	}

	public void removeCollectionRemovedListener(CollectionRemovedListener l) {
		listeners.remove(l);
	}

	public void fireCollectionRemoved() {
		for (CollectionRemovedListener l : listeners)
			l.CollectionRemoved();
	}
	
}
