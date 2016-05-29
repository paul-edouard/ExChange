package com.munch.exchange.parts.chart.tree;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;

public class ChartTreeDragSourceListener implements DragSourceListener  {
	
	private final TreeViewer treeViewer;
	private ExchangeRate rate;
	
	public ChartTreeDragSourceListener(TreeViewer treeViewer,ExchangeRate rate) {
		super();
		this.treeViewer = treeViewer;
		this.rate=rate;
	}
	public ChartTreeDragSourceListener(TreeViewer treeViewer) {
		super();
		this.treeViewer = treeViewer;
	}
	

	@Override
	public void dragStart(DragSourceEvent event) {
//		System.out.println("Start Drag");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		
//		System.out.println("Drag Set Data");
		
		// Here you do the convertion to the type which is expected.
	    IStructuredSelection selection = (IStructuredSelection) treeViewer
	    .getSelection();
	    Object[] array=selection.toArray();
	   
	    String dataString="";
	    for(int j=0;j<array.length;j++){
	    	if(array[j] instanceof ChartIndicator){
	    		ChartIndicator indicator = (ChartIndicator) array[j];
	    		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	    			dataString += rate.getUUID()+";"+indicator.toCsvString()+"\n"; 
	    		}
	    	}
	    	else if(array[j] instanceof IbChartIndicator){
	    		IbChartIndicator indicator = (IbChartIndicator) array[j];
	    		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	    			dataString += String.valueOf(indicator.getId())+"\n"; 
	    		}
	    	}
	    	else if(array[j] instanceof IbChartSerie){
	    		IbChartSerie serie=(IbChartSerie) array[j];
	    		IbChartIndicator indicator=serie.getIndicator();
	    		BarType barType = indicator.getGroup().getRoot().getBarType();
	    		BarSize barSize = indicator.getGroup().getRoot().getBarSize();
	    		double barRange = indicator.getGroup().getRoot().getRange();
	    		BarContainer container=indicator.getGroup().getRoot().getContainer();
	    		IbContract contract=container.getContract();
	    		
	    		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	    			//Contract
	    			dataString += contract.getId()+";"+container.getId()+";"+barType.toString()+";"+
	    			barSize.toString()+";"+barRange+";"+indicator.getId()+";"+serie.getName();
	    			System.out.println("dataString="+dataString);
	    		}
	    		
	    	}
	    }
	    
	    event.data=dataString;
	    
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
//		System.out.println("Finshed Drag");
	}
	
	
}
