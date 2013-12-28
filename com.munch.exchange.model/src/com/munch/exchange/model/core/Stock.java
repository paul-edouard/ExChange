package com.munch.exchange.model.core;

import java.util.Calendar;

import org.w3c.dom.Element;

public class Stock extends ExchangeRate {
	
	
	//Data from YQLStocks
	private Calendar start=Calendar.getInstance();
	private Calendar end=Calendar.getInstance();
	private String sector,industry;
	//To Record Full time Employees
	
	@Override
	public String getTagName() {
		return "stock";
	}

	@Override
	protected void initAttribute(Element rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAttribute(Element rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendChild(Element rootElement) {
		// TODO Auto-generated method stub

	}

}
