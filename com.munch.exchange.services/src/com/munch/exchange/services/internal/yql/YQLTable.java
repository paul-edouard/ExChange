package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.munch.exchange.services.internal.yql.json.JSONObject;



public abstract  class  YQLTable {
	
	
	
	protected String symbol;	
	private JSONObject result;

	
	protected String createUrl(){
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable()
							+" where symbol=\""+this.symbol+"\"";
			
			return baseUrl + URLEncoder.encode(query, "UTF-8") +this.getFormat();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
	}
	protected abstract String getTable();
	protected abstract String getFormat();
	
	
	
	public JSONObject getResult(){
		if(result!=null)return result;
		
		JSONObject message=YQL.getJSONObject(createUrl());
		if(message==null)return null;
		
		JSONObject query=message.getJSONObject("query");
		if(query==null)return null;
		
		result=query.getJSONObject("results");
		
		return result;
		
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
