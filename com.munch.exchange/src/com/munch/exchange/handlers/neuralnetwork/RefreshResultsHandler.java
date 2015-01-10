 
package com.munch.exchange.handlers.neuralnetwork;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.neuralnetwork.Configuration;

public class RefreshResultsHandler {
	
	private static Logger logger = Logger.getLogger(RefreshResultsHandler.class);
	
	private Configuration config=null;
	private boolean isExecutable=false;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_REFRESH_CALLED,config);
		isExecutable=false;
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return true;
	}
	
	
	@Inject
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Configuration config) {
		this.config=config;
		isExecutable=true;
	}
	
	
	@Inject
	private void neuralNetworkResutsCalculated(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_CALCULATED) Configuration config) {
		
		if(config==null)return;
		if(this.config!=config)return;
		
		isExecutable=true;
	}
	
	
	
		
}