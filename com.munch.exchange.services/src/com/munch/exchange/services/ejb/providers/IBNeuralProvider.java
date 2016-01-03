package com.munch.exchange.services.ejb.providers;

import java.util.Collections;
import java.util.List;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBNeuralProvider;
import com.munch.exchange.services.ejb.interfaces.NeuralBeanRemote;

public class IBNeuralProvider implements IIBNeuralProvider {
	
	
	BeanRemote<NeuralBeanRemote> beanRemote;

	@Override
	public void init() {
//		System.out.println("Initialiuation of IBNeuralProvider");
		beanRemote=new BeanRemote<NeuralBeanRemote>("NeuralBean",NeuralBeanRemote.class);
	}

	@Override
	public void close() {
		if(beanRemote!=null)
			beanRemote.CloseContext();

	}

	@Override
	public List<NeuralConfiguration> getNeuralConfigurations(IbContract contract) {
		if(beanRemote==null)init();
		
		List<NeuralConfiguration> configurations=beanRemote.getService().getNeuralConfigurations(contract);
		Collections.sort(configurations);
		
		for(NeuralConfiguration configuration:configurations ){
//			contract.getNeuralConfigurations().add(configuration);
			configuration.setContract(contract);
		}
		contract.setNeuralConfigurations(configurations);
		
		return configurations;
	}

	@Override
	public NeuralConfiguration addNeuralConfiguration(IbContract contract,
			String configurationName) {
		if(beanRemote==null)init();
		NeuralConfiguration config=beanRemote.getService().addNeuralConfiguration(contract, configurationName);
		if(config!=null){
			config.setContract(contract);
			contract.getNeuralConfigurations().add(config);
//			System.out.println("Saved configuration ID: "+config.getId());
		}
		
		Collections.sort(contract.getNeuralConfigurations());
		
		return config;
	}

	@Override
	public void removeNeuralConfiguration(IbContract contract,
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		beanRemote.getService().removeNeuralConfiguration(contract, configuration);
		contract.getNeuralConfigurations().remove(configuration);
	}

	@Override
	public void updateNeuralConfiguration(NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		beanRemote.getService().updateNeuralConfiguration(configuration);
	}
	
	

}
