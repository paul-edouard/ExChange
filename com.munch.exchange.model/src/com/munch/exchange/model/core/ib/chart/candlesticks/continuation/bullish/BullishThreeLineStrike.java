package com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.munch.exchange.model.core.ib.chart.candlesticks.CandlesticksWithPenetration;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishThreeLineStrike extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishThreeLineStrike() {
		super();
	}

	public BullishThreeLineStrike(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishThreeLineStrike();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Three-Line Strike";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st two days are white days with an up gap between the 1st and 2nd day.\n";
		this.note += "2. 3rd day is a white day about the same size as the 2nd day, opening at about the same price.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 2nd and 3rd days are a failed attempt to reverse the uptrend. The uptrend remains intact.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3LineStrike(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
