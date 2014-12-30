 
package com.munch.exchange.parts.neuralnetwork.results;

import java.awt.BasicStroke;
import java.awt.Color;

import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neuroph.core.data.DataSet;

import com.munch.exchange.ExchangeChartComposite;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.parts.composite.RateChart;
import com.munch.exchange.services.INeuralNetworkProvider;

import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Rectangle;

public class NeuralNetworkResultsChartPart {
	
	
	private static Logger logger = Logger.getLogger(NeuralNetworkResultsChartPart.class);
	
	private JFreeChart chart;
	private Composite compositeChart;
	
	private XYLineAndShapeRenderer responseRenderer =new XYLineAndShapeRenderer(true, false);
	private XYLineAndShapeRenderer profitRenderer =new XYLineAndShapeRenderer(true, false);
	
	private XYSeriesCollection responseCollection=new XYSeriesCollection();
	private XYSeriesCollection profitCollection=new XYSeriesCollection();

	
	private NetworkArchitecture archi;
	
	private int[] period=new int[2];
	private double[][] outputs;
	
	@Inject
	INeuralNetworkProvider nn_provider;
	
	@Inject
	private Shell shell;
	
	@Inject
	public NeuralNetworkResultsChartPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.verticalSpacing = 0;
		gl_parent.horizontalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);
		//==================================================
		//==                 CHART                        ==    
		//==================================================
		chart = createChart();
		compositeChart = new ResultsChartComposite(parent, SWT.NONE,chart);
		compositeChart.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
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
    	NumberAxis domainAxis =	createAxis("Step");
    	NumberAxis profitAxis =	createAxis("Profit");
    	NumberAxis valueAxis  =	createAxis("Response");
    	
    	
    	updateDataSet();
    	
    	//====================
    	//===  Main Plot   ===
    	//====================
    	XYPlot plot = createPlot(responseCollection,responseRenderer,domainAxis,valueAxis);
    	
    	
    	plot.setDataset(1,profitCollection);
        plot.setRenderer(1, profitRenderer);
        plot.setRangeAxis(1, profitAxis);
        plot.mapDatasetToRangeAxis(1, 1);
    	
    	
    	 //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart("",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.white);
      
        return chart;
    	
    }
    
   
    
    
    private NumberAxis createAxis(String name){
      	 //Axis
          NumberAxis domainAxis = new NumberAxis(name);
          domainAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
          domainAxis.setAutoRange(true);
          domainAxis.setLowerMargin(0.01);
          domainAxis.setUpperMargin(0.01);
          domainAxis.setVisible(true);
          domainAxis.setAutoRangeIncludesZero(false);
          return domainAxis;
      }
	
    private XYPlot createPlot(XYSeriesCollection xySeriesCollection,XYLineAndShapeRenderer lineAndShapeRenderer, NumberAxis domainAxis, NumberAxis valueAxis){
    	//Plot
    	XYPlot plot = new XYPlot(xySeriesCollection, domainAxis, valueAxis, lineAndShapeRenderer);
    	//plot.setDomainAxis(valueAxis);
    	plot.setRenderer(lineAndShapeRenderer);
    	plot.setDataset(xySeriesCollection);
    	
    	plot.setDomainPannable(true);
        plot.setRangePannable(true);
        
        plot.setBackgroundPaint(Color.lightGray);
        //plot1.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
         
        //bubbleRenderer.setSeriesPaint(0, Color.blue);
    	
    	return plot;
    }
    
    private void updateDataSet(){
    	if(archi==null)return;
    	if(archi.getParent()==null)return;
    	if(archi.getParent().getParent()==null)return;
    	
    	Configuration config=archi.getParent();
    	Stock stock=archi.getParent().getParent();
    	
    	ValuePointList l=nn_provider.calculateMaxProfitOutputList(stock,RateChart.PENALTY);
    	config.setOutputPointList(l);
		
		if(!archi.getParent().areAllTimeSeriesAvailable()){
			nn_provider.createAllInputPoints(stock);
		}
		
		//double[] input=config.getLastInput();
		DataSet dataset=config.getTrainingSet();
		
    
		outputs=archi.calculateNetworkOutputsAndProfitFromBestResult(dataset, RateChart.PENALTY);
		if(outputs==null)return;
		
		updateSeries();
    }
    
    private void updateSeries(){
    	
    	responseCollection.removeAllSeries();
    	profitCollection.removeAllSeries();
    	
    	double[] output 		= outputs[0];
		double[] desiredOutput 	= outputs[1];
		double[] outputdiff		= outputs[2];
		double[] startVal		= outputs[3];
		double[] endVal			= outputs[4];
		double[] profit			= outputs[5];
		
		if(period[0]==period[1]){
			resetPeriode();
		}
		
		addSeriesAsLine(responseRenderer, responseCollection,createResponseSerie("Output",output) , Color.blue);
		addSeriesAsLine(responseRenderer, responseCollection,createResponseSerie("Desired Ouput",desiredOutput) , Color.black);
		addSeriesAsLine(profitRenderer, profitCollection,createSerie("Profit",profit) , Color.gray);
    }
    
    
    private void resetPeriode(){
    	if(outputs==null)return;
    	period[1]=outputs[0].length;
		period[0]=Math.max(0, outputs[0].length-100);
    }
    
    private void scalePeriode(int fac){
    	if(outputs==null)return;
    	period[1]=Math.min(outputs[0].length, period[1]+fac);
    	period[0]=Math.max(0, period[0]-fac);
    	
    	updateSeries();
    }
    
    
    private void setPeriod(int start, int end){
    	if(outputs==null)return;
    	period[1]=Math.min(outputs[0].length,end);
    	period[0]=Math.max(0,start);
    	updateSeries();
    }
    
    
    private XYSeries createSerie(String name,double[] x){
    	XYSeries r_series =new XYSeries(name);
		//int pos=1;
		for(int i=0;i<x.length;i++){
			if(i>=period[0] && i<period[1]){
				r_series.add(i, x[i]);
				//pos++;
			}
		}
		
		return 	r_series;
    }
    
    private XYSeries createResponseSerie(String name,double[] x){
    	XYSeries r_series =new XYSeries(name);
		//int pos=1;
		for(int i=0;i<x.length;i++){
			if(i>=period[0] && i<period[1]){
				r_series.add(i-0.5, x[i]);
				r_series.add(i+0.5, x[i]);
				//pos++;
			}
		}
		
		return 	r_series;
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
    
    
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	//################################
  	//##       Chart Composite      ##
  	//################################
	class ResultsChartComposite extends ExchangeChartComposite{
		
		int x=-1;
		double trans=0;
		int startP=0;
		int endP=0;

		public ResultsChartComposite(Composite comp, int style, JFreeChart chart) {
			super(comp, style, chart);
		}

		@Override
		public void mouseDown(MouseEvent event) {
			// TODO Auto-generated method stub
			logger.info("mouseDown: "+event);
			if(event.button==1){
				x=event.x;
				trans=0;
				startP=period[0];
				endP=period[1];
			}
			else{
				resetPeriode();
				updateSeries();
			}
		}

		@Override
		public void mouseUp(MouseEvent event) {
			// TODO Auto-generated method stub
			//super.mouseUp(event);
			if(x>0){
				Rectangle rec= this.getScreenDataArea();
				
				double fac=((double)(x-event.x))/((double) rec.width);
				int diff=endP-startP;
				trans=fac*diff*2;
				
				setPeriod(startP+(int)(trans), endP+(int)(trans));
				
				x=-1;
			}
		}
		
		
		

		@Override
		public void mouseMove(MouseEvent event) {
			if(x>0){
				Rectangle rec= this.getScreenDataArea();
				double fac=((double)(x-event.x))/((double) rec.width);
				int diff=endP-startP;
				trans=fac*diff*2;
				
				logger.info("Rec: "+rec.width+", fac: "+fac+", trans: "+trans);
				
				setPeriod(startP+(int)(trans), endP+(int)(trans));
				//x=event.x;
			}
		}

		@Override
		public void mouseScrolled(MouseEvent e) {
			// TODO Auto-generated method stub
			//logger.info("mouseScrolled: "+e);
			
			scalePeriode(e.count*3);
			
			super.mouseScrolled(e);
		}
		
		
		
	}
	
	//################################
  	//##       Event Reaction       ##
  	//################################
    private boolean isCompositeAbleToReact(){
		if (shell.isDisposed())
			return false;
		
		if(archi==null)return false;
		if(archi.getParent()==null)return false;
		
		Stock stock=archi.getParent().getParent();
		if (stock == null )
			return false;
		
		return true;
	}
    
    
    @Inject
	public void analyseSelection( @Optional  @Named(IServiceConstants.ACTIVE_SELECTION) 
	NetworkArchitecture selArchi){
    	archi=selArchi;
    	if(isCompositeAbleToReact())
    		updateDataSet();
    	
	}
	
	
}