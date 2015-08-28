package com.munch.exchange.parts.chart;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;


public class ChartDateTickUnit extends DateTickUnit {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3269245816605551801L;
	
	
	private DateFormat dayFormatter;
	
	public ChartDateTickUnit(DateTickUnitType unitType, int multiple,
			DateFormat formatter,DateFormat dayFormatter) {
		super(unitType, multiple, formatter);
		
		this.dayFormatter=dayFormatter;
	}

	@Override
	public String valueToString(double milliseconds) {
		Date date=new Date((long) milliseconds);
		return dateToString(date);
	}

	@Override
	public String dateToString(Date date) {
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.HOUR_OF_DAY)==0 &&
				cal.get(Calendar.MINUTE)==0){
			return this.dayFormatter.format(date);
		}
		
		// TODO Auto-generated method stub
		return super.dateToString(date);
	}
	
	

}
