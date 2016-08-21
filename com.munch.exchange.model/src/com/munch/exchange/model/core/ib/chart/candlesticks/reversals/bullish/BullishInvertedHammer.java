package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishInvertedHammer extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishInvertedHammer() {
		super();
	}

	public BullishInvertedHammer(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishInvertedHammer();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Inverted Hammer";
		
		this.icon = "icons/candlestick/bullish/reversal/inverted_hammer-gravestone_dogi.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. Small real body formed near the bottom of the price range.\n";
		this.note += "2. The upper shadow is no more than two times as long as the body.\n";
		this.note += "3. The lower shadow is small or nonexistent.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The long upper shadow and small real body at the bottom of the trading range are cause for concern\n";
		this.note += "by the bears. They wonder if this is the end of the downtrend and take measures to protect their\n";
		this.note += "gains. If the next day opens above the body of the Inverted Hammer, then expectations could be for\n";
		this.note += "offers the opportunity to exit short positions or initiate long positions.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlInvertedHammer(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
