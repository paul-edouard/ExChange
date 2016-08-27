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
public class BearishShootingStar extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishShootingStar() {
		super();
	}

	public BearishShootingStar(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishShootingStar();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bearish Shooting Star";
		
		this.icon = "icons/candlestick/bearish/reversal/shooting_star-gravestone_doji.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1.Price gap open to the upside.\n";
		this.note += "2. 2.Small real body formed near the bottom of the price range.\n";
		this.note += "3. The upper shadow at least twice as long as the body.\n";
		this.note += "4. The lower shadow is small or nonexistent.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The long upper shadow and small real body at the bottom of the trading range are cause for concern \n";
		this.note += "by the bulls. They wonder if this is the end of the uptrend and take measures to protect their gains.";
	
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlShootingStar(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
