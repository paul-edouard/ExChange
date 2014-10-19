package com.munch.exchange.wizard.parameter.optimization;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class EvolutionStrategyWizardPage extends WizardPage {
	
	private AlgorithmParameters<double[]> optLearnParam;
	
	private Spinner spinnerTotalPopulationSize;
	private Spinner spinnerNumberOfOffspring;
	private Spinner spinnerNumberOfParetnPerOffspring;
	private Button btnLambdaPlusMuStrategy;
	private Combo comboSelectionAlgorithm;
	private Spinner spinnerTournamentSize;
	private Combo comboNullarySearchOperation;

	/**
	 * Create the wizard.
	 */
	public EvolutionStrategyWizardPage(AlgorithmParameters<double[]> optLearnParam) {
		super("wizardPage");
		setTitle("Evolution Strategy");
		setDescription("Please set the evolution strategy parameters");
		
		this.optLearnParam=optLearnParam;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lblNullarySearchOperation = new Label(container, SWT.NONE);
		lblNullarySearchOperation.setText("Nullary search operation (Creation)");
		
		comboNullarySearchOperation = new Combo(container, SWT.NONE);
		comboNullarySearchOperation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		comboNullarySearchOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboNullarySearchOperation.setText(AlgorithmParameters.NSO_Uniform_Creation);
		comboNullarySearchOperation.add(AlgorithmParameters.NSO_Uniform_Creation);
		if(optLearnParam.hasParamKey(AlgorithmParameters.NULLARY_SEARCH_OPERATION)){
			comboNullarySearchOperation.setText(optLearnParam.getStringParam(AlgorithmParameters.NULLARY_SEARCH_OPERATION));
		}
		new Label(container, SWT.NONE);
		
		Label lblTotalPopulationSize = new Label(container, SWT.NONE);
		lblTotalPopulationSize.setText("Total population size: [Mu]");
		
		spinnerTotalPopulationSize = new Spinner(container, SWT.BORDER);
		spinnerTotalPopulationSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerTotalPopulationSize.setIncrement(10);
		spinnerTotalPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerTotalPopulationSize.setMaximum(1000);
		spinnerTotalPopulationSize.setMinimum(1);
		spinnerTotalPopulationSize.setSelection(30);
		if(optLearnParam.hasParamKey(AlgorithmParameters.ES_Mu)){
			spinnerTotalPopulationSize.setSelection(optLearnParam.getIntegerParam(AlgorithmParameters.ES_Mu));
		}
		
		new Label(container, SWT.NONE);
		
		Label lblNumberOfOffspring = new Label(container, SWT.NONE);
		lblNumberOfOffspring.setText("Number of offspring: [Lambda]");
		
		spinnerNumberOfOffspring = new Spinner(container, SWT.BORDER);
		spinnerNumberOfOffspring.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerNumberOfOffspring.setIncrement(1);
		spinnerNumberOfOffspring.setMaximum(200);
		spinnerNumberOfOffspring.setMinimum(1);
		spinnerNumberOfOffspring.setSelection(10);
		spinnerNumberOfOffspring.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optLearnParam.hasParamKey(AlgorithmParameters.ES_Lambda)){
			spinnerNumberOfOffspring.setSelection(optLearnParam.getIntegerParam(AlgorithmParameters.ES_Lambda));
		}
		new Label(container, SWT.NONE);
		
		Label lblNumberOfParentPer = new Label(container, SWT.NONE);
		lblNumberOfParentPer.setText("Number of parent per offspring: [Rho]");
		
		spinnerNumberOfParetnPerOffspring = new Spinner(container, SWT.BORDER);
		spinnerNumberOfParetnPerOffspring.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerNumberOfParetnPerOffspring.setMaximum(200);
		spinnerNumberOfParetnPerOffspring.setMinimum(1);
		spinnerNumberOfParetnPerOffspring.setSelection(10);
		spinnerNumberOfParetnPerOffspring.setIncrement(1);
		spinnerNumberOfParetnPerOffspring.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optLearnParam.hasParamKey(AlgorithmParameters.ES_Rho)){
			spinnerNumberOfParetnPerOffspring.setSelection(optLearnParam.getIntegerParam(AlgorithmParameters.ES_Rho));
		}
		new Label(container, SWT.NONE);
		
		btnLambdaPlusMuStrategy = new Button(container, SWT.CHECK);
		btnLambdaPlusMuStrategy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		btnLambdaPlusMuStrategy.setSelection(true);
		btnLambdaPlusMuStrategy.setText("(lambda+mu) strategy ");
		if(optLearnParam.hasParamKey(AlgorithmParameters.ES_Plus)){
			btnLambdaPlusMuStrategy.setSelection(optLearnParam.getBooleanParam(AlgorithmParameters.ES_Plus));
		}
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblSelectionAlgorithm = new Label(container, SWT.NONE);
		lblSelectionAlgorithm.setText("Selection Algorithm:");
		
		comboSelectionAlgorithm = new Combo(container, SWT.NONE);
		comboSelectionAlgorithm.setText(AlgorithmParameters.SELECTION_ALGORITHM_Tournament);
		comboSelectionAlgorithm.add(AlgorithmParameters.SELECTION_ALGORITHM_Tournament);
		comboSelectionAlgorithm.add(AlgorithmParameters.SELECTION_ALGORITHM_Random);
		comboSelectionAlgorithm.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				spinnerTournamentSize.setEnabled(comboSelectionAlgorithm.getText().equals(AlgorithmParameters.SELECTION_ALGORITHM_Tournament));
				saveParameters();
			}
		});
		comboSelectionAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		
		spinnerTournamentSize = new Spinner(container, SWT.BORDER);
		spinnerTournamentSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerTournamentSize.setMinimum(1);
		spinnerTournamentSize.setSelection(3);
		if(optLearnParam.hasParamKey(AlgorithmParameters.Tournament_Size)){
			spinnerTournamentSize.setSelection(optLearnParam.getIntegerParam(AlgorithmParameters.Tournament_Size));
		}
		if(optLearnParam.hasParamKey(AlgorithmParameters.SELECTION_ALGORITHM)){
			comboSelectionAlgorithm.setText(optLearnParam.getStringParam(AlgorithmParameters.SELECTION_ALGORITHM));
		}
		
		saveParameters();
		
	}
	
	
	private void saveParameters(){
		optLearnParam.setParam(AlgorithmParameters.SELECTION_ALGORITHM, comboSelectionAlgorithm.getText());
		optLearnParam.setParam(AlgorithmParameters.Tournament_Size, spinnerTournamentSize.getSelection());
		
		optLearnParam.setParam(AlgorithmParameters.ES_Plus, btnLambdaPlusMuStrategy.getSelection());
		optLearnParam.setParam(AlgorithmParameters.ES_Rho, spinnerNumberOfParetnPerOffspring.getSelection());
		
		optLearnParam.setParam(AlgorithmParameters.ES_Lambda, spinnerNumberOfOffspring.getSelection());
		optLearnParam.setParam(AlgorithmParameters.ES_Mu, spinnerTotalPopulationSize.getSelection());
		optLearnParam.setParam(AlgorithmParameters.NULLARY_SEARCH_OPERATION, comboNullarySearchOperation.getText());
		
	}
	

}
