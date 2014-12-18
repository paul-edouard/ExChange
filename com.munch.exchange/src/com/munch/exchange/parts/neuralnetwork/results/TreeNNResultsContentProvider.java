package com.munch.exchange.parts.neuralnetwork.results;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class TreeNNResultsContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Configuration){
			Configuration p_el=(Configuration)parentElement;
			return p_el.getNetworkArchitectures().toArray();
			
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof NetworkArchitecture){
			NetworkArchitecture arch=(NetworkArchitecture)element;
			return arch.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Configuration){
			Configuration el=(Configuration)element;
			return el.getNetworkArchitectures().size()>0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Configuration){
			return this.getChildren(inputElement);
		}
		return null;
	}

}
