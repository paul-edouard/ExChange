package com.munch.exchange.services.ejb.providers;

import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ChartIndicatorBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBNeuralProvider;
import com.munch.exchange.services.ejb.interfaces.NeuralBeanRemote;

public class IBNeuralProvider implements IIBNeuralProvider {
	
	
	BeanRemote<NeuralBeanRemote> beanRemote;

	@Override
	public void init() {
		System.out.println("Initialiuation of IBNeuralProvider");
		beanRemote=new BeanRemote<NeuralBeanRemote>("NeuralBean",NeuralBeanRemote.class);
	}

	@Override
	public void close() {
		if(beanRemote!=null)
			beanRemote.CloseContext();

	}
	
	@Override
	public void update(NeuralConfiguration confiuration) {
		if(beanRemote==null)init();

	}

}
