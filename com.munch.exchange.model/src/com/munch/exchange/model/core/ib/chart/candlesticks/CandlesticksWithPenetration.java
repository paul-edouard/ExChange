package com.munch.exchange.model.core.ib.chart.candlesticks;

import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public abstract class CandlesticksWithPenetration extends Candlesticks {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1254390298354044630L;
	public static final String PARAM_PENETATION="Penetration";


		
	public CandlesticksWithPenetration() {
		super();
	}

	public CandlesticksWithPenetration(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public void createParameters() {
		this.parameters.add(new IbChartParameter(this,PARAM_PENETATION,ParameterType.DOUBLE, 0.1, 0.00, 2, 2));
	}

		
	@Override
	protected RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger){
		double optInPenetration = this.getChartParameter(PARAM_PENETATION).getValue();
		return callCdlStickFunction(lib, inOpen, inHigh, inLow, inClose, optInPenetration, outBegIdx, outNBElement, outInteger);
	}
	
	protected abstract RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose, double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger);
	
	
	
	

}
