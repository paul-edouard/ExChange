package com.munch.exchange.model.core.ib.chart;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.chart.oscillators.IbChartAverageTrueRange;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSimpleDerivate;
import com.munch.exchange.model.core.ib.chart.signals.SuperTrendSignal;
import com.munch.exchange.model.core.ib.chart.trend.IbChartDownwardTrendLine;
import com.munch.exchange.model.core.ib.chart.trend.IbChartSimpleMovingAverage;
import com.munch.exchange.model.core.ib.chart.trend.IbChartSuperTrend;
import com.munch.exchange.model.core.ib.chart.trend.IbChartUpwardTrendLine;

public class IbChartIndicatorFactory {
	
	public static HashMap<IbChartIndicatorGroup, LinkedList<String>> parentChildrenMap=new HashMap<IbChartIndicatorGroup, LinkedList<String>>();
	
	public static IbChartIndicatorGroup createRoot(){
		return new IbChartIndicatorGroup(null, IbChartIndicatorGroup.ROOT);
	}
	
	public static boolean updateRoot(IbChartIndicatorGroup root, IbBarContainer container){
		if(!root.getName().equals(IbChartIndicatorGroup.ROOT))return false;
		
		parentChildrenMap.clear();
		
		//================================
		//==           TREND            ==
		//================================
		IbChartIndicatorGroup trend=searchOrCreateSubGroup(root,"Trend");
		
		//MOVING AVERAGE
		IbChartIndicatorGroup movingAverage=searchOrCreateSubGroup(trend,"Moving Average");
		addChartIndicator(movingAverage, IbChartSimpleMovingAverage.class);
		
		//TREND LINE
		IbChartIndicatorGroup trendLine=searchOrCreateSubGroup(trend,"Trend Line");
		addChartIndicator(trendLine, IbChartDownwardTrendLine.class);
		addChartIndicator(trendLine, IbChartUpwardTrendLine.class);
		addChartIndicator(trendLine, IbChartSuperTrend.class);
		
		//================================
		//==         OSCILLATOR         ==
		//================================
		IbChartIndicatorGroup oscillator=searchOrCreateSubGroup(root,"Oscillator");
		addChartIndicator(oscillator, IbChartAverageTrueRange.class);
		
		
		//================================
		//==         SIGNALS            ==
		//================================
		IbChartIndicatorGroup signals=searchOrCreateSubGroup(root,"Signals");
		
		//DERIVATE
		IbChartIndicatorGroup derivate =searchOrCreateSubGroup(signals,"Derivate");
		addChartIndicator(derivate, IbChartSimpleDerivate.class);
		
		//SUPER TREND SIGNAL
		IbChartIndicatorGroup trendSignal =searchOrCreateSubGroup(signals,"Trend signal");
		addChartIndicator(trendSignal, SuperTrendSignal.class);
		
		
		
		cleanParents();
		
		return true;
	}
	
	private static void addChildToParent(IbChartIndicatorGroup parent,String child){
		if(!parentChildrenMap.containsKey(parent))
			parentChildrenMap.put(parent, new LinkedList<String>());
		parentChildrenMap.get(parent).add(child);
	}
	
	private static void cleanParents(){
		for(IbChartIndicatorGroup parent:parentChildrenMap.keySet()){
			
			//Clean the unused children
			List<IbChartIndicatorGroup> childrenToDelete=new LinkedList<IbChartIndicatorGroup>();
			for(IbChartIndicatorGroup child:parent.getChildren()){
				if(parentChildrenMap.get(parent).contains(child.getName()))
					continue;
				childrenToDelete.add(child);
			}
			if(childrenToDelete.size()>0){
				parent.getChildren().removeAll(childrenToDelete);
				parent.setDirty(true);
			}
			
			
			//Clean the unused Indicators
			List<IbChartIndicator> indicatorsToDelete=new LinkedList<IbChartIndicator>();
			for(IbChartIndicator ind:parent.getIndicators()){
				if(parentChildrenMap.get(parent).contains(ind.getName()))
					continue;
				indicatorsToDelete.add(ind);
			}
			if(childrenToDelete.size()>0){
				parent.getIndicators().removeAll(indicatorsToDelete);
				parent.setDirty(true);
			}
			
		}
	}
	
	private static IbChartIndicatorGroup searchOrCreateSubGroup(IbChartIndicatorGroup parent,String subGroupName){
		addChildToParent(parent, subGroupName);
		for(IbChartIndicatorGroup child:parent.getChildren()){
			if(child.getName().equals(subGroupName))return child;
		}
		
		parent.setDirty(true);
		return new IbChartIndicatorGroup(parent, subGroupName);
	}
	
	private static void addChartIndicator(IbChartIndicatorGroup parent,Class<? extends IbChartIndicator> indClass){
		
		try {
			IbChartIndicator ind = (IbChartIndicator) indClass.newInstance();
			
			for(IbChartIndicator c_ind:parent.getIndicators()){
				if(c_ind.getName().equals(ind.getName())){
//					System.out.println("compareAndCopyParametersAndSeries: "+ind.getName());
					
					compareAndCopyParametersAndSeries(c_ind, ind, parent);
					
					//Load the optimization set
					if(c_ind instanceof IbChartSignal){
						IbChartSignal signal=(IbChartSignal) c_ind;
						for(IbChartSignalOptimizedParameters optParameters:signal.getOptimizedSet())
							optParameters.getParameters().size();
						
//						System.out.println("Size of Optimized set: "+signal.getOptimizedSet().size());
						
						//signal.getOptimizedSet().size();
						
					}
					
					return ;
				}
			}
			
			parent.setDirty(true);
			ind.setGroup(parent);
			
			addChildToParent(parent, ind.getName());
			
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
	
	private static void compareAndCopyParametersAndSeries(IbChartIndicator old_ind,
			IbChartIndicator new_ind,IbChartIndicatorGroup parent){
		
//		System.out.println("1. Parent dirty: "+parent.isDirty());
		
		//Clean not used parameters
		LinkedList<IbChartParameter> parametersToDelete=new LinkedList<IbChartParameter>();
		for(IbChartParameter oldParam:old_ind.parameters){
			boolean paramFound=false;
			for(IbChartParameter newParam:new_ind.parameters){
				
				//Try to find the corresponding parameter
				if(newParam.getName().equals(oldParam.getName()) && 
						newParam.getType()==oldParam.getType()){
					
					//Check the Max Value
					if(newParam.getMaxValue()!=oldParam.getMaxValue()){
						oldParam.setMaxValue(newParam.getMaxValue());
						parent.setDirty(true);
					}
					
					//Check the Min Value
					if(newParam.getMinValue()!=oldParam.getMinValue()){
						oldParam.setMinValue(newParam.getMinValue());
						parent.setDirty(true);
					}
					
					//Check the Default Value
					if(newParam.getDefaultValue()!=oldParam.getDefaultValue()){
						oldParam.setDefaultValue(newParam.getDefaultValue());
						parent.setDirty(true);
					}
					
					//Check the Factor
					if(newParam.getScalarFactor()!=oldParam.getScalarFactor()){
						oldParam.setScalarFactor(newParam.getScalarFactor());
						parent.setDirty(true);
					}
					
					//Check the Value
					if(oldParam.getValue()<newParam.getMinValue() || oldParam.getValue()>newParam.getMaxValue()){
						oldParam.setValue(newParam.getValue());
						parent.setDirty(true);
					}
					
					
					paramFound=true;
					break;
				}
				
				
			}
			if(!paramFound)
				parametersToDelete.add(oldParam);
		}
		if(parametersToDelete.size()>0){
			System.out.println("Parameters will be deleted!");
			old_ind.parameters.removeAll(parametersToDelete);
			parent.setDirty(true);
		}
		
//		Add new parameters
		LinkedList<IbChartParameter> parametersToAdd=new LinkedList<IbChartParameter>();
		for(IbChartParameter newParam:new_ind.parameters){
			boolean paramFound=false;
			for(IbChartParameter oldParam:old_ind.parameters){
				if(newParam.getName().equals(oldParam.getName()) && 
						newParam.getType()==oldParam.getType()){
					paramFound=true;break;
				}
			}
			
			if(paramFound)continue;
			
		}
		
		for(IbChartParameter param:parametersToAdd){
			IbChartParameter cp=param.copy();
			cp.setIndicator(old_ind);
			old_ind.parameters.add(cp);
			parent.setDirty(true);
		}
		
		
		
//		System.out.println("2. Parent dirty: "+parent.isDirty());
		
		//Clean not used Series
		LinkedList<IbChartSerie> seriesToDelete=new LinkedList<IbChartSerie>();
		for(IbChartSerie oldSerie:old_ind.series){
			boolean serieFound=false;
			for(IbChartSerie newSerie:new_ind.series){
				
				if(oldSerie.getName().equals(newSerie.getName())){
					
					if(oldSerie.getValidAtPosition()!=newSerie.getValidAtPosition()){
						oldSerie.setValidAtPosition(newSerie.getValidAtPosition());
						parent.setDirty(true);
					}
					
					if(oldSerie.getRendererType()!=newSerie.getRendererType()){
						oldSerie.setRendererType(newSerie.getRendererType());
						parent.setDirty(true);
					}
					
					serieFound=true;
					break;
				}
				
			}
			if(!serieFound)
				seriesToDelete.add(oldSerie);
		}
		
//		System.out.println("3. Parent dirty: "+parent.isDirty());
		
		if(seriesToDelete.size()>0){
			old_ind.series.removeAll(seriesToDelete);
			parent.setDirty(true);
		}
		
		
		//Add new Series
		
		LinkedList<IbChartSerie> seriesToAdd=new LinkedList<IbChartSerie>();
		for(IbChartSerie newSerie:new_ind.series){
			boolean serieFound=false;
			for(IbChartSerie oldSerie:old_ind.series){
				if(oldSerie.getName().equals(newSerie.getName())){
					serieFound=true;break;
				}
			}
			
			if(serieFound)continue;
			seriesToAdd.add(newSerie);
		}
		
		for(IbChartSerie serie:seriesToAdd){
			IbChartSerie cp=serie.copy();
			cp.setIndicator(old_ind);
			old_ind.series.add(cp);
			parent.setDirty(true);
		}
		
		
		
		
//		System.out.println("4. Parent dirty: "+parent.isDirty());
		
		
	}
	
}
