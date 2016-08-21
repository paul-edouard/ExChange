package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishBeltHold extends Candlesticks {


	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8156691935439548373L;
	

	public BullishBeltHold() {
		super();
	}

	public BullishBeltHold(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishBeltHold();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Belt Hold";
		
		this.icon = "icons/candlestick/bullish/reversal/belt_hold.gif";
		
		this.note ="How to identify:\n";
		this.note +="1. Long white day where the open is equal to the low.\n";
		this.note +="2. No lower shadow.\n";
		this.note +="\n";
		
		this.note +="Psychology\n";
		this.note += "A significant gap down occurs. The remaining price action for the day occurs to the upside. This\n";
		this.note +="triggers a buying spree. Shorts cover their positions due to concern over this price action.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlBeltHold(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
