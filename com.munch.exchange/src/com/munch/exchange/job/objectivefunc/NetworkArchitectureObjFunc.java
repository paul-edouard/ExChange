package com.munch.exchange.job.objectivefunc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
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
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.NnObjFunc;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.services.INeuralNetworkProvider;

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
	private NetworkArchitecture architecture;
	
	//Event variables
	private IEventBroker eventBroker;
	private INeuralNetworkProvider nnprovider;
	private IProgressMonitor monitor;
	
	private NetworkArchitectureOptInfo info;
	
	//Local Optimization algorithm
	//private List<Individual<double[], double[]>>  solutions;
	private ISOOptimizationAlgorithm<double[], double[], Individual<double[], double[]>> algorithm=null;
	private StepLimitPropChange<double[],double[]> term=null;
	private int optLoops=1;
	
	private boolean isCancel=false;
	
	private double minWeigth=Double.MAX_VALUE;
	private double maxWeigth=Double.MIN_VALUE;
	
	
	//Local learning Strategy
	private LearningRule learningRule=null;
	
	public static double MAX_WEIGTH_FACTOR=100d;
	public static int NB_OF_RESULTS_TO_TRAIN=10;
	
	public NetworkArchitectureObjFunc(ExchangeRate rate, Configuration configuration,DataSet testSet,
			IEventBroker eventBroker,IProgressMonitor monitor,INeuralNetworkProvider nnprovider ){
		
		this.rate=rate;
		this.configuration=configuration;
		this.trainingSet=testSet;
		this.eventBroker=eventBroker;
		this.monitor=monitor;
		this.nnprovider=nnprovider;
		
		isCancel=false;
		
	}
	
	
	@Override
	public double compute(boolean[] x, Random r) {
		
		//logger.info("Computing: "+Arrays.toString(x));
		
		architecture=configuration.searchArchitecture(x);
		if(architecture==null)return Constants.WORST_FITNESS;
		if(!(rate instanceof Stock))return Constants.WORST_FITNESS;
		if(!nnprovider.loadArchitectureResults((Stock) rate, architecture))return  Constants.WORST_FITNESS;
		
		//Prepare the optimization of the network weights
		prepareNetworkWeightsOptimization();
		if(algorithm==null || term == null)return Constants.WORST_FITNESS;
		
		
		//Prepare the learning strategy
		prepareLearningStrategy();
		if(learningRule==null)return Constants.WORST_FITNESS;
		
		//Prepare the Optimization info
		iniOptInfo(x);
		
		eventBroker.post(IEventConstant.NETWORK_OPTIMIZATION_STARTED,info);
		
		//Start the Optimization
		for(int i=0;i<optLoops;i++){
			
			if(monitor.isCanceled() || isCancel)break;
			
			info.setLoop(i);
			eventBroker.post(IEventConstant.NETWORK_OPTIMIZATION_LOOP,info);
			
			//Start the optimization algorithm
			architecture.prepareOptimizationStatistic();
			algorithm.call();
			architecture.saveOptimizationStatistic();
			
			//ResultEntity ref=architecture.getOptResults().getBestResult();
			
			//Loop on all the individuals to try increase the result quality
			info.setLearningMax(NB_OF_RESULTS_TO_TRAIN);
			for(int j=0;j<NB_OF_RESULTS_TO_TRAIN;j++){
				info.setLearningId(j);
				eventBroker.post(IEventConstant.NETWORK_LEARNING_STARTED,info);
				
				if(monitor.isCanceled() || isCancel)break;
				if(architecture.getResultsEntities().size()<=j)break;

				//Start learning for each individuals
				ResultEntity ent=architecture.getResultsEntities().get(j);
				if(ent==null){
					logger.info("Result entity is null??: ");
					continue;
				}
				if(ent.getDoubleArray()==null || ent.getDoubleArray().length==0){
					logger.info("Results entity has not double array");
					continue;
				}
				if( Double.isNaN(ent.getDoubleArray()[0])){
					logger.info("Ent: "+Arrays.toString(ent.getDoubleArray()));
					continue;
				}
				architecture.prepareTrainingStatistic(ent);
				architecture.getNetwork().setWeights(ent.getDoubleArray());
				architecture.getNetwork().learn(trainingSet);
				
				if(architecture.getNetwork().getLearningRule() instanceof BackPropagation){	
					BackPropagation bp = (BackPropagation)architecture.getNetwork().getLearningRule();
					
					ent.setValue(bp.getTotalNetworkError());
					architecture.saveTrainingStatistic(ent);
				}
				
			}
			
			if(monitor.isCanceled() || isCancel)break;
			
			//Set the best results as starting point for the next loop
			resetMinMaxValuesOfAlgorithm();
			configuration.getOptLearnParam().addLastBestResults(algorithm,architecture.getResultsEntities());
		}
		
		eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_FINISHED,info);
		
		
		ResultEntity bestResult=architecture.getBestResultEntity();
		if(bestResult==null || Double.isNaN(bestResult.getValue()))
			return  Constants.WORST_FITNESS;
		
		//logger.info("Archi: "+x);
		//logger.info("Number of results: "+architecture.getOptResults().getResults().size()+", Best: "+bestResult);
		
		//Save the Architecture results
		configuration.getOptResults(x.length).addResult(new ResultEntity(x, bestResult.getValue()));
		nnprovider.saveArchitectureResults((Stock) rate, architecture);
		architecture.clearResultsAndNetwork();
		
		return bestResult.getValue();
	}
	
	private void resetMinMaxValuesOfAlgorithm(){
		//configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Minimum, minWeigth);
		//configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Maximum, maxWeigth);
		
		if(algorithm instanceof EvolutionStrategy){
			EvolutionStrategy<double[]> ES=(EvolutionStrategy<double[]>) algorithm;
			ES.setMinimum(minWeigth);
			ES.setMaximum(maxWeigth);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void prepareLearningStrategy(){
		learningRule=configuration.getLearnParam().createLearningRule();
		learningRule.addListener(this);
		
		architecture.getNetwork().setLearningRule(learningRule);
	}
	
	@SuppressWarnings("unchecked")
	private void prepareNetworkWeightsOptimization() {
		//Set the dimension
		configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Dimension, architecture.getNetwork().getWeights().length);
		configuration.getOptLearnParam().setParam(AlgorithmParameters.EA_Dimension, architecture.getNetwork().getWeights().length);
		
		//minWeigth=configuration.getOptLearnParam().getDoubleParam(AlgorithmParameters.ES_Minimum);
		//maxWeigth=configuration.getOptLearnParam().getDoubleParam(AlgorithmParameters.ES_Maximum);
		
		minWeigth=-1.0;
		maxWeigth=1.0;
		
		// Reset Min Max According to the last best results weights
		for (ResultEntity ent : architecture.getResultsEntities()) {

			double[] weigths = ent.getDoubleArray();
			if (weigths == null)
				continue;

			for (int i = 0; i < weigths.length; i++) {
				if (weigths[i] > maxWeigth)
					maxWeigth = 1.1 * weigths[i];
				if (weigths[i] < minWeigth)
					minWeigth = 1.1 * weigths[i];
			}
		}
		
		configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Minimum, minWeigth);
		configuration.getOptLearnParam().setParam(AlgorithmParameters.ES_Maximum, maxWeigth);
		
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
		algorithm.setObjectiveFunction(new NnObjFunc(architecture, trainingSet) );
		
		// Add the last best results
		configuration.getOptLearnParam().addLastBestResults(algorithm,
				architecture.getResultsEntities());
		
		
	
		// Get the termination Criterion
		if (algorithm.getTerminationCriterion() instanceof StepLimitPropChange) {
			term = ((StepLimitPropChange<double[],double[]>) algorithm
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
				ResultEntity ent=new ResultEntity(ind.g,ind.v);
				if(info.getResults().addResult(ent)){
					eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
				}
			}
			else if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_STEP)){
				int val=(int)evt.getNewValue();
				if(val%20==0){
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
			
			//logger.info("Learning Error: "+ bp.getTotalNetworkError()+", x Size:" +trainingSet.size()*bp.getTotalNetworkError());
			
			ResultEntity ent=new ResultEntity(bp.getNeuralNetwork().getWeights(), bp.getTotalNetworkError());
			//Save the new results entity
			Double[] weigths=bp.getNeuralNetwork().getWeights();
			for(int j=0;j<weigths.length;j++){
				if(Double.isNaN(weigths[j]) || Double.isInfinite(weigths[j])){
					logger.info("Weigth is NaA or Infinite after learning!");
					return;
				}
				
				if(weigths[j]>maxWeigth)
					maxWeigth=Math.min(weigths[j]+Math.abs(weigths[j])*0.1,MAX_WEIGTH_FACTOR);
				if(weigths[j]<minWeigth)
					minWeigth=Math.max(weigths[j]-Math.abs(weigths[j])*0.1,-MAX_WEIGTH_FACTOR);
			}
			architecture.addResultEntity(ent);
			
			
			if(info.getResults().addResult(ent)){
				eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
			}
			
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
		private int loop=0;
		private int learningId=0;
		private int learningMax=0;
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
		

		public int getLoop() {
			return loop;
		}
		public void setLoop(int loop) {
			this.loop = loop;
		}

		public int getDimension(){
			return archiFingerPrint.length;
		}

		public int getLearningId() {
			return learningId;
		}

		public void setLearningId(int learningId) {
			this.learningId = learningId;
		}

		public int getLearningMax() {
			return learningMax;
		}

		public void setLearningMax(int learningMax) {
			this.learningMax = learningMax;
		}
		
		
		
	}
	

	public boolean isCancel() {
		return isCancel;
	}


	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	
	
	
	
}
