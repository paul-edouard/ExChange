package com.munch.exchange.services.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.quote.RecordedQuote;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IQuoteProvider;
import com.munch.exchange.services.internal.yql.YQLQuote;
import com.munch.exchange.services.internal.yql.YQLQuotes;

public class QuotePoviderLocalImpl implements IQuoteProvider {
	
	private static Logger logger = Logger.getLogger(QuotePoviderLocalImpl.class);
	
	final private static String QuoteStr="Quote";
	
	private String getSavePath(ExchangeRate rate){
		File dir=new File(rate.getDataPath()+File.separator+QuoteStr);
		if(dir.exists()){
			return dir.getAbsolutePath();
		}
		
		if(dir.mkdirs()){
			return dir.getAbsolutePath();
		}
		return "";
	}
	
	private RecordedQuote lastQuotes(ExchangeRate rate){
		RecordedQuote dayQuotes=new RecordedQuote();
		
		String lastDay=DateTool.dateToDayString(rate.getRecordedQuote().getLast().getDate());
		
		for(DatePoint point:rate.getRecordedQuote()){
			String Day=DateTool.dateToDayString(point.getDate());
			if(lastDay.equals(Day)){
				dayQuotes.add(point);
			}
		}
		dayQuotes.sort();
		
		return dayQuotes;
	}
	
	
	private boolean saveCurrent(ExchangeRate rate){
		
		if(rate==null)return false;
		
		String currentDay=DateTool.dateToDayString(rate.getRecordedQuote().getLast().getDate());
		RecordedQuote dayQuotes=lastQuotes(rate);
		
		File f=new File(getSavePath(rate)+File.separator+String.valueOf(currentDay)+".xml");
		logger.info("Writing file: "+f.getAbsolutePath());
		if(!Xml.save(dayQuotes, f.getAbsolutePath()))
				return false;
		
		
		return true;
	}
	
	private RecordedQuote loadLocalData(ExchangeRate rate){

		File localDir = new File(getSavePath(rate));
		File[] Xmlfiles = localDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});

		if(Xmlfiles.length==0)return null;
		
		RecordedQuote allQuotes = new RecordedQuote();
		for(int i=0;i<Xmlfiles.length;i++){
			RecordedQuote dayQuotes = new RecordedQuote();
			Xml.load(dayQuotes, Xmlfiles[i].getAbsolutePath());
			allQuotes.addAll(dayQuotes);allQuotes.sort();
		}
		
		return allQuotes;
	}
	
	
	@Override
	public boolean load(ExchangeRate rate) {
		if(rate==null)return false;
		if(rate.getDataPath()==null)return false;
		if(rate.getDataPath().isEmpty())return false;
		
		// load from local
		RecordedQuote LocalQuotes=loadLocalData(rate);
		if(LocalQuotes!=null){
			logger.info("Quotes localy found for \""+rate.getFullName());
			rate.setRecordedQuote(LocalQuotes);
			update(rate);
			return true;
		}
		
		update(rate);
		
		return false;
	}
	
	@Override
	public boolean load(List<ExchangeRate> rates) {
		List<ExchangeRate> rateToUpdate=new LinkedList<ExchangeRate>();
		
		for(ExchangeRate rate : rates){
			if(rate==null)continue;
			if(rate.getDataPath()==null)continue;
			if(rate.getDataPath().isEmpty())continue;
			
			// load from local
			RecordedQuote LocalQuotes=loadLocalData(rate);
			if(LocalQuotes!=null){
				logger.info("Quotes localy found for \""+rate.getFullName());
				rate.setRecordedQuote(LocalQuotes);
				rateToUpdate.add(rate);
				continue;
			}
			
			rateToUpdate.add(rate);
		}
		
		return update(rateToUpdate);
		
	}

	@Override
	public boolean update(ExchangeRate rate) {
		boolean isUpdated = false;
		
		YQLQuotes quote=new YQLQuotes(rate.getSymbol());
		QuotePoint point=quote.getCurrentQuotePoint();
		//logger.info("Current Point:"+point);
		if (!rate.getRecordedQuote().contains(point) && point!=null) {
			
			rate.getRecordedQuote().addLast(point);
			rate.getRecordedQuote().sort();
			logger.info("Quote Point added: "+point);
			isUpdated = true;
		}
		rate.getRecordedQuote().setUpdated(isUpdated);
		if(isUpdated){
			logger.info("The ExchangeRate was updated: \""+rate.getFullName());
			if(this.saveCurrent(rate)){
				logger.info("The new quote were automaticaly saved!");
				return true;
			}
			else{
				logger.error("Error: cannot save the updated quote!");
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean update(List<ExchangeRate> rates) {
		
		boolean atLeastOneUpdate = false;
		
		YQLQuote quote=new YQLQuote();
		for(ExchangeRate rate : rates){
			quote.addSymbol(rate.getSymbol());
		}
		HashMap<String, QuotePoint> map=quote.getQuoteMap();
		
		/*
		for(String key:map.keySet()){
			System.out.println("Key: "+key+", Value: "+map.get(key));
		}
		*/
		
		for(ExchangeRate rate : rates){
			//boolean isUpdated = false;
			rate.getRecordedQuote().setUpdated(false);
			
			if(!map.containsKey(rate.getSymbol()))continue;
			
			//System.out.println("Synbol: "+rate.getSymbol());
			
			QuotePoint point=map.get(rate.getSymbol());
			if (!rate.getRecordedQuote().contains(point) && point!=null) {
				
				rate.getRecordedQuote().addLast(point);
				rate.getRecordedQuote().sort();
				logger.info("Quote Point added: "+point);
				rate.getRecordedQuote().setUpdated(true);
				atLeastOneUpdate=true;
			}
			
			if(rate.getRecordedQuote().isUpdated()){
				logger.info("The ExchangeRate was updated: \""+rate.getFullName());
				if(this.saveCurrent(rate)){
					logger.info("The new quote were automaticaly saved!");
					//return true;
				}
				else{
					logger.error("Error: cannot save the updated quote!");
					//return false;
				}
			}
			
			
		}
		
		
		return atLeastOneUpdate;
	}
	
	public static void main(String[] args) {
		
		 BasicConfigurator.configure();
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		ExchangeRate rate=provider.load("EURUSD=X");
		//Stock stock=(Stock) rate;
		
		QuotePoviderLocalImpl dataProvider=new QuotePoviderLocalImpl();
		
		dataProvider.load(rate);
		
		//logger.info(stock.getRecordedQuote().getLast().getParameter().getChild("PercentChangeFromTwoHundreddayMovingAverage").getValue());
		
		/*
		for(HistoricalPoint point:stock.getHistoricalData()){
			logger.info(point);
		}
		*/

	}

}
