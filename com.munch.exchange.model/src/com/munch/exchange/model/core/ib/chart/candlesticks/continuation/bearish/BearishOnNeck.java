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
public class BearishOnNeck extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishOnNeck() {
		super();
	}

	public BearishOnNeck(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishOnNeck();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish On Neck";
		
		this.icon = "icons/candlestick/bearish/continuation/on_neck.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day.\n";
		this.note += "2. 2nd day is a white day which opens below and closes at the low of the 1st day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 2nd day is unable to close above the 1st day's low. This should bring some discomfort to the longs \n";
		this.note += "that entered on the 2nd day. The downtrend should continue shortly.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlInNeck(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
