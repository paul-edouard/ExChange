package com.munch.exchange.wizard;

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

public class OptEvoStrategyWizardPage extends WizardPage {
	
	public static String SELECTION_ALGORITHM_Tournament="Tournament";
	public static String SELECTION_ALGORITHM_Random="Random";
	
	public static String NULLARY_SEARCH_OPERATION_Uniform_Creation="Uniform Creation";
	
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
	public OptEvoStrategyWizardPage() {
		super("wizardPage");
		setMessage("Please enter the algorithm parameters");
		setTitle("Evolution Strategy");
		setDescription("Wizard Page description");
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
		comboNullarySearchOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboNullarySearchOperation.setText(NULLARY_SEARCH_OPERATION_Uniform_Creation);
		comboNullarySearchOperation.add(NULLARY_SEARCH_OPERATION_Uniform_Creation);
		new Label(container, SWT.NONE);
		
		Label lblTotalPopulationSize = new Label(container, SWT.NONE);
		lblTotalPopulationSize.setText("Total population size: [Mu]");
		
		spinnerTotalPopulationSize = new Spinner(container, SWT.BORDER);
		spinnerTotalPopulationSize.setIncrement(100);
		spinnerTotalPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerTotalPopulationSize.setPageIncrement(100);
		spinnerTotalPopulationSize.setMaximum(1000000);
		spinnerTotalPopulationSize.setMinimum(10);
		spinnerTotalPopulationSize.setSelection(1000);
		new Label(container, SWT.NONE);
		
		Label lblNumberOfOffspring = new Label(container, SWT.NONE);
		lblNumberOfOffspring.setText("Number of offspring: [Lambda]");
		
		spinnerNumberOfOffspring = new Spinner(container, SWT.BORDER);
		spinnerNumberOfOffspring.setIncrement(10);
		spinnerNumberOfOffspring.setMaximum(1000000);
		spinnerNumberOfOffspring.setMinimum(10);
		spinnerNumberOfOffspring.setSelection(100);
		spinnerNumberOfOffspring.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblNumberOfParentPer = new Label(container, SWT.NONE);
		lblNumberOfParentPer.setText("Number of parent per offspring: [Rho]");
		
		spinnerNumberOfParetnPerOffspring = new Spinner(container, SWT.BORDER);
		spinnerNumberOfParetnPerOffspring.setMaximum(1000);
		spinnerNumberOfParetnPerOffspring.setMinimum(1);
		spinnerNumberOfParetnPerOffspring.setSelection(50);
		spinnerNumberOfParetnPerOffspring.setIncrement(5);
		spinnerNumberOfParetnPerOffspring.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnLambdaPlusMuStrategy = new Button(container, SWT.CHECK);
		btnLambdaPlusMuStrategy.setText("(lambda+mu) strategy ");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblSelectionAlgorithm = new Label(container, SWT.NONE);
		lblSelectionAlgorithm.setText("Selection Algorithm:");
		
		comboSelectionAlgorithm = new Combo(container, SWT.NONE);
		comboSelectionAlgorithm.setText(SELECTION_ALGORITHM_Tournament);
		comboSelectionAlgorithm.add(SELECTION_ALGORITHM_Tournament);
		comboSelectionAlgorithm.add(SELECTION_ALGORITHM_Random);
		comboSelectionAlgorithm.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				spinnerTournamentSize.setEnabled(comboSelectionAlgorithm.getText().equals(SELECTION_ALGORITHM_Tournament));
			}
		});
		comboSelectionAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		spinnerTournamentSize = new Spinner(container, SWT.BORDER);
		spinnerTournamentSize.setMinimum(1);
		spinnerTournamentSize.setSelection(3);
	}

	public Spinner getSpinnerTotalPopulationSize() {
		return spinnerTotalPopulationSize;
	}

	public Spinner getSpinnerNumberOfOffspring() {
		return spinnerNumberOfOffspring;
	}

	public Spinner getSpinnerNumberOfParetnPerOffspring() {
		return spinnerNumberOfParetnPerOffspring;
	}

	public Button getBtnLambdaPlusMuStrategy() {
		return btnLambdaPlusMuStrategy;
	}

	public Combo getComboSelectionAlgorithm() {
		return comboSelectionAlgorithm;
	}

	public Spinner getSpinnerTournamentSize() {
		return spinnerTournamentSize;
	}

	public Combo getComboNullarySearchOperation() {
		return comboNullarySearchOperation;
	}
	
	
	
	
}
