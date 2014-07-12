package com.munch.exchange.model.core.financials;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;

public class HistoricalCashFlow extends DatePointList<CashFlowPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8143802055103724462L;

	@Override
	protected DatePoint createPoint() {
		return new CashFlowPoint();
	}
	
	public HistoricalCashFlow getAnnualPoints(){
		return getPoints(CashFlowPoint.PeriodeTypeAnnual);
	}
	
	public HistoricalCashFlow getQuaterlyPoints(){
		return getPoints(CashFlowPoint.PeriodeTypeQuaterly);
	}
	
	public HistoricalCashFlow getPoints(String periodtype){
		HistoricalCashFlow ps=new HistoricalCashFlow();
		for(DatePoint p:this){
			if(!(p instanceof CashFlowPoint))continue;
			CashFlowPoint isp=(CashFlowPoint) p;
			if(isp.getPeriodType().equals(periodtype)){
				ps.add(isp);
			}
		}
		
		return ps;
		
	}

}
