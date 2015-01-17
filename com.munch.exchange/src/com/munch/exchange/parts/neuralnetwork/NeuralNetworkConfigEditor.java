 
package com.munch.exchange.parts.neuralnetwork;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;

public class NeuralNetworkConfigEditor {
	
	
	
	public static final String CONFIG_EDITOR_ID="com.munch.exchange.partdescriptor.configeditor";
	
	
	@Inject
	public NeuralNetworkConfigEditor() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		//TODO Your code here
	}
	
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}
	
}