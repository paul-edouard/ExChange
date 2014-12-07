package com.munch.exchange.job.neuralnetwork;

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

public class NeuralNetworkOptimizerManager extends Job{
	
	private static Logger logger = Logger.getLogger(NeuralNetworkOptimizerManager.class);
	
	public static final int RESTART=4000;
	
	private IEventBroker eventBroker;
	
	private ExchangeRate rate;
	private Configuration configuration;
	private DataSet trainingSet;
	
	private int currentInnerNeurons;
	private int maxInnerNeurons;
	
	private NNOptManagerInfo info;
	
	private LinkedList<NeuralNetworkOptimizer> optimizers=new LinkedList<NeuralNetworkOptimizer>();
	

	public NeuralNetworkOptimizerManager( IEventBroker eventBroker,
			ExchangeRate rate, Configuration configuration, DataSet trainingSet, int minDim, int maxDim) {
		super("Neural Network Optimizer Manager");
		this.eventBroker = eventBroker;
		this.rate = rate;
		this.configuration = configuration;
		this.trainingSet = trainingSet;
		
		setMinMax(minDim, maxDim);
		
		
		for(int i=0;i<getNumberOfProcessors();i++){
			optimizers.add(new NeuralNetworkOptimizer(this.rate, this.configuration,
						this.trainingSet, this.eventBroker, 0));
		}
		
		
		info=new NNOptManagerInfo(currentInnerNeurons, maxInnerNeurons, this.rate, this.configuration);
		
	}
	
	
	//################################
	//##          SETTER            ##
	//################################
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		info.setConfiguration(configuration);
	}

	public void setTrainingSet(DataSet trainingSet) {
		this.trainingSet = trainingSet;
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

	//################################
	//##            RUN             ##
	//################################
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		InfoPart.postInfoText(eventBroker, "Network Manager Started"
		+"\tMin InnerNeurons: "+currentInnerNeurons+"\tMax InnerNeurons: "+maxInnerNeurons);
		
		eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_STARTED,info);
		
		while(currentInnerNeurons<=maxInnerNeurons){
			int pos=-1;
			for(NeuralNetworkOptimizer optimizer:optimizers){
				pos++;
				if(optimizer.getState()==Job.RUNNING){
					//InfoPart.postInfoText(eventBroker, "Job "+pos+" is running");
					continue;
				}
				
				InfoPart.postInfoText(eventBroker, "New Optimizer started for dimension "+currentInnerNeurons+ " on position "+pos);
				
				optimizer.setDimension(
						NetworkArchitecture.calculateActivatedConnectionsSize(
								configuration.getNumberOfInputNeurons(), currentInnerNeurons));
				optimizer.schedule();
				currentInnerNeurons++;break;
			}
			
			if(!makeItSleep(monitor))return Status.CANCEL_STATUS;
			
		}
		
		boolean areAllFinished=false;
		while(!areAllFinished){
			areAllFinished=true;
			for(NeuralNetworkOptimizer optimizer:optimizers){
				if(optimizer.getState()==Job.RUNNING)areAllFinished=false;
			}
			
			if(areAllFinished)break;
			
			if(!makeItSleep(monitor))return Status.CANCEL_STATUS;
		
		}
		
		
		InfoPart.postInfoText(eventBroker, "Optimizer manager is finished!");
		eventBroker.send(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED,info);
		
		return Status.OK_STATUS;
	}
	
	private boolean makeItSleep(IProgressMonitor monitor){
		
		try {
		
		if (monitor.isCanceled()){
			for(NeuralNetworkOptimizer optimizer:optimizers){
				optimizer.cancel();
				optimizer.join();
			}
			return false;
		}
		//this.wait(RESTART);
		//InfoPart.postInfoText(eventBroker, "Optimizer manager is sleeping!");
		Thread.sleep(RESTART);
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
		
		
		
		
	}
	
	
	//################################
	//##           MAIN             ##
	//################################	
	
	public static void main(String[] args){
		System.out.println(Runtime.getRuntime().availableProcessors());
		System.out.println(System.getenv("NUMBER_OF_PROCESSORS"));
	}
	

}
