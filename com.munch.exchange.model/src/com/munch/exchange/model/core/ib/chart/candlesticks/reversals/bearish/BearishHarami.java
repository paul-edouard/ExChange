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
public class BearishHarami extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishHarami() {
		super();
	}

	public BearishHarami(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishHarami();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bullish Harami";
		
		this.icon = "icons/candlestick/bearish/reversal/harami.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. The 1st day is a long white day.\n";
		this.note += "2. The 2nd day is a short day whose body is engulfed by the 1st day's body.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "A long 1st day with high volume in the existing uptrend brings complacency to the bulls. The next day \n";
		this.note += "trades in a small range within the previous day's real body. Light volume on the 2nd day should give \n";
		this.note += "rise to concern by the bulls of an impending change of trend. Look for lower prices over the coming \n";
		this.note += "days, especially if the next day provides confirmation of a trend change by closing lower.";

		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlHarami(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
