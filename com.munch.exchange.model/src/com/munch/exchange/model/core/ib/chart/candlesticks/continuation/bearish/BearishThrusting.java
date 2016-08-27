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
public class BearishThrusting extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishThrusting() {
		super();
	}

	public BearishThrusting(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishThrusting();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Thrusting";
		
		this.icon = "icons/candlestick/bearish/continuation/thrusting.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a red day.\n";
		this.note += "2. 2nd day is a white day which opens well below the low of the 1st day.\n";
		this.note += "3. 2nd day closes well into the body of the 1st day, but below the midpoint.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This formation underscores the lack of buyers. Even though the 2nd day is an up day, it's still unable \n";
		this.note += "to close above the midpoint of the previous day's body. This suggests that the downtrend will \n";
		this.note += "continue.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlThrusting(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
