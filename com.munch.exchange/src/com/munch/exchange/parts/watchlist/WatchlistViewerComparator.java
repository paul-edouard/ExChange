package com.munch.exchange.parts.watchlist;

import java.util.Calendar;

import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;

public class WatchlistViewerComparator extends ViewerComparator {
	
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;
	  
	private WatchlistService watchlistService;
	
	private Calendar startWatchDate=Calendar.getInstance();

	  public WatchlistViewerComparator(WatchlistService watchlistService) {
	    this.propertyIndex = 0;
	    direction = DESCENDING;
	    this.watchlistService=watchlistService;
	  }
	  
	  

	  public void setStartWatchDate(Calendar startWatchDate) {
		this.startWatchDate = startWatchDate;
	}



	public int getDirection() {
	    return direction == 1 ? SWT.DOWN : SWT.UP;
	  }

	  public void setColumn(int column) {
	    if (column == this.propertyIndex) {
	      // Same column as last sort; toggle the direction
	      direction = 1 - direction;
	    } else {
	      // New column; do an ascending sort
	      this.propertyIndex = column;
	      direction = DESCENDING;
	    }
	  }

	  @Override
	  public int compare(Viewer viewer, Object e1, Object e2) {
		WatchlistEntity p1 = (WatchlistEntity) e1;
		WatchlistEntity p2 = (WatchlistEntity) e2;
		QuotePoint point1=p1.getLastQuote();
    	QuotePoint point2=p2.getLastQuote();
		
	    int rc = 0;
	   
	    
	    switch (propertyIndex) {
	    //Full name
	    case 0:
	    	if(p1.getRate()==null || p2.getRate()==null)break;
	      rc = p2.getRate().getFullName().compareTo(p1.getRate().getFullName());
	      break;
	    //Price
	    case 1:
			if(point1==null || point2==null)break;
			rc = (point2.getLastTradePrice()>=point1.getLastTradePrice() ? 1 : -1);
			break;
		//Change
	    case 2:
			if(point1==null || point2==null)break;
			float per1 = point1.getChange() * 100 / point1.getLastTradePrice();
			float per2 = point2.getChange() * 100 / point2.getLastTradePrice();
			rc = (per2>=per1 ? 1 : -1);
			break;
		//Buy and Old
	    case 3:
	    	if(p1.getRate()==null || p2.getRate()==null)break;
	    	if(p1.getRate().getHistoricalData().isEmpty() || p2.getRate().getHistoricalData().isEmpty())break;
			rc = (p2.getBuyAndOld()>=p1.getBuyAndOld() ? 1 : -1);
			break;
		//MaxPro
	    case 4:
	    	if(p1.getRate()==null || p2.getRate()==null)break;
	    	if(p1.getRate().getHistoricalData().isEmpty() || p2.getRate().getHistoricalData().isEmpty())break;
			rc = (p2.getMaxProfit()>=p1.getMaxProfit() ? 1 : -1);
			break;
		//Bollinger Band
	    case 5:
	    	if(p1.getRate()==null || p2.getRate()==null)break;
	    	if(p1.getRate().getHistoricalData().isEmpty() || p2.getRate().getHistoricalData().isEmpty())break;
	    	
	    	if(p1.getBollingerBandTrigger()!=null && p2.getBollingerBandTrigger()==null)rc=-1;
	    	if(p1.getBollingerBandTrigger()==null && p2.getBollingerBandTrigger()!=null)rc=1;
	    	
	    	if(p1.getBollingerBandTrigger()==null || p2.getBollingerBandTrigger()==null)break;
	    	double pro1=p1.getBollingerBandTrigger().getProfit();
	    	double pro2=p2.getBollingerBandTrigger().getProfit();
	    	
			rc = (pro2>=pro1 ? 1 : -1);
			break;
	    default:
	      rc = 0;
	    }
	    // If descending order, flip the direction
	    if (direction == DESCENDING) {
	      rc = -rc;
	    }
	    return rc;
	  }

}
