package com.munch.exchange.server.ejb.ib.historicaldata;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBarComparator;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.TimeBarSize;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorFactory;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.services.ejb.interfaces.HistoricalDataBeanRemote;

/**
 * Session Bean implementation class HistoricalDataBean
 */
@Stateless
@LocalBean
//@TransactionManagement(value=TransactionManagementType.BEAN)
public class HistoricalDataBean implements HistoricalDataBeanRemote{
	
//	private static final Logger log = Logger.getLogger(HistoricalDataBean.class.getName());
	
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
	public List<IbBarContainer> getAllBarContainers(IbContract exContract) {
		
		//log.info("getAllExContractBars server called!");
		
		IbContract ex_contract=em.find(IbContract.class, exContract.getId());
		ex_contract.getBars().size();
		List<IbBarContainer> contractBars= ex_contract.getBars();
		
		//Load and update the Chart indicators
		
		for(IbBarContainer container:contractBars){
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
	public ExBar getFirstTimeBar(IbBarContainer container
			,BarSize size) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		long firstBarTime=HistoricalBarPersistance.getFirstBarTime(em, container, timeBarSize);

		return HistoricalBarPersistance.getBar(em, container, timeBarSize, firstBarTime);
	}
		
	@Override
	public ExBar getLastTimeBar(IbBarContainer container,
			BarSize size) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		long lastBarTime=HistoricalBarPersistance.getLastBarTime(em, container, timeBarSize);

		return HistoricalBarPersistance.getBar(em, container, timeBarSize, lastBarTime);
		
	}
	
	@Override
	public List<ExBar> getAllTimeBars(IbBarContainer container,BarSize size) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		List<ExBar> bars=HistoricalBarPersistance.getAllBars(em, container, timeBarSize);
//		Sort the bars
		Collections.sort(bars, new ExBarComparator());
		
		
		List<ExBar> convertedBars=BarUtils.convertTimeBars(bars,
				BarUtils.convert(timeBarSize), size);
		
		return convertedBars;
	}

	@Override
	public List<ExBar> getTimeBarsFromTo(IbBarContainer container,
			BarSize size, long from, long to) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		List<ExBar> bars=HistoricalBarPersistance.getBarsFromTo(em, container, timeBarSize, from, to);
		Collections.sort(bars, new ExBarComparator());
		
		
		List<ExBar> convertedBars=BarUtils.convertTimeBars(bars,
				BarUtils.convert(timeBarSize), size);
		
		return convertedBars;
		
		
	}
	
	@Override
	public void removeBarsFromTo(IbBarContainer container,
			BarSize size, long from, long to) {
		
		TimeBarSize timeBarSize=BarUtils.convert(size);
		HistoricalBarPersistance.removeBarsFromTo(em, container, timeBarSize, from, to);
	}

	
	
	@Override
	public List<ExBar> getAllRangeBars(IbBarContainer container, double range) {
		List<ExBar> bars=HistoricalBarPersistance.getAllBars(em, container, TimeBarSize.SECOND);
		
		LinkedList<LinkedList<ExBar>> barBlocks=BarUtils.splitBarListInDayBlocks(bars);
		
		return BarUtils.convertToRangeBars(barBlocks, range);
	}

	@Override
	public ExBar getFirstRangeBar(IbBarContainer container, double range) {
		long firstBarTime=HistoricalBarPersistance.getFirstBarTime(em, container, TimeBarSize.SECOND);

		return HistoricalBarPersistance.getBar(em, container, TimeBarSize.SECOND, firstBarTime);
		
	}

	@Override
	public ExBar getLastRangeBar(IbBarContainer container, double range) {
		long lastBarTime=HistoricalBarPersistance.getLastBarTime(em, container, TimeBarSize.SECOND);

		return HistoricalBarPersistance.getBar(em, container, TimeBarSize.SECOND, lastBarTime);
	}

	@Override
	public List<ExBar> getRangeBarsFromTo(IbBarContainer container, double range, long from, long to) {
		List<ExBar> bars=HistoricalBarPersistance.getBarsFromTo(em, container, TimeBarSize.SECOND, from, to);
		
		LinkedList<LinkedList<ExBar>> barBlocks=BarUtils.splitBarListInDayBlocks(bars);
		
		return BarUtils.convertToRangeBars(barBlocks, range);
		
		
	}
	




	
	

	


	


}
