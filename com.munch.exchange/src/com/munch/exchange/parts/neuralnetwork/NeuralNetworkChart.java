package com.munch.exchange.parts.neuralnetwork;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IExchangeRateProvider;

public class NeuralNetworkChart extends Composite {
	
	
	private JFreeChart chart;
	private Composite compositeChart;
	private XYBubbleRenderer bubbleRenderer=new XYBubbleRenderer(1);
	private DefaultXYZDataset xyzDataset=new DefaultXYZDataset();
	
	private MultiLayerPerceptron neuralNetwork;
	private Stock stock;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked")
	@Inject
	public NeuralNetworkChart(Composite parent,ExchangeRate rate
			,/*MultiLayerPerceptron multiLayerPerceptron, */IEclipseContext p_context) {
		super(parent, SWT.NONE);
		
		this.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));
		
		this.stock=(Stock) rate;
		//this.multiLayerPerceptron=multiLayerPerceptron;
		
		//==================================================
		//==                 CHART                        ==    
		//==================================================
		chart = createChart();
		compositeChart = new ChartComposite(this, SWT.NONE,chart);
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
    	NumberAxis domainAxis =createAxis("X");
    	NumberAxis valueAxis =createAxis("Y");
    	
    	
    	updateYXZDataSet();
    	
    	//====================
    	//===  Main Plot   ===
    	//====================
    	XYPlot plot = createPlot(domainAxis,valueAxis);
    	
    	 //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart("Network",
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(Color.white);
        
      
        return chart;
    	
    }
    
    
    private NumberAxis createAxis(String name){
   	 //Axis
       NumberAxis domainAxis = new NumberAxis(name);
       domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
       domainAxis.setAutoRange(true);
       domainAxis.setLowerMargin(0.15);
       domainAxis.setUpperMargin(0.15);
       return domainAxis;
   }
    
    private XYPlot createPlot( NumberAxis domainAxis, NumberAxis valueAxis){
    	//Plot
    	XYPlot plot = new XYPlot(xyzDataset, domainAxis, valueAxis, bubbleRenderer);
    	//plot.setDomainAxis(valueAxis);
    	plot.setRenderer(bubbleRenderer);
    	plot.setDataset(xyzDataset);
    	
    	plot.setDomainPannable(true);
        plot.setRangePannable(true);
        
        plot.setBackgroundPaint(Color.lightGray);
        //plot1.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
         
        bubbleRenderer.setSeriesPaint(0, Color.blue);
    	
    	return plot;
    }
    
    private void updateYXZDataSet(){
    	
    	clearDataSet();
    	
    	if(neuralNetwork==null){
    		double[] x = {2.1, 2.3, 2.3, 2.2, 2.2, 1.8, 1.8, 1.9, 2.3, 3.8};
            double[] y = {14.1, 11.1, 10.0, 8.8, 8.7, 8.4, 5.4, 4.1, 4.1, 25};
            double[] z = {2.4, 2.7, 2.7, 2.2, 2.2, 2.2, 2.1, 2.2, 1.6, 4};
            double[][] series = new double[][] { x, y, z };
            this.xyzDataset.addSeries("Dummy", series);
    		return;
    	}
    	
    	LinkedList<Double> x=new LinkedList<Double>();
    	LinkedList<Double> y=new LinkedList<Double>();
    	LinkedList<Double> z=new LinkedList<Double>();
    	
    	
    	Layer[] layers=neuralNetwork.getLayers();
    	for(int i=0;i<layers.length;i++){
    		Layer layer=layers[i];
    		Neuron[] neurons=layer.getNeurons();
    		
    		for(int j=0;j<neurons.length;j++){
    			Neuron neuron=neurons[j];
    			
    			y.add((i+1) * -5.0);
    			x.add(j * 20.0 - 10.0 * neurons.length);
    			z.add(neuron.getOutput() * 15);
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
    
    private void clearDataSet(){
    	if(this.xyzDataset!=null){
    		while(xyzDataset.getSeriesCount()>0){
    			xyzDataset.removeSeries(xyzDataset.getSeriesKey(0));
    		}
    	}
    }
    
    //################################
  	//##       Event Reaction       ##
  	//################################
    private boolean isCompositeAbleToReact(String rate_uuid){
		if (this.isDisposed())
			return false;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || stock == null )
			return false;
		if (!incoming.getUUID().equals(stock.getUUID()))
			return false;
		
		return true;
	}
    
    
    @Inject
	private void neuralNetworkChanged(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_NEW_CURRENT) String rate_uuid) {

		if (!isCompositeAbleToReact(rate_uuid))
			return;
		
		
		this.neuralNetwork=this.stock.getNeuralNetwork().getConfiguration().getCurrentNetwork();
		
		updateYXZDataSet();
		
		//logger.info("---->  Message recieved!!: ");
	}
	

}
