package com.munch.exchange.model.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.munch.exchange.model.core.neuralnetwork.PeriodType;

public class DateTool {
	
	
	public static String getCurrentDateString(){
		
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		
		return format.format(Calendar.getInstance().getTime());
		
	}
	
	public static String dateToString(Calendar date){
		if(date==null)return "No Date";
		
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
	
	public static String dateToMonthString(Calendar date){
		SimpleDateFormat format=new SimpleDateFormat("yyyyMM");
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
	
	public static Calendar StringToMs(String dateStr){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
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
	
	
	public static double calculateRelativePosition(Calendar Date,Calendar startDate, Calendar endDate, boolean considerOpenDaysOnly){
		
		long a=0;
		long b=0;
		
		/*
		if(considerOpenDaysOnly){
			a=countNumberOfOpenMillisBetween(startDate,endDate);
			b=countNumberOfOpenMillisBetween(startDate,Date);
		}
		else{
		*/
			a=endDate.getTimeInMillis()-startDate.getTimeInMillis();
			b=Date.getTimeInMillis()-startDate.getTimeInMillis();
		//}
		
		return ((double)b)/((double)a)*2-1;
	}
	
	
	
	public static long countNumberOfOpenMillisBetween(Calendar fromDate, Calendar toDate){
		
		if(toDate.equals(fromDate))return 0;
		
		//Reverse order
		if(toDate.before(fromDate)){
			return countNumberOfOpenMillisBetween(toDate,fromDate);
		}
		
		
		int nbOfFreeDays=0;
		Calendar fromCopy=Calendar.getInstance();
		fromCopy.setTimeInMillis(fromDate.getTimeInMillis());
		
		while(fromCopy.before(toDate)){
			if(isFeiertag(fromCopy)|| 
					fromCopy.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY ||
					fromCopy.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				nbOfFreeDays++;
			}
			
			fromCopy.setTimeInMillis(fromCopy.getTimeInMillis()+PeriodType.DAY.getPeriod());
		}
		
		long ms=toDate.getTimeInMillis()-fromDate.getTimeInMillis();
		
		return  ms-nbOfFreeDays*PeriodType.DAY.getPeriod();
		
	}
	
	
	public static boolean isFeiertag(Calendar date){
				
		GregorianCalendar g_date = new GregorianCalendar(date.get(Calendar.YEAR),
				date.get(Calendar.MONTH),
				date.get(Calendar.DAY_OF_MONTH));
		
		return isFeiertag(g_date);
				
	}
	
	public static boolean isFeiertag(GregorianCalendar date)
	  {
	    int jahr = date.get(Calendar.YEAR);
	  
	    int a = jahr % 19;
	    int b = jahr % 4;
	    int c = jahr % 7;
	    int monat = 0;
	  
	    int m = (8 * (jahr / 100) + 13) / 25 - 2;
	    int s = jahr / 100 - jahr / 400 - 2;
	    m = (15 + s - m) % 30;
	    int n = (6 + s) % 7;
	  
	    int d = (m + 19 * a) % 30;
	  
	    if (d == 29)
	      d = 28;
	    else if (d == 28 && a >= 11)
	      d = 27;
	  
	    int e = (2 * b + 4 * c + 6 * d + n) % 7;
	  
	    int tag = 21 + d + e + 1;
	  
	    if (tag > 31)
	    {
	      tag = tag % 31;
	      monat = 3;
	    }
	    if (tag <= 31)
	      monat = 2;
	  
	    GregorianCalendar gc_ostersonntag = new GregorianCalendar(jahr, monat, tag);
	    GregorianCalendar gc_ostermontag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 1));
	    GregorianCalendar gc_karfreitag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) - 2));    
	    GregorianCalendar gc_rosenmontag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) - 48));
	    GregorianCalendar gc_christihimmelfahrt = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 39));
	    GregorianCalendar gc_pfinstsonntag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 49));
	    GregorianCalendar gc_pfinstmontag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 50));
	    GregorianCalendar gc_frohnleichnahm = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 60));
	    GregorianCalendar gc_wiedervereinigung = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 9, 1);
	    GregorianCalendar gc_weihnachten_1 = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 24);
	    GregorianCalendar gc_weihnachten_2 = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 25);
	    GregorianCalendar gc_weihnachten_3 = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 26);
	    GregorianCalendar gc_silvester = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 31);
	    GregorianCalendar gc_neujahr = new GregorianCalendar(gc_silvester.get(Calendar.YEAR), 0, 1);

	    if(gc_ostermontag.equals(date) || 
	    		gc_karfreitag.equals(date) || 
	    		gc_rosenmontag.equals(date) || 
	    		gc_christihimmelfahrt.equals(date) ||
	    		gc_pfinstsonntag.equals(date) ||
	    		gc_pfinstmontag.equals(date) || 
	    		gc_frohnleichnahm.equals(date) || 
	    		gc_weihnachten_1.equals(date) || 
	    		gc_weihnachten_2.equals(date) || 
	    		gc_weihnachten_3.equals(date) || 
	    		gc_silvester.equals(date) || 
	    		gc_neujahr.equals(date) || 
	    		gc_wiedervereinigung.equals(date))
	    {
	      return true;
	    }
	    else
	    {
	      return false;
	    }
	    
	    
	  }
	
	
}
