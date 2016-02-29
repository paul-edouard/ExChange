package com.munch.exchange.parts.neural;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralNetwork;
import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public class NeuralConfiguationArchitectureContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	
	
	public NeuralConfiguationArchitectureContentProvider() {
		super();
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
			return neuralConfiguration.getNeuralArchitectures().toArray();
		}
		else if(parentElement instanceof NeuralArchitecture){
			NeuralArchitecture neuralArchitecture=(NeuralArchitecture) parentElement;
			return neuralArchitecture.getNeuralNetworks().toArray();
		}
		else if(parentElement instanceof NeuralNetwork){
			NeuralNetwork neuralNetwork=(NeuralNetwork) parentElement;
//			if(!neuralNetwork.isNEAT()){
				if(neuralNetwork.getTrainingRating().getChildren().isEmpty())
					return null;
				if(neuralNetwork.getBackTestingRating().getChildren().isEmpty())
					return null;
				
				Object[] objects=new Object[2];
				objects[0]=neuralNetwork.getTrainingRating();
				objects[1]=neuralNetwork.getBackTestingRating();
				return objects;
//			}
		}
		else if(parentElement instanceof NeuralNetworkRating){
			NeuralNetworkRating rating=(NeuralNetworkRating)parentElement;
			return rating.getChildren().toArray();
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
			return !neuralConfiguration.getNeuralArchitectures().isEmpty();
		}
		else if(element instanceof NeuralArchitecture){
			NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
			return !neuralArchitecture.getNeuralNetworks().isEmpty();
		}
		else if(element instanceof NeuralNetwork){
			NeuralNetwork neuralNetwork=(NeuralNetwork) element;
//			if(!neuralNetwork.isNEAT()){
				if(neuralNetwork.getTrainingRating().getChildren().isEmpty())
					return false;
				if(neuralNetwork.getBackTestingRating().getChildren().isEmpty())
					return false;
				return true;
//			}
		}
		else if(element instanceof NeuralNetworkRating){
			NeuralNetworkRating rating=(NeuralNetworkRating)element;
			return !rating.getChildren().isEmpty();
		}
		
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof NeuralConfiguration ||
				inputElement instanceof NeuralArchitecture ||
				inputElement instanceof NeuralNetwork ||
				inputElement instanceof NeuralNetworkRating ){
			return getChildren(inputElement);
		}
		return null;
	}

}
