package com.munch.exchange.services.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.BalanceSheetPoint;
import com.munch.exchange.model.core.financials.CashFlowPoint;
import com.munch.exchange.model.core.financials.HistoricalBalanceSheet;
import com.munch.exchange.model.core.financials.HistoricalCashFlow;
import com.munch.exchange.model.core.financials.HistoricalIncomeStatement;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IFinancialsProvider;
import com.munch.exchange.services.internal.web.HtmlFunctions;
import com.munch.exchange.services.internal.web.PdfFunctions;
import com.munch.exchange.services.internal.yql.YQLBalanceSheet;
import com.munch.exchange.services.internal.yql.YQLCashFlow;
import com.munch.exchange.services.internal.yql.YQLIncomeStatement;

public class FinancialsProviderLocalImpl implements IFinancialsProvider {
	
	final private static String FinancialsPathStr="Financials";
	final private static String IncomeStatementStr="IncomeStatement.xml";
	final private static String BalanceSheetStr="BalanceSheet.xml";
	final private static String CashFlowStr="CashFlow.xml";
	final private static String ReportReaderConfigurationStr="ReportReaderConfiguration.xml";
	
	
	
	private static Logger logger = Logger.getLogger(FinancialsProviderLocalImpl.class);
	
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
		
		logger.info("Writing file: "+fileStr);
		return Xml.save(stock.getFinancials().getIncomeStatement(), fileStr);
	}
	
	
	private boolean saveBalanceSheet(Stock stock){
		if(stock==null)return false;
		
		String fileStr=getFileName(stock,BalanceSheetStr);
		
		logger.info("Writing file: "+fileStr);
		return Xml.save(stock.getFinancials().getBalanceSheet(), fileStr);
	}
	
	public boolean saveReportReaderConfiguration(Stock stock){
		if(stock==null)return false;
		
		String fileStr=getFileName(stock,ReportReaderConfigurationStr);
		
		logger.info("Writing file: "+fileStr);
		return Xml.save(stock.getFinancials().getReportReaderConfiguration(), fileStr);
	}
	
	private boolean saveCashFlow(Stock stock){
		if(stock==null)return false;
		
		String fileStr=getFileName(stock,CashFlowStr);
		
		logger.info("Writing file: "+fileStr);
		return Xml.save(stock.getFinancials().getCashFlow(), fileStr);
	}
	
	public boolean saveAll(Stock stock){
		if(!saveIncomeStatement(stock))return false;
		if(!saveBalanceSheet(stock))return false;
		if(!saveCashFlow(stock))return false;
		if(!saveReportReaderConfiguration(stock))return false;
		
		return true;
	}
	
	
	private LinkedList<IncomeStatementPoint> getLastIncomeStatementPoints(Stock stock){
		YQLIncomeStatement yql=new YQLIncomeStatement(stock.getSymbol());
		yql.setTimeFrameToQuaterly();
		LinkedList<IncomeStatementPoint> q_points=yql.getPointList();
		yql.resetResult();
		yql.setTimeFrameToAnnual();
		LinkedList<IncomeStatementPoint> a_points=yql.getPointList();
		
		//Try to find value from the parent
		if(q_points.isEmpty() && a_points.isEmpty() && !stock.getParentName().isEmpty()){
			yql=new YQLIncomeStatement(stock.getParentName());
			yql.setTimeFrameToQuaterly();
			q_points=yql.getPointList();
			yql.resetResult();
			yql.setTimeFrameToAnnual();
			a_points=yql.getPointList();
		}
		
		LinkedList<IncomeStatementPoint> res=new LinkedList<IncomeStatementPoint>();
		
		res.addAll(a_points);res.addAll(q_points);
		
		return res;
	}
	
	private LinkedList<CashFlowPoint> getLastCashFlowPoints(Stock stock){
		YQLCashFlow yql=new YQLCashFlow(stock.getSymbol());
		yql.setTimeFrameToQuaterly();
		LinkedList<CashFlowPoint> q_points=yql.getPointList();
		yql.resetResult();
		yql.setTimeFrameToAnnual();
		LinkedList<CashFlowPoint> a_points=yql.getPointList();
		
		//Try to find value from the parent
		if(q_points.isEmpty() && a_points.isEmpty() && !stock.getParentName().isEmpty()){
				yql=new YQLCashFlow(stock.getParentName());
				yql.setTimeFrameToQuaterly();
				q_points=yql.getPointList();
				yql.resetResult();
				yql.setTimeFrameToAnnual();
				a_points=yql.getPointList();
		}
		
		
		LinkedList<CashFlowPoint> res=new LinkedList<CashFlowPoint>();
		
		res.addAll(a_points);res.addAll(q_points);
		
		return res;
	}
	
	private LinkedList<BalanceSheetPoint> getLastBalanceSheetPoints(Stock stock){
		YQLBalanceSheet yql=new YQLBalanceSheet(stock.getSymbol());
		yql.setTimeFrameToQuaterly();
		LinkedList<BalanceSheetPoint> q_points=yql.getPointList();
		yql.resetResult();
		yql.setTimeFrameToAnnual();
		LinkedList<BalanceSheetPoint> a_points=yql.getPointList();
		
		//Try to find value from the parent
		if(q_points.isEmpty() && a_points.isEmpty() && !stock.getParentName().isEmpty()){
				yql=new YQLBalanceSheet(stock.getParentName());
				yql.setTimeFrameToQuaterly();
				q_points=yql.getPointList();
				yql.resetResult();
				yql.setTimeFrameToAnnual();
				a_points=yql.getPointList();
		}
		
		
		LinkedList<BalanceSheetPoint> res=new LinkedList<BalanceSheetPoint>();
		
		res.addAll(a_points);res.addAll(q_points);
		
		return res;
	}
	
	
	@Override
	public boolean loadReportReaderConfiguration(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,ReportReaderConfigurationStr));
		if(localFile.exists()){
			ReportReaderConfiguration reportReaderConfiguration=new ReportReaderConfiguration();
			if( Xml.load(reportReaderConfiguration, localFile.getAbsolutePath())){
				stock.getFinancials().setReportReaderConfiguration(reportReaderConfiguration);
				logger.info("Report Reader Configuration localy found for "+stock.getFullName());
				
				updateIncomeStatement(stock);
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public boolean loadIncomeStatement(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,IncomeStatementStr));
		if(localFile.exists()){
			HistoricalIncomeStatement his=new HistoricalIncomeStatement();
			if( Xml.load(his, localFile.getAbsolutePath())){
				stock.getFinancials().setIncomeStatement(his);
				logger.info("Income statement localy found for "+stock.getFullName());
				
				updateIncomeStatement(stock);
				return true;
			}
		}
		
		//try to load the data from YQL
		
		
		LinkedList<IncomeStatementPoint> last_points=getLastIncomeStatementPoints(stock);
		
		if(last_points.isEmpty()){
			logger.info("No income statement found for the stock: "+stock.getFullName());
			return false;
		}
		else{
			HistoricalIncomeStatement his=new HistoricalIncomeStatement();
			his.addAll(last_points);
			his.sort();
			stock.getFinancials().setIncomeStatement(his);
			if(saveIncomeStatement(stock))return true;
			
		}
		
		
		return false;
	}
	
	

	@Override
	public boolean updateIncomeStatement(Stock stock) {
		boolean isUpdated = false;
		
		LinkedList<IncomeStatementPoint> last_points=getLastIncomeStatementPoints(stock);
		for(IncomeStatementPoint point:last_points){
			//System.out.println("Compare To: "+stock.getHistoricalIncomeStatement().size());
			if(!stock.getFinancials().getIncomeStatement().contains(point)){
				stock.getFinancials().getIncomeStatement().add(point);
				stock.getFinancials().getIncomeStatement().sort();
				logger.info("Income Statement Point added: "+point);
				isUpdated = true;
			}
		}
		
		if(isUpdated){
			System.out.println("The Stock was updated: \""+stock.getFullName());
			if(this.saveIncomeStatement(stock)){
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
	public boolean loadBalanceSheet(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,BalanceSheetStr));
		if(localFile.exists()){
			HistoricalBalanceSheet his=new HistoricalBalanceSheet();
			if( Xml.load(his, localFile.getAbsolutePath())){
				stock.getFinancials().setBalanceSheet(his);
				logger.info("Balance sheet localy found for "+stock.getFullName());
				updateBalanceSheet(stock);
				return true;
			}
		}
		
		//try to load the data from YQL

		LinkedList<BalanceSheetPoint> last_points=getLastBalanceSheetPoints(stock);
		
		if(last_points.isEmpty()){
			logger.info("No income statement found for the stock: "+stock.getFullName());
			return false;
		}
		else{
			HistoricalBalanceSheet his=new HistoricalBalanceSheet();
			his.addAll(last_points);
			his.sort();
			stock.getFinancials().setBalanceSheet(his);
			if(saveBalanceSheet(stock))return true;
			
		}
		
		
		return false;
	}

	@Override
	public boolean updateBalanceSheet(Stock stock) {
		boolean isUpdated = false;
		
		/*
		for(DatePoint point :stock.getFinancials().getBalanceSheet() ){
			System.out.println(point);
		}
		*/
		
		LinkedList<BalanceSheetPoint> last_points=getLastBalanceSheetPoints(stock);
		for(BalanceSheetPoint point:last_points){
			//System.out.println("Compare To: "+stock.getHistoricalBalanceSheet().size());
			if(!stock.getFinancials().getBalanceSheet().contains(point)){
				stock.getFinancials().getBalanceSheet().add(point);
				stock.getFinancials().getBalanceSheet().sort();
				logger.info("Balance Sheet Point added: "+point);
				isUpdated = true;
			}
		}
		
		if(isUpdated){
			logger.info("The Stock was updated: \""+stock.getFullName());
			if(this.saveBalanceSheet(stock)){
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
	public boolean loadCashFlow(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,CashFlowStr));
		if(localFile.exists()){
			HistoricalCashFlow his=new HistoricalCashFlow();
			if( Xml.load(his, localFile.getAbsolutePath())){
				stock.getFinancials().setCashFlow(his);
				logger.info("Cash flow localy found for "+stock.getFullName());
				updateCashFlow(stock);
				return true;
			}
		}
		
		//try to load the data from YQL
		
		
		LinkedList<CashFlowPoint> last_points=getLastCashFlowPoints(stock);
		
		if(last_points.isEmpty()){
			logger.info("No income statement found for the stock: "+stock.getFullName());
			return false;
		}
		else{
			HistoricalCashFlow his=new HistoricalCashFlow();
			his.addAll(last_points);
			his.sort();
			stock.getFinancials().setCashFlow(his);
			if(saveCashFlow(stock))return true;
			
		}
		
		
		return false;
	}

	@Override
	public boolean updateCashFlow(Stock stock) {
		boolean isUpdated = false;
		
		LinkedList<CashFlowPoint> last_points=getLastCashFlowPoints(stock);
		for(CashFlowPoint point:last_points){
			//System.out.println("Compare To: "+stock.getHistoricalCashFlow().size());
			if(!stock.getFinancials().getCashFlow().contains(point)){
				stock.getFinancials().getCashFlow().add(point);
				stock.getFinancials().getCashFlow().sort();
				logger.info("Cash Flow Point added: "+point);
				isUpdated = true;
			}
		}
		
		if(isUpdated){
			logger.info("The Stock was updated: \""+stock.getFullName());
			if(this.saveCashFlow(stock)){
				logger.info("The new Data were automaticaly saved!");
			}
			else{
				logger.info("Error: cannot save the updated data!");
				return false;
			}
		}
		
		return false;
	}
	
	
	// ===================================
	// ==  REPORT READER CONFIGURATION  ==
	// ===================================
	
	
	public String getHtmlContent(String url){
		try {
			URLEncoder.encode(url, "UTF-8");
			return HtmlFunctions.getHtmlPage(url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Cannot encode the url: "+url;
		}
	}
	public LinkedList<String> findPDFDocument(String url){
		String content=getHtmlContent(url);
		return PdfFunctions.findPdfFiles(content);
	}
	
	public String loadReportDocument(Stock stock,String url){
		String targetPath=this.getSavePath(stock)+File.separator;
		File urlFile=new File(url);
		String targetFile=targetPath+urlFile.getName();
		
		if(new File(targetFile).exists())
			return PdfFunctions.getPDFContentFromFile(targetFile);
		
		//Save the url
		try {
			HtmlFunctions.saveUrl(targetFile, url.replace(" ", "%20"));
			return PdfFunctions.getPDFContentFromFile(targetFile);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		
		
	}
	
	
	

public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		//AAPL
		//R
		//O
		//String[] list={"AAPL","R","O","GOOG","L","P","A","H"};
		String[] list={"Q"};
		
		
		for(int i=0;i<list.length;i++){
		
		System.out.println("\nGetting Data: "+list[i]);
			
		ExchangeRate rate=provider.load(list[i]);
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

	}

}
