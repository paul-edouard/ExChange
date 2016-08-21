package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishThreeWhiteSoldiers extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishThreeWhiteSoldiers() {
		super();
	}

	public BullishThreeWhiteSoldiers(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishThreeWhiteSoldiers();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Three White Soldiers";
		
		this.icon = "icons/candlestick/bullish/reversal/three_white_soldiers.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. Three consecutive long white days with higher closes each day.\n";
		this.note += "2. Each day opens within the previous body.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This formation represent powerful unabated buying. Shorts would be wise to stay away from this\n";
		this.note += "steam engine. Longs should be prepared for a rest before proceeding on to higher prices.";

		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3WhiteSoldiers(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
