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

	
	//Neural Configuration
	
	
	@Override
	public List<NeuralConfiguration> getNeuralConfigurations(IbContract contract) {
		
		List<NeuralConfiguration> configurations=this.getNeuralConfigurations(contract.getId());
		contract.setNeuralConfigurations(configurations);
		
		return configurations;
	}
	
	@Override
	public List<NeuralConfiguration> getNeuralConfigurations(int contractId) {
		if(beanRemote==null)init();
		
		List<NeuralConfiguration> configurations=beanRemote.getService().getNeuralConfigurations(contractId);
		Collections.sort(configurations);
		
		return configurations;
	}
	
	
	@Override
	public NeuralConfiguration addNeuralConfiguration(IbContract contract,
			String configurationName) {
		
		NeuralConfiguration config=addNeuralConfiguration(contract.getId(), configurationName);
		if(config!=null){
			config.setContract(contract);
			contract.getNeuralConfigurations().add(config);
		}
		
		Collections.sort(contract.getNeuralConfigurations());
		
		return config;
	}
	
	@Override
	public NeuralConfiguration addNeuralConfiguration(int contractId,
			String configurationName) {
		if(beanRemote==null)init();
		NeuralConfiguration config=beanRemote.getService().addNeuralConfiguration(contractId, configurationName);
		
		return config;
	}
	

	@Override
	public void removeNeuralConfiguration(IbContract contract,
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		removeNeuralConfiguration(contract.getId(), configuration);
		contract.getNeuralConfigurations().remove(configuration);
	}
	
	@Override
	public void removeNeuralConfiguration(int contractId,
			NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		beanRemote.getService().removeNeuralConfiguration(contractId, configuration);
	}
	

	//Neural Inputs

	@Override
	public void updateNeuralInputs(NeuralConfiguration configuration) {
		List<NeuralInput> neuralInputs=updateNeuralInputs(configuration.getId(),configuration.getNeuralInputs());
		configuration.setNeuralInputs(neuralInputs);
	}
	
	@Override
	public List<NeuralInput> updateNeuralInputs(int configurationId, List<NeuralInput> neuralInputs) {
		if(beanRemote==null)init();
		return beanRemote.getService().updateNeuralInputs(configurationId,  neuralInputs);
	}

	
	@Override
	public void loadNeuralInputs(NeuralConfiguration configuration) {
		
		List<NeuralInput> neuralInputs=loadNeuralInputs(configuration.getId());
		configuration.setNeuralInputs(neuralInputs);
		
	}
	
	@Override
	public List<NeuralInput> loadNeuralInputs(int configurationId) {
		if(beanRemote==null)init();
		
		List<NeuralInput> neuralInputs=beanRemote.getService().loadNeuralInputs(configurationId);
		
		return neuralInputs;
	}
	
	

	
	//Training Data
	
	@Override
	public void loadTrainingData(NeuralConfiguration configuration){
		List<NeuralTrainingElement> elts=loadTrainingData(configuration.getId());
		configuration.setNeuralTrainingElements(elts);
	}
	
	@Override
	public List<NeuralTrainingElement> loadTrainingData(
			int configurationId) {
		if(beanRemote==null)init();
		
		List<NeuralTrainingElement> elements=beanRemote.getService().loadTrainingData(configurationId);
		
		return elements;
	}
	

	@Override
	public void updateTrainingData(NeuralConfiguration configuration) {
		if(beanRemote==null)init();
		
		
		List<NeuralTrainingElement> elts=updateTrainingData(configuration.getId(),
				configuration.getNeuralTrainingElements());
		configuration.setNeuralTrainingElements(elts);
		
	}
	
	@Override
	public List<NeuralTrainingElement> updateTrainingData(
			int configurationId,List<NeuralTrainingElement> trainingElts) {
		if(beanRemote==null)init();
		
		return beanRemote.getService().updateTrainingData(configurationId,trainingElts);
		
	}
	
	
	
	
	
	//Neural Architectures
	
	

	@Override
	public void loadNeuralArchitecture(
			NeuralConfiguration configuration) {

		List<NeuralArchitecture> neuralArchitectures=loadNeuralArchitecture(configuration.getId());
		configuration.setNeuralArchitectures(neuralArchitectures);
		
	}
	
	@Override
	public List<NeuralArchitecture> loadNeuralArchitecture(
			int configurationId) {
		if(beanRemote==null)init();
		
		return beanRemote.getService().loadNeuralArchitecture(configurationId);
		
	}
	

	@Override
	public void updateNeuralArchitecture(
			NeuralConfiguration configuration) {
		
		List<NeuralArchitecture> achitectures=updateNeuralArchitecture(configuration.getId(),
				configuration.getNeuralArchitectures());
		configuration.setNeuralArchitectures(achitectures);
		
	}
	
	
	@Override
	public List<NeuralArchitecture> updateNeuralArchitecture(
			int configurationId,  List<NeuralArchitecture> architectures) {
		if(beanRemote==null)init();
		
		return beanRemote.getService().updateNeuralArchitecture(configurationId,architectures);
		
	
	}
	
	@Override
	public void addNeuralArchitecture(NeuralConfiguration configuration, NeuralArchitecture architecture){
		NeuralArchitecture added=addNeuralArchitecture(configuration.getId(), architecture);
//		System.out.println("Added Archi:"+added);
		
		if(added==null)return;
		
//		System.out.println("Added Archi:"+added.getId());
		
		configuration.getNeuralArchitectures().add(added);
		added.setNeuralConfiguration(configuration);
		
	}
	
	@Override
	public NeuralArchitecture addNeuralArchitecture(int configurationId,NeuralArchitecture architecture){
		if(beanRemote==null)init();
		return beanRemote.getService().addNeuralArchitecture(configurationId,architecture);
	}
	
	@Override
	public void removeNeuralArchitecture(NeuralConfiguration configuration, NeuralArchitecture architecture){
		removeNeuralArchitecture(configuration.getId(), architecture.getId());
		configuration.getNeuralArchitectures().remove(architecture);
	}
	
	@Override
	public void removeNeuralArchitecture(int configurationId,int architectureId){
		if(beanRemote==null)init();
		beanRemote.getService().removeNeuralArchitecture(configurationId,architectureId);
	}
	
	
	
	
	
	

}
