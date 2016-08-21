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
public class BearishSideBySideWhiteLines extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishSideBySideWhiteLines() {
		super();
	}

	public BearishSideBySideWhiteLines(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishSideBySideWhiteLines();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Side-by-Side White Lines";
		
		this.icon = "icons/candlestick/bearish/continuation/side_by_side_white_lines.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a red day.\n";
		this.note += "2. 2nd day is a white day which gaps below the 1st day's open.\n";
		this.note += "3. 3rd day is a white day about the same size as the 2nd day, opening at about the same price.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 2nd and 3rd days are a failed attempt to rally. Shorts are basically taking profit here. The \n";
		this.note += "downtrend remains intact.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlGapSideSideWhite(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
