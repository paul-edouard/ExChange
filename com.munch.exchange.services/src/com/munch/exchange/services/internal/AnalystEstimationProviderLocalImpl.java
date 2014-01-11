package com.munch.exchange.services.internal;

import java.io.File;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.analystestimation.AnalystEstimation;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IAnalystEstimationProvider;
import com.munch.exchange.services.internal.yql.YQLAnalystEstimate;

public class AnalystEstimationProviderLocalImpl implements
		IAnalystEstimationProvider {
	
	final private static String DividentStr="AnalystEstimation.xml";
	
	
	private File getLocalDivFile(Stock stock){
		return new File(stock.getDataPath()+File.separator+DividentStr);
	}
	
	private boolean save(Stock stock) {
		if(stock==null)return false;
		
		String divFileStr=getLocalDivFile(stock).getAbsolutePath();
		
		System.out.println("Writing file: "+divFileStr);
		return Xml.save(stock.getAnalystEstimation(), divFileStr);
	}

	@Override
	public boolean load(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localDivFile=getLocalDivFile(stock);
		if(localDivFile.exists()){
			AnalystEstimation AE=new AnalystEstimation();
			if( Xml.load(AE, localDivFile.getAbsolutePath())){
				stock.setAnalystEstimation(AE);
				System.out.println("Analyst estimation localy found for "+stock.getFullName());
				update(stock);
				return true;
			}
		}
		
		

		//try to load the data from YQL
		YQLAnalystEstimate yAE=new YQLAnalystEstimate(stock.getSymbol());
		AnalystEstimation AE=yAE.getEstimation();
		if(AE==null){
			System.out.println("No analyst estimation found for the stock: "+stock.getFullName());
			return false;
		}
		else{
			stock.setAnalystEstimation(AE);
			if(save(stock))return true;
			
		}
		
		
		return false;
	}

	@Override
	public boolean update(Stock stock) {
		boolean isUpdated=false;
		
		//try to load the data from YQL
		YQLAnalystEstimate yAE=new YQLAnalystEstimate(stock.getSymbol());
		AnalystEstimation AE=yAE.getEstimation();
		if(AE!=null){
			if(stock.getAnalystEstimation().update(AE)){
				isUpdated=true;
			}
		}
		
		
		if(isUpdated){
			System.out.println("The analyst estimate were updated:\n \""+stock);
			if(this.save(stock)){
				System.out.println("The analyst estimate Data were automaticaly saved!");
			}
			else{
				System.out.println("Error: cannot save the analyst estimate data!");
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
		
		AnalystEstimationProviderLocalImpl aeProvider=new AnalystEstimationProviderLocalImpl();
		aeProvider.load(stock);
		

	}


}
