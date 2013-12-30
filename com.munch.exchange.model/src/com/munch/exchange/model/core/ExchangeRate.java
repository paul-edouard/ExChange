package com.munch.exchange.model.core;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.quote.RecordedQuote;
import com.munch.exchange.model.xml.XmlParameterElement;


public abstract class ExchangeRate extends XmlParameterElement {
	
	public static final String FIELD_NAME="name";
	public static final String FIELD_SYMBOL="symbol";
	
	protected String name;
	protected String symbol;
	
	
	protected HistoricalData historicalData=new HistoricalData();
	protected RecordedQuote recordedQuote=new RecordedQuote();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		changes.firePropertyChange(FIELD_NAME, this.name,
				this.name = name);
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		changes.firePropertyChange(FIELD_SYMBOL, this.symbol, this.symbol = symbol);
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
	
	
	
	@Override
	public String toString() {
		return "ExchangeRate [name=" + name + ", symbol=" + symbol
				+ ", historicalData=" + historicalData + ", recordedQuote="
				+ recordedQuote + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ExchangeRate)) {
			return false;
		}
		ExchangeRate other = (ExchangeRate) obj;
		if (symbol == null) {
			if (other.symbol != null) {
				return false;
			}
		} else if (!symbol.equals(other.symbol)) {
			return false;
		}
		return true;
	}
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	
	
	
	protected void initAttribute(Element rootElement){
		this.setName(rootElement.getAttribute(FIELD_NAME));
		this.setSymbol(rootElement.getAttribute(FIELD_SYMBOL));
	}
	protected void initChild(Element childElement){}
	
	protected void setAttribute(Element rootElement){
		rootElement.setAttribute(FIELD_NAME, this.getName());
		rootElement.setAttribute(FIELD_SYMBOL, this.getSymbol());
	}
	protected void appendChild(Element rootElement){
	}
	
	

}
