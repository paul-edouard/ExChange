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
public class BearishDojiStar extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishDojiStar() {
		super();
	}

	public BearishDojiStar(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishDojiStar();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Doji Star";
		
		this.icon = "icons/candlestick/bearish/reversal/doji_star.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day.\n";
		this.note += "2. 2nd day is a doji day that gaps above the 1st day.";
		this.note += "3. The doji shadows shouldn't be excessively long.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "The uptrend is in full force with a strong 1st day. All confidence built up by the bulls from the 1st day \n";
		this.note += "is destroyed when the 2nd day's gap up closes near its open. Profit takers will quickly appear if the \n";
		this.note += "next day opens lower.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlDojiStar(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
