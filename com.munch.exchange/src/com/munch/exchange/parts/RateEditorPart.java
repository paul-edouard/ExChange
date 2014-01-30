 
package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.parts.composite.RateCommonInfoGroup;

public class RateEditorPart {
	private DataBindingContext m_bindingContext;
	
	/*
	@Inject
	private EModelService modelService;
	
	@Inject
	private MApplication application;
	*/
	
	public static final String RATE_EDITOR_ID="com.munch.exchange.partdescriptor.rateeditor";
	
	@Inject
	ExchangeRate rate;
	private Label lblTitle;
	
	@Inject
	public RateEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBounds(0, 0, 122, 43);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Overview");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		lblTitle = new Label(composite, SWT.NONE);
		lblTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTitle.setBackground(SWTResourceManager.getColor(0, 0, 255));
		lblTitle.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblTitle.setAlignment(SWT.CENTER);
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblTitle.setText("New Label");
		
		Composite composite_info = new Composite(composite, SWT.NONE);
		composite_info.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_info.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		RateCommonInfoGroup grpInfo = new RateCommonInfoGroup(composite_info, SWT.NONE,rate);
		
		m_bindingContext = initDataBindings();
		//TODO Your code here
	}
	
	
	@PreDestroy
	public void preDestroy() {

		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblNewLabelObserveWidget = WidgetProperties.text().observe(lblTitle);
		IObservableValue fullNameRateObserveValue = BeanProperties.value("fullName").observe(rate);
		bindingContext.bindValue(observeTextLblNewLabelObserveWidget, fullNameRateObserveValue, null, null);
		//
		return bindingContext;
	}
}