package com.munch.exchange.parts;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.munch.exchange.model.core.ExchangeRate;

public class RatesTreeDragSourceListener implements DragSourceListener {
	
	
	private final TreeViewer treeViewer;
	
	public RatesTreeDragSourceListener(TreeViewer treeViewer) {
		super();
		this.treeViewer = treeViewer;
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
	    if(selection.getFirstElement() instanceof ExchangeRate){
	    	ExchangeRate firstElement = (ExchangeRate) selection.getFirstElement();
	    
	    	if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	    		event.data = firstElement.getUUID(); 
	    	}
	    }

	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("Finshed Drag");
	}

}
