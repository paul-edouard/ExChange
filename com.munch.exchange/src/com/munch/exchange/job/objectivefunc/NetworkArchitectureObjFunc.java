package com.munch.exchange.job.objectivefunc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.goataa.impl.OptimizationModule;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Constants;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.NnObjFunc;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.ResultEntity;

public class NetworkArchitectureObjFunc extends OptimizationModule implements
		IObjectiveFunction<boolean[]> , LearningEventListener{
			
					
	/**
	 * 
	 */
	private static final long serialVersionUID = 8630727938986919169L;
	
	
	private static Logger logger = Logger.getLogger(NetworkArchitectureObjFunc.class);
	
	//Config
	private ExchangeRate rate;
	private Configuration configuration;
	private DataSet trainingSet;
	
	
	//Event variables
	private IEventBroker eventBroker;
	private IProgressMonitor monitor;
	
	private NetworkArchitectureOptInfo info;
	
	//Local Optimization algorithm
	//private List<Individual<double[], double[]>>  solutions;
	private ISOOptimizationAlgorithm<double[], double[], Individual<double[], double[]>> algorithm=null;
	private StepLimitPropChange<Double> term=null;
	private int optLoops=1;
	
	private double minWeigth=Double.MAX_VALUE;
	private double maxWeigth=Double.MIN_VALUE;
	
	
	//Local learning Strategy
	private LearningRule learningRule=null;
	
	public NetworkArchitectureObjFunc(ExchangeRate rate, Configuration configuration,DataSet testSet,
			IEventBroker eventBroker,IProgressMonitor monitor){
		
		this.rate=rate;
		this.configuration=configuration;
		this.trainingSet=testSet;
		this.eventBroker=eventBroker;
		this.monitor=monitor;
		
	}
	
	
	@Override
	public double compute(boolean[] x, Random r) {
		
		NetworkArchitecture architecture=configuration.searchArchitecture(x);
		if(architecture==null)return Constants.WORST_FITNESS;
		
		
		//Prepare the optimization of the network weights
		prepareNetworkWeightsOptimization(architecture);
		if(algorithm==null || term == null)return Constants.WORST_FITNESS;
		
		
		//Prepare the learning strategy
		prepareLearningStrategy(architecture);
		if(learningRule==null)return Constants.WORST_FITNESS;
		
		//Prepare the Optimization info
		iniOptInfo(x);
		
		eventBroker.post(IEventConstant.NETWORK_OPTIMIZATION_STARTED,info);
		
		//Start the Optimization
		for(int i=0;i<optLoops;i++){
			//Start the optimization algorithm
			List<Individual<double[], double[]>> individuals=algorithm.call();
			
			//Loop on all the individuals to try increase the result quality
			//TODO set the max number of results
			OptimizationResults results=new OptimizationResults();
			//results.setMaxResult(maxResult);
			for(Individual<double[], double[]> individual:individuals){
				
				//Start learning for each individuals
				architecture.getNetwork().setWeights(individual.x);
				architecture.getNetwork().learn(trainingSet);
				
				//Get the network error
				if(learningRule instanceof BackPropagation){
					BackPropagation bp = (BackPropagation)learningRule;
					double error=bp.getTotalNetworkError();
					
					//Reset the individual values if a better error is found
					if(error<individual.v){
						Double[] weigths=architecture.getNetwork().getWeights();
						for(int j=0;j<weigths.length;j++){
							individual.x[j]=weigths[j];
							individual.g[j]=weigths[j];
							
							if(weigths[j]>maxWeigth)
								maxWeigth=weigths[j]+Math.abs(weigths[j])*0.1;
							if(weigths[j]<minWeigth)
								minWeigth=weigths[j]-Math.abs(weigths[j])*0.1;
							
						}
						individual.v=error;
					}
				}
				
				//Add the individual to the results list
				ResultEntity result=new ResultEntity(individual.g,individual.v);
				results.addResult(result);
				architecture.getOptResults().addResult(result);
			}
			
			
			resetMinMaxValuesOfAlgorithm();
			
			//Set the best results as starting point for the next loop
			configuration.getOptLearnParam().addLastBestResults(algorithm,results);
		}
		
		eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_FINISHED,info);
		
		
		ResultEntity bestResult=architecture.getOptResults().getBestResult();
		if(bestResult==null)return  Constants.WORST_FITNESS;
		
		logger.info("Archi: "+x);
		logger.info("Best Results: "+bestResult.getValue());
		
		return bestResult.getValue();
	}
	
	private void resetMinMaxValuesOfAlgorithm(){
		configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Minimum, minWeigth);
		configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Maximum, maxWeigth);
		
		if(algorithm instanceof EvolutionStrategy){
			EvolutionStrategy<double[]> ES=(EvolutionStrategy<double[]>) algorithm;
			ES.setMinimum(minWeigth);
			ES.setMaximum(maxWeigth);
		}
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void prepareLearningStrategy(NetworkArchitecture architecture){
		learningRule=configuration.getLearnParam().createLearningRule();
		learningRule.addListener(NetworkArchitectureObjFunc.this);
		
		architecture.getNetwork().setLearningRule(learningRule);
	}
	
	
	@SuppressWarnings("unchecked")
	private void prepareNetworkWeightsOptimization(NetworkArchitecture architecture) {
		//Set the dimension
		configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Dimension, architecture.getNetwork().getWeights().length);
		configuration.getOptLearnParam().setParam(AlgorithmParameters.EA_Dimension, architecture.getNetwork().getWeights().length);
		
		// Create the algorithm
		algorithm = configuration.getOptLearnParam().createDoubleAlgorithm();
		
		//Set the number of loops
		if(configuration.getOptLearnParam().hasParamKey(AlgorithmParameters.OPTIMIZATION_Loops)){
			optLoops=configuration.getOptLearnParam().getIntegerParam(AlgorithmParameters.OPTIMIZATION_Loops);
		}
		
		//set the gpm
		final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));
		algorithm.setGPM(gpm);
		
		//Set the objective function
		algorithm.setObjectiveFunction(new NnObjFunc(architecture.getNetwork(), trainingSet) );
		
		// Add the last best results
		configuration.getOptLearnParam().addLastBestResults(algorithm,
				architecture.getOptResults());

		// Get the termination Criterion
		if (algorithm.getTerminationCriterion() instanceof StepLimitPropChange) {
			term = ((StepLimitPropChange<Double>) algorithm
					.getTerminationCriterion());
		}

		if (term == null)
			return;

		// Create and add the listener
		TerminationPropertyChangeListener listener = new TerminationPropertyChangeListener(
				monitor);
		term.addPropertyChangeListener(listener);
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
				Individual<double[], double[]> ind=(Individual<double[], double[]>) evt.getNewValue();
				if(info.getResults().addResult(new ResultEntity(ind.x,ind.v))){
					eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
				}
			}
			else if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_STEP)){
				int val=(int)evt.getNewValue();
				if(val%10==0){
					info.setStep((int)evt.getNewValue());
					eventBroker.post(IEventConstant.NETWORK_OPTIMIZATION_NEW_STEP,info);
				}
			}
			// Cancel called
			if (monitor.isCanceled()){
				term.cancel();
				eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_FINISHED,info);
			}
			
		}
		
		
	}
	
	
	@Override
    public void handleLearningEvent(LearningEvent event) {
		
		if(event.getSource() instanceof BackPropagation){
			
			BackPropagation bp = (BackPropagation)event.getSource();
			ResultEntity ent=new ResultEntity(bp.getNeuralNetwork().getWeights(), bp.getTotalNetworkError());
			if(info.getResults().addResult(ent)){
				eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
			}
			//System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
			//eventBroker.send(IEventConstant.NEURAL_NETWORK_NEW_CURRENT,rate.getUUID());
			//TODO test if a new best was found!!!
		}
        
        
    }
	
	
	private void iniOptInfo(boolean[] archiFingerPrint){
		info = new NetworkArchitectureOptInfo(this.rate,
				this.configuration,this.term.getMaxSteps(),archiFingerPrint);
		
	}
	
	
	public class NetworkArchitectureOptInfo{
		
		private ExchangeRate rate;
		private Configuration configuration;
		private int step;
		private int maximum;
		private OptimizationResults results=new OptimizationResults();
		private boolean[] archiFingerPrint;
		
		public NetworkArchitectureOptInfo(ExchangeRate rate,
				Configuration configuration ,int maximum,boolean[] archiFingerPrint) {
			super();
			this.rate = rate;
			this.configuration = configuration;
			this.step=maximum;
			this.maximum=maximum;
			this.archiFingerPrint=archiFingerPrint;
		}
		
		public int getStep() {
			return step;
		}
		public void setStep(int step) {
			this.step = step;
		}
		
		
		public OptimizationResults getResults() {
			return results;
		}

		public int getMaximum() {
			return maximum;
		}
		public void setMaximum(int maximum) {
			this.maximum = maximum;
		}

		public ExchangeRate getRate() {
			return rate;
		}

		public Configuration getConfiguration() {
			return configuration;
		}

		public boolean[] getArchiFingerPrint() {
			return archiFingerPrint;
		}
		
		

		
	}
	
}
