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
public class BearishTwoCrows extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishTwoCrows() {
		super();
	}

	public BearishTwoCrows(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishTwoCrows();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bearish Two Crows";
		
		this.icon = "icons/candlestick/bearish/reversal/2crows.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day.\n";
		this.note += "2. 2.2nd day gaps up and is red.\n";
		this.note += "3. 3rd day is red and opens inside the body of the 2nd day, then closes inside the body of the 1st day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap created on the 2nd day gets filled by the 3rd day. This quick pull back does not bode well for \n";
		this.note += "the bulls. This price action indicates a short term top.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl2Crows(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
