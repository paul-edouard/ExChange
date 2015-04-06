 
package com.munch.exchange.handlers.neuralnetwork.configuration;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.wizard.parameter.optimization.OptimizationDoubleParamWizard;

public class RegularizationOptimizationParameterHandler {
	
	
	private static Logger logger = Logger.getLogger(RegularizationOptimizationParameterHandler.class);
	
	private Configuration config=null;
	
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute(Shell shell) {
		logger.info("Hallo: RegularizationOptimizationParameterHandler");
		
		if(shell==null)return;
		//MessageDialog.openInformation(shell, "StartNeuralNetworkOptimizationHandler", "StartNeuralNetworkOptimizationHandler"+config.getName());
		
		OptimizationDoubleParamWizard wizard=new OptimizationDoubleParamWizard(
				config.getRegOptParam().createCopy());
		WizardDialog dialog = new WizardDialog(shell, wizard);
		if (dialog.open() == Window.OK){
			logger.info(wizard.getOptLearnParam().getIntegerParam(AlgorithmParameters.OPTIMIZATION_Loops));
			
			config.setRegOptParam(wizard.getOptLearnParam());
			config.setDirty(true);
			
			logger.info(config.getRegOptParam().getIntegerParam(AlgorithmParameters.OPTIMIZATION_Loops));
			
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