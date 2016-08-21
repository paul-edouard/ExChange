package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishHomingPigeon extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishHomingPigeon() {
		super();
	}

	public BullishHomingPigeon(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishHomingPigeon();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Homing Pigeon";
		
		this.icon = "icons/candlestick/bullish/reversal/homing_pigeon.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day.\n";
		this.note += "2. 2nd day is a short red day which is engulfed by the 1st day's range.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 2nd day shows a deterioration of the prior downtrend. If the prior downtrend is severe, then this\n";
		this.note += "offers the opportunity to exit short positions or initiate long positions.";

		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlHomingPigeon(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
