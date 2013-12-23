package com.munch.exchange.services.internal.yql;

import com.munch.exchange.services.internal.yql.json.JSONObject;



public abstract  class  YQLTable {
	
	
	protected String symbol;
	
	private JSONObject result;

	
	protected abstract String createUrl();
	protected abstract String getTable();
	protected abstract String getFormat();
	
	
	
	public JSONObject getResult(){
		if(result!=null)return result;
		
		result=YQL.getJSONObject(createUrl());
		return result;
		
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
