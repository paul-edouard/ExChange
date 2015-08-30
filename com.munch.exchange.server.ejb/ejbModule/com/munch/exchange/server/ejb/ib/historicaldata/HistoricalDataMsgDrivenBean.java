package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbDayBar;
import com.munch.exchange.model.core.ib.bar.IbHourBar;
import com.munch.exchange.model.core.ib.bar.IbMinuteBar;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;
import com.munch.exchange.server.ejb.ib.Constants;
import com.munch.exchange.server.ejb.ib.historicaldata.HistoricalDataLoaders.BarLoader;

/**
 * Message-Driven Bean implementation class for: HistoricalDataMsgDrivenBean
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty(propertyName = "destination", propertyValue =Constants.JMS_TOPIC_HISTORICAL_DATA),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		}, 
		mappedName = Constants.JMS_TOPIC_HISTORICAL_DATA)
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
				//log.info("Recieved Message from topic: " + msg.getText());
			} else {
				//log.info("Message of wrong type: "
				//		+ message.getClass().getName());
				return;
			}
			
			ut.begin();
			
			// Init the His. Data Loaders
			long time = msg
					.getLongProperty(HistoricalDataTimerBean.TIME_STRING);
			List<IbContract> allContracts = em.createNamedQuery(
					"IbContract.getAll", IbContract.class).getResultList();
			List<IbBarContainer> allBars = new LinkedList<IbBarContainer>();

			for (IbContract exContract : allContracts) {
				allBars.addAll(getBarsFrom(exContract,em));
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
    
    
    private void loadLastBars(BarLoader loader){
    	log.info("Start of loading the historical data of "+
    				loader.getBars().getContract().getLongName()+", What to show: "+loader.getBars().getType());
    	//--------------------------------
    	//- 1. Search the last day bars  -
    	//--------------------------------
    	List<IbBar> newDayBars=loadTheDayBars(loader);
    	
    	//----------------------------------
    	//- 2. Search the last hour bars -
    	//----------------------------------
    	List<IbBar> newHourBars=loadNewBars(	loader,
    											BarSize._1_hour,
    											IbBar.getIntervallInMs(BarSize._1_hour),
    											IbHourBar.class,
    											30,
    											IbBar.getIntervallInMs(BarSize._1_day),
    											IbDayBar.class,
    											newDayBars);
    	
    	//----------------------------------
    	//- 3. Search the last minute bars -
    	//----------------------------------
    	
    	List<IbBar> newMinuteBars= loadNewBars(	loader,
    											BarSize._1_min,
    											IbBar.getIntervallInMs(BarSize._1_min),
    											IbMinuteBar.class,
    											20,
    											IbBar.getIntervallInMs(BarSize._1_hour),
    											IbHourBar.class,
    											newHourBars);
    											
    	/*
    	//----------------------------------
    	//- 4. Search the last second bars -
    	//----------------------------------
    	loadNewChildBars(	loader,
    						newMinuteBars,
							ExSecondeBar.class,
							BarSize._1_secs,
							new ExSecondeBar().getIntervall()
						);
    	*/
    	
    }
    
    private List<IbBar>  loadTheDayBars(BarLoader loader){
    	long lastDayBar=searchLastSavedBar(loader.getBars(), IbDayBar.class);
    	log.info("Last day found in DB: "+HistoricalDataLoaders.FORMAT.format( new Date(lastDayBar) ));
    	if(lastDayBar==0){
    		long days_300=300*24*60*60;days_300*=1000;
    		lastDayBar=loader.getTime()-days_300;
    	}
    	//log.info("2. Last day found in DB: "+HistoricalDataLoaders.FORMAT.format( new Date(lastDayBar) ));
    	
    	List<IbBar> newDayBars=new LinkedList<>();
    	if(loader.getTime()-lastDayBar>IbBar.getIntervallInMs(BarSize._1_day)){
    		//Load the Days
    		List<Bar> dayBars=loader.loadBarsFromTo(lastDayBar, loader.getTime(), BarSize._1_day);
    		log.info("Number of new days found: "+dayBars.size());
    		
    		//Create the Ib Day Bars
    		for(Bar bar:dayBars){
    			IbDayBar exDayBar=new IbDayBar(bar);
    			exDayBar.setRootAndParent(loader.getBars(), loader.getBars());
    			newDayBars.add(exDayBar);
    		}
    	}
    	
    	if(!newDayBars.isEmpty()){
    		//Save the bars in the DATABASE
    		saveBars(newDayBars,true);
    	}
    	
    	return newDayBars;
    }
    
    
    private List<IbBar> loadNewBars(BarLoader loader,BarSize barSize, long intervall, Class<? extends IbBar> clazz,
    		long period,long parentIntervall, Class<?> parentClazz, List<IbBar> parentBars ){
    	
    	
    	//Set the last bar found date
    	long lastBar=searchLastSavedBar(loader.getBars(), clazz);
    	//log.info("Last hour found in DB: "+HistoricalDataLoaders.FORMAT.format( new Date(lastBar)));
    	if(lastBar==0){
    		long firstDay=searchFirstSavedBar(loader.getBars(), parentClazz);
    		if(firstDay==0)return null;
    		lastBar=firstDay-parentIntervall;
    	}
    	
    	
    	//Search the new Bars
    	List<IbBar> newBars=new LinkedList<>();
    	if(loader.getTime()-lastBar>intervall && lastBar>0){
    		
    		//Create the search intervals
    		List<Long> intervalls=createIntervalls(lastBar, loader.getTime(),parentIntervall*period);
    		
    		for(int i=0;i<intervalls.size()-1;i++){
    			//Load the bars from the given interval
    			List<Bar> bars=loader.loadBarsFromTo(intervalls.get(i), intervalls.get(i+1), barSize);
        		log.info("Number of "+clazz.getSimpleName() +" found: "+bars.size()
        				+" in the intervall ["+Bar.format(intervalls.get(i))+", "+Bar.format(intervalls.get(i+1))+"]");
        		if(bars.size()==0)break;
        		
    			for(Bar bar : bars){
    				if(bar.time()>lastBar/1000){
						try {
							IbBar exBar = clazz.newInstance();
							exBar.init(bar);
	    					exBar.setRootAndParent(loader.getBars(), loader.getBars());
	    					
	    					newBars.add(exBar);
						} catch (InstantiationException
								| IllegalAccessException e) {
							e.printStackTrace();
						}
    					
    				}
    			}
    		}
    	}
    	
    	//Search bars without parents
    	List<IbBar> bars_withoutParents=searchExBarsWithoutParent(loader.getBars(), clazz);
    	if(bars_withoutParents!=null){
    		searchAndSetParent(loader, bars_withoutParents, parentBars, parentClazz);
    		updateBars(bars_withoutParents);
    	}
    	
    	//Search and set the parents of the new bars
    	searchAndSetParent(loader, newBars, parentBars, parentClazz);
    	//Save the bars in the database
    	saveBars(newBars,true);
    	
    	
    	return newBars;
    }
    
    private List<Long> createIntervalls(long startTime, long endTime, long period){
    	List<Long> intervalls=new LinkedList<>();
    
    	long current=endTime;
    	while(current > startTime){
    		intervalls.add(0,current);
    		current-=period;
    	}
    	
    	intervalls.add(0,startTime);
    	
    	return intervalls;
    }
    
    private void searchAndSetParent(BarLoader loader,List<IbBar> bars,List<IbBar> parentBars, Class<?> parentClazz){
    	for(IbBar bar : bars){
    		IbBar parent=null;
    		for(IbBar parentBar: parentBars){
    			if(bar.getTime()>parentBar.getTime())continue;
    			
    			if(parent==null){parent=parentBar;continue;}
    			
    			if(parent.getTime()>parentBar.getTime())
    				parent=parentBar;
    		}
    		
    		//Search the parent in the database
    		if(parent==null){
    			IbBar new_parent=searchParentOf(bar, loader.getBars(), parentClazz);
    			if(new_parent!=null){
    				parent=new_parent;
    				parentBars.add(parent);
    			}
    		}
    		
    		//Set the parent
    		bar.setParent(parent);
    		
    	}
    }
    
    
    
    //@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
    /*
    private List<IbBar> loadNewChildBars(BarLoader loader, List<IbBar> parentBars,Class<? extends IbBar> childClass, BarSize childBarSize, long childIntervall){
    	
    	
    	//boolean flushEM=false;
    	
    	long lastChildBar=searchLastSavedBar(loader.getBars(), childClass);
    	log.info("Last " +childClass.getSimpleName()+" found in DB: "+HistoricalDataLoaders.FORMAT.format( new Date(lastChildBar) ));
    	
    	//Set the parent of the last hour bars
    	if(!parentBars.isEmpty()){
    		IbBar firstParent=parentBars.get(0);
    		List<IbBar> childBars=searchExBarsFromToDateWithoutParent(loader.getBars(),
    				firstParent.getTimeInMs()-firstParent.getIntervall(), lastChildBar, childClass);
    		log.info(childBars.size()+" bars will be assigned to the parent: "+firstParent.getClass().getSimpleName()+ ": "+
    				HistoricalDataLoaders.FORMAT.format( new Date(firstParent.getTime()) ));
    		for(IbBar h_bar : childBars){
    			h_bar.setParent(firstParent);
    			firstParent.getChildBars().add(h_bar);
    			//em.merge(h_bar);
    			//flushEM=true;
    		}
    		//em.flush();
    		updateBars(childBars);
    	}
    	
    	//Add the new hour bar for each new days
    	List<IbBar> newChildBars=new LinkedList<>();
    	//List<ExBar> reloadedParent=new LinkedList<>();
    	
    	
    	
    	for(IbBar parent_bar:parentBars){
    		if(lastChildBar>parent_bar.getTime())continue;
    		
    		//try {
    		//ut.begin();
    		
    		//ExBar reloadedParentBar=em.find(parent_bar.getClass(), parent_bar.getId());
    		//reloadedParent.add(reloadedParentBar);
    		
    		List<Bar> childBars=loader.loadBarsFromTo(parent_bar.getTimeInMs()-parent_bar.getIntervall(), parent_bar.getTimeInMs(), childBarSize);
    		log.info(childBars.size()+" new bars were found and will be assigned to the parent: "+parent_bar.getClass().getSimpleName()+ ": "+
    				HistoricalDataLoaders.FORMAT.format( new Date(parent_bar.getTime()) ));
    		List<IbBar> localChildBars=new LinkedList<>();
    		
    		for(Bar childBar : childBars){
    			try {
					IbBar exChildBar = childClass.newInstance();
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
    			for(IbBar exHourBar:localChildBars){
    				List<Bar> minuteBars=loader.loadBarsFromTo(exHourBar.getTimeInMs()-exHourBar.getIntervall(), exHourBar.getTimeInMs(), BarSize._1_min);
    				List<IbBar> exMinuteBars=new LinkedList<>();
    				for(Bar minuteBar : minuteBars){
    					IbBar exMinuteBar = new IbMinuteBar(minuteBar);
    					exMinuteBar.setRootAndParent(loader.getBars(), exHourBar);
    					exMinuteBars.add(exMinuteBar);
    				}
    				for(IbBar exMinuteBar:exMinuteBars){
    					List<Bar> secondeBars=loader.loadBarsFromTo(exMinuteBar.getTimeInMs()-exMinuteBar.getIntervall(), exMinuteBar.getTimeInMs(), BarSize._1_secs);
    					
    					for(Bar secondeBar : secondeBars){
        					IbBar exSecondeBar = new IbSecondeBar(secondeBar);
        					exSecondeBar.setRootAndParent(loader.getBars(), exMinuteBar);
        				}
    				}
    			}
    		}
    		else if(childBarSize==BarSize._1_min){
    			for(IbBar exMinuteBar:localChildBars){
					List<Bar> secondeBars=loader.loadBarsFromTo(exMinuteBar.getTimeInMs()-exMinuteBar.getIntervall(), exMinuteBar.getTimeInMs(), BarSize._1_secs);
					
					for(Bar secondeBar : secondeBars){
    					IbBar exSecondeBar = new IbSecondeBar(secondeBar);
    					exSecondeBar.setRootAndParent(loader.getBars(), exMinuteBar);
    				}
				}
    		}
    		
    		
    		
    		//em.merge(arg0)
    		//em.persist(reloadedParentBar);
    		saveBars(localChildBars,true);
        	
    		
    		//parentBuffered.clear();
    		//reloadedParent.clear();
    		
    	}
    	
    	
    	//Search the last
    	if(loader.getTime()-lastChildBar>childIntervall && lastChildBar>0){
    		List<Bar> childBars=loader.loadLastBars(lastChildBar, childBarSize);
    		log.info(childBars.size()+" new bars were found without any parent");
    		List<IbBar> localChildBars=new LinkedList<>();
    		for(Bar bar:childBars){
    			try {
    				
					IbBar exChildBar = childClass.newInstance();
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
    */
    
    
    
    
    private void saveBars(List<IbBar> bars,boolean withTransation){
    	
    	if(withTransation){
    	try {
			ut.begin();
			
			//Clean the bar list from double bars
			HashSet<Long> timeSet=new HashSet<>();
			LinkedList<IbBar> toDel=new LinkedList<>();
			for(IbBar bar : bars){
				if(!timeSet.contains(bar.getTime())){
					timeSet.add(bar.getTime());
				}
				else{
					toDel.add(bar);
				}
			}
			for(IbBar bar : toDel){
				bars.remove(bar);
			}
			
			//Save each bars
			for(IbBar bar : bars){
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
    		for(IbBar bar : bars){
				em.persist(bar);
			}
    	}
    	
    }
    
    private void updateBars(List<IbBar> bars){
    	try {
			ut.begin();
			for(IbBar bar : bars){
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
    
    public static List<IbBarContainer> getBarsFrom(IbContract exContract,EntityManager em){
    	List<IbBarContainer> Allbars=exContract.getBars();
    	if(Allbars==null || Allbars.isEmpty()){
    		Allbars=new LinkedList<IbBarContainer>();
    		
    		//STOCK
    		if(exContract.getSecType()==SecType.STK ){
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.MIDPOINT));
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.ASK));
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.BID));
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.TRADES));
    			
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.HISTORICAL_VOLATILITY));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.OPTION_IMPLIED_VOLATILITY));
    		}
    		else if(exContract.getSecType()==SecType.CASH || 
    				exContract.getSecType()==SecType.CMDTY){
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.MIDPOINT));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.ASK));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.BID));
    		}
    		else if(exContract.getSecType()==SecType.OPT ||
    				exContract.getSecType()==SecType.FUT){
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.MIDPOINT));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.ASK));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.BID));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.TRADES));
    		}
    		//INDICE
    		else if(exContract.getSecType()==SecType.IND){
    			Allbars.add(new IbBarContainer(exContract,WhatToShow.TRADES));
    			
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.HISTORICAL_VOLATILITY));
    			//Allbars.add(new IbBarContainer(exContract,WhatToShow.OPTION_IMPLIED_VOLATILITY));
    		}
    		
    		
    		for(IbBarContainer bars:Allbars){
    			em.persist(bars);
    		}
    		em.flush();
    		
    		exContract.setBars(Allbars);
    		
    	}
    	
    	
    	
    	//List<IbBarContainer> Allbars
    	//for(IbBarContainer container:)
    	
    	return Allbars;
    }
    
    private long searchLastSavedBar(IbBarContainer exContractBars, Class<?> clazz){
    	Query query=em.createQuery("SELECT MAX(b.time)" +
				"FROM "+clazz.getSimpleName()+" b WHERE b.root="+exContractBars.getId());

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)
    		return 0;

    	//log.info("Result: "+singleResult.toString());
    	long time=(long) singleResult;
    	return time*1000;
    }
    
    private long searchFirstSavedBar(IbBarContainer exContractBars, Class<?> clazz){
    	Query query=em.createQuery("SELECT MIN(b.time)" +
				"FROM "+clazz.getSimpleName()+" b WHERE b.root="+exContractBars.getId());

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)
    		return 0;

    	log.info("Result: "+singleResult.toString());
    	long time=(long) singleResult;
    	return time*1000;
    }
    
    private IbBar searchParentOf(IbBar child, IbBarContainer exContractBars,Class<?> parentClazz){
    	
    	TypedQuery<IbBar> query=em.createQuery("SELECT b " +
				"FROM "+parentClazz.getSimpleName()+" b "+
    			"WHERE b.root="+exContractBars.getId()+" "+
				"AND b.time > "+child.getTime(),IbBar.class);
    	
    	List<IbBar> bars=query.getResultList();
    	if(bars==null || bars.isEmpty())return null;
    	IbBar parent=bars.get(0);
    	for(IbBar bar:bars){
    		if(bar.getTime()<parent.getTime()){
    			parent=bar;
    		}
    	}
    	
    	return parent;
    	
    }
    

    
    private List<IbBar> searchExBarsFromToDateWithoutParent(IbBarContainer exContractBars,long from, long to, Class<?> clazz){
    	
    	TypedQuery<IbBar> query=em.createQuery("SELECT b " +
				"FROM "+clazz.getSimpleName()+" b "+
    			"WHERE b.root="+exContractBars.getId()+" "+
				"AND b.time > "+from+" AND b.time <= "+to+" AND b.parent IS NULL",IbBar.class);
    	
    	List<IbBar> bars=query.getResultList();
    	
    	return bars;
    }
    
    private List<IbBar> searchExBarsWithoutParent(IbBarContainer exContractBars, Class<?> clazz){
    	
    	TypedQuery<IbBar> query=em.createQuery("SELECT b " +
				"FROM "+clazz.getSimpleName()+" b "+
    			"WHERE b.root="+exContractBars.getId()+" "+
				"AND b.parent IS NULL",IbBar.class);
    	
    	List<IbBar> bars=query.getResultList();
    	
    	return bars;
    }
    
    
    
  
    
    

}
