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
public class BearishThreeBlackCrows extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishThreeBlackCrows() {
		super();
	}

	public BearishThreeBlackCrows(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishThreeBlackCrows();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bearish Three Black Crows";
		
		this.icon = "icons/candlestick/bearish/reversal/three_black_crows.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. Three consecutive large body red days with lower closes each day.\n";
		this.note += "2. Each day opens within the body of the previous day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "Pervasive profit taking takes its toll on those who remain long. This induces a snowball selling effect in \n";
		this.note += "the coming days.";
	
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3BlackCrows(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
