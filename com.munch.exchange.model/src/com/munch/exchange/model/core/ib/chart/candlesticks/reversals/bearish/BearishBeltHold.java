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
public class BearishBeltHold extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishBeltHold() {
		super();
	}

	public BearishBeltHold(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishBeltHold();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Belt Hold";
		
		this.icon = "icons/candlestick/bearish/reversal/belt_hold.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. Long red day where the open is equal to the high.\n";
		this.note += "2. No upper shadow.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "A significant gap up occurs. The remaining price action for the day occurs to the downside. This \n";
		this.note += "triggers new short positions to be taken. Concern over this price action re-enforces the selling.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlBeltHold(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
