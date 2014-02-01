package com.munch.exchange;

public interface IEventConstant {
	
	//RATE
	String RATE_ALLTOPICS = "RATE/*";
	String RATE_NEW = "RATE/NEW";
	String RATE_DELETE = "RATE/DELETED";
	String RATE_UPDATE = "RATE/UPDATED";
	String RATE_LOADING = "RATE/LOADING";
	String RATE_LOADED = "RATE/LOADED";
	
	//QUOTE
	String QUOTE_ALLTOPICS = "QUOTE/*";
	String QUOTE_LOADED = "QUOTE/LOADED";
	String QUOTE_UPDATE = "QUOTE/UPDATE";
	

}
