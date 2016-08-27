package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bearish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.munch.exchange.model.core.ib.chart.candlesticks.CandlesticksWithPenetration;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BearishAbandonedBaby extends CandlesticksWithPenetration {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishAbandonedBaby() {
		super();
	}

	public BearishAbandonedBaby(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishAbandonedBaby();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Abandoned Baby";
		
		this.icon = "icons/candlestick/bearish/reversal/abondoned_baby.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a white day.\n";
		this.note += "2. 2nd day is a doji whose shadows gaps above the 1st day's close.\n";
		this.note += "3. 3rd day is a red day that gaps down and contains no overlapping shadows.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap up on the second day encourages the bulls, however the close on the second day is nearly the \n";
		this.note += "same as the open on the second day. This could be a sign of temporary profit taking by the longs\n";
		this.note += "however the third day reveals that the more likely scenario is indecision on the second day. Watch for \n";
		this.note += "additional downside price action in the next few days.";
		
	}



	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlAbandonedBaby(0, inClose.length-1, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
