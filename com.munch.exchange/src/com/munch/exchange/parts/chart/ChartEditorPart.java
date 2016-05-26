 
package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
import org.jfree.util.Log;
import org.jfree.util.ShapeUtilities;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.SecType;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.ExchangeChartComposite;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarRecorder;
import com.munch.exchange.model.core.ib.bar.BarRecorderListener;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
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
	private XYAreaRenderer profitPlotRenderer=new XYAreaRenderer();
	private XYAreaRenderer riskPlotRenderer=new XYAreaRenderer();
	private XYErrorRenderer errorPlotRenderer=new XYErrorRenderer();
	private DeviationRenderer deviationPercentPlotRenderer=new DeviationRenderer();
	private DeviationRenderer deviationRenderer = new DeviationRenderer(true, false);
	private CandlestickRenderer candlestickRenderer=new CandlestickRenderer(0.0);
	
	
	//The Series Collections
	private XYSeriesCollection mainCollection=new XYSeriesCollection();
	private XYSeriesCollection secondCollection=new XYSeriesCollection();
	private XYSeriesCollection percentCollection=new XYSeriesCollection();
	private XYSeriesCollection profitCollection=new XYSeriesCollection();
	private XYSeriesCollection riskCollection=new XYSeriesCollection();
	private YIntervalSeriesCollection errorCollection=new YIntervalSeriesCollection();
	private YIntervalSeriesCollection deviationPercentCollection=new YIntervalSeriesCollection();
	private YIntervalSeriesCollection deviationCollection=new YIntervalSeriesCollection();
	private OHLCSeriesCollection oHLCSeriesCollection=new OHLCSeriesCollection();
	
	
	//Plots
	CombinedDomainXYPlot combinedPlot=null;
	XYPlot mainPlot=null;
	XYPlot secondPlot=null;
	XYPlot percentPlot=null;
	XYPlot profitPlot=null;
	XYPlot riskPlot=null;
	
	//Axis
	private DateAxis dateAxis;
	private NumberAxis mainAxis;
	private NumberAxis secondAxis;
	private NumberAxis percentAxis;
	private NumberAxis profitAxis;
	private NumberAxis riskAxis;
	
	
	private JFreeChart chart;
	private Composite compositeChart;
	private ValueMarker threshold;
	private OHLCSeries candleStickSeries= new OHLCSeries(CANDLESTICK);
	private HashSet<Long> candleStickSecondes=new HashSet<Long>();
	
	
	private List<BarContainer> barContainers;
	private BarRecorder barRecorder=new BarRecorder();
	
	private HashMap<WhatToShow, LinkedList<ExBar>> liveBarMap=new HashMap<WhatToShow, LinkedList<ExBar>>();
	private DataUpdater dataUpdater=new DataUpdater();
	
	
	private Combo comboBarSize;
	private Combo comboWhatToShow;
	private Combo comboBarType;
	
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
		GridLayout gl_composite = new GridLayout(3, false);
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
		for(BarContainer container:barContainers)
			comboWhatToShow.add(container.getType().toString());
		if(comboWhatToShow.getItems().length>0){
			comboWhatToShow.setText(comboWhatToShow.getItem(0));
		}
		barRecorder.setWhatToShow(BarUtils.getWhatToShowFromString(comboWhatToShow.getText()));
		//whatToShow=IbBar.getWhatToShowFromString(comboWhatToShow.getText());
		comboWhatToShow.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				logger.info(comboWhatToShow.getText());
				WhatToShow newWhatToShow=BarUtils.getWhatToShowFromString(comboWhatToShow.getText());
				if(newWhatToShow==barRecorder.getWhatToShow())return;
				
				chartGroupSelected();
				
				barRecorder.setWhatToShow(newWhatToShow);
				
				comboWhatToShow.setEnabled(false);
				comboBarSize.setEnabled(false);
				comboBarType.setEnabled(false);
				dataUpdater.schedule();
			}
		});
		
		comboBarType = new Combo(composite, SWT.NONE);
		comboBarType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboBarType.setItems(BarType.toStringArray());
		comboBarType.setText(BarType.TIME.name());
		comboBarType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				logger.info(comboBarType.getText());
				BarType barType = BarType.valueOf(comboBarType.getText());
				if(barType==barRecorder.getBartype())return;
				
				comboBarSize.removeAll();
				if(barType == BarType.TIME){
					for(String bSize:BarUtils.getAllBarSizesAsString())
						comboBarSize.add(bSize);
					comboBarSize.setText(comboBarSize.getItem(0));
					
					BarSize newBarSize=BarUtils.getBarSizeFromString(comboBarSize.getText());
					barRecorder.setBarSize(newBarSize);
					selectedGroup.setBarSize(newBarSize);
					
				}
				else{
					for(String bRange:BarUtils.getAllBarRangesForForex())
						comboBarSize.add(bRange);
					comboBarSize.setText(comboBarSize.getItem(4));
					barRecorder.setRange(BarUtils.convertForexRange(comboBarSize.getText()));
				}
				
				barRecorder.setBartype(barType);
				selectedGroup.setBarType(barType);
				
				comboWhatToShow.setEnabled(false);
				comboBarSize.setEnabled(false);
				comboBarType.setEnabled(false);
				barRecorder.clearAll();
				dataUpdater.clear();
				dataUpdater.schedule();
				
			}
		});
		
		comboBarSize = new Combo(composite, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String bSize:BarUtils.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		comboBarSize.setText(comboBarSize.getItem(0));
		comboBarSize.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				logger.info(comboBarSize.getText());
				if(barRecorder.getBartype() == BarType.TIME){
					BarSize newBarSize=BarUtils.getBarSizeFromString(comboBarSize.getText());
					if(newBarSize==barRecorder.getBarSize())return;
				
					barRecorder.setBarSize(newBarSize);
					selectedGroup.setBarSize(newBarSize);
				}
				else{
					double range = BarUtils.convertForexRange(comboBarSize.getText());
					Log.info("Range: "+range);
					if(range==barRecorder.getRange())return;
					
					barRecorder.setRange(range);
					selectedGroup.setRange(range);
				}
				
				comboWhatToShow.setEnabled(false);
				comboBarSize.setEnabled(false);
				comboBarType.setEnabled(false);
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
		BarContainer container=getCurrentContainer();
		if(container!=null &&
				(selectedGroup==null || selectedGroup.getId()!= container.getIndicatorGroup().getId())){
			//if(selectedGroup!=null)
			//	selectedGroup.removeAllListeners();
			selectedGroup=container.getIndicatorGroup().copy();
			selectedGroup.setBarSize(BarUtils.getBarSizeFromString(comboBarSize.getText()));
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
	
	private BarContainer getCurrentContainer(){
		WhatToShow newWhatToShow=BarUtils.getWhatToShowFromString(comboWhatToShow.getText());
		for(BarContainer container:barContainers){
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
		barContainers=hisDataProvider.getAllBarContainers(contract);
		if(barContainers==null || barContainers.size()==0)return;
		
		/*
		 * Test if the indicators are well loaded!
		for(IbBarContainer container:barContainers){
			IbChartIndicatorGroup root=container.getIndicatorGroup();
			logger.info("IbChartIndicatorGroup: "+root.getName());
		}
		*/
		
	}
	
	private BarContainer getBarContainer(){
		for(BarContainer container: barContainers){
			if(container.getType()==barRecorder.getWhatToShow())
				return container;
		}
		return barContainers.get(0);
	}
	
	private void scalePeriode(double fac,double posFac){
		
		double lower=dateAxis.getRange().getLowerBound();
		double upper=dateAxis.getRange().getUpperBound();
		
		
		upper=Math.min(barRecorder.getLastReceivedBar().getTimeInMs()+BarUtils.getIntervallInMs(barRecorder.getBarSize())/2,
						upper+ (double)( fac*(1-posFac)));
		lower=Math.max(barRecorder.getFirstReceivedBar().getTimeInMs(),
						lower- (double)( fac*posFac));
    	
    	dateAxis.setRange(lower, upper);
    	mainAxis.configure();
    	secondAxis.configure();
    	profitAxis.configure();
    	riskAxis.configure();
    	
    	dataUpdater.checkSafetyInterval();
    	dataUpdater.schedule();
    }
	
	
	private void setPeriod(double start, double end){
		
		double lower=dateAxis.getRange().getLowerBound();
		double upper=dateAxis.getRange().getUpperBound();
		
		//if(end>lastBarTime)return;
		if(barRecorder.getLastReceivedBar()==null)return;
		
		upper=Math.min(barRecorder.getLastReceivedBar().getTimeInMs()+BarUtils.getIntervallInMs(barRecorder.getBarSize())/2,
						end);
		lower=Math.max(barRecorder.getFirstReceivedBar().getTimeInMs(),
						start);
    	
    	dateAxis.setRange(lower, upper);
    	mainAxis.configure();
    	secondAxis.configure();
    	profitAxis.configure();
    	riskAxis.configure();
    	
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

	    //======================
	    //===  Percent Plot   ===
	    //======================
	    createPercentPlot();
	    
	    //======================
	    //===  Profit Plot   ===
	    //======================
	    createProfitPlot();
	    
	    //======================
	    //===  Risk Plot   ===
	    //======================
	    createRiskPlot();
	    
	    
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
		ExBar lastBar=hisDataProvider.getLastTimeBar(getBarContainer(), barRecorder.getBarSize());
		upper.setTime(lastBar.getTimeInMs());
		
		Date lower=new Date();
		lower.setTime(upper.getTime()-BarUtils.getIntervallInMs(barRecorder.getBarSize())*200);
		
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
        
        
        mainAxis = new NumberAxis("Price ["+this.contract.getCurrency()+"]");
        mainAxis.setLowerMargin(0.01);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        if(this.contract.getSecType()==SecType.CASH){
        	format = new DecimalFormat("00.0000");
        }
        mainAxis.setNumberFormatOverride(format);
        mainAxis.setAutoRangeIncludesZero(false);
        //mainAxis.setLabelFont();
        //mainAxis.getLabelFont().
        //Font.
        
        //Plot
        mainPlot = new XYPlot(mainCollection, dateAxis, mainAxis, mainPlotRenderer);
        mainPlot.setBackgroundPaint(Color.lightGray);
        //plot1.setBackgroundPaint(Color.BLACK);
        mainPlot.setDomainGridlinePaint(Color.white);
        mainPlot.setRangeGridlinePaint(Color.white);
        
        
        int i=1;
        //Add the error renderer and collection
		addErrorGraph(mainPlot, mainAxis, i);i++;
		//Add the deviation Graph
		addDevGraph(mainPlot, mainAxis, i);i++;
		//Add the Candle Stick Graph
		addCandleStickGraph(mainPlot, mainAxis, i);i++;
        
		
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
                new DecimalFormat("0.0"), new DecimalFormat("0.0000")));
        
        if (secondPlotrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) secondPlotrenderer;
            renderer.setBaseStroke(new BasicStroke(2.0f));
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesPaint(1, Color.DARK_GRAY);
            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
        }
        
        //Axis Profit
        secondAxis = new NumberAxis("Value");
        //rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
//        DecimalFormat format = new DecimalFormat("00.0000");
//        secondAxis.setNumberFormatOverride(format);
        secondAxis.setAutoRangeIncludesZero(false);
        
        //Plot Profit
        secondPlot = new XYPlot(secondCollection, null, secondAxis, secondPlotrenderer);
        secondPlot.setBackgroundPaint(Color.lightGray);
        secondPlot.setDomainGridlinePaint(Color.white);
        secondPlot.setRangeGridlinePaint(Color.white);
        
        /*
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
		*/
    }
    
    
   private void createPercentPlot(){
    	
    	percentPlotrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
        
        if (percentPlotrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) percentPlotrenderer;
            renderer.setBaseStroke(new BasicStroke(2.0f));
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesPaint(1, Color.DARK_GRAY);
            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
        }
        
        //Axis Profit
        percentAxis = new NumberAxis("Value");
        //rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        percentAxis.setNumberFormatOverride(format);
        percentAxis.setAutoRangeIncludesZero(false);
        
        //Plot Profit
        percentPlot = new XYPlot(percentCollection, null, percentAxis, percentPlotrenderer);
        percentPlot.setBackgroundPaint(Color.lightGray);
        percentPlot.setDomainGridlinePaint(Color.white);
        percentPlot.setRangeGridlinePaint(Color.white);
        
   }
    
    
    
    private void createProfitPlot(){
    	 profitPlotRenderer.setBaseStroke(new BasicStroke(2.0f));
         
         profitAxis = new NumberAxis("Profit ["+this.contract.getCurrency()+"]");
         profitAxis.setLowerMargin(0.01);  // to leave room for volume bars
         DecimalFormat format = new DecimalFormat("00.00");
         profitAxis.setNumberFormatOverride(format);
         profitAxis.setAutoRangeIncludesZero(false);
         
         //Plot
         profitPlot = new XYPlot(profitCollection, dateAxis, profitAxis, profitPlotRenderer);
         profitPlot.setBackgroundPaint(Color.lightGray);
         profitPlot.setDomainGridlinePaint(Color.white);
         profitPlot.setRangeGridlinePaint(Color.white);
    }
    
    private void createRiskPlot(){
   	 	riskPlotRenderer.setBaseStroke(new BasicStroke(2.0f));
        
        riskAxis = new NumberAxis("Risk ["+this.contract.getCurrency()+"]");
        riskAxis.setLowerMargin(0.01);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        riskAxis.setNumberFormatOverride(format);
        riskAxis.setAutoRangeIncludesZero(false);
        
        //Plot
        riskPlot = new XYPlot(riskCollection, dateAxis, riskAxis, riskPlotRenderer);
        riskPlot.setBackgroundPaint(Color.lightGray);
        riskPlot.setDomainGridlinePaint(Color.white);
        riskPlot.setRangeGridlinePaint(Color.white);
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
    	 combinedPlot.setGap(4.0);
    	 combinedPlot.setDomainGridlinePaint(Color.white);
    	 combinedPlot.setDomainGridlinesVisible(true);
    	 combinedPlot.setDomainPannable(true);
    	 combinedPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    }
    
    
    private void refreshPlots(){
    	
    	removePlots();
    	
    	
    	//Main Plot
    	int mainNbOfSeries=oHLCSeriesCollection.getSeriesCount();
    	mainNbOfSeries+=mainCollection.getSeriesCount();
    	
    	if(mainNbOfSeries>0)
    		combinedPlot.add(mainPlot, 5);
    	
    	
    	//Second Plot
    	int totalNbOfSeries=secondCollection.getSeriesCount();
    	if(totalNbOfSeries>0)
    		combinedPlot.add(secondPlot, 2);
    	
//		Percent Plot    	
    	int totalPercentNbOfSeries=percentCollection.getSeriesCount();
    	if(totalPercentNbOfSeries>0)
    		combinedPlot.add(percentPlot, 2);
    	
    	
    	//Profit plot
    	int totalProfitNbOfSeries=profitCollection.getSeriesCount();
    	if(totalProfitNbOfSeries>0)
    		combinedPlot.add(profitPlot, 2);
    	
    	//Risk plot
    	int totalRiskNbOfSeries=riskCollection.getSeriesCount();
    	if(totalRiskNbOfSeries>0)
    		combinedPlot.add(riskPlot, 2);
    	
    	
    }
    
    
    private void removePlots(){
    	
    	@SuppressWarnings("unchecked")
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
    	
		//XYSeries xySerie=createXYSerie(serie);
		int pos=0;
		
		switch (serie.getRendererType()) {
		case MAIN:
			addIbChartSerieToXYSeriesCollection(serie, mainPlotRenderer, mainCollection);
			break;
		case SECOND:
			addIbChartSerieToXYSeriesCollection(serie, secondPlotrenderer, secondCollection);
			break;
		case PERCENT:
			addIbChartSerieToXYSeriesCollection(serie, percentPlotrenderer, percentCollection);
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
		case PROFIT:
			addIbChartSerieToAreaCollection(serie, profitPlotRenderer, profitCollection);
			break;
		case RISK:
			addIbChartSerieToAreaCollection(serie, riskPlotRenderer, riskCollection);
			break;
		default:
			break;
		}
		
	}
    
    private void addIbChartSerieToAreaCollection(IbChartSerie serie, XYAreaRenderer rend,  XYSeriesCollection col){
    	XYSeries xySerie=createXYSerie(serie);
    	Color color=new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B(),100);
    	
    	col.addSeries(xySerie);
		int pos=col.indexOf(xySerie.getKey());
		if(pos>=0){
			//rend.setSeriesShapesVisible(pos, false);
			//rend.setSeriesLinesVisible(pos, true);
			rend.setSeriesStroke(pos,new BasicStroke(2.0f));
			rend.setSeriesPaint(pos, color);
		}
    	
    }
    
    private void addIbChartSerieToXYSeriesCollection(IbChartSerie serie, XYLineAndShapeRenderer rend,  XYSeriesCollection col){
    	XYSeries xySerie=createXYSerie(serie);
    	Color color=new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B());
    	
    	//logger.info("Serie Type: "+serie.getShapeType().toString());
    	
    	switch (serie.getShapeType()){
    		case UP_TRIANGLE:
    			addSeriesAsShape(rend, col, xySerie, ShapeUtilities.createUpTriangle(5), color);
    			break;
    		case DOWN_TRIANGLE:
    			addSeriesAsShape(rend, col, xySerie, ShapeUtilities.createDownTriangle(5), color);
    			break;
    		default:
    			addSeriesAsLine(rend, col, xySerie, color);
    			break;
    			
    	}
    	
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
			if(pos>=0){
				
				updatePlotRendererBeforeSerieDeletion(mainCollection.getSeriesCount(), pos, mainPlotRenderer);
				mainCollection.removeSeries(pos);
			}
			break;
		case SECOND:
			pos=secondCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(secondCollection.getSeriesCount(), pos, secondPlotrenderer);
				secondCollection.removeSeries(pos);
			}
			break;
		case PERCENT:
			pos=percentCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(percentCollection.getSeriesCount(), pos, percentPlotrenderer);
				percentCollection.removeSeries(pos);
			}
			break;
		case ERROR:
			pos=errorCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(errorCollection.getSeriesCount(), pos, errorPlotRenderer);
				errorCollection.removeSeries(pos);
			}
			break;
		case DEVIATION:
			pos=deviationCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(deviationCollection.getSeriesCount(), pos, deviationRenderer);
				deviationCollection.removeSeries(pos);
			}
			break;
		case DEVIATION_PERCENT:
			pos=deviationPercentCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(deviationPercentCollection.getSeriesCount(), pos, deviationPercentPlotRenderer);
				deviationPercentCollection.removeSeries(pos);
			}
			break;
			
		case PROFIT:
			pos=profitCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(profitCollection.getSeriesCount(), pos, profitPlotRenderer);
				profitCollection.removeSeries(pos);
			}
			break;
			
		case RISK:
			pos=riskCollection.indexOf(serie.getName());
			if(pos>=0){
				updatePlotRendererBeforeSerieDeletion(riskCollection.getSeriesCount(), pos, riskPlotRenderer);
				riskCollection.removeSeries(pos);
			}
			break;

		default:
			break;
		}
	}
	
	private void updatePlotRendererBeforeSerieDeletion(int nbOfSeriesBefore, int deletePos,AbstractXYItemRenderer renderer){
		if(nbOfSeriesBefore==deletePos+1)return;
		if(nbOfSeriesBefore==1)return;
		
		for(int i=deletePos;i<nbOfSeriesBefore-1;i++){
			renderer.setSeriesPaint(i, renderer.getSeriesPaint(i+1));
			renderer.setSeriesStroke(i, renderer.getSeriesStroke(i+1));
			
			if(renderer instanceof XYLineAndShapeRenderer){
				XYLineAndShapeRenderer xYLineAndShapeRenderer=(XYLineAndShapeRenderer)renderer;
				xYLineAndShapeRenderer.setSeriesShapesVisible(i, xYLineAndShapeRenderer.getSeriesShapesVisible(i+1));
				xYLineAndShapeRenderer.setSeriesLinesVisible(i, xYLineAndShapeRenderer.getSeriesLinesVisible(i+1));
			}
			
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
		case PROFIT:
			pos=profitCollection.indexOf(serie.getName());
			if(pos>=0)
				profitPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B(),100));
			break;
		case RISK:
			pos=riskCollection.indexOf(serie.getName());
			if(pos>=0)
				riskPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor_R(), serie.getColor_G(), serie.getColor_B(),100));
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
	    
//		logger.info("Message recieved!");
		
	    if(!selectedGroup.containsIndicator(indicator))return;
	    
//	    logger.info("selectedGroup contains indicator!");
	    
	    if(!indicator.isActivated())return;
	    
	    if(indicator instanceof IbChartSignal){
	    	IbChartSignal signal=(IbChartSignal) indicator;
	    	signal.setBatch(false);
	    }
	    
//	    logger.info("indicator is activated!");
	    
	    clearAllSeriesOfIndicator(indicator);
	    addAllSeriesOfIndicatior(indicator);
	    
	    
	    isDirty();
	}
	
	@Inject
	public void chartIndicatorNewCurrentParameters( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_NEW_CURRENT_PARAMETER) IbChartIndicator indicator){
		
		if(!isCompositeAbleToReact())return;
	    
//		logger.info("Message recieved!");
		
	    if(!selectedGroup.containsIndicator(indicator))return;
	    
//	    logger.info("selectedGroup contains indicator!");
	    
	    if(!indicator.isActivated())return;
	    
//	    logger.info("indicator is activated!");
	    
	    indicator.getMainChartSerie().clearPoints();
	    
	    clearAllSeriesOfIndicator(indicator);
	    addAllSeriesOfIndicatior(indicator);
	    
//	    for(IbChartParameter param:indicator.getParameters()){
//	    	logger.info("Param value: "+param.getValue());
//	    }
	    
	    isDirty();
	    
	    refreshPlots();
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
			public void realTimeBarChanged(ExBar bar, WhatToShow whatToShow) {
				//Register the new Bar and save it back into the map
				if(!liveBarMap.containsKey(whatToShow))
					liveBarMap.put(whatToShow, new LinkedList<ExBar>());
				
				LinkedList<ExBar> bars=liveBarMap.get(whatToShow);
				if(bars.isEmpty() || bars.getLast().getTime()!=bar.getTime()){
					bars.add(bar);
				}
				else{
					bars.set(bars.size()-1, bar);
				}
				
				//Call the Real Time Bar Updater
				if(whatToShow==getBarContainer().getType()){
//					logger.info("New Bar: "+bar);
					LinkedList<ExBar> realTimeBars=BarUtils.convertTimeBars(bars,BarSize._1_secs,  barRecorder.getBarSize());
					if(realTimeBars==null ||realTimeBars.isEmpty())return;
					
					//Add the Bar to the bar recorder only if some historical data were already loaded
					if(!barRecorder.isEmpty())
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
		barRecorder.addListener(new BarRecorderListener() {
			
			private ExBar lastBar=null;
			private List<ExBar> addedBars=null;
			private List<ExBar> replacedBars=null;
			
			@Override
			public void newCompletedBar(ExBar bar) {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						refreshSeries();
					}
				});
			}
			
			@Override
			public void barReplaced(List<ExBar> bars) {
				
				
				//logger.info("Bar Replaced!");
				
				replacedBars=bars;
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						//candleStickSeries.clear();
						for(ExBar bar:replacedBars){
							long interval=BarUtils.getIntervallInMs(barRecorder.getBarSize());
//							Second sec=new Second(new Date(bar.getTimeInMs() - interval/2));
							Second sec=new Second(new Date(bar.getTimeInMs() + interval/2));
							int index=candleStickSeries.indexOf(sec);
							//logger.info("Index of: "+index);
							if(index>=0){
								candleStickSeries.remove(index);
								candleStickSeries.add(sec,bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
								candleStickSecondes.add(sec.getFirstMillisecond());
							}
							
						}
						candleStickSeries.fireSeriesChanged();
					}
				});
				
			}
			
			@Override
			public void barAdded(List<ExBar> bars) {
				addedBars=bars;
				
				//logger.info("Bar Added!");
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						//candleStickSeries.clear();
						/*
						Second LastSec=null;
						if(candleStickSeries.getItemCount()>0){
							OHLCItem item=(OHLCItem)candleStickSeries.getDataItem(candleStickSeries.getItemCount()-1);
							LastSec=new Second(new Date(item.getPeriod().getMiddleMillisecond()));
						}
						*/
						
						for(ExBar bar:addedBars){
							long interval=BarUtils.getIntervallInMs(barRecorder.getBarSize());
							Second sec=new Second(new Date(bar.getTimeInMs() + interval/2));
							if(candleStickSecondes.contains(sec.getFirstMillisecond()))continue;
							//if(LastSec!=null && LastSec.equals(sec))continue;
							candleStickSeries.add(sec,bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
							candleStickSecondes.add(sec.getFirstMillisecond());
							//candleStickSeries.getDataItem(candleStickSeries.getItemCount()-1)
						}
						
						double diff=barRecorder.getLastReceivedBar().getTimeInMs()-dateAxis.getRange().getUpperBound();
						//logger.info("Diff: "+diff);
						long interval=BarUtils.getIntervallInMs(barRecorder.getBarSize());
						if(diff>=0 && diff<=interval){
							dateAxis.setRange(dateAxis.getRange().getLowerBound(),
									barRecorder.getLastReceivedBar().getTimeInMs()+interval/2);
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
				
			}
			
			@Override
			public void allBarsCleared() {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						candleStickSeries.clear();
						candleStickSecondes.clear();
					}
				});
				
			}

			@Override
			public void lastBarUpdated(ExBar bar) {
				lastBar=bar;
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						if(!shell.isDisposed()){
							threshold.setValue(lastBar.getClose());
						}
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
		private int numberOfStarts=0;
		
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
		
		public void clear(){
			numberOfStarts=0;
		}
		
		@Override
		public IStatus run(IProgressMonitor monitor) {
			numberOfStarts++;
			
			if(barRecorder.getBartype() == BarType.TIME){
				return loadTimeBar();
			}
			else{
				System.out.println("Load range bars");
				return loadRangeBar();
			}
			
		
//			return Status.OK_STATUS;
		}
		
		private IStatus loadRangeBar(){
			if(barRecorder.isEmpty() || numberOfStarts==1){
				ExBar firstBar=hisDataProvider.getLastRangeBar(getBarContainer(), barRecorder.getRange());
				to=firstBar.getTime();
//				from=to-24*60*60*1000;
				
				from=to-24*60*60;
				
				Log.info("Loading Started!");
				
				List<ExBar> bars=hisDataProvider.getRangeBarsFromTo(getBarContainer(), barRecorder.getRange(), from, to);
//				List<ExBar> newBars=hisDataProvider.getAll(getBarContainer(), barRecorder.getRange());
				
//				List<ExBar> toAdd=new LinkedList<ExBar>();
//				if(newBars!=null && bars!=null && !bars.isEmpty() && !newBars.isEmpty()){
//					for(ExBar bar:newBars){
//						if(bars.get(bars.size()-1).getTime()<bar.getTime()){
//							toAdd.add(bar);
//						}
//					}
//					bars.addAll(toAdd);
//				}
				pastValueAvailable=true;
				
				Log.info("Loading finshed: nb. of bars: "+ bars.size());
				barRecorder.addBars(bars);
			}
			else if(loadPastValues && pastValueAvailable){
				to=barRecorder.getFirstReceivedBar().getTime();
				from=to-24*60*60*1000;
				//logger.info("Ask historical data: ");
				//hisDataProvider.init();
				List<ExBar> bars=hisDataProvider.getRangeBarsFromTo(getBarContainer(), barRecorder.getRange(), from, to);
				
				if(bars.size()==0){
					pastValueAvailable=false;
					//selectedGroup.addListener(indicatorGroupListener);
					return Status.OK_STATUS;
				}
				barRecorder.addBars(bars);
				loadPastValues=false;
			}
			
			
			
			return Status.OK_STATUS;
			
		}
		
		
		private IStatus loadTimeBar(){
			if(barRecorder.isEmpty() || numberOfStarts==1){
//				long intervall=BarUtils.getIntervallInSec(barRecorder.getBarSize());
				ExBar firstBar=hisDataProvider.getLastTimeBar(getBarContainer(), barRecorder.getBarSize());
				to=firstBar.getTime();
				from=to-24*60*60;
				
//				from=firstBar.getTime();
//				to=from-24*60*60;
				
//				logger.info("From: "+BarUtils.format(from*1000));
//				logger.info("To: "+BarUtils.format(to*1000));
				
//				to=new Date().getTime()/1000;
//				from=to-loadingSize*intervall;
				
				List<ExBar> bars=hisDataProvider.getTimeBarsFromTo(getBarContainer(), barRecorder.getBarSize(), from, to);
				List<ExBar> newBars=hisDataProvider.getAllRealTimeBars(getBarContainer(), barRecorder.getBarSize());
//				logger.info("Number of bars: "+bars.size());
				//logger.info("Number of new bars: "+newBars.size());
					
				List<ExBar> toAdd=new LinkedList<ExBar>();
				if(newBars!=null && bars!=null && !bars.isEmpty() && !newBars.isEmpty()){
					for(ExBar bar:newBars){
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
				long intervall=BarUtils.getIntervallInSec(barRecorder.getBarSize());
				to=barRecorder.getFirstReceivedBar().getTime();
				from=to-loadingSize*intervall;
				//logger.info("Ask historical data: ");
				//hisDataProvider.init();
				List<ExBar> bars=hisDataProvider.getTimeBarsFromTo(getBarContainer(), barRecorder.getBarSize(), from, to);
				
				if(bars.size()==0){
					pastValueAvailable=false;
					//selectedGroup.addListener(indicatorGroupListener);
					return Status.OK_STATUS;
				}
				barRecorder.addBars(bars);
				loadPastValues=false;
			}
			
			return Status.OK_STATUS;
		}
		
		
	}
	

}