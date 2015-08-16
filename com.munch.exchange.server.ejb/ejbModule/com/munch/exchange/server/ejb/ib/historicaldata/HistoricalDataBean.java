package com.munch.exchange.server.ejb.ib.historicaldata;


import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;



import javax.persistence.TypedQuery;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
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
	public List<IbBarContainer> getAllExContractBars(IbContract exContract) {
		
		//log.info("getAllExContractBars server called!");
		
		IbContract ex_contract=em.find(IbContract.class, exContract.getId());
		ex_contract.getBars().size();
		
		List<IbBarContainer> contractBars= ex_contract.getBars();
		
		return contractBars;
		}


	@Override
	public IbBar getFirstBar(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		long time=getFirstBarTime(exContractBars, exBarClass);
    	if(time==0)return null;
    	
		return searchBarOfTime(exContractBars, exBarClass, time);
	}
	
	@Override
	public long getFirstBarTime(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		Query query=em.createQuery("SELECT MIN(b.time)" +
				"FROM "+exBarClass.getSimpleName()+" b WHERE b.root="+exContractBars.getId());

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
    	//log.info("Max Time: "+time);
    	
    	return time;
	}
	


	@Override
	public IbBar getLastBar(IbBarContainer exContractBars,
		Class<? extends IbBar> exBarClass) {
		
    	long time=getLastBarTime(exContractBars, exBarClass);
    	if(time==0)return null;
    	
		return searchBarOfTime(exContractBars, exBarClass, time);
		
	}
	
	@Override
	public long getLastBarTime(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		Query query=em.createQuery("SELECT MAX(b.time)" +
				"FROM "+exBarClass.getSimpleName()+" b WHERE b.root="+exContractBars.getId());

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
    	//log.info("Max Time: "+time);
    	
    	return time;
	}


	@Override
	public List<IbBar> getAllBars(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		TypedQuery<IbBar> query=em.createQuery("SELECT b " +
				"FROM "+exBarClass.getSimpleName()+" b "+
    			"WHERE b.root="+exContractBars.getId(),IbBar.class);
    	
    	List<IbBar> bars=query.getResultList();
    	List<IbBar> copies=new LinkedList<IbBar>();
    	for(IbBar bar:bars){
    		IbBar copy;
			try {
				copy = exBarClass.newInstance();
				copy.copyData(bar);
	    		copies.add(copy);
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	}
    	
    	return copies;
	}


	@Override
	public IbBar searchBarOfTime(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass, long time) {
		Query query=em.createQuery("SELECT b " +
				"FROM "+exBarClass.getSimpleName()+" b WHERE b.time="+time+" AND b.root="+exContractBars.getId());
    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)return null;
		
    	IbBar bar=(IbBar) singleResult;
		
		try {
			IbBar copy = exBarClass.newInstance();
			copy.copyData(bar);
			return copy;
		} catch (InstantiationException | IllegalAccessException e) {
			log.warning(e.toString());
		}
		
		return null;
	}


	


	


}
