package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishUniqueThreeRiverBottom extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishUniqueThreeRiverBottom() {
		super();
	}

	public BullishUniqueThreeRiverBottom(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishUniqueThreeRiverBottom();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Unique Three River Bottom";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day.\n";
		this.note += "2. 2nd day is red forming a Homing Pigeon.\n";
		this.note += "3. 2nd day's low sets a new low.\n";
		this.note += "4. 3rd day is a short white day which is below the 2nd day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The 1st day's long red candlestick enforces the bears position. The next day a new low is set, however\n";
		this.note += "it closes higher than the 1st day. The 3rd day produces some indecision on the part of the bears. \n";
		this.note += "Watch for the reversal confirmation of a new high on the next day.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlUnique3River(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
