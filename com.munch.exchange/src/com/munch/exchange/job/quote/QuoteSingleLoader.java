package com.munch.exchange.job.quote;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.services.IQuoteProvider;

public class QuoteSingleLoader extends Job {
	
	private static Logger logger = Logger.getLogger(QuoteLoader.class);
	
	@Inject
	private ExchangeRate rate;
	
	@Inject
	IQuoteProvider quoteProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	public QuoteSingleLoader() {
		super("Quote singel loader");
		setSystem(true);
		setPriority(SHORT);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		
		quoteProvider.load(rate);
		//rate.getRecordedQuote().getLast().
		
		//rate.getHistoricalData().
		
		//rate.getHistoricalData().setLastHisPointFromQuote(lastHisPointFromQuote);
		
		eventBroker.post(IEventConstant.QUOTE_LOADED,rate.getUUID());
		
		logger.info("Loaded quotes from "+rate.getFullName());
		
		return Status.OK_STATUS;
		
	}

}
