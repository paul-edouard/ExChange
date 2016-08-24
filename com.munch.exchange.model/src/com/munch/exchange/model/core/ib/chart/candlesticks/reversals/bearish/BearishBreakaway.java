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
public class BearishBreakaway extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishBreakaway() {
		super();
	}

	public BearishBreakaway(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishBreakaway();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Breakaway";
		
		this.icon = "icons/candlestick/bearish/reversal/breakaway.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day\n";
		this.note += "2. 2nd day is a white day whose body gaps up.\n";
		this.note += "3. 3rd & 4th days close higher each day\n";
		this.note += "4. 5th day is a long red day that closes inside the gap created by the 1st and 2nd days\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The up trend is accelerated by a gap up. The next few days trend up, however start to run out of \n";
		this.note += "steam. The last day of the formation shows a breakdown and close below the previous 3 days, \n";
		this.note += "however the gap created on the 1st day remains unfilled. Since the gap is not filled and the trend is \n";
		this.note += "obviously deteriorating, this implies the reversal signal.";
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlBreakaway(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
