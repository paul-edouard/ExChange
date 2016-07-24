package com.munch.exchange.parts.chart.signal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBarComparator;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationController;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerEvent;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerListener;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizationControllerRunnable;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalProblem;
import com.munch.exchange.services.ejb.interfaces.IIBChartIndicatorProvider;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Text;
import org.encog.ml.genetic.GeneticError;


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
			for(IbChartSignalOptimizedParameters optParameters:signal.getOptimizedSet()){
				optParameters.setSatus(com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.SAVED);
			}
			
			
//			logger.info("Number of saved: "+optParametersSet.size());
			
			//Try to find the current parameter into the saved list
			boolean currentFound=false;
			for(IbChartSignalOptimizedParameters optParameters:optParametersSet){
				
				//optParameters.setSatus(com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.SAVED);
				
				boolean allValuesEquals=true;
				for(int i=0;i<signal.getParameters().size();i++){
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
					newParameters.setSize(BarUtils.getBarSizeFromString(signal.getBarSize()));
					
					//IbChartParameter.areAllValuesEqual(list1, list2)
					boolean newInList=true;
					for(IbChartSignalOptimizedParameters optParameters:optParametersSet){
						if(IbChartParameter.areAllValuesEqual(optParameters.getParameters(), newParameters.getParameters())){
							newInList=false;break;
						}
					}
					
					if(newInList)
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
			DecimalFormat df = new DecimalFormat("#.00"); 
			
			if(columnIndex==0){
				return String.valueOf(optParam.getId());
			}
			else if(columnIndex<=signal.getParameters().size()){
				return df.format(optParam.getParameters().get(columnIndex-1).getValue());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+1){
				return df.format(optParam.getOptRisk());
			}
			else if(columnIndex==signal.getParameters().size()+2){
				return df.format(optParam.getOptBenefit());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+3){
				return df.format(optParam.getBackTestRisk());
			}
			else if(columnIndex==signal.getParameters().size()+4){
				return df.format(optParam.getBackTestBenefit());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+5){
				return df.format(Math.max(optParam.getOptRisk(),optParam.getBackTestRisk()));
			}
			else if(columnIndex==signal.getParameters().size()+6){
				return df.format(optParam.getOptBenefit() + optParam.getBackTestBenefit());
			}
			
			
			else if(columnIndex==signal.getParameters().size()+7){
				return optParam.getSatus().toString();
			}
			
			return element.toString();
		}
		
	}
	
	
	public static final String SIGNAL_OPTIMIZATION_EDITOR_ID="com.munch.exchange.partdescriptor.chart.signal.optimization.editor";
	
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	IbChartSignal signal;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	@Inject
	IIBChartIndicatorProvider chartIndicatorProvider;
	
	
	private List<ExBar> allCollectedBars;
	
	
	private HashMap<String,LinkedList<ExBar>> backTestingBarsMap=new HashMap<>();
	private HashMap<String,LinkedList<ExBar>> optimizationBarsMap=new HashMap<>();
	private HashMap<String,LinkedList<ExBar>> allBarsMap=new HashMap<>();
	
	
	LinkedList<String> sortedAlgorithmNames;
	
	LinkedList<IbChartSignalOptimizedParameters> selectedParameters=new LinkedList<>();
	
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
	private Combo comboBarType;
	private Label lblPercentOfTraining;
	private Spinner spinnerPercentOfTraningData;
	
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
		
		comboBarType = new Combo(groupControls, SWT.NONE);
		comboBarType.setItems(new String[] {"Bar Size", "Bar Range"});
		comboBarType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboBarType.select(0);
		comboBarType.setText("Bar Size");
		comboBarType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				comboBarSize.removeAll();
				if(comboBarType.getText().equals("Bar Size")){
					for(String bSize:BarUtils.getAllBarSizesAsString())
						comboBarSize.add(bSize);
					comboBarSize.setText(comboBarSize.getItem(0));
				}
				else{
					for(String bSize:BarUtils.getAllBarRangesForForex())
						comboBarSize.add(bSize);
					comboBarSize.setText(comboBarSize.getItem(4));
				}
			}
		});
		
		comboBarSize = new Combo(groupControls, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String bSize:BarUtils.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		comboBarSize.setText(comboBarSize.getItem(0));
		
		lblPercentOfData = new Label(groupControls, SWT.NONE);
		lblPercentOfData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPercentOfData.setText("Percent of Data:");
		
		spinnerPercentOfData = new Spinner(groupControls, SWT.BORDER);
		spinnerPercentOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerPercentOfData.setPageIncrement(1);
		spinnerPercentOfData.setMinimum(1);
		spinnerPercentOfData.setSelection(50);
		
		lblPercentOfTraining = new Label(groupControls, SWT.NONE);
		lblPercentOfTraining.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPercentOfTraining.setText("Percent of Training Data:");
		
		spinnerPercentOfTraningData = new Spinner(groupControls, SWT.BORDER);
		spinnerPercentOfTraningData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerPercentOfTraningData.setMinimum(1);
		spinnerPercentOfTraningData.setSelection(60);
		
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
		comboReportType.select(resultTypeListModel.size()-1);
		
		compositeCommandBtns = new Composite(compositeCommand, SWT.NONE);
		compositeCommandBtns.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeCommandBtns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnRun = new Button(compositeCommandBtns, SWT.NONE);
		btnRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Click on btnRun");
				
				OptJobStater stater=new OptJobStater(comboBarType.getText(),
													comboBarSize.getText(),
													comboAlgorithm.getText(),
													comboReportType.getText(),
													spinnerMaxNFE.getSelection(),
													spinnerSeeds.getSelection(),
													spinnerPercentOfData.getSelection(),
													spinnerPercentOfTraningData.getSelection());
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
				/*
				NondominatedPopulation referenceSet = new NondominatedPopulation();
				for(IbChartSignalOptimizedParameters optParameters:signal.getOptimizedSet()){
					Solution IbChartSignalProblem.createSolutionFromIbChartParameter(parameters)
				}
				*/
				
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
				
				
				tabFolder.setSelection(2);
				
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
		tableViewerBestResults.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				IStructuredSelection structuredSelection =(IStructuredSelection) event.getSelection();
				Iterator<?> iter = structuredSelection.iterator();
			    int nbOfSelection=0;
			    int nbOfNew=0;
			    int nbOfSaved=0;
			    int nbOfCurrent=0;
			    
			    selectedParameters.clear();
			    
			    
				while ( iter.hasNext() ) {
			        Object next = iter.next();
			        
			        if(!(next instanceof IbChartSignalOptimizedParameters))
			        	continue;
			        
			        IbChartSignalOptimizedParameters optParameters=(IbChartSignalOptimizedParameters) next;
			        nbOfSelection++;
			        
			        if(optParameters.getSatus()==com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.NEW){
			        	nbOfNew++;
			        }
			        else if(optParameters.getSatus()==com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.SAVED){
			        	nbOfSaved++;
			        }
			        else if(optParameters.getSatus()==com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.CURRENT){
			        	nbOfCurrent++;
			        }
			        
			        selectedParameters.add(optParameters);
			        
			    }
				
				if(!selectedParameters.isEmpty()){
					logger.info("Performance metric: "+selectedParameters.getFirst().getPerformanceMetrics()!=null);
					eventBroker.post(IEventConstant.IB_CHART_INDICATOR_OPTIMIZED_PARAMETERS_SELECTED, selectedParameters.getFirst());
				}
				
				btnSave.setEnabled(nbOfNew>0);
				btnRemove.setEnabled(nbOfSaved>0);
				btnActivate.setEnabled(nbOfSaved>0 && nbOfSelection==1);
				
				
			}
		});
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
				logger.info("Press Button Activate");
				
				IbChartSignalOptimizedParameters optParameters=selectedParameters.get(0);
				
				IbChartParameter.copyValuesOnly(optParameters.getParameters(), signal.getParameters());
				
				//chartIndicatorProvider.update(signal);
				
				bestResultContentProvider.refreshOptSet();
				tableViewerBestResults.refresh();
				
				
//				eventBroker.post(IEventConstant.IB_CHART_INDICATOR_PARAMETER_CHANGED, signal);
				eventBroker.post(IEventConstant.IB_CHART_INDICATOR_NEW_CURRENT_PARAMETER, signal);
			}
		});
		btnActivate.setText("Activate");
		
		btnSave = new Button(compositeBestResultButton, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				logger.info("Press Button Save");
//				logger.info("Nb of parameters: "+signal.getParameters().size());
				
				for(IbChartSignalOptimizedParameters optParameters:selectedParameters){
					if(optParameters.getSatus()==com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.NEW){
						signal.addOptimizedParameters(optParameters);
					}
				}
				
				chartIndicatorProvider.updateOptimizedParameters(signal);
				
				bestResultContentProvider.refreshOptSet();
				tableViewerBestResults.refresh();
				
			}
		});
		btnSave.setBounds(0, 0, 105, 35);
		btnSave.setText("Save");
		
		btnRemove = new Button(compositeBestResultButton, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Press Button Remove");
				
				for(IbChartSignalOptimizedParameters optParameters:selectedParameters){
					if(optParameters.getSatus()==com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters.Status.SAVED){
						signal.removeOptimizedParameters(optParameters);
					}
				}
				
				chartIndicatorProvider.updateOptimizedParameters(signal);
				
				bestResultContentProvider.refreshOptSet();
				tableViewerBestResults.refresh();
				
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
				
				StatisticCalculator statisticCalculator=new StatisticCalculator(signal,
						comboBarType.getText(),
						comboBarSize.getText(),
						spinnerPercentOfData.getSelection(),
						spinnerPercentOfTraningData.getSelection());
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
		
		
		controller=new IbChartSignalOptimizationController();
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
		tableBestResults.setEnabled(!controller.isRunning());
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
	
	
	
	
	private BarContainer getBarContainer(){
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
			tabFolder.setSelection(1);
		
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
		
		private String barType;
		private String barSize;
		private String algorithmName;
		private String reportType;
		private int numberOfEvaluations;
		private int numberOfSeeds;
		private int percentOfDataRequired;
		private int percentOfTrainingData;
		private IbChartSignal jobSignal;
		
		
		public OptJobStater(String barType, String barSize, String algorithmName, String reportType,
				int numberOfEvaluations, int numberOfSeeds, int percentOfDataRequired, int percentOfTrainingData) {
			
			super("Signal Optimization job Starter: "+signal.getName());
			
			
			this.barType=barType;
			this.barSize = barSize;
//			this.barSize = barSize;e
			this.algorithmName=algorithmName;
			this.reportType=reportType;
			this.numberOfEvaluations=numberOfEvaluations;
			this.numberOfSeeds=numberOfSeeds;
			this.percentOfDataRequired=percentOfDataRequired;
			this.percentOfTrainingData=percentOfTrainingData;
			this.jobSignal=(IbChartSignal)signal.copy();
			this.jobSignal.setIsolateLastNeededBars(false);
			
			
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
				controller.setIncludeElapsedTime(true);
				controller.setIncludeApproximationSet(true);
				controller.setIncludePopulationSize(false);
			}
			
		}
		

		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			//Collect the Data
			logger.info("Collect the data");
			if(allCollectedBars==null || allCollectedBars.isEmpty()){
				if(this.barType.equals("Bar Size")){
					allCollectedBars=hisDataProvider.getAllTimeBars(getBarContainer(),
						BarUtils.getBarSizeFromString(barSize));
				}
				else{
					allCollectedBars = hisDataProvider.getAllRangeBars(getBarContainer(),
							BarUtils.convertForexRange(barSize));
				}
				Collections.sort(allCollectedBars, new ExBarComparator());
			}
			
			//Create the Data Set
			logger.info("Create the Data Set");
			createTheDataSet(barSize, percentOfDataRequired, percentOfTrainingData);
			
			//Set the parameters
			jobSignal.setOptimizationBars(optimizationBarsMap.get(barSize+percentOfTrainingData));
			jobSignal.setAllBars(allBarsMap.get(barSize));
			
			jobSignal.setAlgorithmName(algorithmName);
			jobSignal.setNumberOfEvaluations(numberOfEvaluations);
			jobSignal.setNumberOfSeeds(numberOfSeeds);
			jobSignal.setBarSize(barSize);
			
			signal.setAlgorithmName(algorithmName);
			signal.setNumberOfEvaluations(numberOfEvaluations);
			signal.setNumberOfSeeds(numberOfSeeds);
			signal.setBarSize(barSize);
			
			//Prepare the blocks
			jobSignal.setBatch(true);
			jobSignal.activatedDataCollector();
//			jobSignal.createBlocks(jobSignal.getOptimizationBars());
			
			
			//Start the optimization
			logger.info("Start the optimization");
			controller.setSignal(jobSignal);
			controller.run();
			
			return Status.OK_STATUS;
		}

		
	}
	
	private LinkedList<ExBar> isolateAllRequiredBars(int percentOfDataRequired){
		LinkedList<ExBar> allRequiredBars=new LinkedList<ExBar>();
		int i=0;
		int percentOfDataToIgnore=100-percentOfDataRequired;
		for(ExBar bar:allCollectedBars){
			i++;
			if(i*100/allCollectedBars.size()<percentOfDataToIgnore)continue;
			
			allRequiredBars.add(bar);
		}
		return allRequiredBars;
	}
	
	private void createTheDataSet(String bazSize, int percentOfDataRequired, int percentOfTrainingData){
		if(!optimizationBarsMap.containsKey(bazSize+percentOfTrainingData)){
			
			LinkedList<ExBar> allRequiredBars=isolateAllRequiredBars(percentOfDataRequired);
			
			LinkedList<LinkedList<ExBar>> allBlocks=BarUtils.splitBarListInDayBlocks(allRequiredBars);
		
//			Save all bars
			LinkedList<ExBar> allBars = new  LinkedList<ExBar>();
			for(LinkedList<ExBar> bars:allBlocks){
				allBars.addAll(bars);
			}
			Collections.sort(allBars, new ExBarComparator());
			allBarsMap.put(bazSize, allBars);
			
			
			LinkedList<LinkedList<ExBar>> optBlocks=BarUtils.collectPercentageOfBlocks(allBlocks,percentOfTrainingData);
			
//			Save the optimization bars
			LinkedList<ExBar> optimizationBars=new LinkedList<ExBar>();
			HashSet<Long> timeSet=new HashSet<>();
			for(LinkedList<ExBar> bars:optBlocks){
				optimizationBars.addAll(bars);
				for(ExBar bar:bars)
					timeSet.add(bar.getTime());
			}
			Collections.sort(optimizationBars, new ExBarComparator());
			optimizationBarsMap.put(bazSize+percentOfTrainingData, optimizationBars);
			
//			Save the back testing bars
			LinkedList<ExBar> backTestingBars=new LinkedList<ExBar>();
			logger.info("Total Nb. of  data: "+allCollectedBars.size());
			logger.info("Total Nb. of  required data: "+allRequiredBars.size());
			for(ExBar bar:allRequiredBars){
				if(timeSet.contains(bar.getTime()))continue;
				backTestingBars.add(bar);
			}
			Collections.sort(backTestingBars, new ExBarComparator());
			logger.info("Nb. of back testing data: "+backTestingBars.size());
			
			
			backTestingBarsMap.put(bazSize+percentOfTrainingData, backTestingBars);
			
		}
	}
	
	
	//#######################################################
  	//##         Best Result statistic calculator          ##
  	//#######################################################
	private class StatisticCalculator extends Job{
		
		
		private IbChartSignal chartSignal;
		private String barType;
		private String barSize;
		private int percentOfDataRequired;
		private int percentOfTrainingData;
		
		
		public StatisticCalculator(IbChartSignal chartSignal,String barType, String bazSize, int percentOfDataRequired, int percentOfTrainingData) {
			super("Statistic Calculator");
			this.chartSignal=chartSignal;
			this.barType=barType;
			this.barSize=bazSize;
			this.percentOfDataRequired=percentOfDataRequired;
			this.percentOfTrainingData=percentOfTrainingData;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			disableBtns();
			
			logger.info("Collect the data");
			if(allCollectedBars==null || allCollectedBars.isEmpty()){
				allCollectedBars=hisDataProvider.getAllTimeBars(getBarContainer(),
						BarUtils.getBarSizeFromString(barSize));
				Collections.sort(allCollectedBars, new ExBarComparator());
			}
			
			if(allCollectedBars==null || allCollectedBars.isEmpty()){
				if(this.barType.equals("Bar Size")){
					allCollectedBars=hisDataProvider.getAllTimeBars(getBarContainer(),
						BarUtils.getBarSizeFromString(barSize));
				}
				else{
					allCollectedBars = hisDataProvider.getAllRangeBars(getBarContainer(),
							BarUtils.convertForexRange(barSize));
				}
				Collections.sort(allCollectedBars, new ExBarComparator());
			}
			
			
			
			//Create the Data Set
			logger.info("Create the Data Set");
			createTheDataSet(barSize, percentOfDataRequired, percentOfTrainingData );
			
			LinkedList<ExBar> allRequiredBars=isolateAllRequiredBars(percentOfDataRequired);
			
			int processors = Runtime.getRuntime().availableProcessors();
			if(processors > 1)processors--;
			
			ExecutorService taskExecutor = Executors.newFixedThreadPool(processors);
			
			chartSignal.activatedDataCollector();
			for(IbChartSignalOptimizedParameters optParam:bestResultContentProvider.getOptParametersSet()){
				IbChartSignal signal=(IbChartSignal) chartSignal.copy();
				
				taskExecutor.execute(new StatisticTask(
						optimizationBarsMap.get(barSize+percentOfTrainingData),
						backTestingBarsMap.get(barSize+percentOfTrainingData),
						allRequiredBars, signal, optParam));
				
			}
			
			taskExecutor.shutdown();
			
			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new GeneticError(e);
				
			}
			
			chartSignal.deactivatedDataCollector();
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
					tableBestResults.setEnabled(true);
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
					tableBestResults.setEnabled(false);
				}
			});
		}
		
	}
	
	public class StatisticTask implements Runnable {
		
		
		LinkedList<ExBar> optimizationBars= new LinkedList<ExBar>();
		LinkedList<ExBar> backTestingBars= new LinkedList<ExBar>();
		LinkedList<ExBar> requiredBars= new LinkedList<ExBar>();
		IbChartSignal signal;
		IbChartSignalOptimizedParameters optParam;
		
		

		public StatisticTask(LinkedList<ExBar> optimizationBars, LinkedList<ExBar> backTestingBars,
				LinkedList<ExBar> requredBarsBars, IbChartSignal signal, IbChartSignalOptimizedParameters optParam) {
			super();
			this.optimizationBars = optimizationBars;
			this.backTestingBars = backTestingBars;
			this.requiredBars = requredBarsBars;
			this.signal = signal;
			this.optParam = optParam;
		}

		@Override
		public void run() {
			signal.setIsolateLastNeededBars(false);
			
			//Set the parameters
			signal.setParameters(optParam.getParameters());
			
			//Opt. Bars
//			logger.info("Calculate Statistics Opt. Bars!");
			signal.setBatch(true);
			signal.setOptimizationBars(optimizationBars);
			signal.compute(requiredBars);
			double[] profitAndRisk=IbChartSignalProblem.extractProfitAndRiskFromChartSignal(signal);
			optParam.setOptBenefit(profitAndRisk[0]);
			optParam.setOptRisk(profitAndRisk[1]);
			
			refreshTable();
			
//			logger.info("Calculate Statistics Back Testing. Bars!");
			signal.setBatch(true);
			signal.setOptimizationBars(backTestingBars);
			signal.compute(requiredBars);
			profitAndRisk=IbChartSignalProblem.extractProfitAndRiskFromChartSignal(signal);
			optParam.setBackTestBenefit(profitAndRisk[0]);
			optParam.setBackTestRisk(profitAndRisk[1]);
			
			refreshTable();
			
//			logger.info("Calculate Statistics All Bars!");
			signal.setBatch(false);
			signal.setOptimizationBlocks(null);
			signal.compute(requiredBars);
//			logger.info("Performance metric: "+(signal.getPerformanceMetrics()!=null));
			optParam.setPerformanceMetrics(signal.getPerformanceMetrics());
//			logger.info("Performance metric: "+(optParam.getPerformanceMetrics()!=null));
			
			refreshTable();
			
		}
		
		private void refreshTable(){
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					tableViewerBestResults.refresh();
				}
			});
			
		}
		
	}
	
	
	
}
