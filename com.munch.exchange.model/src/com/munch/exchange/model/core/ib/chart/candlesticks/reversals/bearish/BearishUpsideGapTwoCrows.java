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
public class BearishUpsideGapTwoCrows extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishUpsideGapTwoCrows() {
		super();
	}

	public BearishUpsideGapTwoCrows(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishUpsideGapTwoCrows();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bearish Upside Gap Two Crows";
		
		this.icon = "icons/candlestick/bearish/reversal/upside_gap_two_crows.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day.\n";
		this.note += "2. 2nd day is red and gaps above the 1st day.\n";
		this.note += "3. 3rd day is red and opens above and engulfs the 2nd day.\n";
		this.note += "4. 3rd day closes above the close of the 1st day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap created on the 2nd day has already started to be tested by the 3rd day. Two consecutive \n";
		this.note += "lower closes places a damper on the bullishness. Look for lower prices and the gap to be filled soon.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlUpsideGap2Crows(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
