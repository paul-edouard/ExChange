package com.munch.exchange.model.analytic;


/*
 * 
 * http://manual.zorro-trader.com/stop
 * 
 */

public class Trade {
	
	
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
	 */
	double trailLock = 0;
	
	/*
	 * Automatically raise the stop loss every bar by a percentage of the difference between current
	 * asset price and current stop loss (default = 0 = no automatic trailing); has only an effect
	 * when Stop and Trail are set and the profit is above the trail distance. Example: A long position
	 * has a stop at USD 0.98 and the price is currently at USD 1.01. TrailStep = 10 will increase the
	 * stop loss by 0.003 (30 pips) at the next bar. TrailStep reduces the trade time dependent on the
	 * profit situation, and is often preferable to a fixed exit time with ExitTime. 
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
	 * 
	 */
	double trailSpeed = 1;

	public Trade(double volume) {
		super();
		this.volume = volume;
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
		
		this.stopLoss = stopInvest/volume;
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
	public void setCurrentPrice(double curPrice) {
		currentPrice = curPrice;
		
		if(profitTargetDistance>0){
//			Test if the profit target is reached
			if(currentPrice*position >= profitTarget*position){
				position = 0;
				return;
			}
			
		}
		
		if(stopLossDistance == 0)return;
		
		if(currentPrice*position <= stopLoss*position){
			position = 0;
			return;
		}
		
//		Reset the 
		if(trailDistance == 0)return;
		
//		Test if the trail is reached
		if(currentPrice*position >= trail*position){
			stopLoss = (currentPrice*position - trail*position)*trailSlope;
			
			
//			double trailStopDistance = (currentPrice*position - enterPrice*position)*trailSlope;
//			
//			trail = currentPrice;
//			stopLoss =trailStopDistance; 
			
			
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
