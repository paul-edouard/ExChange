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
public class BearishThreeLineStrike extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishThreeLineStrike() {
		super();
	}

	public BearishThreeLineStrike(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishThreeLineStrike();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Three-Line Strike";
		
		this.icon = "icons/candlestick/bearish/continuation/3_line_strike.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st three days make up the Three Black Crows formation\n";
		this.note += "2. The last day is a white day that opens below the 3rd day and closes above the 1st day's open.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 4th day is a powerful move up which could represent a lot of short covering. Since the reversal \n";
		this.note += "has already played out in a matter of one day, the risk is now higher for those who wish to bet on a \n";
		this.note += "reversal. The downtrend should resume.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3LineStrike(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
