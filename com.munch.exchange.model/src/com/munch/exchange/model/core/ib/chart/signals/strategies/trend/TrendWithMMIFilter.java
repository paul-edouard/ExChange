package com.munch.exchange.model.core.ib.chart.signals.strategies.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.SwissArmyKnifeIndicator;
import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.MMI;
import com.munch.exchange.model.analytic.indicator.trend.AverageDirectionalMovementIndexWilder;
import com.munch.exchange.model.analytic.indicator.trend.BollingerBands;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.Resistance;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;

/**
 * 
 * Trade parameters 1 - entry and exit limits 
Entry
Enter the trade only when the price reaches a certain value at the next bar (default = 0 = enter at market). The value can be given either directly as an Ask price, or as a distance to the current close price. A positive price or distance constitutes an entry stop, a negative price or distance an entry limit. An entry limit buys when the price moves against the trade direction and reaches or crosses the limit. It increases the profit as it buys at a price that went in opposite direction to the trade. An entry stop buys when the price moves in trade direction and reaches or crosses the limit; it reduces the profit, but enters only when the price moved in favorable direction, and thus acts as an additional trade filter. For a long trade, an entry limit must be below and an entry stop must be above the current price. If the entry price is not reached within the allowed time period (set through EntryTime), the trade is cancelled and a "Missed Entry" message is printed to the log file. 

Stop
Stop loss value or stop loss distance in price units (default = 0 = no stop loss). The trade is closed when the price reaches the limit resp. the trade loss reaches the distance. A good value for Stop is derived from the ATR, f.i. 3*ATR(20). Setting a stop loss distance is recommended for risk control. 

StopFactor
Distance factor between the real stop loss, and the stop loss sent to the broker to act as a 'safety net' in case of a computer crash. At the default value 1.5 the stop sent to the broker is 50% more distant, thus preventing 'stop hunting' or similar broker practices. At 0, or if the NFA flag is set, the stop loss is only controlled by software; at 1.0 the broker stop is identical to the real stop. If StopFactor is set to a negative value or if a BrokerStop function is not available in the broker plugin, the broker stop is only placed at opening the trade, but not updated afterwards. StopFactor has no effect on trades that have no stop, such as pool trades in Virtual Hedging Mode. 

TakeProfit
Profit target value or profit target distance in price units (default = 0 = no profit target). The trade is closed when the trade profit has reached this amount. A profit target takes profits early, which increases the number of winning trades, but normally reduces the overall profit of a strategy. It is preferable to use TrailLock instead of setting a profit target. 

Trail
Raise the stop loss value as soon as the price reaches the given value, resp. goes in favorable direction by the given distance in price units (default = 0 = no trailing). Has only an effect when a Stop is set. The stop loss is increased in a long position, and decreased in a short position so that it normally follows the price at the distance given by the sum of the Stop and Trail distance . A slower or faster 'movement speed' of the stop loss can be set through TrailSlope. 

TrailSlope
Trailing 'speed' in percent of the asset price change (default = 100%); has only an effect when Stop and Trail are set and the profit is above the trail distance. Example: The asset price of a long position goes up by 10 pips. TrailSlope = 50 would then raise the stop loss by 5 pips. TrailSlope = 200 would raise the stop loss by 20 pips. 

TrailLock
'Locks' a percentage of the profit (default = 0 = no profit locking); has only an effect when Stop and Trail are set and the price has exceeded the trail distance. A stop loss is then automatically placed at the given percentage of the current price excursion. Example: A long position is currently in profit by 20 pips above the entry price. TrailLock = 80 would then place the stop loss at 16 pips above entry, thus locking 80% of the profit (without trading costs). TrailLock = 1 (or any small number) would set the stop loss at the entry price when the current price reaches the Trail value. Using TrailLock is in most cases preferable to setting a profit target. 

TrailStep
Automatically raise the stop loss every bar by a percentage of the difference between current asset price and current stop loss (default = 0 = no automatic trailing); has only an effect when Stop and Trail are set and the profit is above the trail distance. Example: A long position has a stop at USD 0.98 and the price is currently at USD 1.01. TrailStep = 10 will increase the stop loss by 0.003 (30 pips) at the next bar. TrailStep reduces the trade time dependent on the profit situation, and is often preferable to a fixed exit time with ExitTime. 

TrailSpeed
Speed factor for faster raising the stop loss before break even, in percent (default = 100%). Has only an effect when Stop and Trail are set and the profit is above the trail distance. Example: TrailSpeed = 300 will trail the stop loss with triple speed until the entry price plus spread is reached, then continue trailing with normal speed given by TrailSlope and TrailStep. Has no effect on TrailLock. This parameter can prevent that a winning trade with a slow rising stop turns into a loser. 
Type:
var 
Remarks:
All parameters above must be set before calling enterLong / enterShort. They have no effect on already entered trades. For changing a stop or profit target of a particular trade after it has already been opened, use either a TMF, or call exit with the new price limit (see example). 
All prices are Ask prices, regardless of whether they are intended for entry or exit, or for a long or a short trade. A trade is opened or closed when the Ask price reaches the given target. Zorro automatically handles the conversion from Ask to Bid: long trades are filled at the Ask price and closed at the Bid price, short trades are filled at the Bid price and closed at the Ask price. The Bid price is the Ask price minus the Spread. 
Either an (absolute) price, or a (relative) distance to the trade opening price (TradePriceOpen) can be used for Stop, TakeProfit, Trail, and Entry. If the value is less than half the asset price, Zorro assumes that it's a distance, otherwise it's a price. For an entry limit the given price must be negative; "buy at 10 pips below the current Low" is in code: Entry = -(priceLow()-10*PIP);. If Stop or TakeProfit are at the wrong side of the price, no trade is entered, but a "Skipped" message is printed in the log file. 
For setting prices or distances in pip units, multiply the pip amount with the PIP variable (f.i. Stop = 5*PIP;). For adapting distances to the price volatility, set them not to fixed pip values, but use a multiple of ATR(..) - this often improves the performance. 
TrailSlope, TrailStep, and TrailLock can be set simultaneously. The stop loss is raised by all of them. If a more complex stop loss / take profit behavior is required than provided by the trail parameters, use a TMF. The TMF runs at every price quote - resp. every tick - and can trail and check stop and profit limits. 
When Entry, TakeProfit, and/or Stop are used at the same time, or when a TMF is used, the test should run in TICKS mode for better precision (training can normally be run without TICKS for speed reasons). 
When absolute price limits are given for Trail, TakeProfit, Entry, or Stop, they should be at the correct side of the current market price with sufficient distance. Limits at the wrong side of the price are accepted, but cause unfavorable trades. When the Entry condition is already met at entering (f.i. a long trade is entered with the market price already below the entry limit), the trade will be opened immediately at either the market price or the entry price, whatever is worse. When the Stop limit is already met at entering, the trade will be opened and immediately closed, losing slippage and spread. When during the trade the Stop is moved inside the price range of the current bar or tick, the trade will be immediately closed. If a trade lasts only one or a few ticks, the result by stop or trailing can become inaccurate by a large factor due to the limited resolution of the price history. 
All stop, profit, trail, or entry limits are handled by software and controlled at each tick. They are not sent to the broker's server (except for the 'safety net' stop given by StopFactor) and thus not visible to the broker, this way preventing "stop hunting" or similar practices. This also steps around NFA Compliance Rule 2-43(b) that does not allow US citizens to place stop or profit targets. 
A stop loss - regardless if it is handled by software or sent to the broker - is no abolute guarantee to limit losses. In the case of price shocks (such as the EUR/CHF price cap removal in January 2015) trades can sometimes not be closed at the stop loss limit. The loss of the trade can then be remarkably higher. 
Stop limits updated to the broker can appear to temporarily move in 'wrong' direction by a small distance. This can happen when the spread changes at the same time, but in opposite direction as the price. 
When Zorro runs unobserved, stop loss limits should always be used, even when they appear to reduce the strategy performance. They protect against price shocks. The trade engine also uses them for calculating the risk per trade; without a stop, the capital exposure is not available and the performance statistics are less precise. 
The win rate ("accuracy") of a system highly depends on the Stop/Takeprofit ratio (also called risk/reward ratio). An example for using this effect in a strategy can be found in workshop 8. 

 * 
 * @author paul-edouard
 *
 */

@Entity
public class TrendWithMMIFilter extends IbChartSignal {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7458484327940227581L;

	public static final String SERIE_TREND="Trend";
	
	public static final String SERIE_MMI="Market Meanness Index";
	public static final String SERIE_MMI_SMOOTH="Market Meanness Index Smooth";
	
	public static final String PARAM_TREND_PERIOD="Trend Priod";
	public static final String PARAM_MMI_PERIOD="MMI Period";
	public static final String PARAM_MMI_SMOOTH_PERIOD="MMI Smooth Period";

	

	public TrendWithMMIFilter() {
		super();
	}

	public TrendWithMMIFilter(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new TrendWithMMIFilter();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "Trend with MMI Filter";
	}

	@Override
	public void createParameters() {
		
//		TREND PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_TREND_PERIOD,ParameterType.INTEGER, 50, 1, 500, 0));
		
//		MMI PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_MMI_PERIOD,ParameterType.INTEGER, 30, 1, 500, 0));
		
//		MMI SMOOTH PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_MMI_SMOOTH_PERIOD,ParameterType.INTEGER, 50, 1, 500, 0));
		
	}
	
	
	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_TREND,RendererType.MAIN,true,true,50, 44, 89));
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MMI,RendererType.SECOND,false,false,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MMI_SMOOTH,RendererType.SECOND,false,false,50, 244, 189));
				
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		int param1 = this.getChartParameter(PARAM_TREND_PERIOD).getIntegerValue();
		int param2 = this.getChartParameter(PARAM_MMI_PERIOD).getIntegerValue();
		int param3 = this.getChartParameter(PARAM_MMI_SMOOTH_PERIOD).getIntegerValue();
		validAtPosition=Math.max(param1, validAtPosition);
		validAtPosition=Math.max(param2, validAtPosition);
		validAtPosition=Math.max(param3, validAtPosition);
		
		return validAtPosition;
	}
	
	

	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		
		
//		Step 1: Read the parameters
		long[] times=getTimeArrayFromBar(bars);
		double[] close=getDataFromBars(bars, DataType.CLOSE);
//		double[] high=getDataFromBars(bars, DataType.HIGH);
//		double[] low=getDataFromBars(bars, DataType.LOW);
		
		int trend_period=this.getChartParameter(PARAM_TREND_PERIOD).getIntegerValue();
		int mmi_period=this.getChartParameter(PARAM_MMI_PERIOD).getIntegerValue();
		int mmi_smooth_period=this.getChartParameter(PARAM_MMI_SMOOTH_PERIOD).getIntegerValue();
		
		double[] trend = SwissArmyKnifeIndicator.Gauss(close, trend_period);
		
		double[] mmi = MMI.compute(close, mmi_period);
		double[] mmi_smooth = SwissArmyKnifeIndicator.Gauss(mmi, mmi_smooth_period);
		
		
		
		/*
		double[] signal=new double[close.length];
		for(int i=0;i<close.length;i++){
			signal[i] = signal_buy[i] + signal_sell[i];
		}
		
		
		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, signal,getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_MAX_RES_LINE, reset, times, maxResLine, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_MIN_RES_LINE, reset, times, minResLine, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_MAX_BREAKOUT_VALUE, reset, times, maxBreakout, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_MIN_BREAKOUT_VALUE, reset, times, minBreakout, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_BB_TOP_LINE, reset, times, BB_Top_Line, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_BB_BOTTOM_LINE, reset, times, BB_Bottom_Line, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_CONTROL_POS, reset, times, control_buy, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_CONTROL_NEG, reset, times, control_sell, getValidAtPosition());
		*/
		
	}
	
	
	private double[][] createBuyAndControlSignal(double[] close, double[] low,
			double[] maxBreakout,
			double[] maxResLine, double[] minResLine,
			double[] BB_Top_Line,  double[] BB_Bottom_Line,
			double risk, double profitRisk_Factor){
		
		double[][] BC=new double[2][close.length];
		
		double[] signal=new double[close.length];
		double[] control=new double[close.length];
		
		boolean upBreakoutActivated = false;
		boolean upBBTopLineEntryActivated = false;
		boolean upBBTopLineExitActivated = false;
		
		boolean isTrading = false;
		
		double newHighValue = 0;
		double breakOutValue = 0;
		double exitLimit = 0;
		double stopLoss = 0;
		double stopLossMinResDist = 0;
		
		for(int i=1;i<close.length;i++){
			control[i]= control[i-1];
			
			if(!isTrading){
			
//			The breakout is activated
			if(maxBreakout[i] > 0 && upBreakoutActivated == false){
				upBreakoutActivated = true;
				breakOutValue = maxResLine[i];
				control[i] = 1;
			}
			if(!isTrading && upBreakoutActivated == true && maxResLine[i] < breakOutValue){
//				System.out.println("maxBreakout[i]: "+maxResLine[i]);
				upBreakoutActivated = false;
				upBBTopLineEntryActivated = false;
				upBBTopLineExitActivated = false;
				isTrading = false;
				newHighValue = 0;
				control[i] = 0;
				continue;
			}
			
			
//			Save the new high position
			if(newHighValue == 0 && upBreakoutActivated && maxResLine[i] > breakOutValue){
//				newHighValue = maxResLine[i];
				newHighValue = breakOutValue;
			}
			
//			The close price enter for the first time into the Bollinger Band
			if(upBreakoutActivated == true &&
					low[i-1] >= BB_Top_Line[i] &&  low[i] <= BB_Top_Line[i] &&
					upBBTopLineEntryActivated == false && newHighValue > 0){
				upBBTopLineEntryActivated = true;
				control[i] = 2;
			}
			
//			The price exit the Bollinger bands
			if(upBBTopLineEntryActivated &&
					low[i-1] <= BB_Top_Line[i] &&  low[i] > BB_Top_Line[i] &&
					upBBTopLineExitActivated == false){
				upBBTopLineExitActivated = true;
				control[i] = 3;
				continue;
			}
			
			if(upBBTopLineExitActivated && low[i] > BB_Top_Line[i]){
//				The price is above the last new high
				if(close[i] > newHighValue){
					control[i] = 4;
					isTrading = true;
					signal[i] = 1.0;
					stopLoss = close[i]-risk;
					stopLossMinResDist = stopLoss - minResLine[i];
					exitLimit = close[i] + risk*profitRisk_Factor;
				}
//				False Signal reset all to false and wait for the next signal
				else{
					upBreakoutActivated = false;
					upBBTopLineEntryActivated = false;
					upBBTopLineExitActivated = false;
					isTrading = false;
					newHighValue = 0;
					control[i] = 0;
					continue;
				}
			}
			
			
			
//			The price is goes out of the Bollinger Bands but from the wrong side
			if(upBBTopLineEntryActivated && low[i] <= BB_Bottom_Line[i]){
				upBreakoutActivated = false;
				upBBTopLineEntryActivated = false;
				upBBTopLineExitActivated = false;
				isTrading = false;
				newHighValue = 0;
				control[i] = 0;
				continue;				
			}
			}
			
			if(isTrading){
				stopLoss = minResLine[i] + stopLossMinResDist;
//				stopLoss = minResLine[i];
				if(stopLoss < close[i] && close[i] < exitLimit /*&& close[i] >= BB_Bottom_Line[i]*/){
					signal[i] = 1.0;
					control[i] = 4;
				}
				else{
					upBreakoutActivated = false;
					upBBTopLineEntryActivated = false;
					upBBTopLineExitActivated = false;
					isTrading = false;
					newHighValue = 0;
					control[i] = 0;
					continue;
				}
				
			}
			
			
		}
		
		BC[0]=signal;
		BC[1]=control;
		
		
		return BC;
	}
	
	private double[][] createSellAndControlSignal(double[] close, double[] high,
			double[] minBreakout,
			double[] maxResLine, double[] minResLine,
			double[] BB_Top_Line,  double[] BB_Bottom_Line,
			double risk, double profitRisk_Factor){
		
		double[][] BC=new double[2][close.length];
		
		double[] signal=new double[close.length];
		double[] control=new double[close.length];
		
		boolean downBreakoutActivated = false;
		boolean downBBTopLineEntryActivated = false;
		boolean downBBTopLineExitActivated = false;
		
		boolean isTrading = false;
		
		double newLowValue = 0;
		double breakOutValue = 0;
		double exitLimit = 0;
		double stopLoss = 0;
		double stopLossMaxResDist = 0;
		
		for(int i=1;i<close.length;i++){
			control[i]= control[i-1];
			
			if(!isTrading){
			
//			The breakout is activated
			if(minBreakout[i] > 0 && downBreakoutActivated == false){
				downBreakoutActivated = true;
				breakOutValue = minResLine[i];
				control[i] = 1;
			}
			if(!isTrading && downBreakoutActivated == true && minResLine[i] > breakOutValue){
//				System.out.println("maxBreakout[i]: "+maxResLine[i]);
				downBreakoutActivated = false;
				downBBTopLineEntryActivated = false;
				downBBTopLineExitActivated = false;
				isTrading = false;
				newLowValue = 0;
				control[i] = 0;
				continue;
			}
			
			
//			Save the new high position
			if(newLowValue == 0 && downBreakoutActivated && minResLine[i] < breakOutValue){
//				newLowValue = minResLine[i];
				newLowValue = breakOutValue;
			}
			
//			The close price enter for the first time into the Bollinger Band
			if(downBreakoutActivated == true &&
					high[i-1] <= BB_Bottom_Line[i] &&  high[i] >= BB_Bottom_Line[i] &&
					downBBTopLineEntryActivated == false && newLowValue > 0){
				downBBTopLineEntryActivated = true;
				control[i] = 2;
			}
			
//			The price exit the Bollinger bands
			if(downBBTopLineEntryActivated &&
					high[i-1] >= BB_Bottom_Line[i] &&  high[i] < BB_Bottom_Line[i] &&
					downBBTopLineExitActivated == false){
				downBBTopLineExitActivated = true;
				control[i] = 3;
				continue;
			}
			
			if(downBBTopLineExitActivated &&  high[i] < BB_Bottom_Line[i]){
//				The price is above the last new high
				if(close[i] < newLowValue){
					control[i] = 4;
					isTrading = true;
					signal[i] = -1.0;
					stopLoss = close[i]+risk;
					stopLossMaxResDist = stopLoss - maxResLine[i];
					exitLimit = close[i] - risk*profitRisk_Factor;
					continue;
				}
//				False Signal reset all to false and wait for the next signal
				else{
					downBreakoutActivated = false;
					downBBTopLineEntryActivated = false;
					downBBTopLineExitActivated = false;
					isTrading = false;
					newLowValue = 0;
					control[i] = 0;
					continue;
				}
			}
			
			
			
//			The price is goes out of the Bollinger Bands but from the wrong side
			if(downBBTopLineEntryActivated && high[i] >= BB_Top_Line[i]){
				downBreakoutActivated = false;
				downBBTopLineEntryActivated = false;
				downBBTopLineExitActivated = false;
				isTrading = false;
				newLowValue = 0;
				control[i] = 0;
				continue;				
			}
			}
			
			if(isTrading){
				stopLoss = maxResLine[i] + stopLossMaxResDist;
//				stopLoss = maxResLine[i];
				if(stopLoss > close[i] && close[i] > exitLimit /*&& close[i] >= BB_Bottom_Line[i]*/){
					signal[i] = -1.0;
					control[i] = 4;
				}
				else{
					downBreakoutActivated = false;
					downBBTopLineEntryActivated = false;
					downBBTopLineExitActivated = false;
					isTrading = false;
					newLowValue = 0;
					control[i] = 0;
					continue;
				}
				
			}
			
			
		}
		
		BC[0]=signal;
		BC[1]=control;
		
		
		return BC;
	}
	

	

}
