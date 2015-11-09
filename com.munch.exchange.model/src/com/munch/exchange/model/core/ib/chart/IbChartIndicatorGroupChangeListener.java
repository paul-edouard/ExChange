package com.munch.exchange.model.core.ib.chart;

import java.util.EventListener;

public interface IbChartIndicatorGroupChangeListener extends EventListener {
	
	
	void indicatorActivationChanged(IbChartIndicator indicator);
	void indicatorParameterChanged(IbChartIndicator indicator);
	
	void serieActivationChanged(IbChartSerie serie);
	void serieColorChanged(IbChartSerie serie);

}
