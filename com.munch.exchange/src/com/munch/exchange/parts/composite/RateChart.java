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

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.services.IExchangeRateProvider;

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
	
	//Low & Hight
	RateChartLawAndHightComposite lawAndHightComposite;
	
	//Moving Average
	RateChartMovingAverageComposite movingAverageComposite;
	
	//EMA
	RateChartEMAComposite emaComposite;
	
	//MACD
	RateChartMACDComposite macdComposite;
	
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
		
		//Create a context instance
		IEclipseContext localContact=context.createChild();
		localContact.set(Composite.class, expandBar);
		
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
		//======         LOW & HIGHT             ======    
		//=============================================		
		ExpandItem xpndtmLowHight = new ExpandItem(expandBar, SWT.NONE);
		xpndtmLowHight.setExpanded(false);
		xpndtmLowHight.setText("Low & Hight");
		xpndtmLowHight.setHeight(30);
			
		lawAndHightComposite=ContextInjectionFactory.make( RateChartLawAndHightComposite.class,localContact);
		xpndtmLowHight.setControl(lawAndHightComposite);
		
		lawAndHightComposite.setRenderers(mainPlotRenderer, secondPlotrenderer);
		lawAndHightComposite.setSeriesCollections(mainCollection, secondCollection);
		lawAndHightComposite.setPeriodandMaxProfit(period, maxProfit);
		lawAndHightComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		//=============================================
		//======        MOVING AVERAGE           ======    
		//=============================================
		
		ExpandItem xpndtmMovingAvg = new ExpandItem(expandBar, SWT.NONE);
		xpndtmMovingAvg.setExpanded(false);
		xpndtmMovingAvg.setText("Moving Average");
		xpndtmMovingAvg.setHeight(110);
			
		movingAverageComposite=ContextInjectionFactory.make( RateChartMovingAverageComposite.class,localContact);
		xpndtmMovingAvg.setControl(movingAverageComposite);
		
		movingAverageComposite.setRenderers(mainPlotRenderer, secondPlotrenderer);
		movingAverageComposite.setSeriesCollections(mainCollection, secondCollection);
		movingAverageComposite.setPeriodandMaxProfit(period, maxProfit);
		movingAverageComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		//=============================================
		//==== EMA (Exponential Moving Average)  ======    
		//=============================================
		
		ExpandItem xpndtmEma = new ExpandItem(expandBar, SWT.NONE);
		xpndtmEma.setText("EMA (Exponential Moving Average)");
		xpndtmEma.setHeight(50);
		
		emaComposite=ContextInjectionFactory.make( RateChartEMAComposite.class,localContact);
		xpndtmEma.setControl(emaComposite);
		
		emaComposite.setRenderers(mainPlotRenderer, secondPlotrenderer);
		emaComposite.setSeriesCollections(mainCollection, secondCollection);
		emaComposite.setPeriodandMaxProfit(period, maxProfit);
		emaComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		//==================================================
		//== MACD (Moving Average Convergence/Divergence) ==    
		//==================================================
		ExpandItem xpndtmMacd = new ExpandItem(expandBar, SWT.NONE);
		xpndtmMacd.setExpanded(false);
		xpndtmMacd.setText("MACD (Moving Average Convergence/Divergence)");
		xpndtmMacd.setHeight(150);
		
		macdComposite=ContextInjectionFactory.make( RateChartMACDComposite.class,localContact);
		xpndtmMacd.setControl(macdComposite);
		
		macdComposite.setRenderers(mainPlotRenderer, secondPlotrenderer);
		macdComposite.setSeriesCollections(mainCollection, secondCollection);
		macdComposite.setPeriodandMaxProfit(period, maxProfit);
		macdComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		//==================================================
		//==                 CHART                        ==    
		//==================================================
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
		
		mainCollection.removeAllSeries();
		secondCollection.removeAllSeries();
		
		mainPlotRenderer.clearSeriesPaints(false);
		//mainPlotRenderer.clearSeriesStrokes(false);
		
		secondPlotrenderer.clearSeriesPaints(false);
		//secondPlotrenderer.clearSeriesStrokes(false);
		
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
		
		// ===================================
		// Distribute new Period and max profit
		// ===================================
		movingAverageComposite.setPeriodandMaxProfit(period, maxProfit);
		macdComposite.setPeriodandMaxProfit(period, maxProfit);
		emaComposite.setPeriodandMaxProfit(period, maxProfit);
		lawAndHightComposite.setPeriodandMaxProfit(period, maxProfit);
		
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
		fiel_pos=mainCollection.indexOf(field);
		if(fiel_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(fiel_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(fiel_pos, true);
			mainPlotRenderer.setSeriesStroke(fiel_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(fiel_pos, Color.BLUE);
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
	    	
	        mainPlotRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
	        mainPlotRenderer.setBaseStroke(new BasicStroke(2.0f));
	        
	        /*
	        if (mainPlotRenderer instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) mainPlotRenderer;
	            renderer.setBaseStroke(new BasicStroke(2.0f));
	            renderer.setAutoPopulateSeriesStroke(false);
	            renderer.setSeriesPaint(0, Color.BLUE);
	            renderer.setSeriesPaint(1, Color.DARK_GRAY);
	            
	        }
	        */
	        
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
