package com.munch.exchange.job.neuralnetwork;

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
import com.munch.exchange.services.INeuralNetworkProvider;

public class NeuralNetworkDataLoader extends Job {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkDataLoader.class);
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private INeuralNetworkProvider neuralNetworkProvider;
	
	private Stock stock;
	
	@Inject
	public NeuralNetworkDataLoader( ExchangeRate rate) {
		super("Financials Provider");
		if(rate instanceof Stock)
			this.stock=(Stock)rate;
		
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if(stock!=null)
			logger.info("Starting loading neural network data: "+stock.getFullName()+", time: "+Calendar.getInstance().getTime().toString());
		
		if(stock==null)return Status.CANCEL_STATUS;
		
		
		/*boolean r_loading=*/neuralNetworkProvider.load(stock);
		
		//if(r_loading){
			//Create all input points
			//neuralNetworkProvider.createAllInputPoints(stock);
			
			eventBroker.post(IEventConstant.NEURAL_NETWORK_DATA_LOADED,stock.getUUID());
		//}
		
		return Status.OK_STATUS;
	}

}
