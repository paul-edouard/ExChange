package com.munch.exchange.dialog;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
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

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.parts.overview.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;

import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class AddContractDialog extends TitleAreaDialog {
	private Text SymbolText;
	private Button buttonOk;
	
	private static Logger logger = Logger.getLogger(AddContractDialog.class);
	private Combo comboType;
	private Label lblRateType;
	private Label lblSymbol;
	private List listResults;
	private ListViewer listViewerResults;
	private Button btnSearch;
	
	IIBContractProvider contractProvider;
	private IbContract contract;
	private Shell shell;
	
	private HashMap<String, SecType> secTypemap=new HashMap<>();
	private HashMap<String, IbContract> contractMap=new HashMap<>();
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddContractDialog(Shell parentShell, IIBContractProvider contractProvider) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
		setHelpAvailable(false);
		this.contractProvider=contractProvider;
		this.shell=parentShell;
		
		
	}
	

	public IbContract getContract() {
		return contract;
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/login_dialog.gif"));
		setTitle("Search new contract");
		setMessage("Search and add a new Interactive Brokers contract");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lblRateType = new Label(container, SWT.NONE);
		lblRateType.setText("Type:");
		
		comboType = new Combo(container, SWT.NONE);
		comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		comboType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				/*
				System.out.println(comboType.getText());
				boolean modus=(comboType.getText().startsWith("Yahoo Finance Symbol") || 
						comboType.getText().startsWith("ISIN"));
				
				
				if(modus && comboType.getText().startsWith("ISIN")){
					lblSymbol.setText("ISIN:");
				}
				else if(modus && comboType.getText().startsWith("Yahoo Finance")){
					lblSymbol.setText("Yahoo Finance Symbol:");
				}
				else{
					lblSymbol.setText("St. Louis Symbol:");
				}
				*/
			}
		});
		fillCombo();
		
		lblSymbol = new Label(container, SWT.NONE);
		lblSymbol.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSymbol.setText("Symbol:");
		
		SymbolText = new Text(container, SWT.BORDER);
		SymbolText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text=SymbolText.getText();
				btnSearch.setEnabled(!text.isEmpty());
				AddContractDialog.this.setErrorMessage(null);
				//AddRateDialog.this.
			}
		});
		SymbolText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnSearch = new Button(container, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Cursor cursor=shell.getCursor();
				//shell.setCursor( new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT) );
				
				contractMap.clear();
				String symbol=SymbolText.getText();
				SecType secType=secTypemap.get(comboType.getText());
				
				java.util.List<IbContract> contracts=contractProvider.searchContracts(symbol, secType);
				//java.util.List<ExContract> contracts=contractProvider.getAll();
				buttonOk.setEnabled(false);
				listViewerResults.setInput(contracts);
				listViewerResults.refresh();
				
				//shell.setCursor(cursor);
				
			}
		});
		btnSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		btnSearch.setText("Search");
		
		listViewerResults = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		listViewerResults.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel=event.getSelection();
				buttonOk.setEnabled(!sel.isEmpty());
			}
		});
		listResults = listViewerResults.getList();
		listResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listViewerResults.setContentProvider(new ResultListContentProvider());
		listViewerResults.setLabelProvider(new ResultListLabelProvider());

		return area;
	}
	
	private void fillCombo(){
		
		secTypemap.clear();
		
		SecType[] values=SecType.values();
		
		boolean textSet=false;
		for(int i=0;i<values.length;i++){
			if(values[i]==SecType.None)continue;
			comboType.add(values[i].getApiString());
			secTypemap.put(values[i].getApiString(), values[i]);
			
			
			if(!textSet){
				comboType.setText(values[i].getApiString());
				textSet=true;
			}
		}
		
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
		
		String[] selections=listResults.getSelection();
		if(selections.length!=1)return;
		
		String sel=selections[0];
		contract=contractMap.get(sel);
		if(contract==null)return;
		
		//Test if the contract was allready added
		//MessageDialog
		java.util.List<IbContract> list=contractProvider.getAllContracts();
		for(IbContract testContract:list ){
			if(testContract.compareWith(contract)){
				MessageDialog.openWarning(shell, "Contract alleady in the database",
						"The contract "+contract.getLongName()+" connot be added");
				return;
			}
		}
		
		
		contract=contractProvider.create(contract);
		
		
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(672, 434);
	}
	
	private class ResultListContentProvider implements IStructuredContentProvider{

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof java.util.List<?>){
				java.util.List<?> l=(java.util.List<?>) inputElement;
				return l.toArray();
			}
			return null;
		}
		
	}
	
	private class ResultListLabelProvider extends StyledCellLabelProvider implements ILabelProvider{

		@Override
		public void update(ViewerCell cell) {
			Object element=cell.getElement();
			if(element instanceof IbContract){
				IbContract contract=(IbContract) element;
				cell.setText( contract.getLongName());
			}
			super.update(cell);
		}

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(Object element) {
			
			if(element instanceof IbContract){
				IbContract contract=(IbContract) element;
				
				String text="";
				switch (contract.getSecType()) {
				case STK:
					text= contract.getLongName()+" ["+contract.getMarketName()
					+", "+contract.getPrimaryExch()+", "+contract.getExchange()+"] "+contract.getCurrency();
					break;
				case OPT:
					text= contract.getLongName()+", "+contract.getLocalSymbol()+" ["+contract.getMarketName()
					+", "+contract.getM_right()+", "+contract.getExchange()+"] "+contract.getCurrency();
					break;
				case IND:
					text= contract.getLongName()+", "+contract.getLocalSymbol()+" ["+contract.getMarketName()
					+", "+contract.getExchange()+"] "+contract.getCurrency();
					break;
				
				default:
					text= contract.getLongName()+", "+contract.getLocalSymbol()+" ["+contract.getMarketName()
					+", "+contract.getExchange()+"] "+contract.getCurrency();
					break;
				}
				
				if(!contractMap.containsKey(text))
					contractMap.put(text, contract);
				
				return text;
				
				
			}
			return null;
		}
		
	}
	

}
