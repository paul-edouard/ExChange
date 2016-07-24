package com.munch.exchange.model.core.ib.chart.signals;

import java.util.HashMap;
import java.util.List;

import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;

public class IbChartSignalDataCollector {
	
	
	private long[] times = null;
	
	private HashMap<DataType, double[]> dataMap = new HashMap<DataType, double[]>();

	public IbChartSignalDataCollector() {
		super();
	}
	
	
	public synchronized double[] getDataFromBars(List<ExBar> bars, DataType dataType){
		if(!dataMap.containsKey(dataType)){
			dataMap.put(dataType, BarUtils.barsToDoubleArray(bars, dataType));
		}
				
		return dataMap.get(dataType);
	}
	
	protected synchronized long[] getTimeArrayFromBar(List<ExBar> bars){
		if(times == null)
			times=BarUtils.getTimeArray(bars);
		
		return times;
	}
	

}
