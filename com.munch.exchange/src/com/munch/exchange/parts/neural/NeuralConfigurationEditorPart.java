 
package com.munch.exchange.parts.neural;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;

import com.munch.exchange.parts.chart.ChartEditorPart;

public class NeuralConfigurationEditorPart {
	
	
	
	private static Logger logger = Logger.getLogger(NeuralConfigurationEditorPart.class);
	
	public static final String EDITOR_ID="com.munch.exchange.partdescriptor.neuralconfiguration.editor";
	
	
	
	@Inject
	public NeuralConfigurationEditorPart() {
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