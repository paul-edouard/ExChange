package com.munch.exchange.job.neuralnetwork;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.RegularizationParameters;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.utils.ProfitUtils;

public class NeuralNetworkRegulizer extends Job implements LearningEventListener {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkRegulizer.class);
	
	
	private IEventBroker eventBroker;
	private INeuralNetworkProvider nnprovider;
	
	private NetworkArchitecture archi;
	private DataSet trainingSet;
	private DataSet testSet;

	public NeuralNetworkRegulizer( IEventBroker eventBroker, INeuralNetworkProvider nnprovider,
			NetworkArchitecture archi) {
		super("Neural Network Regulizer");
		
		this.eventBroker = eventBroker;
		this.archi = archi;
		this.nnprovider = nnprovider;
		
	}
	
	//################################
	//##          SETTER            ##
	//################################

	
	public void setArchi(NetworkArchitecture archi) {
		this.archi = archi;
	}
	
	//################################
	//##            RUN             ##
	//################################
	
	
	

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		logger.info("Regulizer!");
		
		if(archi==null)return Status.CANCEL_STATUS;
		if(archi.getParent()==null)return Status.CANCEL_STATUS;
		
		double sumProfit=0;
		int count=0;
		//if(archi.isFaMeNetworkCreated()){
		//	archi.resetFaMeNetwork();return;
		//}
		
		nnprovider.createAllValuePoints(archi.getParent(),true);
		
		archi.getParent().resetTrainingData();
		trainingSet=archi.getParent().getTrainingDataSet();
		testSet=archi.getParent().getValidateDataSet();
		
		//Set the learning rule
		LearningRule learningRule=archi.getParent().getRegTrainParam().createLearningRule();
		learningRule.addListener(this);
		archi.getFaMeNetwork().setLearningRule(learningRule);
		
		//Set the Varianz
		double varianz=archi.getParent().getRegBasicParam().getDoubleParam(RegularizationParameters.VARIANZ);
		archi.setVarianzOfFaMeNeurons(varianz);
		logger.info( "Varianz: "+varianz);
		
		archi.getFaMeNetwork().learn(trainingSet);
		
		
		this.archi.setMeanValueOfFaMeNeurons();
		logger.info( "Mean Validate Profit: "+sumProfit/count);
		
		return Status.OK_STATUS;
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
			double[][] outputs=archi.calculateFaMeNetworkOutputsAndProfit(testSet, ProfitUtils.PENALTY);
			sumProfit+=outputs[5][outputs[5].length-1];
			logger.info("Total Error: "+error+", count: "+count+ ", Validate: "+outputs[5][outputs[5].length-1]);
			this.archi.checkFaMeLayerWeigth();
			
			
			this.archi.setNewRandomValueOfFaMeNeurons();
			
			
		}
		
	}

}
