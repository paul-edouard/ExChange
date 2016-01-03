 
package com.munch.exchange.handlers.neural;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.services.ejb.interfaces.IIBNeuralProvider;

public class RemoveNeuralConfiguration {
	
	private static Logger logger = Logger.getLogger(RemoveNeuralConfiguration.class);
	
	private boolean canExcecute=false;
	
	private NeuralConfiguration configuration;
	
	
	@Inject
	private IIBNeuralProvider neuralProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	@Execute
	public void execute(Shell shell) {
		
		boolean res = MessageDialog.openConfirm(shell, "Remove Neural Configuration?",
				"Do you really want to remove the neural configuration: \""+configuration.getName()+"\" ?");
		
		if(!res)return;
		
		
		IbContract contract=configuration.getContract();
		neuralProvider.removeNeuralConfiguration(contract, configuration);
		eventBroker.post(IEventConstant.CONTRACT_NEURAL_CONFIGURATION_CHANGED, contract);
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return canExcecute;
	}
	
	@Inject
	public void analyseSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) 
	ISelection selection){
		if(selection instanceof IStructuredSelection){
			
			configuration=null;
			canExcecute=false;
			
			
			IStructuredSelection sel=(IStructuredSelection) selection;
			if(sel.size()==1 && sel.getFirstElement() instanceof NeuralConfiguration ){
				configuration=(NeuralConfiguration) sel.getFirstElement();
				canExcecute=true;
				return;
			}
		}
		
	}
		
}