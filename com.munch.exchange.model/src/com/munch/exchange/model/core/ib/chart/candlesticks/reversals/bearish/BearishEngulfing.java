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
public class BearishEngulfing extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishEngulfing() {
		super();
	}

	public BearishEngulfing(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishEngulfing();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Engulfing";
		
		this.icon = "icons/candlestick/bearish/reversal/engulfing.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. The color of the 1st day's body reflects the trend, however could be a doji.\n";
		this.note += "2. The 2nd day's real body engulfs the 1st day's body.";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "If not much volume occurs on the 1st day of the Bearish Engulfing formation compared to the 2nd \n";
		this.note += "day, then this increases the strength of the pattern. The 2nd day opens above the close of the 1st\n";
		this.note += "day, however quickly sells off to finally close below the open of the 1st day. This damages the spirits \n";
		this.note += "of the longs and brings into question the bull trend which prompts additional selling in the coming\n";
		this.note += "days.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlEngulfing(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
