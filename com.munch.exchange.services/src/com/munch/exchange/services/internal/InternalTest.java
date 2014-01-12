package com.munch.exchange.services.internal;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;

public class InternalTest {

	public static void main(String[] args) {
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		//AAPL
		//R
		//O
		//String[] list={"AAPL","R","O","GOOG","L","P","A","H", "^GDAXI","^TECDAX","OIL","CTYRX"};
		String[] list={"^GDAXI","^TECDAX","OIL","CTYRX"};
		
		
		for(int i=0;i<list.length;i++){
		
		System.out.println("\nGetting Data: "+list[i]);
			
		ExchangeRate rate=provider.load(list[i]);
		
		if(rate instanceof Stock){
		
		Stock stock=(Stock) rate;
		//Financials
		FinancialsProviderLocalImpl dataProvider=new FinancialsProviderLocalImpl();
		dataProvider.loadIncomeStatement(stock);
		dataProvider.loadBalanceSheet(stock);
		dataProvider.loadCashFlow(stock);

		//Divident
		DividentProviderLocalImpl divProvider = new DividentProviderLocalImpl();
		divProvider.load(stock);
		
		//Historical Data
		HistoricalDataProviderLocalImpl hisProvider = new HistoricalDataProviderLocalImpl();
		hisProvider.load(stock);
		
		//Quote
		QuotePoviderLocalImpl quoteProvider = new QuotePoviderLocalImpl();
		quoteProvider.load(stock);
		
		//Analyst Estimation
		AnalystEstimationProviderLocalImpl aeProvider=new AnalystEstimationProviderLocalImpl();
		aeProvider.load(stock);
		
		//Key Statistics
		KeyStatisticsProviderLocalImpl ksProvider=new KeyStatisticsProviderLocalImpl();
		ksProvider.load(stock);
		
		}
		else if(rate instanceof Indice){
			Indice indice=(Indice) rate;
			
			//Historical Data
			HistoricalDataProviderLocalImpl hisProvider = new HistoricalDataProviderLocalImpl();
			hisProvider.load(indice);
			//Quote
			QuotePoviderLocalImpl quoteProvider = new QuotePoviderLocalImpl();
			quoteProvider.load(indice);
		
		}
		else if(rate instanceof Fund){
			Fund fund=(Fund) rate;
			
			//Historical Data
			HistoricalDataProviderLocalImpl hisProvider = new HistoricalDataProviderLocalImpl();
			hisProvider.load(fund);
			//Quote
			QuotePoviderLocalImpl quoteProvider = new QuotePoviderLocalImpl();
			quoteProvider.load(fund);
			
			
		}
		}

	}

}
