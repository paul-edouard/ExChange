 
package com.munch.exchange.handlers.neuralnetwork;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;




import com.munch.exchange.job.neuralnetwork.NeuralNetworkRegulizer;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.parts.neuralnetwork.error.NeuralNetworkRegularizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.INeuralNetworkProvider;

public class StartNeuralNetworkRegularizationHandler {
	
	private static Logger logger = Logger.getLogger(StartNeuralNetworkRegularizationHandler.class);
	
	private NetworkArchitecture archi=null;
	//private boolean canExec=false;
	
	@Inject
	IEclipseContext context;
	
	@Inject
	EModelService modelService;
	
	@Inject
	EPartService partService;
	
	@Inject
	MApplication application;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	IEventBroker eventBroker;
	
	@Inject
	INeuralNetworkProvider nnprovider;
	
	@Inject
	ESelectionService selectionService;
	
	private NeuralNetworkRegulizer regulizer;
	
	
	@Execute
	public void execute() {
		
		logger.info("execute!");
		
		if(archi==null)return;
		if(archi.getParent()==null)return;
		
		Stock stock=archi.getParent().getParent();

		if(regulizer==null){
			regulizer=new NeuralNetworkRegulizer(eventBroker,nnprovider,archi);
		}
		else{
			regulizer.setArchi(archi);
		}
		
		
		NeuralNetworkRegularizationErrorPart.openPart(
				archi,
				partService,
				modelService,
				application,
				regulizer,
				context);
		
		
		
		regulizer.schedule();
		
	}
		
	
	@CanExecute
	public boolean canExecute() {
		if(this.archi ==null )
			return false;
		
		if(this.regulizer!=null && this.regulizer.getState()==Job.RUNNING)
			return false;
		
		return true;
		
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