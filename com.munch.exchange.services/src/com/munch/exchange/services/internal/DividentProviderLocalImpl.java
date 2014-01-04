package com.munch.exchange.services.internal;

import java.io.File;
import java.util.LinkedList;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.divident.Dividend;
import com.munch.exchange.model.core.divident.HistoricalDividend;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IDividentProvider;
import com.munch.exchange.services.internal.yql.YQLHistoricalData;

public class DividentProviderLocalImpl implements IDividentProvider {
	
	final private static String DividentStr="Divident.xml";
	
	
	private File getLocalDivFile(Stock stock){
		return new File(stock.getDataPath()+File.separator+DividentStr);
	}
	
	private boolean save(Stock stock) {
		if(stock==null)return false;
		
		String divFileStr=getLocalDivFile(stock).getAbsolutePath();
		
		System.out.println("Writing file: "+divFileStr);
		return Xml.save(stock.getHistoricalDividend(), divFileStr);
	}
	
	
	@Override
	public boolean load(Stock stock) {
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localDivFile=getLocalDivFile(stock);
		if(localDivFile.exists()){
			HistoricalDividend hisDiv=new HistoricalDividend();
			if( Xml.load(hisDiv, localDivFile.getAbsolutePath())){
				stock.setHistoricalDividend(hisDiv);
				update(stock);
				return true;
			}
		}
		
		//try to load the data from YQL
		YQLHistoricalData hisData=new YQLHistoricalData(stock.getSymbol(), stock.getStart(), stock.getEnd());
		LinkedList<Dividend> dividents=hisData.getDividendList();
		if(dividents.isEmpty()){
			System.out.println("No divident found for the stock: "+stock.getName()+"("+stock.getSymbol()+")");
			return false;
		}
		else{
			HistoricalDividend hisDiv=new HistoricalDividend();
			hisDiv.addAll(dividents);
			hisDiv.sort();
			stock.setHistoricalDividend(hisDiv);
			if(save(stock))return true;
			
		}
		
		return false;
	}

	@Override
	public boolean update(Stock stock) {
		
		boolean isUpdated=false;
		
		YQLHistoricalData hisData=new YQLHistoricalData(stock.getSymbol(), stock.getHistoricalDividend().getLast().getDate(), stock.getEnd());
		LinkedList<Dividend> dividents=hisData.getDividendList();
		
	//	System.out.println(stock.getHistoricalDividend().getFirst().getDate().getTimeInMillis());
		
		for(Dividend div : dividents){
	//		System.out.println(div.getDate().getTimeInMillis());
			
			if( !stock.getHistoricalDividend().contains(div)){
				stock.getHistoricalDividend().addLast(div);
				stock.getHistoricalDividend().sort();
				isUpdated=true;
			}
		}
		
		if(isUpdated){
			System.out.println("The ExchangeRate was updated:\n \""+stock);
			if(this.save(stock)){
				System.out.println("The divident Data were automaticaly saved!");
			}
			else{
				System.out.println("Error: cannot save the divident data!");
				return false;
			}
		}
		
		
		return false;
	}
	
public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		ExchangeRate rate=provider.load("DAI.DE");
		Stock stock=(Stock) rate;
		
		DividentProviderLocalImpl divProvider=new DividentProviderLocalImpl();
		
		divProvider.load(stock);
		for(DatePoint div:stock.getHistoricalDividend()){
			System.out.println(div);
		}

	}

}
