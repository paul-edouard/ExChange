package com.munch.exchange.services.internal;

import java.io.File;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.keystat.KeyStatistics;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IKeyStatisticProvider;
import com.munch.exchange.services.internal.yql.YQLKeystat;

public class KeyStatisticsProviderLocalImpl implements IKeyStatisticProvider {
	
	
	final private static String DividentStr="KeyStatistics.xml";
	
	
	private File getLocalDivFile(Stock stock){
		return new File(stock.getDataPath()+File.separator+DividentStr);
	}
	
	private boolean save(Stock stock) {
		if(stock==null)return false;
		
		String divFileStr=getLocalDivFile(stock).getAbsolutePath();
		
		System.out.println("Writing file: "+divFileStr);
		return Xml.save(stock.getKeyStatistics(), divFileStr);
	}
	

	@Override
	public boolean load(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localDivFile=getLocalDivFile(stock);
		if(localDivFile.exists()){
			KeyStatistics ks=new KeyStatistics();
			if( Xml.load(ks, localDivFile.getAbsolutePath())){
				stock.setKeyStatistics(ks);
				System.out.println("Key statistics localy found for "+stock.getFullName());
				update(stock);
				return true;
			}
		}
		
		

		//try to load the data from YQL
		YQLKeystat yKS=new YQLKeystat(stock.getSymbol());
		KeyStatistics ks=yKS.getKeyStatistics();
		if(ks==null && !stock.getParentSymbol().isEmpty()){
			yKS=new YQLKeystat(stock.getParentSymbol());
			ks=yKS.getKeyStatistics();
		}
		if(ks==null){
			System.out.println("No key statistics found for the stock: "+stock.getFullName());
			return false;
		}
		else{
			stock.setKeyStatistics(ks);
			if(save(stock))return true;
			
		}
		
		
		return false;
	}

	@Override
	public boolean update(Stock stock) {
		boolean isUpdated=false;
		
		//try to load the data from YQL
		YQLKeystat yKS=new YQLKeystat(stock.getSymbol());
		KeyStatistics ks=yKS.getKeyStatistics();
		if(ks==null && !stock.getParentSymbol().isEmpty()){
			yKS=new YQLKeystat(stock.getParentSymbol());
			ks=yKS.getKeyStatistics();
		}

		
		if(ks!=null){
			stock.setKeyStatistics(ks);
			isUpdated=true;
		}
		
		
		if(isUpdated){
			System.out.println("The key statistics were updated: \""+stock.getFullName());
			if(this.save(stock)){
				System.out.println("The key statistics Data were automaticaly saved!");
			}
			else{
				System.out.println("Error: cannot save the key statistics data!");
				return false;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		ExchangeRate rate=provider.load("AOI");
		Stock stock=(Stock) rate;
		
		KeyStatisticsProviderLocalImpl ksProvider=new KeyStatisticsProviderLocalImpl();
		ksProvider.load(stock);
		

	}

}
