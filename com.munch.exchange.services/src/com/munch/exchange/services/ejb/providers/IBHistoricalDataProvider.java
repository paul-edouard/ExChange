package com.munch.exchange.services.ejb.providers;

import java.util.List;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;
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
		return beanRemote.getService().getAllExContractBars(exContract);
	}

	@Override
	public IbBar getFirstBar(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		return beanRemote.getService().getFirstBar(exContractBars,exBarClass);
	}

	@Override
	public IbBar getLastBar(IbBarContainer exContractBars,
			Class<? extends IbBar> exBarClass) {
		return beanRemote.getService().getLastBar(exContractBars,exBarClass);
	}

	

	

}
