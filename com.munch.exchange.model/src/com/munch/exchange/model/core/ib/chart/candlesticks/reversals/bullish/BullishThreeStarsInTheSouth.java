package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishThreeStarsInTheSouth extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishThreeStarsInTheSouth() {
		super();
	}

	public BullishThreeStarsInTheSouth(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishThreeStarsInTheSouth();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Three Stars in the South";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day with a long lower shadow.\n";
		this.note += "2. 2nd day is a small version of the 1st day with a lower above the 1st day's low.\n";
		this.note += "3. 3rd day is a small red Marubozu which opens and closes inside the 2nd day's range (high-low).\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "Notice that each day's price range is engulfed by the previous day's range. In a downtrend this gives\n";
		this.note += "rise to indecision and increased risk for the bears to remain short. Look for higher prices ahead.";

		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3StarsInSouth(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
