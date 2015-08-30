package com.munch.exchange.services.ejb.interfaces;

import com.munch.exchange.model.core.ib.bar.IbBar;

public interface IIBRealTimeBarListener {
	
	int getContractId();
	
	void realTimeBarChanged(IbBar bar);

}
