package com.munch.exchange.parts.financials;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IFinancialsProvider;

public class FinancialReportParserComposite extends Composite {
	
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IFinancialsProvider financialsProvider;
	
	private Stock stock;
	
	private ReportReaderConfiguration config;
	
	private Text textCompanyWebsite;
	private StyledText styledText;
	
	
	@Inject
	public FinancialReportParserComposite(Composite parent,ExchangeRate rate) {
		super(parent, SWT.NONE);
		this.stock=(Stock) rate;
		this.config=stock.getFinancials().getReportReaderConfiguration();
		setLayout(new GridLayout(1, false));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeLeft = new Composite(sashForm, SWT.NONE);
		compositeLeft.setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(compositeLeft, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(2, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		compositeHeader.setBounds(0, 0, 64, 64);
		
		Label lblCompany = new Label(compositeHeader, SWT.NONE);
		lblCompany.setSize(100, 15);
		lblCompany.setText("Company Website:");
		
		Button btnSave = new Button(compositeHeader, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stock.getFinancials().getReportReaderConfiguration().setWebsite(textCompanyWebsite.getText());
				styledText.append(config.getWebsite()+"\n");
				//styledText.append("Stock: "+stock.getFinancials().getReportReaderConfiguration().getWebsite());
				financialsProvider.saveReportReaderConfiguration(stock);
				
			}
		});
		btnSave.setText("Save");
		
		textCompanyWebsite = new Text(compositeHeader, SWT.BORDER);
		textCompanyWebsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textCompanyWebsite.setSize(138, 21);
		
		Button buttonCompWeb = new Button(compositeHeader, SWT.NONE);
		buttonCompWeb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		buttonCompWeb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String content=financialsProvider.getHtmlContent(textCompanyWebsite.getText());
				styledText.append("* * * COMPANY WEBSITE * * *\n"+content+"\n");
			}
		});
		buttonCompWeb.setText(">");
		
		styledText = new StyledText(compositeLeft, SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setAlwaysShowScrollBars(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(1, false));
		
		TreeViewer treeViewer = new TreeViewer(compositeRight, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setBounds(0, 0, 85, 85);
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnItem = treeViewerColumn.getColumn();
		trclmnItem.setWidth(100);
		trclmnItem.setText("Item");
		sashForm.setWeights(new int[] {266, 271});
		
		
	}
	
	
	private void initFields(){
		if(config.getWebsite()!=null)
			textCompanyWebsite.setText(config.getWebsite());
	}
	
	
	@Inject
	private void financialDataLoaded(
			@Optional @UIEventTopic(IEventConstant.FINANCIAL_DATA_LOADED) String rate_uuid) {

		if(!isCompositeAbleToReact(rate_uuid))return;
		
		this.config=stock.getFinancials().getReportReaderConfiguration();
		
		initFields();
		
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
	
}
