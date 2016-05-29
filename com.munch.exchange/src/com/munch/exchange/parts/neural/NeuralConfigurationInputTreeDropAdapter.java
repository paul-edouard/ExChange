package com.munch.exchange.parts.neural;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

public class NeuralConfigurationInputTreeDropAdapter extends ViewerDropAdapter {
	
	private static Logger logger = Logger.getLogger(NeuralConfigurationInputTreeDropAdapter.class);
	
	@Inject
	private NeuralConfiguration neuralConfiguration;
	
	@Inject
	private IIBContractProvider contractProvider;
	
	@Inject
	private IIBHistoricalDataProvider historicalDataProvider; 
	
	@Inject
	MDirtyable dirty;
	
	private Viewer viewer;
	
	@Inject
	public NeuralConfigurationInputTreeDropAdapter(Viewer viewer) {
		super(viewer);
		this.viewer=viewer;
	}

	@Override
	public boolean performDrop(Object data) {
//		System.out.println("Perform the Drop please! Data: "+data.toString());
		String[] lines=data.toString().split("\n");
		for(int i=0;i<lines.length;i++){
			String[] csvTockens=lines[i].split(";");
			if(csvTockens.length!=7)continue;
			
			String contractId=csvTockens[0];
			String containerId=csvTockens[1];
			String barType = csvTockens[2];
			String barSizeStr=csvTockens[3];
			String barRange=csvTockens[4];
			logger.info(barSizeStr);
			String indicatorId=csvTockens[5];
			String serieName=csvTockens[6];
			
//			Search the contract
			IbContract contract=contractProvider.getContract(Integer.valueOf(contractId));

//			Search the Container
			List<BarContainer> containers=historicalDataProvider.getAllBarContainers(contract);
			BarContainer barContainer=null;
			for(BarContainer container:containers){
				if(container.getId()==Long.valueOf(containerId)){
					barContainer=container;
				}
			}
			if(barContainer==null)continue;
			
//			Search the bar size
			BarSize batSize=BarUtils.getBarSizeFromString(barSizeStr);
			logger.info("bar Size:"+batSize.toString());
			
//			Search the indicator
			IbChartIndicator indicator=barContainer.getIndicatorGroup().searchIndicator(Integer.valueOf(indicatorId));
			IbChartIndicator indicatorCopy=indicator.copy();
			for(IbChartParameter param:indicatorCopy.getParameters()){
				param.setId(0);
			}
			for(IbChartSerie serie:indicatorCopy.getSeries()){
				serie.setId(0);
			}
			
			indicatorCopy.setId(0);
			
//			Search the serie
			IbChartSerie serie=indicator.getChartSerie(serieName);
			
			
//			Create the Neural Indicator Input
			NeuralIndicatorInput neuralInput=new NeuralIndicatorInput();
			neuralInput.setName(serie.getName());
			neuralInput.setNeuralConfiguration(neuralConfiguration);
			neuralInput.setContract(contract);
			neuralInput.setBarContainer(barContainer);
			neuralInput.setIndicator(indicatorCopy);
			indicatorCopy.setNeuralIndicatorInput(neuralInput);
			neuralInput.setBarType(BarType.fromString(barType));
			neuralInput.setSize(batSize);
			neuralInput.setRange(Double.parseDouble(barRange));
			neuralInput.setType(barContainer.getType());
			
//			Add the first input component
			neuralInput.addDirectComponent();
			
			indicatorCopy.getSeries().clear();
			
			neuralConfiguration.getNeuralInputs().add(neuralInput);
			dirty.setDirty(true);
			viewer.refresh();
		}
		
		
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		
		return true;
	}

}
