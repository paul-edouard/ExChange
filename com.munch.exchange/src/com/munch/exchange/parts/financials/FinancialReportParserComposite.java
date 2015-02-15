package com.munch.exchange.parts.financials;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import javax.crypto.spec.PSource;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
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
import com.munch.exchange.model.core.financials.CashFlowPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.core.financials.Period;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration;
import com.munch.exchange.model.core.financials.Period.PeriodType;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration.SearchKeyValEl;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IFinancialsProvider;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wb.swt.SWTResourceManager;

public class FinancialReportParserComposite extends Composite {
	
	private static Logger logger = Logger.getLogger(FinancialReportParserComposite.class);
	
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IFinancialsProvider financialsProvider;
	
	@Inject
	MDirtyable dirty;
	
	private Stock stock;
	
	private ReportReaderConfiguration config;
	
	private Financials financials;
	
	private StockFinancialsContentProvider contentProvider=new StockFinancialsContentProvider();
	
	private boolean isInitiated=false;
	
	private Text textCompanyWebsite;
	private StyledText styledText;
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
	private Button btnSendToTable;
	private Combo comboReportWebsites;
	private Button btnAddWebsite;
	private Button btnDeleteWebsite;
	private Combo comboFileType;
	private Button btnRepweb;
	
	
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
		compositeHeader.setLayout(new GridLayout(1, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		compositeHeader.setBounds(0, 0, 64, 64);
		
		Label lblCompany = new Label(compositeHeader, SWT.NONE);
		lblCompany.setSize(100, 15);
		lblCompany.setText("Company Website:");
		
		Composite composite_4 = new Composite(compositeHeader, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_composite_4 = new GridLayout(2, false);
		gl_composite_4.marginHeight = 0;
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		composite_4.setLayout(gl_composite_4);
		
		textCompanyWebsite = new Text(composite_4, SWT.BORDER);
		textCompanyWebsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textCompanyWebsite.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(!isInitiated)return;
				dirty.setDirty(true);
			}
		});
		textCompanyWebsite.setSize(138, 21);
		
		Button buttonCompWeb = new Button(composite_4, SWT.NONE);
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
		
		Composite compositeWebsites = new Composite(compositeHeader, SWT.NONE);
		compositeWebsites.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_compositeWebsites = new GridLayout(2, false);
		compositeWebsites.setLayout(gl_compositeWebsites);
		
		btnQuaterly = new Button(compositeWebsites, SWT.RADIO);
		btnQuaterly.setEnabled(false);
		btnQuaterly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isInitiated)return;
				if(btnQuaterly.getSelection()){
					config.getSelectedPeriod().setType(PeriodType.QUATERLY);
					
					btnAnnualy.setSelection(false);
					btnQuaterly.setEnabled(false);
					btnAnnualy.setEnabled(true);
					//refreshAfterPeriod();
					dirty.setDirty(true);
				}
			}
		});
		btnQuaterly.setSelection(true);
		btnQuaterly.setSize(66, 16);
		btnQuaterly.setText("Quaterly");
		
		btnAnnualy = new Button(compositeWebsites, SWT.RADIO);
		btnAnnualy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isInitiated)return;
				if(btnAnnualy.getSelection()){
					config.getSelectedPeriod().setType(PeriodType.ANNUAL);
					
					btnQuaterly.setSelection(false);
					btnAnnualy.setEnabled(false);
					btnQuaterly.setEnabled(true);
					//refreshAfterPeriod();
					dirty.setDirty(true);
				}
			}
		});
		btnAnnualy.setText("Annualy");
		
		Label lblReportWebsite = new Label(compositeHeader, SWT.NONE);
		lblReportWebsite.setText("Report Websites:");
		
		comboReportWebsites = new Combo(compositeHeader, SWT.NONE);
		comboReportWebsites.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(compositeHeader, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		btnRepweb = new Button(composite, SWT.NONE);
		btnRepweb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//config.setPattern(textPattern.getText());
				
				loadAndAnalyseDocument();
				
				btnRepweb.setEnabled(false);
				
			}
		});
		btnRepweb.setText("Search Documents");
		
		btnAddWebsite = new Button(composite, SWT.NONE);
		btnAddWebsite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String webSite=comboReportWebsites.getText();
				if(webSite.isEmpty())return;
				if(config.getReportWebsites().contains(webSite))return;
				
				String content=financialsProvider.getHtmlContent(webSite);
				if(content==null || content.isEmpty())return;
				
				//Add the new website
				config.getReportWebsites().addFirst(webSite);
				config.clearSavedDocs();
				refreshComboReportWebsites();
				
				btnRepweb.setEnabled(true);
				dirty.setDirty(true);
				
			}
		});
		btnAddWebsite.setText("Add");
		
		btnDeleteWebsite = new Button(composite, SWT.NONE);
		btnDeleteWebsite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String webSite=comboReportWebsites.getText();
				if(webSite.isEmpty())return;
				if(!config.getReportWebsites().contains(webSite))return;
				
				config.getReportWebsites().remove(webSite);
				config.clearSavedDocs();
				refreshComboReportWebsites();
				
				btnRepweb.setEnabled(true);
				dirty.setDirty(true);
				
			}
		});
		btnDeleteWebsite.setText("Del");
		
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
				if(!isInitiated)return;
				config.setUsePeriod(btnPeriod.getSelection());
				
				loadAndAnalyseDocument();
				
				dirty.setDirty(true);
				
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
				config.getSelectedPeriod().previous();
				if(config.isUsePeriod()){
					comboDocuments.removeAll();
					comboDocuments.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
				}
				
				refreshPeriod();
				
				loadAndAnalyseDocument();
				
				dirty.setDirty(true);
			}
		});
		btnBack.setText("<");
		
		btnNext = new Button(composite_2, SWT.NONE);
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				config.getSelectedPeriod().next();
				if(config.isUsePeriod()){
					comboDocuments.removeAll();
					comboDocuments.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
				}
				refreshPeriod();
				
				loadAndAnalyseDocument();
				
				dirty.setDirty(true);
			}
		});
		btnNext.setText(">");
		
		Composite composite_3 = new Composite(grpDocuments, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_3.setLayout(new GridLayout(4, false));
		
		Label lblFileType = new Label(composite_3, SWT.NONE);
		lblFileType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFileType.setText("File Type:");
		
		comboFileType = new Combo(composite_3, SWT.NONE);
		comboFileType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isInitiated)return;
				config.setDocumentType(comboFileType.getText());
				dirty.setDirty(true);
			}
		});
		comboFileType.setItems(new String[] {
				ReportReaderConfiguration.DocumentType_PDF, 
				ReportReaderConfiguration.DocumentType_XLS, 
				ReportReaderConfiguration.DocumentType_CSV,
				ReportReaderConfiguration.DocumentType_TXT});
		comboFileType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboFileType.setText("PDF");
		
		Label lblPattern = new Label(composite_3, SWT.NONE);
		lblPattern.setText("Pattern: ");
		
		textPattern = new Text(composite_3, SWT.BORDER);
		textPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(!isInitiated)return;
				config.setPattern(textPattern.getText());
				
				loadAndAnalyseDocument();
				
				dirty.setDirty(true);
			}
		});
		textPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isInitiated)return;
				config.setPattern(textPattern.getText());
				
				loadAndAnalyseDocument();
				
				dirty.setDirty(true);
			}
		});
		textPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboDocuments = new Combo(grpDocuments, SWT.NONE);
		comboDocuments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isInitiated)return;
				loadSelectedDocument();
			}
		});
		comboDocuments.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		styledText = new StyledText(compositeLeft, SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setAlwaysShowScrollBars(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(compositeRight, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(3, false));
		
		/*
		Button btnSave = new Button(composite_1, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
			}
		});
		btnSave.setText("Save");
		*/
		
		Button btnReparse = new Button(composite_1, SWT.NONE);
		btnReparse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parseDocument();
			}
		});
		btnReparse.setText("Reparse");
		
		btnSendToTable = new Button(composite_1, SWT.NONE);
		btnSendToTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isInitiated)return;
				
				if(config.getAllFoundElts()==null)return;
				
				for(SearchKeyValEl el:config.getAllFoundElts()){
					if(el.value==Long.MIN_VALUE)continue;
					
					long val=financials.getValue(config.getSelectedPeriod(), el.fieldKey, el.sectorKey);
					if(val==Long.MIN_VALUE || val!=Long.MIN_VALUE && val!=el.value){
						financials.setValue(config.getSelectedPeriod(), el.fieldKey, el.sectorKey, el.value);
						dirty.setDirty(true);
					}
				}
				
				treeViewer.refresh();
				
			}
		});
		btnSendToTable.setText("Send to Table");
		btnSendToTable.setEnabled(false);
		new Label(composite_1, SWT.NONE);
		
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
		treeViewerColumnValu.setEditingSupport(
				new FinancialReportEditingSupport(this, FinancialReportEditingSupport.FIELD_EffectiveDate));
		trclmnValue.setWidth(100);
		trclmnValue.setText("Value");
		sashForm.setWeights(new int[] {266, 271});
		
		
		init();
		
	}
	
	

	private void refreshComboReportWebsites(){
		comboReportWebsites.removeAll();
		if(this.config.getReportWebsites().isEmpty())return;
		
		
		for(String site:this.config.getReportWebsites()){
			comboReportWebsites.add(site);
		}
		
		comboReportWebsites.setText(this.config.getReportWebsites().getFirst());
		
		
	}
	
	
	private void refreshPeriod(){
		config.setAllFoundElts(null);
		styledText.setText("");
		textPeriod.setText(config.getSelectedPeriod().toString());
		trclmnValue.setText(config.getSelectedPeriod().toString());
		treeViewer.refresh();
	}
	
	private void loadAndAnalyseDocument(){
		
		comboDocuments.removeAll();
		String[] docs=financialsProvider.searchAllMatchingDocuments(stock);
		
		for(int i=0;i<docs.length;i++){
			comboDocuments.add(docs[i]);
			styledText.append("* * * DOC: "+docs[i]+"\n");	
		}
		
		if(docs.length>0){
			comboDocuments.select(0);
		}
		
		
		if(comboDocuments.getItemCount()==1){
			comboDocuments.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
			comboDocuments.setEnabled(false);
		}
		else{
			comboDocuments.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			comboDocuments.setEnabled(true);
			return;
		}
		
		loadSelectedDocument();
		
	}
	
	private void loadSelectedDocument(){
		String url=textCompanyWebsite.getText()+comboDocuments.getText();
		String document=financialsProvider.loadReportDocument(stock, url);
		
		styledText.setText("");
		styledText.append("* * * DOC:\n"+document+"\n");
		config.setDocumentContent(document);
		
		parseDocument();
	}
	
	
	private void parseDocument() {
		// Analyze the document
		config.parseDocument();
		
		if(config.getAllFoundElts()==null){
			btnSendToTable.setEnabled(false);
			return;
		}
		btnSendToTable.setEnabled(true);
		
		treeViewer.refresh();
		//refreshAfterPeriod();
		
	}
	
	public void init(){
		if(this.config==null ||
		this.financials==null)return;
		
		
		if(config.getWebsite()!=null)
			textCompanyWebsite.setText(config.getWebsite());
		
		if(config.getPattern()!=null)
			textPattern.setText(config.getPattern());
		
		btnPeriod.setSelection(config.isUsePeriod());
		
		switch (config.getSelectedPeriod().getType()) {
		case ANNUAL:
			btnAnnualy.setSelection(true);
			btnQuaterly.setSelection(false);
			btnAnnualy.setEnabled(false);
			btnQuaterly.setEnabled(true);
			break;

		default:
			btnAnnualy.setSelection(false);
			btnQuaterly.setSelection(true);
			btnAnnualy.setEnabled(true);
			btnQuaterly.setEnabled(false);
			break;
		}
		
		refreshPeriod();
		
		//refreshAfterPeriod();
		
		refreshComboReportWebsites();
		
		isInitiated=true;
	}
	

	@Inject
	private void financialDataLoaded(
			@Optional @UIEventTopic(IEventConstant.FINANCIAL_DATA_LOADED) String rate_uuid) {

		if(!isCompositeAbleToReact(rate_uuid))return;
		
		this.config=stock.getFinancials().getReportReaderConfiguration();
		this.financials=stock.getFinancials();
		init();
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
				
				if(entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate)){
					String savedValue=DateTool.dateToString(stock.getFinancials().getEffectiveDate(config.getSelectedPeriod()));
					return savedValue;
				}
				
				
				long val=financials.getValue(config.getSelectedPeriod(),entity.fieldKey, entity.sectorKey);
				if(val!=Long.MIN_VALUE)
					return String.valueOf(val);
						
				
			}
			return "";
		}
		
		
	}
	
	
	class ParsedValueColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if(config.getAllFoundElts()==null)return "";
			
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				
				
				String key="";
				if (btnQuaterly.getSelection()) {
					key=FinancialPoint.PeriodeTypeQuaterly+"_"+entity.fieldKey+"_"+entity.sectorKey;
				} else {
					key=FinancialPoint.PeriodeTypeAnnual+"_"+entity.fieldKey+"_"+entity.sectorKey;
				}
				
				for(SearchKeyValEl el:config.getAllFoundElts()){
					if(el.getKey().equals(key)&& el.wasCalculated)
						return String.valueOf(el.value);
				}
				
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
		
		
		@Override
		public Color getBackground(Object element) {
			
			if(config.getAllFoundElts()==null)
				return super.getBackground(element);
			
			
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				long val=financials.getValue(config.getSelectedPeriod(),entity.fieldKey, entity.sectorKey);
				long elVal=Long.MIN_VALUE;
				
				if (btnQuaterly.getSelection()) {
					String key=FinancialPoint.PeriodeTypeQuaterly+"_"+entity.fieldKey+"_"+entity.sectorKey;
					SearchKeyValEl searchEl=null;
					if(config.getAllFoundElts()!=null){
					for(SearchKeyValEl el:config.getAllFoundElts()){
						if(el.getKey().equals(key)&& el.wasCalculated){
							searchEl=el;break;
						}
					}}
					if(searchEl==null)
						searchEl = config.getQuaterlySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					
					elVal=searchEl.value;
			
				}
				 else {
					String key=FinancialPoint.PeriodeTypeAnnual+"_"+entity.fieldKey+"_"+entity.sectorKey;
					SearchKeyValEl searchEl=null;
					if(config.getAllFoundElts()!=null){
					for(SearchKeyValEl el:config.getAllFoundElts()){
						if(el.getKey().equals(key)&& el.wasCalculated){
							searchEl=el;break;
						}
					}}
					if(searchEl==null)
						searchEl= config.getAnnualySearchKeyValEl(
							entity.fieldKey, entity.sectorKey);
					
					elVal=searchEl.value;
				}
				
				if(elVal==Long.MIN_VALUE)return super.getBackground(element);
				
				
				if(val==Long.MIN_VALUE){
					return new Color(getDisplay(), new RGB(0, 255, 0));
				}
				else if(val!=Long.MIN_VALUE && val!=elVal){
					return new Color(getDisplay(), new RGB(0, 255, 255));
				}
				
				
			}
			
			
			
			return super.getBackground(element);
		}


		@Override
		public Color getForeground(Object element) {
			if(config.getAllFoundElts()!=null){
			if (element instanceof FinancialElement) {
				FinancialElement entity = (FinancialElement) element;
				String key="";
				if (btnQuaterly.getSelection()) {
					key=FinancialPoint.PeriodeTypeQuaterly+"_"+entity.fieldKey+"_"+entity.sectorKey;
				} else {
					key=FinancialPoint.PeriodeTypeAnnual+"_"+entity.fieldKey+"_"+entity.sectorKey;
				}
				
				for(SearchKeyValEl el:config.getAllFoundElts()){
					if(el.getKey().equals(key)&& el.wasCalculated)
						return new Color(getDisplay(), new RGB(150, 150, 150));
				}
				
				
			}
			}
			return super.getForeground(element);
		}
		
		
		
		
	}
	
	
}
