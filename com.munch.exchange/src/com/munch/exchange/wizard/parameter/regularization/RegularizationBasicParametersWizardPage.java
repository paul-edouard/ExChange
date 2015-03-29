package com.munch.exchange.wizard.parameter.regularization;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

import com.munch.exchange.model.core.neuralnetwork.RegularizationParameters;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.wizard.parameter.architecture.SimpleGenerationalEAWizardPage;

public class RegularizationBasicParametersWizardPage extends WizardPage {
	
	
	private static Logger logger = Logger.getLogger(RegularizationBasicParametersWizardPage.class);
	
	private RegularizationParameters param;
	
	private double scalar=1000.0d;
	
	private Slider sliderVarianz;
	private Label lblVarianzVal;
	
	
	protected RegularizationBasicParametersWizardPage(RegularizationParameters param) {
		super("wizardPage");
		setTitle("Regularization Basic Parameters");
		setDescription("Please set the basic parameters");
		
		this.param=param;
		
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		
		Label lblVarianz = new Label(container, SWT.NONE);
		lblVarianz.setText("FaMe Neuron Varianz:");
		
		sliderVarianz = new Slider(container, SWT.NONE);
		sliderVarianz.setPageIncrement(1);
		sliderVarianz.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		sliderVarianz.setMinimum(1);
		sliderVarianz.setMaximum(501);
		sliderVarianz.setSelection(50);
		sliderVarianz.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(param.hasParamKey(RegularizationParameters.VARIANZ)){
			sliderVarianz.setSelection((int) 
					(scalar*param.getDoubleParam(RegularizationParameters.VARIANZ)));
		}
		
		
		lblVarianzVal = new Label(container, SWT.NONE);
		lblVarianzVal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		saveParameters();
		
	}
	
	private void saveParameters(){
		
		param.setParam(RegularizationParameters.VARIANZ, (double) sliderVarianz.getSelection()/scalar);
		
		lblVarianzVal.setText(String.format("%1$,.3f", (double)sliderVarianz.getSelection()/scalar ));
		
		
	}

}
