package com.munch.exchange.model.core.financials;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class Period extends XmlParameterElement{
	
	private static Logger logger = Logger.getLogger(Period.class);
	
	public static final String FIELD_Year="Year";
	public static final String FIELD_Quarter="Quarter";
	public static final String FIELD_Type="Type";
	
	
	public static final int MAX_QUARTER=4;
	public static final int MIN_QUARTER=1;
	
	private int year;
	private int quarter;
	private PeriodType type;
	
	
	

	public enum PeriodType {
		ANNUAL ("Annual"),
	    QUATERLY ("quarterly");

	    private final String type;   
	    
	    PeriodType(String type) {
	        this.type = type;
	    }
	    
	    String getString(){
	    	return type;
	    }
	    
	    public static PeriodType typeFromString(String type_str){
	    	if(type_str.equals("Annual"))
	    		return ANNUAL;
	    	
	    	else
	    		return QUATERLY;
	    }
	    
	}
	
	
	public Period(int quarter,int year){
		this.quarter=quarter;
		this.year=year;
		type=PeriodType.QUATERLY;
	}
	
	public Period(){
		Calendar date = Calendar.getInstance();
		this.year=date.get(Calendar.YEAR);
		this.quarter=MIN_QUARTER;
		type=PeriodType.QUATERLY;
	}
	
	public Period(int year){
		this.quarter=MIN_QUARTER;
		this.year=year;
		type=PeriodType.ANNUAL;
	}
	
	public Calendar getPeriodEndingDate(){
		Calendar date=Calendar.getInstance();
		date.set(Calendar.HOUR, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);
		date.set(Calendar.MILLISECOND, 0);
		
		switch (type) {
		case ANNUAL:
			date.set(Calendar.YEAR, this.year-1);
			date.set(Calendar.MONTH, 11);
			break;

		case QUATERLY:
			date.set(Calendar.YEAR, this.year);
			date.set(Calendar.MONTH, this.quarter*3-1);
			break;
		}
		
		date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		return date;
	}

	
	public void next(){
		switch(type){
			case  QUATERLY:
				quarter++;
				if(quarter>MAX_QUARTER){
					quarter=MIN_QUARTER;
					year++;
				}
				break;
				
			case ANNUAL:
				//quarter=0;
				year++;
				break;
		}
	}
	
	public void previous(){
		switch(type){
			case  QUATERLY:
				quarter--;
				if(quarter<MIN_QUARTER){
					quarter=MAX_QUARTER;
					year--;
				}
				break;
				
			case ANNUAL:
				//quarter=0;
				year--;
				break;
		}
	}
	
	
	@Override
	public String toString() {
		switch(type){
		case  QUATERLY:
			return "Q"+String.valueOf(quarter)+"-"+String.valueOf(year);
			
		case ANNUAL:
			return String.valueOf(year);
			
		default:
			return "";
		}
	}
	
	public void fromString(String input){
		if(input.contains("-")){
			String[] tockens=input.split("-");
			int a,b=0;
			if(tockens.length!=2)return;
			
			try {
				a=Integer.parseInt(tockens[0]);
				b=Integer.parseInt(tockens[1]);
				
				quarter=a;
				year=b;
				
			} catch (Exception e) {
				logger.info("Cannot parse the input as period string: "+input);
			}
			
					
		}
		else{
			try {
				year=Integer.parseInt(input);
				quarter=MIN_QUARTER;
			} catch (Exception e) {
				logger.info("Cannot parse the input as period string: "+input);
			}
		}
		
	}
	
	
	
	//****************************************
	//***        Getter and Setter        ****
	//****************************************
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
	changes.firePropertyChange(FIELD_Year, this.year,this.year = year);
	}
	

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
	changes.firePropertyChange(FIELD_Quarter, this.quarter, this.quarter = quarter);
	}
	

	public PeriodType getType() {
		return type;
	}

	public void setType(PeriodType type) {
		changes.firePropertyChange(FIELD_Type, this.type, this.type = type);
	}
	
	
	//****************************************
	//***             XML                 ****
	//****************************************


	@Override
	protected void initAttribute(Element rootElement) {
		this.setYear(Integer.parseInt(rootElement.getAttribute(FIELD_Year)));
		this.setQuarter(Integer.parseInt(rootElement.getAttribute(FIELD_Quarter)));
		this.setType(PeriodType.typeFromString(rootElement.getAttribute(FIELD_Type)));
		
	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Year,String.valueOf(this.getYear()));
		rootElement.setAttribute(FIELD_Quarter,String.valueOf(this.getQuarter()));
		rootElement.setAttribute(FIELD_Type,this.getType().getString());
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {}
	
	
	
	
	

}
