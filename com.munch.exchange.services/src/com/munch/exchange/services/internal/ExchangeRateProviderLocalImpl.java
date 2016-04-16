package com.munch.exchange.services.internal;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.internal.yql.YQLIsin;
import com.munch.exchange.services.internal.yql.YQLQuotes;
import com.munch.exchange.services.internal.yql.YQLStocks;


public class ExchangeRateProviderLocalImpl implements IExchangeRateProvider {
	
	
	private String workspace;
	final private static String ExchangeRateStr="ExchangeRate.xml";
	
	private HashMap<String, ExchangeRate> RateCacheMap=new HashMap<String, ExchangeRate>();
	
	private static Logger logger = Logger.getLogger(ExchangeRateProviderLocalImpl.class);
	
	@Override
	public void init(String workspace) {
		logger.info("Service initialisation, setting the workspace: "+workspace);
		this.workspace=workspace;
		
	}
	
	private String getExchangeRatePath(ExchangeRate rate) {
		
		if(rate instanceof Commodity){
			return this.workspace + File.separator
					+ rate.getClass().getSimpleName() + File.separator
					+ rate.getName();
		}
		
		return this.workspace + File.separator
				+ rate.getClass().getSimpleName() + File.separator
				+ rate.getSymbol();
	}
	
	/**
	 * return the directory where the data of the ExchangeRate will be saved
	 * @param rate
	 * @return
	 */
	private File getExchangeRateDir(ExchangeRate rate){
		File dir=new File(this.getExchangeRatePath(rate));
		if(!dir.exists()){
			if(dir.mkdirs())
				return dir;
			else return null;
		}
		
		if(!dir.isDirectory())return null;
		return dir;
	}

	public boolean save(ExchangeRate rate) {
		if(rate==null)return false;
		
		File dir=this.getExchangeRateDir(rate);
		if(dir==null)return false;
		String exchangeRateFile=dir.getAbsolutePath()+File.separator+ExchangeRateStr;
		rate.setDataPath(dir.getAbsolutePath());
		logger.info("Writing file: "+exchangeRateFile);
		//System.out.println("Writing file: "+exchangeRateFile);
		
		return Xml.save(rate, exchangeRateFile);
	}
	
	/**
	 * find all exchange rates file saved in the local workspace
	 * 
	 * @return
	 */
	private HashMap<String, File> findAllLocalRateFiles(){
		HashMap<String, File> localRateFiles=new HashMap<String, File>();
		
		File workspaceDir = new File(this.workspace);
		if (!workspaceDir.exists() || !workspaceDir.isDirectory())
			return localRateFiles;
		
		File[] subDirs = workspaceDir.listFiles();
		if (subDirs.length == 0)
			return localRateFiles;
		for (int i = 0; i < subDirs.length; i++) {
			File subDir = subDirs[i];
			if (!subDir.isDirectory())
				continue;
			
			File[] XChangeDirs = subDir.listFiles();

			for (int j = 0; j < XChangeDirs.length; j++) {
				
				if (!XChangeDirs[j].isDirectory())continue;
				
				String symbol=XChangeDirs[j].getName();
				
				File localFile=new File(XChangeDirs[j].getAbsolutePath()+File.separator+ExchangeRateStr);
				
				if(localFile.exists() && localFile.canRead()){
					
					localRateFiles.put(symbol, localFile);
				}
					
			}
		}
		
		return localRateFiles;
	}
	
	private ExchangeRate findLocalRateFromSymbol(String symbol){
		
		HashMap<String, File> localRateFiles=findAllLocalRateFiles();
		if(!localRateFiles.containsKey(symbol))
			return null;
		
		String localFile=localRateFiles.get(symbol).getAbsolutePath();
		String rateClassName=localRateFiles.get(symbol).getParentFile().getParentFile().getName();
		ExchangeRate XRate=null;
		
		if(!localFile.isEmpty() && !rateClassName.isEmpty()){
			if(Stock.class.getSimpleName().equals(rateClassName)){
				XRate=new Stock();
			}
			
			if(Indice.class.getSimpleName().equals(rateClassName)){
				XRate=new Indice();
			}
			
			if(Fund.class.getSimpleName().equals(rateClassName)){
				XRate=new Fund();
			}
			
			if(Commodity.class.getSimpleName().equals(rateClassName)){
				XRate=new Commodity();
			}
			
			if(Currency.class.getSimpleName().equals(rateClassName)){
				XRate=new Currency();
			}
			
			if(EconomicData.class.getSimpleName().equals(rateClassName)){
				XRate=new EconomicData();
			}
			
			
			if(XRate!=null && Xml.load(XRate, localFile)){
				XRate.setDataPath(localRateFiles.get(symbol).getParent());
				return XRate;
			}
			
		}
		
		return null;
		
	}
	
	/**
	 * test if a given symbol is already used from a exchange rate
	 */
	@Override
	public boolean isSymbolAlreadyUsed(String symbol) {
		HashMap<String, File> map=findAllLocalRateFiles();
		return map.containsKey(symbol);
	}
	
	
	/**
	 * load a rate from a given symbol string.
	 * If the symbol contains ";" then a rate of type Commodity will be assumed
	 * Ex: Commodity_Name;Yql_Symbol
	 */
	@Override
	public synchronized ExchangeRate load(String symbol) {
		//Try to find the Exchange rate from cache
		//Find directly from the UUID
		if(RateCacheMap.containsKey(symbol))
			return RateCacheMap.get(symbol);
		//Find from the symbol
		for(ExchangeRate rate:RateCacheMap.values()){
			//logger.info("Searching value: "+rate.getSymbol()+ " test:"+symbol);
			if(rate.getSymbol().equals(symbol))
				return rate;
		}
		
		
		//Try to load the exchange rate from the local data
		ExchangeRate xchangeRate=findLocalRateFromSymbol(symbol.split(";")[0]);
		if(xchangeRate!=null){
			logger.info("The exchange rate was found localy: "+xchangeRate.getFullName());
			//Add the rate in the map
			RateCacheMap.put(xchangeRate.getUUID(), xchangeRate);
			
			update(xchangeRate);
			return xchangeRate;
		}
		
		return null;
		
		//Test if the string contains ";". In this case the first loading of a commodity will be assumed
		//Name;YqlId;OnVistaId
		//For Gold "Gold;GCJ14.CMX;24877915"
		/*
		ExchangeRate rate=loadExchangeRateOrCommodity(symbol);
		if(rate!=null)return rate;
		
		//Loading economic data
		rate=loadEconomicData(symbol);
		if(rate!=null)return rate;
		
		//Try to load the given symbol directly from YQL
		YQLStocks yqlStocks=new YQLStocks(symbol);
		YQLQuotes yqlQuotes=new YQLQuotes(symbol);
		if(!yqlStocks.hasValidResult() && !yqlQuotes.hasValidResult()){
			logger.info("Cannot find the symbol \""+symbol+"\" on YQL");
			return null;
		}
		
		//ExchangeRate rate=null;
		
		if(yqlStocks.hasValidResult()){
			rate=yqlStocks.getExchangeRate();
			//Search the company name
			if(rate!=null && yqlQuotes.hasValidResult()){
				rate.setName(yqlQuotes.getName());
				rate.setStockExchange(yqlQuotes.getStockExchange());
			}
		}
		else{
			rate=new Indice();
			rate.setName(yqlQuotes.getName());
			rate.setSymbol(symbol);
			rate.setStockExchange(yqlQuotes.getStockExchange());
			
		}
		
		if(rate==null)return null;
		
		//Save the new Exchange Rate:
		if(this.save(rate)){
			//Add the rate in the map
			RateCacheMap.put(rate.getUUID(), rate);
			
			return rate;
		}
		else
			return null;
			*/
		
	}
	
/*
	private ExchangeRate loadEconomicData(String symbol){
		if(!symbol.startsWith(EconomicData.FRED_SYMBOL_PREFIX))return null;
		
		//Get the series id
		String[] data = symbol.split("_");
		if(data.length<2)return null;
		String seriesId="";
		for(int i=1;i<data.length;i++){seriesId+=data[i];}
		if(seriesId.isEmpty())return null;
		
		//Search the series on FRED
		logger.info("Economic Data string recognize: " + seriesId);
		FredSeries f_s=new FredSeries(seriesId);
		EconomicData ec_data=f_s.getEconomicData();
		
		//Add the rate in the map
		RateCacheMap.put(ec_data.getUUID(), ec_data);
		
		update(ec_data);
		
		return ec_data;
		
	}
	*/
	
	
	/**
	 * search the given symbol on Yql but also on Vista
	 * @param symbol
	 * @return
	 */
	private ExchangeRate loadExchangeRateOrCommodity(String symbol){
		if (symbol.contains(";")) {
			String[] data = symbol.split(";");

			if (data[0].endsWith("=X")) {
				logger.info("Currency string recognize: " + symbol);
				Currency rate = new Currency();
				rate.setSymbol(data[0]);
				rate.setName(data[0].substring(0,3)+" to "+data[0].substring(3,6));
				
				if (data.length > 1)
					rate.setOnVistaId(data[1]);
				
				//Add the rate in the map
				RateCacheMap.put(rate.getUUID(), rate);
				
				update(rate);
				return rate;

			} else {
				logger.info("Commodity string recognize: " + symbol);
				Commodity rate = new Commodity();
				rate.setName(data[0]);

				if (data.length > 1) {
					rate.setSymbol(data[1]);
					YQLQuotes yqlQuotes = new YQLQuotes(data[1]);
					if (yqlQuotes.hasValidResult()) {
						rate.setStockExchange(yqlQuotes.getStockExchange());
					}
				}
				if (data.length > 2)
					rate.setOnVistaId(data[2]);
				
				//Add the rate in the map
				RateCacheMap.put(rate.getUUID(), rate);
				

				update(rate);
				return rate;
			}
		}
		return null;
	}
	
	public Stock loadStock(String symbol){
		YQLStocks yqlStocks=new YQLStocks(symbol);
		YQLQuotes yqlQuotes=new YQLQuotes(symbol);
		if(!yqlStocks.hasValidResult() && !yqlQuotes.hasValidResult()){
			logger.info("Cannot find the symbol \""+symbol+"\" on YQL");
			return null;
		}
		
		ExchangeRate rate=yqlStocks.getExchangeRate();
		
		if(yqlStocks.hasValidResult()){
			rate=yqlStocks.getExchangeRate();
			//Search the company name
			if(rate!=null && yqlQuotes.hasValidResult()){
				rate.setName(yqlQuotes.getName());
				rate.setStockExchange(yqlQuotes.getStockExchange());
			}
			
			if(rate instanceof Stock){
				return (Stock) rate;
			}
		}
	
		
		return null;
	}
	
	@Override
	public synchronized boolean update(ExchangeRate rate) {
		
		boolean isUpdated=false;
		
		/*
		Calendar today=Calendar.getInstance();
		
		if(rate.getLastUpdate()!=null && 
				DateTool.dateToDayString( rate.getLastUpdate()).equals(DateTool.dateToDayString(today))){
			return isUpdated;
		}
		else{
			rate.setLastUpdate(today);
			isUpdated=true;
		}
		*/
		
		// Update the End Date from YQL
		if(/*rate instanceof Stock || */rate instanceof Fund){
			
			
			YQLStocks yqlStocks=new YQLStocks(rate.getSymbol());
			YQLQuotes yql_Quotes=new YQLQuotes(rate.getSymbol());
			if(!yqlStocks.hasValidResult() && yql_Quotes.getResult()==null){
				logger.info("Cannot find the symbol \""+rate.getSymbol()+"\" on YQL");
				//System.out.println("Cannot find the symbol \""+rate.getSymbol()+"\" on YQL");
				return isUpdated;
			}
			
			
			if(!rate.getEnd().equals(yqlStocks.getEndDate())){
				rate.setEnd(yqlStocks.getEndDate());
				isUpdated=true;
			}
			
			if(rate instanceof Stock){
			Stock stock=(Stock)rate;
			if(stock.isParentUpdateNeeded()){
				YQLStocks yqlStockParent=new YQLStocks(stock.getParentSymbol());
				if(!yqlStockParent.hasValidResult()){
					logger.info("Cannot find the given parent symbol \""+stock.getParentSymbol()+"\" on YQL");
					//System.out.println("Cannot find the given parent symbol \""+stock.getParentSymbol()+"\" on YQL");
				}
				else{
					YQLQuotes yqlQuotes=new YQLQuotes(stock.getParentSymbol());
					if(!stock.getParentName().equals(yqlQuotes.getName())){
						stock.setParentName(yqlQuotes.getName());isUpdated=true;
					}
					
					if(stock.getSector()==null || stock.getSector().isEmpty()){
						stock.setSector(yqlStockParent.getSector());isUpdated=true;
					}
					
					if(stock.getIndustry()==null || stock.getIndustry().isEmpty()){
						stock.setIndustry(yqlStockParent.getIndustry());isUpdated=true;
					}
					
				}
				
			}
			}
			
			
		}
		else if(rate instanceof Stock){
			Stock stock=(Stock)rate;
			stock.setEnd(Calendar.getInstance());
			isUpdated=true;
		}
		//Update for Indice
		else if(rate instanceof Indice){
			Indice indice=(Indice)rate;
			indice.setEnd(Calendar.getInstance());
			isUpdated=true;
		}
		else if(rate instanceof Commodity){
			Commodity com=(Commodity) rate;
			com.setEnd(Calendar.getInstance());
			isUpdated=true;
		}
		else if(rate instanceof Currency){
			Currency com=(Currency) rate;
			com.setEnd(Calendar.getInstance());
			isUpdated=true;
		}
		/*
		else if(rate instanceof EconomicData){
			EconomicData com=(EconomicData) rate;
			
			FredSeries f_s=new FredSeries(com);
			EconomicData n_ed=f_s.getEconomicData();
			if(n_ed!=null)
				com.setEnd(n_ed.getEnd());
			
			isUpdated=true;
		}
		*/
		
		
		
		if(isUpdated){
			logger.info("The ExchangeRate was updated: "+rate.getFullName());
			if(this.save(rate)){
				logger.info("The new Data were automaticaly saved!");
			}
			else{
				logger.info("Error: cannot save the updated data!");
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public synchronized LinkedList<ExchangeRate> loadAll(Class<? extends ExchangeRate> clazz) {
		
		LinkedList<ExchangeRate> rates=new LinkedList<ExchangeRate>();
		
		String path= this.workspace + File.separator
				+ clazz.getSimpleName();
		File dir=new File(path);
		File[] er_dirs=dir.listFiles();
		if(er_dirs==null)return rates;
		for(int i=0;i<er_dirs.length;i++){
			if(er_dirs[i].isDirectory()){	
				ExchangeRate rate=this.load(er_dirs[i].getName());
				if(rate!=null){
					rates.add(rate);
				}
			}
		}
		
		return rates;
	}
	
	@Override
	public synchronized boolean delete(ExchangeRate rate) {
		String path=getExchangeRatePath(rate);
		if(deleteDirRec(new File(path))){
			this.RateCacheMap.remove(rate.getUUID());
			return true;
		}
		
		return false;
	}
	
	private boolean deleteDirRec(File dir){
		File[] files=dir.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isFile()){
				files[i].delete();}
			else{
				deleteDirRec(files[i]);
			}
		}
		
		return dir.delete();
		
	}
	
	@Override
	public synchronized LinkedList<ExchangeRate> getCachedRates() {
		LinkedList<ExchangeRate> rates=new LinkedList<ExchangeRate>();
		rates.addAll(RateCacheMap.values());
		return rates;
	}
	
	
	public static void main(String[] args) {
		
		//ExchangeRateProviderLocalImpl.logger.setLevel(Level.INFO);
		
		// Set up a simple configuration that logs on the console.
	    BasicConfigurator.configure();

		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		//provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		provider.init("C:\\Documents and Settings\\Polo\\Bureau\\ExChangeData");
		//provider.save(rate);
		
		ExchangeRate rate=provider.load("xcbv");
		if(rate==null){
			logger.info("Data no found");
		}
		
		//ExchangeRate rate=provider.load("^GDAXI");
		
		//ExchangeRate rate=provider.load("Gold;GCJ14.CMX;24877915");
		//ExchangeRate rate=provider.load("Gold;GCJ14.CMX;24877915");
		
		//ExchangeRate rate=provider.load("Gold");
		
		
		//provider.loadAll(Stock.class);
		
		/*
		if(rate instanceof Stock){
			Stock stock=(Stock)rate;stock.setParentSymbol("AMZN");
			provider.update(stock);
		}
		*/
		
		//provider.save(rate);
		
		

	}

	@Override
	public String getYahooSymbolFromIsin(String isin) {
		YQLIsin y_i=new YQLIsin(isin);
		if(!y_i.hasValidResult())return "";
		return y_i.getYahooSymbol();
	}


	


	


	


	


	
}
