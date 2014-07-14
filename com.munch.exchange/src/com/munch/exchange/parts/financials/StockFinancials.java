package com.munch.exchange.parts.financials;

import java.util.Calendar;
import java.util.LinkedList;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.CashFlowPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.HistoricalCashFlow;
import com.munch.exchange.model.core.financials.HistoricalIncomeStatement;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;
import com.munch.exchange.services.IExchangeRateProvider;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;

public class StockFinancials extends Composite {
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	
	private StockFinancialsContentProvider contentProvider=new StockFinancialsContentProvider();
	
	private Stock stock;
	private TreeViewer treeViewer;
	private Tree tree;
	private Button btnAddColumn;
	private Button btnQuaterly;
	private Button btnAnnualy;
	private LinkedList<TreeViewerColumn> columns=new LinkedList<TreeViewerColumn>();
	private Label lblUnit;
	private Combo comboUnit;
	
	private double unitFactor=1;
	
	private String modus;
	
	@Inject
	public StockFinancials(Composite parent,ExchangeRate rate) {
		super(parent, SWT.NONE);
		this.stock=(Stock) rate;
		setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(this, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(5, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnQuaterly = new Button(compositeHeader, SWT.RADIO);
		btnQuaterly.setEnabled(false);
		btnQuaterly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnQuaterly.getSelection()){
					btnAnnualy.setSelection(false);
					btnQuaterly.setEnabled(false);
					btnAnnualy.setEnabled(true);
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
		unitFactor=1000000;
		
		btnAddColumn = new Button(compositeHeader, SWT.NONE);
		btnAddColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnAddColumn.setText("Add Column");
		
		treeViewer = new TreeViewer(this,  SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setInput(contentProvider.getRoot());
		tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		//Main Column
		TreeViewerColumn mainColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		mainColumn.setLabelProvider(new mainColumnLabelProvider());
		TreeColumn trclmnName = mainColumn.getColumn();
		trclmnName.setWidth(300);
		trclmnName.setText("Items");
		
		treeViewer.refresh();
		
	}
	
	
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public String getModus() {
		return modus;
	}


	private void refreshColumns(){
		removeColumns();
		createColumns();
		treeViewer.refresh();
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
			trclmn.setWidth(150);
			trclmn.setText(DateTool.dateToDayString(date));
			
			columns.add(dateColumn);
			
		}
		
		treeViewer.refresh();
		treeViewer.expandToLevel(2);
		
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
				long val=stock.getFinancials().getValue(modus,date, entity.fieldKey,entity.sectorKey);
				if(val==0)return "-";
				return getStringOfValue(val);
			}
			return element == null ? "" : element.toString();
		}
		
		
		private String getStringOfValue(long value){
			
			if(value==Long.MIN_VALUE)return "";
			
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
		
	}
}
