package com.munch.exchange.model.core.ib.chart;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishDownsideGapThreeMethods;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishDownsideTasukiGap;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishFallingThreeMethods;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishInNeck;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishSideBySideWhiteLines;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishThreeLineStrike;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish.BearishThrusting;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish.BullishMatHold;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish.BullishRisingThreeMethods;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish.BullishSidebySideWhiteLines;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish.BullishThreeLineStrike;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish.BullishUpsideGapThreeMethods;
import com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish.BullishUpsideTasukiGap;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishAbandonedBaby;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishAdvanceBlock;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishBeltHold;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishBreakaway;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishDarkCloudCover;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishDojiStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishEngulfing;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishEveningDojiStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishEveningStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishHangingMan;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishHarami;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishIdenticalThreeCrows;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishKicking;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishShootingStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishThreeBlackCrows;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishThreeInsideDown;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishThreeOutsideDown;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishTriStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishTwoCrows;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish.BearishUpsideGapTwoCrows;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishAbandonedBaby;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishBeltHold;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishBreakaway;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishConcealingBabySwallow;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishDojiStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishEngulfing;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishHammer;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishHarami;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishHaramiCross;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishHomingPigeon;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishInvertedHammer;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishKicking;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishLadderBottom;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishMatchingLow;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishMorningDojiStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishMorningStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishPiercingLine;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishStickSandwich;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishThreeInsideUp;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishThreeOutsideUp;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishThreeStarsInTheSouth;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishThreeWhiteSoldiers;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishTriStar;
import com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish.BullishUniqueThreeRiverBottom;
import com.munch.exchange.model.core.ib.chart.cycle.IbChartHilbertTrSineWave;
import com.munch.exchange.model.core.ib.chart.levels.IbChartDayPivot;
import com.munch.exchange.model.core.ib.chart.levels.IbChartRoundNumber;
import com.munch.exchange.model.core.ib.chart.oscillators.IbChartAverageTrueRange;
import com.munch.exchange.model.core.ib.chart.oscillators.IbChartMACD;
import com.munch.exchange.model.core.ib.chart.oscillators.IbChartMMI;
import com.munch.exchange.model.core.ib.chart.oscillators.IbChartRSI;
import com.munch.exchange.model.core.ib.chart.oscillators.IbChartStochasticOscillator;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSimpleDerivate;
import com.munch.exchange.model.core.ib.chart.signals.RockwellLongSignal;
import com.munch.exchange.model.core.ib.chart.signals.SuperTrendSignal;
import com.munch.exchange.model.core.ib.chart.signals.strategies.Bladerunner;
import com.munch.exchange.model.core.ib.chart.signals.strategies.BladerunnerMovingStopLoss;
import com.munch.exchange.model.core.ib.chart.signals.strategies.OpenRange;
import com.munch.exchange.model.core.ib.chart.signals.strategies.ThreeMovingAverage;
import com.munch.exchange.model.core.ib.chart.signals.strategies.ThreeMovingAverageFiltered;
import com.munch.exchange.model.core.ib.chart.signals.strategies.ThreeMovingAverageWithADXFilter;
import com.munch.exchange.model.core.ib.chart.signals.strategies.ZeroLagStrategy;
import com.munch.exchange.model.core.ib.chart.trend.IbChartADX;
import com.munch.exchange.model.core.ib.chart.trend.IbChartAdaptiveMovingAverage;
import com.munch.exchange.model.core.ib.chart.trend.IbChartBollingerBands;
import com.munch.exchange.model.core.ib.chart.trend.IbChartDoubleMovingAverage;
import com.munch.exchange.model.core.ib.chart.trend.IbChartDownwardTrendLine;
import com.munch.exchange.model.core.ib.chart.trend.IbChartFractalAdaptMovAver;
import com.munch.exchange.model.core.ib.chart.trend.IbChartResistanceLine;
import com.munch.exchange.model.core.ib.chart.trend.IbChartSimpleMovingAverage;
import com.munch.exchange.model.core.ib.chart.trend.IbChartSuperTrend;
import com.munch.exchange.model.core.ib.chart.trend.IbChartTrendLine;
import com.munch.exchange.model.core.ib.chart.trend.IbChartTrLineWithoutRes;
import com.munch.exchange.model.core.ib.chart.trend.IbChartTripleMovingAverage;
import com.munch.exchange.model.core.ib.chart.trend.IbChartUpwardTrendLine;
import com.munch.exchange.model.core.ib.chart.trend.IbChartZeroLag;
import com.munch.exchange.model.core.ib.chart.values.IbChartClose;
import com.munch.exchange.model.core.ib.chart.values.IbChartHigh;
import com.munch.exchange.model.core.ib.chart.values.IbChartLow;
import com.munch.exchange.model.core.ib.chart.values.IbChartOpen;

public class IbChartIndicatorFactory {
	
	public static HashMap<IbChartIndicatorGroup, LinkedList<String>> parentChildrenMap=new HashMap<IbChartIndicatorGroup, LinkedList<String>>();
	
	public static IbChartIndicatorGroup createRoot(){
		return new IbChartIndicatorGroup(null, IbChartIndicatorGroup.ROOT);
	}
	
	public static boolean updateRoot(IbChartIndicatorGroup root, BarContainer container){
		if(!root.getName().equals(IbChartIndicatorGroup.ROOT))return false;
		
		parentChildrenMap.clear();
		
		
		//================================
		//==           VALUES           ==
		//================================
		IbChartIndicatorGroup values=searchOrCreateSubGroup(root,"Values");
		
//		OPEN
		addChartIndicator(values, IbChartOpen.class);
//		LOW
		addChartIndicator(values, IbChartLow.class);
//		HIGH
		addChartIndicator(values, IbChartHigh.class);
//		CLOSE
		addChartIndicator(values, IbChartClose.class);
		
		
		//================================
		//==           TREND            ==
		//================================
		IbChartIndicatorGroup trend=searchOrCreateSubGroup(root,"Trend");
		
		//MOVING AVERAGE
		IbChartIndicatorGroup movingAverage=searchOrCreateSubGroup(trend,"Moving Average");
		addChartIndicator(movingAverage, IbChartSimpleMovingAverage.class);
		addChartIndicator(movingAverage, IbChartDoubleMovingAverage.class);
		addChartIndicator(movingAverage, IbChartTripleMovingAverage.class);
		addChartIndicator(movingAverage, IbChartAdaptiveMovingAverage.class);
		addChartIndicator(movingAverage, IbChartFractalAdaptMovAver.class);
		addChartIndicator(movingAverage, IbChartZeroLag.class);
		
		
		//TREND LINE
		IbChartIndicatorGroup trendLine=searchOrCreateSubGroup(trend,"Trend Line");
		addChartIndicator(trendLine, IbChartDownwardTrendLine.class);
		addChartIndicator(trendLine, IbChartUpwardTrendLine.class);
		addChartIndicator(trendLine, IbChartSuperTrend.class);
		addChartIndicator(trendLine, IbChartTrendLine.class);
		addChartIndicator(trendLine, IbChartResistanceLine.class);
		addChartIndicator(trendLine, IbChartTrLineWithoutRes.class);
		
		
//		BOLLINGER BAND
		addChartIndicator(trend, IbChartBollingerBands.class);
		
//		ADX
		addChartIndicator(trend, IbChartADX.class);
		
		
		
		//================================
		//==         OSCILLATOR         ==
		//================================
		IbChartIndicatorGroup oscillator=searchOrCreateSubGroup(root,"Oscillator");
		addChartIndicator(oscillator, IbChartAverageTrueRange.class);
		addChartIndicator(oscillator, IbChartMACD.class);
		addChartIndicator(oscillator, IbChartRSI.class);
		addChartIndicator(oscillator, IbChartStochasticOscillator.class);
		addChartIndicator(oscillator, IbChartMMI.class);
		
		
		//================================
		//==          LEVELS         ==
		//================================
		IbChartIndicatorGroup levels=searchOrCreateSubGroup(root,"Levels");
		addChartIndicator(levels, IbChartRoundNumber.class);
		addChartIndicator(levels, IbChartDayPivot.class);
		
		//================================
		//==          CANDLESTICKS      ==
		//================================
		IbChartIndicatorGroup candlesticks=searchOrCreateSubGroup(root,"Candlesticks");
		
		IbChartIndicatorGroup bullish_continuation=searchOrCreateSubGroup(candlesticks,"Bullish Continuation");
		addChartIndicator(bullish_continuation, BullishMatHold.class);
		addChartIndicator(bullish_continuation, BullishRisingThreeMethods.class);
		addChartIndicator(bullish_continuation, BullishSidebySideWhiteLines.class);
		addChartIndicator(bullish_continuation, BullishThreeLineStrike.class);
		addChartIndicator(bullish_continuation, BullishUpsideGapThreeMethods.class);
		addChartIndicator(bullish_continuation, BullishUpsideTasukiGap.class);
		
		
		IbChartIndicatorGroup bullish_reversals=searchOrCreateSubGroup(candlesticks,"Bullish Reversal");
		addChartIndicator(bullish_reversals, BullishAbandonedBaby.class);
		addChartIndicator(bullish_reversals, BullishBeltHold.class);
		addChartIndicator(bullish_reversals, BullishBreakaway.class);
		addChartIndicator(bullish_reversals, BullishConcealingBabySwallow.class);
		addChartIndicator(bullish_reversals, BullishDojiStar.class);
		addChartIndicator(bullish_reversals, BullishEngulfing.class);
		addChartIndicator(bullish_reversals, BullishHammer.class);
		addChartIndicator(bullish_reversals, BullishHarami.class);
		addChartIndicator(bullish_reversals, BullishHaramiCross.class);
		addChartIndicator(bullish_reversals, BullishHomingPigeon.class);
		addChartIndicator(bullish_reversals, BullishInvertedHammer.class);
		addChartIndicator(bullish_reversals, BullishKicking.class);
		addChartIndicator(bullish_reversals, BullishLadderBottom.class);
		addChartIndicator(bullish_reversals, BullishMatchingLow.class);
		addChartIndicator(bullish_reversals, BullishMorningDojiStar.class);
		addChartIndicator(bullish_reversals, BullishMorningStar.class);
		addChartIndicator(bullish_reversals, BullishPiercingLine.class);
		addChartIndicator(bullish_reversals, BullishStickSandwich.class);
		addChartIndicator(bullish_reversals, BullishThreeInsideUp.class);
		addChartIndicator(bullish_reversals, BullishThreeOutsideUp.class);
		addChartIndicator(bullish_reversals, BullishThreeStarsInTheSouth.class);
		addChartIndicator(bullish_reversals, BullishThreeWhiteSoldiers.class);
		addChartIndicator(bullish_reversals, BullishTriStar.class);
		addChartIndicator(bullish_reversals, BullishUniqueThreeRiverBottom.class);
		
		
		IbChartIndicatorGroup bearish_continuation=searchOrCreateSubGroup(candlesticks,"Bearish Continuation");
		addChartIndicator(bearish_continuation, BearishDownsideGapThreeMethods.class);
		addChartIndicator(bearish_continuation, BearishDownsideTasukiGap.class);
		addChartIndicator(bearish_continuation, BearishFallingThreeMethods.class);
		addChartIndicator(bearish_continuation, BearishInNeck.class);
		addChartIndicator(bearish_continuation, BearishSideBySideWhiteLines.class);
		addChartIndicator(bearish_continuation, BearishThreeLineStrike.class);
		addChartIndicator(bearish_continuation, BearishThrusting.class);
		
		IbChartIndicatorGroup bearish_reversal=searchOrCreateSubGroup(candlesticks,"Bearish Reversal");
		addChartIndicator(bearish_reversal, BearishAbandonedBaby.class);
		addChartIndicator(bearish_reversal, BearishAdvanceBlock.class);
		addChartIndicator(bearish_reversal, BearishBeltHold.class);
		addChartIndicator(bearish_reversal, BearishBreakaway.class);
		addChartIndicator(bearish_reversal, BearishDarkCloudCover.class);
		addChartIndicator(bearish_reversal, BearishDojiStar.class);
		addChartIndicator(bearish_reversal, BearishEngulfing.class);
		addChartIndicator(bearish_reversal, BearishEveningDojiStar.class);
		addChartIndicator(bearish_reversal, BearishEveningStar.class);
		addChartIndicator(bearish_reversal, BearishHangingMan.class);
		addChartIndicator(bearish_reversal, BearishHarami.class);
		addChartIndicator(bearish_reversal, BearishIdenticalThreeCrows.class);
		addChartIndicator(bearish_reversal, BearishKicking.class);
		addChartIndicator(bearish_reversal, BearishShootingStar.class);
		addChartIndicator(bearish_reversal, BearishThreeBlackCrows.class);
		addChartIndicator(bearish_reversal, BearishThreeInsideDown.class);
		addChartIndicator(bearish_reversal, BearishThreeOutsideDown.class);
		addChartIndicator(bearish_reversal, BearishTriStar.class);
		addChartIndicator(bearish_reversal, BearishTwoCrows.class);
		addChartIndicator(bearish_reversal, BearishUpsideGapTwoCrows.class);
		
		
		//================================
		//==             CYCLE          ==
		//================================
		IbChartIndicatorGroup cycle=searchOrCreateSubGroup(root,"Cycle");
		addChartIndicator(cycle, IbChartHilbertTrSineWave.class);
		
		
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
		addChartIndicator(trendSignal, RockwellLongSignal.class);
		addChartIndicator(trendSignal, ThreeMovingAverage.class);
		addChartIndicator(trendSignal, ThreeMovingAverageFiltered.class);
		addChartIndicator(trendSignal, ThreeMovingAverageWithADXFilter.class);
		addChartIndicator(trendSignal, Bladerunner.class);
		addChartIndicator(trendSignal, BladerunnerMovingStopLoss.class);
		addChartIndicator(trendSignal, OpenRange.class);
		addChartIndicator(trendSignal, ZeroLagStrategy.class);
		
		
		
		
		
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
		if(!old_ind.getNote().equals(new_ind.getNote())){
			old_ind.setNote(new_ind.getNote());
			parent.setDirty(true);
		}
		
		
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
			
			parametersToAdd.add(newParam);
			
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
