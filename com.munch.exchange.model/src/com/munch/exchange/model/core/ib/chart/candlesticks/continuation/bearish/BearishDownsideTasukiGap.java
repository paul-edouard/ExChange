package com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bearish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.munch.exchange.model.core.ib.chart.candlesticks.CandlesticksWithPenetration;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BearishDownsideTasukiGap extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishDownsideTasukiGap() {
		super();
	}

	public BearishDownsideTasukiGap(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishDownsideTasukiGap();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Downside Tasuki Gap";
		
		this.icon = "icons/candlestick/bearish/continuation/downside_tasuki_gap.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1.1st two days are red days with a down gap between the 1st and 2nd day.\n";
		this.note += "2. 2.3rd day is a white day which opens within the body of the 2nd day and closes within the gap between the 1st and 2nd days.\n";
		this.note += "3. 3rd day should not fully close the gap.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap down on the 2nd day does not get filled by the 3rd day. This suggests that the downtrend will \n";
		this.note += "continue.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlTasukiGap(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
