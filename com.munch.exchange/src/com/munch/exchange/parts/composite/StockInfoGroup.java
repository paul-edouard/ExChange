package com.munch.exchange.parts.composite;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.dialog.SearchParentDialog;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IExchangeRateProvider;

public class StockInfoGroup extends Group {
	private DataBindingContext m_bindingContext;
	
	
	Stock stock;
	Shell shell;
	IExchangeRateProvider exchangeRateProvider;
	
	private Label lblStocksector;
	private Label lblStockindustry;
	private Label lblStockparentname;
	private Label lblStockparentsymbol;
	private Button btnSetParent;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StockInfoGroup(Composite parent, Stock stock, Shell shell, IExchangeRateProvider exchangeRateProvider) {
		super(parent, SWT.NONE);
		setText("Stock Info");
		this.stock=stock;
		this.shell=shell;
		this.exchangeRateProvider=exchangeRateProvider;
		
		
		setLayout(new GridLayout(2, false));
		
		Label lblIndustry = new Label(this, SWT.NONE);
		lblIndustry.setText("Industry:");
		
		lblStockindustry = new Label(this, SWT.NONE);
		lblStockindustry.setText("Stock_Industry");
		
		Label lblSector = new Label(this, SWT.NONE);
		lblSector.setText("Sector:");
		
		lblStocksector = new Label(this, SWT.NONE);
		lblStocksector.setText("Stock_Sector");
		
		Label lblParentName = new Label(this, SWT.NONE);
		lblParentName.setText("Parent Name:");
		
		lblStockparentname = new Label(this, SWT.NONE);
		lblStockparentname.setText("Stock_ParentName");
		
		Label lblParentSymbol = new Label(this, SWT.NONE);
		lblParentSymbol.setText("Parent Symbol:");
		
		lblStockparentsymbol = new Label(this, SWT.NONE);
		lblStockparentsymbol.setText("Stock_ParentSymbol");
		new Label(this, SWT.NONE);
		
		btnSetParent = new Button(this, SWT.NONE);
		btnSetParent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SearchParentDialog dialog=new SearchParentDialog(StockInfoGroup.this.shell,StockInfoGroup.this.exchangeRateProvider);
				
				if (dialog.open() == Window.OK) {
					//dialog.getRate()
					StockInfoGroup.this.stock.setParentName(dialog.getStock().getName());
					StockInfoGroup.this.stock.setParentSymbol(dialog.getStock().getSymbol());
					
					StockInfoGroup.this.exchangeRateProvider.save(StockInfoGroup.this.stock);
					
					
				}
				
			}
		});
		btnSetParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSetParent.setText("SetParent");
		m_bindingContext = initDataBindings();
		
		

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblStcksectorObserveWidget = WidgetProperties.text().observe(lblStocksector);
		IObservableValue sectorStockObserveValue = BeanProperties.value("sector").observe(stock);
		bindingContext.bindValue(observeTextLblStcksectorObserveWidget, sectorStockObserveValue, null, null);
		//
		IObservableValue observeTextLblStockindustryObserveWidget = WidgetProperties.text().observe(lblStockindustry);
		IObservableValue industryStockObserveValue = BeanProperties.value("industry").observe(stock);
		bindingContext.bindValue(observeTextLblStockindustryObserveWidget, industryStockObserveValue, null, null);
		//
		IObservableValue observeTextLblStockparentnameObserveWidget = WidgetProperties.text().observe(lblStockparentname);
		IObservableValue parentNameStockObserveValue = BeanProperties.value("parentName").observe(stock);
		bindingContext.bindValue(observeTextLblStockparentnameObserveWidget, parentNameStockObserveValue, null, null);
		//
		IObservableValue observeTextLblStockparentsymbolObserveWidget = WidgetProperties.text().observe(lblStockparentsymbol);
		IObservableValue parentSymbolStockObserveValue = BeanProperties.value("parentSymbol").observe(stock);
		bindingContext.bindValue(observeTextLblStockparentsymbolObserveWidget, parentSymbolStockObserveValue, null, null);
		//
		return bindingContext;
	}
}
