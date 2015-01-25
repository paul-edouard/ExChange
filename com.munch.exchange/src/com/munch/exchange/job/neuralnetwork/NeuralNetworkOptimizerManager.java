package com.munch.exchange.job.neuralnetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.neuroph.core.data.DataSet;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.services.INeuralNetworkProvider;

public class NeuralNetworkOptimizerManager extends Job{
	
	private static Logger logger = Logger.getLogger(NeuralNetworkOptimizerManager.class);
	
	public static final int RESTART=4000;
	
	private static final String OPTIMIZER_STATUS_STARTED="Started";
	private static final String OPTIMIZER_STATUS_RUNNING="Running";
	private static final String OPTIMIZER_STATUS_CANCEL="Cancel";
	private static final String OPTIMIZER_STATUS_FINISHED="Finished";
	
	private IEventBroker eventBroker;
	private INeuralNetworkProvider nnprovider;
	
	private ExchangeRate rate;
	private Configuration configuration;
	private DataSet trainingSet;
	
	private int currentInnerNeurons;
	private int maxInnerNeurons;
	
	private NNOptManagerInfo info;
	
	private LinkedList<NeuralNetworkOptimizer> optimizers=new LinkedList<NeuralNetworkOptimizer>();
	

	public NeuralNetworkOptimizerManager( IEventBroker eventBroker, INeuralNetworkProvider nnprovider,
			ExchangeRate rate, Configuration configuration, DataSet trainingSet, int minDim, int maxDim) {
		super("Neural Network Optimizer Manager");
		this.eventBroker = eventBroker;
		this.rate = rate;
		this.configuration = configuration;
		this.trainingSet = trainingSet;
		this.nnprovider = nnprovider;
		
		setMinMax(minDim, maxDim);
		
		
		for(int i=0;i<getNumberOfProcessors();i++){
			NeuralNetworkOptimizer optimizer=new NeuralNetworkOptimizer(this.rate, this.configuration,
					this.trainingSet, this.eventBroker,this.nnprovider, 0);
			optimizer.getInfo().setWorkerPos(i);
			optimizers.add(optimizer);
		}
		
		
		info=new NNOptManagerInfo(currentInnerNeurons, maxInnerNeurons, this.rate, this.configuration);
		
	}
	
	
	//################################
	//##          SETTER            ##
	//################################
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		info.setConfiguration(configuration);
		for(NeuralNetworkOptimizer optimizer:optimizers){
			if(optimizer!=null)
				optimizer.setConfiguration(this.configuration);
		}
	}

	public void setTrainingSet(DataSet trainingSet) {
		this.trainingSet = trainingSet;
		for(NeuralNetworkOptimizer optimizer:optimizers){
			if(optimizer!=null)
				optimizer.setTrainingSet(this.trainingSet);
		}
		
	}
	
	public void setMinMax(int minDim, int maxDim){
		currentInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(minDim,
				this.configuration.getNumberOfInputNeurons());
		maxInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(maxDim,
				this.configuration.getNumberOfInputNeurons());
		
		if(info!=null){
			info.setMinDim(currentInnerNeurons);
			info.setMaxDim(maxInnerNeurons);
		}
		
	}

	private int getNumberOfProcessors(){
		int nbOfProc=Runtime.getRuntime().availableProcessors();
		if(nbOfProc>1)nbOfProc--;
		return nbOfProc;
		
	}

	
	private HashSet<Integer> alreadyClearDim=new HashSet<Integer>();
	
	private void saveAndClearAllArchitectures(NeuralNetworkOptimizer optimizer){
		if(optimizer.getDimension()==0)return;
		if(alreadyClearDim.contains(optimizer.getDimension()))return;
		
		InfoPart.postInfoText(eventBroker, "Clear Architecture Started for dimension: "+optimizer.getDimension());
		/*
		LinkedList<NetworkArchitecture> list=configuration.searchNetworkArchitectures(optimizer.getDimension());
		for(NetworkArchitecture archi: list){
			archi.clearResultsAndNetwork();
		}
		*/
		alreadyClearDim.add(optimizer.getDimension());
		
	}
	
	//################################
	//##            RUN             ##
	//################################
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		InfoPart.postInfoText(eventBroker, "Network Manager Started"
		+"\tMin InnerNeurons: "+currentInnerNeurons+"\tMax InnerNeurons: "+maxInnerNeurons);
		
		alreadyClearDim.clear();
		info.clearOptimizerMap();
		eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_STARTED,info);
		
		IStatus returnStatus=Status.OK_STATUS;
		
		while(currentInnerNeurons<=maxInnerNeurons){
			int pos=-1;
			for(NeuralNetworkOptimizer optimizer:optimizers){
				pos++;
				if(optimizer.getState()==Job.RUNNING){
					//InfoPart.postInfoText(eventBroker, "Job "+pos+" is running");
					info.getOptimizerStatusMap().put(pos, OPTIMIZER_STATUS_RUNNING);
					eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED,info);
					continue;
				}
				
				saveAndClearAllArchitectures(optimizer);
				InfoPart.postInfoText(eventBroker, "New Optimizer started for dimension "+currentInnerNeurons+ " on position "+pos);
				
				int dimension=NetworkArchitecture.calculateActivatedConnectionsSize(
						configuration.getNumberOfInputNeurons(), currentInnerNeurons);
				
				optimizer.setDimension(dimension);
				optimizer.schedule();
				
				info.getOptimizerStatusMap().put(pos, OPTIMIZER_STATUS_STARTED);
				info.getOptimizerDimensionMap().put(pos,dimension);
				eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED,info);
				
				currentInnerNeurons++;break;
			}
			
			if(!makeItSleep(monitor)){
				returnStatus=Status.CANCEL_STATUS;
				break;
			}
			
		}
		
		boolean areAllFinished=false;
		while(!areAllFinished){
			areAllFinished=true;
			int pos=-1;
			for(NeuralNetworkOptimizer optimizer:optimizers){
				pos++;
				if(optimizer.getState()==Job.RUNNING){
					//InfoPart.postInfoText(eventBroker, "Job "+pos+" is running");
					info.getOptimizerStatusMap().put(pos, OPTIMIZER_STATUS_RUNNING);
					eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED,info);
					areAllFinished=false;
				}
				else{
					info.getOptimizerStatusMap().put(pos, OPTIMIZER_STATUS_FINISHED);
					saveAndClearAllArchitectures(optimizer);
					eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED,info);
				}
			}
			
			if(!makeItSleep(monitor)){
				returnStatus=Status.CANCEL_STATUS;
			}
			
			if(areAllFinished)break;
		
		}
		
		saveConfig();
		
		InfoPart.postInfoText(eventBroker, "Optimizer manager is finished!");
		eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED,info);
		
		return returnStatus;
	}
	
	
	private void saveConfig(){
		InfoPart.postInfoText(eventBroker, "Save Configuration after optimization: "+configuration.getName());
		
		if(nnprovider.saveConfiguration(configuration.getParent())){
			configuration.setDirty(false);
		}
		else{
			InfoPart.postInfoText(eventBroker, "Error: Couldn't save the configuration: "+configuration.getName());
		}
		
	}
	
	
	
	private boolean makeItSleep(IProgressMonitor monitor){
		
		try {
		
		Thread.sleep(RESTART);
		if (monitor.isCanceled()){
			int pos=-1;
			for(NeuralNetworkOptimizer optimizer:optimizers){
				pos++;
				if(optimizer.getState()==Job.RUNNING){
					InfoPart.postInfoText(eventBroker, "Try to cancel job "+pos+"!!");
					optimizer.cancel();
					
					info.getOptimizerStatusMap().put(pos, OPTIMIZER_STATUS_CANCEL);
					eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED,info);
					
				}
				//optimizer.join();
				//optimizer.getJobManager().
			}
			return false;
		}
		//this.wait(RESTART);
		//InfoPart.postInfoText(eventBroker, "Optimizer manager is sleeping!");
		
		return true;
		
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	//################################
	//##        INFOCLASS           ##
	//################################	
	public class NNOptManagerInfo{
		
		private int minDim;
		private int maxDim;
		
		private ExchangeRate rate;
		private Configuration configuration;
		
		private HashMap<Integer, String> optimizerStatusMap=new HashMap<Integer, String>();
		private HashMap<Integer, Integer> optimizerDimensionMap=new HashMap<Integer, Integer>();
		
		
		public NNOptManagerInfo(int minDim, int maxDim, ExchangeRate rate,
				Configuration configuration) {
			super();
			this.minDim = minDim;
			this.maxDim = maxDim;
			this.rate = rate;
			this.configuration = configuration;
		}
		public int getMinDim() {
			return minDim;
		}
		public void setMinDim(int minDim) {
			this.minDim = minDim;
		}
		public int getMaxDim() {
			return maxDim;
		}
		public void setMaxDim(int maxDim) {
			this.maxDim = maxDim;
		}
		public ExchangeRate getRate() {
			return rate;
		}
		public void setRate(ExchangeRate rate) {
			this.rate = rate;
		}
		public Configuration getConfiguration() {
			return configuration;
		}
		public void setConfiguration(Configuration configuration) {
			this.configuration = configuration;
		}
		public HashMap<Integer, String> getOptimizerStatusMap() {
			return optimizerStatusMap;
		}
		
		public HashMap<Integer, Integer> getOptimizerDimensionMap() {
			return optimizerDimensionMap;
		}
		public void clearOptimizerMap(){
			optimizerStatusMap.clear();
			optimizerDimensionMap.clear();
		}
		
		
	}
	
	
	//################################
	//##           MAIN             ##
	//################################	
	
	public static void main(String[] args){
		System.out.println(Runtime.getRuntime().availableProcessors());
		System.out.println(System.getenv("NUMBER_OF_PROCESSORS"));
	}
	

}
