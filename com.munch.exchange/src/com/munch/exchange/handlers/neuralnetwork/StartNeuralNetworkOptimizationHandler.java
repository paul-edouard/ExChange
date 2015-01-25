 
package com.munch.exchange.handlers.neuralnetwork;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.neuroph.core.data.DataSet;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.parts.neuralnetwork.error.NeuralNetworkErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.INeuralNetworkProvider;

public class StartNeuralNetworkOptimizationHandler {
	
	
	private static Logger logger = Logger.getLogger(StartNeuralNetworkOptimizationHandler.class);
	
	
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
	
	
	private NeuralNetworkOptimizerManager optimizerManager;
	
	private Configuration config=null;
	
	
	@Execute
	public void execute(Shell shell) {
		
		if(shell==null)return;
		
		if(this.config.isDirty()){
			boolean res=MessageDialog.openQuestion(shell, "Configuration is dirty", "Do you want to save the configuration and start the optimization");
			if(res){
				
				if(nnprovider.saveConfiguration(config.getParent())){
					config.setDirty(false);
					eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_DIRTY,config);
				}
				else{
					MessageDialog.openInformation(shell, "Configuration save Error", "Couldn't save the configuration: "+config.getName());
					return;
				}
				
			}
			else{
				return;
			}
			
		}
		
		//MessageDialog.openInformation(shell, "StartNeuralNetworkOptimizationHandler", "StartNeuralNetworkOptimizationHandler"+config.getName());
		//logger.info("Start Train click!");
		Stock stock=config.getParent();
		
		if(!stock.getNeuralNetwork().getConfiguration().areAllTimeSeriesAvailable()){
			nnprovider.createAllValuePoints(stock.getNeuralNetwork().getConfiguration(),true);
		}
		
		Configuration config=stock.getNeuralNetwork().getConfiguration();
		config.resetTrainingData();
		DataSet trainingSet=config.getTrainingDataSet();
		
		
		int minDim=stock.getNeuralNetwork().getConfiguration().getNumberOfInputNeurons();
		int maxDim=minDim;
		
		logger.info("Dataset row size: "+trainingSet.getRowAt(0).getInput().length);
		logger.info("Number of input neurons: "+minDim);
		
		if(minDim!=trainingSet.getRowAt(0).getInput().length){
			logger.info("Input neuron size error: "+minDim);
			return;
		}
		
		//trainingSet.ge
		
		
		AlgorithmParameters<boolean[]> optArchitectureParam=stock.getNeuralNetwork().getConfiguration().getOptArchitectureParam();
		
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MinDimension)){
			minDim=optArchitectureParam.getIntegerParam(AlgorithmParameters.MinDimension);
		}
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MaxDimension)){
			maxDim=optArchitectureParam.getIntegerParam(AlgorithmParameters.MaxDimension);
		}
		
		
		
		if(optimizerManager==null){
			optimizerManager=new NeuralNetworkOptimizerManager(
					eventBroker,nnprovider,
					stock,
					stock.getNeuralNetwork().getConfiguration(),
					trainingSet,
					minDim,
					maxDim);
		}
		else{
			optimizerManager.setConfiguration(stock.getNeuralNetwork().getConfiguration());
			optimizerManager.setTrainingSet(trainingSet);
			optimizerManager.setMinMax(minDim, maxDim);
		}
		
		//setTrainingStatus(true);
		
		
		//Open the Neural network error part
		NeuralNetworkErrorPart.openNeuralNetworkErrorPart(
				stock,
				partService,
				modelService,
				application,
				optimizerManager,
				context);
		
		
		
		optimizerManager.schedule();
		
		
		//stock.getNeuralNetwork().getConfiguration().setDirty(true);
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		if(this.config==null)return false;
		
		if(this.optimizerManager!=null && this.optimizerManager.getState()==Job.RUNNING)return false;
		
		return true;
		//return true;
	}
	
	
	@Inject
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Configuration config) {
		
		logger.info("Config selected!");
		
		this.config=config;
	}
		
}