package com.munch.exchange.model.core.ib.chart.signals;

import java.util.EventObject;

import org.moeaframework.analysis.diagnostics.Controller;
import org.moeaframework.analysis.diagnostics.DiagnosticTool;


public class IbChartSignalOptimizationControllerEvent extends EventObject {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6512572945660655202L;

	/**
	 * Enumeration of controller event types.
	 */
	public static enum Type {
		
		/**
		 * Indicates the state of the controller changed.  The state changes
		 * when an evaluation job starts and stops.  The state can be
		 * determined by invoking {@link Controller#isRunning()}.
		 */
		STATE_CHANGED,
		
		/**
		 * Indicates the underlying data model has changed.  The model changes
		 * when new results are added or the results are cleared.
		 */
		MODEL_CHANGED,
		
		/**
		 * Indicates the progress of the evaluation has changed.  The progress
		 * can be queried through {@link Controller#getRunProgress()} and
		 * {@link Controller#getOverallProgress()}.
		 */
		PROGRESS_CHANGED,
		
		/**
		 * Indicates the viewing options changed.  These events are primarily
		 * caused by changing how the data is plotted in {@link DiagnosticTool}.
		 */
		VIEW_CHANGED
		
	}
	
	/**
	 * The type of this event.
	 */
	private final Type type;
	
	/**
	 * Constructs a new controller event of the specified type.
	 * 
	 * @param controller the controller from which this event originates
	 * @param type the type of this event
	 */
	public IbChartSignalOptimizationControllerEvent(IbChartSignalOptimizationController controller, Type type) {
		super(controller);
		this.type = type;
	}

	/**
	 * Returns the type of this event.
	 * 
	 * @return the type of this event.
	 */
	public Type getType() {
		return type;
	}

	@Override
	public Controller getSource() {
		return (Controller)super.getSource();
	}


}
