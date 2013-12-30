package com.munch.exchange.services.internal;

import java.io.File;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.internal.yql.YQLQuotes;
import com.munch.exchange.services.internal.yql.YQLStocks;

public class ExchangeRateProviderLocalImpl implements IExchangeRateProvider {
	
	
	private String workspace;
	final private static String ExchangeRateStr="ExchangeRate.xml";
	final private static String DividentStr="Divident.xml";
	
	
	@Override
	public void init(String workspace) {
		this.workspace=workspace;
	}
	
	
	private String getExchangeRatePath(ExchangeRate rate) {
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
	
	@Override
	public boolean save(ExchangeRate rate) {
		if(rate==null)return false;
		
		File dir=this.getExchangeRateDir(rate);
		if(dir==null)return false;
		String exchangeRateFile=dir.getAbsolutePath()+File.separator+ExchangeRateStr;
		System.out.println(exchangeRateFile);
		// TODO Auto-generated method stub
		return Xml.save(rate, exchangeRateFile);
	}

	@Override
	public ExchangeRate load(String symbol) {
		
		//TODO load from local
		
		YQLStocks yqlStocks=new YQLStocks(symbol);
		System.out.println(yqlStocks);
		if(!yqlStocks.hasValidResult()){
			System.out.println("Cannot find the symbol \""+symbol+"\" on YQL");
			return null;
		}
		
		YQLQuotes yqlQuotes=new YQLQuotes(symbol);
		ExchangeRate rate=yqlStocks.getExchangeRate();
		if(rate!=null)
		rate.setName(yqlQuotes.getName());
		
		return rate;
		
	}

	@Override
	public boolean update(ExchangeRate rate) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		//provider.save(rate);
		
		ExchangeRate rate=provider.load("DAI.DE");
		provider.save(rate);
		
		

	}

}
