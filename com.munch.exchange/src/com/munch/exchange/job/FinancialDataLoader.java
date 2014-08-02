package com.munch.exchange.job;

import java.util.Calendar;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IFinancialsProvider;
import com.munch.exchange.services.IOptimizationResultsProvider;

public class FinancialDataLoader extends Job {

	private static Logger logger = Logger.getLogger(FinancialDataLoader.class);
	
	
	@Inject
	IFinancialsProvider financialsProvider;
	
	@Inject
	IOptimizationResultsProvider optimizationResultsProvider;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	private Stock stock;
		
	
	@Inject
	public FinancialDataLoader( ExchangeRate rate) {
		super("Financials Provider");
		if(rate instanceof Stock)
			this.stock=(Stock)rate;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if(stock!=null)
			logger.info("Starting loading financial data: "+stock.getFullName()+", time: "+Calendar.getInstance().getTime().toString());
		
		if(stock==null)return Status.CANCEL_STATUS;
		
		boolean r_bs=financialsProvider.loadBalanceSheet(stock);
		boolean r_cf=financialsProvider.loadCashFlow(stock);
		boolean r_is=financialsProvider.loadIncomeStatement(stock);
		
		financialsProvider.loadReportReaderConfiguration(stock);
		
		if(r_bs && r_cf && r_is){
			eventBroker.send(IEventConstant.FINANCIAL_DATA_LOADED,stock.getUUID());
		}
		
		return Status.OK_STATUS;
	}

}
