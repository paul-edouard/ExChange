package com.munch.exchange.model.core.ib.chart.signals;


import java.util.EventListener;

public interface IbChartSignalOptimizationControllerListener extends EventListener {
	
	/**
	 * Invoked by the controller to indicate its state changed.  The
	 * {@code ControllerEvent} indicates the type of event which has occurred.
	 * 
	 * @param event details of the controller event
	 */
	public void controllerStateChanged(IbChartSignalOptimizationControllerEvent event);
}
