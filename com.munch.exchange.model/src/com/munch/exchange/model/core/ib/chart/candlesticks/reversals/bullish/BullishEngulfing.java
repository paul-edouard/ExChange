package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishEngulfing extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishEngulfing() {
		super();
	}

	public BullishEngulfing(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishEngulfing();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Engulfing";
		
		this.icon = "icons/candlestick/bullish/reversal/engulfing.gif";

		this.note ="How to identify:\n";
		this.note +="1. The color of the 1st day's body reflects the trend, however could be a doji.\n";
		this.note +="2. The 2nd day's real body engulfs the 1st day's body.\n";
		this.note +="\n";
		
		this.note +="Psychology\n";
		this.note += "If not much volume occurs on the 1st day of the Bullish Engulfing formation compared to the 2nd day,\n";
		this.note +="then this increases the strength of the pattern. The 2nd day opens below the close of the 1st day,\n";
		this.note +="however quickly rallies to close above the open of the 1st day. This damages the spirits of the shorts\n";
		this.note +="and brings into question the bear trend which prompts additional buying in the coming days.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlEngulfing(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
