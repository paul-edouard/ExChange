package com.munch.exchange.model.core.ib.chart.candlesticks.continuation.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.munch.exchange.model.core.ib.chart.candlesticks.CandlesticksWithPenetration;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishUpsideGapThreeMethods extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishUpsideGapThreeMethods() {
		super();
	}

	public BullishUpsideGapThreeMethods(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishUpsideGapThreeMethods();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Upside Gap Three Methods";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st two day are long white days with a gap between them.\n";
		this.note += "2. 3rd day is a red day that fills the gap of the 1st two days.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap up on the 2nd day gets filled by the 3rd day. More investigation of the previous weeks is\n";
		this.note += "recommended in order to see if this is the first gap. If so, then this pattern is probably displaying short\n";
		this.note += "selling to 'close the gap' created and the bullish trend should continue.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlXSideGap3Methods(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
