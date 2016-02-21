package com.munch.exchange.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;

import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.TrainingMethod;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class TrainNeuralArchitectureDialog extends TitleAreaDialog {
	
	private boolean allowSimulatedAnnealing=true;
	private TrainingMethod trainingMethod; 
	private int population;
	private int cycles;
	private int stopTemperature;
	private int startTemperature;
	private int nbOfEpoch;
	
	private Combo comboTrainingMethod;
	private Spinner spinnerPopulation;
	private Spinner spinnerCycles;
	private Spinner spinnerStopTemperature;
	private Spinner spinnerStartTemperature;
	private Spinner spinnerEpoch;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TrainNeuralArchitectureDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public TrainNeuralArchitectureDialog(Shell parentShell, boolean allowSimulatedAnnealing) {
		super(parentShell);
		
		this.allowSimulatedAnnealing=allowSimulatedAnnealing;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Please set the training setting before starting");
		setTitle("Training Setting");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblTrainingMethod = new Label(container, SWT.NONE);
		lblTrainingMethod.setText("Training Method:");
		
		comboTrainingMethod = new Combo(container, SWT.NONE);
		comboTrainingMethod.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				TrainingMethod[] methodes=NeuralArchitecture.TrainingMethod.values();
				TrainingMethod methode=methodes[comboTrainingMethod.getSelectionIndex()];
				
				switch (methode) {
				case GENETIC_ALGORITHM:
					spinnerCycles.setEnabled(false);
					spinnerPopulation.setEnabled(true);
					spinnerStartTemperature.setEnabled(false);
					spinnerStopTemperature.setEnabled(false);
					break;
					
					

				case SIMULATED_ANNEALING:
					spinnerCycles.setEnabled(true);
					spinnerPopulation.setEnabled(false);
					spinnerStartTemperature.setEnabled(true);
					spinnerStopTemperature.setEnabled(true);
					
					break;
				}
				
			}
		});
		comboTrainingMethod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblStartTemperature = new Label(container, SWT.NONE);
		lblStartTemperature.setText("Start Temperature:");
		
		spinnerStartTemperature = new Spinner(container, SWT.BORDER);
		spinnerStartTemperature.setEnabled(false);
		spinnerStartTemperature.setPageIncrement(1);
		spinnerStartTemperature.setMinimum(1);
		spinnerStartTemperature.setSelection(10);
		spinnerStartTemperature.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblStopTemperature = new Label(container, SWT.NONE);
		lblStopTemperature.setText("Stop Temperature:");
		
		spinnerStopTemperature = new Spinner(container, SWT.BORDER);
		spinnerStopTemperature.setEnabled(false);
		spinnerStopTemperature.setPageIncrement(1);
		spinnerStopTemperature.setMinimum(1);
		spinnerStopTemperature.setSelection(2);
		spinnerStopTemperature.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblCycles = new Label(container, SWT.NONE);
		lblCycles.setText("Cycles:");
		
		spinnerCycles = new Spinner(container, SWT.BORDER);
		spinnerCycles.setEnabled(false);
		spinnerCycles.setIncrement(10);
		spinnerCycles.setMaximum(1000);
		spinnerCycles.setMinimum(10);
		spinnerCycles.setSelection(100);
		spinnerCycles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPopulation = new Label(container, SWT.NONE);
		lblPopulation.setText("Population:");
		
		spinnerPopulation = new Spinner(container, SWT.BORDER);
		spinnerPopulation.setIncrement(100);
		spinnerPopulation.setPageIncrement(100);
		spinnerPopulation.setMaximum(10000);
		spinnerPopulation.setMinimum(50);
		spinnerPopulation.setSelection(500);
		spinnerPopulation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblEpoch = new Label(container, SWT.NONE);
		lblEpoch.setText("Nb. of Epoch:");
		
		spinnerEpoch = new Spinner(container, SWT.BORDER);
		spinnerEpoch.setIncrement(10);
		spinnerEpoch.setMaximum(1000);
		spinnerEpoch.setMinimum(1);
		spinnerEpoch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		fillCombos();
		
		return area;
	}
	
	private void fillCombos(){
		
		TrainingMethod[] methodes=NeuralArchitecture.TrainingMethod.values();
		for(TrainingMethod methode:methodes){
			comboTrainingMethod.add(methode.toString());
		}
		comboTrainingMethod.select(0);
		
		if(!allowSimulatedAnnealing){
			comboTrainingMethod.setEnabled(false);
		}
	}
	
	

	@Override
	protected void okPressed() {
		
		TrainingMethod[] methodes=NeuralArchitecture.TrainingMethod.values();
		trainingMethod=methodes[comboTrainingMethod.getSelectionIndex()];
		
		
		population = spinnerPopulation.getSelection();
		cycles = spinnerCycles.getSelection();
		stopTemperature = spinnerStopTemperature.getSelection();
		startTemperature = spinnerStartTemperature.getSelection();
		nbOfEpoch = spinnerEpoch.getSelection();
		
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(468, 522);
	}

	public int getCycles() {
		return cycles;
	}

	public void setCycles(int cycles) {
		this.cycles = cycles;
	}

	public int getNbOfEpoch() {
		return nbOfEpoch;
	}

	public void setNbOfEpoch(int nbOfEpoch) {
		this.nbOfEpoch = nbOfEpoch;
	}

	public TrainingMethod getTrainingMethod() {
		return trainingMethod;
	}

	public int getPopulation() {
		return population;
	}

	public int getStopTemperature() {
		return stopTemperature;
	}

	public int getStartTemperature() {
		return startTemperature;
	}
	
	
	

}
