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
	private int nbOfIndividualsInParetoFront=10;
	
	private boolean isCancel=false;
	private int step=0;
	
	private double minWeigth=Double.MAX_VALUE;
	private double maxWeigth=Double.MIN_VALUE;
	
	public static double MAX_WEIGTH_FACTOR=100d;
	
	//Local learning Strategy
	//private LearningRule learningRule=null;
	
	//Effective Value Map
	private HashMap<String, double[]> effectivityValueMap=new HashMap<String, double[]>();
	private HashMap<String, ResultEntity> EntityMap=new HashMap<String, ResultEntity>();
	private HashMap<String, LearningRule> learningRuleMap=new HashMap<String, LearningRule>();
	private int nbOfCalculation=100;
	
	
	private LinkedList<Learner> Learners=new LinkedList<Learner>();
	private LinkedList<EffectivityCalculator> EffectivityCalculators=new LinkedList<EffectivityCalculator>();
	
	

	public NeuralNetworkRegulizer( IEventBroker eventBroker, INeuralNetworkProvider nnprovider,
			NetworkArchitecture archi) {
		super("Neural Network Regulizer");
		
		this.eventBroker = eventBroker;
		this.archi = archi;
		this.nnprovider = nnprovider;
		
		
		for(int i=0;i<getNumberOfProcessors();i++){
			Learners.add(new Learner(this.archi));
			EffectivityCalculators.add(new EffectivityCalculator());
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
		step=0;
		
		archi.getParent().resetTrainingData();
		trainingSet=archi.getParent().getTrainingDataSet();
		testSet=archi.getParent().getValidateDataSet();
		
		//Set the Varianz
		double varianz=archi.getParent().getRegBasicParam().getDoubleParam(RegularizationParameters.VARIANZ);
		archi.setVarianzOfFaMeNeurons(varianz);
		logger.info( "Varianz: "+varianz);
		
		
		//Prepare the optimization of the network weights
		prepareNetworkWeightsRegularization();
		if(algorithm==null || term == null)return false;
		
		
		for(Learner learner:this.Learners){
			learner.setArchitecture(archi);
			learner.setVarianz(varianz);
		}
		
		for(EffectivityCalculator calculator:this.EffectivityCalculators){
			//Set the objective function
			NnRegularizationObjFunc objFuncTest=new NnRegularizationObjFunc(archi, testSet);
			objFuncTest.setVarianz(varianz);
			NnRegularizationObjFunc objFuncTrain=new NnRegularizationObjFunc(archi, trainingSet);
			objFuncTrain.setVarianz(varianz);
			
			calculator.setObjFuncTest(objFuncTest);
			calculator.setObjFuncTrain(objFuncTrain);
		}
		
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
			//logger.info("Set the number of loops: "+optLoops);
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
	
		this.monitor=monitor;
		
		if(archi==null)return Status.CANCEL_STATUS;
		if(archi.getParent()==null)return Status.CANCEL_STATUS;
		
		if(!prepareAll())return Status.CANCEL_STATUS;
		
		sendRegularizationInfo(IEventConstant.REGULARIZATION_STARTED);
		
		
		//Start the Optimization
		for(int i=0;i<optLoops;i++){
					
			if(monitor.isCanceled() || isCancel)break;
			
			
			logger.info("Loop: "+i);
			
			algorithm.call();
			
			//Resort the results and plot
			logger.info("After Evolution Strategy: ");
			sendRegularizationInfo(IEventConstant.REGULARIZATION_NEW_STEP);
			
			//Loop on all the individuals to try increase the result quality
			int nbOfIndTrained=0;
			
			//IStatus returnStatus=Status.OK_STATUS;
			
			
			LinkedList<ResultEntity> results=this.getResultCopy();
			
			while(nbOfIndTrained<Math.max(nbOfIndividualsToTrain,nbOfIndividualsInParetoFront)){
				
				if(monitor.isCanceled() || isCancel)break;
				if(results.size()<=nbOfIndTrained)break;
				
				ResultEntity ent=results.get(nbOfIndTrained);
				if(ent==null){
					logger.info("Result entity is null??: ");
					continue;
				}
				if(ent.getDoubleArray()==null || ent.getDoubleArray().length==0){
						logger.info("Results entity has not double array");
						continue;
				}
				
				//int learnerID=0;
				for(Learner learner:this.Learners){
				//	learnerID++;
					
					if(learner.getState()==Job.RUNNING){
						continue;
					}
					
					learner.initFromResult(ent);
					learner.schedule();
					//logger.info("New Learning is starting at position: "+nbOfIndTrained+", at learner: "+learnerID+", ent: "+ent.getId());
					nbOfIndTrained++;break;
					
				}
				
				if(!waitForNextFreeLearners()){
					//returnStatus=Status.CANCEL_STATUS;
					break;
				}
				
			}
			
			
			waitUntilAllLearnersFinished();
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			
			//Resort the results and plot
			logger.info("After Learning: ");
			sendRegularizationInfo(IEventConstant.REGULARIZATION_NEW_STEP);
			
			
			archi.getParent().getRegOptParam().addLastBestResults(algorithm,
					archi.getRegularizationResultsEntities());
			
			resetMinMaxValuesOfAlgorithm();
						
		}
		
		
		archi.saveRegularizationResults();
		
		sendRegularizationInfo(IEventConstant.REGULARIZATION_FINISHED);
	
		return Status.OK_STATUS;
	}
	

	private void sendRegularizationInfo(String message){
		
		RegularizationInfo info =new RegularizationInfo(archi, step,optLoops*2,
				getResultCopy(),this,trainingSet, testSet,Math.max(nbOfIndividualsToTrain,nbOfIndividualsInParetoFront));
		
		
		if(message.equals(IEventConstant.REGULARIZATION_NEW_STEP)){
			reorderResults();
			eventBroker.send(message,info);
			step++;
			plotResults();
		}
		else{
			eventBroker.send(message,info);
		}
	}
	
	
	private LinkedList<ResultEntity> getResultCopy(){
		LinkedList<ResultEntity> copy=new LinkedList<ResultEntity>();
		for(ResultEntity ent : archi.getRegularizationResultsEntities()){
			copy.add(ent);
		}
		return copy;
	}
	
	private boolean waitForNextFreeLearners(){
		
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
	
	private IStatus waitUntilAllLearnersFinished(){
		
		IStatus returnStatus=Status.OK_STATUS;
		
		boolean areAllFinished=false;
		while(!areAllFinished){
			areAllFinished=true;
			for(Learner learner:Learners){
				if(learner.getState()==Job.RUNNING){
					areAllFinished=false;
				}
			}
			
			if(!waitForNextFreeLearners()){
				returnStatus=Status.CANCEL_STATUS;
			}
			
			if(areAllFinished)break;
		
		}
		
		return returnStatus;
	}
	
	private boolean waitForNextFreeEffectivityCalculators(IProgressMonitor monitor){
		
		try {
			Thread.sleep(RESTART);
			if (monitor.isCanceled()){
				int pos=-1;
				for(EffectivityCalculator calculator:EffectivityCalculators){
					pos++;
					if(calculator.getState()==Job.RUNNING){
						InfoPart.postInfoText(eventBroker, "Try to cancel job "+pos+"!!");
						calculator.cancel();	
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
	
	
	private IStatus waitUntilAllEffectivityCalculatorsFinished(){
		
		IStatus returnStatus=Status.OK_STATUS;
		
		boolean areAllFinished=false;
		while(!areAllFinished){
			areAllFinished=true;
			for(EffectivityCalculator calculator:EffectivityCalculators){
				if(calculator.getState()==Job.RUNNING){
					areAllFinished=false;
				}
			}
			
			if(!waitForNextFreeLearners()){
				returnStatus=Status.CANCEL_STATUS;
			}
			
			if(areAllFinished)break;
		
		}
		
		return returnStatus;
	}
	
	
	private void calculateEffectivitiy(){
		//Clean the maps from the dead keys
		LinkedList<String> keysToDelete=new LinkedList<String>();
		for(String key : effectivityValueMap.keySet()){
			boolean keyFound=false;
			for(ResultEntity ent : archi.getRegularizationResultsEntities()){
				if(ent.getId().equals(key)){
					keyFound=true;break;
				}
			}
			if(!keyFound)
				keysToDelete.add(key);
		}
		
		for(String key : keysToDelete){
			effectivityValueMap.remove(key);
			EntityMap.remove(key);
		}
		
		
		
		int nbOfEffCalculated=0;
		while(nbOfEffCalculated<archi.getRegularizationResultsEntities().size()){
			ResultEntity ent=archi.getRegularizationResultsEntities().get(nbOfEffCalculated);
			
			if(this.effectivityValueMap.containsKey(ent.getId())){
				nbOfEffCalculated++;continue;
			}
			
			//int learnerID=0;
			for(EffectivityCalculator calculator:EffectivityCalculators){
				//learnerID++;
				
				if(calculator.getState()==Job.RUNNING)continue;
				
				calculator.setEnt(archi.getRegularizationResultsEntities().get(nbOfEffCalculated));
				calculator.schedule();
				//logger.info("New Learning is starting at position: "+nbOfIndTrained+", at learner: "+learnerID+", ent: "+ent.getId());
				nbOfEffCalculated++;break;
				
			}
			
			if(!waitForNextFreeEffectivityCalculators(monitor))break;
			
		}
		
		waitUntilAllEffectivityCalculatorsFinished();
	
	}
	
	/*
	private void calculateEffectivityValueOf(ResultEntity ent){
		//logger.info("calculateEffectivityValueOf="+ent.getId());
		
		if(this.effectivityValueMap.containsKey(ent.getId()))
			return;
		
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
		
		
		saveEffectivity(ent,effectivity,stdDev);
		
	}
	*/
	
	private synchronized void saveEffectivity(ResultEntity ent,double effectivityTrain, double effectivityTest,double stdDev){
		// logger.info("Mean="+mean+", Std Dev="+stdDev+", Effectivity="+effectivity);
		if (stdDev > 0.1) {
			double[] eff={effectivityTrain,effectivityTest};
			effectivityValueMap.put(ent.getId(), eff);
			EntityMap.put(ent.getId(), ent);
		} else {
			double[] eff=new double[2];
			effectivityValueMap.put(ent.getId(), eff);
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
		for(String id:paretoSort()){
			results.add(EntityMap.get(id));
		}
		
		
		/*
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
		*/
		archi.setRegularizationResultsEntities(results);
		
	}
	
	
	//Pareto sorter
	private LinkedList<String> paretoSort(){
		LinkedList<String> resultId=new LinkedList<String>();
		for(String id:EntityMap.keySet()){
			if(effectivityValueMap.get(id)[0]>0)
				resultId.add(id);
		}
		
		LinkedList<String> allElements=new LinkedList<>();
		
		int paretoId=0;
		while(resultId.size()>0){
			
			LinkedList<String> pareto=new LinkedList<>();
			
			//Searcht the pareto elements
			for(int i=0;i<resultId.size();i++){
				double[] eff=effectivityValueMap.get(resultId.get(i));
				
				boolean toAdd=true;
				for(int j=0;j<resultId.size();j++){
					if(j==i)continue;
					double[] eff_r=effectivityValueMap.get(resultId.get(j));
					
					if(eff[0]>eff_r[0] && eff[1]>eff_r[1]){
						toAdd=false;
						break;
					}
					
				}
				
				if(toAdd){
					int p_pos=0;
					for(String p_id:pareto){
						double[] eff_r=effectivityValueMap.get(p_id);
						if(eff_r[0]>eff[0])
							break;
						p_pos++;
					}
					pareto.add(p_pos, resultId.get(i));
					
				}
				
			}
			
			//Remove the pareto elements from the list
			for(String id :pareto)
				resultId.remove(id);
			
			
			if(paretoId==0)
				nbOfIndividualsInParetoFront=pareto.size();
			
			//Sort the pareto elements
			allElements.addAll(pareto);
			
			
			paretoId++;
		}
		
		
		logger.info("Number of individuals on the pareto front: "+nbOfIndividualsInParetoFront);
		
		return allElements;
		
	}
	
	
	private void plotResults(){
		
		archi.setMeanValueOfFaMeNeurons();
		
		double val_total=0;
		double train_total=0;
		
		int size=archi.getRegularizationResultsEntities().size();
		
		logger.info("Population: "+size);
		
		//for(int j=0;j<nbOfIndividualsToTrain;j++){
		int nbOfIndToPlot=Math.max(nbOfIndividualsToTrain,nbOfIndividualsInParetoFront);
		
		for(int j=size-1;j>=0;j--){
			
			if (j>=nbOfIndToPlot)continue;
					
			if(archi.getRegularizationResultsEntities().size()<=j)break;

			//Start learning for each individuals
			ResultEntity ent=archi.getRegularizationResultsEntities().get(j);
			
			archi.getFaMeNetwork().setWeights(ent.getDoubleArray());
			double[][] outputs=archi.calculateFaMeNetworkOutputsAndProfit(testSet);
			double val=outputs[5][outputs[5].length-1];
			val_total+=val;
			
			outputs=archi.calculateFaMeNetworkOutputsAndProfit(trainingSet);
			double train=outputs[5][outputs[5].length-1];
			train_total+=train;
			
			//if (j<nbOfIndividualsToTrain)
			//logger.info("Res: "+j+", Train="+train+", val="+val+", genome: "+Arrays.toString(ent.getDoubleArray()));
			 logger.info("Best Res: "+j+", Train="+train+", val="+val+", effectivity="+Arrays.toString(effectivityValueMap.get(ent.getId()))+", id="+ent.getId());
			
		}
		
		
		logger.info("Mean Train: "+train_total/nbOfIndToPlot+", Mean Val="+val_total/nbOfIndToPlot);
		
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
	
	public synchronized LearningRule getLearningRuleOf(ResultEntity ent){
		if(learningRuleMap.containsKey(ent.getId())){
			return learningRuleMap.get(ent.getId());
		}
		else{
			LearningRule learningRule=archi.getParent().getRegTrainParam().createLearningRule();
			learningRuleMap.put(ent.getId(), learningRule);
			return learningRule;
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
	
	//#################################
	//##          WORKERS            ##
	//#################################
	
	private class EffectivityCalculator extends Job{
		
		ResultEntity ent;
		private NnRegularizationObjFunc objFuncTrain;
		private NnRegularizationObjFunc objFuncTest;
		
		public EffectivityCalculator(){
			super("EffectivityCalculator");
		}

		public void setEnt(ResultEntity ent) {
			this.ent = ent;
		}
		
		

		public void setObjFuncTrain(NnRegularizationObjFunc objFuncTrain) {
			this.objFuncTrain = objFuncTrain;
		}
		
		public void setObjFuncTest(NnRegularizationObjFunc objFuncTest) {
			this.objFuncTest = objFuncTest;
		}
		

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// TODO Auto-generated method stub
			
			if(effectivityValueMap.containsKey(ent.getId()))
				return Status.OK_STATUS;
			
			double[] valuesTrain=new double[nbOfCalculation];
			double[] valuesTest=new double[nbOfCalculation];
			
			double meanTrain=0;double varTrain=0;
			double meanTest=0;double varTest=0;
			
			//logger.info("Genome: "+Arrays.toString(ent.getDoubleArray()));
			for(int i=0;i<nbOfCalculation;i++){
				valuesTrain[i]=objFuncTrain.calculateError(ent.getDoubleArray(), null);
				valuesTest[i]=objFuncTest.calculateError(ent.getDoubleArray(), null);
				
				
				meanTrain+=valuesTrain[i];
				meanTest+=valuesTest[i];
			}
			//logger.info("values: "+Arrays.toString(values));
			
			meanTrain/=nbOfCalculation;
			meanTest/=nbOfCalculation;
			
			for(int i=0;i<nbOfCalculation;i++){
				double diffTrain=meanTrain-valuesTrain[i];
				varTrain+=diffTrain*diffTrain;
				
				double diffTest=meanTest-valuesTest[i];
				varTest+=diffTest*diffTest;
				
			}
			varTrain/=nbOfCalculation;
			varTest/=nbOfCalculation;
			
			double stdDevTrain=Math.sqrt(varTrain);
			double stdDevTest=Math.sqrt(varTest);
			
			double effectivityTrain=meanTrain+stdDevTrain;
			double effectivityTest=meanTest+stdDevTest;
			
			
			saveEffectivity(ent,effectivityTrain,effectivityTest , stdDevTrain);
			
			
			return Status.OK_STATUS;
		}
		
		
		
	}
	
	
	
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
			
			//learningRule=archi.getParent().getRegTrainParam().createLearningRule();
			//learningRule.addListener(this);
			//network.setLearningRule(learningRule);
			
		}
		
		
		public void setVarianz(double varianz){
			NetworkArchitecture.setVarianzOfFaMeNeurons(network, varianz);
		}
		
		public void initFromResult(ResultEntity ent){
			network.setWeights(ent.getDoubleArray());
			
			learningRule=getLearningRuleOf(ent);
			learningRule.removeAllListerner();
			learningRule.addListener(this);
			network.setLearningRule(learningRule);
		}

		@Override
		public void handleLearningEvent(LearningEvent event) {
			if(event.getSource() instanceof BackPropagation){
				NetworkArchitecture.setNewRandomValueOfFaMeNeurons(network);
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
			
			learningRule.removeListener(this);
			
			//logger.info("Values: "+NetworkArchitecture.plotValueOfFaMeNeurons(network));
			
			return Status.OK_STATUS;
		}
		
	}
	
	
	//################################
	//##           INFO             ##
	//################################
	
	public class RegularizationInfo{
		
		private NetworkArchitecture archi;
		private int step;
		private int maxStep;
		
		private LinkedList<ResultEntity> population=new LinkedList<ResultEntity>();
		private NeuralNetworkRegulizer regulizer;
		private int nbOfIndividualsToTrain;
		
		private DataSet trainingSet;
		private DataSet testSet;
		
		public RegularizationInfo(NetworkArchitecture archi, int step,int maxStep,
				LinkedList<ResultEntity> population, NeuralNetworkRegulizer regulizer,
				DataSet trainingSet,DataSet testSet,int nbOfIndividualsToTrain ) {
			super();
			this.archi = archi;
			this.step = step;
			this.maxStep = maxStep;
			this.population = population;
			this.regulizer = regulizer;
			
			this.trainingSet = trainingSet;
			this.testSet = testSet;
			this.nbOfIndividualsToTrain=nbOfIndividualsToTrain;
		}

		public NetworkArchitecture getArchi() {
			return archi;
		}

		public int getStep() {
			return step;
		}

		public int getMaxStep() {
			return maxStep;
		}

		public LinkedList<ResultEntity> getPopulation() {
			return population;
		}

		public NeuralNetworkRegulizer getRegulizer() {
			return regulizer;
		}

		public void setRegulizer(NeuralNetworkRegulizer regulizer) {
			this.regulizer = regulizer;
		}
		

		public DataSet getTrainingSet() {
			return trainingSet;
		}
		

		public DataSet getTestSet() {
			return testSet;
		}

		public int getNbOfIndividualsToTrain() {
			return nbOfIndividualsToTrain;
		}
		
		
		
		
	}
	
	
	
}
