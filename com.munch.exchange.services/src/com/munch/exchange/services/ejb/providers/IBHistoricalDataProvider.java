package com.munch.exchange.services.ejb.providers;

import java.util.List;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.HistoricalDataBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

public class IBHistoricalDataProvider implements IIBHistoricalDataProvider {
	
	
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
		List<ExBar> bars=beanRemote.getService().getAllTimeBars(exContractBars,size);
//		Collections.sort(bars);
		return bars;
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
		return beanRemote.getService().getAllRangeBars(container,range);
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

	

	

}
