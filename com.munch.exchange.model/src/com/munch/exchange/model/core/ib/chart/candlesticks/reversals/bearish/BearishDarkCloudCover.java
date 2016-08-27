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
public class BearishDarkCloudCover extends CandlesticksWithPenetration {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5379239025276748671L;

	public BearishDarkCloudCover() {
		super();
	}

	public BearishDarkCloudCover(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new BearishDarkCloudCover();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Bearish Dark Cloud Cover";
		
		this.icon = "icons/candlestick/bearish/reversal/dark_cloud_cover.gif";

		
		this.note = "How to identify:\n";
		this.note += "1. 1st day is a long white day\n";
		this.note += "2. 2nd day is a red day which opens above the 1st day's high.\n";
		this.note += "3. 2nd day closes within the 1st day, but below the midpoint.\n";
		this.note += "\n";
		
		this.note += "Psychology:\n";
		this.note += "A long white candlestick is formed on the 1st day and a gap up is created on the 2nd day. This is \n";
		this.note += "encouraging to the bulls. However, the 2nd day closes below the midpoint of the 1st day. Longs\n";
		this.note += "quickly question their strategy.";

	}

	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose,
			double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		RetCode retCode = lib.cdlDarkCloudCover(0, inClose.length-1, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
		for(int i = 0;i<outInteger.length;i++){
			if(outInteger[i]>0)outInteger[i]=0;
		}
//		
		return retCode;
	}




	
	
	
	

}
