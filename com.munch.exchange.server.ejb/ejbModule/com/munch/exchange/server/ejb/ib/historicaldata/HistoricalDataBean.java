package com.munch.exchange.server.ejb.ib.historicaldata;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBarComparator;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.TimeBarSize;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorFactory;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.server.ejb.ib.topmktdata.TopMktDataMsgSenderCollector;
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
	public List<ExBar> getAllRealTimeBars(BarContainer arg0, BarSize targetSize) {
		// TODO Auto-generated method stub
    	
    	BarContainer container=em.find(BarContainer.class, arg0.getId());
    	IbContract contract=container.getContract();
    	
    	List<ExBar> secondBars=TopMktDataMsgSenderCollector.INSTANCE.searchLoadedSecondBars(contract, container.getType());
    	return BarUtils.convertTimeBars(secondBars, BarSize._1_secs, targetSize);
    	
	}
    
    
    
	@Override
	public List<BarContainer> getAllBarContainers(IbContract exContract) {
		
		//log.info("getAllExContractBars server called!");
		
		IbContract ex_contract=em.find(IbContract.class, exContract.getId());
		ex_contract.getBars().size();
		List<BarContainer> contractBars= ex_contract.getBars();
		
		//Load and update the Chart indicators
		
		for(BarContainer container:contractBars){
			IbChartIndicatorGroup rootGroup=container.getIndicatorGroup();
			
//			log.info("1. Group is Dirty: "+rootGroup.isDirty());
			
			if(!IbChartIndicatorFactory.updateRoot(rootGroup, container))continue;
			
//			log.info("2. Group is Dirty: "+rootGroup.isDirty());
			
			if(rootGroup.isDirty()){
				
				em.persist(rootGroup);
				rootGroup.setDirty(false);
				
//				log.info("3. Group is Dirty: "+rootGroup.isDirty());
			}
			
			loadChildGroup(rootGroup);
			
			
			
		}
		
		/*
		List<IbBarContainer> contractBarsCopies=new LinkedList<IbBarContainer>();
		for(IbBarContainer container:contractBars){
			contractBarsCopies.add(container.copy());
		}
		*/
		
		
		
		return contractBars;
	}
	
	private void loadChildGroup(IbChartIndicatorGroup group){
		for(IbChartIndicatorGroup child:group.getChildren()){
			loadChildGroup(child);
		}
		for(IbChartIndicator ind:group.getIndicators()){
			ind.getParameters().size();
			ind.getSeries().size();
		}
		
	}
	
	

	@Override
	public ExBar getFirstTimeBar(BarContainer container
			,BarSize size) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		BarContainer localContainer=em.find(BarContainer.class, container.getId());
		long firstBarTime=HistoricalBarPersistance.getFirstBarTime(em, localContainer, timeBarSize);

		return HistoricalBarPersistance.getBar(em, localContainer, timeBarSize, firstBarTime);
	}
		
	@Override
	public ExBar getLastTimeBar(BarContainer container,
			BarSize size) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		BarContainer localContainer=em.find(BarContainer.class, container.getId());
		long lastBarTime=HistoricalBarPersistance.getLastBarTime(em, localContainer, timeBarSize);

		return HistoricalBarPersistance.getBar(em, localContainer, timeBarSize, lastBarTime);
		
	}
	
	@Override
	public List<ExBar> getAllTimeBars(BarContainer container,BarSize size) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		
		BarContainer localContainer=em.find(BarContainer.class, container.getId());
		
		List<ExBar> bars=HistoricalBarPersistance.getAllBars(em, localContainer, timeBarSize);
//		Sort the bars
		Collections.sort(bars, new ExBarComparator());
		
		
		List<ExBar> convertedBars=BarUtils.convertTimeBars(bars,
				BarUtils.convert(timeBarSize), size);
		
		return convertedBars;
	}

	@Override
	public List<ExBar> getTimeBarsFromTo(BarContainer container,
			BarSize size, long from, long to) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		BarContainer localContainer=em.find(BarContainer.class, container.getId());
		
		List<ExBar> bars=HistoricalBarPersistance.getBarsFromTo(em, localContainer, timeBarSize, from, to);
		
//		log.info("Number of bars loaded: "+bars.size());
		
		
		Collections.sort(bars, new ExBarComparator());
		
		
		List<ExBar> convertedBars=BarUtils.convertTimeBars(bars,
				BarUtils.convert(timeBarSize), size);
		
		return convertedBars;
		
		
	}
	
	@Override
	public void removeBarsFromTo(BarContainer container,
			BarSize size, long from, long to) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		BarContainer localContainer=em.find(BarContainer.class, container.getId());
		HistoricalBarPersistance.removeBarsFromTo(em, localContainer, timeBarSize, from, to);
	}

	
	
	@Override
	public List<ExBar> getAllRangeBars(BarContainer container, double range) {
		List<ExBar> bars=HistoricalBarPersistance.getAllBars(em, container, TimeBarSize.SECOND);
		
		LinkedList<LinkedList<ExBar>> barBlocks=BarUtils.splitBarListInDayBlocks(bars);
		
		return BarUtils.convertToRangeBars(barBlocks, range);
	}

	@Override
	public ExBar getFirstRangeBar(BarContainer container, double range) {
		long firstBarTime=HistoricalBarPersistance.getFirstBarTime(em, container, TimeBarSize.SECOND);

		return HistoricalBarPersistance.getBar(em, container, TimeBarSize.SECOND, firstBarTime);
		
	}

	@Override
	public ExBar getLastRangeBar(BarContainer container, double range) {
		long lastBarTime=HistoricalBarPersistance.getLastBarTime(em, container, TimeBarSize.SECOND);

		return HistoricalBarPersistance.getBar(em, container, TimeBarSize.SECOND, lastBarTime);
	}

	@Override
	public List<ExBar> getRangeBarsFromTo(BarContainer container, double range, long from, long to) {
		List<ExBar> bars=HistoricalBarPersistance.getBarsFromTo(em, container, TimeBarSize.SECOND, from, to);
		
		LinkedList<LinkedList<ExBar>> barBlocks=BarUtils.splitBarListInDayBlocks(bars);
		
		return BarUtils.convertToRangeBars(barBlocks, range);
		
		
	}

	
	




	
	

	


	


}
