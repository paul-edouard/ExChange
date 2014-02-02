package com.munch.exchange.parts.composite;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.IHistoricalDataProvider;

public class OverviewRateChart extends Composite {

	private ExchangeRate rate;
	private IHistoricalDataProvider historicalDataProvider;
	private int numberOfDays=100;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public OverviewRateChart(Composite parent,ExchangeRate rate, IHistoricalDataProvider historicalDataProvider) {
		super(parent,  SWT.NONE );
		
		this.rate=rate;
		this.historicalDataProvider=historicalDataProvider;
		loadHistoricalData();
		
		
		setLayout(new org.eclipse.swt.layout.FillLayout());
		
	    
	    //PieDataset dataset = createDataset();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(/*dataset, "My Title"*/);
      
		
        ChartComposite c_comp=new ChartComposite(this, SWT.NONE, chart);
        c_comp.pack();
      
		
	}
	
	private void loadHistoricalData(){
		if(rate.getHistoricalData().isEmpty()){
			historicalDataProvider.load(rate);
		}
	}
	
	
	 /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {

        XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close,numberOfDays);
        String title = "Historical Data: "+rate.getFullName();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,
            "Date",
            "Price",
            priceData,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLowerMargin(0.40);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        rangeAxis1.setNumberFormatOverride(format);

        XYItemRenderer renderer1 = plot.getRenderer();
        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));

        NumberAxis rangeAxis2 = new NumberAxis("Volume");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, createDataset(HistoricalPoint.FIELD_Volume,numberOfDays));
        plot.setRangeAxis(1, rangeAxis2);
        plot.mapDatasetToRangeAxis(1, 1);
        XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        renderer2.setBaseToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"),
                new DecimalFormat("0,000.00")));
        plot.setRenderer(1, renderer2);
        ChartUtilities.applyCurrentTheme(chart);
        renderer2.setBarPainter(new StandardXYBarPainter());
        renderer2.setShadowVisible(false);
        return chart;

    }
	
	/**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createDataset(String field,int days) {
    	return new TimeSeriesCollection(rate.getHistoricalData().getTimeSeries(field, days));
    }
	
	
	/**
     * Creates a sample dataset 
     */
    /*
    private  PieDataset createDataset() {
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Linux", 29);
        result.setValue("Mac", 20);
        result.setValue("Windows", 51);
        return result;
        
    }
    */

	
	/**
     * Creates a chart
     */
    /*
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
    */

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
