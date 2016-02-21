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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.Activation;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.ArchitectureType;

public class NeatTrainingDialog extends TitleAreaDialog {
	
	
	private int nbOfEpoch;
	private double connectionDensity;
	private int population;
	
	
	private Text textConnectionDensity;
	private Spinner spinnerPopulationSize;
	private Spinner spinnerNbOfEpoch;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public NeatTrainingDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Neat Training Setting");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblPopulationSize = new Label(container, SWT.NONE);
		lblPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPopulationSize.setText("Population Size:");
		
		spinnerPopulationSize = new Spinner(container, SWT.BORDER);
		spinnerPopulationSize.setIncrement(50);
		spinnerPopulationSize.setMaximum(10000);
		spinnerPopulationSize.setMinimum(100);
		spinnerPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblConnectionDensity = new Label(container, SWT.NONE);
		lblConnectionDensity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConnectionDensity.setText("Connection Density:");
		
		textConnectionDensity = new Text(container, SWT.BORDER);
		textConnectionDensity.setText("1.0");
		textConnectionDensity.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblNbOfEpoch = new Label(container, SWT.NONE);
		lblNbOfEpoch.setText("Nb. of Epoch:");
		
		spinnerNbOfEpoch = new Spinner(container, SWT.BORDER);
		spinnerNbOfEpoch.setIncrement(5);
		spinnerNbOfEpoch.setMaximum(10000);
		spinnerNbOfEpoch.setMinimum(5);
		spinnerNbOfEpoch.setSelection(50);
		spinnerNbOfEpoch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return area;
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
		return new Point(435, 370);
	}

	@Override
	protected void okPressed() {
		
		try{
			connectionDensity=Double.parseDouble(textConnectionDensity.getText());
			population = spinnerPopulationSize.getSelection();
			nbOfEpoch = spinnerNbOfEpoch.getSelection();
			
			
			
		}
		catch(Exception e){
			
			this.setErrorMessage("Please Check the inputs!");
			
			return ;
		}
		
		
		super.okPressed();
	}

	public int getNbOfEpoch() {
		return nbOfEpoch;
	}

	public double getConnectionDensity() {
		return connectionDensity;
	}

	public int getPopulation() {
		return population;
	}
	
	
	
	

}
