package com.munch.exchange.services.internal;

import java.io.File;
import java.util.LinkedList;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.HistoricalIncomeStatement;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IFinancialsProvider;
import com.munch.exchange.services.internal.yql.YQLIncomeStatement;

public class FinancialsProviderLocalImpl implements IFinancialsProvider {
	
	final private static String FinancialsPathStr="Financials";
	final private static String IncomeStatementStr="IncomeStatement.xml";
	
	
	private String getSavePath(Stock stock){
		File dir=new File(stock.getDataPath()+File.separator+FinancialsPathStr);
		if(dir.exists()){
			return dir.getAbsolutePath();
		}
		
		if(dir.mkdirs()){
			return dir.getAbsolutePath();
		}
		return "";
	}
	
	private String getFileName(Stock stock, String str){
		return this.getSavePath(stock)+File.separator+str;
	}
	
	private boolean saveIncomeStatement(Stock stock){
		if(stock==null)return false;
		
		String fileStr=getFileName(stock,IncomeStatementStr);
		
		System.out.println("Writing file: "+fileStr);
		return Xml.save(stock.getHistoricalIncomeStatement(), fileStr);
	}
	

	@Override
	public boolean loadIncomeStatement(Stock stock) {
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,IncomeStatementStr));
		if(localFile.exists()){
			HistoricalIncomeStatement his=new HistoricalIncomeStatement();
			if( Xml.load(his, localFile.getAbsolutePath())){
				stock.setHistoricalIncomeStatement(his);
				/*
				System.out.println("Point Loaded");
				for(DatePoint point:stock.getHistoricalIncomeStatement()){
					System.out.println(point);
				}
				System.out.println("End of loading");
				*/
				updateIncomeStatement(stock);
				return true;
			}
		}
		
		//try to load the data from YQL
		
		
		LinkedList<IncomeStatementPoint> last_points=getLastIncomeStatementPoints(stock);
		
		if(last_points.isEmpty()){
			System.out.println("No income statement found for the stock: "+stock.getFullName());
			return false;
		}
		else{
			HistoricalIncomeStatement his=new HistoricalIncomeStatement();
			his.addAll(last_points);
			his.sort();
			stock.setHistoricalIncomeStatement(his);
			if(saveIncomeStatement(stock))return true;
			
		}
		
		
		return false;
	}
	
	private LinkedList<IncomeStatementPoint> getLastIncomeStatementPoints(Stock stock){
		YQLIncomeStatement yql=new YQLIncomeStatement(stock.getSymbol());
		yql.setTimeFrameToQuaterly();
		LinkedList<IncomeStatementPoint> q_points=yql.getPointList();
		yql.resetResult();
		yql.setTimeFrameToAnnual();
		LinkedList<IncomeStatementPoint> a_points=yql.getPointList();
		
		LinkedList<IncomeStatementPoint> res=new LinkedList<IncomeStatementPoint>();
		
		res.addAll(a_points);res.addAll(q_points);
		
		return res;
	}

	@Override
	public boolean updateIncomeStatement(Stock stock) {
		boolean isUpdated = false;
		
		LinkedList<IncomeStatementPoint> last_points=getLastIncomeStatementPoints(stock);
		for(IncomeStatementPoint point:last_points){
			//System.out.println("Compare To: "+stock.getHistoricalIncomeStatement().size());
			if(!stock.getHistoricalIncomeStatement().contains(point)){
				stock.getHistoricalIncomeStatement().add(point);
				stock.getHistoricalIncomeStatement().sort();
				System.out.println("Income Statement Point added: "+point);
				isUpdated = true;
			}
		}
		
		if(isUpdated){
			System.out.println("The Stock was updated: \""+stock.getFullName());
			if(this.saveIncomeStatement(stock)){
				System.out.println("The new Data were automaticaly saved!");
			}
			else{
				System.out.println("Error: cannot save the updated data!");
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
		
		FinancialsProviderLocalImpl dataProvider=new FinancialsProviderLocalImpl();
		
		dataProvider.loadIncomeStatement(stock);
		/*
		for(HistoricalPoint point:stock.getHistoricalData()){
			System.out.println(point);
		}
		*/

	}

}
