package com.munch.exchange.model.core;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.divident.HistoricalDividend;
import com.munch.exchange.model.tool.DateTool;

public class Stock extends ExchangeRate {
	
	static final String FIELD_Start="start";
	static final String FIELD_End="end";
	static final String FIELD_Sector="sector";
	static final String FIELD_Industry="industry";
	
	//Data from YQLStocks
	private Calendar start=Calendar.getInstance();
	private Calendar end=Calendar.getInstance();
	private String sector,industry;
	//TODO Record Full time Employees
	
	
	protected HistoricalDividend historicalDividend=new HistoricalDividend();
	
	
	
	public Calendar getStart() {
		return start;
	}


	public void setStart(Calendar start) {
		changes.firePropertyChange(FIELD_Start, this.start, this.start = start);
	}


	public Calendar getEnd() {
		return end;
	}


	public void setEnd(Calendar end) {
		changes.firePropertyChange(FIELD_End, this.end, this.end = end);
	}


	public String getSector() {
		return sector;
	}


	public void setSector(String sector) {
		changes.firePropertyChange(FIELD_Sector, this.sector, this.sector = sector);
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		changes.firePropertyChange(FIELD_Industry, this.industry, this.industry = industry);
	}


	public HistoricalDividend getHistoricalDividend() {
		return historicalDividend;
	}


	public void setHistoricalDividend(HistoricalDividend historicalDividend) {
		//changes.firePropertyChange(FIELD, this.historicalDividend, this.historicalDividend = historicalDividend);
		this.historicalDividend = historicalDividend;
	}


	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setEnd(DateTool.StringToDate(rootElement.getAttribute(FIELD_End)));
		this.setStart(DateTool.StringToDate(rootElement.getAttribute(FIELD_Start)));
		this.setSector(rootElement.getAttribute(FIELD_Sector));
		this.setIndustry(rootElement.getAttribute(FIELD_Industry));
		
		super.initAttribute(rootElement);
	}
	
	
	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_End,DateTool.dateToString( this.getEnd()));
		rootElement.setAttribute(FIELD_Start,DateTool.dateToString( this.getStart()));
		
		rootElement.setAttribute(FIELD_Sector,this.getSector());
		rootElement.setAttribute(FIELD_Industry,this.getIndustry());
		
		super.setAttribute(rootElement);
	}
	

	

}
