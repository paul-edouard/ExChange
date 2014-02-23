package com.munch.exchange.job;

import java.util.Calendar;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IQuoteProvider;


public class QuoteLoader extends Job {
	
	private static Logger logger = Logger.getLogger(QuoteLoader.class);
	
	boolean stop=false;
	public static final int RESTART_TIMEOUT=20000;
	
	
	@Inject
	IQuoteProvider quoteProvider;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;

	@Inject
	public QuoteLoader() {
		super("Quote Loader");
		setSystem(true);
		setPriority(SHORT);
		schedule(RESTART_TIMEOUT);
	}
	
	public void Stop() {
		this.stop = true;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		LinkedList<ExchangeRate> list=exchangeRateProvider.getCachedRates();
		monitor.beginTask("Loading quotes", list.size());
		logger.info("\nStarting loading quotes: "+Calendar.getInstance().getTime().toString());
		
		
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		
		
		for(ExchangeRate rate:list){
			if(rate instanceof EconomicData)continue;
			
			monitor.subTask("Loading quote from "+rate.getFullName());
			if(rate.getRecordedQuote().isEmpty()){
				logger.info("Loading quote: "+rate.getFullName());
				if(quoteProvider.load(rate)){
					//logger.info("Message send: Quote loaded!");
					eventBroker.post(IEventConstant.QUOTE_LOADED,rate.getUUID());
				}
			}
			else{
				logger.info("Update quote: "+rate.getFullName());
				if(quoteProvider.update(rate)){
					//logger.info("Message send: Quote update!");
					eventBroker.post(IEventConstant.QUOTE_UPDATE,rate.getUUID());
				}
			}
			
			monitor.worked(1);
			if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		}
		
		
		
		schedule(RESTART_TIMEOUT);
		return Status.OK_STATUS;
		
	}

}
