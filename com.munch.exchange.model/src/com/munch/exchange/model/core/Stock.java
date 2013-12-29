package com.munch.exchange.model.core;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.divident.HistoricalDividend;
import com.munch.exchange.model.tool.DateTool;

public class Stock extends ExchangeRate {
	
	static final String StartStr="start";
	static final String EndStr="end";
	static final String SectorStr="sector";
	static final String IndustryStr="industry";
	
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
		this.start = start;
	}


	public Calendar getEnd() {
		return end;
	}


	public void setEnd(Calendar end) {
		this.end = end;
	}


	public String getSector() {
		return sector;
	}


	public void setSector(String sector) {
		this.sector = sector;
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setEnd(DateTool.StringToDate(rootElement.getAttribute(EndStr)));
		this.setStart(DateTool.StringToDate(rootElement.getAttribute(StartStr)));
		this.setSector(rootElement.getAttribute(SectorStr));
		this.setIndustry(rootElement.getAttribute(IndustryStr));
		
		super.initAttribute(rootElement);
	}
	
	
	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(EndStr,DateTool.dateToString( this.getEnd()));
		rootElement.setAttribute(StartStr,DateTool.dateToString( this.getStart()));
		
		rootElement.setAttribute(SectorStr,this.getSector());
		rootElement.setAttribute(IndustryStr,this.getIndustry());
		
		super.setAttribute(rootElement);
	}
	

	

}
