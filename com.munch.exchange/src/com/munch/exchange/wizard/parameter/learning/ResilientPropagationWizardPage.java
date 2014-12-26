package com.munch.exchange.wizard.parameter.learning;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;

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

public class ResilientPropagationWizardPage extends WizardPage {
	
	private LearnParameters param;
	
	private double scalar=100.0d;

	protected ResilientPropagationWizardPage(LearnParameters param) {
		super("Resilient Propagation Page");
		setTitle("Resilient Propagation");
		setDescription("Please set the learning rate and the resilient parameters");
		
		this.param=param;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		container.setLayout(new GridLayout(3, false));
		
		addDoubleParam("Learning Rate:",LearnParameters.IL_LearningRate,10,1,100,scalar,container);
		
		addDoubleParam("Decrease Factor:",LearnParameters.RP_DecreaseFactor,50,1,100,scalar,container);
		addDoubleParam("Increase Factor:",LearnParameters.RP_IncreaseFactor,120,101,200,scalar,container);
		addDoubleParam("Initial Delta:",LearnParameters.RP_InitialDelta,10,1,100,scalar,container);
		addDoubleParam("Max Delta:",LearnParameters.RP_MaxDelta,100,1,110,scalar,container);
		addDoubleParam("Min Delta:",LearnParameters.RP_MinDelta,1,1,1000,1000000,container);
		
	}
	
	
	private void addDoubleParam(String title,String key, int selection,int min, int max, double scalar, Composite container){
		
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText(title);
		
		Slider slider = new Slider(container, SWT.NONE);
		
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setSelection(selection);
		if(param.hasParamKey(key)){
			slider.setSelection((int) 
					(scalar*param.getDoubleParam(key)));
		}
		
		Label lblValue = new Label(container, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		DoubleParamSelectionAdapter selAdapter=new DoubleParamSelectionAdapter(slider,lblValue,key,scalar);
		slider.addSelectionListener(selAdapter);
		selAdapter.widgetSelected(null);
		
		
	}
	
	
	class DoubleParamSelectionAdapter extends SelectionAdapter{
		
		
		private Slider slider;
		private Label lblValue;
		private String key;
		private double scalar;
		
		
		
		public DoubleParamSelectionAdapter(Slider slider, Label lblValue,
				String key, double scalar) {
			super();
			this.slider = slider;
			this.lblValue = lblValue;
			this.key = key;
			this.scalar = scalar;
		}



		@Override
		public void widgetSelected(SelectionEvent e) {
			param.setParam(key, (double)slider.getSelection()/scalar);
			lblValue.setText(String.format("%1$,.6f", (double)slider.getSelection()/scalar ));
		}
		
	}
	

}
