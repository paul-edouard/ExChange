package com.munch.exchange.parts.composite;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.munch.exchange.model.core.ExchangeRate;

public class RateCommonInfoGroup extends Group {
	private DataBindingContext m_bindingContext;
	
	ExchangeRate rate;
	private Label lblRateName;
	private Label lblRateSymbol;
	private Label lblRateStockExchange;
	private Label lblRateType;
	private DateTime dateTimeStart;
	private DateTime dateTimeEnd;

	public RateCommonInfoGroup(Composite parent, int style,ExchangeRate rate) {
		super(parent, SWT.NONE);
		
		this.rate=rate;
		
		setText("Info");
		setLayout(new GridLayout(2, false));
		
		Label label_Name = new Label(this, SWT.NONE);
		label_Name.setText("Name:");
		
		lblRateName = new Label(this, SWT.NONE);
		lblRateName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblRateName.setText("Rate Name");
		
		Label label_2 = new Label(this, SWT.NONE);
		label_2.setText("Symbol:");
		
		lblRateSymbol = new Label(this, SWT.NONE);
		lblRateSymbol.setText("Rate Symbol");
		
		Label label_4 = new Label(this, SWT.NONE);
		label_4.setText("Stock Exchange:");
		
		lblRateStockExchange = new Label(this, SWT.NONE);
		lblRateStockExchange.setText("Rate StockExchange");
		
		Label label_6 = new Label(this, SWT.NONE);
		label_6.setText("Type:");
		
		lblRateType = new Label(this, SWT.NONE);
		lblRateType.setText("Rate Type");
		
		Label lblStart = new Label(this, SWT.NONE);
		lblStart.setText("Start:");
		
		dateTimeStart = new DateTime(this, SWT.NONE);
		dateTimeStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		dateTimeStart.setEnabled(false);
		
		Label lblEnd = new Label(this, SWT.NONE);
		lblEnd.setText("End");
		
		dateTimeEnd = new DateTime(this, SWT.BORDER);
		dateTimeEnd.setEnabled(false);
		dateTimeEnd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(this, SWT.NONE);
		m_bindingContext = initDataBindings();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLabel_1ObserveWidget = WidgetProperties.text().observe(lblRateName);
		IObservableValue nameRateObserveValue = BeanProperties.value("name").observe(rate);
		bindingContext.bindValue(observeTextLabel_1ObserveWidget, nameRateObserveValue, null, null);
		//
		IObservableValue observeTextLabel_3ObserveWidget = WidgetProperties.text().observe(lblRateSymbol);
		IObservableValue symbolRateObserveValue = BeanProperties.value("symbol").observe(rate);
		bindingContext.bindValue(observeTextLabel_3ObserveWidget, symbolRateObserveValue, null, null);
		//
		IObservableValue observeTextLabel_5ObserveWidget = WidgetProperties.text().observe(lblRateStockExchange);
		IObservableValue stockExchangeRateObserveValue = BeanProperties.value("stockExchange").observe(rate);
		bindingContext.bindValue(observeTextLabel_5ObserveWidget, stockExchangeRateObserveValue, null, null);
		//
		IObservableValue observeTextLabel_7ObserveWidget = WidgetProperties.text().observe(lblRateType);
		IObservableValue tagNameRateObserveValue = BeanProperties.value("tagName").observe(rate);
		bindingContext.bindValue(observeTextLabel_7ObserveWidget, tagNameRateObserveValue, null, null);
		//
		IObservableValue observeSelectionDateTimeStartObserveWidget = WidgetProperties.selection().observe(dateTimeStart);
		IObservableValue startRateObserveValue = BeanProperties.value("start").observe(rate);
		bindingContext.bindValue(observeSelectionDateTimeStartObserveWidget, startRateObserveValue, null, null);
		//
		IObservableValue observeSelectionDateTimeStartObserveWidget_1 = WidgetProperties.selection().observe(dateTimeStart);
		IObservableValue starttimeRateObserveValue = PojoProperties.value("start.time").observe(rate);
		bindingContext.bindValue(observeSelectionDateTimeStartObserveWidget_1, starttimeRateObserveValue, null, null);
		//
		IObservableValue observeSelectionDateTimeEndObserveWidget = WidgetProperties.selection().observe(dateTimeEnd);
		IObservableValue endtimeRateObserveValue = PojoProperties.value("end.time").observe(rate);
		bindingContext.bindValue(observeSelectionDateTimeEndObserveWidget, endtimeRateObserveValue, null, null);
		//
		return bindingContext;
	}
}
