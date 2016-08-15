package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishLadderBottom extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishLadderBottom() {
		super();
	}

	public BullishLadderBottom(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishLadderBottom();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Ladder Bottom";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st three days are red days with lower opens and closes each day.\n";
		this.note += "2. 4th day is a red day with an upper shadow.\n";
		this.note += "3. The last day is white that opens above the body of the 4th day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "In a downtrend and after moving solidly down for three consecutive days, the bears feel in control. \n";
		this.note += "The 4th day prices trade near the open of the previous day, but close at another new low. This draws\n";
		this.note += "attention to the bears who realize that markets do not go down forever. If the next day opens higher,\n";
		this.note += "then shorts will lock in profits. And if volume is high, then a reversal has probably occurred.";

		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlLadderBottom(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
