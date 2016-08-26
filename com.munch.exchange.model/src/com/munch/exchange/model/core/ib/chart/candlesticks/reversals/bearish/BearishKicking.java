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
public class BearishKicking extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishKicking() {
		super();
	}

	public BearishKicking(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishKicking();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bullish Kicking";
		
		this.icon = "icons/candlestick/bearish/reversal/kicking.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a white Marubozu.\n";
		this.note += "2. 2nd day is a red Marubozu and gaps open below the 1st day's open.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap created by the 2nd day becomes a resistance area. Expect lower prices and for the gap to be \n";
		this.note += "tested before breaking back to the upside.";
	
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlKicking(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
