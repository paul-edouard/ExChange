package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishPiercingLine extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishPiercingLine() {
		super();
	}

	public BullishPiercingLine(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishPiercingLine();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Piercing Line";
		
		this.icon = "icons/candlestick/bullish/reversal/piercing_line.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red body.\n";
		this.note += "2. 2nd day is a white body which opens below the low of the 1st day.\n";
		this.note += "3. 2nd day closes within, but above the midpoint of the 1st day's body.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The gap down on the 2nd day perpetuates the downtrend. However, the 2nd day's close is above the\n";
		this.note += "midpoint of the 1st day's body. This suggests to the bears that a bottom could be forming. This price\n";
		this.note += "action is not nearly as discernable using bar charts as it is with candlestick charts. The more\n";
		this.note += "penetration of the close on the 2nd day to the 1st day's body, the more probable the reversal signal\n";
		this.note += "will succeed.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlPiercing(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
