package com.munch.exchange.model.core.divident;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public class Dividend extends DatePoint {
	
	static final String FIELD_Value="value";
	
	
	private float value=0;
	
	
	public Dividend(String dateValue){
		String[] v=dateValue.split(",");
		if(v.length<2){
			date=null;
			return;
		}
		
		String dateStr=v[0];
    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
    	Date d=null;
		try {
			d = format.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(d!=null){
			date.setTime(d);
			date.set(Calendar.HOUR_OF_DAY, 23);
			date.set(Calendar.MINUTE, 59);
			date.set(Calendar.SECOND, 59);
			date.set(Calendar.MILLISECOND, 0);
			
		}
		
		value=Float.valueOf(v[1]);
		
		
	}
	
	public Dividend(){
		super();
	}
	
	
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		changes.firePropertyChange(FIELD_Value, this.value, this.value = value);
		//this.value = value;
	}

	
	@Override
	public String toString() {
		return "Dividend [date=" + DateTool.dateToString(getDate()) + ", value=" + value + "]";
	}

	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	protected void initAttribute(Element Root){
		this.setValue(Float.valueOf(Root.getAttribute(FIELD_Value)));
		
		super.initAttribute(Root);
	}

	
	protected void setAttribute(Element e){
		e.setAttribute(FIELD_Value,String.valueOf(this.getValue()));
		
		super.setAttribute(e);
	}
	
	
	

}
