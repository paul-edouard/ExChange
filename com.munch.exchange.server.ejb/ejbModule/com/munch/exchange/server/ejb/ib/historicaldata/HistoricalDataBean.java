package com.munch.exchange.server.ejb.ib.historicaldata;


import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExContractBars;
import com.munch.exchange.services.ejb.interfaces.HistoricalDataBeanRemote;

/**
 * Session Bean implementation class HistoricalDataBean
 */
@Stateless
@LocalBean
//@TransactionManagement(value=TransactionManagementType.BEAN)
public class HistoricalDataBean implements HistoricalDataBeanRemote{
	
	private static final Logger log = Logger.getLogger(HistoricalDataBean.class.getName());
	
	@PersistenceContext
	private EntityManager em;
	
	//@Resource
	//private UserTransaction ut;
	
	
    /**
     * Default constructor. 
     */
     public HistoricalDataBean() {
    }


	@Override
	public List<ExContractBars> getAllExContractBars(ExContract exContract) {
		
		//log.info("getAllExContractBars server called!");
		
		ExContract ex_contract=em.find(ExContract.class, exContract.getId());
		ex_contract.getBars().size();
		
		List<ExContractBars> contractBars= ex_contract.getBars();
		
		return contractBars;
		}


	@Override
	public ExBar getFirstBar(ExContractBars exContractBars,
			Class<? extends ExBar> exBarClass) {
		Query query=em.createQuery("SELECT MIN(b.time),b.id " +
				"FROM "+exBarClass.getSimpleName()+" b WHERE b.root="+exContractBars.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> rows=query.getResultList();
		if(rows.size()!=1)return null;
		
		long min_id=(long) rows.get(0)[1];
		ExBar bar=em.find(exBarClass, min_id);
		
		try {
			ExBar copy = exBarClass.newInstance();
			copy.copyData(bar);
			return copy;
		} catch (InstantiationException | IllegalAccessException e) {
			log.warning(e.toString());
		}
		
		return null;
	}


	@Override
	public ExBar getLastBar(ExContractBars exContractBars,
		Class<? extends ExBar> exBarClass) {
		
		Query query=em.createQuery("SELECT MAX(b.time),b.id " +
				"FROM "+exBarClass.getSimpleName()+" b WHERE b.root="+exContractBars.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> rows=query.getResultList();
		if(rows.size()!=1)return null;
		
		long max_id=(long) rows.get(0)[1];
		ExBar bar=em.find(exBarClass, max_id);
		
		try {
			ExBar copy = exBarClass.newInstance();
			copy.copyData(bar);
			return copy;
		} catch (InstantiationException | IllegalAccessException e) {
			log.warning(e.toString());
		}
		
		return null;
		
	}


}
