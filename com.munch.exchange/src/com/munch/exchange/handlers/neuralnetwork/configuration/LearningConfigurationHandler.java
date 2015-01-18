 
package com.munch.exchange.handlers.neuralnetwork.configuration;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.neuralnetwork.Configuration;

public class LearningConfigurationHandler {
	
	
	@Inject
	Shell shell;
	
	private Configuration config=null;
	
	@Execute
	public void execute() {
		MessageDialog.openInformation(shell, "LearningConfigurationHandler", "LearningConfigurationHandler");
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