package com.munch.exchange.services.internal.yql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.munch.exchange.model.core.analystestimation.AnalystEstimation;
import com.munch.exchange.model.core.analystestimation.EarningsHistory;
import com.munch.exchange.model.core.analystestimation.EarningsHistory.EstimationPointList;
import com.munch.exchange.model.core.analystestimation.EstimationPoint;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class YQLAnalystEstimate extends YQLTable {
	
	private static String table="yahoo.finance.analystestimate";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	public YQLAnalystEstimate(String symbol){
		this.symbol=symbol;
	}
	
	@Override
	protected String getTable() {
		return table;
	}

	@Override
	protected String getFormat() {
		return format;
	}
	
	@Override
	public boolean hasValidResult() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public AnalystEstimation getEstimation(){
		
		if (this.getResult() == null)
			return null;
		if (!this.getResult().has("EarningsHistory"))
			return null;
		if (!this.getResult().has("GrowthEst"))
			return null;
		if (!this.getResult().has("RevenueEst"))
			return null;
		if (!this.getResult().has("EPSTrends"))
			return null;
		if (!this.getResult().has("EPSRevisions"))
			return null;
		if (!this.getResult().has("EarningsEst"))
			return null;

		AnalystEstimation estimation = new AnalystEstimation();
		searchEarningsHistory(estimation.getEarningsHistory(), this.getResult()
				.getJSONObject("EarningsHistory"));
		
		
		
		return null;
	}
	
	private void searchEarningsHistory(EarningsHistory a,JSONObject obj){
		
		
		addToEstimationPointList(a,a.getDifference(),obj);
		addToEstimationPointList(a,a.getSurprise(),obj);
		addToEstimationPointList(a,a.getEPSEst(),obj);
		addToEstimationPointList(a,a.getEPSActual(),obj);
	
		
	}
	
	private void addToEstimationPointList(EarningsHistory a,EstimationPointList list,JSONObject obj){
		
		if(obj.has(list.getTagName())){
			JSONObject diff=obj.getJSONObject(list.getTagName());
			for(Object key:diff.keySet()){
				if(key instanceof String){
					Calendar date=getDateFromKey((String) key);
					Float val=getFloat(diff.getString((String)key));
					if(val!=Float.NaN)
						list.add(new EstimationPoint(date, val));
				}
			}
		}
		
	}
	
	private Float getFloat(String val){
		if(val.equals("N/A"))return Float.NaN;
		else{
			String withoutPer=val.replace("%", "");
			return Float.valueOf(withoutPer);
		}
	}
	
	private Calendar getDateFromKey(String keyDate){
		SimpleDateFormat format=new SimpleDateFormat("MMMyy",Locale.ENGLISH);
    	Date d;
		try {
			d = format.parse(keyDate);
			if(d!=null){
				Calendar date=Calendar.getInstance();
				date.setTime(d);
				date.set(Calendar.DAY_OF_MONTH, 1);
				date.set(Calendar.HOUR_OF_DAY, 23);
				date.set(Calendar.MINUTE, 59);
				date.set(Calendar.SECOND, 59);
				date.set(Calendar.MILLISECOND, 0);
				
				return date;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	

	public static void main(String[] args) {
		//AOI
		//YQLAnalystEstimate analystEstimate=new YQLAnalystEstimate("APPL");
		//YQLAnalystEstimate analystEstimate=new YQLAnalystEstimate("R");
		YQLAnalystEstimate analystEstimate=new YQLAnalystEstimate("T");
		System.out.println(analystEstimate.getResult().toString(1));
	}

}
