package com.munch.exchange.job.neuralnetwork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.goataa.spec.ISOOptimizationAlgorithm;
import org.neuroph.core.data.DataSet;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.objectivefunc.NetworkArchitectureObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.parts.InfoPart;

public class NeuralNetworkOptimizer extends Job {
	
	
	private static Logger logger = Logger.getLogger(NeuralNetworkOptimizer.class);

	private IEventBroker eventBroker;
	
	private ExchangeRate rate;
	private Configuration configuration;
	private DataSet trainingSet;
	private int dimension;
	
	private OptInfo info;
	private List<Individual<boolean[], boolean[]>>  solutions;
	private NetworkArchitectureObjFunc func;
	
	private boolean isCancel=false;
	
	ISOOptimizationAlgorithm<boolean[], boolean[], Individual<boolean[], boolean[]>> algorithm;
	
	StepLimitPropChange<boolean[],boolean[]> term;
	
	public NeuralNetworkOptimizer(ExchangeRate rate, Configuration configuration,DataSet testSet,
			IEventBroker eventBroker, int dimension) {
		super("Neural Network Optimizer");
		setSystem(true);
		setPriority(SHORT);
		
		this.rate=rate;
		this.configuration=configuration;
		this.trainingSet=testSet;
		this.eventBroker=eventBroker;
		this.dimension=dimension;
		
		this.info=new OptInfo(rate, configuration);
		
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setTrainingSet(DataSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public List<Individual<boolean[], boolean[]>> getSolutions() {
		return solutions;
	}
	
	//################################
	//##            RUN             ##
	//################################
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		isCancel=false;
		
		//InfoPart.postInfoText(eventBroker, "Network Optimization Started");
		
		prepareNetworkArchitectureOptimization(monitor);
		eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_STARTED,info);
		
		
		if(term==null){
			logger.error("Termination Criterion is not initialized!");
			eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED,info);
			return Status.CANCEL_STATUS;
		}
			
		if(algorithm==null){
			logger.error("Algothm is not initialized!");
			eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED,info);
			return Status.CANCEL_STATUS;
		}
		
		if(info==null){
			logger.error("Optimization Info is not initialized!");
			eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED,info);
			return Status.CANCEL_STATUS;
		}
		
		logger.info("Starting!!");
		
		//Create and add the listener
		TerminationPropertyChangeListener listener=new TerminationPropertyChangeListener(monitor);
		term.addPropertyChangeListener(listener);
		
		//Start the Optimization
		solutions=algorithm.call();
		
		//Remove the listener
		term.removePropertyChangeListener(listener);
		info.resetLastReaction();
		eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED,info);
		
		
		//Best Result:
		//logger.info("Optimization finished!!");
		//InfoPart.postInfoText(eventBroker, "Network Optimization finished!!!");
		if(configuration.getOptResults(dimension).getResults().size()>0){
			logger.info("Best Result so far x100: "+configuration.getOptResults(dimension).getBestResult().getValue()*100);
			InfoPart.postInfoText(eventBroker, "Best Result so far x100: "+configuration.getOptResults(dimension).getBestResult().getValue()*100);
		}
		
		return Status.OK_STATUS;
	}
	
	@Override
	protected void canceling() {
		isCancel=true;
		super.canceling();
	}
	

	@SuppressWarnings("unchecked")
	private void prepareNetworkArchitectureOptimization(IProgressMonitor monitor){
		
		//Set the dimension
		configuration.getOptArchitectureParam().setParam(AlgorithmParameters.ES_Dimension, dimension);
		configuration.getOptArchitectureParam().setParam(AlgorithmParameters.EA_Dimension, dimension);
				
		// Create the algorithm
		int numberOfInputNeurons=configuration.getNumberOfInputNeurons();
		algorithm = configuration.getOptArchitectureParam().createBooleanAlgorithm(numberOfInputNeurons);
		
		//set the gpm
		final IGPM<boolean[], boolean[]> gpm = ((IGPM<boolean[], boolean[]>) (IdentityMapping.IDENTITY_MAPPING));
		algorithm.setGPM(gpm);
				
		//Set the objective function
		func=new NetworkArchitectureObjFunc(rate, configuration,trainingSet,
				 eventBroker, monitor);
		algorithm.setObjectiveFunction(func);
		
		//Reload last results
		configuration.getOptArchitectureParam().addBooleanLastBestResults(algorithm, configuration.getOptResults(dimension));
		
		if(configuration.getOptResults(dimension).getResults().size()>0){
			logger.info("Old result found! "+configuration.getOptResults(dimension).getResults().size());
			logger.info("Old Best x100 "+(100*configuration.getOptResults(dimension).getBestResult().getValue()));
		}
		
		// Get the termination Criterion
		if (algorithm.getTerminationCriterion() instanceof StepLimitPropChange) {
			term = ((StepLimitPropChange<boolean[],boolean[]>) algorithm
							.getTerminationCriterion());
		}

		if (term == null)
			return;

		// Create and add the listener
		//TerminationPropertyChangeListener listener = new TerminationPropertyChangeListener(
		//				monitor);
		//term.addPropertyChangeListener(listener);
		
		
		info.setMaximum(term.getMaxSteps());
		info.setDimension(dimension);
		
	}
	
	private class TerminationPropertyChangeListener implements PropertyChangeListener{
		IProgressMonitor monitor;
		
		public TerminationPropertyChangeListener(IProgressMonitor monitor){
			this.monitor=monitor;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_BEST)){
				Individual<boolean[], boolean[]> ind=(Individual<boolean[], boolean[]>) evt.getNewValue();
				//logger.info("New Best Results: "+ind.v);
				if(info.getResults().addResult(new ResultEntity(ind.x,ind.v))){
					info.resetLastReaction();
					eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
				}
			}
			else if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_STEP)){
				//int val=(int)evt.getNewValue();
				//if(val%10==0){
					info.setStep((int)evt.getNewValue());
					info.resetLastReaction();
					eventBroker.post(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_STEP,info);
				//}
			}
			// Cancel called
			if (monitor.isCanceled() || isCancel){
				func.setCancel(true);
				term.cancel();
				info.resetLastReaction();
				eventBroker.send(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED,info);
			}
			
		}
		
		
	}
	
	
	
	//################################
	//##        INFOCLASS           ##
	//################################	
	
	public OptInfo getInfo() {
		return info;
	}

	public class OptInfo{
		
		private ExchangeRate rate;
		private Configuration configuration;
		private int step;
		private int maximum;
		private OptimizationResults results=new OptimizationResults();
		private int dimension;
		private int nbOfInnerNeurons;
		private int workerPos;
		private Calendar lastReaction=Calendar.getInstance();
		
		/*
 		public OptInfo(ExchangeRate rate,
				Configuration configuration ,int maximum,int dimension) {
			super();
			this.rate = rate;
			this.configuration = configuration;
			this.step=maximum;
			this.maximum=maximum;
			this.dimension=dimension;
			nbOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dimension, configuration.getNumberOfInputNeurons());
		}*/
 		
 		public OptInfo(ExchangeRate rate,
				Configuration configuration ) {
			super();
			this.rate = rate;
			this.configuration = configuration;
			this.step=maximum;
			//this.maximum=maximum;
			//this.dimension=dimension;
			//nbOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dimension, configuration.getNumberOfInputNeurons());
		}
		
		
		public int getNumberOfInnerNeurons(){
			return nbOfInnerNeurons;
		}
		
		
		public synchronized void resetLastReaction(){
			lastReaction=Calendar.getInstance();
		}
		
		public synchronized int getElapsedSecondSinceLastReaction(){
			Calendar diff=Calendar.getInstance();
			long elapseTime=diff.getTimeInMillis()-lastReaction.getTimeInMillis();
			return (int) elapseTime/1000;
		}
		
		//################################
		//##   GETTER AND SETTER        ##
		//################################
		
		public int getStep() {
			return step;
		}
		public void setStep(int step) {
			this.step = step;
		}
		
		public int getMaximum() {
			return maximum;
		}
		public void setMaximum(int maximum) {
			this.maximum = maximum;
		}
	
		
		public void setDimension(int dimension) {
			this.dimension=dimension;
			nbOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dimension, configuration.getNumberOfInputNeurons());
		
		}

		public int getWorkerPos() {
			return workerPos;
		}
		public void setWorkerPos(int workerPos) {
			this.workerPos = workerPos;
		}
		
		
		public ExchangeRate getRate() {
			return rate;
		}
		
		public Configuration getConfiguration() {
			return configuration;
		}

		public int getDimension() {
			return dimension;
		}

		public OptimizationResults getResults() {
			return results;
		}
		


		
		
		
		
		
	}
	
	

}
