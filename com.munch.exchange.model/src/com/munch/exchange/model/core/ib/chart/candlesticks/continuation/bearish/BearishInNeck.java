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
public class BearishInNeck extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishInNeck() {
		super();
	}

	public BearishInNeck(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishInNeck();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish In Neck";
		
		this.icon = "icons/candlestick/bearish/continuation/in_neck.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day.\n";
		this.note += "2. 2nd day is a white day which opens below the low of the 1st day.\n";
		this.note += "3. 2nd day closes barely into the body of the 1st day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "Identical to the Bearish On Neck formation, except the downtrend may not continue as quickly.";
		
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
