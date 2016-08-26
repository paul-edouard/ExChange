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
public class BearishEveningStar extends CandlesticksWithPenetration {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishEveningStar() {
		super();
	}

	public BearishEveningStar(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishEveningStar();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Evening Star";
		
		this.icon = "icons/candlestick/bearish/reversal/evening_star.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day.\n";
		this.note += "2. 2nd day gaps above the 1st day's close.\n";
		this.note += "3. 3rd day is a long red day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 2nd day gaps higher, but trades in a small range. The bearishness of this indecision is confirmed \n";
		this.note += "by the lower close of the 3rd day. Look for lower prices.";
		
	}



	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlEveningStar(0, inClose.length-1, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
