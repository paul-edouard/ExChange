package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.ib.controller.Bar;
import com.munch.exchange.model.core.ib.bar.BarContainerInterface;
import com.munch.exchange.model.core.ib.bar.BarConversionInterface;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.TimeBarSize;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.minute.MinuteAskBar;
import com.munch.exchange.model.core.ib.bar.minute.MinuteBidBar;
import com.munch.exchange.model.core.ib.bar.minute.MinuteMidPointBar;
import com.munch.exchange.model.core.ib.bar.minute.MinuteTradesBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeAskBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeBidBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeMidPointBar;
import com.munch.exchange.model.core.ib.bar.seconde.SecondeTradesBar;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;

public class HistoricalBarPersistance {
	
	private static final Logger log = Logger.getLogger(HistoricalBarPersistance.class.getName());
	
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query

	
	public static void saveBars(EntityManager em, BarContainer container, TimeBarSize barType, List<Bar> bars){

		
		Class<?> tableClass=getTableClass(container,barType);
		try {

			int i = 1;

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
		
		
	}
	
	public static void removeBarsFromTo(EntityManager em, BarContainer container,
			TimeBarSize barType , long from, long to) {
		
		Query query=em.createQuery("DELETE " +
				"FROM "+getTableName(container,barType)+
    			"WHERE containerId="+getContainerId(em, container, barType)+" "+
				"AND time>="+from+ " "+
    			"AND time<="+to);
    	
		query.executeUpdate();
		
	}
	
	public static long getFirstBarTime(EntityManager em,
			BarContainer container, TimeBarSize barType ){
		
//		log.info("SELECT MIN(b.time)" +
//					"FROM "+getTableName(container,barType)+
//					" b WHERE b.container="+getContainerId(em, container, barType));
//		
		Query query=em.createQuery("SELECT MIN(b.time)" +
					"FROM "+getTableName(container,barType)+
					" b WHERE b.containerId="+getContainerId(em, container, barType)
					);

    	Object singleResult=query.getSingleResult();
    	em.flush();
    	em.clear();
    	
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
		
    	return time;
	}
		
	public static long getLastBarTime(EntityManager em,
			BarContainer container, TimeBarSize barType ){
		Query query=em.createQuery("SELECT MAX(b.time)" +
					"FROM "+getTableName(container,barType)+
					" b WHERE b.containerId="+getContainerId(em, container, barType)
					);

    	Object singleResult=query.getSingleResult();
    	em.flush();
    	em.clear();
    	
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
		
    	return time;
	}
	
	public static List<ExBar> getAllBars(EntityManager em,
			BarContainer container, TimeBarSize timeBarSize ){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,timeBarSize)+" b "+
    			"WHERE b.containerId="+getContainerId(em, container, timeBarSize);
		
		return getBarListFromQuery(em, queryString);
	}
	
	public static List<ExBar> getBarsFromTo(EntityManager em,
			BarContainer container, TimeBarSize barType , long from, long to){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,barType)+" b "+
				"WHERE b.containerId="+getContainerId(em, container, barType)+" "+
				"AND b.time>="+from+ " "+
				"AND b.time<="+to;
//		log.info("Query: "+queryString);
		return getBarListFromQuery(em, queryString);
	}
	
	public static boolean containsBar(EntityManager em,
			BarContainer container, TimeBarSize timeBarType , long time){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,timeBarType)+" b "+
				"WHERE b.containerId="+getContainerId(em, container, timeBarType)+" "+
				"AND b.time="+time;
		List<ExBar> bars=getBarListFromQuery(em, queryString);
		
//		log.info(queryString+", bars: "+bars.size());
		
		
		return bars.size()==1;
	}
	
	public static ExBar getBar(EntityManager em,
			BarContainer container, TimeBarSize timeBarType , long time){
		String queryString="SELECT b " +
				"FROM "+getTableName(container,timeBarType)+" b "+
				"WHERE b.containerId="+getContainerId(em, container, timeBarType)+" "+
				"AND b.time="+time;
		List<ExBar> bars=getBarListFromQuery(em, queryString);
		if(bars==null || bars.isEmpty() || bars.size()!=1)return null;
		
		return bars.get(0);
	}
	
	
 	public static boolean isLongTermBarLoadingFinished(EntityManager em, BarContainer container, TimeBarSize barType){
		BarContainerInterface containerIt=getContainer(em, container, barType);
		if(containerIt==null)return true;
		
		switch (container.getType()) {
		case ASK:
			return containerIt.isLongTermAskBarLoadingFinished();
		case MIDPOINT:
			return containerIt.isLongTermMidPointBarLoadingFinished();
		case TRADES:
			return containerIt.isLongTermTradesBarLoadingFinished();
		case BID:
			return containerIt.isLongTermBidBarLoadingFinished();
		default:
			return true;
		}
		
	}
	
	
	public static long getLastShortTermBarTime(EntityManager em, BarContainer container, TimeBarSize barType){
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
	
	
	public static void setLongTermBarLoadingFinished(EntityManager em, BarContainer container, TimeBarSize barType, boolean longTermBarLoadingFinished){
		BarContainerInterface containerIt=getContainer(em, container, barType);
		if(containerIt==null)return ;
		
		BarContainerInterface foundContainer=em.find(containerIt.getClass(), containerIt.getId());
		
		switch (container.getType()) {
		case ASK:
			foundContainer.setLongTermAskBarLoadingFinished(longTermBarLoadingFinished);
			break;
		case MIDPOINT:
			foundContainer.setLongTermMidPointBarLoadingFinished(longTermBarLoadingFinished);
			break;
		case TRADES:
			foundContainer.setLongTermTradesBarLoadingFinished(longTermBarLoadingFinished);;
			break;
		case BID:
			foundContainer.setLongTermBidBarLoadingFinished(longTermBarLoadingFinished);;
			break;
		default:
			break ;
		}
		
		em.persist(foundContainer);
		em.flush();
		
		
	}
	
	
	public static void setLastShortTermBarTime(EntityManager em, BarContainer container, TimeBarSize barType, long time){
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
	
	
	
	
	private static List<ExBar> getBarListFromQuery(EntityManager em, String queryString){
		
		TypedQuery<BarConversionInterface> query=em.createQuery(queryString,BarConversionInterface.class);
		List<BarConversionInterface> bars=query.getResultList();
		
//		Convert the bar to IbBars
		List<ExBar> Bars=new LinkedList<ExBar>();
		for(BarConversionInterface bar:bars){
			Bars.add(bar.toExBar());
		}
		
		em.flush();
    	em.clear();
		
		return Bars;
		
	}
	
	
	private static long getContainerId(EntityManager em, BarContainer container, TimeBarSize barType){
		
		BarContainer localContainer = em.find(BarContainer.class, container.getId());
		
		BarContainerInterface containerIt=getContainer(em, localContainer, barType);
		if(containerIt!=null)return containerIt.getId();
		
		return 0;
	}
	
	private static BarContainerInterface getContainer(EntityManager em, BarContainer container, TimeBarSize barType){
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
		default:
			return null;
		}
		
	}
	
	private static String getTableName(BarContainer container, TimeBarSize barType){
		Class<?> tableClass=getTableClass(container,barType);
		if(tableClass==null)return "";
		return tableClass.getSimpleName();
	}
	
	private static Class<?> getTableClass(BarContainer container, TimeBarSize barType){
		switch (barType) {
		case SECOND:
			return getSecondeTableClass(container);
		case MINUTE:
			return getMinuteTableClass(container);
		default:
			return null;
		}
	}
	
	
	public static Class<?> getSecondeTableClass(BarContainer container){
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
	
	public static Class<?> getMinuteTableClass(BarContainer container){
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
