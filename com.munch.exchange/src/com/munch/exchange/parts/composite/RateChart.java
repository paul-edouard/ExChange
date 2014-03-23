package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.BufferedStatistics;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.ITerminationCriterion;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.objectivefunc.MacdObjFunc;
import com.munch.exchange.job.objectivefunc.MovingAverageObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.services.IExchangeRateProvider;

public class RateChart extends Composite {
	
	
	public static double PENALTY=0.0025;
	//public static double PENALTY=0.00;
	
	private static Logger logger = Logger.getLogger(RateChart.class);
	
	private JFreeChart chart;
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
	//private int numberOfDays=100;
	private float maxProfit=0;
	private float keepAndOld=0;
	
	
	
	//Moving Average
	private Text movAvgTextByLimit;
	private Text movAvgTextSellLimit;
	private Label movAvgLabelProfit;
	private Button movAvgBtnCheck;
	private Combo movAvgCombo;
	private Slider movAvgSliderBuyLimit;
	private Button movAvgBtnOpt;
	private Label movAvgLblBuyLimit;
	private Label movAvgLblSellLimit;
	private Slider movAvgSliderSellLimit;
	private Label movAvgLblProfit;
	
	private float movAvgSliderBuyFac=0;
	private float movAvgSliderSellFac=0;
	
	//EMA
	private Button emaBtn;
	private Label emaLblAlpha;
	private Slider emaSlider;
	private XYSeries emaSeries;
	
	//MACD
	private Button macdBtn;
	private Slider macdSliderEmaFast;
	private Slider macdSliderEmaSlow;
	private Label macdLblEmaSlowAlpha;
	private Label macdLblEmaFastAlpha;
	
	private MacdObjFunc macdObjFunc;
	
	private Slider macSliderSignalAlpha;
	private Label macdLblSignalAlpha;
	private Button btnOpt;
	private Label lblProfit;
	private Label macdLblProfit;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public RateChart(Composite parent,ExchangeRate r,
			IExchangeRateProvider exchangeRateProvider) {
		super(parent, SWT.NONE);
		
		this.rate=r;
		this.exchangeRateProvider=exchangeRateProvider;
		
		
		
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
		xpndtmMovingAvg.setText("Moving Average");
		
		Composite movAvgComposite = new Composite(expandBar, SWT.NONE);
		xpndtmMovingAvg.setControl(movAvgComposite);
		xpndtmMovingAvg.setHeight(110);
		movAvgComposite.setLayout(new GridLayout(3, false));
		
		movAvgBtnCheck = new Button(movAvgComposite, SWT.CHECK);
		movAvgBtnCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgBtnCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				movAvgCombo.setEnabled(movAvgBtnCheck.getSelection());
				movAvgBtnOpt.setEnabled(movAvgBtnCheck.getSelection());
				movAvgSliderBuyLimit.setEnabled(movAvgBtnCheck.getSelection());
				movAvgSliderSellLimit.setEnabled(movAvgBtnCheck.getSelection());
				//if(movAvgBtnCheck.getSelection())
				resetChartDataSet();
			}
		});
		movAvgBtnCheck.setText("Average:");
		
		movAvgCombo = new Combo(movAvgComposite, SWT.NONE);
		movAvgCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgCombo.setEnabled(false);
		movAvgCombo.setText("5");
		movAvgCombo.add("2");
		movAvgCombo.add("3");
		movAvgCombo.add("5");
		movAvgCombo.add("10");
		movAvgCombo.add("20");
		movAvgCombo.add("30");
		movAvgCombo.add("50");
		
		movAvgBtnOpt = new Button(movAvgComposite, SWT.NONE);
		movAvgBtnOpt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		movAvgBtnOpt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				resetChartDataSet();
				
				//TODO
				//int maxRuns = 1;
				int maxSteps = 50000;
				int i;
				
				double[] x;
				
				float max=100 / Math.max(movAvgSliderBuyFac, movAvgSliderSellFac);
				
				 //NullarySearchOperation
				 INullarySearchOperation<double[]> create=new DoubleArrayUniformCreation(2, 0.0d, max);
				 
				// final IUnarySearchOperation<double[]> normal = new DoubleArrayAllNormalMutation(0,max);
				 
				 final IGPM<double[], double[]> gpm = ((IGPM) (IdentityMapping.IDENTITY_MAPPING));
				 
				 final ITerminationCriterion term = new StepLimit(maxSteps);
				 
				 final BufferedStatistics stat= new BufferedStatistics();
				 
				
				 
				
				 
				 MovingAverageObjFunc f=new MovingAverageObjFunc(
						 HistoricalPoint.FIELD_Close,
						 PENALTY,
						 rate.getHistoricalData().getNoneEmptyPoints(),
						 maxProfit,
						 period[0],
						 period[1],
						 Integer.parseInt(movAvgCombo.getText())
						 );
				 
				 // Hill Climbing (Algorithm 26.1)
				 stat.clear();
				 
				
				 EvolutionStrategy<double[]> ES= new EvolutionStrategy<double[]>();
				 ES.setObjectiveFunction(f);
				 ES.setNullarySearchOperation(create);
				 ES.setGPM(gpm);
				 ES.setTerminationCriterion(term);
				 //ES.setUnarySearchOperation(normal);
				 
				 ES.setSelectionAlgorithm(new TournamentSelection(3));
				 ES.setDimension(2);
				 ES.setMinimum(0d);
			     ES.setMaximum(max);
			     //Number of parents
			     ES.setMu(1000);
			     //Number of offspring
			     ES.setLambda(100);
			     //Number of parents per offspring
			     ES.setLambda(50);
			     ES.setPlus(true);
				 
				 
				      term.reset();
				      
				      List<Individual<double[], double[]>> solutions;
				      Individual<double[], double[]> individual;
				      
				  
				      solutions = ((List<Individual<double[], double[]>>) (ES.call()));
				 //     System.out.println("Number pf solution:"+solutions.size());
				   
				      individual = solutions.get(0);
		//		      System.out.println("Result x: ["+individual.g[0]+", "+individual.g[1]+"]"+", Profit: "+((maxProfit- (float)individual.v)*100));
				      
				      stat.add(individual.v);
				      
				      
				      
				 /*
				    System.out.println("HC      + uniform: best =" + stat.min() + //$NON-NLS-1$
				        "\n                   med  =" + stat.median() + //$NON-NLS-1$
				        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
				        "\n                   worst=" + stat.max());//$NON-NLS-1$
				    
				    */
				    float movAvgProfit=(maxProfit- (float)stat.min())*100;
					
				    System.out.println("movAvgProfit: "+movAvgProfit);
				    
				    //Buy Limit
				    movAvgSliderBuyLimit.setSelection( (int) (individual.g[0]*movAvgSliderBuyFac) );
				    String buyLimit = String.format("%,.2f%%", ( (float)individual.g[0]));
				    movAvgTextByLimit.setText(buyLimit);
				    
				    //Sell Limit
				    movAvgSliderSellLimit.setSelection( (int) (individual.g[1]*movAvgSliderSellFac) );
				    String sellLimit = String.format("%,.2f%%", ( (float)individual.g[1]));
				    movAvgTextSellLimit.setText(sellLimit);
				    
				    resetChartDataSet();
				
			}
			
			
			
			
			
		});
		movAvgBtnOpt.setText("Opt.");
		
		movAvgLblBuyLimit = new Label(movAvgComposite, SWT.NONE);
		movAvgLblBuyLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgLblBuyLimit.setText("Buy limit:");
		
		movAvgTextByLimit = new Text(movAvgComposite, SWT.BORDER);
		movAvgTextByLimit.setEditable(false);
		movAvgTextByLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgTextByLimit.setText("0.0");
		
		movAvgSliderBuyLimit = new Slider(movAvgComposite, SWT.NONE);
		movAvgSliderBuyLimit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		movAvgSliderBuyLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//logger.info(movAvgSliderBuyLimit.getSelection());
				String buyLimit = String.format("%,.2f%%", ( (float) movAvgSliderBuyLimit.getSelection())/movAvgSliderBuyFac);
				movAvgTextByLimit.setText(buyLimit);
				if(movAvgTextByLimit.isEnabled())
					resetChartDataSet();
				
				
			}
		});
		
		movAvgLblSellLimit = new Label(movAvgComposite, SWT.NONE);
		movAvgLblSellLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgLblSellLimit.setText("Sell limit:");
		
		movAvgTextSellLimit = new Text(movAvgComposite, SWT.BORDER);
		movAvgTextSellLimit.setEditable(false);
		movAvgTextSellLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgTextSellLimit.setText("0.0");
		
		movAvgSliderSellLimit = new Slider(movAvgComposite, SWT.NONE);
		movAvgSliderSellLimit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		movAvgSliderSellLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//logger.info(movAvgSliderSellLimit.getSelection());
				String buyLimit = String.format("%,.2f%%", ( (float) movAvgSliderSellLimit.getSelection())/movAvgSliderSellFac);
				movAvgTextSellLimit.setText(buyLimit);
				if(movAvgSliderSellLimit.isEnabled())
					resetChartDataSet();
			}
		});
		
		movAvgLblProfit = new Label(movAvgComposite, SWT.NONE);
		movAvgLblProfit.setText("Profit:");
		new Label(movAvgComposite, SWT.NONE);
		
		movAvgLabelProfit = new Label(movAvgComposite, SWT.NONE);
		movAvgLabelProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		movAvgLabelProfit.setText("00,00%");
		
		
		
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
		
		macdBtn = new Button(macdComposite, SWT.CHECK);
		macdBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				macdSliderEmaFast.setEnabled(macdBtn.getSelection());
				macdSliderEmaSlow.setEnabled(macdBtn.getSelection());
				macSliderSignalAlpha.setEnabled(macdBtn.getSelection());
				
				resetChartDataSet();
			}
		});
		macdBtn.setText("MACD");
		new Label(macdComposite, SWT.NONE);
		
		btnOpt = new Button(macdComposite, SWT.NONE);
		btnOpt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnOpt.setText("Opt.");
		
		Label lblEmaFast = new Label(macdComposite, SWT.NONE);
		lblEmaFast.setText("EMA Fast");
		
		macdLblEmaFastAlpha = new Label(macdComposite, SWT.NONE);
		macdLblEmaFastAlpha.setText("0.500");
		
		macdSliderEmaFast = new Slider(macdComposite, SWT.NONE);
		macdSliderEmaFast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderEmaFast.getSelection())/1000);
				macdLblEmaFastAlpha.setText(alphaStr.replace(",", "."));
				if(macdSliderEmaFast.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderEmaFast.setMaximum(1000);
		macdSliderEmaFast.setMinimum(1);
		macdSliderEmaFast.setSelection(500);
		macdSliderEmaFast.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblEmaSlow = new Label(macdComposite, SWT.NONE);
		lblEmaSlow.setText("EMA Slow");
		
		macdLblEmaSlowAlpha = new Label(macdComposite, SWT.NONE);
		macdLblEmaSlowAlpha.setText("0.300");
		
		macdSliderEmaSlow = new Slider(macdComposite, SWT.NONE);
		macdSliderEmaSlow.setThumb(1);
		macdSliderEmaSlow.setPageIncrement(1);
		macdSliderEmaSlow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderEmaSlow.getSelection())/1000);
				macdLblEmaSlowAlpha.setText(alphaStr.replace(",", "."));
				if(macdSliderEmaSlow.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderEmaSlow.setMaximum(1000);
		macdSliderEmaSlow.setMinimum(1);
		macdSliderEmaSlow.setSelection(300);
		macdSliderEmaSlow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label macdlblSignal = new Label(macdComposite, SWT.NONE);
		macdlblSignal.setText("Signal");
		
		macdLblSignalAlpha = new Label(macdComposite, SWT.NONE);
		macdLblSignalAlpha.setText("0.150");
		
		macSliderSignalAlpha = new Slider(macdComposite, SWT.NONE);
		macSliderSignalAlpha.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macSliderSignalAlpha.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macSliderSignalAlpha.getSelection())/1000);
				macdLblSignalAlpha.setText(alphaStr.replace(",", "."));
				if(macSliderSignalAlpha.isEnabled())
					resetChartDataSet();
			}
		});
		macSliderSignalAlpha.setThumb(1);
		macSliderSignalAlpha.setPageIncrement(1);
		macSliderSignalAlpha.setMaximum(1000);
		macSliderSignalAlpha.setSelection(150);
		
		lblProfit = new Label(macdComposite, SWT.NONE);
		lblProfit.setText("Profit");
		new Label(macdComposite, SWT.NONE);
		
		macdLblProfit = new Label(macdComposite, SWT.NONE);
		macdLblProfit.setText("0,0%");
		movAvgCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				resetChartDataSet();
			}
		});
		
		chart = createChart();
		compositeChart = new ChartComposite(sashForm, SWT.NONE,chart);
		compositeChart.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {311, 667});
		

	}
	
	
	
	
	
	@Inject
	private void historicalDataLoaded(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid) {

		if (this.isDisposed())
			return;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || periodSliderFrom == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;

		
		fireHistoricalData();
		this.layout();
	}
	
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
		
		resetChartDataSet();
	}
	
	private void resetChartDataSet(){
		
		//Rest the title to the new period
		LinkedList<HistoricalPoint> periodPoints=HistoricalData.getPointsFromPeriod(period, rate.getHistoricalData().getNoneEmptyPoints());
		Calendar start=periodPoints.getFirst().getDate();
		Calendar end=periodPoints.getLast().getDate();
		chart.setTitle(this.rate.getFullName()+" ["+DateTool.dateToDayString(start)+", "+DateTool.dateToDayString(end)+"]");
		
		CombinedDomainXYPlot combinedPlot=(CombinedDomainXYPlot) chart.getPlot();
		
		XYPlot plot1=(XYPlot)combinedPlot.getSubplots().get(0);
		plot1.setDataset(0, createDataset(HistoricalPoint.FIELD_Close));
		if (rate instanceof Indice || rate instanceof Stock) {
			plot1.setDataset(1, createDataset(HistoricalPoint.FIELD_Volume));
		}
		
		//Calculate Keep Old and Max Profit
		keepAndOld=rate.getHistoricalData().calculateKeepAndOld(period, HistoricalPoint.FIELD_Close);
		maxProfit=rate.getHistoricalData().calculateMaxProfit(period, HistoricalPoint.FIELD_Close);
		
		//logger.info("KeepAndOld: "+keepAndOld);
		//logger.info("maxProfit: "+maxProfit);
		
		String keepAndOldString = String.format("%,.2f%%", keepAndOld*100);
		String maxProfitString = String.format("%,.2f%%", maxProfit*100);
		
		labelMaxProfitPercent.setText(maxProfitString);
		labelKeepAndOldPercent.setText(keepAndOldString);
		
		
		//Calculate Moving Average Profit
		XYSeriesCollection collection=null;
		
		if(movAvgBtnCheck.getSelection()){
		try{
			
			 MovingAverageObjFunc func=new MovingAverageObjFunc(
					 HistoricalPoint.FIELD_Close,
					 PENALTY,
					 rate.getHistoricalData().getNoneEmptyPoints(),
					 maxProfit,
					 period[0],
					 period[1],
					 Integer.parseInt(movAvgCombo.getText())
					 );
			
			
			collection = new XYSeriesCollection(func.getProfitSeries());
			collection.addSeries(func.getBySellSeries());
			
			
			double[] x=new double[2];
			//x[0]=Double.parseDouble(comboMovingAvg1.getText());
			x[0]=Double.parseDouble(movAvgTextByLimit.getText().replace(",", ".").replace("%", ""));
			x[1]=Double.parseDouble(movAvgTextSellLimit.getText().replace(",", ".").replace("%", ""));
			
			double delta=func.compute(x, null);
			float movAvgProfit=maxProfit- (float)delta;
			
			//logger.info("movAvgProfit: "+movAvgProfit);
			
			String movAvgProfitString = String.format("%,.2f%%", movAvgProfit*100);
			movAvgLabelProfit.setText(movAvgProfitString);
			
			//Set the slider Max Value
			
			movAvgSliderBuyFac=100/func.getDiff2Max();
			movAvgSliderSellFac=-100/func.getDiff2Min();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		}
		
		
		if(macdBtn.getSelection()){
			if(collection==null){
				collection = new XYSeriesCollection(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdSeries(),period));
			}
			else{
				collection.addSeries(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdSeries(),period));
			}
			
			collection.addSeries(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdSignalSeries(),period));
			collection.addSeries(macdObjFunc.getProfitSeries());
			collection.addSeries(macdObjFunc.getBySellSeries());
			
			String macdProfitString = String.format("%,.2f%%", macdObjFunc.getProfit()*100);
			macdLblProfit.setText(macdProfitString);
		}
		
		
		
		XYPlot plot2=(XYPlot)combinedPlot.getSubplots().get(1);
		plot2.setDataset(0, collection);
		
		
		
	}
	
	private XYDataset createDataset(String field) {

		XYSeries series = rate.getHistoricalData().getXYSeries(field, period);
		XYSeriesCollection collection = new XYSeriesCollection(series);
		
		if(field.equals(HistoricalPoint.FIELD_Volume))return collection;

		if (movAvgBtnCheck.getSelection()) {
			
			XYSeries movAvgSeries=rate.getHistoricalData().getMovingAvg(field,Integer.parseInt(movAvgCombo.getText()),"Moving Average");
			//logger.info("Moving Average Size: "+movAvgSeries.getItemCount());
			
			collection.addSeries(MacdObjFunc.reduceSerieToPeriod(movAvgSeries,period));
		}
		if(emaBtn.getSelection()){
			emaSeries=rate.getHistoricalData().getEMA(field, Float.parseFloat(emaLblAlpha.getText()),"EMA");
			collection.addSeries(MacdObjFunc.reduceSerieToPeriod(emaSeries,period));		
			
		}
		if(macdBtn.getSelection()){
			
			double[] x={Double.parseDouble(macdLblEmaFastAlpha.getText()),
					Double.parseDouble(macdLblEmaSlowAlpha.getText()),
					Double.parseDouble(macdLblSignalAlpha.getText())};
			
			macdObjFunc=new MacdObjFunc(rate.getHistoricalData().getXYSeries(field), period, maxProfit, PENALTY);
			macdObjFunc.compute(x, null);
				
			collection.addSeries(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdEmaFastSeries(),period));
			collection.addSeries(MacdObjFunc.reduceSerieToPeriod(macdObjFunc.getMacdEmaSlowSeries(),period));
			
		}

		return collection;
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
	    	
	    	//Renderer
	        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
	        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
	        
	        if (renderer1 instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) renderer1;
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
	        XYPlot plot1 = new XYPlot(null, null, rangeAxis1, renderer1);
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
	        XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close);
	        
	        //Renderer
	        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
	        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
	                
	        if (renderer1 instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) renderer1;
	            renderer.setBaseStroke(new BasicStroke(2.0f));
	            renderer.setAutoPopulateSeriesStroke(false);
	            renderer.setSeriesPaint(0, Color.BLUE);
	            renderer.setSeriesPaint(1, Color.DARK_GRAY);
	            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
	        }
	        
	        
	        NumberAxis rangeAxis1 = new NumberAxis("Price");
	        rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
	        DecimalFormat format = new DecimalFormat("00.00");
	        rangeAxis1.setNumberFormatOverride(format);
	        rangeAxis1.setAutoRangeIncludesZero(false);
	        
	        //Plot
	        XYPlot plot1 = new XYPlot(priceData, null, rangeAxis1, renderer1);
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
