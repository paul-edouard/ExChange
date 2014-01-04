package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.munch.exchange.services.internal.yql.json.JSONException;
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
	
	public void resetResult(){
		result=null;
	}
	
	public JSONObject getResult(){
		if(result!=null)return result;
		
		JSONObject message=YQL.getJSONObject(createUrl());
		if(message==null)return null;
		
		JSONObject query=message.getJSONObject("query");
		if(query==null)return null;
		
		if(query.get("results")==null)
			return null;
		
		try{
		result=query.getJSONObject("results");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return result;
		
	}
	
	public abstract boolean hasValidResult();
	
	/*
	public boolean hasResult(){
		if( this.getResult()==null){
			return false;
		}
		//else if(this.getResult().)
		return true;
	}
	*/
	

	@Override
	public String toString() {
		return "YQLTable [symbol=" + symbol + ", result=" + this.getResult() + "]";
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
