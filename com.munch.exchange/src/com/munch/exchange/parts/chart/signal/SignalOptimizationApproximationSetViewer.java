package com.munch.exchange.parts.chart.signal;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SignalOptimizationApproximationSetViewer extends Composite implements  ChartChangeListener{
	
	private static Logger logger = Logger.getLogger(SignalOptimizationApproximationSetViewer.class);
	
	
	/**
	 * The accumulators which contain {@code "Approximation Set"} entries.
	 */
	private java.util.List<Accumulator> accumulators;
	
	/**
	 * The reference set.
	 */
	private NondominatedPopulation referenceSet;
	
	private String name;
	
	
	/**
	 * Maintains a mapping from series key to paints displayed in the plot.
	 */
	private PaintHelper paintHelper;
	
	private Scale scale;
	private Composite chartContainer;
	private ListViewer seedListViewer;
	private List seedList;
	
	

	public SignalOptimizationApproximationSetViewer(String name,  java.util.List<Accumulator> accumulators, 
			NondominatedPopulation referenceSet,Composite parent, int style) {
		super(parent, style);
		
		this.accumulators=accumulators;
		this.referenceSet=referenceSet;
		this.name=name;
		
		
		setLayout(new GridLayout(2, false));
		
		seedListViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		seedList = seedListViewer.getList();
		seedList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("seedList selection: "+seedList.getSelection());
				refresh();
			}
		});
		seedList.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		chartContainer = new Composite(composite, SWT.NONE);
		chartContainer.setLayout(new GridLayout(1, false));
		chartContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		scale = new Scale(composite, SWT.NONE);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Scale selection: "+scale.getSelection());
				refresh();
			}
		});
		
		// TODO Auto-generated constructor stub
		
		postGuiInitialization();
	}
	
	
	private void postGuiInitialization(){
		
		int minimumNFE = Integer.MAX_VALUE;
		int maximumNFE = Integer.MIN_VALUE;
		
		for (Accumulator accumulator : accumulators) {
			minimumNFE = Math.min(minimumNFE, 
					(Integer)accumulator.get("NFE", 0));
			maximumNFE = Math.max(maximumNFE, 
					(Integer)accumulator.get("NFE", accumulator.size("NFE")-1));
		}
		
		scale.setMaximum(maximumNFE);
		scale.setMinimum(minimumNFE);
		scale.setSelection(minimumNFE);
		
		
		//initialize the seed list
		//String[] seeds = new String[accumulators.size()];
		seedList.removeAll();
		for (int i=0; i<accumulators.size(); i++) {
			//seeds[i] = "Seed "+i;
			seedList.add("Seed "+i);
		}
		seedList.selectAll();
		
		//initialize miscellaneous components
		paintHelper = new PaintHelper();
		paintHelper.set("Reference Set",
						Color.BLACK);
		
		refresh();
	}
	
	/**
	 * Updates the display.  This method must only be invoked on the event
	 * dispatch thread.
	 */
	protected void refresh() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		//generate approximation set
		//seedList.getSelectionIndices()
		//for (int seedIndex : seedList.getSelectedIndices()) {
		for (int i=0;i<seedList.getSelectionIndices().length;i++) {
			int seedIndex=seedList.getSelectionIndices()[i];
			Accumulator accumulator = accumulators.get(seedIndex);
			int index = 0;
			
			if (!accumulator.keySet().contains("Approximation Set")) {
				continue;
			}
				
			while ((index < accumulator.size("NFE")-1) && 
					((Integer)accumulator.get("NFE", index) < scale.getSelection())) {
				index++;
			}
				
			java.util.List<?> list = (java.util.List<?>)accumulator.get("Approximation Set", index);
			XYSeries series = new XYSeries(
					"Seed "+seedIndex+1,
					false, true);
				
			for (Object object : list) {
				Solution solution = (Solution)object;
				//series.add(getValue(solution, 0), getValue(solution, 1));
				series.add(-solution.getObjective(0),solution.getObjective(1));
			}
			
			dataset.addSeries(series);
		}
		
		//generate reference set
		if (referenceSet != null) {
			XYSeries series = new XYSeries(
					"Reference Set",
					false, true);
				
			for (Solution solution : referenceSet) {
				//series.add(getValue(solution, 0), getValue(solution, 1));
				series.add(-solution.getObjective(0),solution.getObjective(1));
			}
			
			dataset.addSeries(series);
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(
				name + " @ " + scale.getSelection() + " NFE", 
				"Profit",
				"Risk", 
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true,
				false);
		
		//set the renderer to only display shapes
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, 
				true);
		
		for (int i=0; i<dataset.getSeriesCount(); i++) {
			Paint paint = paintHelper.get(dataset.getSeriesKey(i));
			renderer.setSeriesPaint(i, paint);
		}
		
		plot.setRenderer(renderer);
		
		//set the zoom based on the user's preferences
		/*
		if ((initialRangeBounds == null) || (initialDomainBounds == null)) {
			initialRangeBounds = plot.getRangeAxis().getRange();
			initialDomainBounds = plot.getDomainAxis().getRange();
		}
		
		if (useInitialBounds.isSelected()) {
			plot.getRangeAxis().setRange(initialRangeBounds);
			plot.getDomainAxis().setRange(initialDomainBounds);
		} else if (useZoomBounds.isSelected()) {
			if ((zoomRangeBounds == null) || (zoomDomainBounds == null)) {
				zoomRangeBounds = initialRangeBounds;
				zoomDomainBounds = initialDomainBounds;
			}
			
			plot.getRangeAxis().setRange(zoomRangeBounds);
			plot.getDomainAxis().setRange(zoomDomainBounds);
		} else if (useReferenceSetBounds.isSelected()) {
			if (referenceRangeBounds.getLength() > 0.0) {
				plot.getRangeAxis().setRange(referenceRangeBounds);
			}
			
			if (referenceDomainBounds.getLength() > 0.0) {
				plot.getDomainAxis().setRange(referenceDomainBounds);
			}
		}
		*/
		
		//register with the chart to receive zoom events
		chart.addChangeListener(this);
		
		
		Control[] children = chartContainer.getChildren();
		for(int i=0;i<children.length;i++)
			children[i].dispose();
		
		ChartComposite compositeChart=new ChartComposite(chartContainer, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		chartContainer.layout();
		chartContainer.update();
		
	}


	@Override
	public void chartChanged(ChartChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
