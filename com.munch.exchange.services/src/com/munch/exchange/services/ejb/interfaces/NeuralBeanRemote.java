package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralTrainingElement;



@Remote
public interface NeuralBeanRemote {

	
	//Neural Configuration
	public List<NeuralConfiguration> getNeuralConfigurations(IbContract contract);
	public NeuralConfiguration addNeuralConfiguration(IbContract contract,String configurationName);
	public void removeNeuralConfiguration(IbContract contract,NeuralConfiguration configuration);
	
	//Neural Inputs
	public List<NeuralInput> loadNeuralInputs(NeuralConfiguration configuration);
	public List<NeuralInput> updateNeuralInputs(NeuralConfiguration configuration);
	
	//Training Data
	public List<NeuralTrainingElement> loadTrainingData(NeuralConfiguration configuration);
	public List<NeuralTrainingElement> updateTrainingData(NeuralConfiguration configuration);
	
	
	
}
