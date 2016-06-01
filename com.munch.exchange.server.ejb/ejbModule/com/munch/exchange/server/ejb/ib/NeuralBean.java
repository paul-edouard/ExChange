package com.munch.exchange.server.ejb.ib;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.IsolatedNeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent;
import com.munch.exchange.model.core.ib.neural.NeuralNetwork;
import com.munch.exchange.model.core.ib.neural.NeuralTrainingElement;
import com.munch.exchange.services.ejb.interfaces.NeuralBeanRemote;

/**
 * Session Bean implementation class NeuralBean
 */
@Stateless
@LocalBean
public class NeuralBean implements NeuralBeanRemote{
	
	
	private static final Logger log = Logger.getLogger(ChartIndicatorBean.class.getName());
	
	@PersistenceContext
	private EntityManager em;
	

    /**
     * Default constructor. 
     */
    public NeuralBean() {
    }

    
  //Neural Configuration
    
	@Override
	public List<NeuralConfiguration> loadNeuralConfigurations(int contractId) {
		IbContract savedContract=em.find(IbContract.class, contractId);
		List<NeuralConfiguration> savedConfs =savedContract.getNeuralConfigurations();
		List<NeuralConfiguration> cpConfs=new LinkedList<NeuralConfiguration>();
		for(NeuralConfiguration conf:savedConfs){
			cpConfs.add(conf.copy());
		}
		
		return cpConfs;
	}


	@Override
	public NeuralConfiguration addNeuralConfiguration(int contractId,
			String configurationName) {
		
		IbContract savedContract=em.find(IbContract.class, contractId);
		
		NeuralConfiguration configuration=new NeuralConfiguration();
		configuration.setName(configurationName);
		configuration.setContract(savedContract);
		configuration.setCreationDate(Calendar.getInstance().getTimeInMillis());
		savedContract.getNeuralConfigurations().add(configuration);
		em.flush();
		
		return configuration;
	}


	@Override
	public void removeNeuralConfiguration(int contractId,
			NeuralConfiguration configuration) {
		IbContract savedContract=em.find(IbContract.class, contractId);
		for(NeuralConfiguration conf:savedContract.getNeuralConfigurations()){
			if(conf.getId()==configuration.getId()){
				savedContract.getNeuralConfigurations().remove(conf);
				em.remove(conf);
				break;
			}
		}
		//Save the chart signal
		em.flush();
	}

	
	//Neural Inputs
	
	@Override
	public List<NeuralInput> updateNeuralInputs(int configurationId, List<NeuralInput> neuralInputs) {
		
		
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configurationId);
		//Tries to find the neural input to remove
		for(NeuralInput ni_saved:savedConfig.getNeuralInputs()){
			boolean inputFound=false;
			for(NeuralInput ni_new:neuralInputs){
				if(ni_new.getId()==ni_saved.getId()){
					inputFound=true;
					deleteUnusedNeuralInputComponent(ni_saved, ni_new);
					break;
				}
			}
			
			if(!inputFound){
				em.remove(ni_saved);
			}
		}
		
		savedConfig.setNeuralInputs(neuralInputs);
		
		savedConfig=em.merge(savedConfig);
		em.flush();
		
//		Clean the parent before return
//		Not clean but copy the neural inputs
//		for(NeuralInput ni_saved:savedConfig.getNeuralInputs()){
//			ni_saved.setNeuralConfiguration(null);
//		}
		
		return copyNeuralInputs(savedConfig.getNeuralInputs());
		
	}
	
	private List<NeuralInput> copyNeuralInputs(List<NeuralInput> inputs){
		List<NeuralInput> neuralInputs=new LinkedList<NeuralInput>();
		
		for(NeuralInput n_input:inputs){
			NeuralInput cp=n_input.copy();
			cp.setNeuralConfiguration(null);
			neuralInputs.add(cp);
			
		}
		
		
		return neuralInputs;
		
	}
	
	
	
	private void deleteUnusedNeuralInputComponent(NeuralInput ni_saved, NeuralInput ni_new){
		for(NeuralInputComponent nic_saved:ni_saved.getComponents()){
			boolean componentFound=false;
			for(NeuralInputComponent nic_new:ni_new.getComponents()){
				if(nic_saved.getId()==ni_new.getId()){
					componentFound=true;
					break;
				}
			}
			
			if(!componentFound){
				em.remove(nic_saved);
			}
			
		}
	}


	@Override
	public List<NeuralInput> loadNeuralInputs(int configurationId) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configurationId);
		savedConfig.getNeuralInputs().size();
		savedConfig.getContract();
		
//		System.out.println("Load NeuralConfiguration: "+savedConfig.getName());
		
		List<NeuralInput> inputs=new LinkedList<NeuralInput>();
		for(NeuralInput ni_saved:savedConfig.getNeuralInputs()){
//			System.out.println("Neural Input: "+ni_saved.getId());
			ni_saved.getComponents().size();
//			for(NeuralInputComponent component:ni_saved.getComponents()){
//				System.out.println("Saved: "+component.getComponentType().toString());
//			}
			ni_saved.load();
//			if(ni_saved instanceof NeuralIndicatorInput){
//				NeuralIndicatorInput nii=(NeuralIndicatorInput) ni_saved;
//				System.out.println("Contract Id: "+nii.getContract().getId());
//			}
			
			inputs.add(ni_saved.copy());
		}
		
		em.flush();
		
		
		return inputs;
//		return null;
	}

	
	
	//Training Data
	
	@Override
	public List<NeuralTrainingElement> loadTrainingData(
			int configurationId) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configurationId);
		savedConfig.getNeuralTrainingElements().size();
			
		em.flush();
		
		return copy(savedConfig.getNeuralTrainingElements());
	}


	@Override
	public List<NeuralTrainingElement> updateTrainingData(
			int configurationId,List<NeuralTrainingElement> trainingElts) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configurationId);
		//Tries to find the neural input to remove
		for(NeuralTrainingElement saved_element:savedConfig.getNeuralTrainingElements()){
			boolean inputFound=false;
			for(NeuralTrainingElement new_element:trainingElts){
				if(new_element.getId()==saved_element.getId()){
					inputFound=true;
					break;
				}
			}
			
			if(!inputFound){
				em.remove(saved_element);
			}
		}
		
		savedConfig.setNeuralTrainingElements(trainingElts);
		savedConfig=em.merge(savedConfig);
		em.flush();
		
		return copy(savedConfig.getNeuralTrainingElements());
	}
	
	private List<NeuralTrainingElement> copy(List<NeuralTrainingElement> input){
		List<NeuralTrainingElement> elements=new LinkedList<NeuralTrainingElement>();
		for(NeuralTrainingElement saved_element:input){
			NeuralTrainingElement cp=saved_element.copy();
			cp.setNeuralConfiguration(null);
			elements.add(cp);
		}
		
		return elements;
	}
	
	
	
	//Neural Architectures

	@Override
	public List<NeuralArchitecture> loadNeuralArchitecture(
			int configurationId) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configurationId);
		savedConfig.getNeuralArchitectures().size();
		
		em.flush();
		
		return copyArchitectures(savedConfig.getNeuralArchitectures());
	}


	@Override
	public List<NeuralArchitecture> updateNeuralArchitecture(
			int configurationId, List<NeuralArchitecture> architectures) {
		NeuralConfiguration savedConfig = em.find(NeuralConfiguration.class,
				configurationId);

		// Tries to find the architectures to remove
		for (NeuralArchitecture saved_architecture : savedConfig
				.getNeuralArchitectures()) {
			boolean inputFound = false;
			for (NeuralArchitecture new_architecture : architectures) {
				if (new_architecture.getId() == saved_architecture.getId()) {
					findNeuralNetworkToRemove(saved_architecture, new_architecture);
					inputFound = true;
					break;
				}
			}

			if (!inputFound) {
				em.remove(saved_architecture);
			}
		}
		
		savedConfig.setNeuralArchitectures(architectures);
		
		savedConfig = em.merge(savedConfig);
		em.flush();
		
//		Clean the parent before returning

		return copyArchitectures(savedConfig.getNeuralArchitectures());
		
	}
	
	public void removeNeuralArchitecture(int configurationId,int architectureId){
		NeuralConfiguration savedConfig = em.find(NeuralConfiguration.class,
				configurationId);
		for (NeuralArchitecture saved_architecture : savedConfig
				.getNeuralArchitectures()) {
			if(saved_architecture.getId()==architectureId){
				savedConfig.getNeuralArchitectures().remove(saved_architecture);
				em.remove(saved_architecture);
				break;
			}
		}
		
		savedConfig = em.merge(savedConfig);
		em.flush();
	}
	
	public NeuralArchitecture addNeuralArchitecture(int configurationId,NeuralArchitecture architecture){
		NeuralConfiguration savedConfig = em.find(NeuralConfiguration.class,
				configurationId);
//		save the ids of the configurations
		HashSet<Integer> ids=new HashSet<Integer>();
		for (NeuralArchitecture saved_architecture : savedConfig
				.getNeuralArchitectures()) {
			ids.add(saved_architecture.getId());
		}
		
		savedConfig.getNeuralArchitectures().add(architecture);
		architecture.setNeuralConfiguration(savedConfig);
		
		savedConfig = em.merge(savedConfig);
		em.flush();
		
		
		for (NeuralArchitecture saved_architecture : savedConfig
				.getNeuralArchitectures()) {
			if(!ids.contains(saved_architecture.getId())){
				return saved_architecture.copy();
			}
		}
		
		return null;
		
		
	}

	
	private void findNeuralNetworkToRemove(NeuralArchitecture saved_architecture,NeuralArchitecture new_architecture){
		for(NeuralNetwork saved_network:saved_architecture.getNeuralNetworks()){
			boolean inputFound = false;
			for(NeuralNetwork new_network:new_architecture.getNeuralNetworks()){
				if(saved_network.getId()==new_network.getId()){
					inputFound = true;
					break;
				}
			}
			
			if (!inputFound) {
				em.remove(saved_network);
			}
		}
	}

	private List<NeuralArchitecture> copyArchitectures(List<NeuralArchitecture> input){
		List<NeuralArchitecture> architectures=new LinkedList<NeuralArchitecture>();
		for(NeuralArchitecture architecture:input){
			architecture.getNeuralNetworks().size();
			architectures.add(architecture.copy());
		}
		
		return architectures;
	}
	
	
	
	//Isolated Neural Architectures
	
	public List<IsolatedNeuralArchitecture> loadIsolatedNeuralArchitecture(int configurationId){
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configurationId);
		savedConfig.getIsolatedArchitectures().size();
		
		em.flush();
		
		return copyIsolatedArchitectures(savedConfig.getIsolatedArchitectures());
	}
	
	public IsolatedNeuralArchitecture addIsolatedNeuralArchitecture(int configurationId,IsolatedNeuralArchitecture architecture){
		NeuralConfiguration savedConfig = em.find(NeuralConfiguration.class,
				configurationId);
		
//		save the ids of the configurations
		HashSet<Integer> ids=new HashSet<Integer>();
		for (IsolatedNeuralArchitecture saved_architecture : savedConfig
				.getIsolatedArchitectures()) {
			ids.add(saved_architecture.getId());
		}
		
		savedConfig.getIsolatedArchitectures().add(architecture);
		architecture.setParent(savedConfig);
		
		savedConfig = em.merge(savedConfig);
		em.flush();
		
		
		for (IsolatedNeuralArchitecture saved_architecture : savedConfig
				.getIsolatedArchitectures()) {
			if(!ids.contains(saved_architecture.getId())){
				return saved_architecture.copy();
			}
		}
		
		return null;
	}
	
	public void removeIsolatedNeuralArchitecture(int configurationId,int architectureId){
		NeuralConfiguration savedConfig = em.find(NeuralConfiguration.class,
				configurationId);
		for (NeuralArchitecture saved_architecture : savedConfig
				.getIsolatedArchitectures()) {
			if(saved_architecture.getId()==architectureId){
				savedConfig.getNeuralArchitectures().remove(saved_architecture);
				em.remove(saved_architecture);
				break;
			}
		}
		
		savedConfig = em.merge(savedConfig);
		em.flush();
	}
	
	private List<IsolatedNeuralArchitecture> copyIsolatedArchitectures(List<IsolatedNeuralArchitecture> input){
		List<IsolatedNeuralArchitecture> architectures=new LinkedList<IsolatedNeuralArchitecture>();
		for(IsolatedNeuralArchitecture architecture:input){
			architecture.getNeuralNetworks().size();
			architectures.add(architecture.copy());
		}
		
		return architectures;
	}


	@Override
	public NeuralConfiguration update(NeuralConfiguration arg0) {
		em.merge(arg0);
		return arg0;
	}
	

}
