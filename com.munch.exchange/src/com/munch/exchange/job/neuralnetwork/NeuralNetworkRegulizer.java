package com.munch.exchange.job.neuralnetwork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.goataa.impl.algorithms.es.ESIndividual;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Constants;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
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
import com.munch.exchange.model.core.neuralnetwork.NnRegularizationObjFunc;
import com.munch.exchange.model.core.neuralnetwork.RegularizationParameters;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.utils.ProfitUtils;

public class NeuralNetworkRegulizer extends Job implements LearningEventListener {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkRegulizer.class);
	
	
	private IEventBroker eventBroker;
	private INeuralNetworkProvider nnprovider;
	private IProgressMonitor monitor;
	
	private NetworkArchitecture archi;
	private DataSet trainingSet;
	private DataSet testSet;
	
	private NnRegularizationObjFunc objFunc;
	private ISOOptimizationAlgorithm<double[], double[], Individual<double[], double[]>> algorithm=null;
	private StepLimitPropChange<double[],double[]> term=null;
	private int optLoops=1;
	private int nbOfIndividualsToTrain=10;
	
	private boolean isCancel=false;
	
	private double minWeigth=Double.MAX_VALUE;
	private double maxWeigth=Double.MIN_VALUE;
	
	public static double MAX_WEIGTH_FACTOR=100d;
	
	//Local learning Strategy
	private LearningRule learningRule=null;

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
	
	private boolean prepareAll(){
		nnprovider.createAllValuePoints(archi.getParent(),true);
		
		archi.getParent().resetTrainingData();
		trainingSet=archi.getParent().getTrainingDataSet();
		testSet=archi.getParent().getValidateDataSet();
		
		
		//Prepare the optimization of the network weights
		prepareNetworkWeightsRegularization();
		if(algorithm==null || term == null)return false;
		
		
		//Prepare the learning strategy
		prepareLearningStrategy();
		if(learningRule==null)return false;
		
		
		//Set the Varianz
		double varianz=archi.getParent().getRegBasicParam().getDoubleParam(RegularizationParameters.VARIANZ);
		archi.setVarianzOfFaMeNeurons(varianz);
		logger.info( "Varianz: "+varianz);
		
		return true;
	}
	
	private void prepareNetworkWeightsRegularization() {
		
		minWeigth=-1.0;
		maxWeigth=1.0;
		
		// Reset Min Max According to the last best results weights
		for (ResultEntity ent : archi.getRegularizationResultsEntities()) {

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
		
		
		// Create the algorithm
		int dimension=archi.getFaMeNetwork().getWeights().length;
		algorithm = archi.getParent().getRegOptParam().createDoubleAlgorithm(dimension,minWeigth,maxWeigth);
				
		//Set the number of loops
		if(archi.getParent().getRegOptParam().hasParamKey(AlgorithmParameters.OPTIMIZATION_Loops)){
			optLoops=archi.getParent().getRegOptParam().getIntegerParam(AlgorithmParameters.OPTIMIZATION_Loops);
		}
				
		//Set the number of individuals to train
		if(archi.getParent().getRegOptParam().hasParamKey(AlgorithmParameters.LEARNING_NbOfBestIndividuals)){
			nbOfIndividualsToTrain=archi.getParent().getRegOptParam().getIntegerParam(AlgorithmParameters.LEARNING_NbOfBestIndividuals);
		}
				
		//set the gpm
		final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));
		algorithm.setGPM(gpm);
				
		//Set the objective function
		objFunc=new NnRegularizationObjFunc(archi, trainingSet);
		algorithm.setObjectiveFunction(objFunc );
				
		// Add the last best results
		archi.getParent().getRegOptParam().addLastBestResults(algorithm,
				archi.getRegularizationResultsEntities());
				
				
			
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
	
	@SuppressWarnings("unchecked")
	private void prepareLearningStrategy(){
		learningRule=archi.getParent().getRegTrainParam().createLearningRule();
		learningRule.addListener(this);
		archi.getFaMeNetwork().setLearningRule(learningRule);
	}
	
	//################################
	//##            RUN             ##
	//################################

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		logger.info("Regulizer!");
		this.monitor=monitor;
		
		if(archi==null)return Status.CANCEL_STATUS;
		if(archi.getParent()==null)return Status.CANCEL_STATUS;
		
		if(!prepareAll())return Status.CANCEL_STATUS;
		
		//Start the Optimization
		for(int i=0;i<optLoops;i++){
					
			if(monitor.isCanceled() || isCancel)break;
			
			
			logger.info("Loop: "+i);
			
			algorithm.call();
			
			//Resort the results and plot
			logger.info("After Evolution Strategy: ");
			reorderResults();
			plotResults();
			
			//Loop on all the individuals to try increase the result quality
			for(int j=0;j<nbOfIndividualsToTrain;j++){
			
				if(monitor.isCanceled() || isCancel)break;
				if(archi.getRegularizationResultsEntities().size()<=j)break;

				//Start learning for each individuals
				ResultEntity ent=archi.getRegularizationResultsEntities().get(j);
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
						
						
				//architecture.prepareTrainingStatistic(ent);
				archi.getFaMeNetwork().setWeights(ent.getDoubleArray());
				archi.getFaMeNetwork().learn(trainingSet);
						
			}
			
			if(monitor.isCanceled() || isCancel)break;
			
			resetMinMaxValuesOfAlgorithm();
			
			//Resort the results and plot
			logger.info("After Learning: ");
			reorderResults();
			plotResults();
			
			
			archi.getParent().getRegOptParam().addLastBestResults(algorithm,
					archi.getRegularizationResultsEntities());
						
		}
		
		
		archi.saveRegularizationResults();
		
		return Status.OK_STATUS;
	}
	
	private void reorderResults(){
		for(int j=0;j<archi.getRegularizationResultsEntities().size();j++){
			ResultEntity ent=archi.getRegularizationResultsEntities().get(j);
			double error=objFunc.calculateError(ent.getDoubleArray(), null);
			ent.setValue(error);
		}
		archi.sortRegularizationResults();
		
	}
	
	private void plotResults(){
		
		archi.setMeanValueOfFaMeNeurons();
		
		double val_total=0;
		double train_total=0;
		
		int size=archi.getRegularizationResultsEntities().size();
		
		//for(int j=0;j<nbOfIndividualsToTrain;j++){
		for(int j=size-1;j>=0;j--){
					
			if(archi.getRegularizationResultsEntities().size()<=j)break;

			//Start learning for each individuals
			ResultEntity ent=archi.getRegularizationResultsEntities().get(j);
			
			archi.getFaMeNetwork().setWeights(ent.getDoubleArray());
			double[][] outputs=archi.calculateFaMeNetworkOutputsAndProfit(testSet, ProfitUtils.PENALTY);
			double val=outputs[5][outputs[5].length-1];
			val_total+=val;
			
			outputs=archi.calculateFaMeNetworkOutputsAndProfit(trainingSet, ProfitUtils.PENALTY);
			double train=outputs[5][outputs[5].length-1];
			train_total+=train;
			
			//logger.info("Res: "+j+", Train="+train+", val="+val+", genome: "+Arrays.toString(ent.getDoubleArray()));
			//logger.info("Res: "+j+", Train="+train+", val="+val);
			
		}
		
		
		logger.info("Mean Train: "+train_total/size+", Mean Val="+val_total/size);
		
		archi.setNewRandomValueOfFaMeNeurons();
		
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
	
	
	int count=0;
	double sumProfit=0;
	@Override
	public void handleLearningEvent(LearningEvent event) {
		if(event.getSource() instanceof BackPropagation){
			BackPropagation bp = (BackPropagation)event.getSource();
			
			saveLearningResults(bp);
			
			/*
			double error=bp.getTotalNetworkError();
			count++;
			this.archi.setMeanValueOfFaMeNeurons();
			double[][] outputs=archi.calculateFaMeNetworkOutputsAndProfit(testSet, ProfitUtils.PENALTY);
			sumProfit+=outputs[5][outputs[5].length-1];
			logger.info("Total Error: "+error+", count: "+count+ ", Validate: "+outputs[5][outputs[5].length-1]);
			this.archi.checkFaMeLayerWeigth();
			*/
			
			this.archi.setNewRandomValueOfFaMeNeurons();
			
			
		}
		
	}
	
	
	private void saveLearningResults(BackPropagation bp){
		
		//if(!(architecture.getNetwork().getLearningRule() instanceof BackPropagation))return;
		
		//BackPropagation bp = (BackPropagation)architecture.getNetwork().getLearningRule();
		double error=bp.getTotalNetworkError();
		
		//Double[] weigths=architecture.getNetwork().getWeights();
		Double[] weigths=bp.getPreviousEpochNetworkWeights();
		double[] w=new double[weigths.length];
		for(int i=0;i<weigths.length;i++){
			w[i]=weigths[i];
		}
		
		
		ResultEntity ent=new ResultEntity(w, error);
		ent.setId(ent.getId()+", Learning");
		//Save the new results entity

		for(int j=0;j<w.length;j++){
			if(Double.isNaN(w[j]) || Double.isInfinite(w[j])){
				logger.info("Weigth is NaN or Infinite after learning!");
				return;
			}
			
			if(w[j]>maxWeigth)
				maxWeigth=Math.min(w[j]+Math.abs(w[j])*0.1,MAX_WEIGTH_FACTOR);
			if(w[j]<minWeigth)
				minWeigth=Math.max(w[j]-Math.abs(w[j])*0.1,-MAX_WEIGTH_FACTOR);
		}
		archi.addRegularizationResultEntity(ent);
		
		
		//if(info.getResults().addResult(ent)){
		//	eventBroker.post(IEventConstant.NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
		//}
		
		
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
				//Individual<double[], double[]> ind=(Individual<double[], double[]>) evt.getNewValue();
				//ResultEntity ent=new ResultEntity(ind.g,ind.v);
				
			}
			else if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_STEP)){
				//int val=(int)evt.getNewValue();				
				archi.setNewRandomValueOfFaMeNeurons();
			}
			else if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_POP)){
				
				
				ESIndividual<double[]>[] pop=(ESIndividual<double[]>[])evt.getNewValue();
				for (int i = pop.length; (--i) >= 0;) {
					ESIndividual<double[]> p=pop[i];
					archi.addRegularizationResultEntity(
							new ResultEntity(p.g,p.v));
				}
				
			}
			// Cancel called
			if (monitor.isCanceled()){
				term.cancel();
				//eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_FINISHED,info);
			}
			
		}
		
		
	}

}
