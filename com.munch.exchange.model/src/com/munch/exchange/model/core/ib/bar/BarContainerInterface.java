package com.munch.exchange.model.core.ib.bar;

public interface BarContainerInterface {
	
	
	
	public long getId();
	
	public long getLastShortTermAskBarTime();
	public void setLastShortTermAskBarTime(long lastShortTermAskBarTime);
	
	public long getLastShortTermBidBarTime();
	public void setLastShortTermBidBarTime(long lastShortTermBidBarTime);
	
	public long getLastShortTermMidPointBarTime();
	public void setLastShortTermMidPointBarTime(long lastShortTermMidPointBarTime);
	
	public long getLastShortTermTradesBarTime();
	public void setLastShortTermTradesBarTime(long lastShortTermTradesBarTime);

}
