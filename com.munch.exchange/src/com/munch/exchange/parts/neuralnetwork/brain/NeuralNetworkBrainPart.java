 
package com.munch.exchange.parts.neuralnetwork.brain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;


public class NeuralNetworkBrainPart {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkBrainPart.class);
	
	private JFreeChart chart;
	private Composite compositeChart;
	private XYBubbleRenderer bubbleRenderer=new XYBubbleRenderer(1);
	private XYLineAndShapeRenderer lineAndShapeRenderer =new XYLineAndShapeRenderer(true, false);
	
	private DefaultXYZDataset xyzDataset=new DefaultXYZDataset();
	private XYSeriesCollection xySeriesCollection=new XYSeriesCollection();
	
	@SuppressWarnings("rawtypes")
	private NeuralNetwork neuralNetwork;
	private NetworkArchitecture archi;
	
	private HashMap<Neuron, double[]> neuronXYZPosMap=new HashMap<Neuron, double[]>();
	
	
	
	@Inject
	private Shell shell;
	
	private double maxAbsWeight=0;
	
	
	@Inject
	public NeuralNetworkBrainPart() {
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
		compositeChart = new ChartComposite(parent, SWT.NONE,chart);
		compositeChart.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
	}
	
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
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
    	NumberAxis domainAxis =createAxis("");
    	NumberAxis valueAxis =createAxis("");
    	
    	
    	updateYXZDataSet();
    	
    	//====================
    	//===  Main Plot   ===
    	//====================
    	XYPlot plot = createPlot(domainAxis,valueAxis);
    	
    	 //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart("",
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(Color.white);
      
        return chart;
    	
    }
    
    private NumberAxis createAxis(String name){
   	 //Axis
       NumberAxis domainAxis = new NumberAxis(name);
       domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
       domainAxis.setAutoRange(true);
       domainAxis.setLowerMargin(0.05);
       domainAxis.setUpperMargin(0.05);
       domainAxis.setVisible(false);
       return domainAxis;
   }
    
    private XYPlot createPlot( NumberAxis domainAxis, NumberAxis valueAxis){
    	//Plot
    	XYPlot plot = new XYPlot(xyzDataset, domainAxis, valueAxis, bubbleRenderer);
    	//plot.setDomainAxis(valueAxis);
    	plot.setRenderer(bubbleRenderer);
    	plot.setDataset(xyzDataset);
    	
    	plot.setRenderer(1, lineAndShapeRenderer);
    	plot.setDataset(1, xySeriesCollection);
    	
    	plot.setDomainPannable(true);
        plot.setRangePannable(true);
        
        plot.setBackgroundPaint(Color.white);
        //plot1.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
         
        bubbleRenderer.setSeriesPaint(0, Color.darkGray);
    	
    	return plot;
    }
    
    
   
    
    private void searchNetwork(){
    	
    	if(archi==null)return;
		neuralNetwork=archi.getNetwork();
		if(archi.getBestResultEntity()!=null){
			neuralNetwork.setWeights(archi.getBestResultEntity().getDoubleArray());
		}
    }
    
    public void updateYXZDataSet(){
    	
    	clearDataSet();
    	searchNetwork();
    	
    	if(neuralNetwork==null){
    		double[] x = {2.1, 2.3, 2.3, 2.2, 2.2, 1.8, 1.8, 1.9, 2.3, 3.8};
            double[] y = {14.1, 11.1, 10.0, 8.8, 8.7, 8.4, 5.4, 4.1, 4.1, 25};
            double[] z = {2.4, 2.7, 2.7, 2.2, 2.2, 2.2, 2.1, 2.2, 1.6, 4};
            double[][] series = new double[][] { x, y, z };
            this.xyzDataset.addSeries("Dummy", series);
    		return;
    	}
    	
    	maxAbsWeight=0;
    	//Calculate the max Abs Weigth
    	for(double w:neuralNetwork.getWeights()){
    		double abs=Math.abs(w);
    		if(abs>maxAbsWeight)
    			maxAbsWeight=abs;
    	}
    	
    	
    	LinkedList<Double> x=new LinkedList<Double>();
    	LinkedList<Double> y=new LinkedList<Double>();
    	LinkedList<Double> z=new LinkedList<Double>();
    	
    	
    	Layer[] layers=neuralNetwork.getLayers();
    	int numberOfConnections=0;
    	
    	for(int i=0;i<layers.length;i++){
    		Layer layer=layers[i];
    		Neuron[] neurons=layer.getNeurons();
    		
    		for(int j=0;j<neurons.length;j++){
    			Neuron neuron=neurons[j];
    			
    			y.add((i+0.2) * -5.0);
    			x.add(j * 20.0 - 10.0 * neurons.length);
    			z.add(neuron.getOutput() * 5+6);
    			
    			neuronXYZPosMap.put(neuron, new double[]{x.getLast(),y.getLast(),z.getLast()});
    			
    			Connection[] inputConnections=neuron.getInputConnections();
    			//logger.info("Layer: "+i+", Number of input connections: "+inputConnections.length);
    			
    			for(int k=0;k<inputConnections.length;k++){
    				Connection connection=inputConnections[k];
    				if(neuronXYZPosMap.containsKey(connection.getFromNeuron())){
    					numberOfConnections++;
    					XYSeries series = new XYSeries("Connection: "+numberOfConnections);
    					double[] pos=neuronXYZPosMap.get(connection.getFromNeuron());
    		            series.add(pos[0], pos[1]);
    		            series.add(x.getLast(),y.getLast());
    		            addConnectionSeries(connection,series);
    				}
    			}
    			
    			
    			//neuron.
    			
    		}
    		
    	}
    	
    	double[][] series = new double[3][x.size()];
    	for(int i=0;i<x.size();i++){
    		series[0][i]=x.get(i);
    		series[1][i]=y.get(i);
    		series[2][i]=z.get(i);    		
    	}
    	
    	this.xyzDataset.addSeries("Neurons", series);
    	
    }
    
    private void addConnectionSeries(Connection connection, XYSeries series){
    	
    	Color col=new Color(255,255,255);
    	int val=(int)Math.min(255, Math.abs((int)255*connection.getWeight().getValue()));
    	if(connection.getWeight().getValue()>0){
    		col=new Color(val,0,255-val);
    	}
    	else{
    		col=new Color(255-val,0,val);
    	}
    	this.xySeriesCollection.addSeries(series);
    	int pos=xySeriesCollection.indexOf(series.getKey());
		if(pos>=0){
			lineAndShapeRenderer.setSeriesShapesVisible(pos, false);
			lineAndShapeRenderer.setSeriesLinesVisible(pos, true);
			float width=(float ) (10/maxAbsWeight*Math.abs(connection.getWeight().getValue()));
			lineAndShapeRenderer.setSeriesStroke(pos,new BasicStroke(width));
			lineAndShapeRenderer.setSeriesPaint(pos, col);
		}
    	
    }
    
    private void clearDataSet(){
    	if(this.xyzDataset!=null){
    		while(xyzDataset.getSeriesCount()>0){
    			xyzDataset.removeSeries(xyzDataset.getSeriesKey(0));
    		}
    	}
    	
    	this.xySeriesCollection.removeAllSeries();
    	
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
    		updateYXZDataSet();
    	
	}
	
	
	
}