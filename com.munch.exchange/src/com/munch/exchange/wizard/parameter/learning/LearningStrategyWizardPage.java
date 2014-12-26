package com.munch.exchange.wizard.parameter.learning;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class LearningStrategyWizardPage extends WizardPage {
	
	
	
	private Combo comboAlgorithmType;
	private Spinner spinnerNumberOfIterations;
	
	private LearnParameters param;
	private Button btnBatchMode;
	
	private ResilientPropagationWizardPage rp_page;
	private MomentumBackPropagationWizardPage mbp_page;
	

	/**
	 * Create the wizard.
	 */
	public LearningStrategyWizardPage(LearnParameters param,
							ResilientPropagationWizardPage rp_page,
							MomentumBackPropagationWizardPage mbp_page) {
		super("wizardPage");
		setTitle("Learning Strategy Selection");
		setDescription("Please select the learning strategy");
		
		this.rp_page=rp_page;
		this.mbp_page=mbp_page;
		this.param=param;
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
		comboAlgorithmType.add(LearnParameters.MOMENTUM_BACK_PROPAGATION);
		comboAlgorithmType.add(LearnParameters.RESILIENT_PROPAGATION);
		comboAlgorithmType.setText(LearnParameters.MOMENTUM_BACK_PROPAGATION);
		if(!param.getType().isEmpty())
			comboAlgorithmType.setText(param.getType());
		
		
		Label lblTerminationCriterion = new Label(container, SWT.NONE);
		lblTerminationCriterion.setText("Termination Criterion:");
		new Label(container, SWT.NONE);
		
		Label lblNumberOfSteps = new Label(container, SWT.NONE);
		lblNumberOfSteps.setText("Number of Iterations:");
		
		spinnerNumberOfIterations = new Spinner(container, SWT.BORDER);
		spinnerNumberOfIterations.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerNumberOfIterations.setIncrement(1);
		spinnerNumberOfIterations.setMaximum(1000);
		spinnerNumberOfIterations.setMinimum(1);
		spinnerNumberOfIterations.setSelection(3);
		if(param.hasParamKey(LearnParameters.Max_Iterations)){
			spinnerNumberOfIterations.setSelection(param.getIntegerParam(LearnParameters.Max_Iterations));
		}
		
		Label lblBatchMode = new Label(container, SWT.NONE);
		lblBatchMode.setText("Batch Mode:");
		
		btnBatchMode = new Button(container, SWT.CHECK);
		btnBatchMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		btnBatchMode.setSelection(true);
		if(param.hasParamKey(LearnParameters.BatchMode)){
			btnBatchMode.setSelection(param.getBooleanParam(LearnParameters.BatchMode));
		}
		
		
		saveParameters();
	}
	
	
	private void saveParameters(){
		param.setType(comboAlgorithmType.getText());
		param.setParam(LearnParameters.Max_Iterations, spinnerNumberOfIterations.getSelection());
		param.setParam(LearnParameters.BatchMode, btnBatchMode.getSelection());
	}

	@Override
	public IWizardPage getNextPage() {
		if(comboAlgorithmType.getText().equals(LearnParameters.RESILIENT_PROPAGATION)){
			return this.rp_page;
		}
		else if(comboAlgorithmType.getText().equals(LearnParameters.MOMENTUM_BACK_PROPAGATION)){
			return this.mbp_page;
		}
		
		// TODO Auto-generated method stub
		return super.getNextPage();
	}
	
	
	
	

}
