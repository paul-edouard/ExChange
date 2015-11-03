package com.munch.exchange.services.ejb.providers;

import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ChartIndicatorBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBChartIndicatorProvider;

public class IBChartIndicatorProvider implements IIBChartIndicatorProvider {
	
	BeanRemote<ChartIndicatorBeanRemote> beanRemote;

	@Override
	public IbChartIndicatorGroup update(IbChartIndicatorGroup group) {
		if(beanRemote==null)init();
		return beanRemote.getService().update(group);
	}

	@Override
	public void removeGroup(int id) {
		if(beanRemote==null)init();
		beanRemote.getService().removeGroup(id);
	}

	@Override
	public IbChartIndicatorGroup getGroup(int id) {
		if(beanRemote==null)init();
		return beanRemote.getService().getGroup(id);
	}

	@Override
	public void init() {
		beanRemote=new BeanRemote<ChartIndicatorBeanRemote>("ChartIndicatorBean",ChartIndicatorBeanRemote.class);
	}

	@Override
	public void close() {
		if(beanRemote!=null)
			beanRemote.CloseContext();
	}

}
