package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.candlesticks.Candlesticks;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class BullishHammer extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;

	
	public BullishHammer() {
		super();
	}

	public BullishHammer(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BullishHammer();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bullish Hammer";

		this.note ="How to identify:\n";
		this.note +="1. Small real body at the upper trading range.\n";
		this.note +="2. Color of the body is not important.\n";
		this.note +="3. Long lower shadow at least twice the length of the body.\n";
		this.note +="4. Little or no upper shadow.\n";
		this.note +="5. Previous trend should be bearish.\n";
		this.note +="\n";
		
		this.note +="Psychology\n";
		this.note += "As with any single candlestick, confirmation is required. The Bullish Hammer formation shows the\n";
		this.note +="price goes much lower than the open then closes near the opening price. This fact reduces the\n";
		this.note +="confidence of the bears. Ideally, a white real body Hammer with a higher open the following day could\n";
		this.note +="be a bullish signal for the days ahead.";
		
	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlHammer(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]<0)outInteger[i]=0;
		}
//		
		return retCode;
	}


	
	
	
	

}
