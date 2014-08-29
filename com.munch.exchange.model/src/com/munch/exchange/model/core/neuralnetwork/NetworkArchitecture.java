package com.munch.exchange.model.core.neuralnetwork;

import java.util.LinkedList;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class NetworkArchitecture extends XmlParameterElement {
	
	
	
	private int numberOfInnerNeurons;
	private boolean[] activatedConnections;
	private int maxNumberOfSavedNetworks=50;
	private String networksSavePath;
	
	LinkedList<org.neuroph.core.NeuralNetwork> networks=new LinkedList<org.neuroph.core.NeuralNetwork>();

	@Override
	protected void initAttribute(Element rootElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub
		
	}

}
