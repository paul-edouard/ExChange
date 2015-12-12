package com.munch.exchange.parts.chart.signal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class SignalOptimizationApproximationSetPlot extends
		SignalOptimizationResultPlot {
	
	
	/**
	 * The &epsilon; value used when displaying the approximation set.
	 */
	private static final double EPSILON = 0.01;
	
	
	public SignalOptimizationApproximationSetPlot(String metric,
			SignalOptimizationEditorPart signalOptimizationEditorPart,
			Composite parent, int style) {
		super(metric, signalOptimizationEditorPart, parent, style);
		
		
		
	}

	@Override
	protected void refresh() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for (String key : signalOptimizationEditorPart.getSelectedResults()) {
			NondominatedPopulation population = new EpsilonBoxDominanceArchive(
					EPSILON);
			
			for (Accumulator accumulator : controller.get(key)) {
				if (!accumulator.keySet().contains(metric)) {
					continue;
				}
				
				List<?> list = (List<?>)accumulator.get(metric, 
						accumulator.size(metric)-1);
				
				for (Object object : list) {
					population.add((Solution)object);
				}
			}
			
			if (!population.isEmpty()) {
				XYSeries series = new XYSeries(key, false, true);
				
				for (Solution solution : population) {
					if (solution.getNumberOfObjectives() == 1) {
						series.add(solution.getObjective(0), 
								solution.getObjective(0));
					} else if (solution.getNumberOfObjectives() > 1) {
						series.add(solution.getObjective(0), 
								solution.getObjective(1));
					}
				}
				
				dataset.addSeries(series);
			}
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(metric,
				//localization.getString("text.objective", 1),
				"Objective 1",
				//localization.getString("text.objective", 2),
				"Objective 2",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, 
				true);
		
		for (int i=0; i<dataset.getSeriesCount(); i++) {
			Paint paint = signalOptimizationEditorPart.getPaintHelper().get(dataset.getSeriesKey(i));

			renderer.setSeriesStroke(i, new BasicStroke(3f, 1, 1));
			renderer.setSeriesPaint(i, paint);
			renderer.setSeriesFillPaint(i, paint);
		}
		
		plot.setRenderer(renderer);
		
		//add overlay
		if (controller.getShowLastTrace() &&
				(controller.getLastAccumulator() != null) && 
				controller.getLastAccumulator().keySet().contains(metric)) {
			XYSeriesCollection dataset2 = new XYSeriesCollection();
			NondominatedPopulation population = new EpsilonBoxDominanceArchive(
					EPSILON);
			
			if (controller.getLastAccumulator().keySet().contains(metric)) {
				List<?> list = (List<?>)controller.getLastAccumulator().get(
						metric, controller.getLastAccumulator().size(metric)-1);
				
				for (Object object : list) {
					population.add((Solution)object);
				}
			}
			
			if (!population.isEmpty()) {
				XYSeries series = new XYSeries(
						//localization.getString("text.last"),
						"Last",
						false,
						true);
				
				for (Solution solution : population) {
					series.add(solution.getObjective(0), 
							solution.getObjective(1));
				}
				
				dataset2.addSeries(series);
			}
			
			XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(false,
					true);
			renderer2.setSeriesPaint(0, Color.BLACK);
			
			plot.setDataset(1, dataset2);
			plot.setRenderer(1, renderer2);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		}
		
		Control[] children = this.getChildren();
		for(int i=0;i<children.length;i++)
			children[i].dispose();
		
		ChartComposite compositeChart=new ChartComposite(this, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		this.layout();
		this.update();
		
		/*
		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
		*/

	}

}
