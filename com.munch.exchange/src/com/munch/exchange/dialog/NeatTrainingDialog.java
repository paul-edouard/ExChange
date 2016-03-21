package com.munch.exchange.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;

public class NeatTrainingDialog extends TitleAreaDialog {
	
	
	private int nbOfEpoch;
	private double connectionDensity;
	private int population;
	private long timeout;
	private boolean timeoutSet;
	private int archiveSize;
	private int nbOfneighbors;
	private int nbOfBackTestingEvaluation;
	
	private boolean isNoveltySearch=false;
	
	
	private Text textConnectionDensity;
	private Spinner spinnerPopulationSize;
	private Spinner spinnerEpoch;
	private Button btnTimeout;
	private DateTime dateTime;
	private Spinner spinnerNbOfNeighbors;
	private Spinner spinnerArchiveSize;
	private Label lblBackTEvaluation;
	private Spinner spinnerBackTestingEval;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public NeatTrainingDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public NeatTrainingDialog(Shell parentShell, boolean isNoveltySearch) {
		super(parentShell);
		this.isNoveltySearch=isNoveltySearch;
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
		spinnerPopulationSize.setSelection(500);
		spinnerPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblConnectionDensity = new Label(container, SWT.NONE);
		lblConnectionDensity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConnectionDensity.setText("Connection Density:");
		
		textConnectionDensity = new Text(container, SWT.BORDER);
		textConnectionDensity.setText("1.0");
		textConnectionDensity.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblArchiveSize = new Label(container, SWT.NONE);
		lblArchiveSize.setText("Archive Size:");
		
		spinnerArchiveSize = new Spinner(container, SWT.BORDER);
		spinnerArchiveSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		spinnerArchiveSize.setIncrement(500);
		spinnerArchiveSize.setPageIncrement(500);
		spinnerArchiveSize.setMaximum(100000);
		spinnerArchiveSize.setMinimum(1000);
		spinnerArchiveSize.setSelection(3000);
		spinnerArchiveSize.setEnabled(isNoveltySearch);
		
		Label lblNbOfNeighbors = new Label(container, SWT.NONE);
		lblNbOfNeighbors.setText("Nb. of Neighbors:");
		
		spinnerNbOfNeighbors = new Spinner(container, SWT.BORDER);
		spinnerNbOfNeighbors.setIncrement(50);
		spinnerNbOfNeighbors.setPageIncrement(50);
		spinnerNbOfNeighbors.setMaximum(10000);
		spinnerNbOfNeighbors.setMinimum(50);
		spinnerNbOfNeighbors.setSelection(200);
		spinnerNbOfNeighbors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerNbOfNeighbors.setEnabled(isNoveltySearch);
		
		lblBackTEvaluation = new Label(container, SWT.NONE);
		lblBackTEvaluation.setText("Back T. Evaluation:");
		
		spinnerBackTestingEval = new Spinner(container, SWT.BORDER);
		spinnerBackTestingEval.setMaximum(1000);
		spinnerBackTestingEval.setMinimum(1);
		spinnerBackTestingEval.setSelection(20);
		spinnerBackTestingEval.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		Label lblNbOfEpoch = new Label(container, SWT.NONE);
		lblNbOfEpoch.setText("Nb. of Epoch:");
		
		spinnerEpoch = new Spinner(container, SWT.BORDER);
		spinnerEpoch.setIncrement(5);
		spinnerEpoch.setMaximum(10000);
		spinnerEpoch.setMinimum(5);
		spinnerEpoch.setSelection(50);
		spinnerEpoch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnTimeout = new Button(container, SWT.CHECK);
		btnTimeout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dateTime.setEnabled(btnTimeout.getSelection());
				spinnerEpoch.setEnabled(!btnTimeout.getSelection());
				timeoutSet=btnTimeout.getSelection();
			}
		});
		btnTimeout.setText("Timeout");
		
		dateTime = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime.setEnabled(false);
		dateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dateTime.setTime(1, 0, 0);
		

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
		return new Point(511, 508);
	}

	@Override
	protected void okPressed() {
		
		try{
			connectionDensity=Double.parseDouble(textConnectionDensity.getText());
			population = spinnerPopulationSize.getSelection();
			nbOfEpoch = spinnerEpoch.getSelection();
			nbOfBackTestingEvaluation=spinnerBackTestingEval.getSelection();
			timeout=dateTime.getHours()*3600+dateTime.getMinutes()*60;
			timeout*=1000;
			
			
			archiveSize=spinnerArchiveSize.getSelection();
			nbOfneighbors=spinnerNbOfNeighbors.getSelection();
			
			
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

	public long getTimeout() {
		return timeout;
	}

	public boolean isTimeoutSet() {
		return timeoutSet;
	}

	public int getArchiveSize() {
		return archiveSize;
	}

	public int getNbOfneighbors() {
		return nbOfneighbors;
	}

	public boolean isNoveltySearch() {
		return isNoveltySearch;
	}
	
	public int getNbOfBackTestingEvaluation() {
		return nbOfBackTestingEvaluation;
	}
	
	

}
