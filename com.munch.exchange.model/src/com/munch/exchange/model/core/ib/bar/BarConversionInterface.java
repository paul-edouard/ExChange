package com.munch.exchange.model.core.ib.bar;

import com.ib.controller.Bar;

public interface BarConversionInterface {
	
	public void init(Bar bar);
	public void attachToContainer(Object container);
	public IbBar toIbBar();
	

}
