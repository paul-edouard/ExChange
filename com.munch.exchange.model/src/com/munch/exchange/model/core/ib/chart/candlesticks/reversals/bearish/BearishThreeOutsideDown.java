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
public class BearishThreeOutsideDown extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishThreeOutsideDown() {
		super();
	}

	public BearishThreeOutsideDown(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishThreeOutsideDown();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bullish Three Outside Down";
		
		this.icon = "icons/candlestick/bearish/reversal/three_outside_down.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. Bearish Engulfing formation occurs making up the 1st two days.\n";
		this.note += "2. The 3rd day closes lower than the 2nd day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This is the confirmation of the Engulfing formation.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3Outside(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
