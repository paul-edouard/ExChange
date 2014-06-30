package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.IExchangeRateProvider;

import org.eclipse.swt.widgets.Label;

public class RateChartLawAndHightComposite extends Composite {
	
	public static final String LOW_AND_HIGH="Law and High";
	public static final String CLOSE_PRICE="Close Price";
	public static final String CANDLESTICK="Candlestick";
	
	private static Logger logger = Logger.getLogger(RateChartLawAndHightComposite.class);
	
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
	private ExchangeRate rate;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	
	//Renderers
	private XYLineAndShapeRenderer mainPlotRenderer;
	private XYLineAndShapeRenderer secondPlotrenderer;
	private XYErrorRenderer errorPlotRenderer;
	private CandlestickRenderer candlestickRenderer;
	
	//Series Collections
	private XYSeriesCollection mainCollection;
	private XYSeriesCollection secondCollection;
	private YIntervalSeriesCollection errorCollection;
	private OHLCSeriesCollection oHLCSeriesCollection;
	
	// set the period and max profit
	private int[] period=new int[2];
	private float maxProfit=0;
	
	private Button btnLowHight;
	private Button btnClose;
	private Button btnCandlestick;

	@Inject
	public RateChartLawAndHightComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		
		btnClose = new Button(this, SWT.CHECK);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetChartDataSet();
				if(!btnClose.getSelection())
					fireCollectionRemoved();
				
			}
		});
		btnClose.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnClose.setText("Close");
		
		btnLowHight = new Button(this, SWT.CHECK);
		btnLowHight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetChartDataSet();
				if(!btnLowHight.getSelection())
					fireCollectionRemoved();
			}
		});
		
		btnLowHight.setText("Low & Hight");
		
		
		btnCandlestick = new Button(this, SWT.CHECK);
		btnCandlestick.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetChartDataSet();
				if(!btnCandlestick.getSelection())
					fireCollectionRemoved();
			}
		});
		btnCandlestick.setText("Candlestick");
		
		
		btnCandlestick.setSelection(true);
		
	}
	
	public void setRenderers(XYLineAndShapeRenderer mainPlotRenderer,
							XYLineAndShapeRenderer secondPlotrenderer,
							XYErrorRenderer errorPlotRenderer,
							CandlestickRenderer candlestickRenderer){
		this.mainPlotRenderer=mainPlotRenderer;
		this.secondPlotrenderer=secondPlotrenderer;
		this.errorPlotRenderer=errorPlotRenderer;
		this.candlestickRenderer=candlestickRenderer;
	}
	
	public void setSeriesCollections(XYSeriesCollection mainCollection,
							XYSeriesCollection secondCollection,
							YIntervalSeriesCollection errorCollection,
							OHLCSeriesCollection oHLCSeriesCollection){
		this.mainCollection=mainCollection;
		this.secondCollection=secondCollection;
		this.errorCollection=errorCollection;
		this.oHLCSeriesCollection=oHLCSeriesCollection;
	}

	public void setPeriodandMaxProfit(int[] period,float maxProfit){
		this.period=period;
		this.maxProfit=maxProfit;
		
		resetChartDataSet();
	}
	
	private void  clearCollections(){
		/*
		int low_pos=mainCollection.indexOf(HistoricalPoint.FIELD_Low);
		if(low_pos>=0)mainCollection.removeSeries(low_pos);
		
		int hight_pos=mainCollection.indexOf(HistoricalPoint.FIELD_High);
		if(hight_pos>=0)mainCollection.removeSeries(hight_pos);
		*/
		
		int pos=errorCollection.indexOf(LOW_AND_HIGH);
		if (pos>=0)errorCollection.removeSeries(pos);
		
		pos=oHLCSeriesCollection.indexOf(CANDLESTICK);
		if(pos>=0)oHLCSeriesCollection.removeSeries(pos);
		
		pos=mainCollection.indexOf(HistoricalPoint.FIELD_Close);
		if(pos>=0)mainCollection.removeSeries(pos);
		
		
		
		//mainPlotRenderer.addChangeListener(listener);
	}
	
	private void resetChartDataSet() {
		
		clearCollections();
		
		int fiel_pos=0;
		
		if(btnCandlestick.getSelection()){
			OHLCSeries Seriepos=rate.getHistoricalData().getPosOHLCSeries(CANDLESTICK,period);
			oHLCSeriesCollection.addSeries(Seriepos);
			fiel_pos=oHLCSeriesCollection.indexOf(CANDLESTICK);
			if(fiel_pos>=0){
				candlestickRenderer.setSeriesPaint(fiel_pos, Color.black);
				candlestickRenderer.setSeriesStroke(fiel_pos,new BasicStroke(1.5f));
			}
		}
		
		if(btnLowHight.getSelection()){
			//Error Low and Higth
			YIntervalSeries inter_series= rate.getHistoricalData().getYIntervalSeries(LOW_AND_HIGH, period);
			errorCollection.addSeries(inter_series);
			int pos=errorCollection.indexOf(LOW_AND_HIGH);
			if(pos>=0){
				errorPlotRenderer.setSeriesShapesVisible(pos, false);
				errorPlotRenderer.setSeriesLinesVisible(pos, false);
				errorPlotRenderer.setSeriesStroke(pos,new BasicStroke(1.0f));
				errorPlotRenderer.setSeriesPaint(pos, Color.BLUE);
			}
		}
		
		if(btnClose.getSelection()){
			System.out.println("Close Selected");
			XYSeries series = rate.getHistoricalData().getXYSeries(HistoricalPoint.FIELD_Close, period);
			mainCollection.addSeries(series);
			fiel_pos=mainCollection.indexOf(HistoricalPoint.FIELD_Close);
			if(fiel_pos>=0){
				mainPlotRenderer.setSeriesShapesVisible(fiel_pos, false);
				mainPlotRenderer.setSeriesLinesVisible(fiel_pos, true);
				mainPlotRenderer.setSeriesStroke(fiel_pos,new BasicStroke(2.0f));
				mainPlotRenderer.setSeriesPaint(fiel_pos, Color.BLUE);
			}
		}
			
		
		
		
		/*
		//LOW
		XYSeries lowSeries = rate.getHistoricalData().getXYSeries(HistoricalPoint.FIELD_Low, period);
		mainCollection.addSeries(lowSeries);
		
		int low_pos=mainCollection.indexOf(HistoricalPoint.FIELD_Low);
		if(low_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(low_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(low_pos, true);
			mainPlotRenderer.setSeriesStroke(low_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(low_pos, new Color(255, 50, 50));
		}
		
		//HIGHT
		XYSeries hightSeries = rate.getHistoricalData().getXYSeries(HistoricalPoint.FIELD_High, period);
		mainCollection.addSeries(hightSeries);
		
		int hight_pos=mainCollection.indexOf(HistoricalPoint.FIELD_High);
		if(hight_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(hight_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(hight_pos, true);
			mainPlotRenderer.setSeriesStroke(hight_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(hight_pos, new Color(50, 50, 255));
		}
		*/
		
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

	private void fireCollectionRemoved() {
		for (CollectionRemovedListener l : listeners)
			l.CollectionRemoved();
	}
	

}
