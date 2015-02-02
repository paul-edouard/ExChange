package com.munch.exchange.model.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		if(dateStr.equals("No Date"))return null;
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
	
	
	public static double calculateRelativePosition(Calendar date,Calendar startDate, Calendar endDate, boolean considerOpenDaysOnly){
		
		double a=0;
		double b=0;
		
		//System.out.println("Calculate relative position: Date="+dateToDayString(date)+ "[start="+dateToDayString(startDate)+",end="+dateToDayString(endDate)+"]");
		
		//String ret="Calculate relative position: Date="+dateToDayString(date)+ "[start="+dateToDayString(startDate)+",end="+dateToDayString(endDate)+"]";
		
		if(considerOpenDaysOnly){
			a=countNumberOfOpenMillisBetween(startDate,endDate);
			b=countNumberOfOpenMillisBetween(startDate,date);
		}
		else{
		
			a=endDate.getTimeInMillis()-startDate.getTimeInMillis();
			b=date.getTimeInMillis()-startDate.getTimeInMillis();
		}
		
		//System.out.println(ret+", a="+a+", b="+b+", b/a="+(b/a));
		
		return b/a*2-1;
	}
	
	
	
	public static long countNumberOfOpenMillisBetween(Calendar fromDate, Calendar toDate){
		
		if(toDate.equals(fromDate))return 0;
		
		//Reverse order
		if(toDate.before(fromDate)){
			return countNumberOfOpenMillisBetween(toDate,fromDate);
		}
		
		
		long nbOfClosedDays=0;
		Calendar fromCopy=Calendar.getInstance();
		fromCopy.setTimeInMillis(fromDate.getTimeInMillis());
		
		while(fromCopy.before(toDate)){
			if(!isStockMarketOpen(fromCopy)){
				nbOfClosedDays++;
			}
			
			fromCopy.setTimeInMillis(fromCopy.getTimeInMillis()+PeriodType.DAY.getPeriod());
		}
		
		long ms=toDate.getTimeInMillis()-fromDate.getTimeInMillis();
		long abs=ms-nbOfClosedDays*PeriodType.DAY.getPeriod();
		
		//System.out.println("Total ms: "+ms+" Nb of closed days: "+nbOfClosedDays+", open abs: "+abs+", clase day ms="+nbOfClosedDays*PeriodType.DAY.getPeriod());
		
		
		
		return abs;
		
	}
	
	
	public static boolean isStockMarketOpen(Calendar date){
	
		//TODO only valid for germany
		//http://www.finanzen.net/feiertage/
		
		//Sunday
		if(date.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)return false;
		
		//Saturday
		if(date.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)return false;
		
		//Neujahr
		if(date.get(Calendar.MONTH)==Calendar.JANUARY && date.get(Calendar.DAY_OF_MONTH)==1)return false;
		
		//Tag der Arbeit
		if(date.get(Calendar.MONTH)==Calendar.MAY && date.get(Calendar.DAY_OF_MONTH)==1)return false;
		
		//Tag der deutschen Einheit
		if(date.get(Calendar.MONTH)==Calendar.OCTOBER && date.get(Calendar.DAY_OF_MONTH)==3)return false;
		
		//Heiligabend
		if(date.get(Calendar.MONTH)==Calendar.DECEMBER && date.get(Calendar.DAY_OF_MONTH)==24)return false;
		
		//1. Weihnachtsfeiertag
		if(date.get(Calendar.MONTH)==Calendar.DECEMBER && date.get(Calendar.DAY_OF_MONTH)==25)return false;
		
		//2. Weihnachtsfeiertag
		if(date.get(Calendar.MONTH)==Calendar.DECEMBER && date.get(Calendar.DAY_OF_MONTH)==26)return false;
		
		//Silvester
		if(date.get(Calendar.MONTH)==Calendar.DECEMBER && date.get(Calendar.DAY_OF_MONTH)==31)return false;
		
		if(isChristischFeiertag(date))return false;
		
		return true;
		
	}
	
	
	public static boolean isChristischFeiertag(Calendar date){
				
		GregorianCalendar g_date = new GregorianCalendar(date.get(Calendar.YEAR),
				date.get(Calendar.MONTH),
				date.get(Calendar.DAY_OF_MONTH));
		
		return isFeiertag(g_date);
				
	}
	
	/*
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
	*/
	
	public static boolean isFeiertag(GregorianCalendar date){
		int tag, monat; // Eingabedaten
	    int jahr; // Laufvariable für getesteten Jahresbereich
	    
	    tag=date.get(Calendar.DAY_OF_MONTH);
	    monat=date.get(Calendar.MONTH)+1;
	    jahr=date.get(Calendar.YEAR);
	    
	    int rosenmontag, fasching, aschermittwoch, karfreitag, ostertag, ostermontag,
        himmelfahrt, pfingsttag, pfingstmontag, fronleichnam;
        //Tage der berechneten Feiertagsdaten
	    int rosenmonat, faschingsmonat, aschermonat, karmonat, ostermonat,
        ostermmonat, himmelmonat, pfingstmonat, pfingstmmonat, fronmonat;
        //Monate der berechneten Feiertagsdaten
	    String feiertagsname; // für die Ausgabe
	    boolean keinFeiertag = true; // für die Ausgabe
	    int m, n, a, b, c, d, e; // interne Größen für den Gauß-Algorithmus
	    boolean schaltjahr; // gibt an, ob betreffendes Jahr Schaltjahr ist
	    
	    
	    //System.out.println ("Bewegliche Feiertage der Osterzeit in den folgenden Jahren:");
	    
	    // BERECHNUNG (Kernalgorithmus):
	    
	    if(jahr >= 1583 && jahr <= 2299){
	    
	   // for (jahr = 1583; jahr <= 2299; jahr++)
	    { // Schleife über alle Jahre im zulässigen Bereich des Gauß-Algorithmus
	    
	      schaltjahr = (((jahr%4) == 0) && ((jahr%100) != 0)) || (jahr%400 == 0);
	      
	      // Berechnung der internen Größen m und n:
	      if (jahr<1700)
	      {
	        m=22; n=2;
	      }
	      else if (jahr < 1800)
	      {
	        m=23; n=3;
	      }
	      else if (jahr < 1900)
	      {
	        m=23; n=4;
	      }
	      else if (jahr < 2100)
	      {
	        m=24; n=5;
	      }
	      else if (jahr < 2200)
	      {
	        m=24; n=6;
	      }
	      else
	      {
	        m=25; n=0;
	      }

	      // Berechnung der internen Größen a,b,c,d,e:
	      a = jahr % 19;
	      b = jahr % 4;
	      c = jahr % 7;
	      d = (19*a+m) % 30;
	      e = (2*b+4*c+6*d+n) % 7;

	      // Berechnung der Feiertage:
	      ostertag = (22+d+e); // gilt nur für ostermonat == 3
	      
	      if (ostertag > 31)
	      { // ostermonat == 4
	      
	        // Berechnung von Ostern:
	        ostermonat = 4;
	        ostertag = (d+e-9); // Korrektur für April
	        if (ostertag == 26)
	          ostertag = 19;
	        else if ((ostertag == 25) && (d==28) && (e==6) && (a>10))
	          ostertag = 18;

	        // Berechnung von Pfingsten (7 Wochen nach Ostern):
	        pfingsttag = ostertag + 19; // 49 Tage danach minus 30 Apriltage
	        if (pfingsttag > 31)
	        {
	          pfingsttag = pfingsttag - 31;
	          pfingstmonat = 6;
	        }
	        else
	          pfingstmonat = 5;

	        // Berechnung von Karfreitag (2 Tage vor Ostern):
	        karfreitag = ostertag - 2;
	        if (karfreitag < 1)
	        {
	          karfreitag = karfreitag + 31;
	          karmonat = 3;
	        }
	        else
	          karmonat = 4;

	        // Berechnung von Ostermontag (1 Tag nach Ostern):
	        ostermontag = ostertag + 1;
	        ostermmonat = 4;

	        // Berechnung von Himmelfahrt (39 Tage nach Ostern):
	        himmelfahrt = ostertag + 9; // 39 Tage danach minus 30 Apriltage
	        if (himmelfahrt > 31)
	        {
	          himmelfahrt = himmelfahrt - 31;
	          himmelmonat = 6;
	        }
	        else
	          himmelmonat = 5;

	        // Berechnung von Pfingstmontag (1 Tag nach Pfingsten):
	        pfingstmontag = pfingsttag + 1;
	        if (pfingstmontag == 32)
	        {
	          pfingstmontag = 1;
	          pfingstmmonat = 6;
	        }
	        else
	          pfingstmmonat = pfingstmonat;

	        // Berechnung von Fronleichnam (11 Tage nach Pfingsten):
	        fronleichnam = ostertag - 1; // 60 Tage nach Ostern minus 30 Apriltage
	                                     // minus 31 Maitage
	        if (fronleichnam == 0)
	        {
	          fronleichnam = 31;
	          fronmonat = 5;
	        }
	        else
	          fronmonat = 6;

	        // Berechnung von Aschermittwoch (46 Tage vor Ostern):
	        if (ostertag >= 16)
	        {
	          aschermittwoch = ostertag - 15; // 46 Tage davor plus 31 Märztage
	          aschermonat = 3;
	        }
	        else
	        {
	          aschermonat = 2;
	          if (schaltjahr)
	            aschermittwoch = ostertag + 14; // 46 Tage davor plus 31 Märztage
	                                            // plus 29 Februartage
	          else
	            aschermittwoch = ostertag + 13; // 46 Tage davor plus 31 Märztage
	                                            // plus 28 Februartage
	        }

	      } // ostermonat == 4
	      else
	      { // ostermonat == 3
	      
	        ostermonat = 3;
	        
	        // Berechnung von Pfingsten (7 Wochen nach Ostern):
	        pfingsttag = ostertag - 12; // 49 Tage danach minus 31 Märztage
	                                    // minus 30 Apriltage
	        pfingstmonat = 5;
	        
	        // Berechnung von Karfreitag (2 Tage vor Ostern):
	        karfreitag = ostertag - 2;
	        karmonat = 3;
	        
	        // Berechnung von Ostermontag (1 Tag nach Ostern):
	        ostermontag = ostertag + 1;
	        if (ostermontag == 32)
	        {
	          ostermontag = 1;
	          ostermmonat = 4;
	        }
	        else
	          ostermmonat = 3;

	        // Berechnung von Himmelfahrt (39 Tage nach Ostern):
	        himmelfahrt = ostertag - 22; // 39 Tage danach minus 31 Märztage
	                                     // minus 30 Apriltage
	        if (himmelfahrt == 0)
	        {
	          himmelfahrt = 30;
	          himmelmonat = 4;
	        }
	        else
	          himmelmonat = 5;

	        // Berechnung von Pfingstmontag (1 Tag nach Pfingsten):
	        pfingstmontag = pfingsttag + 1;
	        pfingstmmonat = 5;
	        
	        // Berechnung von Fronleichnam (11 Tage nach Pfingsten):
	        fronleichnam = ostertag - 1; // 60 Tage nach Ostern minus 31 Märztage
	                                     // minus 30 Apriltage
	        fronmonat = 5;
	        
	        // Berechnung von Aschermittwoch (46 Tage vor Ostern):
	        aschermonat = 2;
	        if (schaltjahr)
	          aschermittwoch = ostertag - 17; // 46 Tage davor plus 29 Februartage
	        else
	          aschermittwoch = ostertag - 18; // 46 Tage davor plus 28 Februartage
	          
	      } // ostermonat == 3
	      
	      // Berechnung von Fasching (1 Tag vor Aschermittwoch):
	      if (aschermittwoch != 1)
	      { // Aschermittwoch != 1. März
	        faschingsmonat = aschermonat;
	        fasching = aschermittwoch - 1;
	      }
	      else
	      { // Aschermittwoch == 1. März
	        faschingsmonat = 2;
	        if (schaltjahr)
	          fasching = 29;
	        else
	          fasching = 28;
	      }

	      // Berechnung von Rosenmontag (1 Tag vor Fasching):
	      if (fasching != 1)
	      { // Fasching != 1. März
	        rosenmonat = faschingsmonat;
	        rosenmontag = fasching - 1;
	      }
	      else
	      { // Fasching == 1. März
	        rosenmonat = 2;
	        if (schaltjahr)
	          rosenmontag = 29;
	        else
	          rosenmontag = 28;
	      }
	      
	      // Hiemit sind alle Feiertage berechnet
	      
	      // Vergleich der Feiertage mit dem Eingabedatum:
	      if ((karfreitag == tag) && (karmonat == monat))
	        feiertagsname = "Karfreitag";
	      else if ((ostertag == tag) && (ostermonat == monat))
	        feiertagsname = "Ostern";
	      else if ((ostermontag == tag) && (ostermmonat == monat))
	        feiertagsname = "Ostermontag";
	      else if ((himmelfahrt == tag) && (himmelmonat == monat))
	        feiertagsname = "Himmelfahrt";
	      else if ((pfingsttag == tag) && (pfingstmonat == monat))
	        feiertagsname = "Pfingsten";
	      else if ((pfingstmontag == tag) && (pfingstmmonat == monat))
	        feiertagsname = "Pfingstmontag";
	      else if ((fronleichnam == tag) && (fronmonat == monat))
	        feiertagsname = "Fronleichnam";
	      else if ((aschermittwoch == tag) && (aschermonat == monat))
	        feiertagsname = "Aschermittwoch";
	      else if ((fasching == tag) && (faschingsmonat == monat))
	        feiertagsname = "Fasching";
	      else if ((rosenmontag == tag) && (rosenmonat == monat))
	        feiertagsname = "Rosenmontag";
	      else
	        feiertagsname = "";
	        
	      // OUTPUT:
	      if (feiertagsname != "")
	      {
	        keinFeiertag = false;
	       // System.out.println (jahr + ": " + feiertagsname);
	        return true;
	      }
	        
	    } // Schleife über alle Jahre im zulässigen Bereich des Gauß-Algorithmus
	    
	    // OUTPUT:
	    if (keinFeiertag){
	      //System.out.println ("Dieser Tag ist in keinem Jahr zwischen 1583 und 2299 ein Feiertag der Osterzeit.");
	      return false;
	    }
	    
	    }
	    return false;
	    
	}
	
	
	public static void main (String args[]) throws IOException
	  {
		
		
		
		Calendar date=Calendar.getInstance();
		date.set(Calendar.YEAR, 2014);
		date.set(Calendar.MONTH, 6);
		date.set(Calendar.DAY_OF_MONTH, 23);
		
		//2014-10-11
		//2014-10-10
		//2014-10-13
		
		Calendar todate=Calendar.getInstance();
		todate.set(Calendar.YEAR, 2014);
		todate.set(Calendar.MONTH, 9);
		todate.set(Calendar.DAY_OF_MONTH, 10);
		
		long inter=countNumberOfOpenMillisBetween(date, todate);
		
		System.out.println("Date: "+dateToString(date)+", is stock market open: "+isStockMarketOpen(date));
		System.out.println("Date: "+dateToString(date)+" to Date:  "+dateToString(todate)+", Nb of day ms: "+inter/86400000);
		
		
		todate=Calendar.getInstance();
		todate.set(Calendar.YEAR, 2014);
		todate.set(Calendar.MONTH, 9);
		todate.set(Calendar.DAY_OF_MONTH, 13);
		
		inter=countNumberOfOpenMillisBetween(date, todate);
		
		System.out.println("Date: "+dateToString(date)+", is stock market open: "+isStockMarketOpen(date));
		System.out.println("Date: "+dateToString(date)+" to Date:  "+dateToString(todate)+", Nb of day ms: "+inter/86400000);
		
		
	  }
		
	
	
}
