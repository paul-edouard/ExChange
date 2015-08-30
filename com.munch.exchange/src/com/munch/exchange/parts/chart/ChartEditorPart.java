 
package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

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
import com.munch.exchange.model.core.ib.bar.IbHourBar;
import com.munch.exchange.model.core.ib.bar.IbMinuteBar;
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
	
	@Inject
	IbContract contract;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	@Inject
	IIBRealTimeBarProvider realTimeBarProvider;
	
	IIBRealTimeBarListener realTimeBarListener;
	
	HashMap<WhatToShow, LinkedList<IbBar>> barMap=new HashMap<WhatToShow, LinkedList<IbBar>>();
	
	
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
	
	
	private IbBarContainer barContainer;
	private List<IbBar> bars=null;
	
	private long[] period=new long[2];
	private long[] minMaxperiod=new long[2];
	private Combo comboBarSize;
	
	
	
	@Inject
	public ChartEditorPart() {
		//TODO Your code here
	}
	
	
	private void addRealTimeBarListener(){
		
		realTimeBarListener=new IIBRealTimeBarListener() {
			
			@Override
			public void realTimeBarChanged(IbBar bar) {
				//logger.info("New Bar: "+bar);
				if(!barMap.containsKey(bar.getType()))
					barMap.put(bar.getType(), new LinkedList<IbBar>());
				
				LinkedList<IbBar> bars=barMap.get(bar.getType());
				if(bars.isEmpty() || bars.getLast().getTime()!=bar.getTime()){
					bars.add(bar);
				}
				else{
					bars.set(bars.size()-1, bar);
				}
				
				if(bar.getType()==barContainer.getType())
					Display.getDefault().asyncExec(new realTimeBarUpdater(bars)); 
				
			
			}
			
			@Override
			public int getContractId() {
				return contract.getId();
			}
		};
		
		realTimeBarProvider.addIbRealTimeBarListener(realTimeBarListener);
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		addRealTimeBarListener();
		
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);
		
		setBarContainer();
		setMinMaxPeriod();
		threshold=createMarker();
		updateSeries();
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
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
		
		
		
		comboBarSize = new Combo(composite, SWT.NONE);
		comboBarSize.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
			}
		});
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
	}
	
	
	private void setBarContainer(){
		List<IbBarContainer> containers=hisDataProvider.getAllExContractBars(contract);
		if(containers==null || containers.size()==0)return;
		
		barContainer=containers.get(0);
	}
	
	private void setMinMaxPeriod(){
		minMaxperiod[0]=hisDataProvider.getFirstBarTime(barContainer, IbMinuteBar.class);
		minMaxperiod[1]=hisDataProvider.getLastBarTime(barContainer, IbMinuteBar.class);
		
		period[1]=minMaxperiod[1];
		period[0]=Math.max(minMaxperiod[0], minMaxperiod[1]-60*60*24);
	}
	
	private void scalePeriode(int fac,double posFac){
    
    	period[1]=Math.min(minMaxperiod[1], period[1]+ (long)( fac*(1-posFac)));
    	period[0]=Math.max(minMaxperiod[0], period[0]- (long)( fac*posFac));
    	
    	dateAxis.setRange(period[0]*1000, period[1]*1000);
    	
    	
    	updateSeries();
    }
	
	private void updateSeries(){
		if(bars==null){
			bars=hisDataProvider.getAllBars(barContainer, BarSize._1_min);
			logger.info("Number of bars: "+bars.size());
			HashMap<Long, IbBar> map=new HashMap<>();
			List<IbBar> toDel=new LinkedList<IbBar>();
			for(IbBar bar:bars){
				if(!map.containsKey(bar.getTime())){
					map.put(bar.getTime(), bar);
				}
				else{
					logger.info("Error: bar are double!");
					logger.info("Error: bar1: "+bar.toString());
					logger.info("Error: bar2: "+map.get(bar.getTime()).toString());
					toDel.add(bar);
				}
			}
			
			for(IbBar bar:toDel){
				hisDataProvider.removeBar(bar.getId());
				bars.remove(bar);
			}
		}
		
		
		if(bars==null)return ;
		candleStickSeries.clear();
		for(IbBar bar:bars){
			//logger.info(bar);
			if(bar.getTime()>=period[0] && bar.getTime()<=period[1]){
			candleStickSeries.add(new Second(new Date(bar.getTimeInMs())),bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
			
			}
		}
	}
	
	private void setPeriod(long start, long end){
		
		if(end>minMaxperiod[1])return;
		if(start<minMaxperiod[0])return;
		
		period[1]=Math.min(minMaxperiod[1],end);
    	period[0]=Math.max(minMaxperiod[0],start);
    	
    	dateAxis.setRange(period[0]*1000, period[1]*1000);
    	
    	updateSeries();
    	
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
        
        return dateAxis;
    }
	
	private ValueMarker createMarker(){
	// add a labelled marker for the safety threshold...
	ValueMarker threshold = new ValueMarker(690);
    threshold.setLabelOffsetType(LengthAdjustmentType.EXPAND);
    threshold.setPaint(Color.red);
    threshold.setStroke(new BasicStroke(1.0f));
    threshold.setLabel("Price");
    //threshold.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
    threshold.setLabelPaint(Color.red);
    threshold.setLabelAnchor(RectangleAnchor.TOP_LEFT);
    threshold.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
    return threshold;
	}
    //plot.addRangeMarker(threshold);
	
	
		
	
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
        rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
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
    	
    	candlestickRenderer.setAutoWidthFactor(0.7);
		candlestickRenderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
		//candlestickRenderer.setDownPaint(Color.);
    }
	
    
    private void createPosOHLCSeries(){
    	
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
		long startP=0;
		long endP=0;
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
				startP=period[0];
				endP=period[1];
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
			// TODO Auto-generated method stub
			//logger.info("mouseScrolled: "+e);
			//w=this.getChartRenderingInfo().getPlotInfo().getDataArea().getWidth();
			
			w=this.getChartRenderingInfo().getPlotInfo().getPlotArea().getWidth();
			
			//w=this.getChartRenderingInfo().getChartArea().getWidth();
			
			
			
			
			double fac=((double)event.x)/((double) w);
			logger.info("fac: "+fac);
		
			scalePeriode(event.count*( (int) (period[1]-period[0])/10),fac);
			
			super.mouseScrolled(event);
		}
		
	}
	
	
	
	//######################################
  	//##       Real Time Bar Updater      ##
  	//######################################
	
	private class realTimeBarUpdater implements Runnable{
		
		LinkedList<IbBar> bars;

		public realTimeBarUpdater(LinkedList<IbBar> bars) {
			super();
			this.bars=bars;
		}



		@Override
		public void run() {
			
			IbBar bar=bars.getLast();
			
			//if(period[1]==minMaxperiod[1]){
				period[1]=bar.getTime();
				dateAxis.setRange(period[0]*1000, period[1]*1000+30*1000);
			//}
			minMaxperiod[1]=bar.getTime();
			Second sec=new Second(new Date(bar.getTimeInMs()));
			
			
			int index=candleStickSeries.indexOf(sec);
			if(index>0){
				candleStickSeries.remove(index);
			}
			
			candleStickSeries.add(sec,bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
			candleStickSeries.fireSeriesChanged();
			
			threshold.setValue(bar.getClose());
			
		}
		
	}
	
	
}