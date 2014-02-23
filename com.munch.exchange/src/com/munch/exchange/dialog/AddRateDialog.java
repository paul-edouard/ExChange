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
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.services.IExchangeRateProvider;

public class AddRateDialog extends TitleAreaDialog {
	private Text SymbolText;
	private Text OnVistaIdText;
	private Text CommodityName;
	private Button btnOnVistaId;
	private Button btnCommodityName;
	private Button buttonOk;
	
	private IExchangeRateProvider exchangeRateProvider;
	
	private ExchangeRate rate;
	
	private static Logger logger = Logger.getLogger(AddRateDialog.class);
	private Combo comboRateType;
	private Label lblRateType;
	private Label lblYahooFinanceSymbol;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddRateDialog(Shell parentShell, IExchangeRateProvider exchangeRateProvider) {
		super(parentShell);
		setHelpAvailable(false);
		this.exchangeRateProvider=exchangeRateProvider;
	}
	
	
	public ExchangeRate getRate() {
		return rate;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/login_dialog.gif"));
		setTitle("Add Rate");
		setMessage("Add a new rate to the list by downloading it to from Yahoo Finance or OnVista");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lblRateType = new Label(container, SWT.NONE);
		lblRateType.setText("Rate Type:");
		
		comboRateType = new Combo(container, SWT.NONE);
		comboRateType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboRateType.setText("Yahoo Finance Symbol, ex: DAE.DE");
		comboRateType.add("Yahoo Finance Symbol, ex: DAE.DE");
		comboRateType.add("Federal Reserve Bank St. Louis Symbol, ex: CPIAUCSL");
		
		comboRateType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				System.out.println(comboRateType.getText());
				boolean modus=comboRateType.getText().startsWith("Yahoo Finance Symbol");
				btnOnVistaId.setVisible(modus);
				OnVistaIdText.setVisible(modus);
				btnCommodityName.setVisible(modus);
				CommodityName.setVisible(modus);
				
				if(modus){
					lblYahooFinanceSymbol.setText("Yahoo Finance Symbol:");
				}
				else{
					lblYahooFinanceSymbol.setText("St. Louis Symbol:");
				}
				
			}
		});
		
		
		lblYahooFinanceSymbol = new Label(container, SWT.NONE);
		lblYahooFinanceSymbol.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblYahooFinanceSymbol.setText("Yahoo Finance Symbol:");
		
		SymbolText = new Text(container, SWT.BORDER);
		SymbolText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text=SymbolText.getText();
				buttonOk.setEnabled(!text.isEmpty());
				AddRateDialog.this.setErrorMessage(null);
				//AddRateDialog.this.
			}
		});
		SymbolText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnOnVistaId = new Button(container, SWT.CHECK);
		btnOnVistaId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				OnVistaIdText.setEnabled(btnOnVistaId.getSelection());
				btnCommodityName.setEnabled(btnOnVistaId.getSelection());
				if(!btnOnVistaId.getSelection()){
					btnCommodityName.setSelection(false);
					CommodityName.setEnabled(false);
				}
				
			}
		});
		btnOnVistaId.setText("On Vista ID:");
		
		OnVistaIdText = new Text(container, SWT.BORDER);
		OnVistaIdText.setEnabled(false);
		OnVistaIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnCommodityName = new Button(container, SWT.CHECK);
		btnCommodityName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommodityName.setEnabled(btnCommodityName.getSelection());
			}
		});
		btnCommodityName.setText("Commodity Name:");
		btnCommodityName.setEnabled(false);
		
		CommodityName = new Text(container, SWT.BORDER);
		CommodityName.setEnabled(false);
		CommodityName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		buttonOk = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		buttonOk.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		
		String search_str=SymbolText.getText();
		boolean modus=comboRateType.getText().startsWith("Yahoo Finance Symbol");
		if(!modus){
			search_str="FRED_"+SymbolText.getText();
		}
		//Test if the symbol is already used
		if(this.btnCommodityName.getSelection()){
			search_str=CommodityName.getText();
		}
		if(exchangeRateProvider.isSymbolAlreadyUsed(search_str)){
			this.setErrorMessage("The given symbol \""+SymbolText.getText()+
					"\" is already used!\nPlease enter a other one.");
			return;
		}
		
		if(modus){
			if(this.btnOnVistaId.getSelection()){
				search_str=SymbolText.getText()+";"+OnVistaIdText.getText();
			}
			if(this.btnCommodityName.getSelection()){
				search_str=CommodityName.getText()+";"+SymbolText.getText()+";"+OnVistaIdText.getText();
			}
		}
		
	
		rate=  exchangeRateProvider.load(search_str);
		if(rate==null){
			this.setErrorMessage("Cannot find the given symbol \""+SymbolText.getText()+
					"\" on Yahoo Finance!\nPlease check if it is correct spelled.");
			return;
		}
		
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 254);
	}
	
	

}
