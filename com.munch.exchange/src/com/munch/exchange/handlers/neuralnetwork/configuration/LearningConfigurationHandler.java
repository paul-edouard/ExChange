 
package com.munch.exchange.handlers.neuralnetwork.configuration;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.wizard.parameter.learning.LearnParameterWizard;

public class LearningConfigurationHandler {
	
	
	
	
	private Configuration config=null;
	
	
	@Inject
	IEventBroker eventBroker;
	
	
	@Execute
	public void execute(Shell shell) {
		if(shell==null)return;
		
		LearnParameterWizard wizard=new LearnParameterWizard(
				config.getLearnParam().createCopy());
		WizardDialog dialog = new WizardDialog(shell, wizard);
		if (dialog.open() == Window.OK){
			config.setLearnParam(wizard.getParam());
			config.setDirty(true);
			eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_DIRTY,config);
		}
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return this.config!=null;
		
	}
	
	
	
	@Inject
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Configuration config) {
		this.config=config;
	}
		
}