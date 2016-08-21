package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishKicking extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishKicking() {
		super();
	}

	public BullishKicking(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishKicking();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Kicking";
		
		this.icon = "icons/candlestick/bullish/reversal/kicking.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a red Marubozu.\n";
		this.note += "2. 2nd day is a white Marubozu and gaps open above the 1st day's close.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap created by the 2nd day becomes a support area. Expect higher prices and for the gap to be\n";
		this.note += "tested before breaking back to the downside.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlKicking(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
