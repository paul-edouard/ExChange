package com.munch.exchange.model.core.chart;


import org.apache.log4j.Logger;

import com.munch.exchange.model.core.chart.signals.ChartSimpleDerivate;
import com.munch.exchange.model.core.chart.trend.ChartAdaptiveMovingAverage;
import com.munch.exchange.model.core.chart.trend.ChartDoubleLinearWeigthedMovingAverage;
import com.munch.exchange.model.core.chart.trend.ChartSimpleMovingAverage;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;

public class ChartIndicatorFactory {
	
	
	private static Logger logger = Logger.getLogger(ChartIndicatorFactory.class);
	
	public static ChartIndicator createChartIndicator(String className,TimeSeries series){
		if(className.equals(ChartAdaptiveMovingAverage.class.getSimpleName())){
			return new ChartAdaptiveMovingAverage(series);
		}
		else if(className.equals(ChartDoubleLinearWeigthedMovingAverage.class.getSimpleName())){
			return new ChartDoubleLinearWeigthedMovingAverage(series);
		}
		else if(className.equals(ChartSimpleMovingAverage.class.getSimpleName())){
			return new ChartSimpleMovingAverage(series);
		}
		else if(className.equals(ChartSimpleDerivate.class.getSimpleName())){
			return new ChartSimpleDerivate(series);
		}
		
		return null;
		
	}
	
	public static ChartIndicator createChartIndicator(String[] csvTocken,TimeSeries series){
		if(csvTocken==null || csvTocken.length<2)return null;
		String className=csvTocken[1];
		
		logger.info("Class Name: "+className);
		
		ChartIndicator ind=createChartIndicator(className,series);
		
		logger.info("Ind: "+ind.getName());
		
		if(ind.getChartParameters().size()!=csvTocken.length-2)return null;
		
		for(int i=2;i<csvTocken.length;i++){
			ind.getChartParameters().get(i-2).setValue(csvTocken[i]);
		}
		
		return ind;
		
	}
	

	
	public static ChartIndicatorGroup createRoot(){
		ChartIndicatorGroup root=new ChartIndicatorGroup(null,ChartIndicatorGroup.ROOT);
		
		//TREND
		ChartIndicatorGroup trend=new ChartIndicatorGroup(root,"Trend");
		
		ChartIndicatorGroup movingAverage=new ChartIndicatorGroup(trend,"Moving Average");
		new ChartSimpleMovingAverage(movingAverage);
		new ChartDoubleLinearWeigthedMovingAverage(movingAverage);
		new ChartAdaptiveMovingAverage(movingAverage);
		
		//SIGNALS
		ChartIndicatorGroup signals=new ChartIndicatorGroup(root,"Signals");
		
		ChartIndicatorGroup derivate=new ChartIndicatorGroup(signals,"Derivate");
		new ChartSimpleDerivate(derivate);
		
		return root;
	}

}
