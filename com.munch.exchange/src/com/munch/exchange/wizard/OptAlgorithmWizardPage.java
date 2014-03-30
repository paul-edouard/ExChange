package com.munch.exchange.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class OptAlgorithmWizardPage extends WizardPage {
	
	
	public static String ALGORITHM_Evolution_Strategy="Evolution Strategy";
	
	private Combo comboAlgorithmType;
	private Spinner spinnerNumberOfSteps;

	/**
	 * Create the wizard.
	 */
	public OptAlgorithmWizardPage() {
		super("wizardPage");
		setTitle("Algorithm Selection");
		setDescription("Please select the optimization algorithm");
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
		comboAlgorithmType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboAlgorithmType.add(ALGORITHM_Evolution_Strategy);
		comboAlgorithmType.setText(ALGORITHM_Evolution_Strategy);
		
		Label lblTerminationCriterion = new Label(container, SWT.NONE);
		lblTerminationCriterion.setText("Termination Criterion:");
		new Label(container, SWT.NONE);
		
		Label lblNumberOfSteps = new Label(container, SWT.NONE);
		lblNumberOfSteps.setText("Number of Steps:");
		
		spinnerNumberOfSteps = new Spinner(container, SWT.BORDER);
		spinnerNumberOfSteps.setIncrement(50);
		spinnerNumberOfSteps.setMaximum(100000);
		spinnerNumberOfSteps.setMinimum(1);
		spinnerNumberOfSteps.setSelection(500);
	}

	public Combo getComboAlgorithmType() {
		return comboAlgorithmType;
	}

	public Spinner getSpinnerNumberOfSteps() {
		return spinnerNumberOfSteps;
	}
	
	

}
