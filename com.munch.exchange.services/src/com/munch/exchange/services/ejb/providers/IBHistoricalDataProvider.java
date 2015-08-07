package com.munch.exchange.services.ejb.providers;

import java.util.List;

import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExContractBars;
import com.munch.exchange.model.core.ib.bar.ExSecondeBar;
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
	public List<ExContractBars> getAllExContractBars(ExContract exContract) {
		return beanRemote.getService().getAllExContractBars(exContract);
	}

	@Override
	public ExBar getFirstBar(ExContractBars exContractBars,
			Class<? extends ExBar> exBarClass) {
		return beanRemote.getService().getFirstBar(exContractBars,exBarClass);
	}

	@Override
	public ExBar getLastBar(ExContractBars exContractBars,
			Class<? extends ExBar> exBarClass) {
		return beanRemote.getService().getLastBar(exContractBars,exBarClass);
	}

	

	

}
