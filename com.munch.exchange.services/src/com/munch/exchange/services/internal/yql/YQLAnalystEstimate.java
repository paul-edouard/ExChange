package com.munch.exchange.services.internal.yql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.munch.exchange.model.core.analystestimation.AnalystEstimation;
import com.munch.exchange.model.core.analystestimation.EPSRevisions;
import com.munch.exchange.model.core.analystestimation.EPSTrends;
import com.munch.exchange.model.core.analystestimation.EarningsEst;
import com.munch.exchange.model.core.analystestimation.EarningsHistory;
import com.munch.exchange.model.core.analystestimation.EarningsHistory.EstimationPointList;
import com.munch.exchange.model.core.analystestimation.Estimation;
import com.munch.exchange.model.core.analystestimation.EstimationPoint;
import com.munch.exchange.model.core.analystestimation.GrowthEst;
import com.munch.exchange.model.core.analystestimation.RevenueEst;
import com.munch.exchange.model.xml.XmlHashMap;
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
		
		AnalystEstimation estimation = new AnalystEstimation();
		
		if (this.getResult() == null)
			return null;
		if (!this.getResult().has("results"))
			return null;
		JSONObject res=this.getResult().getJSONObject("results");
		
		if (!res.has(estimation.getEarningsHistory().getTagName()))
			return null;
		searchEarningsHistory(estimation.getEarningsHistory(), res
				.getJSONObject(estimation.getEarningsHistory().getTagName()));
		
		if (!res.has(estimation.getGrowthEst().getTagName()))
			return null;
		searchGrowthEst(estimation.getGrowthEst(), res
				.getJSONObject(estimation.getGrowthEst().getTagName()) );
		
		if (!res.has(estimation.getRevenueEst().getTagName()))
			return null;
		searchRevenueEst(estimation.getRevenueEst(), res
				.getJSONObject(estimation.getRevenueEst().getTagName()) );
		
		if (!res.has(estimation.getEPSTrends().getTagName()))
			return null;
		searchEPSTrends(estimation.getEPSTrends(), res
				.getJSONObject(estimation.getEPSTrends().getTagName()) );
		
		if (!res.has(estimation.getEPSRevisions().getTagName()))
			return null;
		searchEPSRevisions(estimation.getEPSRevisions(), res
				.getJSONObject(estimation.getEPSRevisions().getTagName()) );
		
		if (!res.has(estimation.getEarningsEst().getTagName()))
			return null;
		searchEarningsEst(estimation.getEarningsEst(), res
				.getJSONObject(estimation.getEarningsEst().getTagName()) );

		/*
		System.out.println("Estimation: "+estimation.getEarningsHistory());
		System.out.println("Estimation: "+estimation.getGrowthEst());
		System.out.println("Estimation: "+estimation.getRevenueEst());
		System.out.println("Estimation: "+estimation.getEPSTrends());
		System.out.println("Estimation: "+estimation.getEPSRevisions());
		System.out.println("Estimation: "+estimation.getEarningsEst());
		*/
		
		return estimation;
	}
	
	private void searchEarningsEst(EarningsEst e, JSONObject obj) {
		getEstimationData(e.getNoofAnalysts(),obj);
		getEstimationData(e.getYearAgoEPS(),obj);
		getEstimationData(e.getAvgEstimate(),obj);
		getEstimationData(e.getLowEstimate(),obj);
		getEstimationData(e.getHighEstimate(),obj);
	}
	
	private void searchEPSRevisions(EPSRevisions e, JSONObject obj) {
		getEstimationData(e.getUpLast7Days(),obj);
		getEstimationData(e.getUpLast30Days(),obj);
		getEstimationData(e.getDownLast30Days(),obj);
		getEstimationData(e.getDownLast90Days(),obj);
		
	}
	
	private void searchEPSTrends(EPSTrends e, JSONObject obj) {
		getEstimationData(e.getCurrentEstimate(),obj);
		getEstimationData(e.get_7DaysAgo(),obj);
		getEstimationData(e.get_30DaysAgo(),obj);
		getEstimationData(e.get_60DaysAgo(),obj);
		getEstimationData(e.get_90DaysAgo(),obj);
	}
	
	
	/**
	 * Search the revenue estimation
	 * @param e
	 * @param obj
	 */
	private void searchRevenueEst(RevenueEst e, JSONObject obj){
		getEstimationData(e.getYearAgoSales(),obj);
		getEstimationData(e.getNoofAnalysts(),obj);
		getEstimationData(e.getAvgEstimate(),obj);
		getEstimationData(e.getLowEstimate(),obj);
		getEstimationData(e.getHighEstimate(),obj);
		getEstimationData(e.getSalesGrowth(),obj);
	}
	
	private void getEstimationData(Estimation e, JSONObject obj){
		if (!obj.has(e.getTagName()))return;
		JSONObject e_obj=obj.getJSONObject(e.getTagName());
		if(e_obj==null)return;
		
		if(e_obj.has(Estimation.FIELD_CurrentQtr)){
			Object o=e_obj.get(Estimation.FIELD_CurrentQtr);
			if(o instanceof String)
				e.setCurrentQtr(this.getFloat((String)o));
		}
		if(e_obj.has(Estimation.FIELD_NextQtr)){
			Object o=e_obj.get(Estimation.FIELD_NextQtr);
			if(o instanceof String)
				e.setNextQtr(this.getFloat((String)o));
		}
		if(e_obj.has(Estimation.FIELD_CurrentYear)){
			Object o=e_obj.get(Estimation.FIELD_CurrentYear);
			if(o instanceof String)
				e.setCurrentYear(this.getFloat((String)o));
		}
		if(e_obj.has(Estimation.FIELD_NextYear)){
			Object o=e_obj.get(Estimation.FIELD_NextYear);
			if(o instanceof String)
				e.setNextYear(this.getFloat((String)o));
		}
		
	}
	
	/**
	 * search the growth estimation
	 * @param e
	 * @param obj
	 */
	private void searchGrowthEst(GrowthEst e, JSONObject obj){
		
		getGrowthEstData(e.getPEGRatio(),obj);
		getGrowthEstData(e.getPriceEarnings(),obj);
		getGrowthEstData(e.getPast5Years(),obj);
		getGrowthEstData(e.getThisYear(),obj);
		getGrowthEstData(e.getCurrentQtr(),obj);
		getGrowthEstData(e.getNext5Years(),obj);
		getGrowthEstData(e.getNextYear(),obj);
		getGrowthEstData(e.getNextQtr(),obj);
		
	}
	
	private void getGrowthEstData(XmlHashMap<String, Float> map,JSONObject obj){
		if (!obj.has(map.getTagName()))return;
		JSONObject map_obj=obj.getJSONObject(map.getTagName());
		if(map_obj==null)return;
		for(Object key:map_obj.keySet()){
			if(!(key instanceof String))continue;
			String key_str=(String)key;
			map.put(key_str, this.getFloat(map_obj.getString(key_str)));
		}
	}
	
	/**
	 * search the Earning History Data
	 * 
	 * @param a
	 * @param obj
	 */
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
		if(val.contains(",") && val.contains(".") ){
			val=val.replace(",", "");
		}
		
		if(val.equals("N/A"))return Float.NaN;
		else if(val.endsWith("B")){
			Float val_f= Float.valueOf(val.replace("B", ""));
			return val_f*1000000000;
		}
		else if(val.endsWith("M")){
			Float val_f= Float.valueOf(val.replace("M", ""));
			return val_f*1000000;
		}
		else if(val.endsWith("T")){
			Float val_f= Float.valueOf(val.replace("T", ""));
			return val_f*1000;
		}
		else if(val.endsWith("%")){
			Float val_f= Float.valueOf(val.replace("%", ""));
			return val_f;
		}
		else{
			try{
			return Float.valueOf(val);
			}
			catch(Exception e){
				e.printStackTrace();
				return Float.NaN;
			}
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
		YQLAnalystEstimate analystEstimate=new YQLAnalystEstimate("R");
		System.out.println(analystEstimate.getResult().toString(1));
		analystEstimate.getEstimation();
	}

}
