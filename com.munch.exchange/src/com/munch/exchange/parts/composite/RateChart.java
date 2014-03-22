package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Random;

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
import org.goataa.impl.algorithms.hc.HillClimbing;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.searchOperations.strings.real.unary.DoubleArrayAllNormalMutation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.BufferedStatistics;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.ITerminationCriterion;
import org.goataa.spec.IUnarySearchOperation;
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
import com.munch.exchange.job.objectivefunc.MovingAverageObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.IExchangeRateProvider;

public class RateChart extends Composite {
	
	
	public static double PENALTY=0.0025;
	//public static double PENALTY=0.00;
	
	private static Logger logger = Logger.getLogger(RateChart.class);
	private Combo comboLastDays;
	
	private JFreeChart chart;
	private ExchangeRate rate;
	private IExchangeRateProvider exchangeRateProvider;

	private Composite compositeChart;
	private Button movAvgBtnCheck;
	private Combo movAvgCombo;
	private Label labelMaxProfitPercent;
	private Label labelKeepAndOldPercent;
	private Text movAvgTextByLimit;
	private Text movAvgTextSellLimit;
	private Label movAvgLabelProfit;
	
	private int numberOfDays=100;
	private float maxProfit=0;
	private float keepAndOld=0;
	private Slider movAvgSliderBuyLimit;
	private Button movAvgBtnOpt;
	private Label movAvgLblBuyLimit;
	private Label movAvgLblSellLimit;
	private Slider movAvgSliderSellLimit;
	private Label movAvgLblProfit;
	
	private float movAvgSliderBuyFac=0;
	private float movAvgSliderSellFac=0;

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
		
		ExpandItem xpndtmPeriod = new ExpandItem(expandBar, SWT.NONE);
		xpndtmPeriod.setExpanded(true);
		xpndtmPeriod.setText("Period");
		
		Composite compositePeriode = new Composite(expandBar, SWT.NONE);
		xpndtmPeriod.setControl(compositePeriode);
		xpndtmPeriod.setHeight(122);
		compositePeriode.setLayout(new GridLayout(1, false));
		
		Composite compositePeriodDefinition = new Composite(compositePeriode, SWT.NONE);
		compositePeriodDefinition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositePeriodDefinition.setLayout(new GridLayout(4, false));
		
		Button btnCheckButtonLastDays = new Button(compositePeriodDefinition, SWT.CHECK);
		btnCheckButtonLastDays.setSize(72, 16);
		btnCheckButtonLastDays.setSelection(true);
		btnCheckButtonLastDays.setText("Last days:");
		
		comboLastDays = new Combo(compositePeriodDefinition, SWT.NONE);
		comboLastDays.setSize(54, 23);
		comboLastDays.setText("100");
		comboLastDays.add("all");
		comboLastDays.add("1000");
		comboLastDays.add("500");
		comboLastDays.add("300");
		comboLastDays.add("200");
		comboLastDays.add("100");
		comboLastDays.add("50");
		comboLastDays.add("30");
		comboLastDays.add("10");
		comboLastDays.add("5");
		comboLastDays.addModifyListener(new ModifyListener() {
	        	public void modifyText(ModifyEvent e) {
	        		int allpts=rate.getHistoricalData().size();
	        		
	        		if(comboLastDays.getText().equals("all")){
	        			numberOfDays=allpts;
	        			resetChartDataSet();
	        			//c_comp.setChart(createChart());
	        		}
	        		else if(!comboLastDays.getText().isEmpty()){
	        			int d=Integer.parseInt(comboLastDays.getText());
	        			if(d>allpts)numberOfDays=allpts;
	        			else numberOfDays=d;
	        			
	        			resetChartDataSet();
	        			
	        		}
	        		
	        		
	        	}
	        });
		new Label(compositePeriodDefinition, SWT.NONE);
		new Label(compositePeriodDefinition, SWT.NONE);
		
		Button btnPeriodeFrom = new Button(compositePeriodDefinition, SWT.CHECK);
		btnPeriodeFrom.setSize(49, 16);
		btnPeriodeFrom.setText("From");
		
		Combo comboPriodFrom = new Combo(compositePeriodDefinition, SWT.NONE);
		comboPriodFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboPriodFrom.setSize(54, 23);
		comboPriodFrom.setEnabled(false);
		comboPriodFrom.setText("0");
		
		Label lblTo = new Label(compositePeriodDefinition, SWT.NONE);
		GridData gd_lblTo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblTo.widthHint = 19;
		lblTo.setLayoutData(gd_lblTo);
		lblTo.setSize(11, 15);
		lblTo.setText("to");
		
		Combo comboPeriodTo = new Combo(compositePeriodDefinition, SWT.NONE);
		comboPeriodTo.setSize(45, 23);
		comboPeriodTo.setEnabled(false);
		comboPeriodTo.setText("100");
		
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
		
		
		ExpandItem xpndtmMovingAvg = new ExpandItem(expandBar, SWT.NONE);
		xpndtmMovingAvg.setExpanded(true);
		xpndtmMovingAvg.setText("Moving Average");
		
		Composite movAvgComposite = new Composite(expandBar, SWT.NONE);
		xpndtmMovingAvg.setControl(movAvgComposite);
		xpndtmMovingAvg.setHeight(150);
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
				if(movAvgBtnCheck.getSelection())
					resetChartDataSet();
			}
		});
		movAvgBtnCheck.setText("Average 1:");
		
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
				int maxRuns = 10;
				int maxSteps = 50000;
				int i;
				
				double[] x;
				
				
				float max=100 / Math.max(movAvgSliderBuyFac, movAvgSliderSellFac);
				
				 //NullarySearchOperation
				 INullarySearchOperation<double[]> create=new DoubleArrayUniformCreation(2, 0.0d, max);
				 
				 final IUnarySearchOperation<double[]> normal = new DoubleArrayAllNormalMutation(0,max);
				 
				 final IGPM<double[], double[]> gpm = ((IGPM) (IdentityMapping.IDENTITY_MAPPING));
				 
				 final ITerminationCriterion term = new StepLimit(maxSteps);
				 
				 final BufferedStatistics stat= new BufferedStatistics();
				 
				 //Objectiv Function
				 XYSeries profitSeries = new XYSeries("Profit Series");
				 
				 MovingAverageObjFunc f=new MovingAverageObjFunc(
						 HistoricalPoint.FIELD_Close,
						 PENALTY,
						 profitSeries,
						 rate.getHistoricalData().getNoneEmptyPoints(),
						 maxProfit,
						 0,
						 numberOfDays,
						 Integer.parseInt(movAvgCombo.getText())
						 );
				 
				 // Hill Climbing (Algorithm 26.1)
				 stat.clear();
				 
				// Hill Climbing (Algorithm 26.1)
				    stat.clear();
				    for (i = 0; i < maxRuns; i++) {
				      term.reset();
				      x = HillClimbing.hillClimbing(f, create, normal, gpm, term,
				          new Random()).x;
				      System.out.println("Result x: ["+x[0]+", "+x[1]+"]");
				      stat.add(f.compute(x, null));
				    }
				    System.out.println("HC      + uniform: best =" + stat.min() + //$NON-NLS-1$
				        "\n                   med  =" + stat.median() + //$NON-NLS-1$
				        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
				        "\n                   worst=" + stat.max());//$NON-NLS-1$
				    
				    
				    float movAvgProfit=(maxProfit- (float)stat.min())*100;
					
				    System.out.println("movAvgProfit: "+movAvgProfit);
				
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
		
		movAvgLabelProfit = new Label(movAvgComposite, SWT.NONE);
		movAvgLabelProfit.setText("00,00%");
		new Label(movAvgComposite, SWT.NONE);
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
		if (incoming == null || rate == null || comboLastDays == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;

		
		fireHistoricalData();
		this.layout();
	}
	
	private void fireHistoricalData(){
		if(!rate.getHistoricalData().isEmpty()){
			//historicalDataProvider.load(rate);
			resetChartDataSet();
			
		}
	}
	
	private void resetChartDataSet(){
		
		CombinedDomainXYPlot combinedPlot=(CombinedDomainXYPlot) chart.getPlot();
		
		XYPlot plot1=(XYPlot)combinedPlot.getSubplots().get(0);
		plot1.setDataset(0, createDataset(HistoricalPoint.FIELD_Close,numberOfDays));
		if (rate instanceof Indice || rate instanceof Stock) {
			plot1.setDataset(1, createDataset(HistoricalPoint.FIELD_Volume, numberOfDays));
		}
		
		//Calculate Keep Old and Max Profit
		keepAndOld=rate.getHistoricalData().calculateKeepAndOld(0, numberOfDays, HistoricalPoint.FIELD_Close);
		maxProfit=rate.getHistoricalData().calculateMaxProfit(0, numberOfDays, HistoricalPoint.FIELD_Close);
		
		//logger.info("KeepAndOld: "+keepAndOld);
		//logger.info("maxProfit: "+maxProfit);
		
		String keepAndOldString = String.format("%,.2f%%", keepAndOld*100);
		String maxProfitString = String.format("%,.2f%%", maxProfit*100);
		
		labelMaxProfitPercent.setText(maxProfitString);
		labelKeepAndOldPercent.setText(keepAndOldString);
		
		
		//Calculate Moving Average Profit
		if(movAvgBtnCheck.getSelection()){
		try{
			 XYSeries profitSeries = new XYSeries("Profit Series");
			 XYSeriesCollection collection = new XYSeriesCollection(profitSeries);
			 
			 MovingAverageObjFunc func=new MovingAverageObjFunc(
					 HistoricalPoint.FIELD_Close,
					 PENALTY,
					 profitSeries,
					 rate.getHistoricalData().getNoneEmptyPoints(),
					 maxProfit,
					 0,
					 numberOfDays,
					 Integer.parseInt(movAvgCombo.getText())
					 );
			
			double[] x=new double[2];
			//x[0]=Double.parseDouble(comboMovingAvg1.getText());
			x[0]=Double.parseDouble(movAvgTextByLimit.getText().replace(",", ".").replace("%", ""));
			x[1]=Double.parseDouble(movAvgTextSellLimit.getText().replace(",", ".").replace("%", ""));
			
			double delta=func.compute(x, null);
			float movAvgProfit=maxProfit- (float)delta;
			
			logger.info("movAvgProfit: "+movAvgProfit);
			
			String movAvgProfitString = String.format("%,.2f%%", movAvgProfit*100);
			movAvgLabelProfit.setText(movAvgProfitString);
			
			//Set the slider Max Value
			
			movAvgSliderBuyFac=100/func.getDiff2Max();
			movAvgSliderSellFac=-100/func.getDiff2Min();
			
			
			XYPlot plot2=(XYPlot)combinedPlot.getSubplots().get(1);
			plot2.setDataset(0, collection);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		}
		
		
	}
	
	private XYDataset createDataset(String field, int days) {

		XYSeries series = rate.getHistoricalData().getXYSeries(field, days);
		XYSeriesCollection collection = new XYSeriesCollection(series);
		
		if(field.equals(HistoricalPoint.FIELD_Volume))return collection;

		if (movAvgBtnCheck.getSelection()) {
			collection.addSeries(rate.getHistoricalData().getMovingAvg(field,
					days, Integer.parseInt(movAvgCombo.getText())));
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
	        XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close,numberOfDays);
	        
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
					createDataset(HistoricalPoint.FIELD_Volume, numberOfDays));
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
