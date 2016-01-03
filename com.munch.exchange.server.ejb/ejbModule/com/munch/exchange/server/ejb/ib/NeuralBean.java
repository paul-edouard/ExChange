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
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
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
//				em.remove(conf);
			}
		}
		//Save the chart signal
		em.flush();
	}


	@Override
	public void updateNeuralConfiguration(NeuralConfiguration configuration) {
		em.merge(configuration);
		em.flush();
	}


	

}
