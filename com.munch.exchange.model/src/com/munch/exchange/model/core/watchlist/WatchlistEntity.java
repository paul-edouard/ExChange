package com.munch.exchange.model.core.watchlist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.limit.OrderTrigger;
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
	
	private double maxProfit=0;
	private double buyAndOld=0;
	private QuotePoint lastQuote=null;
	
	private OrderTrigger bollingerBandTrigger=null;
	private OrderTrigger RSITrigger=null;
	
	
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
	
	

	public double getMaxProfit() {
		return maxProfit;
	}

	public void setMaxProfit(double maxProfit) {
	this.maxProfit = maxProfit;}
	

	public double getBuyAndOld() {
		return buyAndOld;
	}

	public void setBuyAndOld(double buyAndOld) {
	this.buyAndOld = buyAndOld;}
	

	public QuotePoint getLastQuote() {
		return lastQuote;
	}

	public void setLastQuote(QuotePoint lastQuote) {
	this.lastQuote = lastQuote;}
	

	public OrderTrigger getBollingerBandTrigger() {
		return bollingerBandTrigger;
	}

	public void setBollingerBandTrigger(OrderTrigger bollingerBandTrigger) {
	this.bollingerBandTrigger = bollingerBandTrigger;}
	
	
	

	public OrderTrigger getRSITrigger() {
		return RSITrigger;
	}

	public void setRSITrigger(OrderTrigger rSITrigger) {
				this.RSITrigger = rSITrigger;
	}

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
