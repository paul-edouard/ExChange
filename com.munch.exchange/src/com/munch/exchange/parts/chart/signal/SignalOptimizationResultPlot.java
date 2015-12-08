package com.munch.exchange.parts.chart.signal;

import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationController;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerEvent;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerListener;

public abstract class SignalOptimizationResultPlot extends Composite implements IbChartSignalOptimizationControllerListener{
	
	/**
	 * The {@link DiagnosticTool} instance containing this plot.
	 */
	
	protected final SignalOptimizationEditorPart signalOptimizationEditorPart ;
	
	/**
	 * The {@link Controller} this plot uses to access result data.
	 */
	protected final IbChartSignalOptimizationController controller;
	
	protected  final Composite parent;
	
	/**
	 * The metric to display.
	 */
	protected final String metric;
	
	
	public SignalOptimizationResultPlot(String metric, SignalOptimizationEditorPart signalOptimizationEditorPart, Composite parent, int style) {
		super(parent, style);
		
		this.parent=parent;
		this.signalOptimizationEditorPart=signalOptimizationEditorPart;
		this.controller=signalOptimizationEditorPart.getController();
		this.metric=metric;
		
		controller.addControllerListener(this);
		
	}
	
	
	@Override
	public void dispose() {
		controller.removeControllerListener(this);
		super.dispose();
	}


	/**
	 * Updates the contents of this plot.  This method is automatically invoked
	 * when the data model is changed, and will always be executed on the
	 * event dispatch thread.
	 */
	protected abstract void refresh();
	
	

	@Override
	public void controllerStateChanged(
			IbChartSignalOptimizationControllerEvent event) {
		if (event.getType().equals(IbChartSignalOptimizationControllerEvent.Type.MODEL_CHANGED)) {
			refresh();
		}
		
	}

}
