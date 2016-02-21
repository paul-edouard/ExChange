package com.munch.exchange.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.Activation;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.ArchitectureType;

public class AddNeuralArchitectureDialog extends TitleAreaDialog {
	
	private static Logger logger = Logger.getLogger(AddNeuralArchitectureDialog.class);
	
	private NeuralArchitecture neuralArchitecture=new NeuralArchitecture();
	
	private Text txtName;
	private Combo comboType;
	private Combo comboActivationFunction;
	private Spinner spinnerHidderLayer1;
	private Spinner spinnerHidderLayer2;
	private Button btnHidderLayer;
	private Spinner spinnerVolume;
	private Label lblBlockProfitLimit;
	private Spinner spinnerBlockProfitLimit;
	private Label lblTradeProfitLimit;
	private Spinner spinnerTradeProfitLimit;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddNeuralArchitectureDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Create a new Architecture for the defined inputs");
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/keygroups_obj.gif"));
		setTitle("New Neural Architecture");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.grabExcessHorizontalSpace = false;
		container.setLayoutData(gd_container);
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblName.setText("Name:");
		
		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblVolume = new Label(container, SWT.NONE);
		lblVolume.setText("Volume:");
		
		spinnerVolume = new Spinner(container, SWT.BORDER);
		spinnerVolume.setMaximum(100000);
		spinnerVolume.setMinimum(1);
		spinnerVolume.setSelection(10);
		spinnerVolume.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblBlockProfitLimit = new Label(container, SWT.NONE);
		lblBlockProfitLimit.setText("Block Profit Limit:");
		
		spinnerBlockProfitLimit = new Spinner(container, SWT.BORDER);
		spinnerBlockProfitLimit.setIncrement(100);
		spinnerBlockProfitLimit.setMaximum(10000);
		spinnerBlockProfitLimit.setMinimum(100);
		spinnerBlockProfitLimit.setSelection(500);
		spinnerBlockProfitLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblTradeProfitLimit = new Label(container, SWT.NONE);
		lblTradeProfitLimit.setText("Trade Profit Limit:");
		
		spinnerTradeProfitLimit = new Spinner(container, SWT.BORDER);
		spinnerTradeProfitLimit.setIncrement(10);
		spinnerTradeProfitLimit.setMaximum(1000);
		spinnerTradeProfitLimit.setMinimum(10);
		spinnerTradeProfitLimit.setSelection(100);
		spinnerTradeProfitLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblType.setText("Type:");
		
		comboType = new Combo(container, SWT.NONE);
		comboType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				ArchitectureType[] types=NeuralArchitecture.ArchitectureType.values();
				ArchitectureType type=types[comboType.getSelectionIndex()];
				if(type==ArchitectureType.Elman){
					btnHidderLayer.setEnabled(false);
					spinnerHidderLayer1.setEnabled(true);
				}
				else if(type==ArchitectureType.FeedFoward){
					btnHidderLayer.setEnabled(true);
					spinnerHidderLayer1.setEnabled(true);
				}
				else if(type==ArchitectureType.Jordan){
					btnHidderLayer.setEnabled(false);
					spinnerHidderLayer1.setEnabled(true);
				}
				else if(type==ArchitectureType.Neat){
					btnHidderLayer.setEnabled(false);
					spinnerHidderLayer1.setEnabled(false);
//					setErrorMessage("Neat is not available");
				}
			}
		});
		comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblActivationFunction = new Label(container, SWT.NONE);
		lblActivationFunction.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblActivationFunction.setText("Activation Function:");
		
		comboActivationFunction = new Combo(container, SWT.NONE);
		comboActivationFunction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblHiddenLayer = new Label(container, SWT.NONE);
		lblHiddenLayer.setText("Hidden Layer 1:");
		
		spinnerHidderLayer1 = new Spinner(container, SWT.BORDER);
		spinnerHidderLayer1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerHidderLayer1.setPageIncrement(1);
		spinnerHidderLayer1.setMinimum(1);
		spinnerHidderLayer1.setSelection(10);
		
		btnHidderLayer = new Button(container, SWT.CHECK);
		btnHidderLayer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				spinnerHidderLayer2.setEnabled(btnHidderLayer.getSelection());
			}
		});
		btnHidderLayer.setText("Hidder Layer 2:");
		
		spinnerHidderLayer2 = new Spinner(container, SWT.BORDER);
		spinnerHidderLayer2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		spinnerHidderLayer2.setEnabled(false);
		spinnerHidderLayer2.setMinimum(1);
		spinnerHidderLayer2.setSelection(1);
		
		fillCombos();
		
		return area;
	}
	
	private void fillCombos(){
		ArchitectureType[] types=NeuralArchitecture.ArchitectureType.values();
		for(ArchitectureType type:types){
			comboType.add(type.toString());
		}
		comboType.select(0);
		
		
		
		Activation[] activations=NeuralArchitecture.Activation.values();
		for(Activation activation:activations){
			comboActivationFunction.add(activation.toString());
		}
		comboActivationFunction.select(0);
		
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
		return new Point(498, 543);
	}
	
	

	public NeuralArchitecture getNeuralArchitecture() {
		return neuralArchitecture;
	}

	@Override
	protected void okPressed() {
		
		if(txtName.getText().isEmpty()){
			this.setErrorMessage("Please Enter a name!");
			return;
		}
		
		ArchitectureType[] types=NeuralArchitecture.ArchitectureType.values();
		ArchitectureType type=types[comboType.getSelectionIndex()];
		/*
		if(type==ArchitectureType.Neat){
			this.setErrorMessage("Neat is not available");
			return;
		}
		*/
		
		Activation[] activations=NeuralArchitecture.Activation.values();
		Activation activation=activations[comboActivationFunction.getSelectionIndex()];
		
		String hiddenLayerDes=String.valueOf(spinnerHidderLayer1.getSelection());
		if(btnHidderLayer.getSelection() && spinnerHidderLayer2.getSelection()>0){
			hiddenLayerDes+=","+String.valueOf(spinnerHidderLayer2.getSelection());
		}
		
		neuralArchitecture.setName(txtName.getText());
		neuralArchitecture.setVolume(spinnerVolume.getSelection());
		neuralArchitecture.setBlockProfitLimit(spinnerBlockProfitLimit.getSelection());
		neuralArchitecture.setTradeProfitLimit(spinnerTradeProfitLimit.getSelection());
		neuralArchitecture.setType(type);
		neuralArchitecture.setActivation(activation);
		if(type==ArchitectureType.Neat)
			hiddenLayerDes="";
		neuralArchitecture.setHiddenLayerDescription(hiddenLayerDes);
		
		super.okPressed();
	}
	
	
	
	

}
