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
public class BearishAdvanceBlock extends Candlesticks {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishAdvanceBlock() {
		super();
	}

	public BearishAdvanceBlock(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishAdvanceBlock();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Advance Block";
		
		this.icon = "icons/candlestick/bearish/reversal/advance_block.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. Three consecutive white days with higher closes each day.\n";
		this.note += "2. Each day opens within the previous body.\n";
		this.note += "3. Each day displays deterioration of the upward move as shown with the long upper shadows on the 2nd and 3rd days.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "This formation is similar to the Bullish Three White Soldiers formation. However, the Bearish Advance \n";
		this.note += "Block chart alerts traders to the weakness of the upside price action since the close of the second and \n";
		this.note += "third days are significantly less than their highs.";
		
	}


	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlAdvanceBlock(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;

	}


	
	
	
	

}
