package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;



@Remote
public interface NeuralBeanRemote {

	
	//Neural Configuration
	public List<NeuralConfiguration> getNeuralConfigurations(IbContract contract);
	public NeuralConfiguration addNeuralConfiguration(IbContract contract,String configurationName);
	public void removeNeuralConfiguration(IbContract contract,NeuralConfiguration configuration);
	public void updateNeuralConfiguration(NeuralConfiguration configuration);
	
	
}
