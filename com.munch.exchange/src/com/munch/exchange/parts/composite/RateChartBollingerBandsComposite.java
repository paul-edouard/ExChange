package com.munch.exchange.parts.composite;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.services.IExchangeRateProvider;

public class RateChartBollingerBandsComposite extends Composite {
	
	
	private static Logger logger = Logger.getLogger(RateChartMovingAverageComposite.class);
	
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
	
	//Series Collections
	private XYSeriesCollection mainCollection;
	private XYSeriesCollection secondCollection;
	
	//TODO set the period and max profit
	private int[] period=new int[2];
	private float maxProfit=0;
	
	private Text numberOfDaystext;
	private Text bandFactortext;
	private Button btnBollingerBands;
	private Label lblMovingAverage;
	private Slider movingAverageSlider;
	private Label lblBandFactor;
	private Slider bandFactorSlider;
	
	
	private double numberOfDays = 20;
	private double maxNumberOfDays = 100;
	
	private double bandFactor=2;
	private double maxBandFactor=5;
	
	@Inject
	public RateChartBollingerBandsComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));
		
		btnBollingerBands = new Button(this, SWT.CHECK);
		btnBollingerBands.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				movingAverageSlider.setEnabled(btnBollingerBands.getSelection());
				bandFactorSlider.setEnabled(btnBollingerBands.getSelection());
				
				resetChartDataSet();
				
				if(!btnBollingerBands.getSelection())
					fireCollectionRemoved();
				
			}
		});
		btnBollingerBands.setText("Bollinger Bands");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblMovingAverage = new Label(this, SWT.NONE);
		lblMovingAverage.setText("Moving Average:");
		
		numberOfDaystext = new Text(this, SWT.BORDER);
		numberOfDaystext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		numberOfDaystext.setText(String.valueOf((int)numberOfDays));
		numberOfDaystext.setEditable(false);
		
		movingAverageSlider = new Slider(this, SWT.NONE);
		movingAverageSlider.setThumb(1);
		movingAverageSlider.setPageIncrement(1);
		movingAverageSlider.setEnabled(false);
		movingAverageSlider.setMaximum((int) maxNumberOfDays);
		movingAverageSlider.setMinimum(1);
		movingAverageSlider.setSelection((int)numberOfDays);
		movingAverageSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movingAverageSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numberOfDays=((double) movingAverageSlider.getSelection());
				numberOfDaystext.setText(String.valueOf((int)numberOfDays));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		lblBandFactor = new Label(this, SWT.NONE);
		lblBandFactor.setText("Band Factor [k]:");
		
		bandFactortext = new Text(this, SWT.BORDER);
		bandFactortext.setEditable(false);
		bandFactortext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bandFactortext.setText(String.format("%,.1f%%",  bandFactor*100));
		
		bandFactorSlider = new Slider(this, SWT.NONE);
		bandFactorSlider.setThumb(1);
		bandFactorSlider.setPageIncrement(1);
		bandFactorSlider.setEnabled(false);
		bandFactorSlider.setMaximum((int)( 1000 *maxBandFactor) );
		bandFactorSlider.setMinimum(1);
		bandFactorSlider.setSelection((int)(bandFactor*1000));
		bandFactorSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				bandFactor=(((double) bandFactorSlider.getSelection())/1000.0);
				bandFactortext.setText(String.format("%,.1f%%",  bandFactor*1000));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		// TODO Auto-generated constructor stub
	}
	
	public void setRenderers(XYLineAndShapeRenderer mainPlotRenderer,XYLineAndShapeRenderer secondPlotrenderer){
		this.mainPlotRenderer=mainPlotRenderer;
		this.secondPlotrenderer=secondPlotrenderer;
	}
	
	public void setSeriesCollections(XYSeriesCollection mainCollection,XYSeriesCollection secondCollection){
		this.mainCollection=mainCollection;
		this.secondCollection=secondCollection;
	}

	public void setPeriodandMaxProfit(int[] period,float maxProfit){
		this.period=period;
		this.maxProfit=maxProfit;
		
		resetChartDataSet();
	}

	
	private void resetChartDataSet() {
		//TODO
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
