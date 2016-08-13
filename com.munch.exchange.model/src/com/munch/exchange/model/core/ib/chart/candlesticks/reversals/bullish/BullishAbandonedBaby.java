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
		this.note = "The gap down on the second day encourages the bears, however the close on the second day is nearly\n";
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
