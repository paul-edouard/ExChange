package com.munch.exchange.job;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IHistoricalDataProvider;
import com.munch.exchange.services.IOptimizationResultsProvider;

public class HistoricalDataLoader extends Job {
	
	
	private static Logger logger = Logger.getLogger(HistoricalDataLoader.class);
	
	@Inject
	IHistoricalDataProvider historicalDataProvider;
	
	@Inject
	IOptimizationResultsProvider optimizationResultsProvider;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	private ExchangeRate rate;
	
	private Set<ExchangeRate> toLoad=new HashSet<ExchangeRate>();
	
	
	@Inject
	public HistoricalDataLoader(@Optional ExchangeRate rate) {
		super("Historical Data Provider "/*+rate.getFullName()*/);
		if(rate!=null)this.rate=rate;
		
		this.setPriority(LONG);
	}
	
	
	public void setRate(ExchangeRate rate) {
		this.rate = rate;
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
				eventBroker.send(IEventConstant.HISTORICAL_DATA_LOADING,rate.getUUID()+";"+String.format("%.2f", per));
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
			eventBroker.send(IEventConstant.HISTORICAL_DATA_LOADED,rate.getUUID());
		}
		
		//Laod the optimization result
		if(optimizationResultsProvider.load(rate)){
			eventBroker.send(IEventConstant.OPTIMIZATION_RESULTS_LOADED,rate.getUUID());
		}
		
		return Status.OK_STATUS;
	}

}
