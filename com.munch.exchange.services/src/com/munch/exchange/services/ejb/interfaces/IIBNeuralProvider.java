package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.IsolatedNeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;

public interface IIBNeuralProvider extends NeuralBeanRemote {
	
	
	//Initialization
	void init();
			
	//Close the service
	void close();
	
	//Neural Configuration
	public List<NeuralConfiguration> loadNeuralConfigurations(IbContract contract);
	public NeuralConfiguration addNeuralConfiguration(IbContract contract,String configurationName);
	public void removeNeuralConfiguration(IbContract contract,NeuralConfiguration configuration);
	
	//Neural Inputs
	public void loadNeuralInputs(NeuralConfiguration configuration);
	public void updateNeuralInputs(NeuralConfiguration configuration);
	
	//Training Data
	public void loadTrainingData(NeuralConfiguration configuration);
	public void updateTrainingData(NeuralConfiguration configuration);
	
	
	//Neural Architectures
	public void loadNeuralArchitecture(NeuralConfiguration configuration);
	public void addNeuralArchitecture(NeuralConfiguration configuration, NeuralArchitecture architecture);
	public void removeNeuralArchitecture(NeuralConfiguration configuration, NeuralArchitecture architecture);
	public void updateNeuralArchitecture(NeuralConfiguration configuration);
	
	//Isolated Neural Architectures
	public void loadIsolatedNeuralArchitecture(NeuralConfiguration configuration);
	public void addIsolatedNeuralArchitecture(NeuralConfiguration configuration, IsolatedNeuralArchitecture architecture);
	public void removeIsolatedNeuralArchitecture(NeuralConfiguration configuration, IsolatedNeuralArchitecture architecture);
	
	
	
}
