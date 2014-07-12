package com.munch.exchange.model.core.financials;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;

public class HistoricalIncomeStatement extends DatePointList<IncomeStatementPoint>
		 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4346137119537920111L;


	@Override
	protected DatePoint createPoint() {
		return new IncomeStatementPoint();
	}
	
	public HistoricalIncomeStatement getAnnualPoints(){
		return getPoints(IncomeStatementPoint.PeriodeTypeAnnual);
	}
	
	public HistoricalIncomeStatement getQuaterlyPoints(){
		return getPoints(IncomeStatementPoint.PeriodeTypeQuaterly);
	}
	
	public HistoricalIncomeStatement getPoints(String periodtype){
		HistoricalIncomeStatement ps=new HistoricalIncomeStatement();
		for(DatePoint p:this){
			if(!(p instanceof IncomeStatementPoint))continue;
			IncomeStatementPoint isp=(IncomeStatementPoint) p;
			if(isp.getPeriodType().equals(periodtype)){
				ps.add(isp);
			}
		}
		
		return ps;
		
	}
	

}
