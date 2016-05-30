package com.munch.exchange.services.ejb.providers;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.HistoricalDataBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

public class IBHistoricalDataProvider implements IIBHistoricalDataProvider {
	
	private static final Logger log = Logger.getLogger(IBHistoricalDataProvider.class.getName());
	
	
	BeanRemote<HistoricalDataBeanRemote> beanRemote;
	
	@Override
	public void init() {
		beanRemote=new BeanRemote<HistoricalDataBeanRemote>("HistoricalDataBean",HistoricalDataBeanRemote.class);
	}

	@Override
	public List<BarContainer> getAllBarContainers(IbContract exContract) {
		if(beanRemote==null)init();
		List<BarContainer> containers=beanRemote.getService().getAllBarContainers(exContract);
		//close();
		return containers;
	}

	@Override
	public ExBar getFirstTimeBar(BarContainer exContractBars,
			BarSize size) {
		if(beanRemote==null)init();
		return beanRemote.getService().getFirstTimeBar(exContractBars,size);
	}

	@Override
	public ExBar getLastTimeBar(BarContainer exContractBars,
			BarSize size) {
		if(beanRemote==null)init();
		return beanRemote.getService().getLastTimeBar(exContractBars,size);
	}

	@Override
	public List<ExBar> getAllTimeBars(BarContainer exContractBars,
			BarSize size) {
		if(beanRemote==null)init();
		
//		Start of loading all bars, split the query in order to avoid java heap space error
		long nbOfSplits=40;
		ExBar lastTimeBar=beanRemote.getService().getLastTimeBar(exContractBars,size);
		ExBar firstTimeBar=beanRemote.getService().getFirstTimeBar(exContractBars,size);
		long lastBarTime=lastTimeBar.getTime();
		long firstBarTime=firstTimeBar.getTime();
		long inc=lastTimeBar.getTime()-firstTimeBar.getTime();
		inc/=nbOfSplits;
		if(inc==0)
			inc=1;
		
		
		int cores = Runtime.getRuntime().availableProcessors();
		
		List<ExBar> collectedBars=new LinkedList<ExBar>();
		while(firstBarTime < lastBarTime){
			long from=firstBarTime;
			long to=firstBarTime+inc;
			
			log.info("From: "+BarUtils.format(from*1000)+ ", "+"To: "+BarUtils.format(to*1000));
//			log.info("To: "+BarUtils.format(to*1000));
			
			if(cores < 10 && (lastBarTime-to) > 2*inc){
				log.info("Small power cores: "+cores+" skip the loading");
			}
			else{
				collectedBars.addAll(beanRemote.getService().getTimeBarsFromTo(exContractBars,size,from,to));
			}
			
			firstBarTime+=inc;
		}
		
		
		
//		List<ExBar> bars=beanRemote.getService().getAllTimeBars(exContractBars,size);
//		Collections.sort(bars);
		return collectedBars;
	}


	@Override
	public List<ExBar> getTimeBarsFromTo(BarContainer exContractBars,
			BarSize size, long from, long to) {
		if(beanRemote==null)init();
		List<ExBar> bars=beanRemote.getService().getTimeBarsFromTo(exContractBars,size,from,to);
		//close();
		return bars;
	}

	@Override
	public void close() {
		if(beanRemote!=null)
			beanRemote.CloseContext();
	}

	@Override
	public void removeBarsFromTo(BarContainer exContractBars, BarSize size,
			long from, long to) {
		if(beanRemote==null)init();
		beanRemote.getService().removeBarsFromTo(exContractBars,size,from,to);
		
	}

	@Override
	public List<ExBar> getAllRangeBars(BarContainer container, double range) {
		if(beanRemote==null)init();
//		return beanRemote.getService().getAllRangeBars(container,range);
		
		
//		Start of loading all bars, split the query in order to avoid java heap space error
		ExBar lastTimeBar=beanRemote.getService().getLastRangeBar(container,range);
		ExBar firstTimeBar=beanRemote.getService().getFirstRangeBar(container,range);
		
		Calendar currentDay=BarUtils.getCurrentDayOf(firstTimeBar.getTimeInMs());
		Calendar nextDay=BarUtils.addOneDayTo(currentDay);
				
		int cores = Runtime.getRuntime().availableProcessors();
		
		List<ExBar> collectedBars=new LinkedList<ExBar>();
		while(nextDay.getTimeInMillis() < lastTimeBar.getTimeInMs()){
			
			nextDay=BarUtils.addOneDayTo(currentDay);
			
			
			long from=currentDay.getTimeInMillis()/1000L;
			long to=nextDay.getTimeInMillis()/1000L;
			
			currentDay = nextDay;
			
			if(cores < 10){
				long diff = lastTimeBar.getTimeInMs() - nextDay.getTimeInMillis();
				if(diff > 40L*24L*60L*60L*1000L)
					continue;
				
			}

			log.info("From: "+BarUtils.format(currentDay.getTimeInMillis())+
					", "+"To: "+BarUtils.format(nextDay.getTimeInMillis()));

			collectedBars.addAll(beanRemote.getService().getRangeBarsFromTo(container,range,from,to));
			
			
		}
		
		return collectedBars;
		
		
		
	}

	@Override
	public ExBar getFirstRangeBar(BarContainer container, double range) {
		if(beanRemote==null)init();
		return beanRemote.getService().getFirstRangeBar(container,range);
	}

	@Override
	public ExBar getLastRangeBar(BarContainer container, double range) {
		if(beanRemote==null)init();
		return beanRemote.getService().getLastRangeBar(container,range);
	}

	@Override
	public List<ExBar> getRangeBarsFromTo(BarContainer container, double range, long from, long to) {
		if(beanRemote==null)init();
		return beanRemote.getService().getRangeBarsFromTo(container,range,from,to);
	}

	@Override
	public List<ExBar> getAllRealTimeBars(BarContainer arg0, BarSize arg1) {
		if(beanRemote==null)init();
		return beanRemote.getService().getAllRealTimeBars(arg0,arg1);
	}

	

	

}
