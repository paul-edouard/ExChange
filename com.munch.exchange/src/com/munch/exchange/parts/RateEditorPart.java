 
package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.composite.OverviewRateChart;
import com.munch.exchange.parts.composite.RateCommonInfoGroup;
import com.munch.exchange.parts.composite.RateTitle;
import com.munch.exchange.parts.composite.StockInfoGroup;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IKeyStatisticProvider;

public class RateEditorPart {
	
	private static Logger logger = Logger.getLogger(RateEditorPart.class);
	
	private DataBindingContext m_bindingContext;
	
	
	public static final String RATE_EDITOR_ID="com.munch.exchange.partdescriptor.rateeditor";
	
	@Inject
	ExchangeRate rate;
	
	@Inject
	IEclipseContext context;
	
	@Inject
	IKeyStatisticProvider keyStatisticProvider; 
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	RateTitle compositeTitle;
	
	//private Label lblTitle;
	
	@Inject
	public RateEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent,Shell shell) {
		
		
		
		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBounds(0, 0, 122, 43);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Overview");
		
		Composite compositeOverview = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(compositeOverview);
		compositeOverview.setLayout(new GridLayout(1, false));
		
		
		//Create a context instance
		IEclipseContext localContact=EclipseContextFactory.create();
		localContact.set(Composite.class, compositeOverview);
		localContact.setParent(context);
		
		compositeTitle=ContextInjectionFactory.make( RateTitle.class,localContact);
		//Composite compositeTitle = new Composite(compositeOverview, SWT.NONE);
		compositeTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Composite composite_Info = new Composite(compositeOverview, SWT.NONE);
		composite_Info.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite_Info.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		/*RateCommonInfoGroup grpInfo =*/ new RateCommonInfoGroup(composite_Info, SWT.NONE,rate);
		
		if(rate instanceof Stock){
			//keyStatisticProvider.load((Stock) rate);
		
			new StockInfoGroup(composite_Info,(Stock) rate,shell,exchangeRateProvider);
		}
		
		OverviewRateChart chart=new OverviewRateChart(compositeOverview);
		chart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		m_bindingContext = initDataBindings();
		//TODO Your code here
	}
	
	/*
	@Inject
	private void quoteLoaded(@Optional  @UIEventTopic(IEventConstant.QUOTE_LOADED) String rate_uuid ){
		logger.info("Message recieved: Quote loaded!");
	}
	
	@Inject
	private void quoteUpdate(@Optional  @UIEventTopic(IEventConstant.QUOTE_UPDATE) String rate_uuid ){
		logger.info("Message recieved: Quote update!");
	}
	*/
	
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
		/*
		IObservableValue observeTextLblNewLabelObserveWidget = WidgetProperties.text().observe(lblTitle);
		IObservableValue fullNameRateObserveValue = BeanProperties.value("fullName").observe(rate);
		bindingContext.bindValue(observeTextLblNewLabelObserveWidget, fullNameRateObserveValue, null, null);
		*/
		//
		return bindingContext;
	}
}