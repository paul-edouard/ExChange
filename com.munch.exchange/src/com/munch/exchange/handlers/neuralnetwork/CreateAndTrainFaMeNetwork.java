 
package com.munch.exchange.handlers.neuralnetwork;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.goataa.spec.IObjectiveFunction;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.utils.ProfitUtils;

public class CreateAndTrainFaMeNetwork implements LearningEventListener{
	
	private static Logger logger = Logger.getLogger(CreateAndTrainFaMeNetwork.class);
	
	private NetworkArchitecture archi=null;
	//private boolean canExec=false;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	INeuralNetworkProvider nn_provider;
	
	
	private LearningRule learningRule=null;
	
	private DataSet validateDataSet=null;
	
	
	@Execute
	public void execute() {
		
		logger.info("execute!");
		
		if(archi==null)return;
		if(archi.getParent()==null)return;
		
		sumProfit=0;
		count=0;
		//if(archi.isFaMeNetworkCreated()){
		//	archi.resetFaMeNetwork();return;
		//}
		
		nn_provider.createAllValuePoints(archi.getParent(),true);
		
		archi.getParent().resetTrainingData();
		DataSet trainingSet=archi.getParent().getTrainingDataSet();
		validateDataSet=archi.getParent().getValidateDataSet();
		
		
		learningRule=archi.getParent().getLearnParam().createLearningRule();
		learningRule.addListener(this);
		archi.getFaMeNetwork().setLearningRule(learningRule);
		
		/*
		double[][] outputs=archi.calculateFaMeNetworkOutputsAndProfit(validateDataSet, ProfitUtils.PENALTY);
		
		logger.info( "Validate Profit: "+outputs[5][outputs[5].length-1]);
		
		archi.getFaMeNetwork().setInput(archi.getParent().getLastInput());
		archi.getFaMeNetwork().calculate();
		//archi.checkFaMeNeuronsOutput();
		logger.info( "Output FaMe: "+archi.getFaMeNetwork().getOutput()[0]);
		
		
		archi.getNetwork().setInput(archi.getParent().getLastInput());
		archi.getNetwork().calculate();
		//archi.checkNeuronsOutput();
		logger.info( "Output Norm: "+archi.getNetwork().getOutput()[0]);
		
		//archi.getFaMeNetwork().set
		*/
		
		archi.getFaMeNetwork().learn(trainingSet);
		
		
		this.archi.setMeanValueOfFaMeNeurons();
		logger.info( "Mean Validate Profit: "+sumProfit/count);
		
		
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

	int count=0;
	double sumProfit=0;
	@Override
	public void handleLearningEvent(LearningEvent event) {
		if(event.getSource() instanceof BackPropagation){
			BackPropagation bp = (BackPropagation)event.getSource();
			double error=bp.getTotalNetworkError();
			count++;
			this.archi.setMeanValueOfFaMeNeurons();
			double[][] outputs=archi.calculateFaMeNetworkOutputsAndProfit(validateDataSet, ProfitUtils.PENALTY);
			sumProfit+=outputs[5][outputs[5].length-1];
			logger.info("Total Error: "+error+", count: "+count+ ", Validate: "+outputs[5][outputs[5].length-1]);
			this.archi.checkFaMeLayerWeigth();
			
			
			this.archi.setNewRandomValueOfFaMeNeurons();
			
			
		}
		
	}
	
}