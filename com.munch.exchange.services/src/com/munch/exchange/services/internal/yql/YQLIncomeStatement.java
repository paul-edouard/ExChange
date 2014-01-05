package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.LinkedList;

import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class YQLIncomeStatement extends YQLTable {

	private static String table="yahoo.finance.incomestatement";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private String timeframeType=FinancialPoint.PeriodeTypeNone;
	
	public YQLIncomeStatement(String symbol){
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
	
	public LinkedList<IncomeStatementPoint> getPointList(){
		
		LinkedList<IncomeStatementPoint> plist=new LinkedList<IncomeStatementPoint>();
		
		if(this.getResult()==null)return plist;
		
		if(!this.getResult().has("incomestatement"))return plist;
		
		JSONObject incomestatement=this.getResult().getJSONObject("incomestatement");
		if(incomestatement==null)return plist;
		
		if(incomestatement.get("statement") instanceof JSONArray){
			JSONArray array=  incomestatement.getJSONArray("statement");
			
			for(int i=0;i<array.length();i++){
				JSONObject json=array.getJSONObject(i);
				IncomeStatementPoint point=createPoint(json);
				//System.out.println(point);
				
				plist.add(point);
				
			}
		}
		
		
		return plist;
	}
	
	private IncomeStatementPoint createPoint(JSONObject json){
		IncomeStatementPoint point=new IncomeStatementPoint();
		
		
		point.setTotalRevenue(getLongValue(json,"TotalRevenue"));
		point.setCostofRevenue(getLongValue(json,"CostofRevenue"));
		point.setGrossProfit(getLongValue(json,"GrossProfit"));
		point.setResearchDevelopment(getLongValue(json,"ResearchDevelopment"));
		point.setSellingGeneralandAdministrative(getLongValue(json,"SellingGeneralandAdministrative"));
		point.setNonRecurring(getLongValue(json,"NonRecurring"));
		point.setOthers(getLongValue(json,"Others"));
		point.setTotalOperatingExpenses(getLongValue(json,"TotalOperatingExpenses"));
		point.setOperatingIncomeorLoss(getLongValue(json,"OperatingIncomeorLoss"));
		point.setTotalOtherIncome_ExpensesNet(getLongValue(json,"TotalOtherIncome_ExpensesNet"));
		point.setEarningsBeforeInterestAndTaxes(getLongValue(json,"EarningsBeforeInterestAndTaxes"));
		point.setInterestExpense(getLongValue(json,"InterestExpense"));
		point.setIncomeBeforeTax(getLongValue(json,"IncomeBeforeTax"));
		point.setIncomeTaxExpense(getLongValue(json,"IncomeTaxExpense"));
		point.setMinorityInterest(getLongValue(json,"MinorityInterest"));
		point.setNetIncomeFromContinuingOps(getLongValue(json,"NetIncomeFromContinuingOps"));
		point.setDiscontinuedOperations(getLongValue(json,"DiscontinuedOperations"));
		point.setExtraordinaryItems(getLongValue(json,"ExtraordinaryItems"));
		point.setEffectOfAccountingChanges(getLongValue(json,"EffectOfAccountingChanges"));
		point.setOtherItems(getLongValue(json,"OtherItems"));
		point.setNetIncome(getLongValue(json,"NetIncome"));
		point.setPreferredStockAndOtherAdjustments(getLongValue(json,"PreferredStockAndOtherAdjustments"));
		point.setNetIncomeApplicableToCommonShares(getLongValue(json,"NetIncomeApplicableToCommonShares"));
		
		
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
		YQLIncomeStatement incomeStatement=new YQLIncomeStatement("DAI.DE");
		incomeStatement.setTimeFrameToQuaterly();
	//	System.out.println(incomeStatement.getResult().toString(1));
		incomeStatement.getPointList();
	}

}
