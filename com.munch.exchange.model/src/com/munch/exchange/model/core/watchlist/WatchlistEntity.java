package com.munch.exchange.model.core.watchlist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.xml.XmlParameterElement;

public class WatchlistEntity extends XmlParameterElement {
	
	
	static final String FIELD_RateUuid="rateUuid";
	static final String FIELD_BuyPrice="buyPrice";
	static final String FIELD_Number="number";
	static final String FIELD_Rate="rate";
	
	
	private String rateUuid="";
	private double buyPrice=0;
	private int number=0;
	
	private ExchangeRate rate=null;
	
	
	public WatchlistEntity(){
		
	}
		
	public String getRateUuid() {
		return rateUuid;
	}

	public void setRateUuid(String rateUuid) {
	changes.firePropertyChange(FIELD_RateUuid, this.rateUuid, this.rateUuid = rateUuid);
	}
	
	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
	changes.firePropertyChange(FIELD_BuyPrice, this.buyPrice, this.buyPrice = buyPrice);
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
	changes.firePropertyChange(FIELD_Number, this.number, this.number = number);
	}
	
	public ExchangeRate getRate() {
		return rate;
	}

	public void setRate(ExchangeRate rate) {
	changes.firePropertyChange(FIELD_Rate, this.rate, this.rate = rate);}
	
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setRateUuid(rootElement.getAttribute(FIELD_RateUuid));
		this.setBuyPrice(Double.parseDouble(rootElement.getAttribute(FIELD_BuyPrice)));
		this.setNumber(Integer.parseInt(rootElement.getAttribute(FIELD_Number)));

	}

	@Override
	protected void initChild(Element childElement) {
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_RateUuid,String.valueOf(this.getRateUuid()));
		rootElement.setAttribute(FIELD_BuyPrice,String.valueOf(this.getBuyPrice()));
		rootElement.setAttribute(FIELD_Number,String.valueOf(this.getNumber()));

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}

}
