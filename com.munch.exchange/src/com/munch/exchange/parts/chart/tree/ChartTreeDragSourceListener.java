package com.munch.exchange.parts.chart.tree;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.chart.ChartIndicator;

public class ChartTreeDragSourceListener implements DragSourceListener  {
	
	private final TreeViewer treeViewer;
	private ExchangeRate rate;
	
	public ChartTreeDragSourceListener(TreeViewer treeViewer,ExchangeRate rate) {
		super();
		this.treeViewer = treeViewer;
		this.rate=rate;
	}
	

	@Override
	public void dragStart(DragSourceEvent event) {
		System.out.println("Start Drag");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
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
	    }
	    
	    event.data=dataString;
	    
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("Finshed Drag");
	}
	
	
}
