package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.munch.exchange.model.core.ib.chart.candlesticks.CandlesticksWithPenetration;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishAbandonedBaby extends CandlesticksWithPenetration{


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;

	
	public BullishAbandonedBaby() {
		super();
	}

	public BullishAbandonedBaby(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishAbandonedBaby();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Abandoned Baby";
		
		this.note ="How to identify:\n";
		this.note +="1. 1st day is a red day.\n";
		this.note +="2. 2nd day is a doji whose shadows gaps below the 1st day's close.\n";
		this.note +="3. 3rd day is a white day with no overlapping shadows.\n";
		this.note +="\n";
		
		this.note +="Psychology\n";
		this.note += "The gap down on the second day encourages the bears, however the close on the second day is nearly\n";
		this.note +="the same as the open on the second day. This could be a sign of temporary profit taking by the shorts,\n";
		this.note +="however the third day reveals that the more likely scenario is indecision on the second day. Watch for\n";
		this.note +="upside price action in the next few days.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlAbandonedBaby(0, inClose.length-1, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
