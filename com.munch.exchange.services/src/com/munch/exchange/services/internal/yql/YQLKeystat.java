package com.munch.exchange.services.internal.yql;

import com.munch.exchange.model.core.keystat.ContentAndTerm;
import com.munch.exchange.model.core.keystat.KeyStatistics;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONObject;


public class YQLKeystat extends YQLTable {
	
	private static String table="yahoo.finance.keystats";
	private static String format="&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	
	public YQLKeystat(String symbol){
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
	
	
	public KeyStatistics getKeyStatistics(){
		KeyStatistics ks=new KeyStatistics();
		
		if (this.getResult() == null)
			return null;
		if (!this.getResult().has("stats"))
			return null;
		JSONObject s=this.getResult().getJSONObject("stats");
		for(Object key:s.keySet()){
			//System.out.println(key);
			Object child=s.get((String)key);
			if(child instanceof JSONObject){
				ContentAndTerm ct=toContentAndTerm((JSONObject)child);
				//System.out.println("-> "+ct);
				ks.putContent((String)key, ct);
			}
			else if(child instanceof String){
				ContentAndTerm ct=new ContentAndTerm();
				ct.setContent((String) child);
				//System.out.println("-> String:"+(String) child);
				ks.putContent((String)key, ct);
			}
			else if(child instanceof JSONArray){
				//System.out.println("----> Array:");
				JSONArray array=(JSONArray) child;
				for(int i=0;i<array.length();i++){
					Object array_c=array.get(i);
					//System.out.println("---> "+array_c.getClass());
					if(array_c instanceof JSONObject){
						ContentAndTerm ct=toContentAndTerm((JSONObject)array_c);
						//System.out.println("--> "+ct);
						ks.putContent((String)key, ct);
					}
					else if(array_c instanceof String){
						ContentAndTerm ct=new ContentAndTerm();
						ct.setContent((String) array_c);
						//System.out.println("--> String:"+(String) array_c);
						ks.putContent((String)key, ct);
					}
					
				}
			}
			
		}
		
		return ks;
	}
	
	private ContentAndTerm toContentAndTerm(JSONObject o){
		if(o.has("content") && o.has("term")){
			ContentAndTerm ct=new ContentAndTerm();
			ct.setContent(o.getString("content"));
			ct.setTerm(o.getString("term"));
			return ct;
		}
		return null;
		
	}

	public static void main(String[] args) {
		YQLKeystat keystat=new YQLKeystat("GOOG");
		//System.out.println(keystat.getResult().toString(1));
		System.out.println(keystat.getKeyStatistics());
	}

}
