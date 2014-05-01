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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
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
	
	private Text numberOfDaystextUpper;
	private Text bandFactortextUpper;
	private Button btnBollingerBands;
	private Label lblMovingAverage;
	private Slider movingAverageSliderUpper;
	private Label lblBandFactor;
	private Slider bandFactorSliderUpper;
	
	
	private double numberOfDaysUpper = 20;
	private double numberOfDaysLower = 20;
	private double maxNumberOfDays = 100;
	
	private double bandFactorUpper=2;
	private double bandFactorLower=2;
	private double maxBandFactor=5;
	
	private BollingerBandObjFunc bollingerBandObjFunc;
	
	@Inject
	public RateChartBollingerBandsComposite(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		btnBollingerBands = new Button(this, SWT.CHECK);
		btnBollingerBands.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				movingAverageSliderUpper.setEnabled(btnBollingerBands.getSelection());
				bandFactorSliderUpper.setEnabled(btnBollingerBands.getSelection());
				
				resetChartDataSet();
				
				if(!btnBollingerBands.getSelection())
					fireCollectionRemoved();
				
			}
		});
		btnBollingerBands.setText("Bollinger Bands");
		new Label(this, SWT.NONE);
		
		
		////////////////////////////////
		//           BUTTONS          //
		////////////////////////////////
		
		OptButtons = new Composite(this, SWT.NONE);
		GridLayout gl_OptButtons = new GridLayout(7, false);
		gl_OptButtons.verticalSpacing = 1;
		gl_OptButtons.marginWidth = 1;
		gl_OptButtons.marginHeight = 1;
		gl_OptButtons.horizontalSpacing = 0;
		OptButtons.setLayout(gl_OptButtons);
		OptButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		btnReset = new Button(OptButtons, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReset.setText("Reset");
		
		
		lblNewLabel = new Label(OptButtons, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnLoad = new Button(OptButtons, SWT.NONE);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnLoad.setText("Load");
		
		btnSave = new Button(OptButtons, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSave.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSave.setText("Save");
		
		
		////////////////////////////////
		//            UPPER           //
		////////////////////////////////
		
		lblUpper = new Label(this, SWT.NONE);
		lblUpper.setText("Upper:");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		
		lblMovingAverage = new Label(this, SWT.NONE);
		lblMovingAverage.setText("Moving Average:");
		
		numberOfDaystextUpper = new Text(this, SWT.BORDER);
		numberOfDaystextUpper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		numberOfDaystextUpper.setText(String.valueOf((int)numberOfDaysUpper));
		numberOfDaystextUpper.setEditable(false);
		
		movingAverageSliderUpper = new Slider(this, SWT.NONE);
		movingAverageSliderUpper.setThumb(1);
		movingAverageSliderUpper.setPageIncrement(1);
		movingAverageSliderUpper.setEnabled(false);
		movingAverageSliderUpper.setMaximum((int) maxNumberOfDays);
		movingAverageSliderUpper.setMinimum(1);
		movingAverageSliderUpper.setSelection((int)numberOfDaysUpper);
		movingAverageSliderUpper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		movingAverageSliderUpper.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numberOfDaysUpper=((double) movingAverageSliderUpper.getSelection());
				numberOfDaystextUpper.setText(String.valueOf((int)numberOfDaysUpper));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		lblBandFactor = new Label(this, SWT.NONE);
		lblBandFactor.setText("Band Factor [k]:");
		
		bandFactortextUpper = new Text(this, SWT.BORDER);
		bandFactortextUpper.setEditable(false);
		bandFactortextUpper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bandFactortextUpper.setText(String.format("%,.1f%%",  bandFactorUpper));
		
		bandFactorSliderUpper = new Slider(this, SWT.NONE);
		bandFactorSliderUpper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		bandFactorSliderUpper.setThumb(1);
		bandFactorSliderUpper.setPageIncrement(1);
		bandFactorSliderUpper.setEnabled(false);
		bandFactorSliderUpper.setMaximum((int)( 1000 *maxBandFactor) );
		bandFactorSliderUpper.setMinimum(1);
		bandFactorSliderUpper.setSelection((int)(bandFactorUpper*1000));
		bandFactorSliderUpper.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				bandFactorUpper=(((double) bandFactorSliderUpper.getSelection())/1000.0);
				bandFactortextUpper.setText(String.format("%,.1f%%",  bandFactorUpper));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		////////////////////////////////
		//            LOWER           //
		////////////////////////////////
		
		lblLower = new Label(this, SWT.NONE);
		lblLower.setText("Lower:");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblMovingAverage_1 = new Label(this, SWT.NONE);
		lblMovingAverage_1.setText("Moving Average:");
		
		numberOfDaysTextLower = new Text(this, SWT.BORDER);
		numberOfDaystextUpper.setText(String.valueOf((int)numberOfDaysLower));
		numberOfDaysTextLower.setEditable(false);
		numberOfDaysTextLower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		movingAverageSliderLower = new Slider(this, SWT.NONE);
		movingAverageSliderLower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		movingAverageSliderLower.setThumb(1);
		movingAverageSliderLower.setPageIncrement(1);
		movingAverageSliderLower.setEnabled(false);
		movingAverageSliderLower.setMaximum((int) maxNumberOfDays);
		movingAverageSliderLower.setMinimum(1);
		movingAverageSliderLower.setSelection((int)numberOfDaysLower);
		movingAverageSliderLower.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numberOfDaysLower=((double) movingAverageSliderLower.getSelection());
				numberOfDaystextUpper.setText(String.valueOf((int)numberOfDaysLower));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
			}
		});
		movingAverageSliderLower.setEnabled(false);
		
		lblBandFactork = new Label(this, SWT.NONE);
		lblBandFactork.setText("Band Factor [k]:");
		
		bandFactorTextLower = new Text(this, SWT.BORDER);
		bandFactorTextLower.setText(String.format("%,.1f%%",  bandFactorLower));
		bandFactorTextLower.setEditable(false);
		bandFactorTextLower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		bandFactorSliderLower = new Slider(this, SWT.NONE);
		bandFactorSliderLower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		bandFactorSliderLower.setThumb(1);
		bandFactorSliderLower.setPageIncrement(1);
		bandFactorSliderLower.setEnabled(false);
		bandFactorSliderLower.setMaximum((int)( 1000 *maxBandFactor) );
		bandFactorSliderLower.setMinimum(1);
		bandFactorSliderLower.setSelection((int)(bandFactorLower*1000));
		bandFactorSliderLower.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bandFactorLower=(((double) bandFactorSliderLower.getSelection())/1000.0);
				bandFactortextUpper.setText(String.format("%,.1f%%",  bandFactorLower));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
			}
		});
		bandFactorSliderLower.setEnabled(false);
		
		lblProfit = new Label(this, SWT.NONE);
		lblProfit.setText("Profit:");
		new Label(this, SWT.NONE);
		
		bollingerBandProfitlbl = new Label(this, SWT.NONE);
		bollingerBandProfitlbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		bollingerBandProfitlbl.setText("0%");
		
		
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
	
	/**
	 * create the Bollinger Band function
	 * @return
	 */
	private BollingerBandObjFunc getBollingerBandObjFunc(){
		if(bollingerBandObjFunc!=null)return bollingerBandObjFunc;
		
		bollingerBandObjFunc=new BollingerBandObjFunc(
				 HistoricalPoint.FIELD_Close,
				 RateChart.PENALTY,
				 rate.getHistoricalData().getNoneEmptyPoints(),
				 maxNumberOfDays,
				 maxBandFactor,
				 maxProfit
				 );
		return bollingerBandObjFunc;
	}
	
	private void  clearCollections(){
		int mov_ave_pos=mainCollection.indexOf(BollingerBandObjFunc.BollingerBand_MovingAverage_Upper);
		if(mov_ave_pos>=0)mainCollection.removeSeries(mov_ave_pos);
		
		int upper_pos=mainCollection.indexOf(BollingerBandObjFunc.BollingerBand_UpperBand);
		if(upper_pos>=0)mainCollection.removeSeries(upper_pos);
		
		int lower_pos=mainCollection.indexOf(BollingerBandObjFunc.BollingerBand_LowerBand);
		if(lower_pos>=0)mainCollection.removeSeries(lower_pos);
		
		
	}

	
	private void resetChartDataSet() {
		clearCollections();
		
		if(!btnBollingerBands.getSelection())return;
		
		//Refresh the main plot
		double[] x=new double[2];
		x[0]=numberOfDaysUpper/maxNumberOfDays;
		x[1]=bandFactorUpper/maxBandFactor;
		
		getBollingerBandObjFunc().setPeriod(period);
		getBollingerBandObjFunc().compute(x, null);
		
		//Moving Average Series
		mainCollection.addSeries(getBollingerBandObjFunc().getMovingAverageUpperSeries());
		int mov_ave_pos=mainCollection.indexOf(BollingerBandObjFunc.BollingerBand_MovingAverage_Upper);
		if(mov_ave_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(mov_ave_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(mov_ave_pos, true);
			mainPlotRenderer.setSeriesStroke(mov_ave_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(mov_ave_pos, Color.GRAY);
		}
		
		//Upper Band Series
		mainCollection.addSeries(getBollingerBandObjFunc().getUpperBandSeries());
		int upper_pos=mainCollection.indexOf(BollingerBandObjFunc.BollingerBand_UpperBand);
		if(upper_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(upper_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(upper_pos, true);
			mainPlotRenderer.setSeriesStroke(upper_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(upper_pos, Color.RED);
		}
		
		//Upper Band Series
		mainCollection.addSeries(getBollingerBandObjFunc().getLowerBandSeries());
		int lower_pos=mainCollection.indexOf(BollingerBandObjFunc.BollingerBand_LowerBand);
		if(lower_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(lower_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(lower_pos, true);
			mainPlotRenderer.setSeriesStroke(lower_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(lower_pos, Color.WHITE);
		}
		
	}
	
	// ///////////////////////////
	// // LISTERNER ////
	// ///////////////////////////
	private List<CollectionRemovedListener> listeners = new LinkedList<CollectionRemovedListener>();
	private Composite OptButtons;
	private Button btnLoad;
	private Button btnSave;
	private Label lblNewLabel;
	private Button btnReset;
	private Label lblUpper;
	private Label lblLower;
	private Label lblMovingAverage_1;
	private Label lblBandFactork;
	private Text numberOfDaysTextLower;
	private Text bandFactorTextLower;
	private Slider movingAverageSliderLower;
	private Slider bandFactorSliderLower;
	private Label lblProfit;
	private Label bollingerBandProfitlbl;
	

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
