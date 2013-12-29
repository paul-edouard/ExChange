package com.munch.exchange.model.core;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.quote.RecordedQuote;
import com.munch.exchange.model.xml.XmlParameterElement;


public abstract class ExchangeRate extends XmlParameterElement {
	
	protected String name;
	static final String NameStr="name";
	
	protected String symbol;
	static final String SymbolStr="symbol";
	
	protected HistoricalData historicalData=new HistoricalData();
	protected RecordedQuote recordedQuote=new RecordedQuote();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
	public HistoricalData getHistoricalData() {
		return historicalData;
	}
	public void setHistoricalData(HistoricalData historicalData) {
		this.historicalData = historicalData;
	}
	
	
	public RecordedQuote getRecordedQuote() {
		return recordedQuote;
	}
	public void setRecordedQuote(RecordedQuote recordedQuote) {
		this.recordedQuote = recordedQuote;
	}
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	
	
	
	protected void initAttribute(Element rootElement){
		this.setName(rootElement.getAttribute(NameStr));
		this.setSymbol(rootElement.getAttribute(SymbolStr));
	}
	protected void initChild(Element childElement){}
	
	protected void setAttribute(Element rootElement){
		rootElement.setAttribute(NameStr, this.getName());
		rootElement.setAttribute(SymbolStr, this.getSymbol());
	}
	protected void appendChild(Element rootElement){
	}
	
	

}
