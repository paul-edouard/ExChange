package com.munch.exchange.services.ejb.interfaces;

import com.munch.exchange.model.core.ib.bar.ExBar;

public interface IIBRealTimeBarListener {
	
	int getContractId();
	
	void realTimeBarChanged(ExBar bar);

}
