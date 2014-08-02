package com.munch.exchange.parts.financials;

import java.util.Calendar;
import java.util.LinkedList;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IFinancialsProvider;

public class StockFinancials extends Composite {
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IFinancialsProvider financialsProvider;

	
	
	private StockFinancialsContentProvider contentProvider=new StockFinancialsContentProvider();
	
	private Stock stock;
	private TreeViewer treeViewer;
	private Tree tree;
	private Button btnAddColumn;
	private Button btnQuaterly;
	private Button btnAnnualy;
	private ScrollBar horizontalScrollBar;
	
	int maxVisibleColumns=6;
	int firstVisibleColumn=0;
	
	private LinkedList<TreeViewerColumn> columns=new LinkedList<TreeViewerColumn>();
	
	
	private Label lblUnit;
	private Combo comboUnit;
	
	private double unitFactor=1;
	
	private String modus;
	private Button btnCancel;
	private Button btnSave;
	private Button btnLastest;
	private Button btnFirst;
	private Button btnLast;
	private Button btnNext;
	private TabFolder tabFolder;
	private TabItem itemTable;
	private Composite composite;
	private TabItem tbtmParser;
	private FinancialReportParserComposite compositeParser;
	
	@Inject
	public StockFinancials(Composite parent,ExchangeRate rate,IEclipseContext context) {
		super(parent, SWT.NONE);
		this.stock=(Stock) rate;
		setLayout(new GridLayout(1, false));
		unitFactor=1000000;
		
		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		itemTable = new TabItem(tabFolder, SWT.NONE);
		itemTable.setText("Table");
		
		composite = new Composite(tabFolder, SWT.NONE);
		itemTable.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(composite, SWT.NONE);
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeHeader.setSize(610, 35);
		compositeHeader.setLayout(new GridLayout(11, false));
		
		btnQuaterly = new Button(compositeHeader, SWT.RADIO);
		btnQuaterly.setEnabled(false);
		btnQuaterly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnQuaterly.getSelection()){
					btnAnnualy.setSelection(false);
					btnQuaterly.setEnabled(false);
					btnAnnualy.setEnabled(true);
					firstVisibleColumn=0;
					refreshColumns();
				}
			}
		});
		btnQuaterly.setSelection(true);
		btnQuaterly.setText("quaterly");
		
		btnAnnualy = new Button(compositeHeader, SWT.RADIO);
		btnAnnualy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnAnnualy.getSelection()){
					btnQuaterly.setSelection(false);
					btnAnnualy.setEnabled(false);
					btnQuaterly.setEnabled(true);
					firstVisibleColumn=0;
					refreshColumns();
				}
			}
		});
		btnAnnualy.setText("annualy");
		
		lblUnit = new Label(compositeHeader, SWT.NONE);
		lblUnit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUnit.setText("Unit:");
		
		comboUnit = new Combo(compositeHeader, SWT.NONE);
		comboUnit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(comboUnit.getText().equals("1"))
					unitFactor=1;
				else if(comboUnit.getText().equals("1.000")){
					unitFactor=1000;
				}
				else if(comboUnit.getText().equals("1.000.000")){
					unitFactor=1000000;
				}
				
				treeViewer.refresh();
				
			}
		});
		comboUnit.add("1");
		comboUnit.add("1.000");
		comboUnit.add("1.000.000");
		comboUnit.setText("1.000.000");
		
		btnAddColumn = new Button(compositeHeader, SWT.NONE);
		btnAddColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				stock.getFinancials().addPoint(modus);
				if(firstVisibleColumn<columns.size()-maxVisibleColumns)firstVisibleColumn++;
				refreshColumns();
				
			}
		});
		btnAddColumn.setText("Add Column");
		
		btnLastest = new Button(compositeHeader, SWT.NONE);
		btnLastest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				firstVisibleColumn=0;
				refreshColumnsVisibility();
			}
		});
		btnLastest.setText("<<");
		
		btnLast = new Button(compositeHeader, SWT.NONE);
		btnLast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(firstVisibleColumn>0)firstVisibleColumn--;
				refreshColumnsVisibility();
				
			}
		});
		btnLast.setText("<");
		
		btnNext = new Button(compositeHeader, SWT.NONE);
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(firstVisibleColumn<columns.size()-maxVisibleColumns)firstVisibleColumn++;
				refreshColumnsVisibility();
				
				
			}
		});
		btnNext.setText(">");
		
		btnFirst = new Button(compositeHeader, SWT.NONE);
		btnFirst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(columns.size()-maxVisibleColumns>=0)firstVisibleColumn=columns.size()-maxVisibleColumns;
				else
					firstVisibleColumn=0;
				refreshColumnsVisibility();
				
			}
		});
		btnFirst.setText(">>");
		
		btnSave = new Button(compositeHeader, SWT.NONE);
		btnSave.setEnabled(false);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				financialsProvider.saveAll(stock);
				btnSave.setEnabled(false);
				btnCancel.setEnabled(false);
			}
		});
		btnSave.setText("Save");
		
		btnCancel = new Button(compositeHeader, SWT.NONE);
		btnCancel.setEnabled(false);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//financialsProvider.loadBalanceSheet(stock);
				
			}
		});
		btnCancel.setText("Cancel");
		
		treeViewer = new TreeViewer(composite,  SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setInput(contentProvider.getRoot());
		horizontalScrollBar=treeViewer.getTree().getHorizontalBar();
		
	
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setSize(610, 225);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		
		//Main Column
		TreeViewerColumn mainColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		mainColumn.setLabelProvider(new mainColumnLabelProvider());
		TreeColumn trclmnName = mainColumn.getColumn();
		trclmnName.setWidth(300);
		trclmnName.setText("Period");
		
		tbtmParser = new TabItem(tabFolder, SWT.NONE);
		tbtmParser.setText("Parser");
		
		
		//Create a context instance
		IEclipseContext localContact=EclipseContextFactory.create();
		localContact.set(Composite.class, tabFolder);
		localContact.setParent(context);
		
		compositeParser=ContextInjectionFactory.make( FinancialReportParserComposite.class,localContact);
		tbtmParser.setControl(compositeParser);
		
		treeViewer.refresh();
		
	}
	
	
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public String getModus() {
		return modus;
	}
	
	public double getUnitFactor() {
		return unitFactor;
	}
	
	public Button getBtnCancel() {
		return btnCancel;
	}

	public Button getBtnSave() {
		return btnSave;
	}


	private void refreshColumns(){
		removeColumns();
		createColumns();
		refreshColumnsVisibility();
	}
	
	private void removeColumns(){
		for(TreeViewerColumn column:columns){
			column.getColumn().dispose();
		}
		columns.clear();
	}
	
	private void createColumns(){
		
		modus=FinancialPoint.PeriodeTypeQuaterly;
		if(btnAnnualy.getSelection())
			modus=FinancialPoint.PeriodeTypeAnnual;
		
		//trclmnName.addSelectionListener(getSelectionAdapter(trclmnName, 0));
		
		for(Calendar date :this.stock.getFinancials().getDateList(modus)){
			TreeViewerColumn dateColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
			dateColumn.setLabelProvider(new valueColumnLabelProvider(date));
			dateColumn.setEditingSupport(new StockFinancialsEditingSupport(this, stock, date));
			
			TreeColumn trclmn = dateColumn.getColumn();
			//trclmn.setWidth(150);
			trclmn.setText(getColumnHeaderName(date));
			
			columns.add(dateColumn);
			
		}
		
		treeViewer.expandToLevel(2);
		
	}
	
	private String getColumnHeaderName(Calendar date){
		if(modus.equals(FinancialPoint.PeriodeTypeAnnual)){
			return String.valueOf(date.get(Calendar.YEAR));
		}
		else{
			if(date.get(Calendar.MONTH)>0 && date.get(Calendar.MONTH)<=3){
				return "Q1 "+String.valueOf(date.get(Calendar.YEAR));
			}
			else if(date.get(Calendar.MONTH)>3 && date.get(Calendar.MONTH)<=6){
				return "Q2 "+String.valueOf(date.get(Calendar.YEAR));
			}
			else if(date.get(Calendar.MONTH)>6 && date.get(Calendar.MONTH)<=9){
				return "Q3 "+String.valueOf(date.get(Calendar.YEAR));
			}
			else{
				return "Q4 "+String.valueOf(date.get(Calendar.YEAR));
			}
		}
	}
	
	
	private void refreshColumnsVisibility(){
		int pos=0;
		for(TreeViewerColumn dateColumn:columns){
			if(pos>=firstVisibleColumn && pos<firstVisibleColumn+maxVisibleColumns)
				dateColumn.getColumn().setWidth(150);
			else
				dateColumn.getColumn().setWidth(0);
			pos++;
		}
		
		treeViewer.refresh();
		
	}
	
	
	//################################
	//##     ColumnLabelProvider    ##
	//################################
	class mainColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
			return null;
		}
		public String getText(Object element) {
			if(element instanceof FinancialElement){
				//System.out.println("Financial element:"+ element.toString());
				FinancialElement entity=(FinancialElement) element;
				return entity.name;
			}
			//System.out.println("No Financial element?"+ element.toString());
			return element == null ? "" : element.toString();
		}
	}
	
	class valueColumnLabelProvider extends ColumnLabelProvider{
		
		Calendar date;
		
		public valueColumnLabelProvider(Calendar date){
			this.date=date;
		}
		
		public Image getImage(Object element) {
			return null;
		}
		
		public String getText(Object element) {
			
			String modus=FinancialPoint.PeriodeTypeQuaterly;
			if(btnAnnualy.getSelection())
				modus=FinancialPoint.PeriodeTypeAnnual;
			
			
			if(element instanceof FinancialElement){
				FinancialElement entity=(FinancialElement) element;
				if(entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate)){
					return DateTool.dateToDayString(stock.getFinancials().getEffectiveDate(modus,date));
				}
				else{
					long val=stock.getFinancials().getValue(modus,date, entity.fieldKey,entity.sectorKey);
					if(val==0)return "-";
					return StockFinancials.this.getStringOfValue(val,entity.fieldKey);
				}
			}
			return element == null ? "" : element.toString();
		}
		
		
	}
	
	public String getStringOfValue(long value,String key){
		
		if(value==Long.MIN_VALUE)return "";
		
		if(key.equals(IncomeStatementPoint.FIELD_Employees)){
			return String.valueOf(value);
		}
		else if(key.equals(IncomeStatementPoint.FIELD_EarningsPerShare)){
			return String.valueOf(((double)value)/100);
		}
		
		
		if(unitFactor==1){
			return String.valueOf(value);
		}
		else if(unitFactor==1000){
			return String.valueOf((long) (value/unitFactor));
		}
		else if(unitFactor==1000000){
			//return String.format("%.1f",  ((double)value)/unitFactor);
			return String.valueOf((long) (value/unitFactor));
		}
		return "-";
	}
	
	public long getValueOfString(String value,String key){
		
		if(value.equals("") || value.equals("-"))
			return Long.MIN_VALUE;
		try{
			if(key.equals(IncomeStatementPoint.FIELD_Employees)){
				return Long.valueOf(value);
			}
			else if(key.equals(IncomeStatementPoint.FIELD_EarningsPerShare)){
				double val=Double.valueOf(value)*100;
				return (long) val;
			}
			
			double val=Double.valueOf(value)*unitFactor;
			return (long) val;
		}
		catch(NumberFormatException ex){
			return Long.MIN_VALUE;
		}
	}
	
	
	
	private boolean isCompositeAbleToReact(String rate_uuid){
		if (this.isDisposed())
			return false;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || stock == null )
			return false;
		if (!incoming.getUUID().equals(stock.getUUID()))
			return false;
		
		return true;
	}
	
	@Inject
	private void financialDataLoaded(
			@Optional @UIEventTopic(IEventConstant.FINANCIAL_DATA_LOADED) String rate_uuid) {

		if(!isCompositeAbleToReact(rate_uuid))return;
		/*
		HistoricalIncomeStatement his=stock.getFinancials().getIncomeStatement().getQuaterlyPoints();
		for(DatePoint point:his){
			if(point instanceof IncomeStatementPoint){
				IncomeStatementPoint isp=(IncomeStatementPoint) point;
				System.out.println(isp);
			}
		}
		*/
		/*
		HistoricalCashFlow his=stock.getFinancials().getCashFlow().getQuaterlyPoints();
		for(DatePoint point:his){
			if(point instanceof CashFlowPoint){
				CashFlowPoint isp=(CashFlowPoint) point;
				System.out.println(isp);
			}
		}
		*/
		
		createColumns();
		firstVisibleColumn=0;
		refreshColumnsVisibility();
		
	}
}
