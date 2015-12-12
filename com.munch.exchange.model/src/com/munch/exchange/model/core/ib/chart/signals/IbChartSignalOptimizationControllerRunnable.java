package com.munch.exchange.model.core.ib.chart.signals;

public abstract class  IbChartSignalOptimizationControllerRunnable implements Runnable {

	
	protected IbChartSignalOptimizationControllerEvent event;
	
	
	public IbChartSignalOptimizationControllerRunnable(
			IbChartSignalOptimizationControllerEvent event) {
		super();
		this.event = event;
	}


	@Override
	public abstract void run();

}
