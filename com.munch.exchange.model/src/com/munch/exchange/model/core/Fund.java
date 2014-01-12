package com.munch.exchange.model.core;

import org.w3c.dom.Element;

public class Fund extends ExchangeRate {
	
	static final String FIELD_FundFamily="FundFamily";
	static final String FIELD_Category="Category";
	static final String FIELD_NetAssets="NetAssets";
	static final String FIELD_YeartoDateReturn="Year-to-DateReturn";
	
	private String FundFamily="";
	private String Category="";
	private String NetAssets="";
	private String YeartoDateReturn="";
	
	public String getFundFamily() {
		return FundFamily;
	}
	public void setFundFamily(String fundFamily) {
		changes.firePropertyChange(FIELD_FundFamily, this.FundFamily,
				this.FundFamily = fundFamily);
	}
	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		changes.firePropertyChange(FIELD_Category, this.Category,
				this.Category = category);
	}
	public String getNetAssets() {
		return NetAssets;
	}
	public void setNetAssets(String netAssets) {
		changes.firePropertyChange(FIELD_NetAssets, this.NetAssets,
				this.NetAssets = netAssets);
	}
	public String getYeartoDateReturn() {
		return YeartoDateReturn;
	}
	public void setYeartoDateReturn(String yeartoDateReturn) {
		changes.firePropertyChange(FIELD_YeartoDateReturn, this.YeartoDateReturn,
				this.YeartoDateReturn = yeartoDateReturn);
	}
	@Override
	public String toString() {
		return "Fund [FundFamily=" + FundFamily + ", Category=" + Category
				+ ", NetAssets=" + NetAssets + ", YeartoDateReturn="
				+ YeartoDateReturn + ", start=" + start + ", end=" + end
				+ ", name=" + name + ", symbol=" + symbol + ", dataPath="
				+ dataPath + ", stockExchange=" + stockExchange
				+ ", historicalData=" + historicalData + ", recordedQuote="
				+ recordedQuote + "]";
	}
	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setFundFamily(rootElement.getAttribute(FIELD_FundFamily));
		this.setCategory(rootElement.getAttribute(FIELD_Category));
		this.setNetAssets(rootElement.getAttribute(FIELD_NetAssets));
		this.setYeartoDateReturn(rootElement.getAttribute(FIELD_YeartoDateReturn));
		
		super.initAttribute(rootElement);
	}
	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_FundFamily,this.getFundFamily());
		rootElement.setAttribute(FIELD_Category,this.getCategory());
		rootElement.setAttribute(FIELD_NetAssets,this.getNetAssets());
		rootElement.setAttribute(FIELD_YeartoDateReturn,this.getYeartoDateReturn());
		
		super.setAttribute(rootElement);
	}
	
	
	
	
}
