package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.wb.swt.SWTResourceManager;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.ShapeUtilities;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.Optimizer;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.job.objectivefunc.MacdObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

public class RateChart extends Composite {
	
	
	public static final double PENALTY=0.0025;
	//public static double PENALTY=0.00;
	
	private static Logger logger = Logger.getLogger(RateChart.class);
	
	
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
	
	
	private JFreeChart chart;
	
	//The renderers
	private XYLineAndShapeRenderer mainPlotRenderer=new XYLineAndShapeRenderer(true, false);
	private XYLineAndShapeRenderer secondPlotrenderer=new XYLineAndShapeRenderer(true, false);

	//The Series Collections
	private XYSeriesCollection mainCollection=new XYSeriesCollection();
	private XYSeriesCollection secondCollection=new XYSeriesCollection();
	

	private ExchangeRate rate;
	private IExchangeRateProvider exchangeRateProvider;

	private Composite compositeChart;
	
	private Label labelMaxProfitPercent;
	private Label labelKeepAndOldPercent;
	
	private Button periodBtnUpTo;
	private Slider periodSliderFrom;
	private Label periodLblFrom;
	private Slider periodSliderUpTo;
	private Label periodlblUpTo;
	
	private int[] period=new int[2];
	private float maxProfit=0;
	private float keepAndOld=0;
	
	
	
	//Moving Average
	RateChartMovingAverageComposite movingAverageComposite;
	
	
	//EMA
	private Button emaBtn;
	private Label emaLblAlpha;
	private Slider emaSlider;
	private XYSeries emaSeries;
	
	//MACD
	private Button macdBtnCheck;
	private Slider macdSliderEmaFast;
	private Slider macdSliderEmaSlow;
	private Label macdLblEmaSlowAlpha;
	private Label macdLblEmaFastAlpha;
	
	private MacdObjFunc macdObjFunc;
	private double macdSlowAlpha=0;
	private double macdFastAlpha=0;
	private double macdSignalAlpha=0;
	
	private Optimizer<double[]> macdOptimizer=new Optimizer<double[]>();
	
	private Slider macdSliderSignalAlpha;
	private Label macdLblSignalAlpha;
	private Button macdbtnOpt;
	private Label lblProfit;
	private Label macdLblProfit;

	
	
	
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked")
	@Inject
	public RateChart(Composite parent,ExchangeRate r,
			IExchangeRateProvider exchangeRateProvider,IEclipseContext p_context) {
		super(parent, SWT.NONE);
		
		this.rate=r;
		this.exchangeRateProvider=exchangeRateProvider;
		context=p_context;
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		setLayout(gridLayout);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		ExpandBar expandBar = new ExpandBar(sashForm, SWT.NONE);
		
		//==================================================
		//========             PERIOD                =======    
		//==================================================
		
		ExpandItem xpndtmPeriod = new ExpandItem(expandBar, SWT.NONE);
		xpndtmPeriod.setExpanded(true);
		xpndtmPeriod.setText("Period");
		
		Composite compositePeriode = new Composite(expandBar, SWT.NONE);
		xpndtmPeriod.setControl(compositePeriode);
		xpndtmPeriod.setHeight(122);
		compositePeriode.setLayout(new GridLayout(1, false));
		
		Composite compositePeriodDefinition = new Composite(compositePeriode, SWT.NONE);
		compositePeriodDefinition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositePeriodDefinition.setLayout(new GridLayout(3, false));
		
		Label lblFrom = new Label(compositePeriodDefinition, SWT.NONE);
		lblFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFrom.setText("From :");
		/*
		comboLastDays.addModifyListener(new ModifyListener() {
	        	public void modifyText(ModifyEvent e) {
	        		int allpts=rate.getHistoricalData().size();
	        		
	        		if(comboLastDays.getText().equals("all")){
	        			period[0]=0;period[1]=allpts-1;
	        			//numberOfDays=allpts;
	        			resetChartDataSet();
	        			//c_comp.setChart(createChart());
	        		}
	        		else if(!comboLastDays.getText().isEmpty()){
	        			int d=Integer.parseInt(comboLastDays.getText());
	        			if(d>allpts){period[0]=0;period[1]=allpts-1;}
	        			else{
	        				period[0]=allpts-d;period[1]=allpts-1;
	        				//numberOfDays=d;
	        			}
	        			
	        			resetChartDataSet();
	        			
	        		}
	        		
	        		
	        	}
	        });
		*/
		periodSliderFrom = new Slider(compositePeriodDefinition, SWT.NONE);
		periodSliderFrom.setPageIncrement(1);
		periodSliderFrom.setThumb(1);
		if(!rate.getHistoricalData().isEmpty())
			periodSliderFrom.setMaximum(rate.getHistoricalData().size());
		else{
			periodSliderFrom.setMaximum(200);
		}
		periodSliderFrom.setMinimum(2);
		periodSliderFrom.setSelection(100);
		periodSliderFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int upTo=periodSliderUpTo.getSelection();
				if(periodSliderUpTo.getSelection()>periodSliderFrom.getSelection()){
					upTo=0;
				}
				periodSliderUpTo.setMaximum(periodSliderFrom.getSelection()-1);
				periodSliderUpTo.setEnabled(false);
				periodSliderUpTo.setSelection(upTo);
				periodSliderUpTo.setEnabled(periodBtnUpTo.getSelection());
				
				refreshPeriod();
				
			}
		});
		
		periodLblFrom = new Label(compositePeriodDefinition, SWT.NONE);
		periodLblFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		periodLblFrom.setText("100");
		
		periodBtnUpTo = new Button(compositePeriodDefinition, SWT.CHECK);
		periodBtnUpTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				periodSliderUpTo.setEnabled(periodBtnUpTo.getSelection());
				periodSliderUpTo.setSelection(0);
				
				
				refreshPeriod();
				
			}
		});
		periodBtnUpTo.setSize(49, 16);
		periodBtnUpTo.setText("Up to:");
		
		periodSliderUpTo = new Slider(compositePeriodDefinition, SWT.NONE);
		periodSliderUpTo.setThumb(1);
		periodSliderUpTo.setPageIncrement(1);
		periodSliderUpTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(periodSliderUpTo.isEnabled()){
					refreshPeriod();
				}
				
			}
		});
		periodSliderUpTo.setEnabled(false);
		
		periodlblUpTo = new Label(compositePeriodDefinition, SWT.NONE);
		periodlblUpTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		periodlblUpTo.setText("0");
		
		Composite compositeAnalysis = new Composite(compositePeriode, SWT.NONE);
		compositeAnalysis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeAnalysis.setBounds(0, 0, 64, 64);
		compositeAnalysis.setLayout(new GridLayout(3, false));
		
		Label lblMaxProfit = new Label(compositeAnalysis, SWT.NONE);
		lblMaxProfit.setSize(60, 15);
		lblMaxProfit.setText("Max. Profit:");
		
		labelMaxProfitPercent = new Label(compositeAnalysis, SWT.NONE);
		labelMaxProfitPercent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelMaxProfitPercent.setText("0,00%");
		
		Label lblNewLabelSep = new Label(compositeAnalysis, SWT.NONE);
		lblNewLabelSep.setText(" ");
		
		Label lblKeepOld = new Label(compositeAnalysis, SWT.NONE);
		lblKeepOld.setSize(74, 15);
		lblKeepOld.setText("Keep and Old:");
		
		labelKeepAndOldPercent = new Label(compositeAnalysis, SWT.NONE);
		labelKeepAndOldPercent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelKeepAndOldPercent.setText("0,00%");
		new Label(compositeAnalysis, SWT.NONE);
		
		//=============================================
		//======        MOVING AVERAGE           ======    
		//=============================================
		
		ExpandItem xpndtmMovingAvg = new ExpandItem(expandBar, SWT.NONE);
		xpndtmMovingAvg.setExpanded(true);
		xpndtmMovingAvg.setText("Moving Average");
		xpndtmMovingAvg.setHeight(110);
		
		//Create a context instance
		IEclipseContext localContact=context.createChild();
		localContact.set(Composite.class, expandBar);
		//localContact.setParent(context);
		
		
		movingAverageComposite=ContextInjectionFactory.make( RateChartMovingAverageComposite.class,localContact);
		xpndtmMovingAvg.setControl(movingAverageComposite);
		
		movingAverageComposite.setRenderers(mainPlotRenderer, secondPlotrenderer);
		movingAverageComposite.setSeriesCollections(mainCollection, secondCollection);
		movingAverageComposite.setPeriodandMaxProfit(period, maxProfit);
		
		//=============================================
		//==== EMA (Exponential Moving Average)  ======    
		//=============================================
		
		ExpandItem xpndtmEma = new ExpandItem(expandBar, SWT.NONE);
		xpndtmEma.setText("EMA (Exponential Moving Average)");
		
		Composite emaComposite = new Composite(expandBar, SWT.NONE);
		xpndtmEma.setControl(emaComposite);
		emaComposite.setLayout(new GridLayout(4, false));
		
		emaBtn = new Button(emaComposite, SWT.CHECK);
		emaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				emaSlider.setEnabled(emaBtn.getSelection());
				//if(emaBtn.getSelection())
				resetChartDataSet();
				
			}
		});
		emaBtn.setText("EMA");
		
		Label lblAlpha = new Label(emaComposite, SWT.NONE);
		lblAlpha.setText("Alpha:");
		
		emaLblAlpha = new Label(emaComposite, SWT.NONE);
		emaLblAlpha.setText("0.600");
		
		emaSlider = new Slider(emaComposite, SWT.NONE);
		emaSlider.setMaximum(1000);
		emaSlider.setMinimum(1);
		emaSlider.setSelection(600);
		emaSlider.setEnabled(false);
		emaSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) emaSlider.getSelection())/1000);
				emaLblAlpha.setText(alphaStr.replace(",", "."));
				if(emaSlider.isEnabled())
					resetChartDataSet();
				
			}
		});
		emaSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		xpndtmEma.setHeight(50);
		
		
		//==================================================
		//== MACD (Moving Average Convergence/Divergence) ==    
		//==================================================
		
		
		ExpandItem xpndtmMacd = new ExpandItem(expandBar, SWT.NONE);
		xpndtmMacd.setExpanded(true);
		xpndtmMacd.setText("MACD (Moving Average Convergence/Divergence)");
		
		Composite macdComposite = new Composite(expandBar, SWT.NONE);
		xpndtmMacd.setControl(macdComposite);
		xpndtmMacd.setHeight(150);
		macdComposite.setLayout(new GridLayout(3, false));
		
		macdBtnCheck = new Button(macdComposite, SWT.CHECK);
		macdBtnCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				macdSliderEmaFast.setEnabled(macdBtnCheck.getSelection());
				macdSliderEmaSlow.setEnabled(macdBtnCheck.getSelection());
				macdSliderSignalAlpha.setEnabled(macdBtnCheck.getSelection());
				macdbtnOpt.setEnabled(macdBtnCheck.getSelection());
				
				resetChartDataSet();
			}
		});
		macdBtnCheck.setText("MACD");
		new Label(macdComposite, SWT.NONE);
		
		macdbtnOpt = new Button(macdComposite, SWT.NONE);
		macdbtnOpt.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				final IGPM<double[], double[]> gpm = ((IGPM) (IdentityMapping.IDENTITY_MAPPING));

				macdObjFunc = new MacdObjFunc(rate.getHistoricalData()
						.getXYSeries(HistoricalPoint.FIELD_Close), period,
						maxProfit, PENALTY);

				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						macdObjFunc, gpm, 3, 0.0d, 1.0d,rate.getOptResultsMap().get(Type.MACD));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;
				
				//Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(
						rate,macdOptimizer,
						OptimizationResults.OptimizationTypeToString(Type.MACD),
						partService, modelService, application, context);
				
				
				macdOptimizer.initOptimizationInfo(eventBroker,Type.MACD,rate, wizard.getAlgorithm(), wizard.getTerm());
				macdOptimizer.schedule();
				
				macdbtnOpt.setEnabled(false);
				macdBtnCheck.setEnabled(false);
				
			}
		});
		macdbtnOpt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macdbtnOpt.setText("Opt.");
		macdbtnOpt.setEnabled(false);
		
		Label lblEmaFast = new Label(macdComposite, SWT.NONE);
		lblEmaFast.setText("EMA Fast");
		
		macdLblEmaFastAlpha = new Label(macdComposite, SWT.NONE);
		macdLblEmaFastAlpha.setText("0.846");
		
		macdSliderEmaFast = new Slider(macdComposite, SWT.NONE);
		macdSliderEmaFast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderEmaFast.getSelection())/1000);
				macdLblEmaFastAlpha.setText(alphaStr.replace(",", "."));
				macdFastAlpha=0;
				
				if(macdSliderEmaFast.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderEmaFast.setMaximum(1000);
		macdSliderEmaFast.setMinimum(1);
		macdSliderEmaFast.setSelection(846);
		macdSliderEmaFast.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblEmaSlow = new Label(macdComposite, SWT.NONE);
		lblEmaSlow.setText("EMA Slow");
		
		macdLblEmaSlowAlpha = new Label(macdComposite, SWT.NONE);
		macdLblEmaSlowAlpha.setText("0.926");
		
		macdSliderEmaSlow = new Slider(macdComposite, SWT.NONE);
		macdSliderEmaSlow.setThumb(1);
		macdSliderEmaSlow.setPageIncrement(1);
		macdSliderEmaSlow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderEmaSlow.getSelection())/1000);
				macdLblEmaSlowAlpha.setText(alphaStr.replace(",", "."));
				macdFastAlpha=0;
				if(macdSliderEmaSlow.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderEmaSlow.setMaximum(1000);
		macdSliderEmaSlow.setMinimum(1);
		macdSliderEmaSlow.setSelection(926);
		macdSliderEmaSlow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label macdlblSignal = new Label(macdComposite, SWT.NONE);
		macdlblSignal.setText("Signal");
		
		macdLblSignalAlpha = new Label(macdComposite, SWT.NONE);
		macdLblSignalAlpha.setText("0.800");
		
		macdSliderSignalAlpha = new Slider(macdComposite, SWT.NONE);
		macdSliderSignalAlpha.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macdSliderSignalAlpha.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderSignalAlpha.getSelection())/1000);
				macdLblSignalAlpha.setText(alphaStr.replace(",", "."));
				macdFastAlpha=0;
				if(macdSliderSignalAlpha.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderSignalAlpha.setThumb(1);
		macdSliderSignalAlpha.setPageIncrement(1);
		macdSliderSignalAlpha.setMaximum(1000);
		macdSliderSignalAlpha.setSelection(800);
		
		lblProfit = new Label(macdComposite, SWT.NONE);
		lblProfit.setText("Profit");
		new Label(macdComposite, SWT.NONE);
		
		macdLblProfit = new Label(macdComposite, SWT.NONE);
		macdLblProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macdLblProfit.setText("0,0%");
		
		
		chart = createChart();
		compositeChart = new ChartComposite(sashForm, SWT.NONE,chart);
		compositeChart.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {311, 667});
		

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
		if (incoming == null || rate == null || periodSliderFrom == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	
	
	@Inject
	private void historicalDataLoaded(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid) {

		if(!isCompositeAbleToReact(rate_uuid))return;
		
		fireHistoricalData();
		this.layout();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		//logger.info("Massage OPTIMIZATION_RESULTS_LOADED recieved!");
		
		if(!rate.getOptResultsMap().get(Type.MACD).getResults().isEmpty()){
		//	logger.info("Setting MACD Data!!");
			double[] g=rate.getOptResultsMap().get(Type.MACD).getResults().getFirst().getDoubleArray();
			macdObjFunc = new MacdObjFunc(rate.getHistoricalData()
					.getXYSeries(HistoricalPoint.FIELD_Close), period,
					maxProfit, PENALTY);
			double v=macdObjFunc.compute(g, null);
			resetMACDGuiData(g,v);
		}
		
		
		
	}
	

	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerFinished(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_FINISHED) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.MACD){
			this.macdbtnOpt.setEnabled(true);
			this.macdBtnCheck.setEnabled(true);
		}
		
	}
	
	private void resetMACDGuiData(double[] g, double v) {

		float macdProfit = (maxProfit - (float) v) * 100;
		// Fast Alpha
		macdSliderEmaFast.setSelection((int) (g[0] * 1000));
		macdSliderEmaSlow.setSelection((int) (g[1] * 1000));
		macdSliderSignalAlpha.setSelection((int) (g[2] * 1000));

		String alphaFast = String.format("%.3f", ((float) g[0]));
		String alphaSlow = String.format("%.3f", ((float) g[1]));
		String alphaSignal = String.format("%.3f", ((float) g[2]));

		macdLblEmaFastAlpha.setText(alphaFast.replace(",", "."));
		macdLblEmaSlowAlpha.setText(alphaSlow.replace(",", "."));
		macdLblSignalAlpha.setText(alphaSignal.replace(",", "."));

		macdFastAlpha = g[0];
		macdSlowAlpha = g[1];
		macdSignalAlpha = g[2];

		String macdProfitString = String.format("%,.2f%%", macdProfit);
		macdLblProfit.setText(macdProfitString);

		resetChartDataSet();
	}
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.MACD){
			Individual<double[], double[]> individual=info.getBest();
			resetMACDGuiData(individual.g,individual.v);
		}
		
		
	}
	
	/*
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerNewStep(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_STEP) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==OptimizationType.MACD){
			logger.info("New MACD Optimizer Step: "+info.getStep());
		}
		
	}
	*/
	
	/////////////////////////////
	////     REFESHING       ////
	/////////////////////////////
	
	
	private void fireHistoricalData(){
		if(!rate.getHistoricalData().isEmpty()){
			//historicalDataProvider.load(rate);
			periodSliderFrom.setMaximum(rate.getHistoricalData().size());
			refreshPeriod();
			
			
		}
	}
	
	private void refreshPeriod(){
		int allpts=rate.getHistoricalData().getNoneEmptyPoints().size();
		
		if(periodSliderFrom.getMaximum()!=allpts)
			periodSliderFrom.setMaximum(allpts);
		if(periodSliderUpTo.getMaximum()!=periodSliderFrom.getSelection())
			periodSliderUpTo.setMaximum(periodSliderFrom.getSelection());
		
		
		period[0]=allpts-periodSliderFrom.getSelection();period[1]=allpts-periodSliderUpTo.getSelection();
		
		periodLblFrom.setText(String.valueOf(periodSliderFrom.getSelection()));
		periodlblUpTo.setText(String.valueOf(periodSliderUpTo.getSelection()));
		
		// ===================================
		// Calculate Keep Old and Max Profit
		// ===================================
		keepAndOld = rate.getHistoricalData().calculateKeepAndOld(period,
				HistoricalPoint.FIELD_Close);
		maxProfit = rate.getHistoricalData().calculateMaxProfit(period,
				HistoricalPoint.FIELD_Close);

		String keepAndOldString = String.format("%,.2f%%", keepAndOld * 100);
		String maxProfitString = String.format("%,.2f%%", maxProfit * 100);

		labelMaxProfitPercent.setText(maxProfitString);
		labelKeepAndOldPercent.setText(keepAndOldString);
		
		movingAverageComposite.setPeriodandMaxProfit(period, maxProfit);
		
		resetChartDataSet();
		
		
	}
	
	private void resetChartDataSet(){
		
		//Rest the title to the new period
		LinkedList<HistoricalPoint> periodPoints=HistoricalData.getPointsFromPeriod(period, rate.getHistoricalData().getNoneEmptyPoints());
		if(!periodPoints.isEmpty()){
		Calendar start=periodPoints.getFirst().getDate();
		Calendar end=periodPoints.getLast().getDate();
		chart.setTitle(this.rate.getFullName()+" ["+DateTool.dateToDayString(start)+", "+DateTool.dateToDayString(end)+"]");
		}
		else{
			chart.setTitle(this.rate.getFullName());
		}
		
		CombinedDomainXYPlot combinedPlot=(CombinedDomainXYPlot) chart.getPlot();
		
		createDataset(HistoricalPoint.FIELD_Close);
		XYPlot plot1=(XYPlot)combinedPlot.getSubplots().get(0);
		//plot1.setDataset(0,mainCollection);
		if (rate instanceof Indice || rate instanceof Stock) {
			plot1.setDataset(1, createDataset(HistoricalPoint.FIELD_Volume));
		}
		
	
		
		//secondCollection.removeAllSeries();
		
		//===================================
		//Add the MACD series
		//===================================
		if(macdBtnCheck.getSelection()){
			
			if(secondCollection==null){
				secondCollection = new XYSeriesCollection(macdObjFunc.getProfitSeries());
			}
			else{
				secondCollection.addSeries(macdObjFunc.getProfitSeries());
			}
			
			String macdProfitString = String.format("%,.2f%%", macdObjFunc.getProfit()*100);
			macdLblProfit.setText(macdProfitString);
		}
		
		
		
		//XYPlot secondPlot=(XYPlot)combinedPlot.getSubplots().get(1);
		//secondPlot.setDataset(0, secondCollection);
		
		
		
	}
	
	private XYDataset createDataset(String field) {
		
		if(field.equals(HistoricalPoint.FIELD_Volume)){
			XYSeries series = rate.getHistoricalData().getXYSeries(field, period);
			return new XYSeriesCollection(series);
		}
		
		
		//Clear
		int fiel_pos=mainCollection.indexOf(field);
		if(fiel_pos>=0)mainCollection.removeSeries(fiel_pos);
		
		XYSeries series = rate.getHistoricalData().getXYSeries(field, period);
		mainCollection.addSeries(series);
		
		
		if(emaBtn.getSelection()){
			emaSeries=rate.getHistoricalData().getEMA(field, Float.parseFloat(emaLblAlpha.getText()),"EMA");
			mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(emaSeries,period));
			
			int pos=mainCollection.indexOf("EMA");
			if(pos>0){
				mainPlotRenderer.setSeriesShapesVisible(pos, false);
				mainPlotRenderer.setSeriesLinesVisible(pos, true);
				mainPlotRenderer.setSeriesPaint(pos, Color.GREEN);
			}
			
		}
		
		//===================================
		//Compute the MACD series
		//===================================
		
		if(macdBtnCheck.getSelection()){
			
			double[] x=new double[3];
			
			if(macdFastAlpha>0){
				x[0]=macdFastAlpha;
				x[1]=macdSlowAlpha;
				x[2]=macdSignalAlpha;
			}
			else{
				x[0]=Double.parseDouble(macdLblEmaFastAlpha.getText());
				x[1]=Double.parseDouble(macdLblEmaSlowAlpha.getText());
				x[2]=Double.parseDouble(macdLblSignalAlpha.getText());
				
			}
			
			macdObjFunc=new MacdObjFunc(rate.getHistoricalData().getXYSeries(field), period, maxProfit, PENALTY);
			macdObjFunc.compute(x, null);
				
			mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdEmaFastSeries(),period));
			mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdEmaSlowSeries(),period));
			int ema_fast_pos=mainCollection.indexOf(MacdObjFunc.Macd_EMA_Fast);
			if(ema_fast_pos>0){
				mainPlotRenderer.setSeriesShapesVisible(ema_fast_pos, false);
				mainPlotRenderer.setSeriesLinesVisible(ema_fast_pos, true);
				mainPlotRenderer.setSeriesPaint(ema_fast_pos, Color.CYAN);
			}
			int ema_slow_pos=mainCollection.indexOf(MacdObjFunc.Macd_EMA_Slow);
			if(ema_slow_pos>0){
				mainPlotRenderer.setSeriesShapesVisible(ema_slow_pos, false);
				mainPlotRenderer.setSeriesLinesVisible(ema_slow_pos, true);
				mainPlotRenderer.setSeriesPaint(ema_slow_pos, Color.ORANGE);
			}
			
			mainCollection.addSeries(macdObjFunc.getBuySignalSeries());
			mainCollection.addSeries(macdObjFunc.getSellSignalSeries());

			int buy_pos = mainCollection.indexOf(MacdObjFunc.Macd_Buy_Signal);
			if (buy_pos > 0) {
				// logger.info("Signal found!!");
				mainPlotRenderer.setSeriesShapesVisible(buy_pos, true);
				mainPlotRenderer.setSeriesLinesVisible(buy_pos, false);
				mainPlotRenderer.setSeriesShape(buy_pos,
						ShapeUtilities.createUpTriangle(5));
				mainPlotRenderer.setSeriesShapesFilled(buy_pos, true);
				mainPlotRenderer.setSeriesPaint(buy_pos, Color.GREEN);
				mainPlotRenderer.setSeriesOutlinePaint(buy_pos, Color.BLACK);
				mainPlotRenderer.setSeriesOutlineStroke(buy_pos,
						new BasicStroke(1.0f));
				mainPlotRenderer.setUseOutlinePaint(true);

			}

			int sell_pos = mainCollection.indexOf(MacdObjFunc.Macd_Sell_Signal);
			if (sell_pos > 0) {
				mainPlotRenderer.setSeriesShapesVisible(sell_pos, true);
				mainPlotRenderer.setSeriesLinesVisible(sell_pos, false);
				mainPlotRenderer.setSeriesShape(sell_pos,
						ShapeUtilities.createDownTriangle(5));
				mainPlotRenderer.setSeriesShapesFilled(sell_pos, true);
				mainPlotRenderer.setSeriesPaint(sell_pos, Color.RED);
				mainPlotRenderer.setSeriesOutlinePaint(sell_pos, Color.BLACK);
				mainPlotRenderer.setSeriesOutlineStroke(sell_pos,
						new BasicStroke(1.0f));
			}
			
		}

		return mainCollection;
	}
	
	
	
	
	
	
	 /**
	     * Creates a chart.
	     *
	     * @return a chart.
	     */
	    private JFreeChart createChart() {
	    	
	    	//====================
	    	//===  Main Axis   ===
	    	//====================
	    	NumberAxis domainAxis =createDomainAxis();
	    	
	    	//====================
	    	//===  Main Plot   ===
	    	//====================
	        XYPlot plot1 = createMainPlot(domainAxis);
	       // plot1.a
	        
	        //====================
	    	//===  Second Plot   ===
	    	//====================
	        XYPlot plot2 = createSecondPlot(domainAxis);
	       
	        //======================
	    	//=== Combined Plot  ===
	    	//======================
	        CombinedDomainXYPlot cplot = createCombinedDomainXYPlot(domainAxis,plot1,plot2);
	        
	        //=========================
	    	//=== Create the Chart  ===
	    	//=========================
	        chart = new JFreeChart(rate.getFullName(),
	                JFreeChart.DEFAULT_TITLE_FONT, cplot, false);
	        chart.setBackgroundPaint(Color.white);
	        
	      
	        return chart;

	    }
	    
	    /**
	     * create the Combined Plot
	     * 
	     * @param domainAxis
	     * @param plot1
	     * @param plot2
	     * @return
	     */
	    private CombinedDomainXYPlot createCombinedDomainXYPlot(NumberAxis domainAxis,XYPlot plot1,XYPlot plot2){
	    	 CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(domainAxis);
		        cplot.add(plot1, 5);
		        cplot.add(plot2, 2);
		        cplot.setGap(8.0);
		        cplot.setDomainGridlinePaint(Color.white);
		        cplot.setDomainGridlinesVisible(true);
		        cplot.setDomainPannable(true);
		       
		        return cplot;
	    }
	    
	    
	    private NumberAxis createDomainAxis(){
	    	 //Axis
	        NumberAxis domainAxis = new NumberAxis("Day");
	        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        domainAxis.setAutoRange(true);
	        domainAxis.setLowerMargin(0.01);
	        domainAxis.setUpperMargin(0.01);
	        return domainAxis;
	    }
	    
	    private XYPlot createSecondPlot( NumberAxis domainAxis){
	    	//Creation of data Set
	    	//XYDataset priceData = new XYDataset
	    	
	    	//secondPlotrenderer = new XYLineAndShapeRenderer(true, false);
	        secondPlotrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
	        
	        if (secondPlotrenderer instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) secondPlotrenderer;
	            renderer.setBaseStroke(new BasicStroke(2.0f));
	            renderer.setAutoPopulateSeriesStroke(false);
	            renderer.setSeriesPaint(0, Color.BLUE);
	            renderer.setSeriesPaint(1, Color.DARK_GRAY);
	            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
	        }
	        
	        NumberAxis rangeAxis1 = new NumberAxis("Profit");
	        rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
	        DecimalFormat format = new DecimalFormat("00.00");
	        rangeAxis1.setNumberFormatOverride(format);
	        rangeAxis1.setAutoRangeIncludesZero(false);
	        
	        //Plot
	        XYPlot plot1 = new XYPlot(secondCollection, null, rangeAxis1, secondPlotrenderer);
	        plot1.setBackgroundPaint(Color.lightGray);
	        plot1.setDomainGridlinePaint(Color.white);
	        plot1.setRangeGridlinePaint(Color.white);
	        
	        return plot1;
	    	
	    }
	    
	    
	    
	    /**
	     * Create the Main Plot
	     * 
	     * @return
	     */
	    private XYPlot createMainPlot( NumberAxis domainAxis){
	    	
	    	//====================
	    	//=== Main Curves  ===
	    	//====================
	    	//Creation of data Set
	       // XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close);
	        
	       // priceData.g
	        
	        //Renderer
	       // mainPlotRenderer = new XYLineAndShapeRenderer(true, false);
	        mainPlotRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
	                
	        if (mainPlotRenderer instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) mainPlotRenderer;
	            renderer.setBaseStroke(new BasicStroke(2.0f));
	            renderer.setAutoPopulateSeriesStroke(false);
	            renderer.setSeriesPaint(0, Color.BLUE);
	            renderer.setSeriesPaint(1, Color.DARK_GRAY);
	            
	        }
	        
	        
	        NumberAxis rangeAxis1 = new NumberAxis("Price");
	        rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
	        DecimalFormat format = new DecimalFormat("00.00");
	        rangeAxis1.setNumberFormatOverride(format);
	        rangeAxis1.setAutoRangeIncludesZero(false);
	        
	        //Plot
	        XYPlot plot1 = new XYPlot(mainCollection, null, rangeAxis1, mainPlotRenderer);
	        plot1.setBackgroundPaint(Color.lightGray);
	        plot1.setDomainGridlinePaint(Color.white);
	        plot1.setRangeGridlinePaint(Color.white);
	        
	        //If Stock or indice add the volume
			if (rate instanceof Indice || rate instanceof Stock) {
				addVolumeBars(plot1);
			}
	        
	        return plot1;
	    	
	    }
	    
	    /**
	     * create the Volume Bar
	     * 
	     * @param plot
	     */
	    private void addVolumeBars(XYPlot plot){
	    	
	    	NumberAxis rangeAxis2 = new NumberAxis("Volume");
			rangeAxis2.setUpperMargin(1.00); // to leave room for price line
			plot.setRangeAxis(1, rangeAxis2);
			plot.setDataset(1,
					createDataset(HistoricalPoint.FIELD_Volume));
			plot.mapDatasetToRangeAxis(1, 1);
			XYBarRenderer renderer2 = new XYBarRenderer(0.20);
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
					StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
					 new DecimalFormat("0.0"), new DecimalFormat(
							"0,000.00")));
			plot.setRenderer(1, renderer2);
			
			renderer2.setBarPainter(new StandardXYBarPainter());
			renderer2.setShadowVisible(false);
			renderer2.setBarAlignmentFactor(-0.5);
	    	
	    }
	    
}
