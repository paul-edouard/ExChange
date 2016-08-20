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
public class BullishMatHold extends CandlesticksWithPenetration {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishMatHold() {
		super();
	}

	public BullishMatHold(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishMatHold();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Mat Hold";
		
		this.icon = "icons/candlestick/downside_gap_3_methods.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day.\n";
		this.note += "2. 2nd day is a red day that gaps above the 1st day.\n";
		this.note += "3. The next two days are small body days which trend lower and stay within the upper range of the 1st day.\n";
		this.note += "4. The last day is a white day which closes above the previous four day's range.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This is a resting pattern for the bulls. The 2nd day still closes at a new high and the 4th day still closes\n";
		this.note += "above the 1st day's open. Bears worry that a reversal in not in the cards this time. Hence, the bullish \n";
		this.note += "trend continues on the 5th day.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlMatHold(0, inClose.length-1, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
