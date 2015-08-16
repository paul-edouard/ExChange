 
package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;

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
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.providers.IBHistoricalDataProvider;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;


public class ChartEditorPart {
	
	private static Logger logger = Logger.getLogger(ChartEditorPart.class);
	
	public static final String CHART_EDITOR_ID="com.munch.exchange.partdescriptor.charteditor";
	
	public static final String CANDLESTICK="Candlestick";
	
	@Inject
	IbContract contract;
	
	@Inject
	IIBHistoricalDataProvider provider;
	
	//The renderers
	private XYLineAndShapeRenderer mainPlotRenderer=new XYLineAndShapeRenderer(true, false);
	private CandlestickRenderer candlestickRenderer=new CandlestickRenderer(0.0);
	
	//The Series Collections
	private XYSeriesCollection mainCollection=new XYSeriesCollection();
	private OHLCSeriesCollection oHLCSeriesCollection=new OHLCSeriesCollection();
	
	
	private JFreeChart chart;
	private Composite compositeChart;
	
	
	@Inject
	public ChartEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);
		
		
		
		//==================================================
		//==                 CHART                        ==    
		//==================================================
		chart = createChart();
		compositeChart = new ChartComposite(parent, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		
		//TODO Your code here
		/*
		logger.info("Contract: "+contract.getLongName());
		List<IbBarContainer> containers=provider.getAllExContractBars(contract);
		for(IbBarContainer container:containers){
			//List<IbBar> bars=provider.getAllBars(container, IbMinuteBar.class);
			//for(IbBar bar:bars)
			//	logger.info("bar: "+bar.toString());
			IbBar firstBar=provider.getFirstBar(container, IbMinuteBar.class);
			IbBar lastBar=provider.getLastBar(container, IbMinuteBar.class);
			logger.info("Fisrt bar: "+firstBar.toString());
			logger.info("Last bar: "+lastBar.toString());
		}
		*/
		
	}
	
	 private JFreeChart createChart() {
		//====================
	    //===  Main Axis   ===
	    //====================
	    NumberAxis domainAxis =createDomainAxis();
	    	
	    //====================
	    //===  Main Plot   ===
	    //====================
	    XYPlot mainPlot = createMainPlot(domainAxis);
	    
	    //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart(contract.getLongName(),
                JFreeChart.DEFAULT_TITLE_FONT, mainPlot, false);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setVisible(false);
      
        return chart;
	  
	 }
	
	private NumberAxis createDomainAxis(){
    	 //Axis
        NumberAxis domainAxis = new NumberAxis("Day");
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        //domainAxis.setAutoRange(true);
        domainAxis.setLowerMargin(0.1);
        domainAxis.setUpperMargin(0.1);
        
        domainAxis.setAutoRangeIncludesZero(false);
        
        return domainAxis;
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
    	OHLCSeries series = new OHLCSeries(CANDLESTICK);
    	List<IbBarContainer> containers=provider.getAllExContractBars(contract);
    	List<IbBar> bars=null;
		for(IbBarContainer container:containers){
			bars=provider.getAllBars(container, IbMinuteBar.class);
			break;
		}
		if(bars==null)return ;
		
		for(IbBar bar:bars){
			logger.info(bar);
			// if(point.get(Type.CLOSE)>point.get(Type.OPEN)){
			series.add(new Second(new Date(bar.getTimeInMs())),bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose());
		}
		
		
		oHLCSeriesCollection.addSeries(series);
		int fiel_pos=oHLCSeriesCollection.indexOf(CANDLESTICK);
		if(fiel_pos>=0){
			candlestickRenderer.setSeriesPaint(fiel_pos, Color.black);
			candlestickRenderer.setSeriesStroke(fiel_pos,new BasicStroke(1.5f));
		}
    	
    	
    }
	
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}
}