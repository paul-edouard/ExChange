 
package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.Second;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.ExchangeChartComposite;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbBarRecorder;
import com.munch.exchange.model.core.ib.bar.IbBarRecorderListener;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.parts.chart.tree.ChartTreeEditorPart;
import com.munch.exchange.services.ejb.interfaces.IIBChartIndicatorProvider;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarListener;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarProvider;


public class ChartEditorPart{
	
	private static Logger logger = Logger.getLogger(ChartEditorPart.class);
	
	public static final String CHART_EDITOR_ID="com.munch.exchange.partdescriptor.charteditor";
	
	public static final String CANDLESTICK="Candlestick";
	public static final String LIVE_CANDLESTICK="Live Candlestick";
	
	private IbChartIndicatorGroup selectedGroup=null;
	
	@Inject
	IbContract contract;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	@Inject
	IIBRealTimeBarProvider realTimeBarProvider;
	
	@Inject
	IIBChartIndicatorProvider chartIndicatorProvider;
	
	IIBRealTimeBarListener realTimeBarListener;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	EPartService partService;
	
	@Inject
	MDirtyable dirty;
	
	//The renderers
	private XYLineAndShapeRenderer mainPlotRenderer=new XYLineAndShapeRenderer(true, false);
	private XYLineAndShapeRenderer secondPlotrenderer=new XYLineAndShapeRenderer(true, false);
	private XYLineAndShapeRenderer percentPlotrenderer=new XYLineAndShapeRenderer(true, false);
	private XYErrorRenderer errorPlotRenderer=new XYErrorRenderer();
	private DeviationRenderer deviationPercentPlotRenderer=new DeviationRenderer();
	private DeviationRenderer deviationRenderer = new DeviationRenderer(true, false);
	private CandlestickRenderer candlestickRenderer=new CandlestickRenderer(0.0);
	
	
	//The Series Collections
	private XYSeriesCollection mainCollection=new XYSeriesCollection();
	private XYSeriesCollection secondCollection=new XYSeriesCollection();
	private XYSeriesCollection percentCollection=new XYSeriesCollection();
	private YIntervalSeriesCollection errorCollection=new YIntervalSeriesCollection();
	private YIntervalSeriesCollection deviationPercentCollection=new YIntervalSeriesCollection();
	private YIntervalSeriesCollection deviationCollection=new YIntervalSeriesCollection();
	private OHLCSeriesCollection oHLCSeriesCollection=new OHLCSeriesCollection();
	
	
	//Plots
	CombinedDomainXYPlot combinedPlot=null;
	XYPlot mainPlot=null;
	XYPlot secondPlot=null;
	
	//Axis
	private DateAxis dateAxis;
	
	
	private JFreeChart chart;
	private Composite compositeChart;
	private ValueMarker threshold;
	private OHLCSeries candleStickSeries= new OHLCSeries(CANDLESTICK);
	
	
	private List<IbBarContainer> barContainers;
	private IbBarRecorder barRecorder=new IbBarRecorder();
	
	private HashMap<WhatToShow, LinkedList<IbBar>> liveBarMap=new HashMap<WhatToShow, LinkedList<IbBar>>();
	private DataUpdater dataUpdater=new DataUpdater();
	
	
	private Combo comboBarSize;
	private Combo comboWhatToShow;
	
	@Inject
	private Shell shell;
	
	@Inject
	public ChartEditorPart() {}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		initBarContainers();
		initMarker();
		
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);
		
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.horizontalSpacing = 1;
		gl_composite.marginHeight = 1;
		gl_composite.verticalSpacing = 1;
		gl_composite.marginWidth = 1;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		//==================================================
		//==                 CHART                        ==    
		//==================================================
		chart = createChart();
		compositeChart = new ChartComposite(parent, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		//==================================================
		//==                COMBOS                        ==    
		//==================================================
				
		comboWhatToShow = new Combo(composite, SWT.NONE);
		comboWhatToShow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(IbBarContainer container:barContainers)
			comboWhatToShow.add(container.getType().toString());
		if(comboWhatToShow.getItems().length>0){
			comboWhatToShow.setText(comboWhatToShow.getItem(0));
		}
		barRecorder.setWhatToShow(IbBar.getWhatToShowFromString(comboWhatToShow.getText()));
		//whatToShow=IbBar.getWhatToShowFromString(comboWhatToShow.getText());
		comboWhatToShow.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				logger.info(comboWhatToShow.getText());
				WhatToShow newWhatToShow=IbBar.getWhatToShowFromString(comboWhatToShow.getText());
				if(newWhatToShow==barRecorder.getWhatToShow())return;
				
				chartGroupSelected();
				
				barRecorder.setWhatToShow(newWhatToShow);
				
				comboWhatToShow.setEnabled(false);
				comboBarSize.setEnabled(false);
				dataUpdater.schedule();
			}
		});
		
		
		comboBarSize = new Combo(composite, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String bSize:IbBar.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		comboBarSize.setText(comboBarSize.getItem(0));
		comboBarSize.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				logger.info(comboBarSize.getText());
				BarSize newBarSize=IbBar.getBarSizeFromString(comboBarSize.getText());
				if(newBarSize==barRecorder.getBarSize())return;
				barRecorder.setBarSize(newBarSize);
				
				comboWhatToShow.setEnabled(false);
				comboBarSize.setEnabled(false);
				dataUpdater.schedule();
			}
		});
		
		addRealTimeBarListener();
		addBarRecorderListener();
		
		
		openChartTreeEditor();
		chartGroupSelected();
		
		dataUpdater.schedule();
	}
	
	private void chartGroupSelected(){
		IbBarContainer container=getCurrentContainer();
		if(container!=null &&
				(selectedGroup==null || selectedGroup.getId()!= container.getIndicatorGroup().getId())){
			//if(selectedGroup!=null)
			//	selectedGroup.removeAllListeners();
			selectedGroup=container.getIndicatorGroup().copy();
			eventBroker.post(IEventConstant.IB_CHART_INDICATOR_GROUP_SELECTED, selectedGroup);
		}
	}
	
	private void openChartTreeEditor(){
		MPart part=partService.findPart(ChartTreeEditorPart.CHART_TREE_EDITOR_ID);
		if(part!=null){
			partService.showPart(part, PartState.CREATE);
			partService.bringToTop(part);
		}
	}
	
	private IbBarContainer getCurrentContainer(){
		WhatToShow newWhatToShow=IbBar.getWhatToShowFromString(comboWhatToShow.getText());
		for(IbBarContainer container:barContainers){
			if(container.getType()==newWhatToShow)
				return container;
		}
		return null;
	}
	
	private IbChartIndicatorGroup getCurrentIndicatorGroup(){
		if(selectedGroup!=null)
			return selectedGroup;
		
		return getCurrentContainer().getIndicatorGroup();
	}
	
	
	private void initBarContainers(){
		barContainers=hisDataProvider.getAllExContractBars(contract);
		if(barContainers==null || barContainers.size()==0)return;
		
		/*
		 * Test if the indicators are well loaded!
		for(IbBarContainer container:barContainers){
			IbChartIndicatorGroup root=container.getIndicatorGroup();
			logger.info("IbChartIndicatorGroup: "+root.getName());
		}
		*/
		
	}
	
	private IbBarContainer getBarContainer(){
		for(IbBarContainer container: barContainers){
			if(container.getType()==barRecorder.getWhatToShow())
				return container;
		}
		return barContainers.get(0);
	}
	
	private void scalePeriode(double fac,double posFac){
		
		double lower=dateAxis.getRange().getLowerBound();
		double upper=dateAxis.getRange().getUpperBound();
		
		
		upper=Math.min(barRecorder.getLastReceivedBar().getTimeInMs()+IbBar.getIntervallInMs(barRecorder.getBarSize())/2,
						upper+ (double)( fac*(1-posFac)));
		lower=Math.max(barRecorder.getFirstReceivedBar().getTimeInMs(),
						lower- (double)( fac*posFac));
    	
    	dateAxis.setRange(lower, upper);
    	
    	dataUpdater.checkSafetyInterval();
    	dataUpdater.schedule();
    }
	
	/*
	private void updateSeries(){
		
		
		
		if(bars==null)return ;
		candleStickSeries.clear();
		double lower=dateAxis.getRange().getLowerBound();
		double upper=dateAxis.getRange().getUpperBound();
		
		
		for(IbBar bar:bars){
			//logger.info(bar);
			if(bar.getTimeInMs()>=upper && bar.getTime()<=lower){
			candleStickSeries.add(new Second(new Date(bar.getTimeInMs())),bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
			
			}
		}
	}
	*/
	
	private void setPeriod(double start, double end){
		
		double lower=dateAxis.getRange().getLowerBound();
		double upper=dateAxis.getRange().getUpperBound();
		
		//if(end>lastBarTime)return;
		//if(start<firstBarTime)return;
		
		upper=Math.min(barRecorder.getLastReceivedBar().getTimeInMs()+IbBar.getIntervallInMs(barRecorder.getBarSize())/2,
						end);
		lower=Math.max(barRecorder.getFirstReceivedBar().getTimeInMs(),
						start);
    	
    	dateAxis.setRange(lower, upper);
    	
    	dataUpdater.checkSafetyInterval();
    	dataUpdater.schedule();
    	
	}
	
	//################################
	//##       CHART CREATION      ##
	//################################    
	
	private JFreeChart createChart() {
		
		//====================
	    //===  Main Axis   ===
	    //====================
		createDomainAxis();
	    	
	    //====================
	    //===  Main Plot   ===
	    //====================
	    createMainPlot();
	    
	    //======================
	    //===  Second Plot   ===
	    //======================
	    createSecondPlot();
	    
	    //====================
	    //===  Combined Plot   ===
	    //====================
	    createCombinedDomainXYPlot();
	    
	    //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart(contract.getLongName(),
                JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, false);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setVisible(false);
      
        return chart;
	  
	 }
	
	private DateAxis createDomainAxis(){
    	 //Axis
		dateAxis = new DateAxis("Time");
		dateAxis.setTickUnit(
        		new ChartDateTickUnit(DateTickUnitType.HOUR, 1, 
        				new SimpleDateFormat("HH:mm"), new SimpleDateFormat("yyyy-MM-dd")));
        
		//dateAxis.setAutoRange(true);
		dateAxis.setLowerMargin(0.01);
		dateAxis.setUpperMargin(0.01);
		//dateAxis.setAutoTickUnitSelection(true);
		Date upper=new Date();
		Date lower=new Date();
		lower.setTime(upper.getTime()-IbBar.getIntervallInMs(barRecorder.getBarSize())*200);
		
		dateAxis.setRange(lower,upper);
        
        return dateAxis;
    }
	
	private void initMarker(){
		// add a labelled marker for the safety threshold...
		threshold = new ValueMarker(690);
		threshold.setLabelOffsetType(LengthAdjustmentType.EXPAND);
		threshold.setPaint(Color.red);
		threshold.setStroke(new BasicStroke(1.0f));
		threshold.setLabel("Price");
		//threshold.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		threshold.setLabelPaint(Color.red);
		threshold.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		threshold.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
    }
	

	/**
     * Create the Main Plot
     * 
     * @return
     */
    private void createMainPlot( ){
    	
    	//====================
    	//=== Main Curves  ===
    	//====================
    	
        mainPlotRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
        mainPlotRenderer.setBaseStroke(new BasicStroke(2.0f));
        
        
        NumberAxis rangeAxis1 = new NumberAxis("Price");
        rangeAxis1.setLowerMargin(0.01);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        rangeAxis1.setNumberFormatOverride(format);
        rangeAxis1.setAutoRangeIncludesZero(false);
        
        //Plot
        mainPlot = new XYPlot(mainCollection, dateAxis, rangeAxis1, mainPlotRenderer);
        mainPlot.setBackgroundPaint(Color.lightGray);
        //plot1.setBackgroundPaint(Color.BLACK);
        mainPlot.setDomainGridlinePaint(Color.white);
        mainPlot.setRangeGridlinePaint(Color.white);
        
        
        int i=1;
        //Add the error renderer and collection
		addErrorGraph(mainPlot, rangeAxis1, i);i++;
		//Add the deviation Graph
		addDevGraph(mainPlot, rangeAxis1, i);i++;
		//Add the Candle Stick Graph
		addCandleStickGraph(mainPlot, rangeAxis1, i);i++;
        
		
		createPosOHLCSeries();
		
		mainPlot.addRangeMarker(threshold);
        
		
    }
    
    private void addDevGraph(XYPlot plot, NumberAxis rangeAxis1, int i){
    	plot.setDataset(i,deviationCollection);
    	plot.setRenderer(i, deviationRenderer);
    }
    
    private void addErrorGraph(XYPlot plot, NumberAxis rangeAxis1, int i){
    	
    	plot.setDataset(i,errorCollection);
    	plot.setRenderer(i, errorPlotRenderer);
    	
    	errorPlotRenderer.setBaseLinesVisible(true);
		errorPlotRenderer.setBaseShapesVisible(false);
    }
    
    private void addCandleStickGraph(XYPlot plot, NumberAxis rangeAxis1, int i){
    	plot.setDataset(i,oHLCSeriesCollection);
    	plot.setRenderer(i, candlestickRenderer);
    	
    	candlestickRenderer.setAutoWidthGap(0.2);
		candlestickRenderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
		
    }
	
    private void createPosOHLCSeries(){
    	
    	//Add the history Candle stick
		oHLCSeriesCollection.addSeries(candleStickSeries);
		int fiel_pos=oHLCSeriesCollection.indexOf(CANDLESTICK);
		if(fiel_pos>=0){
			candlestickRenderer.setSeriesPaint(fiel_pos, Color.black);
			candlestickRenderer.setSeriesStroke(fiel_pos,new BasicStroke(1.5f));
		}
		
		
    }
	
	
    /**
     * Create the Second Plot
     * 
     * @return
     */
    private void createSecondPlot(){
    	
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
        secondPlot = new XYPlot(secondCollection, null, rangeAxis1, secondPlotrenderer);
        secondPlot.setBackgroundPaint(Color.lightGray);
        secondPlot.setDomainGridlinePaint(Color.white);
        secondPlot.setRangeGridlinePaint(Color.white);
        
        //Axis Percent
        NumberAxis rangeAxis2 = new NumberAxis("Percent");
        rangeAxis2.setNumberFormatOverride(format);
        rangeAxis2.setAutoRangeIncludesZero(false);
        
        int i=1;
        //Add percent graph
        addPercentGraph(secondPlot,rangeAxis2,i);i++;
        
        //Add the deviation Graph
        addDeviationPercentGraph(secondPlot, rangeAxis2, i);
		i++;
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
     * create the Combined Plot
     * 
     * @param domainAxis
     * @param plot1
     * @param plot2
     * @return
     */
    private void createCombinedDomainXYPlot(){
    	 combinedPlot = new CombinedDomainXYPlot(dateAxis);
    	 //combinedPlot.add(mainPlot, 5);
	        //cplot.add(plot2, 2);
    	 combinedPlot.setGap(8.0);
    	 combinedPlot.setDomainGridlinePaint(Color.white);
    	 combinedPlot.setDomainGridlinesVisible(true);
    	 combinedPlot.setDomainPannable(true);
    }
    
    
    private void refreshPlots(){
    	
    	removePlots();
    	
    	
    	int mainNbOfSeries=oHLCSeriesCollection.getSeriesCount();
    	mainNbOfSeries+=mainCollection.getSeriesCount();
    	
    	if(mainNbOfSeries>0)
    	combinedPlot.add(mainPlot, 5);
    	
    	
    	int totalNbOfSeries=secondCollection.getSeriesCount();
    	totalNbOfSeries+=percentCollection.getSeriesCount();
    	//logger.info("totalNbOfSeries "+totalNbOfSeries);
    	
    	if(totalNbOfSeries>0)
    		combinedPlot.add(secondPlot, 2);
    	
    }
    
    
    private void removePlots(){
    	
    	List<XYPlot> list = new ArrayList<>(combinedPlot.getSubplots());
    	for (XYPlot plot : list) {
    		combinedPlot.remove(plot);
    	}

    }
    
	//################################
	//##     Series operations      ##
	//################################
    
    public void refreshSeries() {
		clearSeries();
		
		createSeries();
		
		refreshPlots();
	}
    
    private void createSeries(){
		for(IbChartSerie serie:searchSeriesToAdd(getCurrentIndicatorGroup()))
			addSerie(serie);
	}
    
    private void addSerie(IbChartSerie serie){
    	//logger.info("Serie Added: "+serie.getName());
    	
		XYSeries xySerie=createXYSerie(serie);
		int pos=0;
		
		switch (serie.getRendererType()) {
		case MAIN:
			mainCollection.addSeries(xySerie);
			pos=mainCollection.indexOf(serie.getName());
			if(pos>=0){
				mainPlotRenderer.setSeriesShapesVisible(pos, false);
				mainPlotRenderer.setSeriesLinesVisible(pos, true);
				mainPlotRenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				mainPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			}
			break;
		case SECOND:
			secondCollection.addSeries(xySerie);
			pos=secondCollection.indexOf(serie.getName());
			if(pos>=0){
				secondPlotrenderer.setSeriesShapesVisible(pos, false);
				secondPlotrenderer.setSeriesLinesVisible(pos, true);
				secondPlotrenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				secondPlotrenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			}
			break;
		case PERCENT:
			pos=percentCollection.indexOf(serie.getName());
			if(pos>=0)percentCollection.removeSeries(pos);
			break;
		case ERROR:
			pos=errorCollection.indexOf(serie.getName());
			if(pos>=0)errorCollection.removeSeries(pos);
			break;
		case DEVIATION:
			pos=deviationCollection.indexOf(serie.getName());
			if(pos>=0)deviationCollection.removeSeries(pos);
			break;
		case DEVIATION_PERCENT:
			pos=deviationPercentCollection.indexOf(serie.getName());
			if(pos>=0)deviationPercentCollection.removeSeries(pos);
			break;
		default:
			break;
		}
		
	}
    
    private XYSeries  createXYSerie(IbChartSerie serie){
		XYSeries r_series =new XYSeries(serie.getName());
		for(IbChartPoint point:serie.getPoints()){
			//logger.info("Point: "+point.getTime()+", "+point.getValue());
			if(Double.isNaN(point.getValue())){
				r_series.add(point.getTime(),null);
			}
			else{
				r_series.add(point.getTime(),point.getValue());
			}
		}
		
		return 	r_series;
	}
    
    private LinkedList<IbChartSerie> searchSeriesToAdd(IbChartIndicatorGroup group){
		LinkedList<IbChartSerie> toAddList=new LinkedList<IbChartSerie>();
		if(group==null)return toAddList;
		
		for(IbChartIndicatorGroup subGroup:group.getChildren()){
			toAddList.addAll(searchSeriesToAdd(subGroup));
		}
		
		for(IbChartIndicator indicator:group.getIndicators()){
			if(!indicator.isActivated())continue;
			//Compute the series
			indicator.compute(barRecorder.getAllCompletedBars());
			
			for(IbChartSerie serie:indicator.getSeries()){
				if(serie.isActivated() && serie.getPoints().size()>0){
					toAddList.add(serie);
				}
			}
		}
		return toAddList;
		
	}
    
    private void clearSeries(){
		for(IbChartSerie serie:searchSeriesToRemove(getCurrentContainer().getIndicatorGroup()))
			removeChartSerie(serie);
	}
	
	private LinkedList<IbChartSerie> searchSeriesToRemove(IbChartIndicatorGroup group){
		LinkedList<IbChartSerie> toRemoveList=new LinkedList<IbChartSerie>();
		
		
		if(group==null)return toRemoveList;
		
		
		for(IbChartIndicatorGroup subGroup:group.getChildren()){
			toRemoveList.addAll(searchSeriesToRemove(subGroup));
		}
		
		for(IbChartIndicator indicator:group.getIndicators()){
			for(IbChartSerie serie:indicator.getSeries()){
				//if(!serie.isActivated()){
					toRemoveList.add(serie);
				//}
			}
		}
		return toRemoveList;
		
	}
	
	private void removeChartSerie(IbChartSerie serie){
		int pos=0;
		switch (serie.getRendererType()) {
		case MAIN:
			pos=mainCollection.indexOf(serie.getName());
			if(pos>=0)mainCollection.removeSeries(pos);
			break;
		case SECOND:
			pos=secondCollection.indexOf(serie.getName());
			if(pos>=0)secondCollection.removeSeries(pos);
			break;
		case PERCENT:
			pos=percentCollection.indexOf(serie.getName());
			if(pos>=0)percentCollection.removeSeries(pos);
			break;
		case ERROR:
			pos=errorCollection.indexOf(serie.getName());
			if(pos>=0)errorCollection.removeSeries(pos);
			break;
		case DEVIATION:
			pos=deviationCollection.indexOf(serie.getName());
			if(pos>=0)deviationCollection.removeSeries(pos);
			break;
		case DEVIATION_PERCENT:
			pos=deviationPercentCollection.indexOf(serie.getName());
			if(pos>=0)deviationPercentCollection.removeSeries(pos);
			break;

		default:
			break;
		}
	}
	
	private void resetChartSerieColor(IbChartSerie serie){
		int pos=0;
		switch (serie.getRendererType()) {
		case MAIN:
			pos=mainCollection.indexOf(serie.getName());
			if(pos>=0)
				mainPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			break;
		case SECOND:
			pos=secondCollection.indexOf(serie.getName());
			if(pos>=0)
				secondPlotrenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			break;
		case PERCENT:
			pos=percentCollection.indexOf(serie.getName());
			if(pos>=0)
				percentPlotrenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			break;
		case ERROR:
			pos=errorCollection.indexOf(serie.getName());
			if(pos>=0)
				errorPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			break;
		case DEVIATION:
			pos=deviationCollection.indexOf(serie.getName());
			if(pos>=0)
				deviationRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			break;
		case DEVIATION_PERCENT:
			pos=deviationPercentCollection.indexOf(serie.getName());
			if(pos>=0)
				deviationPercentPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B()));
			break;

		default:
			break;
		}
	}
	
	private void clearAllSeriesOfIndicator(IbChartIndicator indicator){
		for(IbChartSerie serie:indicator.getSeries()){
    		this.removeChartSerie(serie);
    	}
	}
	
	private void addAllSeriesOfIndicatior(IbChartIndicator indicator){
		indicator.compute(barRecorder.getAllCompletedBars());
		for(IbChartSerie serie:indicator.getSeries()){
    		if(!serie.isActivated())continue;
    		if(serie.getPoints().size()==0)continue;
    		this.addSerie(serie);
    	}
	}
    
    
	//################################
  	//##       Event Reaction       ##
  	//################################
	
	private boolean isDirty(){
		
		/*
		logger.info("Selected Group: "+selectedGroup);
		logger.info("Current Group: "+getCurrentContainer().getIndicatorGroup());
		IbChartIndicatorGroup group=new IbChartIndicatorGroup();
		group.setId(0);
		group.setChildren(new LinkedList<IbChartIndicatorGroup>());
		group.setIndicators(new LinkedList<IbChartIndicator>());
		logger.info("New Group: "+getCurrentContainer().getIndicatorGroup());
		*/
		
		boolean equals=selectedGroup.identical(getCurrentContainer().getIndicatorGroup());
		//boolean equals=selectedGroup.identical(group);
		//logger.info("Is equal: "+equals);
		dirty.setDirty(!equals);
		//dirty.setDirty(true);
		return equals;
	}
	private boolean isCompositeAbleToReact(){
		if (shell.isDisposed())
			return false;
		
		if(comboWhatToShow==null)return false;
		
		if(comboWhatToShow.isDisposed())return false;
		
		if(chart==null)return false;
		
		if(selectedGroup==null) return false;
		
		return true;
	}
	
	@Inject
	public void chartIndicatorActivationChanged( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_ACTIVATION_CHANGED) IbChartIndicator indicator){
		//logger.info("chartIndicatorActivationChanged");
		
		
	    if(!isCompositeAbleToReact())return;
	    
	    //IbChartIndicatorGroup indGroup=this.getCurrentIndicatorGroup();
	    //logger.info("Search the indicator");
	    if(!selectedGroup.containsIndicator(indicator))return;
	    
	    //logger.info("Test the activation");
	    if(indicator.isActivated()){
	    	addAllSeriesOfIndicatior(indicator);
	    }
	    else{
	    	clearAllSeriesOfIndicator(indicator);
	    }
	    
	    isDirty();
	    //refreshSeries();
	    
	    refreshPlots();
	    
	}
	
	@Inject
	public void chartIndicatorParameterChanged( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_PARAMETER_CHANGED) IbChartIndicator indicator){
		
		if(!isCompositeAbleToReact())return;
	    
	   // IbChartIndicatorGroup indGroup=this.getCurrentIndicatorGroup();
	    if(!selectedGroup.containsIndicator(indicator))return;
	    
	    if(!indicator.isActivated())return;
	    
	    //for(IbChartParameter param:indicator.getParameters())
	    //	logger.info("Param: "+param.getValue());
	    
	    clearAllSeriesOfIndicator(indicator);
	    addAllSeriesOfIndicatior(indicator);
	    
	    isDirty();
	}

	@Inject
	public void chartSerieActivationChanged( @Optional  @UIEventTopic(IEventConstant.IB_CHART_SERIE_ACTIVATION_CHANGED) IbChartSerie serie){
		
		if(!isCompositeAbleToReact())return;
	    
	   // IbChartIndicatorGroup indGroup=this.getCurrentIndicatorGroup();
	    if(!selectedGroup.containsSerie(serie))return;
	    
	    if(serie.isActivated()){
	    	this.addSerie(serie);
	    }
	    else{
	    	this.removeChartSerie(serie);
	    }
	    
	    isDirty();
	    refreshPlots();
	}
	
	@Inject
	public void chartSerieColorChanged( @Optional  @UIEventTopic(IEventConstant.IB_CHART_SERIE_COLOR_CHANGED) IbChartSerie serie){
		
		if(!isCompositeAbleToReact())return;
	    
	    //IbChartIndicatorGroup indGroup=this.getCurrentIndicatorGroup();
	    if(!selectedGroup.containsSerie(serie))return;
	    
	    resetChartSerieColor(serie);
	    
	    isDirty();
	}
	
    
	@PreDestroy
	public void preDestroy() {
		realTimeBarProvider.removeRealTimeBarListener(realTimeBarListener);
	}
	
	
	@Focus
	public void onFocus() {
		chartGroupSelected();
	}
	
	
	@Persist
	public void save() {
		chartIndicatorProvider.update(selectedGroup);
		getCurrentContainer().setIndicatorGroup(selectedGroup.copy());
		dirty.setDirty(false);
		
		logger.info("Data are saved");
		
	}
	
	
	
	//################################
  	//##       Chart Composite      ##
  	//################################
	
	class ChartComposite extends ExchangeChartComposite{
		
		int x=-1;
		double trans=0;
		double startP=0;
		double endP=0;
		double w=0;
		
		public ChartComposite(Composite comp, int style, JFreeChart chart) {
			super(comp, style, chart);
		}
		
		@Override
		public void mouseDown(MouseEvent event) {
			// TODO Auto-generated method stub
			//logger.info("mouseDown: "+event);
			if(event.button==1){
				x=event.x;
				w=this.getChartRenderingInfo().getPlotInfo().getDataArea().getWidth();
				trans=0;
				startP=dateAxis.getRange().getLowerBound();
				endP=dateAxis.getRange().getUpperBound();
				
			}
			/*
			else{
				resetPeriode();
				updateSeries();
			}
			*/
		}

		@Override
		public void mouseUp(MouseEvent event) {
			// TODO Auto-generated method stub
			//super.mouseUp(event);
			if(x>0){
				
				double fac=((double)(x-event.x))/((double) w);
				double diff=(endP-startP);
				trans=fac*diff;
				
				setPeriod(startP+(long)(trans), endP+(long)(trans));
				
				x=-1;
			}
		}
		
		
		

		@Override
		public void mouseMove(MouseEvent event) {
			
			
			if(x>0){
				
				double fac=((double)(x-event.x))/((double) w);
				double diff=(endP-startP);
				trans=fac*diff;
				
				setPeriod(startP+(long)(trans), endP+(long)(trans));
				
				//logger.info("Rec: "+rec.width+", fac: "+fac+", trans: "+trans);
				
				//setPeriod(startP+(long)(trans), endP+(long)(trans));
				//x=event.x;
			}
			
		}

		@Override
		public void mouseScrolled(MouseEvent event) {
			
			w=this.getChartRenderingInfo().getPlotInfo().getPlotArea().getWidth();
			
			
			double fac=((double)event.x)/((double) w);
			//logger.info("fac: "+fac);
			
			double lower=dateAxis.getRange().getLowerBound();
			double upper=dateAxis.getRange().getUpperBound();
			
			scalePeriode(-event.count*( (int) (upper-lower)/10),fac);
			
			super.mouseScrolled(event);
		}
		
	}
	
	
	//######################################
  	//##        Bar Updaters              ##
  	//######################################
	
	private void addRealTimeBarListener(){
		
		realTimeBarListener=new IIBRealTimeBarListener() {
			
			@Override
			public void realTimeBarChanged(IbBar bar) {
				//Register the new Bar and save it back into the map
				if(!liveBarMap.containsKey(bar.getType()))
					liveBarMap.put(bar.getType(), new LinkedList<IbBar>());
				
				LinkedList<IbBar> bars=liveBarMap.get(bar.getType());
				if(bars.isEmpty() || bars.getLast().getTime()!=bar.getTime()){
					bars.add(bar);
				}
				else{
					bars.set(bars.size()-1, bar);
				}
				
				//Call the Real Time Bar Updater
				if(bar.getType()==getBarContainer().getType()){
					//logger.info("New Bar: "+bar);
					LinkedList<IbBar> realTimeBars=IbBar.convertIbBars(bars, barRecorder.getBarSize());
					if(realTimeBars==null ||realTimeBars.isEmpty())return;
					barRecorder.addBar(realTimeBars.getLast());
					//lastBarTime=bar.getTimeInMs();
					//Display.getDefault().asyncExec(new realTimeBarUpdater(liveBars));
				}
			}
			
			@Override
			public int getContractId() {
				return contract.getId();
			}
		};
		
		realTimeBarProvider.addIbRealTimeBarListener(realTimeBarListener);
		
	}
	
	
	private void addBarRecorderListener(){
		barRecorder.addListener(new IbBarRecorderListener() {
			
			private IbBar lastBar=null;
			private List<IbBar> addedBars=null;
			private List<IbBar> replacedBars=null;
			
			@Override
			public void newCompletedBar(IbBar bar) {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						refreshSeries();
					}
				});
			}
			
			@Override
			public void barReplaced(List<IbBar> bars) {
				
				//logger.info("Bar Replaced!");
				
				replacedBars=bars;
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						//candleStickSeries.clear();
						for(IbBar bar:replacedBars){
							Second sec=new Second(new Date(bar.getTimeInMs() - bar.getIntervallInMs()/2));
							int index=candleStickSeries.indexOf(sec);
							//logger.info("Index of: "+index);
							if(index>=0){
								candleStickSeries.remove(index);
								candleStickSeries.add(sec,bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
							}
							
						}
						candleStickSeries.fireSeriesChanged();
					}
				});
				
			}
			
			@Override
			public void barAdded(List<IbBar> bars) {
				addedBars=bars;
				
				//logger.info("Bar Added!");
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						//candleStickSeries.clear();
						Second LastSec=null;
						if(candleStickSeries.getItemCount()>0){
							OHLCItem item=(OHLCItem)candleStickSeries.getDataItem(candleStickSeries.getItemCount()-1);
							LastSec=new Second(new Date(item.getPeriod().getMiddleMillisecond()));
						}
						
						for(IbBar bar:addedBars){
							Second sec=new Second(new Date(bar.getTimeInMs() - bar.getIntervallInMs()/2));
							if(LastSec!=null && LastSec.equals(sec))continue;
							candleStickSeries.add(sec,bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
							//candleStickSeries.getDataItem(candleStickSeries.getItemCount()-1)
						}
						
						double diff=barRecorder.getLastReceivedBar().getTimeInMs()-dateAxis.getRange().getUpperBound();
						//logger.info("Diff: "+diff);
						if(diff>=0 && diff<=barRecorder.getLastReceivedBar().getIntervallInMs()){
							dateAxis.setRange(dateAxis.getRange().getLowerBound(),
									barRecorder.getLastReceivedBar().getTimeInMs()+barRecorder.getLastReceivedBar().getIntervallInMs()/2);
						}
						
						candleStickSeries.fireSeriesChanged();
						threshold.setValue(lastBar.getClose());
						
						//Refresh the series only if the more than one bar were added
						//Otherwise the refresh will be called from the newCompletedBar function
						if(addedBars.size()>1)
							refreshSeries();
						
						
						comboWhatToShow.setEnabled(true);
						comboBarSize.setEnabled(true);
					}
				});
				
				//barRecorder.getAllBars();
				//Udate the Series
			}
			
			@Override
			public void allBarsCleared() {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						candleStickSeries.clear();
					}
				});
				
			}

			@Override
			public void lastBarUpdated(IbBar bar) {
				lastBar=bar;
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						threshold.setValue(lastBar.getClose());
					}
				});
			}
		});
	}
	
	
	
	//######################################
  	//##           Data Updater          ##
  	//######################################
	private class DataUpdater extends Job{
		
		private long from;
		private long to;
		
		private final static long loadingSize=1000;
		private boolean loadPastValues=false;
		private boolean pastValueAvailable=true;
		
		public DataUpdater() {
			super("Data Updater");
		}

		public void checkSafetyInterval(){
			double lastSec=barRecorder.getFirstReceivedBar().getTime();
			double lowerRangeSec=dateAxis.getRange().getLowerBound()/1000L;
			double diff=Math.abs(lowerRangeSec-lastSec);
			if(diff<loadingSize/4)
				loadPastValues=true;
		}
		
		@Override
		public IStatus run(IProgressMonitor monitor) {
			
			//selectedGroup.removeAllListeners();
			
			if(barRecorder.isEmpty()){
				long intervall=IbBar.getIntervallInSec(barRecorder.getBarSize());
				to=new Date().getTime()/1000;
				from=to-loadingSize*intervall;
				
				List<IbBar> bars=hisDataProvider.getBarsFromTo(getBarContainer(), barRecorder.getBarSize(), from, to);
				List<IbBar> newBars=hisDataProvider.downloadLastBars(getBarContainer(),barRecorder.getBarSize());
				//logger.info("Number of bars: "+bars.size());
				//logger.info("Number of new bars: "+newBars.size());
					
				List<IbBar> toAdd=new LinkedList<IbBar>();
				if(!bars.isEmpty() && !newBars.isEmpty()){
					for(IbBar bar:newBars){
						if(bars.get(bars.size()-1).getTime()<bar.getTime()){
							toAdd.add(bar);
						}
					}
					bars.addAll(toAdd);
				}
				pastValueAvailable=true;
				barRecorder.addBars(bars);
			}
			else if(loadPastValues && pastValueAvailable){
				long intervall=IbBar.getIntervallInSec(barRecorder.getBarSize());
				to=barRecorder.getFirstReceivedBar().getTime();
				from=to-loadingSize*intervall;
				logger.info("Ask historical data: ");
				//hisDataProvider.init();
				List<IbBar> bars=hisDataProvider.getBarsFromTo(getBarContainer(), barRecorder.getBarSize(), from, to);
				
				if(bars.size()==0){
					pastValueAvailable=false;
					//selectedGroup.addListener(indicatorGroupListener);
					return Status.OK_STATUS;
				}
				barRecorder.addBars(bars);
				loadPastValues=false;
			}
			
			//selectedGroup.addListener(indicatorGroupListener);
			return Status.OK_STATUS;
		}
		
	}
	

}