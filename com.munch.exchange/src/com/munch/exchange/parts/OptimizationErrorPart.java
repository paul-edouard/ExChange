 
package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class OptimizationErrorPart {
	
	private static Logger logger = Logger.getLogger(OptimizationErrorPart.class);
	
	public static final String OPTIMIZATION_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.optimizationerroreditor";
	
	
	@Inject
	public OptimizationErrorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Label lblTest = new Label(parent, SWT.NONE);
		lblTest.setText("Test");
		//TODO Your code here
	}
	
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	
}