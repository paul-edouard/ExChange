package com.munch.exchange.parts.composite;

import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.Rotation;

public class OverviewRateChart extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OverviewRateChart(Composite parent) {
		super(parent,  SWT.NONE );
		
		setLayout(new org.eclipse.swt.layout.FillLayout());
		
		/*
		Composite swtAwtComponent = new Composite(this, SWT.EMBEDDED);
	    java.awt.Frame frame = SWT_AWT.new_Frame( swtAwtComponent );
	    javax.swing.JPanel panel = new javax.swing.JPanel( );
	    frame.add(panel);
	   // frame.add(new PieChart("My App", "My Title"));
	    */
	    
	    PieDataset dataset = createDataset();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, "My Title");
        // we put the chart into a panel
       // ChartPanel chartPanel = new ChartPanel(chart);
        // default size
       // chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		
        ChartComposite c_comp=new ChartComposite(this, SWT.NONE, chart);
        c_comp.pack();
        /*
        frame.add(chartPanel);
        
        frame.pack();
        frame.setVisible(true);
	    */
		
		
	}
	
	public class PieChart extends JFrame {

		  private static final long serialVersionUID = 1L;

		  public PieChart(String applicationTitle, String chartTitle) {
		        super(applicationTitle);
		        // This will create the dataset 
		        PieDataset dataset = createDataset();
		        // based on the dataset we create the chart
		        JFreeChart chart = createChart(dataset, chartTitle);
		        // we put the chart into a panel
		        ChartPanel chartPanel = new ChartPanel(chart);
		        // default size
		        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		        // add it to our application
		        setContentPane(chartPanel);
		        
		        pack();
		        
		        setVisible(true);


		    }
		    
		    /**
		     * Creates a sample dataset 
		     */

		    private  PieDataset createDataset() {
		        DefaultPieDataset result = new DefaultPieDataset();
		        result.setValue("Linux", 29);
		        result.setValue("Mac", 20);
		        result.setValue("Windows", 51);
		        return result;
		        
		    }
		    
		    /**
		     * Creates a chart
		     */

		    private JFreeChart createChart(PieDataset dataset, String title) {
		        
		        JFreeChart chart = ChartFactory.createPieChart3D(title,          // chart title
		            dataset,                // data
		            true,                   // include legend
		            true,
		            false);

		        PiePlot3D plot = (PiePlot3D) chart.getPlot();
		        plot.setStartAngle(290);
		        plot.setDirection(Rotation.CLOCKWISE);
		        plot.setForegroundAlpha(0.5f);
		        return chart;
		        
		    }
		} 
	
	/**
     * Creates a sample dataset 
     */

    private  PieDataset createDataset() {
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Linux", 29);
        result.setValue("Mac", 20);
        result.setValue("Windows", 51);
        return result;
        
    }

	
	/**
     * Creates a chart
     */

    private JFreeChart createChart(PieDataset dataset, String title) {
        
        JFreeChart chart = ChartFactory.createPieChart3D(title,          // chart title
            dataset,                // data
            true,                   // include legend
            true,
            false);

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;
        
    }

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
