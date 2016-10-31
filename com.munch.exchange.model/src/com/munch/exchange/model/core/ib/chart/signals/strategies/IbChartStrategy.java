package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.List;

import com.munch.exchange.model.analytic.Trade;
import com.munch.exchange.model.analytic.Trade.TradeType;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;

public abstract class IbChartStrategy extends IbChartSignal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -356638687120958290L;
	
	
	private Trade trade ;
	
//	public static final String SERIE_STOP_LOSS="Stop Loss";
	
	public static final String PARAM_TRADE_TYPE="Trade Type";
	public static final String PARAM_STOP_LOSS_DISTANCE="Stop loss distance";
	public static final String PARAM_TRAIL_DISTANCE="Trail distance";
	public static final String PARAM_PERCENT="Stop loss percent";
	public static final String PARAM_TAIL_SPEED="Trail Speed";
	
	public IbChartStrategy() {
		super();
	}

	public IbChartStrategy(IbChartIndicatorGroup group) {
		super(group);
	}

	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		String tradeType=this.getChartParameter(PARAM_TRADE_TYPE).getStringValue();
		TradeType type = TradeType.fromString(tradeType);
		
		double volume = (double) this.getVolume();
		
		double stopLossDistance = this.getChartParameter(PARAM_STOP_LOSS_DISTANCE).getValue();
		double trailDistance 	= this.getChartParameter(PARAM_TRAIL_DISTANCE).getValue();
		
		double param_percent 	=  this.getChartParameter(PARAM_PERCENT).getValue();
		double trailSpeed 		= this.getChartParameter(PARAM_TAIL_SPEED).getValue();
		
		trade = new Trade(volume, type, stopLossDistance, trailDistance, param_percent, trailSpeed);
		
		computeSignalPoint(bars, reset, trade);
	}
	
	public abstract void computeSignalPoint(List<ExBar> bars, boolean reset, Trade trade );
	


	@Override
	public void createParameters() {
		
//		TRADE TYPE
		IbChartParameter param=new IbChartParameter(this, PARAM_TRADE_TYPE,TradeType.STOP_LOSS.name(), TradeType.toStringArray());
		this.parameters.add(param);
		
//		STOP LOSS DISTANCE
		this.parameters.add(new IbChartParameter(this, PARAM_STOP_LOSS_DISTANCE,ParameterType.DOUBLE, 10, 2, 300, 1));

//		TAIL DISTANCE
		this.parameters.add(new IbChartParameter(this, PARAM_TRAIL_DISTANCE,ParameterType.DOUBLE, 10, 2, 300, 1));

//		PERCENT
		this.parameters.add(new IbChartParameter(this, PARAM_PERCENT,ParameterType.DOUBLE, 0.5, 0.001, 1, 3));

//		TAIL SPEED
		this.parameters.add(new IbChartParameter(this, PARAM_TAIL_SPEED,ParameterType.DOUBLE, 1, 1, 3, 2));

	}


	
	
	

}
