package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.SecType;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar_;
import com.munch.exchange.model.core.ib.bar.ExContractBars;
import com.munch.exchange.model.core.ib.bar.ExDayBar;
import com.munch.exchange.model.core.ib.bar.ExHourBar;
import com.munch.exchange.model.core.ib.bar.ExMinuteBar;
import com.munch.exchange.model.core.ib.bar.ExSecondeBar;
import com.munch.exchange.server.ejb.ib.historicaldata.HistoricalDataLoaders.BarLoader;

/**
 * Message-Driven Bean implementation class for: HistoricalDataMsgDrivenBean
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/topic/HistoricalData"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		}, 
		mappedName = "java:/jms/topic/HistoricalData")
@TransactionManagement(value=TransactionManagementType.BEAN)
public class HistoricalDataMsgDrivenBean implements MessageListener {
	
	
	private static final Logger log = Logger.getLogger(HistoricalDataMsgDrivenBean.class.getName());
	
	
	@PersistenceContext
	private EntityManager em;
	
	@Resource
	private UserTransaction ut;
	

    /**
     * Default constructor. 
     */
    public HistoricalDataMsgDrivenBean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
    	TextMessage msg = null;
		try {
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
				log.info("Recieved Message from topic: " + msg.getText());
			} else {
				log.info("Message of wrong type: "
						+ message.getClass().getName());
				return;
			}
			/*
			if(HistoricalDataLoaders.INSTANCE.isLoading()){
				log.info("Sorry the historical data are still loading!");
				return;
			}
			*/
			
			ut.begin();
			
			//HistoricalDataLoaders.INSTANCE.setLoading(true);
			// Init the His. Data Loaders
			long time = msg
					.getLongProperty(HistoricalDataTimerBean.TIME_STRING);
			List<ExContract> allContracts = em.createNamedQuery(
					"ExContract.getAll", ExContract.class).getResultList();
			List<ExContractBars> allBars = new LinkedList<ExContractBars>();

			for (ExContract exContract : allContracts) {
				allBars.addAll(getBarsFrom(exContract));
			}

			HistoricalDataLoaders.INSTANCE.init(allBars, time);
			ut.commit();
			// Start the loading of the data for each loaders
			for (BarLoader loader : HistoricalDataLoaders.INSTANCE.getLoaders()) {
				if(loader.isLoading())continue;
				loader.setLoading(true);
				loadLastBars(loader);
				loader.setLoading(false);
			}
			
			//HistoricalDataLoaders.INSTANCE.setLoading(false);
			
			
		} catch (JMSException | NotSupportedException | SystemException | SecurityException | 
				IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			HistoricalDataLoaders.INSTANCE.setLoading(false);
			log.warning(e.toString());
			
			try {
				ut.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			throw new RuntimeException(e);
		}
         
    }
    
    
   // @TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
   // @Asynchronous
    private void loadLastBars(BarLoader loader){
    	log.info("Start of loading the historical data of "+
    				loader.getBars().getContract().getLongName()+", What to show: "+loader.getBars().getType());
    	//--------------------------------
    	//- 1. Search the last day bars  -
    	//--------------------------------
    	long lastDayBar=searchLastSavedBar(loader.getBars(), ExDayBar.class);
    	log.info("Last day found in DB: "+HistoricalDataLoaders.FORMAT.format( new Date(lastDayBar) ));
    	List<ExBar> newDayBars=new LinkedList<>();
    	if(loader.getTime()-lastDayBar>new ExDayBar().getIntervall()){
    		List<Bar> dayBars=loader.loadLastBars(lastDayBar, BarSize._1_day);
    		log.info("Number of new days found: "+dayBars.size());
    		for(Bar bar:dayBars){
    			ExDayBar exDayBar=new ExDayBar(bar);
    			exDayBar.setRootAndParent(loader.getBars(), loader.getBars());
    			newDayBars.add(exDayBar);
    		}
    	}
    	
    	//log.info("Now persist the day bars: "+newDayBars.size());
    	if(!newDayBars.isEmpty()){
    		saveBars(newDayBars,true);
    		//for(ExBar bar:newDayBars)		{em.persist(bar);}
    		//em.flush();
    	}
    	
    	//----------------------------------
    	//- 2. Search the last hour bars -
    	//----------------------------------
    	List<ExBar> newHourBars= loadNewChildBars(	loader,
    												newDayBars,
    												ExHourBar.class,
    												BarSize._1_hour,
    												new ExHourBar().getIntervall()
    												);
    	
    	//----------------------------------
    	//- 3. Search the last minute bars -
    	//----------------------------------
    	List<ExBar> newMinuteBars= loadNewChildBars(	loader,
    													newHourBars,
														ExMinuteBar.class,
														BarSize._1_min,
														new ExMinuteBar().getIntervall()
    													);
    	
    	//----------------------------------
    	//- 4. Search the last second bars -
    	//----------------------------------
    	loadNewChildBars(	loader,
    						newMinuteBars,
							ExSecondeBar.class,
							BarSize._1_secs,
							new ExSecondeBar().getIntervall()
						);
    	
    	
    }
    
    //@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
    private List<ExBar> loadNewChildBars(BarLoader loader, List<ExBar> parentBars,Class<? extends ExBar> childClass, BarSize childBarSize, long childIntervall){
    	
    	
    	//boolean flushEM=false;
    	
    	long lastChildBar=searchLastSavedBar(loader.getBars(), childClass);
    	log.info("Last " +childClass.getSimpleName()+" found in DB: "+HistoricalDataLoaders.FORMAT.format( new Date(lastChildBar) ));
    	
    	//Set the parent of the last hour bars
    	if(!parentBars.isEmpty()){
    		ExBar firstParent=parentBars.get(0);
    		List<ExBar> childBars=searchExBarsFromToDateWithoutParent(loader.getBars(),
    				firstParent.getTime()-firstParent.getIntervall(), lastChildBar, childClass);
    		log.info(childBars.size()+" bars will be assigned to the parent: "+firstParent.getClass().getSimpleName()+ ": "+
    				HistoricalDataLoaders.FORMAT.format( new Date(firstParent.getTime()) ));
    		for(ExBar h_bar : childBars){
    			h_bar.setParent(firstParent);
    			firstParent.getChildBars().add(h_bar);
    			//em.merge(h_bar);
    			//flushEM=true;
    		}
    		//em.flush();
    		updateBars(childBars);
    	}
    	
    	//Add the new hour bar for each new days
    	List<ExBar> newChildBars=new LinkedList<>();
    	//List<ExBar> reloadedParent=new LinkedList<>();
    	
    	
    	
    	for(ExBar parent_bar:parentBars){
    		if(lastChildBar>parent_bar.getTime())continue;
    		
    		//try {
    		//ut.begin();
    		
    		//ExBar reloadedParentBar=em.find(parent_bar.getClass(), parent_bar.getId());
    		//reloadedParent.add(reloadedParentBar);
    		
    		List<Bar> childBars=loader.loadBarsFromTo(parent_bar.getTime()-parent_bar.getIntervall(), parent_bar.getTime(), childBarSize);
    		log.info(childBars.size()+" new bars were found and will be assigned to the parent: "+parent_bar.getClass().getSimpleName()+ ": "+
    				HistoricalDataLoaders.FORMAT.format( new Date(parent_bar.getTime()) ));
    		List<ExBar> localChildBars=new LinkedList<>();
    		
    		for(Bar childBar : childBars){
    			try {
					ExBar exChildBar = childClass.newInstance();
					exChildBar.init(childBar);
					exChildBar.setRootAndParent(loader.getBars(), parent_bar);
	    			
	    			lastChildBar=exChildBar.getTime();
	    			newChildBars.add(exChildBar);
	    			localChildBars.add(exChildBar);
    			} catch (InstantiationException e) {
    				e.printStackTrace();
    			} catch (IllegalAccessException e) {
    				e.printStackTrace();
    			}
				
				
    		}
    		//If new full hour found then load until the seconds
    		if(childBarSize==BarSize._1_hour){
    			for(ExBar exHourBar:localChildBars){
    				List<Bar> minuteBars=loader.loadBarsFromTo(exHourBar.getTime()-exHourBar.getIntervall(), exHourBar.getTime(), BarSize._1_min);
    				List<ExBar> exMinuteBars=new LinkedList<>();
    				for(Bar minuteBar : minuteBars){
    					ExBar exMinuteBar = new ExMinuteBar(minuteBar);
    					exMinuteBar.setRootAndParent(loader.getBars(), exHourBar);
    					exMinuteBars.add(exMinuteBar);
    				}
    				for(ExBar exMinuteBar:exMinuteBars){
    					List<Bar> secondeBars=loader.loadBarsFromTo(exMinuteBar.getTime()-exMinuteBar.getIntervall(), exMinuteBar.getTime(), BarSize._1_secs);
    					
    					for(Bar secondeBar : secondeBars){
        					ExBar exSecondeBar = new ExSecondeBar(secondeBar);
        					exSecondeBar.setRootAndParent(loader.getBars(), exMinuteBar);
        				}
    				}
    			}
    		}
    		else if(childBarSize==BarSize._1_min){
    			for(ExBar exMinuteBar:localChildBars){
					List<Bar> secondeBars=loader.loadBarsFromTo(exMinuteBar.getTime()-exMinuteBar.getIntervall(), exMinuteBar.getTime(), BarSize._1_secs);
					
					for(Bar secondeBar : secondeBars){
    					ExBar exSecondeBar = new ExSecondeBar(secondeBar);
    					exSecondeBar.setRootAndParent(loader.getBars(), exMinuteBar);
    				}
				}
    		}
    		
    		
    		
    		//em.merge(arg0)
    		//em.persist(reloadedParentBar);
    		saveBars(localChildBars,true);
        	/*
    		ut.commit();
    		} catch (NotSupportedException | SystemException | SecurityException |
    				IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
    			log.warning(e.toString());
    			e.printStackTrace();
    			try {
    				ut.rollback();
    			} catch (IllegalStateException | SecurityException
    					| SystemException e1) {
    				log.warning(e1.toString());
    				e1.printStackTrace();
    			}
    			
    		}
    		*/
    		
    		//parentBuffered.clear();
    		//reloadedParent.clear();
    		
    	}
    	
    	
    	//Search the last
    	if(loader.getTime()-lastChildBar>childIntervall){
    		List<Bar> childBars=loader.loadLastBars(lastChildBar, childBarSize);
    		log.info(childBars.size()+" new bars were found without any parent");
    		List<ExBar> localChildBars=new LinkedList<>();
    		for(Bar bar:childBars){
    			try {
    				
					ExBar exChildBar = childClass.newInstance();
					exChildBar.init(bar);
					exChildBar.setRoot(loader.getBars());
					//exChildBar.setRootAndParent(loader.getBars(), loader.getBars());
					
					newChildBars.add(exChildBar);
					localChildBars.add(exChildBar);
					//em.persist(exChildBar);
					//flushEM=true;
					
    			} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
    			
    			//em.flush();
    		}
    		saveBars(localChildBars,true);
    	}
    	
    	//if(flushEM)em.flush();
    	
    	return newChildBars;
    }
    
    private void saveBars(List<ExBar> bars,boolean withTransation){
    	
    	if(withTransation){
    	try {
			ut.begin();
			for(ExBar bar : bars){
				em.persist(bar);
			}
			ut.commit();
		} catch (NotSupportedException | SystemException | SecurityException |
				IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			log.warning(e.toString());
			e.printStackTrace();
			try {
				ut.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				log.warning(e1.toString());
				e1.printStackTrace();
			}
			
		}
    	}
    	else{
    		for(ExBar bar : bars){
				em.persist(bar);
			}
    	}
    	
    }
    
    private void updateBars(List<ExBar> bars){
    	try {
			ut.begin();
			for(ExBar bar : bars){
				em.merge(bar);
			}
			ut.commit();
		} catch (NotSupportedException | SystemException | SecurityException |
				IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			log.warning(e.toString());
			e.printStackTrace();
			try {
				ut.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				log.warning(e1.toString());
				e1.printStackTrace();
			}
			
		}
    	
    }
    
    private List<ExContractBars> getBarsFrom(ExContract exContract){
    	List<ExContractBars> Allbars=exContract.getBars();
    	if(Allbars==null || Allbars.isEmpty()){
    		Allbars=new LinkedList<ExContractBars>();
    		
    		//STOCK
    		if(exContract.getSecType()==SecType.STK){
    			Allbars.add(new ExContractBars(exContract,WhatToShow.MIDPOINT));
    			Allbars.add(new ExContractBars(exContract,WhatToShow.ASK));
    		}
    		//INDICE
    		else if(exContract.getSecType()==SecType.IND){
    			Allbars.add(new ExContractBars(exContract,WhatToShow.MIDPOINT));
    		}
    		
    		for(ExContractBars bars:Allbars){
    			em.persist(bars);
    		}
    		em.flush();
    	}
    	
    	return Allbars;
    }
    
    /*
    private long searchLastSavedSecondeBar(ExContractBars exContractBars){
    	return searchLastSavedBar(exContractBars,ExSecondeBar.class);
    }
    */
    
    private long searchLastSavedBar(ExContractBars exContractBars, Class<?> clazz){
    	Query query=em.createQuery("SELECT MAX(b.time)" +
				"FROM "+clazz.getSimpleName()+" b WHERE b.root="+exContractBars.getId());

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)
    		return 0;

    	log.info("Result: "+singleResult.toString());
    	return (long) singleResult;
    }
    
    /*
    private List<ExBar> searchExBarsFromToDate(ExContractBars exContractBars,long from, long to, Class<?> clazz){
    	
    	TypedQuery<ExBar> query=em.createQuery("SELECT b " +
				"FROM "+clazz.getSimpleName()+" b "+
    			"WHERE b.root="+exContractBars.getId()+" "+
				"AND b.time > "+from+" AND b.time <= "+to,ExBar.class);
    	
    	List<ExBar> bars=query.getResultList();
    	
    	return bars;
    }
    */
    
    private List<ExBar> searchExBarsFromToDateWithoutParent(ExContractBars exContractBars,long from, long to, Class<?> clazz){
    	
    	TypedQuery<ExBar> query=em.createQuery("SELECT b " +
				"FROM "+clazz.getSimpleName()+" b "+
    			"WHERE b.root="+exContractBars.getId()+" "+
				"AND b.time > "+from+" AND b.time <= "+to+" AND b.parent IS NULL",ExBar.class);
    	
    	List<ExBar> bars=query.getResultList();
    	
    	return bars;
    }
    
    
    
    /*
	private ExBar getLastSavedBar(ExContractBars exContractBars, BarSize barSize) {
		// TODO Auto-generated method stub
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExContractBars> cq=cb.createQuery(ExContractBars.class);
		Root<ExContractBars> c=cq.from(ExContractBars.class);
		cq.select(c);
		
		TypedQuery<ExContractBars> typedQuery = em.createQuery(cq);
		List<ExContractBars> bars=typedQuery.getResultList();
		
		for(ExBar bar : bars)
			log.info(bar.toString());
		
		//if(bars.size()>0)
		//	return bars.get(0);
		
		return null;
	}
	*/
    
    
    

}
