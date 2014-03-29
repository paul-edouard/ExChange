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
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IHistoricalDataProvider;

public class HistoricalDataLoader extends Job {
	
	
	private static Logger logger = Logger.getLogger(HistoricalDataLoader.class);
	
	@Inject
	IHistoricalDataProvider historicalDataProvider;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	private ExchangeRate rate;
	
	@Inject
	public HistoricalDataLoader(ExchangeRate rate) {
		super("Historical Data Provider: "+rate.getFullName());
		this.rate=rate;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		logger.info("Starting loading historical data: "+rate.getFullName()+", time: "+Calendar.getInstance().getTime().toString());
		
		if(historicalDataProvider.hasLocalData(rate)){
			if (monitor.isCanceled()) return Status.CANCEL_STATUS;
			monitor.beginTask("Loading histrical data: "+rate.getFullName(), 1);
			historicalDataProvider.load(rate);
		}
		else{
			Calendar[] intervals=historicalDataProvider.getIntervals(rate);
			monitor.beginTask("Loading histrical data: "+intervals.length+1, 1);
			HistoricalData hisDatas = new HistoricalData();
			for(int i=0;i<intervals.length;i=i+2){
				if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				float per=((float)i*100)/((float)intervals.length);
				eventBroker.post(IEventConstant.HISTORICAL_DATA_LOADING,rate.getUUID()+";"+String.format("%.2f", per));
				historicalDataProvider.loadInterval(rate,hisDatas,intervals[i],intervals[i+1]);
				monitor.worked(i);
				
			}
			
			if (monitor.isCanceled()) return Status.CANCEL_STATUS;
			historicalDataProvider.save(rate, hisDatas);
		}
		
		if(!rate.getHistoricalData().isEmpty()){
			 HistoricalPoint point=rate.getRecordedQuote().createLastHistoricalPoint();
			 if(point!=null){
				 rate.getHistoricalData().setLastHisPointFromQuote(point);
			 }
			eventBroker.post(IEventConstant.HISTORICAL_DATA_LOADED,rate.getUUID());
		}
		
		
		return Status.OK_STATUS;
	}

}
