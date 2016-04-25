package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.ib.controller.Bar;
import com.munch.exchange.model.core.ib.bar.BarContainerInterface;
import com.munch.exchange.model.core.ib.bar.BarConversionInterface;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.minute.MinuteAskBar;
import com.munch.exchange.model.core.ib.bar.minute.MinuteBidBar;
import com.munch.exchange.model.core.ib.bar.minute.MinuteMidPointBar;
import com.munch.exchange.model.core.ib.bar.minute.MinuteTradesBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeAskBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeBidBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeMidPointBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeTradesBar;

public class HistoricalBarPersistance {
	
	private static final Logger log = Logger.getLogger(HistoricalBarPersistance.class.getName());
	
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query

	
	public static void saveBars(EntityManager em, IbBarContainer container, BarType barType, List<Bar> bars){
//		log.info("Start the persistance of "+barType.toString()+ " "+bars.size()+" bars");
		Calendar start=Calendar.getInstance();
		
		Class<?> tableClass=getTableClass(container,barType);
		try {

			int i = 0;

			for (Bar bar : bars) {

				BarConversionInterface ibBar = (BarConversionInterface) tableClass.newInstance();
				ibBar.init(bar);
				ibBar.attachToContainer(getContainer(em, container, barType));
				em.persist(ibBar);

				if ((i % 10000) == 0) {
					
					 em.flush();
			         em.clear();
				}

				i++;

			}
			
			em.flush();
	        em.clear();

		} catch (InstantiationException | IllegalAccessException e) {
			log.warning("Error by persisiting the bars "+barType.toString()+ " "+bars.size()+" bars");
			e.printStackTrace();
		}
		
//		Calendar end=Calendar.getInstance();
//		long time_s=(end.getTimeInMillis()-start.getTimeInMillis());
//		log.info("Time needed to persit "+bars.size()+" bars: "+time_s+ " ms");
		
	}
	
	
	public static void removeBarsFromTo(EntityManager em, IbBarContainer container,
			BarType barType , long from, long to) {
		
		Query query=em.createQuery("DELETE " +
				"FROM "+getTableName(container,barType)+" b "+
    			"WHERE b.container="+getContainerId(em, container, barType)+" "+
				"AND b.time>="+from+ " "+
    			"AND b.time<="+to);
    	
		query.executeUpdate();
		
	}
	
	public static long getFirstBarTime(EntityManager em,
			IbBarContainer container, BarType barType ){
		
//		log.info("SELECT MIN(b.time)" +
//					"FROM "+getTableName(container,barType)+
//					" b WHERE b.container="+getContainerId(em, container, barType));
//		
		Query query=em.createQuery("SELECT MIN(b.time)" +
					"FROM "+getTableName(container,barType)+
					" b WHERE b.containerId="+getContainerId(em, container, barType)
					);

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
		
    	return time;
	}
		
	public static long getLastBarTime(EntityManager em,
			IbBarContainer container, BarType barType ){
		Query query=em.createQuery("SELECT MAX(b.time)" +
					"FROM "+getTableName(container,barType)+
					" b WHERE b.containerId="+getContainerId(em, container, barType)
					);

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
		
    	return time;
	}
	
	public static List<IbBar> getAllBars(EntityManager em,
			IbBarContainer container, BarType barType ){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,barType)+" b "+
    			"WHERE b.containerId="+getContainerId(em, container, barType);
		
		return getIbBarListFromQuery(em, queryString);
	}
	
	public static List<IbBar> getBarsFromTo(EntityManager em,
			IbBarContainer container, BarType barType , long from, long to){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,barType)+" b "+
				"WHERE b.containerId="+getContainerId(em, container, barType)+" "+
				"AND b.time>="+from+ " "+
				"AND b.time<="+to;
		
		return getIbBarListFromQuery(em, queryString);
	}
	
	public static boolean containsBar(EntityManager em,
			IbBarContainer container, BarType barType , long time){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,barType)+" b "+
				"WHERE b.containerId="+getContainerId(em, container, barType)+" "+
				"AND b.time="+time;
		
		List<IbBar> bars=getIbBarListFromQuery(em, queryString);
		
		return bars.size()==1;
	}
	
	
	
	public static long getLastShortTermBarTime(EntityManager em, IbBarContainer container, BarType barType){
		BarContainerInterface containerIt=getContainer(em, container, barType);
		if(containerIt==null)return 0;
		
		switch (container.getType()) {
		case ASK:
			return containerIt.getLastShortTermAskBarTime();
		case MIDPOINT:
			return containerIt.getLastShortTermMidPointBarTime();
		case TRADES:
			return containerIt.getLastShortTermTradesBarTime();
		case BID:
			return containerIt.getLastShortTermBidBarTime();
		default:
			return 0;
		}
		
	}
	
	public static void setLastShortTermBarTime(EntityManager em, IbBarContainer container, BarType barType, long time){
		BarContainerInterface containerIt=getContainer(em, container, barType);
		if(containerIt==null)return ;
		
		BarContainerInterface foundContainer=em.find(containerIt.getClass(), containerIt.getId());
		
		switch (container.getType()) {
		case ASK:
			foundContainer.setLastShortTermAskBarTime(time);
			break;
		case MIDPOINT:
			foundContainer.setLastShortTermMidPointBarTime(time);
			break;
		case TRADES:
			foundContainer.setLastShortTermTradesBarTime(time);
			break;
		case BID:
			foundContainer.setLastShortTermBidBarTime(time);
			break;
		default:
			break ;
		}
		
		em.persist(foundContainer);
		em.flush();
		
		
	}
	
	
	
	
	private static List<IbBar> getIbBarListFromQuery(EntityManager em, String queryString){
		
		TypedQuery<BarConversionInterface> query=em.createQuery(queryString,BarConversionInterface.class);
		List<BarConversionInterface> bars=query.getResultList();
		
//		Convert the bar to IbBars
		List<IbBar> ibBars=new LinkedList<IbBar>();
		for(BarConversionInterface bar:bars){
			ibBars.add(bar.toIbBar());
		}
		
		return ibBars;
		
	}
	
	
	private static long getContainerId(EntityManager em, IbBarContainer container, BarType barType){
		BarContainerInterface containerIt=getContainer(em, container, barType);
		if(containerIt!=null)return containerIt.getId();
		
		return 0;
	}
	
	private static BarContainerInterface getContainer(EntityManager em, IbBarContainer container, BarType barType){
		switch (barType) {
		case SECOND:
			if(container.getContract().getSecondeContainer().getId()==0){
				em.persist(container.getContract().getSecondeContainer());
			}
			return container.getContract().getSecondeContainer();
		case MINUTE:
			if(container.getContract().getMinuteContainer().getId()==0){
				em.persist(container.getContract().getMinuteContainer());
			}
			return container.getContract().getMinuteContainer();
		case DAY:
			return null;
		default:
			return null;
		}
		
	}
	
	private static String getTableName(IbBarContainer container, BarType barType){
		Class<?> tableClass=getTableClass(container,barType);
		if(tableClass==null)return "";
		return tableClass.getSimpleName();
	}
	
	private static Class<?> getTableClass(IbBarContainer container, BarType barType){
		switch (barType) {
		case SECOND:
			return getSecondeTableClass(container);
		case MINUTE:
			return getMinuteTableClass(container);
		case DAY:
			return getMinuteTableClass(container);
		default:
			return null;
		}
	}
	
	
	public static Class<?> getSecondeTableClass(IbBarContainer container){
		switch (container.getType()) {
		case ASK:
			return SecondeAskBar.class;
		case MIDPOINT:
			return SecondeMidPointBar.class;
		case TRADES:
			return SecondeTradesBar.class;
		case BID:
			return SecondeBidBar.class;
		default:
			return null;
		}
	}
	
	public static Class<?> getMinuteTableClass(IbBarContainer container){
		switch (container.getType()) {
		case ASK:
			return MinuteAskBar.class;
		case MIDPOINT:
			return MinuteMidPointBar.class;
		case TRADES:
			return MinuteTradesBar.class;
		case BID:
			return MinuteBidBar.class;
		default:
			return null;
		}
	}
	
	
	

}
