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
	
	public boolean isLongTermAskBarLoadingFinished();
	public void setLongTermAskBarLoadingFinished(boolean longTermAskBarLoadingFinished);

	public boolean isLongTermBidBarLoadingFinished();
	public void setLongTermBidBarLoadingFinished(boolean longTermBidBarLoadingFinished);

	public boolean isLongTermMidPointBarLoadingFinished() ;
	public void setLongTermMidPointBarLoadingFinished(boolean longTermMidPointBarLoadingFinished);

	public boolean isLongTermTradesBarLoadingFinished();
	public void setLongTermTradesBarLoadingFinished(boolean longTermTradesBarLoadingFinished);
	
	
	
}
