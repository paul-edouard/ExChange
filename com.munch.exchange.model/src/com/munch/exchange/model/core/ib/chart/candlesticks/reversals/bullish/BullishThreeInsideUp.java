package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishThreeInsideUp extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishThreeInsideUp() {
		super();
	}

	public BullishThreeInsideUp(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishThreeInsideUp();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Three Inside Up";
		
		this.icon = "icons/candlestick/bullish/reversal/three_inside_up.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st two days form a bullish harami.\n";
		this.note += "2. 3rd day closes higher than the 2nd day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This is the confirmation signal of the Bullish Harami formation.";

		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdl3Inside(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
