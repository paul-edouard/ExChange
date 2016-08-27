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
public class BearishFallingThreeMethods extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishFallingThreeMethods() {
		super();
	}

	public BearishFallingThreeMethods(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishFallingThreeMethods();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Falling Three Methods";
		
		this.icon = "icons/candlestick/bearish/continuation/falling_three_methods.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day.\n";
		this.note += "2. Three small body candlesticks follow the 1st day. Each trends upward and closes within the range of the 1st day.\n";
		this.note += "3. The last day is a long red day and closes below the 1st day's close.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This is a formation which shows the market taking a breather before continuing it's downtrend. Notice \n";
		this.note += "that a new high is not seen during the 4 remaining days of this formation. This gives little confidence \n";
		this.note += "to the bulls, making way for the short sellers.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlRiseFall3Methods(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
