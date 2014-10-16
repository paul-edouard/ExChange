package com.munch.exchange.wizard.parameter.learning;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.widgets.Slider;

public class MomentumBackPropagationWizardPage extends WizardPage {
	
	private LearnParameters param;
	
	private double scalar=100.0d;
	private Slider sliderLearningRate;
	private Label lblLearningRateValue;
	private Slider sliderMomentum;
	private Label lblMomentumValue;
	
	/**
	 * Create the wizard.
	 */
	public MomentumBackPropagationWizardPage(LearnParameters param) {
		super("wizardPage");
		setTitle("Momentum Back Propagation");
		setDescription("Please set the learning rate and the Momentum");
		
		this.param=param;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		container.setLayout(new GridLayout(3, false));
		
		Label lblLearningRate = new Label(container, SWT.NONE);
		lblLearningRate.setText("Learning Rate:");
		
		
		sliderLearningRate = new Slider(container, SWT.NONE);
		sliderLearningRate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		sliderLearningRate.setMinimum(1);
		sliderLearningRate.setSelection(10);
		if(param.hasParamKey(LearnParameters.IL_LearningRate)){
			sliderLearningRate.setSelection((int) 
					(scalar*param.getDoubleParam(LearnParameters.IL_LearningRate)));
		}
		
		lblLearningRateValue = new Label(container, SWT.NONE);
		lblLearningRateValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		Label lblMomentum = new Label(container, SWT.NONE);
		lblMomentum.setText("Momentum:");
		
		sliderMomentum = new Slider(container, SWT.NONE);
		sliderMomentum.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		sliderMomentum.setMinimum(1);
		sliderMomentum.setSelection(25);
		if(param.hasParamKey(LearnParameters.MBP_Momentum)){
			sliderMomentum.setSelection((int) 
					(scalar*param.getDoubleParam(LearnParameters.MBP_Momentum)));
		}
		
		lblMomentumValue = new Label(container, SWT.NONE);
		lblMomentumValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		saveParameters();
	}
	
	
	private void saveParameters(){
		param.setParam(LearnParameters.IL_LearningRate, (double)sliderLearningRate.getSelection()/scalar);
		param.setParam(LearnParameters.MBP_Momentum, (double)sliderMomentum.getSelection()/scalar);
		
		lblLearningRateValue.setText(String.format("%1$,.2f", (double)sliderLearningRate.getSelection()/scalar ));
		lblMomentumValue.setText(String.format("%1$,.2f", (double)sliderMomentum.getSelection()/scalar ));
		
	}
	
	

}
