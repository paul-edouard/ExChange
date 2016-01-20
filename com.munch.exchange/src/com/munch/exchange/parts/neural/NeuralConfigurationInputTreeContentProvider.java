package com.munch.exchange.parts.neural;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeriesGroup;

public class NeuralConfigurationInputTreeContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	private NeuralConfiguration neuralConfiguration;
	

	public NeuralConfigurationInputTreeContentProvider(
			NeuralConfiguration neuralConfiguration) {
		super();
		this.neuralConfiguration = neuralConfiguration;
	}

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
		if(parentElement instanceof NeuralConfiguration){
			NeuralConfiguration neuralConfiguration=(NeuralConfiguration) parentElement;
			return neuralConfiguration.getNeuralInputs().toArray();
		}
		else if(parentElement instanceof NeuralInput){
			NeuralInput neuralInput=(NeuralInput) parentElement;
			return neuralInput.getComponents().toArray();
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
			NeuralConfiguration neuralConfiguration=(NeuralConfiguration) element;
			return !neuralConfiguration.getNeuralInputs().isEmpty();
		}
		else if(element instanceof NeuralInput){
			NeuralInput neuralInput=(NeuralInput) element;
			return !neuralInput.getComponents().isEmpty();
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof NeuralConfiguration || inputElement instanceof NeuralInput){
			return getChildren(inputElement);
		}
		return null;
	}

}
