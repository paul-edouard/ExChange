package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.munch.exchange.model.core.ib.ExContract;

public interface IContractProvider {
	
	List<ExContract> getAll();

	void init();
}
