package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishHaramiCross extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishHaramiCross() {
		super();
	}

	public BullishHaramiCross(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishHaramiCross();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Harami Cross";
		
		this.icon = "icons/candlestick/bullish/reversal/harami_cross.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. The 1st day is a long red day.\n";
		this.note += "2. The 2nd day is a doji day that is engulfed by the 1st day's body.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 2nd day's price range does not pierce the previous day's range and closes about where it opened.\n";
		this.note += "Volume on the 2nd day is low which indicates that traders are lacking enough information to decide\n";
		this.note += "whether to go long or short.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlHaramiCross(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
