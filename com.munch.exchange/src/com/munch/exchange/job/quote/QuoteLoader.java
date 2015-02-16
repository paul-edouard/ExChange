package com.munch.exchange.job.quote;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

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
	
	private int currentRestartTimeout=RESTART_TIMEOUT;
	
	
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
		
		List<ExchangeRate> rateToLoad=new LinkedList<ExchangeRate>();
		List<ExchangeRate> rateToUpdate=new LinkedList<ExchangeRate>();
		
		for(ExchangeRate rate:list){
			if(rate instanceof EconomicData)continue;
			
			//monitor.subTask("Loading quote from "+rate.getFullName());
			if(rate.getRecordedQuote().isEmpty()){
				rateToLoad.add(rate);
			}
			else{
				rateToUpdate.add(rate);
			}
		}
		
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		
		
		int totalUpdatedRates=0;
		
		if(rateToLoad.size()>0){
			//logger.info("Loading quotes: "+rateToLoad.size());
			quoteProvider.load(rateToLoad);
			int numberOfUpdated=0;
			for(ExchangeRate rate:rateToLoad){
				if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				eventBroker.post(IEventConstant.QUOTE_LOADED,rate.getUUID());
				if(rate.getRecordedQuote().isUpdated()){
					numberOfUpdated++;
				}
			}
			totalUpdatedRates+=numberOfUpdated;
			logger.info("Loaded & updated quotes: "+numberOfUpdated+"/"+rateToLoad.size());
		}
		
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		
		if(rateToUpdate.size()>0){
			//logger.info("Update quotes: "+rateToUpdate.size());
			quoteProvider.update(rateToUpdate);
			int numberOfUpdated=0;
			for(ExchangeRate rate:rateToUpdate){
				if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				if(rate.getRecordedQuote().isUpdated()){
				eventBroker.post(IEventConstant.QUOTE_UPDATE,rate.getUUID());numberOfUpdated++;
				}
			}
			totalUpdatedRates+=numberOfUpdated;
			logger.info("Updated quotes: "+numberOfUpdated+"/"+rateToUpdate.size());
		}
		
		if(totalUpdatedRates>0)
			currentRestartTimeout=RESTART_TIMEOUT;
		else
			currentRestartTimeout+=RESTART_TIMEOUT;
		
		if(currentRestartTimeout>10*RESTART_TIMEOUT)
			currentRestartTimeout=10*RESTART_TIMEOUT;
		
		schedule(currentRestartTimeout);
		
		return Status.OK_STATUS;
		
	}

}
