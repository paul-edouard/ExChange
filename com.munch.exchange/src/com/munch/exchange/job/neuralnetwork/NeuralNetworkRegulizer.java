package com.munch.exchange.job.neuralnetwork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

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
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.transfer.RandomGaussian;
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
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.utils.ProfitUtils;

public class NeuralNetworkRegulizer extends Job  {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkRegulizer.class);
	
	public static final int RESTART=500;
	
	
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
	
	//Effective Value Map
	private HashMap<String, Double> effectivityValueMap=new HashMap<String, Double>();
	private HashMap<String, ResultEntity> EntityMap=new HashMap<String, ResultEntity>();
	private int nbOfCalculation=20;
	
	
	private LinkedList<Learner> Learners=new LinkedList<Learner>();
	
	
	

	public NeuralNetworkRegulizer( IEventBroker eventBroker, INeuralNetworkProvider nnprovider,
			NetworkArchitecture archi) {
		super("Neural Network Regulizer");
		
		this.eventBroker = eventBroker;
		this.archi = archi;
		this.nnprovider = nnprovider;
		
		
		for(int i=0;i<getNumberOfProcessors();i++){
			Learners.add(new Learner(this.archi));
		}
		
		
	}
	
	//################################
	//##      GETTER & SETTER       ##
	//################################

	
	public void setArchi(NetworkArchitecture archi) {
		this.archi = archi;
	}
	
	private boolean prepareAll(){
		nnprovider.createAllValuePoints(archi.getParent(),true);
		
		effectivityValueMap.clear();
		EntityMap.clear();
		
		archi.getParent().resetTrainingData();
		trainingSet=archi.getParent().getTrainingDataSet();
		testSet=archi.getParent().getValidateDataSet();
		
		
		//Prepare the optimization of the network weights
		prepareNetworkWeightsRegularization();
		if(algorithm==null || term == null)return false;
		
		
		//Set the Varianz
		double varianz=archi.getParent().getRegBasicParam().getDoubleParam(RegularizationParameters.VARIANZ);
		archi.setVarianzOfFaMeNeurons(varianz);
		logger.info( "Varianz: "+varianz);
		
		for(Learner learner:this.Learners)
			learner.setArchitecture(archi);
		
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
	
	private int getNumberOfProcessors(){
		int nbOfProc=Runtime.getRuntime().availableProcessors();
		if(nbOfProc>1)nbOfProc--;
		return nbOfProc;
		
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
			int nbOfIndTrained=0;
			
			IStatus returnStatus=Status.OK_STATUS;
			
			
			while(nbOfIndTrained<nbOfIndividualsToTrain){
				
				if(monitor.isCanceled() || isCancel)break;
				if(archi.getRegularizationResultsEntities().size()<=nbOfIndTrained)break;
				
				ResultEntity ent=archi.getRegularizationResultsEntities().get(nbOfIndTrained);
				if(ent==null){
					logger.info("Result entity is null??: ");
					continue;
				}
				if(ent.getDoubleArray()==null || ent.getDoubleArray().length==0){
						logger.info("Results entity has not double array");
						continue;
				}
				
				int learnerID=0;
				for(Learner learner:this.Learners){
					learnerID++;
					
					if(learner.getState()==Job.RUNNING){
						continue;
					}
					
					learner.initFromResult(ent);
					learner.schedule();
					logger.info("New Learning is starting at position: "+nbOfIndTrained+", at learner: "+learnerID);
					nbOfIndTrained++;break;
					
				}
				
				if(!makeItSleep(monitor)){
					returnStatus=Status.CANCEL_STATUS;
					break;
				}
				
			}
			
			
			boolean areAllFinished=false;
			while(!areAllFinished){
				areAllFinished=true;
				for(Learner learner:Learners){
					if(learner.getState()==Job.RUNNING){
						//InfoPart.postInfoText(eventBroker, "Job "+pos+" is running");
						areAllFinished=false;
					}
				}
				
				if(!makeItSleep(monitor)){
					returnStatus=Status.CANCEL_STATUS;
				}
				
				if(areAllFinished)break;
			
			}
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			
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
	
	
	private boolean makeItSleep(IProgressMonitor monitor){
		
		try {
			Thread.sleep(RESTART);
			if (monitor.isCanceled()){
				int pos=-1;
				for(Learner learner:Learners){
					pos++;
					if(learner.getState()==Job.RUNNING){
						InfoPart.postInfoText(eventBroker, "Try to cancel job "+pos+"!!");
						learner.cancel();	
					}
				}
				return false;
			}
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	private void calculateEffectivitiy(){
		for(int j=0;j<archi.getRegularizationResultsEntities().size();j++){
			ResultEntity ent=archi.getRegularizationResultsEntities().get(j);
			calculateEffectivityValueOf(ent);
		}
	}
	
	private void calculateEffectivityValueOf(ResultEntity ent){
		//logger.info("calculateEffectivityValueOf="+ent.getId());
		
		//if(this.effectivityValueMap.containsKey(ent.getId()))
		//	return;
		
		double[] values=new double[nbOfCalculation];
		
		double mean=0;double var=0;
		//logger.info("Genome: "+Arrays.toString(ent.getDoubleArray()));
		for(int i=0;i<nbOfCalculation;i++){
			values[i]=objFunc.calculateError(ent.getDoubleArray(), null);
			mean+=values[i];
		}
		//logger.info("values: "+Arrays.toString(values));
		
		mean/=nbOfCalculation;
		for(int i=0;i<nbOfCalculation;i++){
			double diff=mean-values[i];
			var+=diff*diff;
		}
		var/=nbOfCalculation;
		
		double stdDev=Math.sqrt(var);
		
		double effectivity=mean+stdDev;
		
		//logger.info("Mean="+mean+", Std Dev="+stdDev+", Effectivity="+effectivity);
		if (stdDev>0.1){
			effectivityValueMap.put(ent.getId(), effectivity);
			EntityMap.put(ent.getId(), ent);
		}
		else{
			effectivityValueMap.put(ent.getId(), 0.0);
			EntityMap.put(ent.getId(), ent);
		}
		
	}
	
	
	private void reorderResults(){
		
		calculateEffectivitiy();
		
		/*
		for(int j=0;j<archi.getRegularizationResultsEntities().size();j++){
			ResultEntity ent=archi.getRegularizationResultsEntities().get(j);
			double error=objFunc.calculateError(ent.getDoubleArray(), null);
			ent.setValue(error);
		}
		*/
		//archi.sortRegularizationResults();
		LinkedList<ResultEntity> results=new LinkedList<ResultEntity>();
		for(String id:EntityMap.keySet()){
			double effectivity=effectivityValueMap.get(id);
			if(effectivity==0.0)continue;
			if (results.size()==0){
				results.add(EntityMap.get(id));continue;
			}
			int pos=0;
			for(ResultEntity sorted_ent:results){
				double effectivity_sorted=effectivityValueMap.get(sorted_ent.getId());
				if(effectivity_sorted<effectivity){
					pos++;
				}
				else break;
			}
			results.add(pos, EntityMap.get(id));
			
		}
		
		/*
		for(ResultEntity sorted_ent:results){
			double effectivity_sorted=effectivityValueMap.get(sorted_ent.getId());
			logger.info("effectivity_sorted="+effectivity_sorted);
		}
		*/
		
		archi.setRegularizationResultsEntities(results);
		
		
		
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
			
			if (j<nbOfIndividualsToTrain)
			//logger.info("Res: "+j+", Train="+train+", val="+val+", genome: "+Arrays.toString(ent.getDoubleArray()));
			 logger.info("Best Res: "+j+", Train="+train+", val="+val+", effectivity="+effectivityValueMap.get(ent.getId()));
			
		}
		
		
		logger.info("Mean Train: "+train_total/size+", Mean Val="+val_total/size);
		
		archi.setNewRandomValueOfFaMeNeurons();
		
	}
	
	private void resetMinMaxValuesOfAlgorithm(){
		
		if(algorithm instanceof EvolutionStrategy){
			EvolutionStrategy<double[]> ES=(EvolutionStrategy<double[]>) algorithm;
			ES.setMinimum(minWeigth);
			ES.setMaximum(maxWeigth);
		}
		
	}
	

	public synchronized void setMinMaxWeigth(ResultEntity ent){
		
		double[] w=ent.getDoubleArray();
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
		
	}
	
	//##################################
	//##     TERMINATION LISTENER     ##
	//##################################
	
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
	
	//################################
	//##          WORKER            ##
	//################################
	
	private class Learner extends Job implements LearningEventListener{
		
		@SuppressWarnings("rawtypes")
		private NeuralNetwork network=null;
		private NetworkArchitecture archi=null;
		private LearningRule learningRule=null;

		public Learner(NetworkArchitecture archi) {
			super("Neural Network Learner");
			this.archi=archi;
			setArchitecture(archi);
		}
		
		@SuppressWarnings("unchecked")
		public void setArchitecture(NetworkArchitecture archi){
			this.archi=archi;
			network=this.archi.getCopyOfFaMeNetwork();
			
			learningRule=archi.getParent().getRegTrainParam().createLearningRule();
			learningRule.addListener(this);
			network.setLearningRule(learningRule);
			
		}
		
		public void initFromResult(ResultEntity ent){
			archi.getFaMeNetwork().setWeights(ent.getDoubleArray());
		}

		@Override
		public void handleLearningEvent(LearningEvent event) {
			if(event.getSource() instanceof BackPropagation){
				
				//Rest the Random Gaussian values
				if(network==null)return;
				for(int i=0;i<network.getLayersCount();i++){
					Layer layer=network.getLayerAt(i);
					for(int j=0;j<layer.getNeuronsCount();j++){
						Neuron n=layer.getNeuronAt(j);
						if(n.getTransferFunction() instanceof RandomGaussian){
							RandomGaussian func=(RandomGaussian)n.getTransferFunction();
							func.resetValue();
						}
					}
				}
				
			}
			
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			//Learn the training set
			network.learn(trainingSet);
			
			//Save the last result
			if(learningRule instanceof BackPropagation){
				BackPropagation bp=(BackPropagation)learningRule;
				double error=bp.getTotalNetworkError();
				
				Double[] weigths=bp.getPreviousEpochNetworkWeights();
				double[] w=new double[weigths.length];
				for(int i=0;i<weigths.length;i++){
					w[i]=weigths[i];
				}
				ResultEntity ent=new ResultEntity(w, error);
				
				//Set the Min Max Weight values
				setMinMaxWeigth(ent);
				
				archi.addRegularizationResultEntity(ent);
			}
			
			return Status.OK_STATUS;
		}
		
	}

}
