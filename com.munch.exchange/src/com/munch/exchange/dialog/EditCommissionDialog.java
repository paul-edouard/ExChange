package com.munch.exchange.dialog;

import java.util.Arrays;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.wb.swt.ResourceManager;

import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbCommission.CommissionCategory;
import com.munch.exchange.model.core.ib.IbCommission.CommissionType;
import com.munch.exchange.model.core.ib.IbCommission.Currency;

public class EditCommissionDialog extends TitleAreaDialog {
	
	private IbCommission commission;
	
	private Combo comboCategory;
	private Combo comboType;
	private Combo comboCurrency;
	private Spinner spinnerFixed;
	private Button btnFixedPercent;
	private Spinner spinnerMinPerOrder;
	private Button btnMinPerOrderPercent;
	private Spinner spinnerMaxPerorder;
	private Button btnMaxPerOrderPercent;
	private Spinner spinnerMonthlyTradeAmount;
	private Spinner spinnerCommissions;
	private Button btnCommissionPercent;
	private Spinner spinnerVolume;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditCommissionDialog(Shell parentShell,IbCommission commission) {
		super(parentShell);
		setHelpAvailable(false);
		this.commission=commission;
	}
	
	public static String[] getNames(Class<? extends Enum<?>> e) {
	    return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/72/stock-market-icon.png"));
		setTitle("IB Commission\r\n");
		setMessage("Please set the IB commission");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		Label lblCategory = new Label(composite, SWT.NONE);
		lblCategory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblCategory.setSize(76, 25);
		lblCategory.setText("Category:");
		
		comboCategory = new Combo(composite, SWT.NONE);
		comboCategory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Label lblType = new Label(composite, SWT.NONE);
		lblType.setText("Type:");
		
		comboType = new Combo(composite, SWT.NONE);
		comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Label lblCurrency = new Label(composite, SWT.NONE);
		lblCurrency.setText("Currency:");
		
		comboCurrency = new Combo(composite, SWT.NONE);
		comboCurrency.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Label lblFixed = new Label(composite, SWT.NONE);
		lblFixed.setText("Fixed:");
		
		spinnerFixed = new Spinner(composite, SWT.BORDER);
		spinnerFixed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnFixedPercent = new Button(composite, SWT.CHECK);
		btnFixedPercent.setText("Percent");
		
		Label lblMinOerOrder = new Label(composite, SWT.NONE);
		lblMinOerOrder.setText("Min per order:");
		
		spinnerMinPerOrder = new Spinner(composite, SWT.BORDER);
		spinnerMinPerOrder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnMinPerOrderPercent = new Button(composite, SWT.CHECK);
		btnMinPerOrderPercent.setText("Percent");
		
		Label lblMaxPerOrder = new Label(composite, SWT.NONE);
		lblMaxPerOrder.setText("Max per order:");
		
		spinnerMaxPerorder = new Spinner(composite, SWT.BORDER);
		spinnerMaxPerorder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnMaxPerOrderPercent = new Button(composite, SWT.CHECK);
		btnMaxPerOrderPercent.setText("Percent");
		
		Label lblMonthlyTradeAmount = new Label(composite, SWT.NONE);
		lblMonthlyTradeAmount.setText("Monthly trade amount:");
		
		spinnerMonthlyTradeAmount = new Spinner(composite, SWT.BORDER);
		spinnerMonthlyTradeAmount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Label lblCommissions = new Label(composite, SWT.NONE);
		lblCommissions.setText("Commissions:");
		
		spinnerCommissions = new Spinner(composite, SWT.BORDER);
		spinnerCommissions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnCommissionPercent = new Button(composite, SWT.CHECK);
		btnCommissionPercent.setText("Percent");
		
		Label lblContractVolume = new Label(composite, SWT.NONE);
		lblContractVolume.setText("Contract Volume:");
		
		spinnerVolume = new Spinner(composite, SWT.BORDER);
		spinnerVolume.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		initCompo(comboCategory,IbCommission.CommissionCategory.class,
				commission.getCommissionCategory().toString());
		initCompo(comboType,IbCommission.CommissionType.class,
				commission.getCommissionType().toString());
		initCompo(comboCurrency,IbCommission.Currency.class,
				commission.getCurrency().toString());
		
		initSpinners();
		addListeners();
		
		return container;
	}
	
	private void initCompo(Combo combo,Class<? extends Enum<?>> e,String text){
		String[] values=getNames(e);
		for(int i=0;i<values.length;i++){
			combo.add(values[i]);
			if(values[i].equals(text)){
				combo.setText(values[i]);
			}
		}
	}
	
	private void initSpinners(){
		
		spinnerFixed.setValues((int) (commission.getFixed()*1000), -5, 5000, 3, 5, 1);
		btnFixedPercent.setSelection(commission.isFixedPercentOfTradeValue());
		
		spinnerMinPerOrder.setValues((int) (commission.getMinPerOrder()*10), -5, 600, 1, 5, 1);
		btnMinPerOrderPercent.setSelection(commission.isMinPerOrderPercentOfTradeValue());
		
		spinnerMaxPerorder.setValues((int) (commission.getMaxPerOrder()*10), -5, 2000, 1, 5, 1);
		btnMaxPerOrderPercent.setSelection(commission.isMaxPerOrderPercentOfTradeValue());
		
		spinnerMonthlyTradeAmount.setValues((int) commission.getMonthlyTradeAmount(), 0, 2000000, 0, 100, 1);
		
		spinnerCommissions.setValues((int) (commission.getCommissions()*100), -1, 200, 2, 1, 1);
		btnCommissionPercent.setVisible(false);
		
		
		spinnerVolume.setValues((int) commission.getContractVolume(), 0, 2000000, 0, 1000, 1);
		
	}
	
	private void addListeners(){
		comboCategory.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				CommissionCategory category=CommissionCategory.valueOf(comboCategory.getText());
				comboType.setEnabled(category==CommissionCategory.StocksETFsWarrants 
						|| category==CommissionCategory.FuturesAndFOPs);
				
				refreshSpinnerEditability();
			}
		});
		
		comboType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				refreshSpinnerEditability();
			}
		});
		
		comboCurrency.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Currency currency=Currency.valueOf(comboCurrency.getText());
				if(currency==Currency.None){
					setErrorMessage(null);
				}
				
			}
		});
		
	}
	
	private void refreshSpinnerEditability(){
		setAllSpinnerDisable();
		
		CommissionCategory category=CommissionCategory.valueOf(comboCategory.getText());
		CommissionType type=CommissionType.valueOf(comboType.getText());
		
		if( category==CommissionCategory.None){
			return;
		}
		
		if(category==CommissionCategory.StocksETFsWarrants){
			if(type==CommissionType.None)return;
			if(this.getButton(OK)!=null)
				this.getButton(OK).setEnabled(true);
			spinnerMinPerOrder.setEnabled(true);
			//btnMinPerOrderPercent.setEnabled(true);
			if(type==CommissionType.Fixed){
				spinnerFixed.setEnabled(true);
				btnFixedPercent.setEnabled(true);
				
				spinnerMaxPerorder.setEnabled(true);
				btnMaxPerOrderPercent.setEnabled(true);
			}
			else{
				spinnerMonthlyTradeAmount.setEnabled(true);
				
				spinnerCommissions.setEnabled(true);
				btnCommissionPercent.setEnabled(true);
			}
		}
		else if(category==CommissionCategory.Forex){
			if(this.getButton(OK)!=null)
				this.getButton(OK).setEnabled(true);
			spinnerMonthlyTradeAmount.setEnabled(true);
			
			spinnerCommissions.setEnabled(true);
			btnCommissionPercent.setEnabled(true);
			
			spinnerMinPerOrder.setEnabled(true);
			btnMinPerOrderPercent.setEnabled(true);
			
		}
		
		
		
	}
	
	private void setAllSpinnerDisable(){
		spinnerFixed.setEnabled(false);
		btnFixedPercent.setEnabled(false);
		
		spinnerMinPerOrder.setEnabled(false);
		btnMinPerOrderPercent.setEnabled(false);
		
		spinnerMaxPerorder.setEnabled(false);
		btnMaxPerOrderPercent.setEnabled(false);
		
		spinnerMonthlyTradeAmount.setEnabled(false);
		
		spinnerCommissions.setEnabled(false);
		btnCommissionPercent.setEnabled(false);
		
		spinnerVolume.setEnabled(false);
		
		if(this.getButton(OK)!=null)
			this.getButton(OK).setEnabled(false);
		
	}
	
	


	@Override
	protected void okPressed() {
		Currency currency=Currency.valueOf(comboCurrency.getText());
		if(currency==Currency.None){
			this.setErrorMessage("Please set the currency!");
			return;
		}
		
		//Save the values
		CommissionCategory category=CommissionCategory.valueOf(comboCategory.getText());
		CommissionType type=CommissionType.valueOf(comboType.getText());
		
		commission.setCommissionCategory(category);
		commission.setCommissionType(type);
		commission.setCurrency(currency);
		
		
		commission.setFixed(((double)spinnerFixed.getSelection())/1000);
		commission.setFixedPercentOfTradeValue(btnFixedPercent.getSelection());
		
		commission.setMinPerOrder(((double)spinnerMinPerOrder.getSelection())/10);
		commission.setMinPerOrderPercentOfTradeValue(btnMinPerOrderPercent.getSelection());
		
		commission.setMaxPerOrder(((double)spinnerMaxPerorder.getSelection())/10);
		commission.setMaxPerOrderPercentOfTradeValue(btnMaxPerOrderPercent.getSelection());
		
		commission.setMonthlyTradeAmount(((double)spinnerMonthlyTradeAmount.getSelection())/10);
		
		commission.setCommissions(((double)spinnerCommissions.getSelection())/100);
		//System.out.println("spinnerCommissions: "+spinnerCommissions.getSelection());
		
		commission.setContractVolume(((double)spinnerVolume.getSelection()));
		
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
		
		this.getButton(OK).setEnabled(false);
		
		refreshSpinnerEditability();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(561, 591);
	}

	public IbCommission getCommission() {
		return commission;
	}
	
	

}
