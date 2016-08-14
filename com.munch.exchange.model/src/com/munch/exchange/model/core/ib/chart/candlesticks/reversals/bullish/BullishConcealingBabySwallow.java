package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishConcealingBabySwallow extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishConcealingBabySwallow() {
		super();
	}

	public BullishConcealingBabySwallow(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishConcealingBabySwallow();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Concealing Baby Swallow";
		
		this.note ="How to identify:\n";
		this.note +="1. 1st two days are red Marubozu days.\n";
		this.note +="2. 3rd day is red and opens with a gap down as well as a long\n upper shadow that trades into the body of the 2nd day.\n";
		this.note +="3. 4th day is red and completely engulfs the 3rd day.\n";
		this.note +="\n";
		
		this.note +="Psychology\n";
		this.note += "The bears are in control for the 1st two days of this formation. However, the high on the 3rd day\n";
		this.note +="trades above the close of the previous day. And, a strong upward opening gap appears the last day.\n";
		this.note +="Since the last day closes at a new low, then this is the perfect opportunity for shorts to cover their\n";
		this.note +="positions. Strong short covering should propel the price upward in the coming days.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlConcealBabysWall(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
