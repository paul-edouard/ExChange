package com.munch.exchange.server.ejb.ib;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent;
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


	@Override
	public List<NeuralConfiguration> getNeuralConfigurations(IbContract contract) {
		IbContract savedContract=em.find(IbContract.class, contract.getId());
		List<NeuralConfiguration> savedConfs =savedContract.getNeuralConfigurations();
		List<NeuralConfiguration> cpConfs=new LinkedList<NeuralConfiguration>();
		for(NeuralConfiguration conf:savedConfs){
			cpConfs.add(conf.copy());
		}
		
		return cpConfs;
	}


	@Override
	public NeuralConfiguration addNeuralConfiguration(IbContract contract,
			String configurationName) {
		
		IbContract savedContract=em.find(IbContract.class, contract.getId());
		
		NeuralConfiguration configuration=new NeuralConfiguration();
		configuration.setName(configurationName);
		configuration.setContract(savedContract);
		configuration.setCreationDate(Calendar.getInstance().getTimeInMillis());
		savedContract.getNeuralConfigurations().add(configuration);
		em.flush();
		
		return configuration;
	}


	@Override
	public void removeNeuralConfiguration(IbContract contract,
			NeuralConfiguration configuration) {
		IbContract savedContract=em.find(IbContract.class, contract.getId());
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


	@Override
	public List<NeuralInput> updateNeuralInputs(NeuralConfiguration configuration) {
		
		
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configuration.getId());
		//Tries to find the neural input to remove
		for(NeuralInput ni_saved:savedConfig.getNeuralInputs()){
			boolean inputFound=false;
			for(NeuralInput ni_new:configuration.getNeuralInputs()){
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
		
		
		
		savedConfig=em.merge(configuration);
		em.flush();
		
		return savedConfig.getNeuralInputs();
		
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
	public List<NeuralInput> loadNeuralInputs(NeuralConfiguration configuration) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configuration.getId());
		savedConfig.getNeuralInputs().size();
		
		List<NeuralInput> inputs=new LinkedList<NeuralInput>();
		for(NeuralInput ni_saved:savedConfig.getNeuralInputs()){
			ni_saved.getComponents().size();
			ni_saved.load();
			inputs.add(ni_saved.copy());
		}
		
		em.flush();
		
		
		return inputs;
	}


	@Override
	public List<NeuralTrainingElement> loadTrainingData(
			NeuralConfiguration configuration) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configuration.getId());
		savedConfig.getNeuralTrainingElements().size();
		
		List<NeuralTrainingElement> elements=new LinkedList<NeuralTrainingElement>();
		for(NeuralTrainingElement saved_element:savedConfig.getNeuralTrainingElements()){
			elements.add(saved_element.copy());
		}
		
		em.flush();
		
		return elements;
	}


	@Override
	public List<NeuralTrainingElement> updateTrainingData(
			NeuralConfiguration configuration) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configuration.getId());
		//Tries to find the neural input to remove
		for(NeuralTrainingElement saved_element:savedConfig.getNeuralTrainingElements()){
			boolean inputFound=false;
			for(NeuralTrainingElement new_element:configuration.getNeuralTrainingElements()){
				if(new_element.getId()==saved_element.getId()){
					inputFound=true;
					break;
				}
			}
			
			if(!inputFound){
				em.remove(saved_element);
			}
		}
		
		
		
		savedConfig=em.merge(configuration);
		em.flush();
		
		
		return savedConfig.getNeuralTrainingElements();
	}


	@Override
	public List<NeuralArchitecture> loadNeuralArchitecture(
			NeuralConfiguration configuration) {
		NeuralConfiguration savedConfig=em.find(NeuralConfiguration.class, configuration.getId());
		savedConfig.getNeuralArchitectures().size();
		
		
		List<NeuralArchitecture> architectures=new LinkedList<NeuralArchitecture>();
		for(NeuralArchitecture architecture:savedConfig.getNeuralArchitectures()){
			architecture.getNeuralNetworks().size();
			architectures.add(architecture.copy());
		}
		
		
		em.flush();
		
		return architectures;
	}


	@Override
	public List<NeuralArchitecture> updateNeuralArchitecture(
			NeuralConfiguration configuration) {
		NeuralConfiguration savedConfig = em.find(NeuralConfiguration.class,
				configuration.getId());

		// Tries to find the architectures to remove
		for (NeuralArchitecture saved_architecture : savedConfig
				.getNeuralArchitectures()) {
			boolean inputFound = false;
			for (NeuralArchitecture new_architecture : configuration
					.getNeuralArchitectures()) {
				if (new_architecture.getId() == saved_architecture.getId()) {
					inputFound = true;
					break;
				}
			}

			if (!inputFound) {
				em.remove(saved_architecture);
			}
		}

		savedConfig = em.merge(configuration);
		em.flush();

		return savedConfig.getNeuralArchitectures();
		
		
		
	}
	


	

}
