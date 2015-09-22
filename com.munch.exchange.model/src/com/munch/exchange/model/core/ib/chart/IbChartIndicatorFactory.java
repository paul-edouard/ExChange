package com.munch.exchange.model.core.ib.chart;


import com.munch.exchange.model.core.ib.chart.trend.IbChartSimpleMovingAverage;

public class IbChartIndicatorFactory {
	
	
	public static IbChartIndicatorGroup createRoot(){
		return new IbChartIndicatorGroup(null, IbChartIndicatorGroup.ROOT);
	}
	
	public static boolean updateRoot(IbChartIndicatorGroup root){
		if(!root.getName().equals(IbChartIndicatorGroup.ROOT))return false;
		
		//TREND
		IbChartIndicatorGroup trend=searchOrCreateSubGroup(root,"Trend");
		
		IbChartIndicatorGroup movingAverage=searchOrCreateSubGroup(trend,"Moving Average");
		addChartIndicator(movingAverage, IbChartSimpleMovingAverage.class);
		
		return false;
	}
	
	private static IbChartIndicatorGroup searchOrCreateSubGroup(IbChartIndicatorGroup parent,String subGroupName){
		for(IbChartIndicatorGroup child:parent.getChildren()){
			if(child.getName().equals(subGroupName))return child;
		}
		
		parent.setDirty(true);
		return new IbChartIndicatorGroup(parent, subGroupName);
	}
	
	private static void addChartIndicator(IbChartIndicatorGroup parent,Class<? extends IbChartIndicator> indClass){
		//Class<?> c = Class.forName("mypackage.MyClass");
		
		try {
			//Constructor<?> cons = indClass.getConstructor(IbChartIndicatorGroup.class);
			IbChartIndicator ind = (IbChartIndicator) indClass.newInstance();
			
			for(IbChartIndicator c_ind:parent.getIndicators()){
				if(c_ind.getName().equals(ind.getName()))return ;
			}
			
			parent.setDirty(true);
			ind.setGroup(parent);
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
	}
	
}
