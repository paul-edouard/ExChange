package com.munch.exchange.parts.neural;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;

public class NeuralConfigurationTrainingDataContentProvider implements
IStructuredContentProvider, ITreeContentProvider{
	
	private static Logger logger = Logger.getLogger(NeuralConfigurationTrainingDataContentProvider.class);
	

	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof NeuralConfiguration){
			NeuralConfiguration config=(NeuralConfiguration) parentElement;
			return config.getAllBlocks().toArray();
		}
		else if(parentElement instanceof List<?>){
			List<?> list=(List<?>)parentElement;
			return list.toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof NeuralConfiguration){
			NeuralConfiguration config=(NeuralConfiguration) element;
			return config.getAllBlocks().size()>0;
		}
		else if(element instanceof List<?>){
			List<?> list=(List<?>)element;
			return list.size()>0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof NeuralConfiguration || inputElement instanceof List<?>){
			return this.getChildren(inputElement);
		}
		return null;
	}

}
