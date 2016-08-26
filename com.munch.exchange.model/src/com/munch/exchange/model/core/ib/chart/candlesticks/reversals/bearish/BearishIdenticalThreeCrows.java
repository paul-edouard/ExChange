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
public class BearishIdenticalThreeCrows extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishIdenticalThreeCrows() {
		super();
	}

	public BearishIdenticalThreeCrows(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishIdenticalThreeCrows();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bullish Identical Three Crows";
		
		this.icon = "icons/candlestick/bearish/reversal/identical_three_crows.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. Three consecutive long red days with lower closes each day.\n";
		this.note += "2. Each day opens at the previous day's close.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This formation could represent panic selling. Each closing price establishes the opening price for the \n";
		this.note += "next trading day. Additional downside price action should follow.";
	
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlIdentical3Crows(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
