package com.munch.exchange.wizard.parameter.architecture;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.munch.exchange.model.core.optimization.AlgorithmParameters;

public class ArchitectureOptimizationAlgorithmWizardPage extends WizardPage {
	
	private static Logger logger = Logger.getLogger(ArchitectureOptimizationAlgorithmWizardPage.class);
	
	private AlgorithmParameters<Boolean> optArchitectureParam;
	
	private Combo comboAlgorithmType;
	private Spinner spinnerNumberOfSteps;
	private Spinner spinnerMaxDimension;
	private Spinner spinnerMinDimension;

	/**
	 * Create the wizard.
	 */
	public ArchitectureOptimizationAlgorithmWizardPage(AlgorithmParameters<Boolean> optArchitectureParam) {
		super("wizardPage");
		setTitle("Algorithm Selection");
		setDescription("Please select the optimization algorithm");
		
		
		this.optArchitectureParam=optArchitectureParam;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblAlgorithmType = new Label(container, SWT.NONE);
		lblAlgorithmType.setText("Algorithm type:");
		
		comboAlgorithmType = new Combo(container, SWT.NONE);
		comboAlgorithmType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		comboAlgorithmType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboAlgorithmType.add(AlgorithmParameters.ALGORITHM_Simple_Generational_EA);
		comboAlgorithmType.setText(AlgorithmParameters.ALGORITHM_Simple_Generational_EA);
		if(!optArchitectureParam.getType().isEmpty()){
			comboAlgorithmType.setText(optArchitectureParam.getType());
		}
		
		Label lblDimension = new Label(container, SWT.NONE);
		lblDimension.setText("Nb of inner neurons:");
		new Label(container, SWT.NONE);
		
		Label lblMin = new Label(container, SWT.NONE);
		lblMin.setText("Min:");
		
		spinnerMinDimension = new Spinner(container, SWT.BORDER);
		spinnerMinDimension.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(spinnerMaxDimension.getSelection()<spinnerMinDimension.getSelection()){
					spinnerMaxDimension.setSelection(spinnerMinDimension.getSelection());
				}
				
				saveParameters();
				
			}
		});
		spinnerMinDimension.setMaximum(1000);
		spinnerMinDimension.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MinDimension)){
			spinnerMinDimension.setSelection(optArchitectureParam.getIntegerParam(AlgorithmParameters.MinDimension));
		}
		
		Label lblMax = new Label(container, SWT.NONE);
		lblMax.setText("Max");
		
		spinnerMaxDimension = new Spinner(container, SWT.BORDER);
		spinnerMaxDimension.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(spinnerMaxDimension.getSelection()<spinnerMinDimension.getSelection()){
					spinnerMinDimension.setSelection(spinnerMaxDimension.getSelection());
				}
				
				saveParameters();
			}
		});
		spinnerMaxDimension.setMaximum(1000);
		spinnerMaxDimension.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MaxDimension)){
			spinnerMaxDimension.setSelection(optArchitectureParam.getIntegerParam(AlgorithmParameters.MaxDimension));
		}
		
		Label lblTerminationCriterion = new Label(container, SWT.NONE);
		lblTerminationCriterion.setText("Termination Criterion:");
		new Label(container, SWT.NONE);
		
		Label lblNumberOfSteps = new Label(container, SWT.NONE);
		lblNumberOfSteps.setText("Number of Steps:");
		
		spinnerNumberOfSteps = new Spinner(container, SWT.BORDER);
		spinnerNumberOfSteps.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerNumberOfSteps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerNumberOfSteps.setIncrement(1);
		spinnerNumberOfSteps.setMaximum(200);
		spinnerNumberOfSteps.setMinimum(1);
		spinnerNumberOfSteps.setSelection(5);
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.TERMINATION_Steps)){
			spinnerNumberOfSteps.setSelection(optArchitectureParam.getIntegerParam(AlgorithmParameters.TERMINATION_Steps));
		}
		
		saveParameters();
	}
	
	
	private void saveParameters(){
		optArchitectureParam.setType(comboAlgorithmType.getText());
		optArchitectureParam.setParam(AlgorithmParameters.TERMINATION_Steps, spinnerNumberOfSteps.getSelection());
		optArchitectureParam.setParam(AlgorithmParameters.MaxDimension, spinnerMaxDimension.getSelection());
		optArchitectureParam.setParam(AlgorithmParameters.MinDimension, spinnerMinDimension.getSelection());
		
	}

}
