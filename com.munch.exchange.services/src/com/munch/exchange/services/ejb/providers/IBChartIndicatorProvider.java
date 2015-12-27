package com.munch.exchange.services.ejb.providers;

import java.util.List;

import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ChartIndicatorBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBChartIndicatorProvider;

public class IBChartIndicatorProvider implements IIBChartIndicatorProvider {
	
	BeanRemote<ChartIndicatorBeanRemote> beanRemote;

	@Override
	public void update(IbChartIndicatorGroup group) {
		if(beanRemote==null)init();
		
		//Create a copy of the Indicator Group and send it to the JPA Service
		beanRemote.getService().update(group.copy());
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

	@Override
	public List<IbChartSignalOptimizedParameters> updateOptimizedParameters(IbChartSignal signal) {
		if(beanRemote==null)init();
		//Create a copy of the Indicator Signal and send it to the JPA Service
		IbChartSignal cp=(IbChartSignal)signal.copy();
		cp.setGroup(signal.getGroup().copy());
		
		//Update the parameters of the signal
		List<IbChartSignalOptimizedParameters> list=beanRemote.getService().updateOptimizedParameters(cp);
		signal.removeAllOptimizedParameters();
		for(IbChartSignalOptimizedParameters optimizedParameters:list){
			signal.addOptimizedParameters(optimizedParameters);
		}
		
		return list;
		//return beanRemote.getService().updateOptimizedParameters(signal);
		
	}

	@Override
	public IbChartSignal getSignal(int id) {
		if(beanRemote==null)init();
		return beanRemote.getService().getSignal(id);
	}

	
}
