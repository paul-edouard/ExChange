package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.HistoricalDataLoader;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.IExchangeRateProvider;

public class OverviewRateChart extends Composite {
	
	private JFreeChart chart;
	private ExchangeRate rate;
	private IExchangeRateProvider exchangeRateProvider;
	private int numberOfDays=100;
	private ChartComposite c_comp;
	private Combo LastDays;
	private Label lblLastDays;
	
	private boolean quoteActivated=false;
	
	@Inject
	HistoricalDataLoader historicalDataLoader;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public OverviewRateChart(Composite parent,ExchangeRate r,
			IExchangeRateProvider exchangeRateProvider) {
		super(parent,  SWT.NONE );
		
		this.rate=r;
		this.exchangeRateProvider=exchangeRateProvider;
		
		
	    //PieDataset dataset = createDataset();
        // based on the dataset we create the chart
        chart = createChart(/*dataset, "My Title"*/);
        setLayout(new GridLayout(1, false));
        
        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        
        lblLastDays = new Label(composite, SWT.NONE);
        //lblLastDays.setText("Plot last Days: ");
        lblLastDays.setText("Loading ");
        
        LastDays = new Combo(composite, SWT.NONE);
        LastDays.setText("100");
        LastDays.add("Intraday");
        LastDays.add("all");
        LastDays.add("300");
        LastDays.add("200");
        LastDays.add("100");
        LastDays.add("50");
        LastDays.add("30");
        LastDays.add("10");
        LastDays.add("5");
        LastDays.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		int allpts=rate.getHistoricalData().size();
        		quoteActivated=false;
        		
        		if(LastDays.getText().equals("all")){
        			numberOfDays=allpts;
        		}
        		else if(LastDays.getText().equals("Intraday")){
        			quoteActivated=true;
        			numberOfDays=-1;
        		}
        		else if(!LastDays.getText().isEmpty()){
        			int d=Integer.parseInt(LastDays.getText());
        			if(d>allpts)numberOfDays=allpts;
        			else numberOfDays=d;
        		}
        		
        		resetChartDataSet();
        		
        		
        	}
        });
      
		
        c_comp=new ChartComposite(this, SWT.NONE, chart);
        c_comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
       // c_comp.setRedraw(true);
       // c_comp.pack();
        
        c_comp.setVisible(false);
        LastDays.setVisible(false);
      
        fireHistoricalData();
	}
	
	
	@Inject
	private void quoteDataLoaded(
			@Optional @UIEventTopic(IEventConstant.QUOTE_ALLTOPICS) String rate_uuid) {
		
		
		
		if (this.isDisposed() || !quoteActivated)
			return;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || c_comp == null
				|| LastDays == null || lblLastDays == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;

		
		fireHistoricalData();
		this.layout();
	}
	
	@Inject
	private void historicalDataCleared(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_CLEARED) String rate_uuid) {
		
		if (this.isDisposed())
			return;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || c_comp == null
				|| LastDays == null || lblLastDays == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;
		
		 c_comp.setVisible(false);
	     LastDays.setVisible(false);
	     lblLastDays.setText("Loading ");
	     
	     historicalDataLoader.schedule();
		
	}
	
	@Inject
	private void historicalDataLoading(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADING) String rate_uuid_per) {
		
		if (this.isDisposed())
			return;
		if (rate_uuid_per == null || rate_uuid_per.isEmpty())
			return;
		
		String[] s=rate_uuid_per.split(";");
		if(s.length!=2)return;
		
		String rate_uuid=s[0];
		
		

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || c_comp == null
				|| LastDays == null || lblLastDays == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;

		 lblLastDays.setText("Loading "+s[1]+"%");
		this.layout();
	}
	
	@Inject
	private void historicalDataLoaded(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid) {

		if (this.isDisposed())
			return;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || c_comp == null
				|| LastDays == null || lblLastDays == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;

		
		fireHistoricalData();
		this.layout();
	}
	
	private void resetChartDataSet(){
		this.chart.getXYPlot().setDataset(0, createDataset(HistoricalPoint.FIELD_Close,numberOfDays));
		if (rate instanceof Indice || rate instanceof Stock) {
			this.chart.getXYPlot().setDataset(1, createDataset(HistoricalPoint.FIELD_Volume, numberOfDays));
		}
	}
	
	private void fireHistoricalData(){
		if(!rate.getHistoricalData().isEmpty()){
			//historicalDataProvider.load(rate);
			resetChartDataSet();
			
			c_comp.setVisible(true);
		    LastDays.setVisible(true);
		    lblLastDays.setText("Plot last Days: ");
		}
	}
	
	
	 /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {

        XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close,numberOfDays);
        //String title = "Historical Data: "+rate.getFullName();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "",
            "Date",
            "Price",
            priceData,
            false,
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
    
        if (renderer1 instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) renderer1;
            renderer.setBaseStroke(new BasicStroke(2.0f));
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setSeriesPaint(0, java.awt.Color.BLUE);
            //renderer.setSeriesPaint(1, new Color(0xA6D96A));
            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
            //renderer.setSeriesPaint(3, new Color(0xFFFFBF));
     
        }
        
        
        //Volume
		if (rate instanceof Indice || rate instanceof Stock) {
			NumberAxis rangeAxis2 = new NumberAxis("Volume");
			rangeAxis2.setUpperMargin(1.00); // to leave room for price line
			plot.setRangeAxis(1, rangeAxis2);
			plot.setDataset(1,
					createDataset(HistoricalPoint.FIELD_Volume, numberOfDays));
			plot.setRangeAxis(1, rangeAxis2);
			plot.mapDatasetToRangeAxis(1, 1);
			XYBarRenderer renderer2 = new XYBarRenderer(0.20);
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
					StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
					new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat(
							"0,000.00")));
			plot.setRenderer(1, renderer2);

			ChartUtilities.applyCurrentTheme(chart);
			renderer2.setBarPainter(new StandardXYBarPainter());
			renderer2.setShadowVisible(false);
			renderer2.setBarAlignmentFactor(0.5);
		
		}
        
        return chart;

    }
    
    
   
    
	
	/**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createDataset(String field,int days) {
    	
    	if(!quoteActivated){
    		TimeSeries series=rate.getHistoricalData().getTimeSeries(field, days);
    		TimeSeriesCollection collection=new TimeSeriesCollection(series);
    		return collection;
    	}
    	else{
    		TimeSeries series=rate.getRecordedQuote().getTimeSeries(field);
    		TimeSeriesCollection collection=new TimeSeriesCollection(series);
    		return collection;
    	}
    }
	
	
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
