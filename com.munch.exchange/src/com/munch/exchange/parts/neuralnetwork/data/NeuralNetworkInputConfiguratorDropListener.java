package com.munch.exchange.parts.neuralnetwork.data;

import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorFactory;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeriesGroup;
import com.munch.exchange.services.IExchangeRateProvider;

public class NeuralNetworkInputConfiguratorDropListener extends ViewerDropAdapter {

	  private final NeuralNetworkInputConfiguratorComposite parent;
	  private TimeSeriesGroup targetGroup;
	  private IExchangeRateProvider provider;

	  public NeuralNetworkInputConfiguratorDropListener(NeuralNetworkInputConfiguratorComposite parent,IExchangeRateProvider provider ) {
	    super(parent.getTreeViewer());
	    this.parent = parent;
	    this.provider=provider;
	  }

	  @Override
	  public void drop(DropTargetEvent event) {
	    int location = this.determineLocation(event);
	    String translatedLocation ="";
	    switch (location){
	    case 1 :
	      translatedLocation = "Dropped before the target ";
	      break;
	    case 2 :
	      translatedLocation = "Dropped after the target ";
	      break;
	    case 3 :
	      translatedLocation = "Dropped on the target ";
	      break;
	    case 4 :
	      translatedLocation = "Dropped into nothing ";
	      break;
	    }
	    System.out.println(translatedLocation);
	    Object target=determineTarget(event);
	    if(target instanceof TimeSeriesGroup){
	    	TimeSeriesGroup group=(TimeSeriesGroup)target;
	    	System.out.println("The drop was done on the element: " + group.getName());
	    }
	    super.drop(event);
	  }

	  // This method performs the actual drop
	  // We simply add the String we receive to the model and trigger a refresh of the 
	  // viewer by calling its setInput method.
	  @Override
	  public boolean performDrop(Object data) {
		System.out.println("Perform the Drop please! Data: "+data.toString());
	    String[] lines=data.toString().split("\n");
	    for(int i=0;i<lines.length;i++){
	    	String[] csvTockens=lines[i].split(";");
	    	if(csvTockens.length<2)continue;
	    	ExchangeRate rate=provider.load(csvTockens[0]);
	    	if(rate==null)continue;
	    	
	    	TimeSeriesGroup indicatorGroup=targetGroup.searchIndicatorGroupOf(rate);
	    	if(indicatorGroup==null)continue;
	    	
	    	TimeSeries series=new TimeSeries();
	    	ChartIndicator indicator=ChartIndicatorFactory.createChartIndicator(csvTockens, series);
	    	series.setIndicator(indicator);
	    	series.setName(indicator.getName());
	    	//series.setNumberOfPastValues(4);
	    	
	    	boolean isNewSeries=true;
	    	for(TimeSeries s:indicatorGroup.getTimeSeriesList()){
	    		if(s.getName().equals(indicator.getName()))
	    			isNewSeries=false;
	    	}
	    	
	    	if(!isNewSeries)continue;
	    	
	    	//Add the time series
	    	indicatorGroup.addTimeSeries(series);
	    	
	    	
	    }
	    
	    parent.getTreeViewer().refresh();
		//ContentProviderTree.INSTANCE.getModel().add(data.toString());
	    //viewer.setInput(ContentProviderTree.INSTANCE.getModel());
	    return false;
	  }

	  @Override
	  public boolean validateDrop(Object target, int operation,
	      TransferData transferType) {
		if(!parent.isEditing())return false;
	    
		if(target instanceof TimeSeriesGroup){
			targetGroup=(TimeSeriesGroup)target;
			return true;
		}
		
		return false;
	    
	  }

	  

}
