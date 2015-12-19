package com.munch.exchange.parts.chart.signal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
		
		
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		this.setLayout(gl_parent);
		
		controller.addControllerListener(this);
		
		this.refresh();
		
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
			
			if(this.isDisposed())return;
			
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					
					refresh();
					
				}
			}
			);
			
			
		}
		
	}

}
