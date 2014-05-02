package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.Optimizer;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

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
	
	
	private double numberOfDaysUpper = 20;
	private double numberOfDaysLower = 20;
	private double maxNumberOfDays = 40;
	
	private double bandFactorUpper=2;
	private double bandDamperUpper=0;
	private double bandFactorLower=2;
	private double bandDamperLower=0;
	private double maxBandFactor=4;
	
	private BollingerBandObjFunc bollingerBandObjFunc;
	
	private Optimizer<double[]> optimizer = new Optimizer<double[]>();
	
	
	private Text numberOfDaystextUpper;
	private Text bandFactortextUpper;
	private Button btnBollingerBands;
	private Label lblMovingAverage;
	private Slider movingAverageSliderUpper;
	private Label lblBandFactor;
	private Slider bandFactorSliderUpper;
	private Composite OptButtons;
	private Button btnLoad;
	private Button btnOpt;
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
	private Label lblDamperd;
	private Text damperTextUpper;
	private Slider damperSliderUpper;
	private Label lblDamperd_1;
	private Text damperTextLower;
	private Slider damperSliderLower;
	
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
				damperSliderUpper.setEnabled(btnBollingerBands.getSelection());
				
				movingAverageSliderLower.setEnabled(btnBollingerBands.getSelection());
				bandFactorSliderLower.setEnabled(btnBollingerBands.getSelection());
				damperSliderLower.setEnabled(btnBollingerBands.getSelection());
				
				btnReset.setEnabled(btnBollingerBands.getSelection());
				//btnLoad.setEnabled(btnBollingerBands.getSelection());
				btnOpt.setEnabled(btnBollingerBands.getSelection());
				
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
				
				numberOfDaysUpper = 20;
				numberOfDaysLower = 20;
				
				bandFactorUpper=2;
				bandDamperUpper=0;
				bandFactorLower=2;
				bandDamperLower=0;
				
				reset();
				
			}
		});
		btnReset.setText("Reset");
		btnReset.setEnabled(false);
		
		lblNewLabel = new Label(OptButtons, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnLoad = new Button(OptButtons, SWT.NONE);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				double[] g=rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().getFirst().getDoubleArray();
				
				getBollingerBandObjFunc().setPeriod(period);
				double v=getBollingerBandObjFunc().compute(g, null);
				resetGuiData(g,v);
				
			}
		});
		btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnLoad.setText("Load");
		btnLoad.setEnabled(false);
		
		btnOpt = new Button(OptButtons, SWT.NONE);
		btnOpt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int dimension=6;
				getBollingerBandObjFunc().setPeriod(period);
				
				
				final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));
				
				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						getBollingerBandObjFunc(), gpm, dimension, 0, 1, rate.getOptResultsMap()
								.get(Type.BILLINGER_BAND));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;
				
				
				//Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(
						rate,optimizer,
						OptimizationResults.OptimizationTypeToString(Type.BILLINGER_BAND),
						partService, modelService, application, context);
				
				
				optimizer.initOptimizationInfo(eventBroker,Type.BILLINGER_BAND,rate, wizard.getAlgorithm(), wizard.getTerm());
				optimizer.schedule();
				
				btnOpt.setEnabled(false);
				btnBollingerBands.setEnabled(false);
				
				
				
			}
		});
		btnOpt.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnOpt.setText("Opt.");
		btnOpt.setEnabled(false);
	
		
		
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
		
		lblDamperd = new Label(this, SWT.NONE);
		lblDamperd.setText("Damper [d]:");
		
		damperTextUpper = new Text(this, SWT.BORDER);
		damperTextUpper.setEditable(false);
		damperTextUpper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		damperTextUpper.setText(String.format("%,.1f%%",  bandDamperUpper));
		
		damperSliderUpper = new Slider(this, SWT.NONE);
		damperSliderUpper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		damperSliderUpper.setThumb(1);
		damperSliderUpper.setPageIncrement(1);
		damperSliderUpper.setEnabled(false);
		damperSliderUpper.setMaximum((int)( 1000 *maxBandFactor/2) );
		damperSliderUpper.setMinimum(1);
		damperSliderUpper.setSelection((int)(bandDamperUpper*1000));
		damperSliderUpper.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bandDamperUpper=(((double) damperSliderUpper.getSelection())/1000.0);
				damperTextUpper.setText(String.format("%,.1f%%",  bandDamperUpper));
				
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
		numberOfDaysTextLower.setText(String.valueOf((int)numberOfDaysLower));
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
				numberOfDaysTextLower.setText(String.valueOf((int)numberOfDaysLower));
				
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
				bandFactorTextLower.setText(String.format("%,.1f%%",  bandFactorLower));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
			}
		});
		bandFactorSliderLower.setEnabled(false);
		//TODO
		lblDamperd_1 = new Label(this, SWT.NONE);
		lblDamperd_1.setText("Damper [d]:");
		
		damperTextLower = new Text(this, SWT.BORDER);
		damperTextLower.setEditable(false);
		damperTextLower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		damperTextLower.setText(String.format("%,.1f%%",  bandDamperLower));
		
		damperSliderLower = new Slider(this, SWT.NONE);
		damperSliderLower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		damperSliderLower.setThumb(1);
		damperSliderLower.setPageIncrement(1);
		damperSliderLower.setEnabled(false);
		damperSliderLower.setMaximum((int)( 1000 *maxBandFactor/2) );
		damperSliderLower.setMinimum(1);
		damperSliderLower.setSelection((int)(bandDamperLower*1000));
		damperSliderLower.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bandDamperLower=(((double) damperSliderLower.getSelection())/1000.0);
				damperTextLower.setText(String.format("%,.1f%%",  bandDamperLower));
				
				if(btnBollingerBands.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		lblProfit = new Label(this, SWT.NONE);
		lblProfit.setText("Profit:");
		new Label(this, SWT.NONE);
		
		bollingerBandProfitlbl = new Label(this, SWT.NONE);
		bollingerBandProfitlbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		bollingerBandProfitlbl.setText("000.00%");
		
		lblBuyLimits = new Label(this, SWT.NONE);
		lblBuyLimits.setText("Day buy limits:");
		new Label(this, SWT.NONE);
		
		dayBuyLimitsLbl = new Label(this, SWT.NONE);
		dayBuyLimitsLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dayBuyLimitsLbl.setText("[000.000*-000.000*]");
		
		lblSellLimits = new Label(this, SWT.NONE);
		lblSellLimits.setText("Day sell limits:");
		new Label(this, SWT.NONE);
		
		daySellLimitsLbl = new Label(this, SWT.NONE);
		daySellLimitsLbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		daySellLimitsLbl.setText("[000.000*-000.000*]");
		
		
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
	
	
	/////////////////////////////
	////  EVENT REACTIONS    ////
	/////////////////////////////
	private boolean isCompositeAbleToReact(String rate_uuid){
		if (this.isDisposed())
			return false;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || bollingerBandProfitlbl == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	private void resetGuiData(double[] g,double v){
		
		
		//Upper
		numberOfDaysUpper=Math.round(g[0]*maxNumberOfDays);
		if(numberOfDaysUpper<1)
			numberOfDaysUpper=1;
		bandFactorUpper=maxBandFactor*g[1];
		bandDamperUpper=maxBandFactor*g[2]/2;
		
		
		//Lower
		numberOfDaysLower=Math.round(g[3]*maxNumberOfDays);
		if(numberOfDaysLower<1)
			numberOfDaysLower=1;
		bandFactorLower=maxBandFactor*g[4];
		bandDamperLower=maxBandFactor*g[5]/2;
		
		
		
		//Profit
		float profit=maxProfit- (float)v;
		String movAvgProfitString = String.format("%,.2f%%",
				profit * 100);
		bollingerBandProfitlbl.setText(movAvgProfitString);
		
		reset();
		
	}
	
	private void reset(){
		//Upper
		numberOfDaystextUpper.setText(String.valueOf((int)numberOfDaysUpper));
		movingAverageSliderUpper.setSelection((int)numberOfDaysUpper);
		bandFactortextUpper.setText(String.format("%,.1f%%",  bandFactorUpper));
		bandFactorSliderUpper.setSelection((int)(bandFactorUpper*1000));
		damperTextUpper.setText(String.format("%,.1f%%",  bandDamperUpper));
		damperSliderUpper.setSelection((int)(bandDamperUpper*1000));
		
		//Lower
		numberOfDaysTextLower.setText(String.valueOf((int)numberOfDaysLower));
		movingAverageSliderLower.setSelection((int)numberOfDaysLower);
		bandFactorTextLower.setText(String.format("%,.1f%%",  bandFactorLower));
		bandFactorSliderLower.setSelection((int)(bandFactorLower*1000));
		damperTextLower.setText(String.format("%,.1f%%",  bandDamperLower));
		damperSliderLower.setSelection((int)(bandDamperLower*1000));
		
		fireCollectionRemoved();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().isEmpty()){
			double[] g=rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().getFirst().getDoubleArray();
			
			getBollingerBandObjFunc().setPeriod(period);
			double v=getBollingerBandObjFunc().compute(g, null);
			resetGuiData(g,v);
			
			btnLoad.setEnabled(true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerFinished(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_FINISHED) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.BILLINGER_BAND){
			btnBollingerBands.setEnabled(true);
			btnOpt.setEnabled(true);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.BILLINGER_BAND){
			Individual<double[], double[]> individual=info.getBest();
			resetGuiData(individual.g,individual.v);
		}
		
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
		
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_MovingAverage_Upper);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_MovingAverage_Lower);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_MovingAverage);
		
		
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_UpperBand_Max);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_UpperBand_Min);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_LowerBand_Max);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_LowerBand_Min);
		
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_Buy_Signal);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_Buy_Signal);
		
		
		removeSerie(secondCollection,BollingerBandObjFunc.BollingerBand_Profit);
			
	}
	
	private void removeSerie(XYSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}

	
	private void resetChartDataSet() {
		clearCollections();
		
		if(!btnBollingerBands.getSelection())return;
		
		//Refresh the main plot
		double[] x=new double[6];
		x[0]=numberOfDaysUpper/maxNumberOfDays;
		x[1]=bandFactorUpper/maxBandFactor;
		x[2]=2*bandDamperUpper/maxBandFactor;
		
		x[3]=numberOfDaysLower/maxNumberOfDays;
		x[4]=bandFactorLower/maxBandFactor;
		x[5]=2*bandDamperLower/maxBandFactor;
		
		getBollingerBandObjFunc().setPeriod(period);
		getBollingerBandObjFunc().compute(x, null);
		
		//Moving Average Series
		//addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getMovingAverageUpperSeries(),Color.GRAY);
		//addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getMovingAverageLowerSeries(),Color.GRAY);
		addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getMovingAverageSeries(),Color.GRAY);
		
		//Bands
		addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getUpperBandMaxSeries(),Color.ORANGE);
		addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getUpperBandMinSeries(),Color.ORANGE);
		
		addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getLowerBandMaxSeries(),Color.WHITE);
		addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getLowerBandMinSeries(),Color.WHITE);
		
		//Profit
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getBollingerBandObjFunc().getBuySignalSeries(),
				ShapeUtilities.createUpTriangle(5),Color.GREEN);
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getBollingerBandObjFunc().getSellSignalSeries(),
				ShapeUtilities.createDownTriangle(5),Color.RED);
		
		
		addSeriesAsLine(secondPlotrenderer,secondCollection,getBollingerBandObjFunc().getProfitSeries(),Color.WHITE);
		
		
		String movAvgProfitString = String.format("%,.2f%%",
				getBollingerBandObjFunc().getProfit() * 100);
		bollingerBandProfitlbl.setText(movAvgProfitString);
		//TODO
		String daySellLimits_str="["+
				String.format("%,.3f",getBollingerBandObjFunc().getDaySellUpLimit())+
				((getBollingerBandObjFunc().isDaySellUpLimitIsActivated())?"*":"")+
				"-"+
				String.format("%,.3f",getBollingerBandObjFunc().getDaySellDownLimit())+
				((getBollingerBandObjFunc().isDaySellDownLimitIsActivated())?"*":"")+
				"]";
		
		daySellLimitsLbl.setText(daySellLimits_str);
		
		String dayBuyLimits_str="["+
				String.format("%,.3f",getBollingerBandObjFunc().getDayBuyUpLimit())+
				((getBollingerBandObjFunc().isDayBuyUpLimitIsActivated())?"*":"")+
				"-"+
				String.format("%,.3f",getBollingerBandObjFunc().getDayBuyDownLimit())+
				((getBollingerBandObjFunc().isDayBuyDownLimitIsActivated())?"*":"")+
				"]";
		
		dayBuyLimitsLbl.setText(dayBuyLimits_str);
		
		
		
	}
	
	private void addSeriesAsLine(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Color color){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			rend.setSeriesShapesVisible(pos, false);
			rend.setSeriesLinesVisible(pos, true);
			rend.setSeriesStroke(pos,new BasicStroke(2.0f));
			rend.setSeriesPaint(pos, color);
		}
	}
	
	private void addSeriesAsShape(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Shape shape,Color color){
		
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
	private Label lblBuyLimits;
	private Label lblSellLimits;
	private Label dayBuyLimitsLbl;
	private Label daySellLimitsLbl;
	
	

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
