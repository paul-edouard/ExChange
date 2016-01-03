 
package com.munch.exchange.handlers.neural;

import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.StringEditorDialog;
import com.munch.exchange.handlers.contract.EditContractCommissionHandler;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.parts.overview.RatesTreeContentProvider.ExContractContainer;
import com.munch.exchange.services.ejb.interfaces.IIBNeuralProvider;

public class AddNeuralConfiguration {
	
	private static Logger logger = Logger.getLogger(EditContractCommissionHandler.class);
	
	private boolean canExcecute=false;
	private IbContract selectedContract;
	
	@Inject
	private Shell shell;
	
	@Inject
	private IIBNeuralProvider neuralProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	@Execute
	public void execute() {
//		logger.info("Press on AddNeuralConfiguration!"+selectedContract.getLongName());
		
		StringEditorDialog dialog=new StringEditorDialog(shell, "New Configuration", "");
		if(dialog.open()==StringEditorDialog.OK && dialog.getNewString()!=null){
			logger.info("Config Name: "+dialog.getNewString());
			HashSet<String> configNames=new HashSet<>();
			for(NeuralConfiguration config:selectedContract.getNeuralConfigurations()){
				configNames.add(config.getName());
			}
			String newConfigName=dialog.getNewString();
			String newConfigNameCopy=newConfigName;
			int i=0;
			while(configNames.contains(newConfigNameCopy)){
				newConfigNameCopy=newConfigName+" "+i;i++;
			}
			
			neuralProvider.addNeuralConfiguration(selectedContract, newConfigNameCopy);
			
			
			logger.info(selectedContract.getLongName()+", nb of configs: "+selectedContract.getNeuralConfigurations().size());
			
			
			eventBroker.post(IEventConstant.CONTRACT_NEURAL_CONFIGURATION_CHANGED, selectedContract);
			
		}
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return canExcecute;
	}
	
	@Inject
	public void analyseSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) 
	ISelection selection){
		if(selection instanceof IStructuredSelection){
			
			selectedContract=null;
			canExcecute=false;
			
			
			IStructuredSelection sel=(IStructuredSelection) selection;
			if(sel.size()==1 && sel.getFirstElement() instanceof IbContract && !(sel.getFirstElement() instanceof ExContractContainer)){
				selectedContract=(IbContract) sel.getFirstElement();
				canExcecute=true;
				return;
			}
		}
		
	}
		
}