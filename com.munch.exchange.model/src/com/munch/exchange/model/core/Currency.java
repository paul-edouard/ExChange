package com.munch.exchange.model.core;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;

public class Currency extends ExchangeRate{
	
	static final String FIELD_Category="Category";
	static final String FIELD_OnVistaId="OnVistaId";
	
	private String Category="";
	private String OnVistaId="";
	
	
	public Currency() {
		super();
		
		this.setStart(DateTool.StringToDay("1990-01-01"));
		
	}
	
	public String getOnVistaId() {
		return OnVistaId;
	}
	public void setOnVistaId(String onVistaId) {
		changes.firePropertyChange(FIELD_OnVistaId, this.OnVistaId,
				this.OnVistaId = onVistaId);
	}
	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		changes.firePropertyChange(FIELD_Category, this.Category,
				this.Category = category);
	}
	
	
	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setCategory(rootElement.getAttribute(FIELD_Category));
		this.setOnVistaId(rootElement.getAttribute(FIELD_OnVistaId));
		
		super.initAttribute(rootElement);
	}
	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_Category,this.getCategory());
		rootElement.setAttribute(FIELD_OnVistaId,this.getOnVistaId());
		
		
		super.setAttribute(rootElement);
	}

}
