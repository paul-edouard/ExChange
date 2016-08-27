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
public class BearishThreeInsideDown extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishThreeInsideDown() {
		super();
	}

	public BearishThreeInsideDown(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishThreeInsideDown();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bearish Three Inside Down";
		
		this.icon = "icons/candlestick/bearish/reversal/three_inside_down.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st two days form a bearish harami.\n";
		this.note += "2. 3rd day closes lower than the 2nd day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This is the confirmation signal of the Bearish Harami formation.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3Inside(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
