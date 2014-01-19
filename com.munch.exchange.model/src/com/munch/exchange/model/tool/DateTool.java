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
				date.set(Calendar.MILLISECOND, 0);
				return date;
			}
			return null;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();return null;
		}
		
	}
	
	
	public static String dateToDayString(Calendar date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date.getTime());
	}
	
	public static String OnVistadateToDayString(Calendar date){
		SimpleDateFormat format=new SimpleDateFormat("dd.MM.yyyy");
		return format.format(date.getTime());
	}
	
	
	public static Calendar OnVistaStringToDay(String dateStr){
		SimpleDateFormat format=new SimpleDateFormat("dd.MM.yyyy");
		try {
			Date d=format.parse(dateStr);
			if(d!=null){
				Calendar date=Calendar.getInstance();
				date.setTime(d);
				date.set(Calendar.MILLISECOND, 0);
				date.set(Calendar.HOUR_OF_DAY, 0);
				date.set(Calendar.MINUTE, 0);
				date.set(Calendar.SECOND, 0);
				
				return date;
			}
			return null;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();return null;
		}
		
	}
	
	public static Calendar StringToDay(String dateStr){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d=format.parse(dateStr);
			if(d!=null){
				Calendar date=Calendar.getInstance();
				date.setTime(d);
				date.set(Calendar.MILLISECOND, 0);
				date.set(Calendar.HOUR_OF_DAY, 0);
				date.set(Calendar.MINUTE, 0);
				date.set(Calendar.SECOND, 0);
				
				return date;
			}
			return null;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();return null;
		}
		
	}
	
	public static Calendar[] splitIntervalInYears(Calendar date_A,Calendar date_B){
		
		int year_first=date_A.get(Calendar.YEAR);
		int year_last=date_B.get(Calendar.YEAR);
		
		int diff=year_last-year_first;
		
		Calendar[] s=new Calendar[2+diff*2];
		int j=0;
		s[j]=date_A;
		for(int i=year_first;i<year_last;i++){
			Calendar y_end=StringToDate(String.valueOf(i)+"-12-31T23:59:59");j++;s[j]=y_end;
			Calendar y_begin=StringToDate(String.valueOf(i+1)+"-01-01T23:59:59");j++;s[j]=y_begin;
		}
		j++;
		s[j]=date_B;
		return s;
	}
	
}
