package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.services.IExchangeRateProvider;

public class RateChart extends Composite {

	private static Logger logger = Logger.getLogger(RateChart.class);
	private Combo comboLastDays;
	
	private JFreeChart chart;
	private ExchangeRate rate;
	private IExchangeRateProvider exchangeRateProvider;
	private int numberOfDays=100;
	private Composite compositeChart;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public RateChart(Composite parent,ExchangeRate r,
			IExchangeRateProvider exchangeRateProvider) {
		super(parent, SWT.NONE);
		
		this.rate=r;
		this.exchangeRateProvider=exchangeRateProvider;
		
		
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		setLayout(gridLayout);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		ExpandBar expandBar = new ExpandBar(sashForm, SWT.NONE);
		
		ExpandItem xpndtmPeriode = new ExpandItem(expandBar, SWT.NONE);
		xpndtmPeriode.setExpanded(true);
		xpndtmPeriode.setText("Periode");
		
		Composite compositePeriode = new Composite(expandBar, SWT.NONE);
		xpndtmPeriode.setControl(compositePeriode);
		xpndtmPeriode.setHeight(xpndtmPeriode.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		compositePeriode.setLayout(new GridLayout(2, false));
		
		Label lblLastDays = new Label(compositePeriode, SWT.NONE);
		lblLastDays.setText("Last days:");
		
		comboLastDays = new Combo(compositePeriode, SWT.NONE);
		
		ExpandItem xpndtmNewExpanditem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setExpanded(true);
		xpndtmNewExpanditem.setText("New ExpandItem");
		
		Composite composite = new Composite(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setControl(composite);
		xpndtmNewExpanditem.setHeight(xpndtmNewExpanditem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		chart = createChart();
		compositeChart = new ChartComposite(sashForm, SWT.NONE,chart);
		compositeChart.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {216, 640});
		

	}
	
	
	
	@Inject
	private void historicalDataLoaded(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid) {

		if (this.isDisposed())
			return;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || comboLastDays == null)
			return;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return;

		
		fireHistoricalData();
		this.layout();
	}
	
	private void fireHistoricalData(){
		if(!rate.getHistoricalData().isEmpty()){
			//historicalDataProvider.load(rate);
			resetChartDataSet();
			
		}
	}
	
	private void resetChartDataSet(){
		this.chart.getXYPlot().setDataset(0, createDataset(HistoricalPoint.FIELD_Close,numberOfDays));
		if (rate instanceof Indice || rate instanceof Stock) {
			this.chart.getXYPlot().setDataset(1, createDataset(HistoricalPoint.FIELD_Volume, numberOfDays));
		}
	}
	
	 private XYDataset createDataset(String field,int days) {
	    	
		 XYSeries series=rate.getHistoricalData().getXYSeries(field, days);
		
		
	    	if(!(rate instanceof EconomicData) && !rate.getHistoricalData().isEmpty()){
	    	Calendar LastHisPtDate=rate.getHistoricalData().getLast().getDate();
	    	Calendar LastQuoteDate=rate.getRecordedQuote().getLast().getDate();
	    	if(!DateTool.dateToDayString(LastHisPtDate).equals(DateTool.dateToDayString(LastQuoteDate))){
	    		 HistoricalPoint point=rate.getRecordedQuote().createLastHistoricalPoint();
	    		 if(point!=null)
	    			 series.add(series.getItemCount(),point.get(field));
	    	}
	    	}
	    	
	    	XYSeriesCollection collection=new XYSeriesCollection(series);
	    	//collection.getSeries(series);
	    	return collection;
	    }
	
	 /**
	     * Creates a chart.
	     *
	     * @return a chart.
	     */
	    private JFreeChart createChart() {

	        XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close,numberOfDays);
	        //String title = "Historical Data: "+rate.getFullName();
	       
	       
	        JFreeChart chart = ChartFactory.createXYLineChart(
	            "",
	            "Day",
	            "Price",
	            priceData,
	            PlotOrientation.VERTICAL, // orientation,
	            false,
	            true,
	            false
	        );
	        //ChartUtilities.applyCurrentTheme(chart);
	        chart.setBackgroundPaint(Color.white);
	        
	       
	        
	        XYPlot plot = (XYPlot) chart.getPlot();
	       // plot.setShadowGenerator(new DefaultShadowGenerator());
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        
	        
	     // change the auto tick unit selection to integer units only...
	       // NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	       // rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	       // rangeAxis.setLowerMargin(0.40);
	        
	        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
	        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        domainAxis.setAutoRange(true);
	        domainAxis.setLowerMargin(0.01);
	        domainAxis.setUpperMargin(0.01);
	        
	        //domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        
	        NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
	        rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
	        DecimalFormat format = new DecimalFormat("00.00");
	        rangeAxis1.setNumberFormatOverride(format);
	        rangeAxis1.setAutoRangeIncludesZero(false);
	      	
	        
	        XYItemRenderer renderer1 = plot.getRenderer();
	        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
	                
	    
	        if (renderer1 instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) renderer1;
	            renderer.setBaseStroke(new BasicStroke(2.0f));
	            renderer.setAutoPopulateSeriesStroke(false);
	            renderer.setSeriesPaint(0, Color.BLUE);
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
				plot.mapDatasetToRangeAxis(1, 1);
				XYBarRenderer renderer2 = new XYBarRenderer(0.20);
				renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
						StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
						 new DecimalFormat("0.0"), new DecimalFormat(
								"0,000.00")));
				plot.setRenderer(1, renderer2);
				
				renderer2.setBarPainter(new StandardXYBarPainter());
				renderer2.setShadowVisible(false);
				renderer2.setBarAlignmentFactor(-0.5);
				
			
			}
			
			
	        
	        return chart;

	    }
	
	
}
