package com.munch.exchange.model.analytic;

import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.ib.bar.ExBar.DataType;

/*
 * 
 * http://manual.zorro-trader.com/stop
 * 
 */

public class Trade {
	
	public static enum TradeType {
		STOP_LOSS, TRAIL, TRAIL_LOCK, TRAIL_STEP;
		
		public static String[] toStringArray(){
			List<String> list=new LinkedList<String>();
			for(TradeType type:TradeType.values()){
				list.add(type.name());
			}
			return list.toArray(new String[list.size()]);
		}
		
		public static TradeType fromString(String string){
			if(string.equals(STOP_LOSS.name())){
				return STOP_LOSS;
			}
			else if(string.equals(TRAIL.name())){
				return TRAIL;
			}
			else if(string.equals(TRAIL_LOCK.name())){
				return TRAIL_LOCK;
			}
			else if(string.equals(TRAIL_STEP.name())){
				return TRAIL_STEP;
			}
			
			
			return STOP_LOSS;
			
		}
		
		
	}
	
	
	double enterPrice = 0;
	
	double currentPrice = 0;
	
	double volume = 0;
	
	/*
	 * if position > 0, then the current trade is long 
	 * if position < 0, then the current trade is short
	 * if position == 0, then thre is no position
	 */
	
	double position = 0;
	
	/*
	 * Stop loss value or stop loss distance in price units (default = 0 = no stop loss).
	 * The trade is closed when the price reaches the limit resp. the trade loss reaches the distance.
	 * A good value for Stop is derived from the ATR, f.i. 3*ATR(20).
	 * Setting a stop loss distance is recommended for risk control.
	 * 
	 */
	double stopLoss = 0;
	double stopLossDistance = 0;
	
	/*
	 * Profit target value or profit target distance in price units (default = 0 = no profit target).
	 * The trade is closed when the trade profit has reached this amount.
	 * A profit target takes profits early, which increases the number of winning trades,
	 * but normally reduces the overall profit of a strategy.
	 * It is preferable to use TrailLock instead of setting a profit target.
	 */
	double profitTarget = 0;
	double profitTargetDistance = 0;
	
	/*
	 * Raise the stop loss value as soon as the price reaches the given value,
	 * resp. goes in favorable direction by the given distance in price units (default = 0 = no trailing).
	 * Has only an effect when a Stop is set. The stop loss is increased in a long position,
	 * and decreased in a short position so that it normally follows the price at the distance given by the sum of the Stop and Trail distance .
	 * A slower or faster 'movement speed' of the stop loss can be set through TrailSlope. 
	 */
	double trail = 0;
	double trailDistance = 0;
	
	/*
	 * Trailing 'speed' in percent of the asset price change (default = 100%);
	 * has only an effect when Stop and Trail are set and the profit is above the trail distance.
	 * Example: The asset price of a long position goes up by 10 pips. TrailSlope = 50 
	 * would then raise the stop loss by 5 pips. TrailSlope = 200 would raise the stop loss by 20 pips.
	 * 
	 * 0.1 <= trailSlope < 2
	 * 
	 */
	double trailSlope = 1;
	
	
	/*
	 *'Locks' a percentage of the profit (default = 0 = no profit locking);
	 *has only an effect when Stop and Trail are set and the price has exceeded the trail distance.
	 *A stop loss is then automatically placed at the given percentage of the current price excursion.
	 *Example: A long position is currently in profit by 20 pips above the entry price.
	 *TrailLock = 80  would then place the stop loss at 16 pips above entry,
	 *thus locking 80% of the profit (without trading costs). TrailLock = 1 (or any small number)
	 *would set the stop loss at the entry price when the current price reaches the Trail value.
	 *Using TrailLock is in most cases preferable to setting a profit target.  
	 * 
	 * 0 <= trailLock < 1
	 * 
	 */
	double trailLock = 0;
	boolean isTrailLocked = false;
	
	/*
	 * Automatically raise the stop loss every bar by a percentage of the difference between current
	 * asset price and current stop loss (default = 0 = no automatic trailing); has only an effect
	 * when Stop and Trail are set and the profit is above the trail distance. Example: A long position
	 * has a stop at USD 0.98 and the price is currently at USD 1.01. TrailStep = 10 will increase the
	 * stop loss by 0.003 (30 pips) at the next bar. TrailStep reduces the trade time dependent on the
	 * profit situation, and is often preferable to a fixed exit time with ExitTime. 
	 * 
	 *  0 <= trailStep < 1
	 * 
	 */
	double trailStep = 0;
	
	/*
	 * Speed factor for faster raising the stop loss before break even, in percent (default = 100%).
	 * Has only an effect when Stop and Trail are set and the profit is above the trail distance.
	 * Example: TrailSpeed = 300 will trail the stop loss with triple speed until the entry price
	 * plus spread is reached, then continue trailing with normal speed given by TrailSlope and TrailStep.
	 * Has no effect on TrailLock. This parameter can prevent that a winning trade with a slow rising stop
	 * turns into a loser. 
	 * 
	 * 1 <= trailSpeed < 3
	 * 
	 */
	double trailSpeed = 1;

	/**
	 * 
	 * @param volume	the volume of the entry
	 * @param type		the type of the stop management
	 * @param stopLossDistance	the stop loss distance in $
	 * @param trailDistance		the trail distance in $
	 * @param param_percent		the first parameter use for the optimization 0 < param < 1
	 * @param trailSpeed		the speed 0 <= speed <3
	 */
	public Trade(double volume, TradeType type,
			double stopLossDistance,double trailDistance,
			double param_percent, double trailSpeed) {
		super();
		this.volume = volume;
		
		this.stopLossDistance = stopLossDistance;
		if(type!=TradeType.STOP_LOSS){
			this.trailDistance = trailDistance;
			
			if(type==TradeType.TRAIL_LOCK){
				this.trailLock = param_percent;
			}
			else if(type==TradeType.TRAIL_STEP){
				this.trailStep = param_percent;
			}
			else{
				this.trailSlope = 2*param_percent;
			}
			
			this.trailSpeed = trailSpeed;
			
		}
		
	}
	
	
	public void enterLong(double price){
		enterPrice = price;
		position = 1;
		
		setProfitTarget();
		
		setStopLoss();
		setTrail();
	}
	
	public void enterShort(double price){
		enterPrice = price;
		position = -1;
		
		setProfitTarget();
		
		setStopLoss();
		setTrail();
	}
	
	private void setStopLoss(){
		if(stopLossDistance==0)return;
		
		double startInvest = enterPrice * volume;
		double stopInvest = startInvest - stopLossDistance*position;
		
		this.stopLoss = stopInvest/volume;
	}
	
	private void setTrail(){
		if(stopLossDistance==0)return;
		if(trailDistance==0)return;
		
		double startInvest = enterPrice * volume;
		double stopInvest = startInvest + trailDistance*position;
		
		this.trail = stopInvest/volume;
	}
	
	private void setProfitTarget(){
		if(profitTargetDistance==0)return;
		
		double startInvest = enterPrice * volume;
		double stopInvest = startInvest + profitTargetDistance*position;
		
		this.stopLoss = stopInvest/volume;
	}
	

	public void setProfitTarget(double profitTarget) {
		this.profitTarget = profitTarget;
	}


	public void setTrailSlop(double trailSlop) {
		this.trailSlope = trailSlop;
	}

	public void setTrailLock(double trailLock) {
		this.trailLock = trailLock;
	}

	public void setTrailStep(double trailStep) {
		this.trailStep = trailStep;
	}

	public void setTrailSpeed(double trailSpeed) {
		this.trailSpeed = trailSpeed;
	}

	public double getPosition() {
		return position;
	}

	
	
	public void setStopLossDistance(double stopLossDistance) {
		this.stopLossDistance = stopLossDistance;
	}


	public void setTrailDistance(double trailDistance) {
		this.trailDistance = trailDistance;
	}


	public void setProfitTargetDistance(double profitTargetDistance) {
		this.profitTargetDistance = profitTargetDistance;
	}


	/**
	 * Set the current price
	 * @param currentPrice
	 */
	public double setCurrentPrice(double curPrice) {
		currentPrice = curPrice;
		if(position == 0)return position;
		
		if(profitTargetDistance>0){
//			Test if the profit target is reached
			if(currentPrice*position >= profitTarget*position){
				position = 0;
				return position;
			}	
		}
		
		if(stopLossDistance == 0)return position;
		
		if(currentPrice*position <= stopLoss*position){
			position = 0;
			return position;
		}
		
//		System.out.println("stopLossDistance = "+stopLossDistance);
//		System.out.println("stopLoss = "+stopLoss);
		
//		Reset the 
		if(trailDistance == 0){
			if(currentPrice*position <= stopLoss*position)
				position = 0;
			return position;
		}
		
//		Test if the trail is reached
//		System.out.println("Trail distance: "+trailDistance);
//		System.out.println("Trail: "+trail+", Stop loss: "+stopLoss);
		
		if(currentPrice*position >= trail*position){
//			System.out.println("Trail lock: "+trailLock);
			if(trailLock>0){
				if(!isTrailLocked){
					double diff = (currentPrice - enterPrice)*trailLock;
					stopLoss = enterPrice+diff;
					isTrailLocked = true;
				}
			}
			else if(trailStep > 0){
				double diff = (currentPrice - stopLoss)*trailStep;
//				System.out.println("Diff: "+diff);
				if(stopLoss*position < enterPrice*position){
					diff *= trailSpeed ;
				}
//				System.out.println("Diff2: "+diff);
				stopLoss += diff;
			}
			else{
				double diff = (currentPrice - trail)*trailSlope;
//				System.out.println("Diff: "+diff);
				if(stopLoss*position < enterPrice*position){
					diff *= trailSpeed ;
				}
//				System.out.println("Diff2: "+diff);
				trail = currentPrice;
				stopLoss += diff;
			}
						
		}
		
//		System.out.println("-> New stopLoss = "+stopLoss);
//		System.out.println("New Trail: "+trail+", New Stop loss: "+stopLoss);
		
		if(currentPrice*position <= stopLoss*position){
			position = 0;
			return position;
		}
	
		
		
		return position;
	}
	
	
	
	
	public static void main(String[] args){
		
		double[] serie = {1,1.1,1.1,1,0.8,0.7,0.8,0.9,1.0};
		
		double volume = 1;
		TradeType type = TradeType.TRAIL_LOCK;
		
		double stopLossDistance = 0.2;
		double trailDistance = 0.05;
		double param_percent =  0.8;
		double trailSpeed = 1.1;
		
		Trade trade = new Trade(volume, type, stopLossDistance, trailDistance, param_percent, trailSpeed);
		trade.enterShort(serie[0]);
		for(int i = 0;i<serie.length;i++){
			System.out.println("\nValue: "+serie[i]);
			double position = trade.setCurrentPrice(serie[i]);
			
			System.out.println("> Position: "+position);
		}
		
		
	}
	
	
	

}
