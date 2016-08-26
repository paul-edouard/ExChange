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
public class BearishTriStar extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishTriStar() {
		super();
	}

	public BearishTriStar(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishTriStar();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bullish Tri-Star";
		
		this.icon = "icons/candlestick/bearish/reversal/tri-star.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. All three days are doji days.\n";
		this.note += "2. 2nd day gaps above the 1st and 3rd days.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This formation is rare, so always be suspect of the data. This pattern is not reliable for stocks with low \n";
		this.note += "volume. The huge amount of indecision created by these three dojis must not be ignored by traders.\n";
		this.note += "This level of indecision strongly suggests that the trend is about to change.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlTristar(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
