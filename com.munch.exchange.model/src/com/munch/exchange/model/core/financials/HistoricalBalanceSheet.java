package com.munch.exchange.model.core.financials;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;

public class HistoricalBalanceSheet extends DatePointList<BalanceSheetPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7473791110289430239L;

	@Override
	protected DatePoint createPoint() {
		return new BalanceSheetPoint();
	}
	
	public HistoricalBalanceSheet getAnnualPoints(){
		return getPoints(BalanceSheetPoint.PeriodeTypeAnnual);
	}
	
	public HistoricalBalanceSheet getQuaterlyPoints(){
		return getPoints(BalanceSheetPoint.PeriodeTypeQuaterly);
	}
	
	private HistoricalBalanceSheet getPoints(String periodtype){
		HistoricalBalanceSheet ps=new HistoricalBalanceSheet();
		for(DatePoint p:this){
			if(!(p instanceof BalanceSheetPoint))continue;
			BalanceSheetPoint isp=(BalanceSheetPoint) p;
			if(isp.getPeriodType().equals(periodtype)){
				ps.add(isp);
			}
		}
		
		return ps;
		
	}

}
