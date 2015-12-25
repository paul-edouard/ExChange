package com.munch.exchange.parts.chart.signal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.hibernate.validator.internal.xml.GetterType;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.diagnostics.ControllerEvent;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationController;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerEvent;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerListener;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerRunnable;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalProblem;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

import org.eclipse.jface.viewers.TableViewerColumn;


public class SignalOptimizationEditorPart implements SelectionListener, 
IbChartSignalOptimizationControllerListener{
	

	private static Logger logger = Logger.getLogger(SignalOptimizationEditorPart.class);
	
	/**
	 * The &epsilon; value used when displaying the approximation set.
	 */
	private static final double EPSILON = 0.01;	
	
	private  class ContentProvider implements IStructuredContentProvider {
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
			String key=(String) element;
				
				
			switch (columnIndex) {
			case 0: 
				return key.split("#")[1];
			case 1: 
				return key.split("#")[0];
			case 2: 
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
	
	private class BestResultContentProvider implements IStructuredContentProvider{
		
		private LinkedList<IbChartSignalOptimizedParameters> optParametersSet=new LinkedList<>();
		
		public void refreshOptSet(){
			optParametersSet.clear();
			
			//Add the already saved optimized set into the list
			optParametersSet.addAll(signal.getOptimizedSet());
			
			//Try to find the current parameter into the saved list
			boolean currentFound=false;
			for(IbChartSignalOptimizedParameters optParameters:optParametersSet){
				
				boolean allValuesEquals=true;
				for(int i=0;i<optParameters.getParameters().size();i++){
					IbChartParameter param1=optParameters.getParameters().get(i);
					IbChartParameter param2=signal.getParameters().get(i);
					if(!param1.hasSameValueAs(param2)){
						allValuesEquals=false;
						break;
					}
				}
				
				//the current parameter was found in list
				if(allValuesEquals){
					optParameters.setSatus(com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.CURRENT);
					currentFound=true;
					break;
				}
				
			}
			
			//the current parmeter was not found
			if(!currentFound){
				IbChartSignalOptimizedParameters currentParameters=new IbChartSignalOptimizedParameters();
				currentParameters.setSatus(com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.CURRENT);
				currentParameters.setParameters(signal.getParameters());
				optParametersSet.add(currentParameters);
			}
			
			
			//Add the new parameters from the fresh optimizations
			String metric="Approximation Set";
			
			
			
			for(String key : controller.getKeys()){
				if(!key.contains(signal.getBarSize()))continue;
				
				NondominatedPopulation population = new EpsilonBoxDominanceArchive(
						EPSILON);
				
				for(Accumulator accumulator :  controller.get(key)){
					if (!accumulator.keySet().contains(metric)) {
						continue;
					}
					
					List<?> list = (List<?>)accumulator.get(metric, 
							accumulator.size(metric)-1);
					
					for (Object object : list) {
						population.add((Solution)object);
					}
					
				}
				
				//Transform the population into optimized parameters
				for(Solution solution:population){
					List<IbChartParameter> parameters=IbChartSignalProblem.createIbChartParametersFromSolution(solution, signal.getParameters());
					IbChartSignalOptimizedParameters newParameters=new IbChartSignalOptimizedParameters();
					newParameters.setSatus(com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.NEW);
					newParameters.setParameters(parameters);
					newParameters.setAlgorithm(key.split("#")[1]);
					
					optParametersSet.add(newParameters);
				}
	
			}
			
			
			
			
		}
		
		public LinkedList<IbChartSignalOptimizedParameters> getOptParametersSet() {
			return optParametersSet;
		}

		@Override
		public void dispose() {}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		@Override
		public Object[] getElements(Object inputElement) {
			return optParametersSet.toArray();
		}
		
	}
	
	private class BestResultLabelProvider  extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if(!(element instanceof IbChartSignalOptimizedParameters))
				element.toString();
			
			IbChartSignalOptimizedParameters optParam=(IbChartSignalOptimizedParameters) element;
			
			if(columnIndex==0){
				return String.valueOf(optParam.getId());
			}
			else if(columnIndex<=signal.getParameters().size()){
				return String.valueOf(optParam.getParameters().get(columnIndex-1).getValue());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+1){
				return String.valueOf(optParam.getOptRisk());
			}
			else if(columnIndex==signal.getParameters().size()+2){
				return String.valueOf(optParam.getOptBenefit());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+3){
				return String.valueOf(optParam.getBackTestRisk());
			}
			else if(columnIndex==signal.getParameters().size()+4){
				return String.valueOf(optParam.getBackTestBenefit());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+5){
				return String.valueOf(Math.max(optParam.getOptRisk(),optParam.getBackTestRisk()));
			}
			else if(columnIndex==signal.getParameters().size()+6){
				return String.valueOf(optParam.getOptBenefit() + optParam.getBackTestBenefit());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+7){
				return optParam.getSatus().toString();
			}
			
			return element.toString();
		}
		
	}
	
	
	public static final String SIGNAL_OPTIMIZATION_EDITOR_ID="com.munch.exchange.partdescriptor.chart.signal.optimization.editor";
	
	
	@Inject
	IbChartSignal signal;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	private List<IbBar> allCollectedBars;
	
	
	private HashMap<String,List<IbBar>> backTestingBarsMap=new HashMap<>();
	private HashMap<String,List<IbBar>> optimizationBarsMap=new HashMap<>();
	
	LinkedList<String> sortedAlgorithmNames;
	
	BestResultContentProvider bestResultContentProvider=new BestResultContentProvider();
	
	
	/**
	 * The underlying data model storing all available results.
	 */
	private LinkedList<String> resultListModel;
	
	/**
	 * The underlying data model storing all available metrics.
	 */
	private LinkedList<String> metricListModel;
	
	/**
	 * The underlying data model storing the different result type
	 */
	private LinkedList<String> resultTypeListModel;
	
	/**
	 * Maintains a mapping from series key to paints displayed in the plot.
	 */
	private PaintHelper paintHelper;
	
	
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
	private Composite chartContainer;
	private TabItem tbtmMetrics;
	private TabFolder tabFolder;
	private Label lblPogress;
	private ProgressBar progressBarSeed;
	private ProgressBar progressBarMemory;
	private Composite approximationSetContainer;
	private TableColumn tblclmnBarSize;
	private TabItem tbtmBestResults;
	private Composite bestResultContainer;
	private Table tableBestResults;
	private Button btnRemove;
	private Button btnSave;
	private Button btnActivate;
	private TableViewer tableViewerBestResults;
	private TableColumn tblclmnOptRisk;
	private TableColumn tblclmnOptBenefit;
	private TableColumn tblclmnBackTRisk;
	private TableColumn tblclmnBackTBenefit;
	private TableColumn tblclmnTotalRisk;
	private TableColumn tblclmnTotalBenefit;
	private TableColumn tblclmnStatus;
	private TableColumn tblclmnId;
	private Button btnCalculateStatistics;
	
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
		GridData gd_compositeMain = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_compositeMain.widthHint = 788;
		compositeMain.setLayoutData(gd_compositeMain);
		
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
		spinnerPercentOfData.setMinimum(1);
		spinnerPercentOfData.setSelection(20);
		
		lblAlgorithm = new Label(groupControls, SWT.NONE);
		lblAlgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAlgorithm.setText("Algorithm:");
		
		comboAlgorithm = new Combo(groupControls, SWT.NONE);
		
		comboAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboAlgorithm.setItems(sortedAlgorithmNames.toArray(new String[0]));
		/*
		comboAlgorithm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				signal.setAlgorithmName(comboAlgorithm.getText());
			}
		});
		*/
		comboAlgorithm.select(0);
		
		
		lblSeeds = new Label(groupControls, SWT.NONE);
		lblSeeds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeeds.setText("Seeds:");
		
		spinnerSeeds = new Spinner(groupControls, SWT.BORDER);
		spinnerSeeds.setMaximum(10);
		spinnerSeeds.setMinimum(1);
		spinnerSeeds.setSelection(1);
		spinnerSeeds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblMaxNfe = new Label(groupControls, SWT.NONE);
		lblMaxNfe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaxNfe.setText("Max NFE:");
		
		spinnerMaxNFE = new Spinner(groupControls, SWT.BORDER);
		spinnerMaxNFE.setIncrement(100);
		spinnerMaxNFE.setMaximum(100000);
		spinnerMaxNFE.setMinimum(10);
		spinnerMaxNFE.setSelection(1000);
		spinnerMaxNFE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblReport = new Label(groupControls, SWT.NONE);
		lblReport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReport.setText("Report:");
		
		comboReportType = new Combo(groupControls, SWT.NONE);
		comboReportType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboReportType.setItems(resultTypeListModel.toArray(new String[0]));
		comboReportType.select(0);
		
		compositeCommandBtns = new Composite(compositeCommand, SWT.NONE);
		compositeCommandBtns.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeCommandBtns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnRun = new Button(compositeCommandBtns, SWT.NONE);
		btnRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnRun");
				
				OptJobStater stater=new OptJobStater(comboBarSize.getText(),
													comboAlgorithm.getText(),
													comboReportType.getText(),
													spinnerMaxNFE.getSelection(),
													spinnerSeeds.getSelection(),
													spinnerPercentOfData.getSelection());
				stater.schedule();
				btnRun.setEnabled(false);
				btnClear.setEnabled(false);
				
			}
		});
		btnRun.setText("Run");
		
		btnCancel = new Button(compositeCommandBtns, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//logger.info("Click on btnCancel");
				controller.cancel();
				resetRunCancelEnable();
			}
		});
		btnCancel.setText("Cancel");
		
		btnClear = new Button(compositeCommandBtns, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnClear");
				controller.clear();
			}
		});
		btnClear.setText("Clear");
		
		grpDisplayedResults = new Group(compositeCommand, SWT.NONE);
		grpDisplayedResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDisplayedResults.setLayout(new GridLayout(1, false));
		grpDisplayedResults.setText("Displayed Results");
		
		tableViewerResults = new TableViewer(grpDisplayedResults, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.MULTI);
		tableViewerResults.setLabelProvider(new TableLabelProvider());
		tableViewerResults.setContentProvider(new ContentProvider());
		tableViewerResults.setInput(resultListModel);
		tableResults = tableViewerResults.getTable();
		tableResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//logger.info("Double click on Table");
				NondominatedPopulation referenceSet = null;
				//TODO Set the reference set
				
				int selectedIndex=tableResults.getSelectionIndex();
				String key=resultListModel.get(selectedIndex);
				
				Control[] children = approximationSetContainer.getChildren();
				for(int i=0;i<children.length;i++)
					children[i].dispose();
				
				SignalOptimizationApproximationSetViewer viewer=
						new SignalOptimizationApproximationSetViewer(signal.getName()+":"+key,
								controller.get(key),
								referenceSet,
								approximationSetContainer, SWT.NONE);
				
				viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
				approximationSetContainer.layout();
				approximationSetContainer.update();
				
				
				tabFolder.setSelection(1);
				
			}
		});
		tableResults.setHeaderVisible(true);
		tableResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		tblclmnAlgorithm = new TableColumn(tableResults, SWT.NONE);
		tblclmnAlgorithm.setWidth(150);
		tblclmnAlgorithm.setText("Algorithm");
		
		tblclmnBarSize = new TableColumn(tableResults, SWT.NONE);
		tblclmnBarSize.setWidth(100);
		tblclmnBarSize.setText("Bar size");
		
		tblclmnNbOfSeeds = new TableColumn(tableResults, SWT.LEFT);
		tblclmnNbOfSeeds.setWidth(131);
		tblclmnNbOfSeeds.setText("Nb of Seeds");
		
		grpDisplayedMetrics = new Group(compositeCommand, SWT.NONE);
		grpDisplayedMetrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		grpDisplayedMetrics.setText("Displayed Metrics");
		grpDisplayedMetrics.setLayout(new GridLayout(1, false));
		
		listViewerMetrics = new ListViewer(grpDisplayedMetrics, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listMetrics = listViewerMetrics.getList();
		listMetrics.addSelectionListener(this);
		listMetrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		compositeChart = new Composite(compositeMain, SWT.NONE);
		compositeChart.setLayout(new GridLayout(1, false));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeChart.setSize(288, 107);
		
		tabFolder = new TabFolder(compositeChart, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tbtmBestResults = new TabItem(tabFolder, SWT.NONE);
		tbtmBestResults.setText("Best Results");
		
		bestResultContainer = new Composite(tabFolder, SWT.NONE);
		tbtmBestResults.setControl(bestResultContainer);
		bestResultContainer.setLayout(new GridLayout(1, false));
		
		tableViewerBestResults = new TableViewer(bestResultContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL);
		tableViewerBestResults.setLabelProvider(new BestResultLabelProvider());
		tableViewerBestResults.setContentProvider(bestResultContentProvider);
		tableViewerBestResults.setInput(this);
		tableBestResults = tableViewerBestResults.getTable();
		tableBestResults.setHeaderVisible(true);
		tableBestResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		tblclmnId = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnId.setWidth(50);
		tblclmnId.setText("Id");
		
		addBestResultParameterColumns();
		
		tblclmnOptRisk = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnOptRisk.setWidth(100);
		tblclmnOptRisk.setText("Opt. Risk");
		
		tblclmnOptBenefit = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnOptBenefit.setWidth(100);
		tblclmnOptBenefit.setText("Opt. Benefit");
		
		tblclmnBackTRisk = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnBackTRisk.setWidth(100);
		tblclmnBackTRisk.setText("Back T. Risk");
		
		tblclmnBackTBenefit = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnBackTBenefit.setWidth(100);
		tblclmnBackTBenefit.setText("Back T. Benefit");
		
		tblclmnTotalRisk = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnTotalRisk.setWidth(100);
		tblclmnTotalRisk.setText("Total Risk");
		
		tblclmnTotalBenefit = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnTotalBenefit.setWidth(100);
		tblclmnTotalBenefit.setText("Total Benefit");
		
		tblclmnStatus = new TableColumn(tableBestResults, SWT.NONE);
		tblclmnStatus.setWidth(100);
		tblclmnStatus.setText("Status");
		
		Composite compositeBestResultButton = new Composite(bestResultContainer, SWT.NONE);
		compositeBestResultButton.setLayout(new GridLayout(4, false));
		compositeBestResultButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnActivate = new Button(compositeBestResultButton, SWT.NONE);
		btnActivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO Press Button Activate
				logger.info("Press Button Activate");
			}
		});
		btnActivate.setText("Activate");
		
		btnSave = new Button(compositeBestResultButton, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO Press Button Save
				logger.info("Press Button Save");
			}
		});
		btnSave.setBounds(0, 0, 105, 35);
		btnSave.setText("Save");
		
		btnRemove = new Button(compositeBestResultButton, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO Press Button Remove
				logger.info("Press Button Remove");
			}
		});
		btnRemove.setText("Remove");
		
		btnCalculateStatistics = new Button(compositeBestResultButton, SWT.NONE);
		btnCalculateStatistics.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO
				logger.info("btnCalculateStatistics click!");
				
				bestResultContentProvider.refreshOptSet();
				
				StatisticCalculator statisticCalculator=new StatisticCalculator(signal, comboBarSize.getText(),spinnerPercentOfData.getSelection());
				statisticCalculator.schedule();
				
				
			}
		});
		btnCalculateStatistics.setText("Calculate Statistics");
		
		tbtmMetrics = new TabItem(tabFolder, SWT.NONE);
		tbtmMetrics.setText("Metrics");
		
		chartContainer = new Composite(tabFolder, SWT.NONE);
		tbtmMetrics.setControl(chartContainer);
		chartContainer.setLayout(new GridLayout(1, false));
		
		tbtmApproximationSet = new TabItem(tabFolder, SWT.NONE);
		tbtmApproximationSet.setText("Approximation Set");
		
		approximationSetContainer = new Composite(tabFolder, SWT.NONE);
		tbtmApproximationSet.setControl(approximationSetContainer);
		approximationSetContainer.setLayout(new GridLayout(1, false));
		
		Composite compositeBottom = new Composite(parent, SWT.BORDER);
		compositeBottom.setLayout(new GridLayout(6, false));
		compositeBottom.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
		lblPogress = new Label(compositeBottom, SWT.NONE);
		lblPogress.setText("Pogress:");
		
		progressBarRun = new ProgressBar(compositeBottom, SWT.NONE);
		progressBarRun.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSeedsNb = new Label(compositeBottom, SWT.NONE);
		lblSeedsNb.setText("Seed");
		
		progressBarSeed = new ProgressBar(compositeBottom, SWT.NONE);
		progressBarSeed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblMemory = new Label(compositeBottom, SWT.NONE);
		lblMemory.setText("Memory");
		
		progressBarMemory = new ProgressBar(compositeBottom, SWT.NONE);
		progressBarMemory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
		postGuiInitialization();
		
	}
	
	private void addBestResultParameterColumns(){
		for(IbChartParameter param:this.signal.getParameters()){
			String parameName=param.getName();
			TableColumn paramColumn = new TableColumn(tableBestResults, SWT.NONE);
			paramColumn.setWidth(100);
			paramColumn.setText(parameName);
		}
		
		
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
		
		resultTypeListModel=new LinkedList<String>();
		resultTypeListModel.add("Full");
		resultTypeListModel.add("Middle");
		resultTypeListModel.add("None");
		
		paintHelper = new PaintHelper();
		
	}
	
	
	private void postGuiInitialization(){
		resetRunCancelEnable();
		
		bestResultContentProvider.refreshOptSet();
		tableViewerBestResults.refresh();
		
	}
	
	
	private void resetRunCancelEnable(){
		btnRun.setEnabled(!controller.isRunning());
		btnCancel.setEnabled(controller.isRunning());
		btnClear.setEnabled(!controller.isRunning());
		btnActivate.setEnabled(!controller.isRunning());
		btnRemove.setEnabled(!controller.isRunning());
		btnSave.setEnabled(!controller.isRunning());
		btnCalculateStatistics.setEnabled(!controller.isRunning());
	}
	
	
	
	private void updateModel() {
		//determine selection mode
		List<String> selectedResults = getSelectedResults();
		List<String> selectedMetrics = getSelectedMetrics();
		boolean selectAllResults = false;
		boolean selectFirstMetric = false;
		
		
		if (selectedResults.size() == resultListModel.size()) {
			selectAllResults = true;
		}
		
		if ((selectedMetrics.size() == 0) && (metricListModel.size() == 0)) {
			selectFirstMetric = true;
		}
		
		//update metric list and result table contents
		resultListModel.clear();
		resultListModel.addAll(controller.getKeys());
		tableViewerResults.refresh();
		
		metricListModel.clear();
		for (String key : controller.getKeys()) {
			for (Accumulator accumulator : controller.get(key)) {
				for(String metricKey:accumulator.keySet()){
					if(!metricListModel.contains(metricKey))
						metricListModel.add(metricKey);
				}
			}
		}
		
		//update metric list selection
		listMetrics.removeSelectionListener(this);
		listMetrics.removeAll();
		for(String key:metricListModel){
			listMetrics.add(key);
		}
		
		if (selectFirstMetric) {
			listMetrics.setSelection(0);
		} else {
			for (String metric : selectedMetrics) {
				int index = metricListModel.indexOf(metric);
				listMetrics.select(index);
			}
		}
		listMetrics.addSelectionListener(this);
		
		
		
		//update result table selection
		tableResults.removeSelectionListener(this);
		
		if (selectAllResults && (selectedResults.size() < 
				resultListModel.size())) {
			tableResults.setSelection(0, 
					resultListModel.size()-1);
			
		} else {
			for (String key : selectedResults) {
				int index = resultListModel.indexOf(key);
				tableResults.setSelection(index, 
						index);
			}
		}

		tableResults.addSelectionListener(this);
	}
	
	
	
	/**
	 * Returns a list of the selected metrics.
	 * 
	 * @return a list of the selected metrics
	 */
	protected List<String> getSelectedMetrics() {
		List<String> selectedMetrics = new ArrayList<String>();
		
		int[] selectedIndices=this.listViewerMetrics.getList().getSelectionIndices();
		
		for (int i=0;i<selectedIndices.length;i++) {
			int index=selectedIndices[i];
			selectedMetrics.add(metricListModel.get(index));
		}
		
		return selectedMetrics;
	}
	
	
	/**
	 * Returns a list of the selected results.
	 * 
	 * @return a list of the selected results
	 */
	protected List<String> getSelectedResults() {
		List<String> selectedResults = new ArrayList<String>();
		
		int[] selectedIndices=this.tableResults.getSelectionIndices();
		
		for (int i=0;i<selectedIndices.length;i++) {
			int index=selectedIndices[i];
			selectedResults.add(resultListModel.get(index));
		}
		
		return selectedResults;
	}
	
	
	/**
	 * Invoked when the underlying data model is cleared, resulting in the GUI
	 * removing and resetting all components.  This method must only be invoked
	 * on the event dispatch thread.
	 */
	protected void clear() {
		resultListModel.clear();
		tableResults.deselectAll();
		//resultTableModel.fireTableDataChanged();
		metricListModel.clear();
		listMetrics.deselectAll();
		paintHelper.clear();
		
		
		chartContainer.dispose();
		
	}
	
	
	/**
	 * Updates the chart layout when the user changes which metrics to plot.
	 * This method must only be invoked on the event dispatch thread.
	 */
	protected void updateChartLayout() {
		chartContainer.dispose();
		
		chartContainer = new Composite(tabFolder, SWT.NONE);
		tbtmMetrics.setControl(chartContainer);
		chartContainer.setLayout(new GridLayout(1, false));
		
		List<String> selectedMetrics = getSelectedMetrics();
		
		int nbOfRow=0;
		
		if (selectedMetrics.size() > 0) {
			if (selectedMetrics.size() <= 1) {
				chartContainer.setLayout(new GridLayout(1, false));
				nbOfRow=1;
			} else if (selectedMetrics.size() <= 2) {
				chartContainer.setLayout(new GridLayout(1, false));
				nbOfRow=2;
			} else if (selectedMetrics.size() <= 4) {
				chartContainer.setLayout(new GridLayout(2, false));
				nbOfRow=2;
			} else if (selectedMetrics.size() <= 6) {
				chartContainer.setLayout(new GridLayout(2, false));
				nbOfRow=3;
			} else {
				chartContainer.setLayout(new GridLayout(
						3, false));
				nbOfRow=(int)Math.ceil(selectedMetrics.size()/3.0);
			}
			
			GridLayout layout = (GridLayout)chartContainer.getLayout();
			int spaces = nbOfRow*layout.numColumns;
			
			for (int i=0; i<Math.max(spaces, selectedMetrics.size()); i++) {
				if (i < selectedMetrics.size()) {
					logger.info("Selected Metric: "+selectedMetrics.get(i));
					/*
					Label lbl = new Label(chartContainer, SWT.NONE);
					lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
					lbl.setText("Test1");
					*/
					
					createChart(selectedMetrics.get(i),chartContainer);
				} else {
					Label lbl = new Label(chartContainer, SWT.NONE);
					lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
					lbl.setText("Test2");
					//chartContainer.add(new EmptyPlot(this));
				}
			}
		}
		
		
		chartContainer.layout();
		
	}
	
	
	
	/**
	 * Creates and returns the GUI component for plotting the specified metric.
	 * 
	 * @param metric the metric to plot
	 * @return the GUI component for plotting the specified metric
	 */
	protected void createChart(String metric, Composite parent) {
		if (metric.equals("Approximation Set")) {
			new SignalOptimizationApproximationSetPlot(metric, this, parent, SWT.NONE);
			
		} else {
			logger.info("SignalOptimizationLinePlot!");
			new SignalOptimizationLinePlot(metric, this, parent, SWT.NONE);
			
		}
	}
	
	
	
	
	private IbBarContainer getBarContainer(){
		return signal.getGroup().getRoot().getContainer();
	}
	
	
	@PreDestroy
	public void dispose() {
		controller.cancel();
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}

	

	@Override
	public void controllerStateChanged(
			IbChartSignalOptimizationControllerEvent event) {

		Display.getDefault().asyncExec(
				new IbChartSignalOptimizationControllerRunnable(event) {

					@Override
					public void run() {
						
						resetRunCancelEnable();

						if (event
								.getType()
								.equals(IbChartSignalOptimizationControllerEvent.Type.MODEL_CHANGED)) {
							
							logger.info("MODEL_CHANGED: "
									+ controller.getKeys().size());
							
							if (controller.getKeys().isEmpty()) {
								clear();
							} else {
								updateModel();
							}
						} else if (event
								.getType()
								.equals(IbChartSignalOptimizationControllerEvent.Type.PROGRESS_CHANGED)) {

							logger.info("PROGRESS_CHANGED: "
									+ controller.getRunProgress());
							
							progressBarRun.setSelection(controller.getRunProgress());
							progressBarRun.setMaximum(signal.getNumberOfEvaluations());
							progressBarRun.setToolTipText(
									controller.getRunProgress()*100/signal.getNumberOfEvaluations()+"%");
							
							progressBarSeed.setSelection(controller.getOverallProgress());
							progressBarSeed.setMaximum(signal.getNumberOfSeeds());
							
							
							int mb = 1024*1024;
					         
					        //Getting the runtime reference from system
					        Runtime runtime = Runtime.getRuntime();
					         
					   
					        //Print used memory
					        //logger.info("Used Memory:"
					        //    + (runtime.totalMemory() - runtime.freeMemory()) / mb);
					 
					        progressBarMemory.setSelection((int)(runtime.totalMemory() - runtime.freeMemory()) / mb);
					        progressBarMemory.setMaximum((int)runtime.totalMemory()/mb );
							
							
							if (controller.getKeys().isEmpty()) {
								clear();
							} else {
								updateModel();
							}
							
							bestResultContentProvider.refreshOptSet();
							tableViewerBestResults.refresh();
							
							
						} else if (event.getType().equals(
								IbChartSignalOptimizationControllerEvent.Type.VIEW_CHANGED)) {
							
							logger.info("VIEW_CHANGED!");
							
							updateChartLayout();
						}
					}
				});

	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		
		//logger.info("widgetSelected");
		
		controller.fireViewChangedEvent();
		
		if(e.getSource()!=tableResults)
			tabFolder.setSelection(0);
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		
		controller.fireViewChangedEvent();
		
	}

	public IbChartSignalOptimizationController getController() {
		return controller;
	}
	
	/**
	 * Returns the paint helper used by this diagnostic tool instance.  This
	 * paint helper contains the mapping from series to paints displayed in this
	 * window.
	 * 
	 * @return the paint helper used by this diagnostic tool instance
	 */
	public PaintHelper getPaintHelper() {
		return paintHelper;
	}
	
	//######################################
  	//##           Data Collector         ##
  	//######################################
	private class OptJobStater extends Job{
		
		private String bazSize;
		private String algorithmName;
		private String reportType;
		private int numberOfEvaluations;
		private int numberOfSeeds;
		private int percentOfDataRequired;
		
		
		public OptJobStater(String bazSize, String algorithmName, String reportType,
				int numberOfEvaluations, int numberOfSeeds, int percentOfDataRequired) {
			super("Signal Optimization job Starter: "+signal.getName());
			
			this.bazSize=bazSize;
			this.algorithmName=algorithmName;
			this.reportType=reportType;
			this.numberOfEvaluations=numberOfEvaluations;
			this.numberOfSeeds=numberOfSeeds;
			this.percentOfDataRequired=percentOfDataRequired;
			
			if(this.reportType.equals("Full")){
				controller.setIncludeHypervolume(true);
				controller.setIncludeGenerationalDistance(true);
				controller.setIncludeInvertedGenerationalDistance(true);
				controller.setIncludeSpacing(true);
				controller.setIncludeAdditiveEpsilonIndicator(true);
				controller.setIncludeContribution(true);
				controller.setIncludeR1(true);
				controller.setIncludeR2(true);
				controller.setIncludeR3(true);
				controller.setIncludeEpsilonProgress(true);
				controller.setIncludeAdaptiveMultimethodVariation(true);
				controller.setIncludeAdaptiveTimeContinuation(true);
				controller.setIncludeElapsedTime(true);
				controller.setIncludeApproximationSet(true);
				controller.setIncludePopulationSize(true);
			}
			else if(this.reportType.equals("Middle")){
				controller.setIncludeHypervolume(true);
				controller.setIncludeGenerationalDistance(true);
				controller.setIncludeInvertedGenerationalDistance(false);
				controller.setIncludeSpacing(false);
				controller.setIncludeAdditiveEpsilonIndicator(false);
				controller.setIncludeContribution(true);
				controller.setIncludeR1(false);
				controller.setIncludeR2(false);
				controller.setIncludeR3(false);
				controller.setIncludeEpsilonProgress(false);
				controller.setIncludeAdaptiveMultimethodVariation(false);
				controller.setIncludeAdaptiveTimeContinuation(false);
				controller.setIncludeElapsedTime(true);
				controller.setIncludeApproximationSet(true);
				controller.setIncludePopulationSize(true);
			}
			else{
				controller.setIncludeHypervolume(false);
				controller.setIncludeGenerationalDistance(false);
				controller.setIncludeInvertedGenerationalDistance(false);
				controller.setIncludeSpacing(false);
				controller.setIncludeAdditiveEpsilonIndicator(false);
				controller.setIncludeContribution(false);
				controller.setIncludeR1(false);
				controller.setIncludeR2(false);
				controller.setIncludeR3(false);
				controller.setIncludeEpsilonProgress(false);
				controller.setIncludeAdaptiveMultimethodVariation(false);
				controller.setIncludeAdaptiveTimeContinuation(false);
				controller.setIncludeElapsedTime(false);
				controller.setIncludeApproximationSet(false);
				controller.setIncludePopulationSize(false);
			}
			
		}
		

		private LinkedList<LinkedList<IbBar>> collectOptimizationBlocks(LinkedList<LinkedList<IbBar>> allBlocks, int percentRequired){
			LinkedList<LinkedList<IbBar>> optBlocks=new LinkedList<>();
			int numberOfBars=0;
			//int percentRequired=spinnerPercentOfData.getSelection();
			int numberOfRequired=allCollectedBars.size()*percentRequired/100;
			//logger.info("Percent of bars required: "+percentRequired);
			//logger.info("Number of required bars: "+numberOfRequired);
			
			while(numberOfBars<numberOfRequired ){
				
				if(allBlocks.isEmpty())break;
				
				Random rand = new Random();
				int index=rand.nextInt(allBlocks.size());
				LinkedList<IbBar> removedBlock=allBlocks.remove(index);
				optBlocks.add(removedBlock);
				
				numberOfBars+=removedBlock.size();
			}
			//logger.info("Number of blocks: "+optBlocks.size());
			//logger.info("Number of bars: "+numberOfBars);
			
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
		

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			//Collect the Data
			logger.info("Collect the data");
			if(allCollectedBars==null || allCollectedBars.isEmpty()){
				allCollectedBars=hisDataProvider.getAllBars(getBarContainer(),
						IbBar.getBarSizeFromString(bazSize));
				
				//TODO Remove this!
				/*
				while (allCollectedBars.size()>1000) {
					allCollectedBars.remove(0);
					
				}
				*/
			}
			
			//Create the Data Set
			logger.info("Create the Data Set");
			if(!optimizationBarsMap.containsKey(bazSize+percentOfDataRequired)){
			
				LinkedList<LinkedList<IbBar>> allBlocks=splitCollectedBarsInBlocks();
				LinkedList<LinkedList<IbBar>> optBlocks=collectOptimizationBlocks(allBlocks,percentOfDataRequired);
			
				List<IbBar> optimizationBars=new LinkedList<IbBar>();
				HashSet<Long> timeSet=new HashSet<>();
				for(LinkedList<IbBar> bars:optBlocks){
					optimizationBars.addAll(bars);
					for(IbBar bar:bars)
						timeSet.add(bar.getTime());
				}
				optimizationBarsMap.put(bazSize+percentOfDataRequired, optimizationBars);
				
				
				List<IbBar> backTestingBars=new LinkedList<IbBar>();
				logger.info("Total Nb. of  data: "+allCollectedBars.size());
				for(IbBar bar:allCollectedBars){
					if(timeSet.contains(bar.getTime()))continue;
					backTestingBars.add(bar);
				}
				
				logger.info("Nb. of back testing data: "+backTestingBars.size());
				
				
				backTestingBarsMap.put(bazSize+percentOfDataRequired, backTestingBars);
				
			}
			
			
			signal.setOptimizationBars(optimizationBarsMap.get(bazSize+percentOfDataRequired));
			 
			
			
			//Set the parameters
			signal.setAlgorithmName(algorithmName);
			signal.setNumberOfEvaluations(numberOfEvaluations);
			signal.setNumberOfSeeds(numberOfSeeds);
			signal.setBarSize(bazSize);
			
			//Start the optimization
			logger.info("Start the optimization");
			controller.run();
			
			return Status.OK_STATUS;
		}

		
	}
	
	//#######################################################
  	//##         Best Result statistic calculator          ##
  	//#######################################################
	private class StatisticCalculator extends Job{
		
		
		private IbChartSignal chartSignal;
		
		private String bazSize;
		private int percentOfDataRequired;
		
		
		public StatisticCalculator(IbChartSignal chartSignal,String bazSize, int percentOfDataRequired) {
			super("Statistic Calculator");
			this.chartSignal=chartSignal;
			this.bazSize=bazSize;
			this.percentOfDataRequired=percentOfDataRequired;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			disableBtns();
			
			for(IbChartSignalOptimizedParameters optParam:bestResultContentProvider.getOptParametersSet()){
				IbChartSignal signal=(IbChartSignal) chartSignal.copy();
				
				//Set the parameters
				signal.setParameters(optParam.getParameters());
				
				//Opt. Bars
				logger.info("Calculate Statistics Opt. Bars!");
				signal.setBatch(true);
				signal.setOptimizationBlocks(null);
				signal.compute(optimizationBarsMap.get(bazSize+percentOfDataRequired));
				double[] profitAndRisk=IbChartSignalProblem.extractProfitAndRiskFromChartSignal(signal);
				optParam.setOptBenefit(profitAndRisk[0]);
				optParam.setOptRisk(profitAndRisk[1]);
				
				refreshTable();
				
				logger.info("Calculate Statistics Back Testing. Bars!");
				signal.setBatch(true);
				signal.setOptimizationBlocks(null);
				signal.compute(backTestingBarsMap.get(bazSize+percentOfDataRequired));
				profitAndRisk=IbChartSignalProblem.extractProfitAndRiskFromChartSignal(signal);
				optParam.setBackTestBenefit(profitAndRisk[0]);
				optParam.setBackTestRisk(profitAndRisk[1]);
				
				refreshTable();
				
				logger.info("Calculate Statistics All Bars!");
				signal.setBatch(false);
				signal.setOptimizationBlocks(null);
				signal.compute(allCollectedBars);
				optParam.setPerformanceMetrics(signal.getPerformanceMetrics());
				
				refreshTable();
			}
			
			enableBtns();
			
			return Status.OK_STATUS;
		}
		
		
		private void refreshTable(){
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					tableViewerBestResults.refresh();
				}
			});
			
		}
		
		private void enableBtns(){
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					btnActivate.setEnabled(true);
					btnCalculateStatistics.setEnabled(true);
					btnCancel.setEnabled(true);
					btnClear.setEnabled(true);
					btnRemove.setEnabled(true);
					btnRun.setEnabled(true);
					btnSave.setEnabled(true);
				}
			});
		}
		
		private void disableBtns(){
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					btnActivate.setEnabled(false);
					btnCalculateStatistics.setEnabled(false);
					btnCancel.setEnabled(false);
					btnClear.setEnabled(false);
					btnRemove.setEnabled(false);
					btnRun.setEnabled(false);
					btnSave.setEnabled(false);
				}
			});
		}
		
		
		
		
		
		
	}
	
	
}
