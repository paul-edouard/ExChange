package com.munch.exchange.parts.chart.signal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationController;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerEvent;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerListener;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.moeaframework.analysis.diagnostics.ControllerEvent;
import org.moeaframework.analysis.diagnostics.ControllerListener;
import org.moeaframework.analysis.diagnostics.ResultKey;
import org.moeaframework.analysis.diagnostics.SortedListModel;
import org.moeaframework.core.Settings;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;


public class SignalOptimizationEditorPart implements SelectionListener, 
IbChartSignalOptimizationControllerListener{
	
	
	
	
	
	private static Logger logger = Logger.getLogger(SignalOptimizationEditorPart.class);
	
	
	private static class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof LinkedList){
				@SuppressWarnings({  "unchecked" })
				LinkedList<String> list=(LinkedList<String>) inputElement;
				return list.toArray();
			}
			
			return new Object[0];
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			
			if(element instanceof String){
			
			switch (columnIndex) {
			case 0: 
				return (String) element;
			case 1: 
				return String.valueOf(controller.get((String) element).size());
			
			default: 
				return element.toString();
			}
			}
			else{
				return element.toString();
			}
			
		}
	}
	
	
	
	public static final String SIGNAL_OPTIMIZATION_EDITOR_ID="com.munch.exchange.partdescriptor.chart.signal.optimization.editor";
	
	
	@Inject
	IbChartSignal signal;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	private List<IbBar> allCollectedBars;
	
	LinkedList<LinkedList<IbBar>> backTestingBlocks;
	LinkedList<LinkedList<IbBar>> optimizationBlocks;
	
	LinkedList<String> sortedAlgorithmNames;
	
	
	/**
	 * The underlying data model storing all available results.
	 */
	private LinkedList<String> resultListModel;
	
	/**
	 * The underlying data model storing all available metrics.
	 */
	private LinkedList<String> metricListModel;
	
	
	/**
	 * The controller which stores the underlying data model and notifies this
	 * diagnostic tool of any changes.
	 */
	private IbChartSignalOptimizationController controller;
	
	
	
	private Combo comboBarSize;
	private Spinner spinnerPercentOfData;
	private Label lblPercentOfData;
	private Composite compositeCommand;
	private Composite compositeChart;
	private Group groupControls;
	private Label lblBarSize;
	private Composite compositeMain;
	private Group grpDisplayedResults;
	private Table tableResults;
	private TableViewer tableViewerResults;
	private Composite composite;
	private Button btnSelectAll;
	private Button btnShowStatistic;
	private Group grpDisplayedMetrics;
	private org.eclipse.swt.widgets.List listMetrics;
	private ListViewer listViewerMetrics;
	private Label lblAlgorithm;
	private Combo comboAlgorithm;
	private Label lblSeeds;
	private Spinner spinnerSeeds;
	private Label lblMaxNfe;
	private Spinner spinnerMaxNFE;
	private Composite compositeCommandBtns;
	private Button btnRun;
	private Button btnCancel;
	private Button btnClear;
	private ProgressBar progressBarRun;
	private Label lblMemory;
	private Label lblReport;
	private Combo comboReportType;
	private TabItem tbtmApproximationSet;
	private TableColumn tblclmnAlgorithm;
	private TableColumn tblclmnNbOfSeeds;
	
	public SignalOptimizationEditorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		
		preGuiInitialization();
		
		parent.setLayout(new GridLayout(1, false));
		
		compositeMain = new Composite(parent, SWT.NONE);
		compositeMain.setLayout(new GridLayout(2, false));
		compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		compositeCommand = new Composite(compositeMain, SWT.NONE);
		compositeCommand.setLayout(new GridLayout(1, false));
		compositeCommand.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		
		groupControls = new Group(compositeCommand, SWT.NONE);
		groupControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		groupControls.setLayout(new GridLayout(2, false));
		groupControls.setText("Controls");
		
		lblBarSize = new Label(groupControls, SWT.NONE);
		lblBarSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBarSize.setText("Bar size:");
		
		comboBarSize = new Combo(groupControls, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String bSize:IbBar.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		comboBarSize.setText(comboBarSize.getItem(0));
		
		lblPercentOfData = new Label(groupControls, SWT.NONE);
		lblPercentOfData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPercentOfData.setText("Percent of Data:");
		
		spinnerPercentOfData = new Spinner(groupControls, SWT.BORDER);
		spinnerPercentOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerPercentOfData.setPageIncrement(1);
		spinnerPercentOfData.setIncrement(5);
		spinnerPercentOfData.setMinimum(10);
		spinnerPercentOfData.setSelection(70);
		
		lblAlgorithm = new Label(groupControls, SWT.NONE);
		lblAlgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAlgorithm.setText("Algorithm:");
		
		comboAlgorithm = new Combo(groupControls, SWT.NONE);
		comboAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboAlgorithm.setItems(sortedAlgorithmNames.toArray(new String[0]));
		comboAlgorithm.select(0);
		
		
		lblSeeds = new Label(groupControls, SWT.NONE);
		lblSeeds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeeds.setText("Seeds:");
		
		spinnerSeeds = new Spinner(groupControls, SWT.BORDER);
		spinnerSeeds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblMaxNfe = new Label(groupControls, SWT.NONE);
		lblMaxNfe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaxNfe.setText("Max NFE:");
		
		spinnerMaxNFE = new Spinner(groupControls, SWT.BORDER);
		spinnerMaxNFE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblReport = new Label(groupControls, SWT.NONE);
		lblReport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReport.setText("Report:");
		
		comboReportType = new Combo(groupControls, SWT.NONE);
		comboReportType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		compositeCommandBtns = new Composite(compositeCommand, SWT.NONE);
		compositeCommandBtns.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeCommandBtns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnRun = new Button(compositeCommandBtns, SWT.NONE);
		btnRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnRun");
			}
		});
		btnRun.setText("Run");
		
		btnCancel = new Button(compositeCommandBtns, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnCancel");
			}
		});
		btnCancel.setText("Cancel");
		
		btnClear = new Button(compositeCommandBtns, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnClear");
			}
		});
		btnClear.setText("Clear");
		
		grpDisplayedResults = new Group(compositeCommand, SWT.NONE);
		grpDisplayedResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDisplayedResults.setLayout(new GridLayout(1, false));
		grpDisplayedResults.setText("Displayed Results");
		
		tableViewerResults = new TableViewer(grpDisplayedResults, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewerResults.setLabelProvider(new TableLabelProvider());
		tableViewerResults.setContentProvider(new ContentProvider());
		tableViewerResults.setInput(resultListModel);
		tableResults = tableViewerResults.getTable();
		tableResults.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		tableResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				logger.info("Double click on Table");
			}
		});
		tableResults.setHeaderVisible(true);
		tableResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		tblclmnAlgorithm = new TableColumn(tableResults, SWT.NONE);
		tblclmnAlgorithm.setWidth(150);
		tblclmnAlgorithm.setText("Algorithm");
		
		tblclmnNbOfSeeds = new TableColumn(tableResults, SWT.LEFT);
		tblclmnNbOfSeeds.setWidth(100);
		tblclmnNbOfSeeds.setText("Nb of Seeds");
		
		
		composite = new Composite(grpDisplayedResults, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnSelectAll = new Button(composite, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnSelectAll");
			}
		});
		btnSelectAll.setText("Select All");
		
		btnShowStatistic = new Button(composite, SWT.NONE);
		btnShowStatistic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnShowStatistic");
			}
		});
		btnShowStatistic.setText("Show Statistic");
		
		grpDisplayedMetrics = new Group(compositeCommand, SWT.NONE);
		grpDisplayedMetrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		grpDisplayedMetrics.setText("Displayed Metrics");
		grpDisplayedMetrics.setLayout(new GridLayout(1, false));
		
		listViewerMetrics = new ListViewer(grpDisplayedMetrics, SWT.BORDER | SWT.V_SCROLL);
		listMetrics = listViewerMetrics.getList();
		listMetrics.addSelectionListener(this);
		listMetrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		compositeChart = new Composite(compositeMain, SWT.NONE);
		compositeChart.setLayout(new GridLayout(1, false));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeChart.setSize(288, 107);
		
		TabFolder tabFolder = new TabFolder(compositeChart, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmMetrics = new TabItem(tabFolder, SWT.NONE);
		tbtmMetrics.setText("Metrics");
		
		tbtmApproximationSet = new TabItem(tabFolder, SWT.NONE);
		tbtmApproximationSet.setText("Approximation Set");
		
		Composite compositeBottom = new Composite(parent, SWT.BORDER);
		compositeBottom.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeBottom.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
		progressBarRun = new ProgressBar(compositeBottom, SWT.NONE);
		
		Label lblSeedsNb = new Label(compositeBottom, SWT.NONE);
		lblSeedsNb.setText("Seed");
		
		lblMemory = new Label(compositeBottom, SWT.NONE);
		lblMemory.setText("Memory");
		
		
		
		postGuiInitialization();
		
	}
	
	
	private void preGuiInitialization(){
		
		
		controller=new IbChartSignalOptimizationController(signal);
		controller.addControllerListener(this);
		
		resultListModel = new LinkedList<String>();
		metricListModel = new LinkedList<String>();
		
		// initialize the sorted list of algorithms
		Set<String> algorithmNames = new HashSet<String>();

		for (String algorithm : Settings.getDiagnosticToolAlgorithms()) {
			algorithmNames.add(algorithm);
		}

		for (String algorithm : Settings.getPISAAlgorithms()) {
			algorithmNames.add(algorithm);
		}

		sortedAlgorithmNames = new LinkedList<String>(algorithmNames);
		Collections.sort(sortedAlgorithmNames);
		
	}
	
	
	private void postGuiInitialization(){
		
	}
	
	
	private LinkedList<LinkedList<IbBar>> collectOptimizationBlocks(LinkedList<LinkedList<IbBar>> allBlocks){
		LinkedList<LinkedList<IbBar>> optBlocks=new LinkedList<>();
		int numberOfBars=0;
		int percentRequired=spinnerPercentOfData.getSelection();
		int numberOfRequired=allCollectedBars.size()*percentRequired/100;
		
		while(numberOfBars<numberOfRequired){
			Random rand = new Random();
			int index=rand.nextInt(allBlocks.size());
			LinkedList<IbBar> removedBlock=allBlocks.remove(index);
			optBlocks.add(removedBlock);
			
			numberOfBars+=removedBlock.size();
		}
		logger.info("Number of blocks: "+optBlocks.size());
		logger.info("Number of bars: "+numberOfBars);
		
		return optBlocks;
	}
	
	
	private LinkedList<LinkedList<IbBar>> splitCollectedBarsInBlocks(){
		LinkedList<LinkedList<IbBar>> blocks=new LinkedList<>();
		
		IbBar lastBar=allCollectedBars.get(0);
		long interval=lastBar.getIntervallInSec();
		LinkedList<IbBar> block=new LinkedList<IbBar>();
		block.add(lastBar);
		
		for(int i=1;i<allCollectedBars.size();i++){
			IbBar currentBar=allCollectedBars.get(i);
			long timeDiff=currentBar.getTime()-lastBar.getTime();
			if(timeDiff > interval ){
				//Add the block to the list
				blocks.add(block);
			
				//Reset the block
				block=new LinkedList<IbBar>();
			}
			block.add(currentBar);
			lastBar=currentBar;
		}
		if(block.size()>0){
			blocks.add(block);
		}
	
		
		
		return blocks;
	}
	
	
	private IbBarContainer getBarContainer(){
		return signal.getGroup().getRoot().getContainer();
	}
	
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}

	

	@Override
	public void controllerStateChanged(
			IbChartSignalOptimizationControllerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
