package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.munch.exchange.model.core.ib.chart.candlesticks.CandlesticksWithPenetration;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishMorningDojiStar extends CandlesticksWithPenetration {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BullishMorningDojiStar() {
		super();
	}

	public BullishMorningDojiStar(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishMorningDojiStar();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Morning Doji Star";
		
		this.icon = "icons/candlestick/bullish/reversal/morning_doji_star.gif";
		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long red day.\n";
		this.note += "2. 2nd day is a doji which gaps below the 1st day's close.\n";
		this.note += "3. 3rd day is a white day.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The bullishness of the doji star created on the 1st two days is confirmed with the 3rd day. If the\n";
		this.note += "penetration of the 3rd day is more than 50 percent, then this formation has a much better chance to\n";
		this.note += "succeed for the trader.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlMorningDojiStar(0, inClose.length-1, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}




	
	
	
	

}
