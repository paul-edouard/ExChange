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
public class BearishHangingMan extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishHangingMan() {
		super();
	}

	public BearishHangingMan(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishHangingMan();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
	this.name="Bearish Hanging Man";
		
		this.icon = "icons/candlestick/bearish/reversal/hanging_man-dragonfly_doji.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. Small real body at the upper trading range.\n";
		this.note += "2. Color of the body is not important.\n";
		this.note += "3. Long lower shadow at least twice the length of the body.\n";
		this.note += "4. Little or no upper shadow.\n";
		this.note += "5. Previous trend should be bullish.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "As with any single candlestick, confirmation is required. The Hanging Man formation shows the price \n";
		this.note += "goes much lower than the open then closes near the opening price. This could mean that many longs\n";
		this.note += "have positions that they are attempting to sell. Ideally, a red real body Hanging Man with a lower \n";
		this.note += "open the following day could be a bearish signal for the days ahead.";

		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlHangingMan(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
