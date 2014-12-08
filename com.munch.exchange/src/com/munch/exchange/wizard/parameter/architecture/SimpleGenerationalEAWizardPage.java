package com.munch.exchange.wizard.parameter.architecture;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import org.eclipse.swt.widgets.Slider;

public class SimpleGenerationalEAWizardPage extends WizardPage {
	
	private static Logger logger = Logger.getLogger(SimpleGenerationalEAWizardPage.class);
	
	
	private AlgorithmParameters<boolean[]> optArchitectureParam;
	
	private double scalar=100.0d;
	
	private Spinner spinnerPopulationSize;
	private Spinner spinnerMatingPoolSize;
	private Combo comboSelectionAlgorithm;
	private Spinner spinnerTournamentSize;
	private Combo comboNullarySearchOperation;
	private Combo comboBinarySearchOpe;
	private Combo comboUnarySearchOpe;
	private Slider sliderMutationRate;
	private Slider sliderCrossoverRate;
	private Label lblMutationRateVal;
	private Label lblCrossoverRateVal;

	/**
	 * Create the wizard.
	 */
	public SimpleGenerationalEAWizardPage(AlgorithmParameters<boolean[]> optArchitectureParam) {
		super("wizardPage");
		setTitle("Algorithm Parameters");
		setDescription("Please select the algorithm parameters");
		
		this.optArchitectureParam=optArchitectureParam;
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
		comboNullarySearchOperation.setText(AlgorithmParameters.NSO_ValidRandomNetworkCreation);
		comboNullarySearchOperation.add(AlgorithmParameters.NSO_BooleanArrayUniformCreation);
		comboNullarySearchOperation.add(AlgorithmParameters.NSO_PyramidNetworkCreation);
		comboNullarySearchOperation.add(AlgorithmParameters.NSO_ValidRandomNetworkCreation);
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.NULLARY_SEARCH_OPERATION)){
			comboNullarySearchOperation.setText(optArchitectureParam.getStringParam(AlgorithmParameters.NULLARY_SEARCH_OPERATION));
		}
		new Label(container, SWT.NONE);
		
		Label lblBinarySearchOperation = new Label(container, SWT.NONE);
		lblBinarySearchOperation.setText("Binary search operation:");
		
		comboBinarySearchOpe = new Combo(container, SWT.NONE);
		comboBinarySearchOpe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		comboBinarySearchOpe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboBinarySearchOpe.setText(AlgorithmParameters.BSO_BooleanArrayUniformCrossover);
		comboBinarySearchOpe.add(AlgorithmParameters.BSO_BooleanArrayUniformCrossover);
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.BINARY_SEARCH_OPERATION)){
			comboBinarySearchOpe.setText(optArchitectureParam.getStringParam(AlgorithmParameters.BINARY_SEARCH_OPERATION));
		}
		
		new Label(container, SWT.NONE);
		
		Label lblUnarySearchOperation = new Label(container, SWT.NONE);
		lblUnarySearchOperation.setText("Unary search operation:");
		
		comboUnarySearchOpe = new Combo(container, SWT.NONE);
		comboUnarySearchOpe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		comboUnarySearchOpe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboUnarySearchOpe.setText(AlgorithmParameters.USO_BooleanArraySingleBitFlipMutation);
		comboUnarySearchOpe.add(AlgorithmParameters.USO_BooleanArraySingleBitFlipMutation);
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.UNARY_SEARCH_OPERATION)){
			comboUnarySearchOpe.setText(optArchitectureParam.getStringParam(AlgorithmParameters.UNARY_SEARCH_OPERATION));
		}
		new Label(container, SWT.NONE);
		
		Label lblTotalPopulationSize = new Label(container, SWT.NONE);
		lblTotalPopulationSize.setText("Population size:");
		
		spinnerPopulationSize = new Spinner(container, SWT.BORDER);
		spinnerPopulationSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerPopulationSize.setIncrement(10);
		spinnerPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerPopulationSize.setMaximum(1000);
		spinnerPopulationSize.setMinimum(1);
		spinnerPopulationSize.setSelection(30);
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.EA_PopulationSize)){
			spinnerPopulationSize.setSelection(optArchitectureParam.getIntegerParam(AlgorithmParameters.EA_PopulationSize));
		}
		
		new Label(container, SWT.NONE);
		
		Label lblMatingPoolSize = new Label(container, SWT.NONE);
		lblMatingPoolSize.setText("Mating pool size:");
		
		spinnerMatingPoolSize = new Spinner(container, SWT.BORDER);
		spinnerMatingPoolSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		spinnerMatingPoolSize.setIncrement(1);
		spinnerMatingPoolSize.setMaximum(200);
		spinnerMatingPoolSize.setMinimum(1);
		spinnerMatingPoolSize.setSelection(10);
		spinnerMatingPoolSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.EA_MatingPoolSize)){
			spinnerMatingPoolSize.setSelection(optArchitectureParam.getIntegerParam(AlgorithmParameters.EA_MatingPoolSize));
		}
		new Label(container, SWT.NONE);
		
		Label lblMutationRate = new Label(container, SWT.NONE);
		lblMutationRate.setText("Mutation rate:");
		
		sliderMutationRate = new Slider(container, SWT.NONE);
		sliderMutationRate.setPageIncrement(1);
		sliderMutationRate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		sliderMutationRate.setMinimum(1);
		sliderMutationRate.setSelection(50);
		sliderMutationRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.EA_MutationRate)){
			sliderMutationRate.setSelection((int) 
					(scalar*optArchitectureParam.getDoubleParam(AlgorithmParameters.EA_MutationRate)));
		}
		
		
		lblMutationRateVal = new Label(container, SWT.NONE);
		
		Label lblCrossoverRate = new Label(container, SWT.NONE);
		lblCrossoverRate.setText("Crossover rate:");
		
		sliderCrossoverRate = new Slider(container, SWT.NONE);
		sliderCrossoverRate.setPageIncrement(1);
		sliderCrossoverRate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveParameters();
			}
		});
		sliderCrossoverRate.setMinimum(1);
		sliderCrossoverRate.setSelection(50);
		sliderCrossoverRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.EA_CrossoverRate)){
			sliderCrossoverRate.setSelection((int) 
					(scalar*optArchitectureParam.getDoubleParam(AlgorithmParameters.EA_CrossoverRate)));
		}
		
		
		lblCrossoverRateVal = new Label(container, SWT.NONE);
		
		
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
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.Tournament_Size)){
			spinnerTournamentSize.setSelection(optArchitectureParam.getIntegerParam(AlgorithmParameters.Tournament_Size));
		}
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.SELECTION_ALGORITHM)){
			comboSelectionAlgorithm.setText(optArchitectureParam.getStringParam(AlgorithmParameters.SELECTION_ALGORITHM));
		}
		
		saveParameters();
	}
	
	private void saveParameters(){
		optArchitectureParam.setParam(AlgorithmParameters.SELECTION_ALGORITHM, comboSelectionAlgorithm.getText());
		
		optArchitectureParam.setParam(AlgorithmParameters.Tournament_Size, spinnerTournamentSize.getSelection());
		
		optArchitectureParam.setParam(AlgorithmParameters.EA_MatingPoolSize, spinnerMatingPoolSize.getSelection());
		optArchitectureParam.setParam(AlgorithmParameters.EA_PopulationSize, spinnerPopulationSize.getSelection());
		
		optArchitectureParam.setParam(AlgorithmParameters.NULLARY_SEARCH_OPERATION, comboNullarySearchOperation.getText());
		optArchitectureParam.setParam(AlgorithmParameters.BINARY_SEARCH_OPERATION, comboBinarySearchOpe.getText());
		optArchitectureParam.setParam(AlgorithmParameters.UNARY_SEARCH_OPERATION, comboUnarySearchOpe.getText());
		
		optArchitectureParam.setParam(AlgorithmParameters.EA_MutationRate, (double) sliderMutationRate.getSelection()/scalar);
		optArchitectureParam.setParam(AlgorithmParameters.EA_CrossoverRate, (double) sliderCrossoverRate.getSelection()/scalar);
		
		
		lblMutationRateVal.setText(String.format("%1$,.2f", (double)sliderMutationRate.getSelection()/scalar ));
		lblCrossoverRateVal.setText(String.format("%1$,.2f", (double)sliderCrossoverRate.getSelection()/scalar ));
		
	}

}
