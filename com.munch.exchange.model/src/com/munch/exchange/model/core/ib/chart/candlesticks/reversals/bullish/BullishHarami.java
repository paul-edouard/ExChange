package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishHarami extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishHarami() {
		super();
	}

	public BullishHarami(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishHarami();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Harami";
		
		this.icon = "icons/candlestick/bullish/reversal/harami.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. The 1st day is a long red day.\n";
		this.note += "2. The 2nd day is a short day whose body is engulfed by the 1st day's body.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "A long 1st day with high volume in the existing downtrend brings complacency to the bears. The next\n";
		this.note +="day trades in a small range within the previous day's real body. Light volume on the 2nd day should\n";
		this.note +="give rise to concern by the bears of an impending change of trend. Look for higher prices over the\n";
		this.note +="coming days, especially if the next day provides confirmation of a trend change by closing higher.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlHarami(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
