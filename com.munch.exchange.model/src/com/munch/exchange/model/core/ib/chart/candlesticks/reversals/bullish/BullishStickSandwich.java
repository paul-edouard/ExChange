package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishStickSandwich extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishStickSandwich() {
		super();
	}

	public BullishStickSandwich(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishStickSandwich();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Stick Sandwich";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is red.\n";
		this.note += "2. 2nd day is white and trades above the close of the 1st day.\n";
		this.note += "3. 3rd day is red with a close equal to the 1st day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The price action of the 2nd day suggests that the downtrend is over. The next day opens higher, but\n";
		this.note += "sells off to close at the close of the 1st day. The 3rd day's close can be viewed as closing at a short\n";
		this.note += "term support level. Watch for confirmation via a higher close the next day.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlStickSandwhich(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
