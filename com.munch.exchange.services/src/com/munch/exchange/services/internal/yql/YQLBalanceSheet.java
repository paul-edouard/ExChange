package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.LinkedList;

import com.munch.exchange.model.core.financials.BalanceSheetPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class YQLBalanceSheet extends YQLTable {

	private static String table="yahoo.finance.balancesheet";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private String timeframeType=FinancialPoint.PeriodeTypeNone;
	
	public YQLBalanceSheet(String symbol){
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
				// TODO Auto-generated catch block
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
	
public LinkedList<BalanceSheetPoint> getPointList(){
		
		LinkedList<BalanceSheetPoint> plist=new LinkedList<BalanceSheetPoint>();
		
		if(this.getResult()==null)return plist;
		
		if(!this.getResult().has("balancesheet"))return plist;
		
		JSONObject incomestatement=this.getResult().getJSONObject("balancesheet");
		if(incomestatement==null)return plist;
		
		if(incomestatement.get("statement") instanceof JSONArray){
			JSONArray array=  incomestatement.getJSONArray("statement");
			
			for(int i=0;i<array.length();i++){
				JSONObject json=array.getJSONObject(i);
				BalanceSheetPoint point=createPoint(json);
				//System.out.println(point);
				
				plist.add(point);
				
			}
		}
		
		
		return plist;
	}
	
	private BalanceSheetPoint createPoint(JSONObject json){
		BalanceSheetPoint point=new BalanceSheetPoint();
		
		
		point.setCashAndCashEquivalents(getLongValue(json,"CashAndCashEquivalents"));
		point.setShortTermInvestments(getLongValue(json,"ShortTermInvestments"));
		point.setNetReceivables(getLongValue(json,"NetReceivables"));
		point.setInventory(getLongValue(json,"Inventory"));
		point.setOtherCurrentAssets(getLongValue(json,"OtherCurrentAssets"));
		point.setTotalCurrentAssets(getLongValue(json,"TotalCurrentAssets"));
		point.setLongTermInvestments(getLongValue(json,"LongTermInvestments"));
		point.setPropertyPlantandEquipment(getLongValue(json,"PropertyPlantandEquipment"));
		point.setGoodwill(getLongValue(json,"Goodwill"));
		point.setIntangibleAssets(getLongValue(json,"IntangibleAssets"));
		point.setAccumulatedAmortization(getLongValue(json,"AccumulatedAmortization"));
		point.setOtherAssets(getLongValue(json,"OtherAssets"));
		point.setDeferredLongTermAssetCharges(getLongValue(json,"DeferredLongTermAssetCharges"));
		point.setTotalAssets(getLongValue(json,"TotalAssets"));
		point.setAccountsPayable(getLongValue(json,"AccountsPayable"));
		point.setShort_CurrentLongTermDebt(getLongValue(json,"Short_CurrentLongTermDebt"));
		point.setOtherCurrentLiabilities(getLongValue(json,"OtherCurrentLiabilities"));
		point.setTotalCurrentLiabilities(getLongValue(json,"TotalCurrentLiabilities"));
		point.setLongTermDebt(getLongValue(json,"LongTermDebt"));
		point.setOtherLiabilities(getLongValue(json,"OtherLiabilities"));
		point.setDeferredLongTermLiabilityCharges(getLongValue(json,"DeferredLongTermLiabilityCharges"));
		point.setMinorityInterest(getLongValue(json,"MinorityInterest"));
		point.setNegativeGoodwill(getLongValue(json,"NegativeGoodwill"));
		point.setTotalLiabilities(getLongValue(json,"TotalLiabilities"));
		point.setMiscStocksOptionsWarrants(getLongValue(json,"MiscStocksOptionsWarrants"));
		point.setRedeemablePreferredStock(getLongValue(json,"RedeemablePreferredStock"));
		point.setPreferredStock(getLongValue(json,"PreferredStock"));
		point.setCommonStock(getLongValue(json,"CommonStock"));
		point.setRetainedEarnings(getLongValue(json,"RetainedEarnings"));
		point.setTreasuryStock(getLongValue(json,"TreasuryStock"));
		point.setCapitalSurplus(getLongValue(json,"CapitalSurplus"));
		point.setOtherStockholderEquity(getLongValue(json,"OtherStockholderEquity"));
		point.setTotalStockholderEquity(getLongValue(json,"TotalStockholderEquity"));
		point.setNetTangibleAssets(getLongValue(json,"NetTangibleAssets"));
		
		
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
		YQLBalanceSheet balanceSheet=new YQLBalanceSheet("yhoo");
		balanceSheet.setTimeFrameToAnnual();
		System.out.println(balanceSheet.getResult().toString(1));
		for(BalanceSheetPoint point:balanceSheet.getPointList()){
			System.out.println(point);
		}
	}

}
