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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeriesCollection;
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
	private XYLineAndShapeRenderer percentPlotrenderer=new XYLineAndShapeRenderer(true, false);
	private XYErrorRenderer errorPlotRenderer=new XYErrorRenderer();
	private DeviationRenderer deviationPercentPlotRenderer=new DeviationRenderer();
	private DeviationRenderer deviationRenderer = new DeviationRenderer(true, false);
	//TODO
	
	//The Series Collections
	private XYSeriesCollection mainCollection=new XYSeriesCollection();
	private XYSeriesCollection secondCollection=new XYSeriesCollection();
	private XYSeriesCollection percentCollection=new XYSeriesCollection();
	private YIntervalSeriesCollection errorCollection=new YIntervalSeriesCollection();
	private YIntervalSeriesCollection deviationPercentCollection=new YIntervalSeriesCollection();
	private YIntervalSeriesCollection deviationCollection=new YIntervalSeriesCollection();
	
	

	private ExchangeRate rate;
	private IExchangeRateProvider exchangeRateProvider;

	private Composite compositeChart;
	
	/*
	private Label labelMaxProfitPercent;
	private Label labelKeepAndOldPercent;
	
	private Button periodBtnUpTo;
	private Slider periodSliderFrom;
	private Label periodLblFrom;
	private Slider periodSliderUpTo;
	private Label periodlblUpTo;
	*/
	
	private int[] period=new int[2];
	private float maxProfit=0;
	private float keepAndOld=0;
	
	
	//Period Composite
	RateChartPeriodComposite periodComposite;
	
	//Low & Hight
	RateChartLawAndHightComposite lawAndHightComposite;
	
	//Moving Average
	RateChartMovingAverageComposite movingAverageComposite;
	
	//NMAW
	NMAWComposite NMAWComposite;
	
	//EMA
	RateChartEMAComposite emaComposite;
	
	//MACD
	RateChartMACDComposite macdComposite;
	
	//Bollinger Bands
	RateChartBollingerBandsComposite bollingerBandsComposite;
	
	//Parabolic SAR
	RateChartParabolicSAR parabolicSARComposite;
	
	//Relative strength index
	RateChartRelativeStrengthIndexComposite relativeStrengthIndexComposite;
	
	private ExpandBar createExpandBar(String name,TabFolder tabFolder){
		TabItem tbtm = new TabItem(tabFolder, SWT.NONE);
		tbtm.setText(name);
		ExpandBar expandBar = new ExpandBar(tabFolder, SWT.NONE);
		tbtm.setControl(expandBar);
		return expandBar;
	}
	
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
		
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		//Control Expand Bar 
		ExpandBar expandBarControl=createExpandBar("Control",tabFolder);
		ExpandBar expandBarTrend =createExpandBar("Trend",tabFolder);
		ExpandBar expandBarMomentum =createExpandBar("Momentum",tabFolder);
		ExpandBar expandBarVolume =createExpandBar("Volume",tabFolder);
		ExpandBar expandBarVolatility =createExpandBar("Volatility",tabFolder);
		
		
		//Create a context instance
		IEclipseContext localContextControl=context.createChild();
		localContextControl.set(Composite.class, expandBarControl);
		IEclipseContext localContextTrend=context.createChild();
		localContextTrend.set(Composite.class, expandBarTrend);
		IEclipseContext localContextMomentum=context.createChild();
		localContextMomentum.set(Composite.class, expandBarMomentum);
		IEclipseContext localContextVolume=context.createChild();
		localContextVolume.set(Composite.class, expandBarVolume);
		IEclipseContext localContextVolatility=context.createChild();
		localContextVolatility.set(Composite.class, expandBarVolatility);
		
		//==================================================
		//========             PERIOD                =======    
		//==================================================
		
		ExpandItem xpndtmPeriod = new ExpandItem(expandBarControl, SWT.NONE);
		xpndtmPeriod.setExpanded(true);
		xpndtmPeriod.setText("Period");
		
		
		periodComposite=ContextInjectionFactory.make( RateChartPeriodComposite.class,localContextControl);
		xpndtmPeriod.setControl(periodComposite);
		xpndtmPeriod.setHeight(periodComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		periodComposite.addPeriodChangedListener(new RateChartPeriodComposite.PeriodChangedListener() {
			
			@Override
			public void PeriodChanged() {
				refreshPeriod();
			}
		});
	
		//=============================================
		//======         LOW & HIGHT             ======    
		//=============================================		
		ExpandItem xpndtmLowHight = new ExpandItem(expandBarControl, SWT.NONE);
		xpndtmLowHight.setExpanded(true);
		xpndtmLowHight.setText("Low & Hight");
		//xpndtmLowHight.setHeight(30);
			
		lawAndHightComposite=ContextInjectionFactory.make( RateChartLawAndHightComposite.class,localContextControl);
		xpndtmLowHight.setControl(lawAndHightComposite);
		xpndtmLowHight.setHeight(lawAndHightComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		
		lawAndHightComposite.setRenderers(mainPlotRenderer, secondPlotrenderer,errorPlotRenderer);
		lawAndHightComposite.setSeriesCollections(mainCollection, secondCollection,errorCollection);
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
		
		ExpandItem xpndtmMovingAvg = new ExpandItem(expandBarTrend, SWT.NONE);
		xpndtmMovingAvg.setExpanded(true);
		xpndtmMovingAvg.setText("Moving Average");
		//xpndtmMovingAvg.setHeight(110);
			
		movingAverageComposite=ContextInjectionFactory.make( RateChartMovingAverageComposite.class,localContextTrend);
		xpndtmMovingAvg.setControl(movingAverageComposite);
		xpndtmMovingAvg.setHeight(movingAverageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		
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
		//======              NMAW               ======    
		//=============================================
		
		ExpandItem xpndtmNMAW = new ExpandItem(expandBarTrend, SWT.NONE);
		xpndtmNMAW.setExpanded(true);
		xpndtmNMAW.setText("NMAW");
		//xpndtmMovingAvg.setHeight(110);
			
		NMAWComposite=ContextInjectionFactory.make( NMAWComposite.class,localContextTrend);
		xpndtmNMAW.setControl(NMAWComposite);
		xpndtmNMAW.setHeight(NMAWComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		
		NMAWComposite.setRenderers(mainPlotRenderer, deviationPercentPlotRenderer,percentPlotrenderer,secondPlotrenderer);
		NMAWComposite.setSeriesCollections(mainCollection, deviationPercentCollection,percentCollection,secondCollection);
		NMAWComposite.setPeriodandMaxProfit(period, maxProfit);
		NMAWComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		//=============================================
		//==== EMA (Exponential Moving Average)  ======    
		//=============================================
		
		ExpandItem xpndtmEma = new ExpandItem(expandBarTrend, SWT.NONE);
		xpndtmEma.setText("EMA (Exponential Moving Average)");
		xpndtmEma.setExpanded(true);
		
		emaComposite=ContextInjectionFactory.make( RateChartEMAComposite.class,localContextTrend);
		xpndtmEma.setControl(emaComposite);
		xpndtmEma.setHeight(emaComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		
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
		ExpandItem xpndtmMacd = new ExpandItem(expandBarTrend, SWT.NONE);
		xpndtmMacd.setExpanded(true);
		xpndtmMacd.setText("MACD (Moving Average Convergence/Divergence)");
		
		macdComposite=ContextInjectionFactory.make( RateChartMACDComposite.class,localContextTrend);
		xpndtmMacd.setControl(macdComposite);
		xpndtmMacd.setHeight(macdComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		
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
		//==              Bollinger Bands                 ==    
		//==================================================
		
		ExpandItem xpndtmBollingerBands = new ExpandItem(expandBarVolatility, SWT.NONE);
		xpndtmBollingerBands.setExpanded(true);
		xpndtmBollingerBands.setText("Bollinger Bands");
		
		
		bollingerBandsComposite=ContextInjectionFactory.make( RateChartBollingerBandsComposite.class,localContextVolatility);
		xpndtmBollingerBands.setControl(bollingerBandsComposite);
		xpndtmBollingerBands.setHeight(bollingerBandsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		
		bollingerBandsComposite.setRenderers(mainPlotRenderer, secondPlotrenderer,deviationRenderer);
		bollingerBandsComposite.setSeriesCollections(mainCollection, secondCollection,deviationCollection );
		bollingerBandsComposite.setPeriodandMaxProfit(period, maxProfit);
		bollingerBandsComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		
		//==================================================
		//==              Parabolic SAR                   ==    
		//==================================================
				
		ExpandItem xpndtmParabolicSAR = new ExpandItem(expandBarTrend, SWT.NONE);
		xpndtmParabolicSAR.setExpanded(true);
		xpndtmParabolicSAR.setText("Parabolic SAR");
		//xpndtmParabolicSAR.setHeight(150);
				
		parabolicSARComposite=ContextInjectionFactory.make( RateChartParabolicSAR.class,localContextTrend);
		xpndtmParabolicSAR.setControl(parabolicSARComposite);
		xpndtmParabolicSAR.setHeight(parabolicSARComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		parabolicSARComposite.setRenderers(mainPlotRenderer, secondPlotrenderer);
		parabolicSARComposite.setSeriesCollections(mainCollection, secondCollection);
		parabolicSARComposite.setPeriodandMaxProfit(period, maxProfit);
		parabolicSARComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
			@Override
			public void CollectionRemoved() {
				refreshPeriod();
			}
		});
		
		
		//==================================================
		//==        Relative strength index               ==    
		//==================================================
		ExpandItem xpndtmRSI = new ExpandItem(expandBarMomentum, SWT.NONE);
		xpndtmRSI.setExpanded(true);
		xpndtmRSI.setText("Relative strength index");
		//xpndtmParabolicSAR.setHeight(150);
				
		relativeStrengthIndexComposite=ContextInjectionFactory.make( RateChartRelativeStrengthIndexComposite.class,localContextMomentum);
		xpndtmRSI.setControl(relativeStrengthIndexComposite);
		xpndtmRSI.setHeight(relativeStrengthIndexComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		relativeStrengthIndexComposite.setRenderers(mainPlotRenderer, deviationPercentPlotRenderer,percentPlotrenderer,secondPlotrenderer);
		relativeStrengthIndexComposite.setSeriesCollections(mainCollection, deviationPercentCollection,percentCollection,secondCollection);
		relativeStrengthIndexComposite.setPeriodandMaxProfit(period, maxProfit);
		relativeStrengthIndexComposite.addCollectionRemovedListener(new CollectionRemovedListener() {
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
		if (incoming == null || rate == null || periodComposite == null)
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
			periodComposite.getPeriodSliderFrom().setMaximum(rate.getHistoricalData().size());
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
		
		//periodComposite.getPeriodSliderFrom()
		//periodComposite.getPeriodSliderUpTo()
		
		if(periodComposite.getPeriodSliderFrom().getMaximum()!=allpts)
			periodComposite.getPeriodSliderFrom().setMaximum(allpts);
		if(periodComposite.getPeriodSliderUpTo().getMaximum()!=periodComposite.getPeriodSliderFrom().getSelection())
			periodComposite.getPeriodSliderUpTo().setMaximum(periodComposite.getPeriodSliderFrom().getSelection());
		
		
		period[0]=allpts-periodComposite.getPeriodSliderFrom().getSelection();
		period[1]=allpts-periodComposite.getPeriodSliderUpTo().getSelection();
		
		periodComposite.getPeriodLblFrom().setText(String.valueOf(periodComposite.getPeriodSliderFrom().getSelection()));
		periodComposite.getPeriodlblUpTo().setText(String.valueOf(periodComposite.getPeriodSliderUpTo().getSelection()));
		
		// ===================================
		// Calculate Keep Old and Max Profit
		// ===================================
		keepAndOld = rate.getHistoricalData().calculateKeepAndOld(period,
				HistoricalPoint.FIELD_Close);
		maxProfit = rate.getHistoricalData().calculateMaxProfit(period,
				HistoricalPoint.FIELD_Close);

		String keepAndOldString = String.format("%,.2f%%", keepAndOld * 100);
		String maxProfitString = String.format("%,.2f%%", maxProfit * 100);
		
		
		periodComposite.getLabelMaxProfitPercent().setText(maxProfitString);
		periodComposite.getLabelKeepAndOldPercent().setText(keepAndOldString);
		
		// ===================================
		// Distribute new Period and max profit
		// ===================================
		movingAverageComposite.setPeriodandMaxProfit(period, maxProfit);
		macdComposite.setPeriodandMaxProfit(period, maxProfit);
		emaComposite.setPeriodandMaxProfit(period, maxProfit);
		lawAndHightComposite.setPeriodandMaxProfit(period, maxProfit);
		bollingerBandsComposite.setPeriodandMaxProfit(period, maxProfit);
		parabolicSARComposite.setPeriodandMaxProfit(period, maxProfit);
		relativeStrengthIndexComposite.setPeriodandMaxProfit(period, maxProfit);
		NMAWComposite.setPeriodandMaxProfit(period, maxProfit);
		
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
	        
	        //Axis Profit
	        NumberAxis rangeAxis1 = new NumberAxis("Profit");
	        //rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
	        DecimalFormat format = new DecimalFormat("00.00");
	        rangeAxis1.setNumberFormatOverride(format);
	        rangeAxis1.setAutoRangeIncludesZero(false);
	        
	        //Plot Profit
	        XYPlot plot1 = new XYPlot(secondCollection, null, rangeAxis1, secondPlotrenderer);
	        plot1.setBackgroundPaint(Color.lightGray);
	        plot1.setDomainGridlinePaint(Color.white);
	        plot1.setRangeGridlinePaint(Color.white);
	        
	        //Axis Percent
	        NumberAxis rangeAxis2 = new NumberAxis("Percent");
	        //rangeAxis2.setUpperMargin(1.00);  // to leave room for volume bars
	        //DecimalFormat format = new DecimalFormat("00.00");
	        rangeAxis2.setNumberFormatOverride(format);
	        rangeAxis2.setAutoRangeIncludesZero(false);
	        
	        int i=1;
	        addPercentGraph(plot1,rangeAxis2,i);
	        i++;
	        
	        //Add the deviation Graph
	        addDeviationPercentGraph(plot1, rangeAxis2, i);
			i++;
	        
			//TODO
			
	        return plot1;
	    	
	    }
	    
	    private void addPercentGraph(XYPlot plot, NumberAxis rangeAxis, int i){
	    	
	    	plot.setDataset(i,percentCollection);
	        plot.setRenderer(i, percentPlotrenderer);
	        plot.setRangeAxis(i, rangeAxis);
	        plot.mapDatasetToRangeAxis(i, i);
	    	
	    }
	    
	    private void addDeviationPercentGraph(XYPlot plot, NumberAxis rangeAxis1, int i){
	    	
	    	
	    	//plot.setRangeAxis(i, rangeAxis1);
	    	plot.setDataset(i,deviationPercentCollection);
	    	plot.setRenderer(i, deviationPercentPlotRenderer);
	    	plot.mapDatasetToRangeAxis(i, 1);
	    	
	    	deviationPercentPlotRenderer.setBaseLinesVisible(true);
	    	deviationPercentPlotRenderer.setBaseShapesVisible(false);
	    	
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
	        
	        int i=1;
	        
	        //If Stock or indice add the volume
			if (rate instanceof Indice || rate instanceof Stock) {
				addVolumeBars(plot1,i);
				i++;
			}
			
			//Add the error renderer and collection
			addErrorGraph(plot1, rangeAxis1, i);
			i++;
			//Add the deviation Graph
			addDevGraph(plot1, rangeAxis1, i);
			i++;
			
	        return plot1;
	    	
	    }
	    
	    private void addDevGraph(XYPlot plot, NumberAxis rangeAxis1, int i){
	    	plot.setDataset(i,deviationCollection);
	    	plot.setRenderer(i, deviationRenderer);
	    }
	    
	    private void addErrorGraph(XYPlot plot, NumberAxis rangeAxis1, int i){
	    	
	    	//TODO
	    	
	    	//plot.setRangeAxis(i, rangeAxis1);
	    	plot.setDataset(i,errorCollection);
	    	plot.setRenderer(i, errorPlotRenderer);
	    	
	    	errorPlotRenderer.setBaseLinesVisible(true);
			errorPlotRenderer.setBaseShapesVisible(false);
	    	
	    }
	    
	    
	    /**
	     * create the Volume Bar
	     * 
	     * @param plot
	     */
	    private void addVolumeBars(XYPlot plot, int i){
	    	
	    	NumberAxis rangeAxis2 = new NumberAxis("Volume");
			rangeAxis2.setUpperMargin(1.00); // to leave room for price line
			plot.setRangeAxis(i, rangeAxis2);
			plot.setDataset(i,
					createDataset(HistoricalPoint.FIELD_Volume));
			plot.mapDatasetToRangeAxis(i, 1);
			XYBarRenderer renderer2 = new XYBarRenderer(0.20);
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
					StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
					 new DecimalFormat("0.0"), new DecimalFormat(
							"0,000.00")));
			plot.setRenderer(i, renderer2);
			
			renderer2.setBarPainter(new StandardXYBarPainter());
			renderer2.setShadowVisible(false);
			renderer2.setBarAlignmentFactor(-0.5);
	    	
	    }
	    
}
