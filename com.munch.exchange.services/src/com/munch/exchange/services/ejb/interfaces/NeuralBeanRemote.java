package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.IsolatedNeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralTrainingElement;



@Remote
public interface NeuralBeanRemote {

	
	//Neural Configuration
	public List<NeuralConfiguration> loadNeuralConfigurations(int contractId);
	public NeuralConfiguration addNeuralConfiguration(int contractId,String configurationName);
	public void removeNeuralConfiguration(int contractId,NeuralConfiguration configuration);
	public NeuralConfiguration update(NeuralConfiguration configuration);
	
	//Neural Inputs
	public List<NeuralInput> loadNeuralInputs(int configurationId);
	public List<NeuralInput> updateNeuralInputs(int configurationId,List<NeuralInput> neuralInputs);
	
	//Training Data
	public List<NeuralTrainingElement> loadTrainingData(int configurationId);
	public List<NeuralTrainingElement> updateTrainingData(int configurationId,List<NeuralTrainingElement> trainingElts);
	
	//Neural Architectures
	public List<NeuralArchitecture> loadNeuralArchitecture(int configurationId);
	public NeuralArchitecture addNeuralArchitecture(int configurationId,NeuralArchitecture architecture);
	public void removeNeuralArchitecture(int configurationId,int architectureId);
	public List<NeuralArchitecture> updateNeuralArchitecture(int configurationId, List<NeuralArchitecture> architectures);
	
	
	//Isolated Neural Architectures
	public List<IsolatedNeuralArchitecture> loadIsolatedNeuralArchitecture(int configurationId);
	public IsolatedNeuralArchitecture addIsolatedNeuralArchitecture(int configurationId,IsolatedNeuralArchitecture architecture);
	public void removeIsolatedNeuralArchitecture(int configurationId,int architectureId);
	
	
	
}
