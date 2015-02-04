package com.munch.exchange.parts.financials;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import javax.crypto.spec.PSource;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration.SearchKeyValEl;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IFinancialsProvider;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wb.swt.SWTResourceManager;

public class FinancialReportParserComposite extends Composite {
	
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IFinancialsProvider financialsProvider;
	
	
	
	private Stock stock;
	
	private ReportReaderConfiguration config;
	
	private Financials financials;
	
	private StockFinancialsContentProvider contentProvider=new StockFinancialsContentProvider();
	
	
	private int period_year=0;
	private int period_qua=0;
	
	private String lastContent;
	
	
	private Text textCompanyWebsite;
	private StyledText styledText;
	private Text txtReportwebsite;
	private TreeViewer treeViewer;
	private Tree tree;
	private Button btnQuaterly;
	private Button btnAnnualy;
	private Combo comboDocuments;
	private Text textPattern;
	private Button btnPeriod;
	private Button btnBack;
	private Button btnNext;
	private Text textPeriod;
	private TreeColumn trclmnValue;
	
	
	public Stock getStock() {
		return stock;
	}

	public ReportReaderConfiguration getConfig() {
		return config;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	

	public Button getBtnQuaterly() {
		return btnQuaterly;
	}

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
		btnSave.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
			}
		});
		btnSave.setText("Save");
		
		textCompanyWebsite = new Text(compositeHeader, SWT.BORDER);
		textCompanyWebsite.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});
		textCompanyWebsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textCompanyWebsite.setSize(138, 21);
		
		Button buttonCompWeb = new Button(compositeHeader, SWT.NONE);
		buttonCompWeb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		buttonCompWeb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String content=financialsProvider.getHtmlContent(textCompanyWebsite.getText());
				config.setWebsite(textCompanyWebsite.getText());
				//styledText.append("* * * COMPANY WEBSITE * * *\n"+content+"\n");
				eventBroker.send(IEventConstant.FINANCIAL_DATA_COMPANY_SIDE, stock.getUUID());
			}
		});
		buttonCompWeb.setText(">>");
		
		Composite composite = new Composite(compositeHeader, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_composite = new GridLayout(2, false);
		composite.setLayout(gl_composite);
		
		btnQuaterly = new Button(composite, SWT.RADIO);
		btnQuaterly.setEnabled(false);
		btnQuaterly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnQuaterly.getSelection()){
					btnAnnualy.setSelection(false);
					btnQuaterly.setEnabled(false);
					btnAnnualy.setEnabled(true);
					refreshAfterPeriod();
				}
			}
		});
		btnQuaterly.setSelection(true);
		btnQuaterly.setSize(66, 16);
		btnQuaterly.setText("Quaterly");
		
		btnAnnualy = new Button(composite, SWT.RADIO);
		btnAnnualy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnAnnualy.getSelection()){
					btnQuaterly.setSelection(false);
					btnAnnualy.setEnabled(false);
					btnQuaterly.setEnabled(true);
					refreshAfterPeriod();
				}
			}
		});
		btnAnnualy.setText("Annualy");
		new Label(compositeHeader, SWT.NONE);
		
		Label lblReportWebsite = new Label(compositeHeader, SWT.NONE);
		lblReportWebsite.setText("Report Website:");
		new Label(compositeHeader, SWT.NONE);
		
		txtReportwebsite = new Text(compositeHeader, SWT.BORDER);
		txtReportwebsite.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});
		txtReportwebsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnRepweb = new Button(compositeHeader, SWT.NONE);
		btnRepweb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String content=financialsProvider.getHtmlContent(txtReportwebsite.getText());
				styledText.setText("");
				styledText.append("* * * REPORT WEBSITE * * *\n"+content+"\n");
				
				comboDocuments.removeAll();
				String searchPeriod="";
				if(btnPeriod.getSelection())searchPeriod=textPeriod.getText();
					
				String[] docs=ReportReaderConfiguration.searchDocuments(content, textPattern.getText(),searchPeriod);
				for(int i=0;i<docs.length;i++){
					comboDocuments.add(docs[i]);
					styledText.append("* * * DOC: "+docs[i]+"\n");
					
				}
				
				if(docs.length>0){
					comboDocuments.select(0);
				}
				
				loadAndAnalyseDocument();
				
			}
		});
		btnRepweb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnRepweb.setText(">>");
		
		Group grpDocuments = new Group(compositeLeft, SWT.NONE);
		grpDocuments.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		grpDocuments.setLayout(new GridLayout(1, false));
		grpDocuments.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpDocuments.setText("Documents");
		
		Composite compositeDoc1 = new Composite(grpDocuments, SWT.NONE);
		compositeDoc1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeDoc1.setLayout(new GridLayout(3, false));
		
		btnPeriod = new Button(compositeDoc1, SWT.CHECK);
		btnPeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				textPeriod.setEnabled(btnPeriod.getSelection());
				btnBack.setEnabled(btnPeriod.getSelection());
				btnNext.setEnabled(btnPeriod.getSelection());
				
			}
		});
		btnPeriod.setText("period");
		
		textPeriod = new Text(compositeDoc1, SWT.BORDER);
		textPeriod.setEnabled(false);
		textPeriod.setEditable(false);
		textPeriod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_2 = new Composite(compositeDoc1, SWT.NONE);
		composite_2.setSize(48, 35);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.horizontalSpacing = 0;
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.verticalSpacing = 0;
		composite_2.setLayout(gl_composite_2);
		
		btnBack = new Button(composite_2, SWT.NONE);
		btnBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnQuaterly.getSelection()){
					period_qua--;
					if(period_qua==0){
						period_qua=3;period_year--;
					}
				}
				else{period_year--;}
				
				refreshPeriod();
			}
		});
		btnBack.setEnabled(false);
		btnBack.setText("<");
		
		btnNext = new Button(composite_2, SWT.NONE);
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnQuaterly.getSelection()){
					period_qua++;
					if(period_qua>3){
						period_qua=1;period_year++;
					}
				}
				else{period_year++;}
				
				refreshPeriod();
				
			}
		});
		btnNext.setEnabled(false);
		btnNext.setText(">");
		
		Composite composite_3 = new Composite(grpDocuments, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_3.setLayout(new GridLayout(2, false));
		
		Label lblPattern = new Label(composite_3, SWT.NONE);
		lblPattern.setText("Pattern: ");
		
		textPattern = new Text(composite_3, SWT.BORDER);
		textPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboDocuments = new Combo(grpDocuments, SWT.NONE);
		comboDocuments.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		styledText = new StyledText(compositeLeft, SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setAlwaysShowScrollBars(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(compositeRight, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(2, false));
		
		Button btnReparse = new Button(composite_1, SWT.NONE);
		btnReparse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parseDocument();
			}
		});
		btnReparse.setText("Reparse");
		
		Button btnSendToTable = new Button(composite_1, SWT.NONE);
		btnSendToTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
				
			}
		});
		btnSendToTable.setText("Send to Table");
		
		treeViewer = new TreeViewer(compositeRight,  SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL| SWT.FULL_SELECTION);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setInput(contentProvider.getRoot());
		treeViewer.expandToLevel(2);
		
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setBounds(0, 0, 85, 85);
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new MainColumnLabelProvider());
		TreeColumn trclmnItem = treeViewerColumn.getColumn();
		trclmnItem.setWidth(200);
		trclmnItem.setText("Item");
		
		TreeViewerColumn treeViewerColumnActivation = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnActivation = treeViewerColumnActivation.getColumn();
		treeViewerColumnActivation.setLabelProvider(new ActivationColumnLabelProvider());
		treeViewerColumnActivation.setEditingSupport(
				new FinancialReportEditingSupport(this, FinancialReportEditingSupport.FIELD_Activation));
		trclmnActivation.setWidth(100);
		trclmnActivation.setText("Activation");
		
		TreeViewerColumn treeViewerColumnLineStart = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnLineStart = treeViewerColumnLineStart.getColumn();
		treeViewerColumnLineStart.setLabelProvider(new LineStartColumnLabelProvider());
		treeViewerColumnLineStart.setEditingSupport(
				new FinancialReportEditingSupport(this, FinancialReportEditingSupport.FIELD_StartLineWith));
		trclmnLineStart.setWidth(100);
		trclmnLineStart.setText("Line start");
		
		TreeViewerColumn treeViewerColumnPosition = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnPosition = treeViewerColumnPosition.getColumn();
		treeViewerColumnPosition.setLabelProvider(new PositionColumnLabelProvider());
		treeViewerColumnPosition.setEditingSupport(
				new FinancialReportEditingSupport(this, FinancialReportEditingSupport.FIELD_Position));
		trclmnPosition.setWidth(100);
		trclmnPosition.setText("Position");
		
		TreeViewerColumn treeViewerColumnFactor = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnFactor = treeViewerColumnFactor.getColumn();
		treeViewerColumnFactor.setLabelProvider(new FactorColumnLabelProvider());
		treeViewerColumnFactor.setEditingSupport(
				new FinancialReportEditingSupport(this, FinancialReportEditingSupport.FIELD_Factor));
		trclmnFactor.setWidth(100);
		trclmnFactor.setText("Factor");
		
		TreeViewerColumn treeViewerColumnParsedValue = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnParsedValue =treeViewerColumnParsedValue.getColumn();
		treeViewerColumnParsedValue.setLabelProvider(new ParsedValueColumnLabelProvider());
		trclmnParsedValue.setWidth(100);
		trclmnParsedValue.setText("Parsed Value");
		
		TreeViewerColumn treeViewerColumnValu = new TreeViewerColumn(treeViewer, SWT.NONE);
		trclmnValue =treeViewerColumnValu.getColumn();
		treeViewerColumnValu.setLabelProvider(new ValueColumnLabelProvider());
		trclmnValue.setWidth(100);
		trclmnValue.setText("Value");
		sashForm.setWeights(new int[] {266, 271});
		
		
		refresh();
		
	}
	
	
	private void refreshPeriod(){
		String periodString="";
		if(btnQuaterly.getSelection()){
			periodString="Q"+String.valueOf(period_qua)+"-"+String.valueOf(period_year);
		}
		else{
			periodString=String.valueOf(period_year);
		}
		
		textPeriod.setText(periodString);
		trclmnValue.setText(periodString);
		treeViewer.refresh();
		
	}
	
	
	private void loadAndAnalyseDocument(){
		if(comboDocuments.getItemCount()==1)
			comboDocuments.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		else{
			comboDocuments.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			return;
		}
		
		String url=textCompanyWebsite.getText()+comboDocuments.getItem(0);
		String document=financialsProvider.loadReportDocument(stock, url);
		
		styledText.setText("");
		styledText.append("* * * DOC:\n"+document+"\n");
		lastContent=document;
		
		parseDocument();
		
	}
	
	private void parseDocument() {
		// Analyze the document
		LinkedList<SearchKeyValEl> allFoundElts = null;
		if (btnQuaterly.getSelection()) {
			allFoundElts=config.parseQuaterlyDocument(lastContent);
		} else {
			allFoundElts=config.parseAnnualyDocument(lastContent);
		}
		if(allFoundElts==null)return;

		for (SearchKeyValEl el : allFoundElts) {
				styledText.append(el+"\n");
		}
		//TODO
		refreshAfterPeriod();
	}
	
	
	public void refresh(){
		if(config.getWebsite()!=null)
			textCompanyWebsite.setText(config.getWebsite());
		
		if(config.getSelectedPeriodType()!=null){
			if(config.getSelectedPeriodType()==FinancialPoint.PeriodeTypeQuaterly){
				btnAnnualy.setSelection(false);
				btnQuaterly.setSelection(true);
				btnAnnualy.setEnabled(true);
				btnQuaterly.setEnabled(false);
			}
			else if(config.getSelectedPeriodType()==FinancialPoint.PeriodeTypeAnnual){
				btnAnnualy.setSelection(true);
				btnQuaterly.setSelection(false);
				btnAnnualy.setEnabled(false);
				btnQuaterly.setEnabled(true);
			}
		}
		
		period_year=getExpectedYear();
		period_qua=getExpectedQuartal();
		
		refreshPeriod();
		
		refreshAfterPeriod();
		
	}
	
	private int getExpectedQuartal() {
		Calendar date = Calendar.getInstance();
		if (date.get(Calendar.MONTH) > 0 && date.get(Calendar.MONTH) <= 3) {
			return 3;
		} else if (date.get(Calendar.MONTH) > 3
				&& date.get(Calendar.MONTH) <= 6) {
			return 1;
		} else if (date.get(Calendar.MONTH) > 6
				&& date.get(Calendar.MONTH) <= 9) {
			return 2;
		} else {
			return 3;
		}
	}
	
	private int getExpectedYear(){
		Calendar date=Calendar.getInstance();
		if(date.get(Calendar.MONTH)>0 && date.get(Calendar.MONTH)<=3){
			return date.get(Calendar.YEAR);
		}
		else{
			return date.get(Calendar.YEAR)+1;
		}
	}
	
	
	
	public void refreshAfterPeriod(){
		if(btnQuaterly.getSelection()){
			
			if(period_year==0){
				Calendar date = Calendar.getInstance();
				period_year=date.get(Calendar.YEAR);
			}
			
			if(config.getQuaterlyReportWebsite()!=null)
				txtReportwebsite.setText(config.getQuaterlyReportWebsite());
			if(config.getQuaterlyPattern()!=null)
				textPattern.setText(config.getQuaterlyPattern());
			if(config.isQuaterlySearchPeriodActivated()){
				btnPeriod.setSelection(config.isQuaterlySearchPeriodActivated());
				textPeriod.setEnabled(btnPeriod.getSelection());
				btnBack.setEnabled(btnPeriod.getSelection());
				btnNext.setEnabled(btnPeriod.getSelection());
			}
			if(config.getQuaterlySearchPeriod()!=null &&
					!config.getQuaterlySearchPeriod().isEmpty() &&
					period_year==0){
				textPeriod.setText(config.getQuaterlySearchPeriod());
				trclmnValue.setText(config.getQuaterlySearchPeriod());
				String[] tockens=textPeriod.getText().split("-");
				if(tockens.length==2){
					period_qua=Integer.valueOf(tockens[0].replace("Q", ""));
					period_year=Integer.valueOf(tockens[1]);
				}
			}
			else{
				textPeriod.setText("Q"+String.valueOf(period_qua)+"-"+String.valueOf(period_year));
				trclmnValue.setText("Q"+String.valueOf(period_qua)+"-"+String.valueOf(period_year));
			}
			
			
		}
		else{
			if(config.getAnnualyReportWebsite()!=null)
				txtReportwebsite.setText(config.getAnnualyReportWebsite());
			if(config.getAnnualyPattern()!=null)
				textPattern.setText(config.getAnnualyPattern());
			if(config.isAnnualySearchPeriodActivated()){
				btnPeriod.setSelection(config.isAnnualySearchPeriodActivated());
				textPeriod.setEnabled(btnPeriod.getSelection());
				btnBack.setEnabled(btnPeriod.getSelection());
				btnNext.setEnabled(btnPeriod.getSelection());
			}
			if(config.getAnnualySearchPeriod()!=null &&
					!config.getAnnualySearchPeriod().isEmpty() &&
					period_year==0){
				textPeriod.setText(config.getAnnualySearchPeriod());
				trclmnValue.setText(config.getAnnualySearchPeriod());
				period_year=Integer.valueOf(textPeriod.getText());
			}
			else{
				textPeriod.setText(String.valueOf(period_year));
				trclmnValue.setText(String.valueOf(period_year));
			}
		}
		
		treeViewer.refresh();
	}
	
	
	private void save(){
		//Save the Company web site
		config.setWebsite(textCompanyWebsite.getText());
		styledText.append("Saving: "+config.getWebsite()+"\n");
		
		//Save the current period type
		if(btnQuaterly.getSelection())
			config.setSelectedPeriodType(FinancialPoint.PeriodeTypeQuaterly);
		else
			config.setSelectedPeriodType(FinancialPoint.PeriodeTypeAnnual);
		
		//Save the Report web site
		if(btnQuaterly.getSelection())
			config.setQuaterlyReportWebsite(txtReportwebsite.getText());
		else
			config.setAnnualyReportWebsite(txtReportwebsite.getText());
		
		//Save the pattern
		if(btnQuaterly.getSelection())
			config.setQuaterlyPattern(textPattern.getText());
		else
			config.setAnnualyPattern(textPattern.getText());
		
		//Save the period selection
		if(btnQuaterly.getSelection())
			config.setQuaterlySearchPeriodActivated(btnPeriod.getSelection());
		else
			config.setAnnualySearchPeriodActivated(btnPeriod.getSelection());
		
		//Save the period 
		if(btnQuaterly.getSelection())
			config.setQuaterlySearchPeriod(textPeriod.getText());
		else
			config.setAnnualySearchPeriod(textPeriod.getText());
		
		
		financialsProvider.saveReportReaderConfiguration(stock);
	}
	
	
	
	@Inject
	private void financialDataLoaded(
			@Optional @UIEventTopic(IEventConstant.FINANCIAL_DATA_LOADED) String rate_uuid) {

		if(!isCompositeAbleToReact(rate_uuid))return;
		
		this.config=stock.getFinancials().getReportReaderConfiguration();
		this.financials=stock.getFinancials();
		refresh();
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
	
	
	//################################
	//##     ColumnLabelProvider    ##
	//################################
	
	class ActivationColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				if (btnQuaterly.getSelection()) {
					SearchKeyValEl el = config.getQuaterlySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return el.activation;
				} else {
					SearchKeyValEl el = config.getAnnualySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return el.activation;
				}
			}
			return "";
		}
	}
	
	class LineStartColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				if (btnQuaterly.getSelection()) {
					SearchKeyValEl el = config.getQuaterlySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return el.startLineWith;
				} else {
					SearchKeyValEl el = config.getAnnualySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return el.startLineWith;
				}
			}
			return "";
		}
	}
	
	class PositionColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				if (btnQuaterly.getSelection()) {
					SearchKeyValEl el = config.getQuaterlySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return String.valueOf(el.position);
				} else {
					SearchKeyValEl el = config.getAnnualySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return String.valueOf(el.position);
				}
			}
			return "";
		}
	}
	
	class FactorColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				if (btnQuaterly.getSelection()) {
					SearchKeyValEl el = config.getQuaterlySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return String.valueOf(el.factor);
				} else {
					SearchKeyValEl el = config.getAnnualySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					return String.valueOf(el.factor);
				}
			}
			return "";
		}
	}
	
	class ValueColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if(financials==null)return "";
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				if (btnQuaterly.getSelection()) {
					Calendar date=null;
					
					if(period_qua==1){
						date=financials.getQ1Date(period_year);
					}
					else if(period_qua==2){
						date=financials.getQ2Date(period_year);
					}
					else if(period_qua==3){
						date=financials.getQ3Date(period_year);
					}
					if(date==null)return "";
					
					//financials.getQ1Date(year)
					long val=financials.getValue(FinancialPoint.PeriodeTypeQuaterly, date, entity.fieldKey, entity.sectorKey);
					if(val!=Long.MIN_VALUE)
						return String.valueOf(val);
				} else {
					
					Calendar date=financials.getQ4Date(period_year);
					if(date==null)return "";
					
					long val=financials.getValue(FinancialPoint.PeriodeTypeAnnual, date, entity.fieldKey, entity.sectorKey);
					if(val!=Long.MIN_VALUE)
						return String.valueOf(val);
				}
			}
			return "";
		}
	}
	
	class ParsedValueColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				if (btnQuaterly.getSelection()) {
					
					
					SearchKeyValEl el = config.getQuaterlySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					if(el.value!=Long.MIN_VALUE)
						return String.valueOf(el.value);
					return "";
				} else {
					SearchKeyValEl el = config.getAnnualySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					if(el.value!=Long.MIN_VALUE)
						return String.valueOf(el.value);
					return "";
				}
			}
			return "";
		}
	}
	
	
	
}
