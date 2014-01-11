package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.LinkedList;

import com.munch.exchange.model.core.financials.CashFlowPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class YQLCashFlow extends YQLTable {

	private static String table="yahoo.finance.cashflow";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private String timeframeType=FinancialPoint.PeriodeTypeNone;
	
	public YQLCashFlow(String symbol){
		this.symbol=symbol;
	}

	public String getTimeFrame(){
		return timeframeType;
	}

	public String getTimeframeType() {
		return timeframeType;
	}

	public void setTimeframeType(String timeframeType) {
		this.timeframeType = timeframeType;
	}

	public void setTimeFrameToQuaterly(){
		this.timeframeType=FinancialPoint.PeriodeTypeQuaterly;
	}
	public void setTimeFrameToAnnual(){
		this.timeframeType=FinancialPoint.PeriodeTypeAnnual;
	}
	
	@Override
	protected String createUrl(){
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable()
							+" where symbol=\""+this.symbol+"\""
							+" and timeframe=\""+this.getTimeFrame()+"\"";
			
			return baseUrl + URLEncoder.encode(query, "UTF-8") +this.getFormat();
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
				return "";
			}
	}
	
	@Override
	protected String getTable() {
		return table;
	}

	@Override
	protected String getFormat() {
		return format;
	}
	
	public LinkedList<CashFlowPoint> getPointList(){
		
		LinkedList<CashFlowPoint> plist=new LinkedList<CashFlowPoint>();
		
		if(this.getResult()==null)return plist;
		
		if(!this.getResult().has("cashflow"))return plist;
		
		JSONObject incomestatement=this.getResult().getJSONObject("cashflow");
		if(incomestatement==null)return plist;
		
		if(!incomestatement.has("statement"))return plist;
		
		if(incomestatement.get("statement") instanceof JSONArray){
			JSONArray array=  incomestatement.getJSONArray("statement");
			
			for(int i=0;i<array.length();i++){
				JSONObject json=array.getJSONObject(i);
				//System.out.println(json.toString(1)+"\n\n");
				CashFlowPoint point=createPoint(json);
				//System.out.println(point);
				
				plist.add(point);
				
			}
		}
		
		
		return plist;
	}
	
	private CashFlowPoint createPoint(JSONObject json){
		CashFlowPoint point=new CashFlowPoint();
		
		
		point.setNetIncome(getLongValue(json,"NetIncome"));
		point.setDepreciation(getLongValue(json,"Depreciation"));
		point.setAdjustmentsToNetIncome(getLongValue(json,"AdjustmentsToNetIncome"));
		point.setChangesInAccountsReceivables(getLongValue(json,"ChangesInAccountsReceivables"));
		point.setChangesInLiabilities(getLongValue(json,"ChangesInLiabilities"));
		point.setChangesInInventories(getLongValue(json,"ChangesInInventories"));
		point.setChangesInOtherOperatingActivities(getLongValue(json,"ChangesInOtherOperatingActivities"));
		point.setTotalCashFlowFromOperatingActivities(getLongValue(json,"TotalCashFlowFromOperatingActivities"));
		point.setCapitalExpenditures(getLongValue(json,"CapitalExpenditures"));
		point.setInvestments(getLongValue(json,"Investments"));
		point.setOtherCashflowsfromInvestingActivities(getLongValue(json,"OtherCashflowsfromInvestingActivities"));
		point.setTotalCashFlowsFromInvestingActivities(getLongValue(json,"TotalCashFlowsFromInvestingActivities"));
		point.setDividendsPaid(getLongValue(json,"DividendsPaid"));
		point.setSalePurchaseofStock(getLongValue(json,"SalePurchaseofStock"));
		point.setNetBorrowings(getLongValue(json,"NetBorrowings"));
		point.setOtherCashFlowsfromFinancingActivities(getLongValue(json,"OtherCashFlowsfromFinancingActivities"));
		point.setTotalCashFlowsFromFinancingActivities(getLongValue(json,"TotalCashFlowsFromFinancingActivities"));
		point.setEffectOfExchangeRateChanges(getLongValue(json,"EffectOfExchangeRateChanges"));
		point.setChangeInCashandCashEquivalents(getLongValue(json,"ChangeInCashandCashEquivalents"));
	
		
		try {
			point.setPeriodEnding(json.getPeriod("period"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		point.setPeriodType(this.getTimeframeType());
		point.setDate(point.getPeriodEnding());
		
		return point;
	}
	
	private long getLongValue(JSONObject json,String key){
		JSONObject item=json.getJSONObject(key);
		String content=item.getString("content");
		//System.out.println("Key: "+key+", Content: "+content);
		try{
		long val=Long.valueOf(content);
		return val;
		}
		catch(NumberFormatException e){
			/*e.printStackTrace();*/return Long.MIN_VALUE;
		}
		
	}
	
	@Override
	public boolean hasValidResult() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		YQLCashFlow cashFlow=new YQLCashFlow("yhoo");
		cashFlow.setTimeFrameToQuaterly();
		
		for(CashFlowPoint point:cashFlow.getPointList()){
			System.out.println(point);
		}
	}

}
