 
package com.munch.exchange.parts.chart;

import java.util.List;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbHourBar;
import com.munch.exchange.model.core.ib.bar.IbMinuteBar;
import com.munch.exchange.parts.RateEditorPart;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.providers.IBHistoricalDataProvider;

public class ChartEditorPart {
	
	private static Logger logger = Logger.getLogger(ChartEditorPart.class);
	
	public static final String CHART_EDITOR_ID="com.munch.exchange.partdescriptor.charteditor";
	
	@Inject
	IbContract contract;
	
	@Inject
	IIBHistoricalDataProvider provider;
	
	
	
	@Inject
	public ChartEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		//TODO Your code here
		logger.info("Contract: "+contract.getLongName());
		List<IbBarContainer> containers=provider.getAllExContractBars(contract);
		for(IbBarContainer container:containers){
			//List<IbBar> bars=provider.getAllBars(container, IbMinuteBar.class);
			//for(IbBar bar:bars)
			//	logger.info("bar: "+bar.toString());
			IbBar firstBar=provider.getFirstBar(container, IbMinuteBar.class);
			IbBar lastBar=provider.getLastBar(container, IbMinuteBar.class);
			logger.info("Fisrt bar: "+firstBar.toString());
			logger.info("Last bar: "+lastBar.toString());
		}
		
	}
	
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}
	
}