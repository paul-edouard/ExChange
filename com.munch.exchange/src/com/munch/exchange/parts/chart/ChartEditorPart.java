 
package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.ExchangeChartComposite;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.historical.HistoricalPoint.Type;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbBarRecorder;
import com.munch.exchange.model.core.ib.bar.IbBarRecorderListener;
import com.munch.exchange.model.core.ib.bar.IbHourBar;
import com.munch.exchange.model.core.ib.bar.IbMinuteBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.parts.RateEditorPart;
import com.munch.exchange.services.IBundleResourceLoader;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarListener;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarProvider;
import com.munch.exchange.services.ejb.providers.IBHistoricalDataProvider;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;


public class ChartEditorPart{
	
	private static Logger logger = Logger.getLogger(ChartEditorPart.class);
	
	public static final String CHART_EDITOR_ID="com.munch.exchange.partdescriptor.charteditor";
	
	public static final String CANDLESTICK="Candlestick";
	public static final String LIVE_CANDLESTICK="Live Candlestick";
	
	@Inject
	IbContract contract;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	@Inject
	IIBRealTimeBarProvider realTimeBarProvider;
	
	IIBRealTimeBarListener realTimeBarListener;
	
	
	//The renderers
	private XYLineAndShapeRenderer mainPlotRenderer=new XYLineAndShapeRenderer(true, false);
	private CandlestickRenderer candlestickRenderer=new CandlestickRenderer(0.0);
	
	//The Series Collections
	private XYSeriesCollection mainCollection=new XYSeriesCollection();
	private OHLCSeriesCollection oHLCSeriesCollection=new OHLCSeriesCollection();
	
	
	private JFreeChart chart;
	private Composite compositeChart;
	private DateAxis dateAxis;
	private ValueMarker threshold;
	private OHLCSeries candleStickSeries= new OHLCSeries(CANDLESTICK);
	
	private List<IbBarContainer> barContainers;
	private IbBarRecorder barRecorder=new IbBarRecorder();
	
	private HashMap<WhatToShow, LinkedList<IbBar>> liveBarMap=new HashMap<WhatToShow, LinkedList<IbBar>>();
	private DataUpdater dataUpdater=new DataUpdater();
	
	
	private Combo comboBarSize;
	private Combo comboWhatToShow;
	
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
		comboWhatToShow.setText(comboWhatToShow.getItem(0));
		barRecorder.setWhatToShow(IbBar.getWhatToShowFromString(comboWhatToShow.getText()));
		//whatToShow=IbBar.getWhatToShowFromString(comboWhatToShow.getText());
		comboWhatToShow.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				logger.info(comboWhatToShow.getText());
				WhatToShow newWhatToShow=IbBar.getWhatToShowFromString(comboWhatToShow.getText());
				if(newWhatToShow==barRecorder.getWhatToShow())return;
				
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
		dataUpdater.schedule();
		
	}
	
	
	private void initBarContainers(){
		barContainers=hisDataProvider.getAllExContractBars(contract);
		if(barContainers==null || barContainers.size()==0)return;
		
		for(IbBarContainer container:barContainers){
			IbChartIndicatorGroup root=container.getIndicatorGroup();
			logger.info("IbChartIndicatorGroup: "+root.getName());
		}
		
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
	
	private JFreeChart createChart() {
		
		
		 
		//====================
	    //===  Main Axis   ===
	    //====================
		ValueAxis domainAxis =createDomainAxis();
	    	
	    //====================
	    //===  Main Plot   ===
	    //====================
	    XYPlot mainPlot = createMainPlot(domainAxis);
	    
	    mainPlot.addRangeMarker(threshold);
	    
	    //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart(contract.getLongName(),
                JFreeChart.DEFAULT_TITLE_FONT, mainPlot, false);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setVisible(false);
      
        return chart;
	  
	 }
	
	private ValueAxis createDomainAxis(){
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
    private XYPlot createMainPlot( ValueAxis domainAxis){
    	
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
        XYPlot plot1 = new XYPlot(mainCollection, domainAxis, rangeAxis1, mainPlotRenderer);
        plot1.setBackgroundPaint(Color.lightGray);
        //plot1.setBackgroundPaint(Color.BLACK);
        plot1.setDomainGridlinePaint(Color.white);
        plot1.setRangeGridlinePaint(Color.white);
        
        
        int i=1;
		//Add the Candle Stick Graph
		addCandleStickGraph(plot1, rangeAxis1, i);i++;
        
		
		createPosOHLCSeries();
        
        return plot1;
    	
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
	
	
	@PreDestroy
	public void preDestroy() {
		realTimeBarProvider.removeRealTimeBarListener(realTimeBarListener);
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
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
				// TODO Auto-generated method stub
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
						for(IbBar bar:addedBars){
							Second sec=new Second(new Date(bar.getTimeInMs() - bar.getIntervallInMs()/2));
							candleStickSeries.add(sec,bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
						}
						
						double diff=barRecorder.getLastReceivedBar().getTimeInMs()-dateAxis.getRange().getUpperBound();
						//logger.info("Diff: "+diff);
						if(diff>=0 && diff<=barRecorder.getLastReceivedBar().getIntervallInMs()){
							dateAxis.setRange(dateAxis.getRange().getLowerBound(),
									barRecorder.getLastReceivedBar().getTimeInMs()+barRecorder.getLastReceivedBar().getIntervallInMs()/2);
						}
						
						candleStickSeries.fireSeriesChanged();
						threshold.setValue(lastBar.getClose());
						
						
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
				List<IbBar> bars=hisDataProvider.getBarsFromTo(getBarContainer(), barRecorder.getBarSize(), from, to);
				
				if(bars.size()==0){
					pastValueAvailable=false;
					return Status.OK_STATUS;
				}
				barRecorder.addBars(bars);
				loadPastValues=false;
			}
			
			return Status.OK_STATUS;
		}
		
	}
	
	
	
}