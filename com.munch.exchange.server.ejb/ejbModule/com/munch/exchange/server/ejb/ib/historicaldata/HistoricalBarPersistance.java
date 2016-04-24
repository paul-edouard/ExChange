package com.munch.exchange.server.ejb.ib.historicaldata;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.ib.controller.Bar;
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
	
	
	
	public static void saveBars(EntityManager em, IbBarContainer container, BarType barType, List<Bar> bars){
		Class<?> tableClass=getTableClass(container,barType);
		try {

			int i = 0;

//			em.getTransaction().begin();

			for (Bar bar : bars) {

				BarConversionInterface ibBar = (BarConversionInterface) tableClass.newInstance();
				ibBar.init(bar);
				em.persist(ibBar);

				if ((i % 10000) == 0) {
					
					 em.flush();
			         em.clear();
				}

				i++;

			}

//			em.getTransaction().commit();

		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void removeBarsFromTo(EntityManager em, IbBarContainer container,
			BarType barType , long from, long to) {
		
		Query query=em.createQuery("DELETE " +
				"FROM "+getTableName(container,barType)+" b "+
    			"WHERE b.container="+getContainerId(container, barType)+" "+
				"AND b.time>="+from+ " "+
    			"AND b.time<="+to);
    	
		query.executeUpdate();
		
	}
	
	public static long getFirstBarTime(EntityManager em,
			IbBarContainer container, BarType barType ){
		Query query=em.createQuery("SELECT MIN(b.time)" +
					"FROM "+getTableName(container,barType)+
					" b WHERE b.container="+getContainerId(container, barType)
					);

    	Object singleResult=query.getSingleResult();
    	if(singleResult==null)return 0;
    	
    	long time=(long) singleResult;
		
    	return time;
	}
		
	public static long getLastMinuteBarTime(EntityManager em,
			IbBarContainer container, BarType barType ){
		Query query=em.createQuery("SELECT MAX(b.time)" +
					"FROM "+getTableName(container,barType)+
					" b WHERE b.container="+getContainerId(container, barType)
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
    			"WHERE b.container="+getContainerId(container, barType);
		
		return getIbBarListFromQuery(em, queryString);
	}
	
	public static List<IbBar> getBarsFromTo(EntityManager em,
			IbBarContainer container, BarType barType , long from, long to){
		
		String queryString="SELECT b " +
				"FROM "+getTableName(container,barType)+" b "+
				"WHERE b.container="+getContainerId(container, barType)+" "+
				"AND b.time>="+from+ " "+
				"AND b.time<="+to;
		
		return getIbBarListFromQuery(em, queryString);
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
	
	
	private static long getContainerId(IbBarContainer container, BarType barType){
		switch (barType) {
		case SECONDE:
			return container.getContract().getSecondeContainer().getId();
		case MINUTE:
			return container.getContract().getMinuteContainer().getId();
		case DAY:
			return 0;
		default:
			return 0;
		}
		
	}
	
	private static String getTableName(IbBarContainer container, BarType barType){
		Class<?> tableClass=getTableClass(container,barType);
		if(tableClass==null)return "";
		return tableClass.getSimpleName();
	}
	
	private static Class<?> getTableClass(IbBarContainer container, BarType barType){
		switch (barType) {
		case SECONDE:
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
