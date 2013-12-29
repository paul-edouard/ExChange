package com.munch.exchange.model.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {
	
	
	public static String dateToString(Calendar date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return format.format(date.getTime());
	}
	
	
	public static Calendar StringToDate(String dateStr){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			Date d=format.parse(dateStr);
			if(d!=null){
				Calendar date=Calendar.getInstance();
				date.setTime(d);
				return date;
			}
			return null;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();return null;
		}
		
	}
	
}
