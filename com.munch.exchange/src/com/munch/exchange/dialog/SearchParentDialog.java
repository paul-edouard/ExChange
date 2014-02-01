package com.munch.exchange.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IExchangeRateProvider;

public class SearchParentDialog extends TitleAreaDialog {
	
	
	private IExchangeRateProvider exchangeRateProvider;
	
	private Stock stock;
	
	private static Logger logger = Logger.getLogger(SearchParentDialog.class);
	private Text textSymbol;
	private Button buttonOk;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SearchParentDialog(Shell parentShell, IExchangeRateProvider exchangeRateProvider) {
		super(parentShell);
		setHelpAvailable(false);
		this.exchangeRateProvider=exchangeRateProvider;
	}
	
	

	public Stock getStock() {
		return stock;
	}



	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Please enter the YQL symbol corresponding to the parent Stock");
		setTitle("Search Parent");
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/login_dialog.gif"));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		textSymbol = new Text(container, SWT.BORDER);
		textSymbol.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text=textSymbol.getText();
				buttonOk.setEnabled(!text.isEmpty());
				SearchParentDialog.this.setErrorMessage(null);
			}
		});
		textSymbol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

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
		
		String search_str=textSymbol.getText();
		
		Stock s=exchangeRateProvider.loadStock(search_str);
		
		if(s==null){
			this.setErrorMessage("Cannot find the given symbol \""+textSymbol.getText()+
					"\" on Yahoo Finance!\nPlease check if it is correct spelled.");
			return;
		}
		
		this.stock=s;
		
		// TODO Auto-generated method stub
		super.okPressed();
	}



	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}
