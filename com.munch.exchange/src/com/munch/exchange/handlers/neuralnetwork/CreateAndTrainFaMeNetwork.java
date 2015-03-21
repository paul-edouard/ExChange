 
package com.munch.exchange.handlers.neuralnetwork;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class CreateAndTrainFaMeNetwork {
	
	private static Logger logger = Logger.getLogger(CreateAndTrainFaMeNetwork.class);
	
	private NetworkArchitecture archi=null;
	//private boolean canExec=false;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	
	@Execute
	public void execute() {
		
		logger.info("execute!");
		
		if(archi==null)return;
		
		archi.createFactoredMeanNetwork();
		
		
	}
		
	
	@CanExecute
	public boolean canExecute() {
		if(this.archi !=null )
			return true;
		
		return false;
	}
	
	//################################
  	//##       Event Reaction       ##
  	//################################
	@Inject
	public void analyseSelection( @Optional  @Named(IServiceConstants.ACTIVE_SELECTION) 
	NetworkArchitecture selArchi){
    	this.archi=selArchi;
    	
	}
	
}