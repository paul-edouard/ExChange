package com.munch.exchange.services.ejb.providers;

import java.util.List;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
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
	public List<IbBarContainer> getAllExContractBars(IbContract exContract) {
		if(beanRemote==null)init();
		List<IbBarContainer> containers=beanRemote.getService().getAllExContractBars(exContract);
		//close();
		return containers;
	}

	@Override
	public IbBar getFirstBar(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		if(beanRemote==null)init();
		return beanRemote.getService().getFirstBar(exContractBars,exBarClass);
	}

	@Override
	public IbBar getLastBar(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		if(beanRemote==null)init();
		return beanRemote.getService().getLastBar(exContractBars,exBarClass);
	}

	@Override
	public List<IbBar> getAllBars(IbBarContainer exContractBars,
			BarSize size) {
		if(beanRemote==null)init();
		return beanRemote.getService().getAllBars(exContractBars,size);
	}

	@Override
	public IbBar searchBarOfTime(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass, long time) {
		if(beanRemote==null)init();
		return beanRemote.getService().searchBarOfTime(exContractBars,exBarClass,time);
	}

	@Override
	public long getFirstBarTime(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		if(beanRemote==null)init();
		return beanRemote.getService().getFirstBarTime(exContractBars,exBarClass);
	}

	@Override
	public long getLastBarTime(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		if(beanRemote==null)init();
		return beanRemote.getService().getLastBarTime(exContractBars,exBarClass);
	}

	@Override
	public List<IbBar> getBarsFromTo(IbBarContainer exContractBars,
			BarSize size, long from, long to) {
		if(beanRemote==null)init();
		List<IbBar> bars=beanRemote.getService().getBarsFromTo(exContractBars,size,from,to);
		//close();
		return bars;
	}

	@Override
	public void removeBar(long id) {
		if(beanRemote==null)init();
		beanRemote.getService().removeBar(id);
	}

	@Override
	public IbBar getBar(long id) {
		if(beanRemote==null)init();
		return beanRemote.getService().getBar(id);
	}

	@Override
	public List<IbBar> downloadLastBars(IbBarContainer exContractBars,
			BarSize size) {
		if(beanRemote==null)init();
		return beanRemote.getService().downloadLastBars(exContractBars, size);
	}

	@Override
	public void close() {
		if(beanRemote!=null)
			beanRemote.CloseContext();
	}

	

	

}
