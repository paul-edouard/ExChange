package com.munch.exchange.services.ejb.providers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent;
import com.munch.exchange.model.core.ib.neural.NeuralTrainingElement;
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
	public List<NeuralInput> updateNeuralInputs(NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
//		for(NeuralInput input: configuration.getNeuralInputs()){
//			for(NeuralInputComponent component:input.getComponents()){
//				System.out.println("Update: "+component.getComponentType().toString());
//			}
//			
//		}
		
		
		/*List<NeuralInput> neuralInputs=*/beanRemote.getService().updateNeuralInputs(configuration);
		
		return loadNeuralInputs(configuration);
	}

	@Override
	public List<NeuralInput> loadNeuralInputs(NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		List<NeuralInput> neuralInputs=beanRemote.getService().loadNeuralInputs(configuration);
		configuration.setNeuralInputs(neuralInputs);
		
		for(NeuralInput input: neuralInputs){
			input.setNeuralConfiguration(configuration);
//			for(NeuralInputComponent component:input.getComponents()){
//				System.out.println("Load: "+component.getComponentType().toString());
//			}
		}
		
		return neuralInputs;
	}

	@Override
	public List<NeuralTrainingElement> loadTrainingData(
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		List<NeuralTrainingElement> elements=beanRemote.getService().loadTrainingData(configuration);
		configuration.setNeuralTrainingElements(elements);
		
		for(NeuralTrainingElement element: elements){
			element.setNeuralConfiguration(configuration);
		}
		
		return elements;
	}

	@Override
	public List<NeuralTrainingElement> updateTrainingData(
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		beanRemote.getService().updateTrainingData(configuration);
		
		return loadTrainingData(configuration);
	}

	@Override
	public List<NeuralArchitecture> loadNeuralArchitecture(
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		List<NeuralArchitecture> neuralArchitectures=beanRemote.getService().loadNeuralArchitecture(configuration);
		
		for(NeuralArchitecture neuralArchitecture: neuralArchitectures){
			neuralArchitecture.setNeuralConfiguration(configuration);
		}
		configuration.setNeuralArchitectures(neuralArchitectures);
		
		return neuralArchitectures;
		
	}

	@Override
	public List<NeuralArchitecture> updateNeuralArchitecture(
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		beanRemote.getService().updateNeuralArchitecture(configuration);
		
		return loadNeuralArchitecture(configuration);
	}
	
	

}
