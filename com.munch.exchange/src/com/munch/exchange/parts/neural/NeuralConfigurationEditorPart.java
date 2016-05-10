 
package com.munch.exchange.parts.neural;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;
import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.ml.factory.method.NEATFactory;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateFactory;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.persist.PersistorRegistry;
import org.moeaframework.analysis.collector.ElapsedTimeCollector;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.AddNeuralArchitectureDialog;
import com.munch.exchange.dialog.NeatTrainingDialog;
import com.munch.exchange.dialog.TrainNeuralArchitectureDialog;
import com.munch.exchange.model.core.encog.NoveltySearchEA;
import com.munch.exchange.model.core.encog.NoveltySearchGenome;
import com.munch.exchange.model.core.encog.NoveltySearchGenomeFactory;
import com.munch.exchange.model.core.encog.NoveltySearchPopulation;
import com.munch.exchange.model.core.encog.NoveltySearchUtil;
import com.munch.exchange.model.core.encog.PersistNoveltySearchPopulation;
import com.munch.exchange.model.core.ib.IbContract.TradingPeriod;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.neural.BestGenomes;
import com.munch.exchange.model.core.ib.neural.GenomeEvaluation;
import com.munch.exchange.model.core.ib.neural.IsolatedNeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.ArchitectureType;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.TrainingMethod;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration.ReferenceData;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration.SplitStrategy;
import com.munch.exchange.model.core.ib.neural.NeuralDayOfWeekInput;
import com.munch.exchange.model.core.ib.neural.NeuralDayPositionInput;
import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent;
import com.munch.exchange.model.core.ib.neural.NeuralNetwork;
import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;
import com.munch.exchange.model.core.ib.neural.NeuralTrainingElement;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent.ComponentType;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.interfaces.IIBNeuralProvider;

public class NeuralConfigurationEditorPart {
	
	
	
	private static Logger logger = Logger.getLogger(NeuralConfigurationEditorPart.class);
	
	public static final String EDITOR_ID="com.munch.exchange.partdescriptor.neuralconfiguration.editor";
	private Button btnResetMinmax;
	private Button btnEdit;
	private TabFolder tabFolder;
	
	@Inject
	private Shell shell;
	
	@Inject
	IEclipseContext context;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private NeuralConfiguration neuralConfiguration;
	
	private NeuralArchitecture neuralArchitecture;
	
	private List<NeuralNetwork> selectedNetworks=new LinkedList<NeuralNetwork>();
	
	@Inject
	private IIBNeuralProvider neuralProvider;
	
	@Inject
	private IIBHistoricalDataProvider historicalDataProvider;
	
	private NeuralConfigurationInputTreeDropAdapter  dropAdapter;
	
	@Inject
	MDirtyable dirty;
	private Tree treeInputData;
	private TreeViewer treeViewerInputData;
	private MenuItem mntmDelete;
	private MenuItem mntmAddDayPosition;
	private MenuItem mntmAddDayOfWeek;
	private MenuItem mntmAddComponent;
	private Text textNbOfData;
	private Tree treeTrainingData;
	private TreeViewer treeViewerTrainingData;
	private Spinner spinnerPercentOfTrainingData;
	private Button btnViewTrainingData;
	private Button btnSearch;
	private Combo comboReferenceData;
	private Combo comboSplitStrategy;
	private Combo comboBarSize;
	private Tree treeArchitecture;
	private TreeViewer treeViewerArchitecture;
	private ProgressBar progressBarArchitecture;
	private MenuItem mntmDeleteArchitecture;
	private MenuItem mntmTrainArchitecture;
	private Menu menuArchitecture;
	private ProgressBar progressBarDataSet;
	
	private static int epoch=-1;
	private static int elapseTime=0;
	
	private static boolean cancelTraining=false;
	
	private static int dataSetCounter=-1;
	private MenuItem mntmEvaluateArchitecture;
	private MenuItem mntmTrainAll;
	private MenuItem mntmIsolate;
	
	
	@Inject
	public NeuralConfigurationEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
//		########################
//		##    PRE GUI FUNC    ##
//		########################
		
		preGuiFunc();
		
		parent.setLayout(new GridLayout(1, false));
		
		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setSelection(1);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
//		########################
//		##        INPUT       ##
//		########################
		
		
		TabItem tbtmInput = new TabItem(tabFolder, SWT.NONE);
		tbtmInput.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/action1.gif"));
		tbtmInput.setText("Input");
		
		Composite compositeInput = new Composite(tabFolder, SWT.NONE);
		tbtmInput.setControl(compositeInput);
		compositeInput.setLayout(new GridLayout(1, false));
		
		Composite compositeActionItems = new Composite(compositeInput, SWT.NONE);
		compositeActionItems.setLayout(new GridLayout(2, false));
		compositeActionItems.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnEdit = new Button(compositeActionItems, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				treeInputData.setEnabled(true);
				btnEdit.setEnabled(false);
			}
		});
		btnEdit.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/tree_explorer.gif"));
		btnEdit.setText("Edit");
		
		btnResetMinmax = new Button(compositeActionItems, SWT.NONE);
		btnResetMinmax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetMinMax();
			}
		});
		btnResetMinmax.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/glyph7.gif"));
		btnResetMinmax.setText("Min/Max");
		btnResetMinmax.setEnabled(false);
		
		treeViewerInputData = new TreeViewer(compositeInput, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewerInputData.setContentProvider(new NeuralConfigurationInputTreeContentProvider(neuralConfiguration));
		treeViewerInputData.setInput(neuralConfiguration);
		treeInputData = treeViewerInputData.getTree();
		treeInputData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mntmDelete.setEnabled(false);
				mntmAddComponent.setEnabled(false);
				mntmAddDayOfWeek.setEnabled(false);
				mntmAddDayPosition.setEnabled(false);
				btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
				
				if(e.button==3 && treeInputData.getSelection().length==1){
					
					mntmAddDayOfWeek.setEnabled(true);
					mntmAddDayPosition.setEnabled(true);
					for(NeuralInput input:neuralConfiguration.getNeuralInputs()){
						logger.info("Input: "+input.getClass().toString());
						if(input instanceof NeuralDayPositionInput){
							mntmAddDayPosition.setEnabled(false);
						}
						
						if(input instanceof NeuralDayOfWeekInput){
							mntmAddDayOfWeek.setEnabled(false);
						}
					}
					
					
					
					TreeItem item=treeInputData.getSelection()[0];
					if(item.getData() instanceof NeuralInput){
						mntmDelete.setEnabled(true);
//						mntmAddComponent.setEnabled(true);
					}
					else if(item.getData() instanceof NeuralInputComponent){
						NeuralInputComponent nic=(NeuralInputComponent) item.getData();
						mntmDelete.setEnabled(nic.getNeuralInput().getComponents().size()>1);
						
						if(nic.getNeuralInput() instanceof NeuralDayPositionInput){
							mntmAddComponent.setEnabled(false);
						}
						else if(nic.getNeuralInput() instanceof NeuralDayOfWeekInput){
							mntmAddComponent.setEnabled(false);
						}
						else{
							mntmAddComponent.setEnabled(true);
						}
					}
				}
				
			}
		});
		treeInputData.setEnabled(false);
		treeInputData.setLinesVisible(true);
		treeInputData.setHeaderVisible(true);
		treeInputData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		//Add Drop Support
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		createDropAdaptor(treeViewerInputData);
		treeViewerInputData.addDropSupport(operations, transferTypes, dropAdapter);
		
		Menu menuInputData = new Menu(treeInputData);
		treeInputData.setMenu(menuInputData);
		
		mntmAddDayOfWeek = new MenuItem(menuInputData, SWT.NONE);
		mntmAddDayOfWeek.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				logger.info("Day of week pressed!");
				for(NeuralInput input:neuralConfiguration.getNeuralInputs()){	
					if(input instanceof NeuralDayOfWeekInput){
						return;
					}
				}
				
				NeuralDayOfWeekInput input=new NeuralDayOfWeekInput();
				input.addTo(neuralConfiguration);
				dirty.setDirty(true);
				treeViewerInputData.refresh();
				treeViewerInputData.expandAll();
			}
		});
		mntmAddDayOfWeek.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/configs.gif"));
		mntmAddDayOfWeek.setText("Add Day Of Week");
		
		mntmAddDayPosition = new MenuItem(menuInputData, SWT.NONE);
		mntmAddDayPosition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				logger.info("Day position!");
				
				for(NeuralInput input:neuralConfiguration.getNeuralInputs()){	
					if(input instanceof NeuralDayPositionInput){
						return;
					}
				}
				
				NeuralDayPositionInput input=new NeuralDayPositionInput();
				input.addTo(neuralConfiguration);
				dirty.setDirty(true);
				treeViewerInputData.refresh();
				treeViewerInputData.expandAll();
			}
		});
		mntmAddDayPosition.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/configs.gif"));
		mntmAddDayPosition.setText("Add Day Position");
		
		mntmAddComponent = new MenuItem(menuInputData, SWT.NONE);
		mntmAddComponent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				logger.info("Press on add component!");
				TreeItem item=treeInputData.getSelection()[0];
				NeuralInput neuralInput=null;
				
				if(item.getData() instanceof NeuralInput){
					neuralInput=(NeuralInput) item.getData();
					neuralInput.addDirectComponent();
					dirty.setDirty(true);
				}
				else if(item.getData() instanceof NeuralInputComponent){
					NeuralInputComponent nic=(NeuralInputComponent) item.getData();
					neuralInput=nic.getNeuralInput();
					NeuralInputComponent new_nic=nic.copy();
					new_nic.setId(0);
					nic.setUpperRange(0);
					nic.setLowerRange(0);
					new_nic.setOffset(nic.getOffset()+1);
					neuralInput.getComponents().add(new_nic);
					dirty.setDirty(true);
				}
				
				
				treeViewerInputData.refresh();
				treeViewerInputData.expandAll();
			}
		});
		mntmAddComponent.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/add_obj.gif"));
		mntmAddComponent.setText("Add Component");
		
		new MenuItem(menuInputData, SWT.SEPARATOR);
		
		mntmDelete = new MenuItem(menuInputData, SWT.NONE);
		mntmDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Press on delete!");
				TreeItem item=treeInputData.getSelection()[0];
				if(item.getData() instanceof NeuralInput){
					NeuralInput neuralInput=(NeuralInput) item.getData();
					neuralConfiguration.getNeuralInputs().remove(neuralInput);
					dirty.setDirty(true);
					treeViewerInputData.refresh();
				}
				else if(item.getData() instanceof NeuralInputComponent){
					NeuralInputComponent nic=(NeuralInputComponent) item.getData();
					NeuralInput neuralInput=nic.getNeuralInput();
					neuralInput.getComponents().remove(nic);
					dirty.setDirty(true);
					treeViewerInputData.refresh();
				}
			}
		});
		mntmDelete.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/error.gif"));
		mntmDelete.setText("Delete");
		
		TreeViewerColumn treeViewerColumnInputDataName = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnInputDataName.setLabelProvider(new InputDataNameLabelProvider());
		TreeColumn trclmnName = treeViewerColumnInputDataName.getColumn();
		trclmnName.setWidth(200);
		trclmnName.setText("Name");
		
		TreeViewerColumn treeViewerColumnInputDataSource = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnInputDataSource.setLabelProvider(new InputDataSourceLabelProvider());
		TreeColumn trclmnSource = treeViewerColumnInputDataSource.getColumn();
		trclmnSource.setWidth(100);
		trclmnSource.setText("Source");
		
		TreeViewerColumn treeViewerColumnInputDataType = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnInputDataType.setLabelProvider(new InputDataTypeLabelProvider());
		treeViewerColumnInputDataType.setEditingSupport(new InputDataTypeEditingSupport(treeViewerInputData));
		TreeColumn trclmnType = treeViewerColumnInputDataType.getColumn();
		trclmnType.setWidth(100);
		trclmnType.setText("Type");
		
		TreeViewerColumn treeViewerColumnInputDataPeriod = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnInputDataPeriod.setLabelProvider(new InputDataPeriodLabelProvider());
		treeViewerColumnInputDataPeriod.setEditingSupport(new InputDataPeriodEditingSupport(treeViewerInputData));
		TreeColumn trclmnPeriod = treeViewerColumnInputDataPeriod.getColumn();
		trclmnPeriod.setWidth(100);
		trclmnPeriod.setText("Period");
		
		TreeViewerColumn treeViewerColumnOffsetDataPeriod = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnOffsetDataPeriod.setLabelProvider(new InputDataOffsetLabelProvider());
		treeViewerColumnOffsetDataPeriod.setEditingSupport(new InputDataOffsetEditingSupport(treeViewerInputData));
		TreeColumn trclmnOffset = treeViewerColumnOffsetDataPeriod.getColumn();
		trclmnOffset.setWidth(100);
		trclmnOffset.setText("Offset");
		
		TreeViewerColumn treeViewerColumnInputDataMin = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnInputDataMin.setLabelProvider(new InputDataMinLabelProvider());
		TreeColumn trclmnMin = treeViewerColumnInputDataMin.getColumn();
		trclmnMin.setWidth(100);
		trclmnMin.setText("Min");
		
		TreeViewerColumn treeViewerColumnInputDataMax = new TreeViewerColumn(treeViewerInputData, SWT.NONE);
		treeViewerColumnInputDataMax.setLabelProvider(new InputDataMaxLabelProvider());
		TreeColumn trclmnMax = treeViewerColumnInputDataMax.getColumn();
		trclmnMax.setWidth(100);
		trclmnMax.setText("Max");
		
//		#########################
//		##       DATA SET      ##
//		#########################
		
		TabItem tbtmDataSet = new TabItem(tabFolder, SWT.NONE);
		tbtmDataSet.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/action2.gif"));
		tbtmDataSet.setText("Data Set");
		
		Composite compositeDataSet = new Composite(tabFolder, SWT.NONE);
		tbtmDataSet.setControl(compositeDataSet);
		compositeDataSet.setLayout(new GridLayout(1, false));
		
		Composite compositeDataSetCommandItems = new Composite(compositeDataSet, SWT.NONE);
		compositeDataSetCommandItems.setLayout(new GridLayout(5, false));
		compositeDataSetCommandItems.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblBarSize = new Label(compositeDataSetCommandItems, SWT.NONE);
		lblBarSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBarSize.setText("Bar size: ");
		
		comboBarSize = new Combo(compositeDataSetCommandItems, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String bSize:BarUtils.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		
		Label lblReferenceData = new Label(compositeDataSetCommandItems, SWT.NONE);
		lblReferenceData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblReferenceData.setText("Reference Data: ");
		
		comboReferenceData = new Combo(compositeDataSetCommandItems, SWT.NONE);
		comboReferenceData.setItems(new String[] {"MID POINT", "BID & ASK"});
		comboReferenceData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboReferenceData.select(0);
		
		btnSearch = new Button(compositeDataSetCommandItems, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				SearchAndDistribute job=new SearchAndDistribute();
				job.schedule();
				
				btnSearch.setEnabled(false);
				
//				searchReferenceDataFunc();
//				distributeDataFunc();
			}
		});
		btnSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSearch.setText("Search And Distribute");
		
		Label lblPercentOfTraining = new Label(compositeDataSetCommandItems, SWT.NONE);
		lblPercentOfTraining.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPercentOfTraining.setText("Percent of Training Data: ");
		
		spinnerPercentOfTrainingData = new Spinner(compositeDataSetCommandItems, SWT.BORDER);
		spinnerPercentOfTrainingData.setSelection(60);
		spinnerPercentOfTrainingData.setPageIncrement(5);
		spinnerPercentOfTrainingData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSplitStrategy = new Label(compositeDataSetCommandItems, SWT.NONE);
		lblSplitStrategy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSplitStrategy.setText("Split Strategy:");
		
		comboSplitStrategy = new Combo(compositeDataSetCommandItems, SWT.NONE);
		comboSplitStrategy.setItems(new String[] {"WEEK", "DAY"});
		comboSplitStrategy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if(neuralConfiguration.getContract().getTraidingPeriod()==TradingPeriod.WEEKLY){
			comboSplitStrategy.select(0);
		}
		else{
			comboSplitStrategy.select(1);
		}
		comboSplitStrategy.setEnabled(false);
		
		btnViewTrainingData = new Button(compositeDataSetCommandItems, SWT.NONE);
		btnViewTrainingData.setEnabled(false);
		btnViewTrainingData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewInputData();
			}
		});
		btnViewTrainingData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnViewTrainingData.setText("View Data");
		btnViewTrainingData.setEnabled(false);
		
		treeViewerTrainingData = new TreeViewer(compositeDataSet,SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewerTrainingData.setContentProvider(new NeuralConfigurationTrainingDataContentProvider());
//		treeViewerTrainingData.setInput(neuralConfiguration);
		
		treeTrainingData = treeViewerTrainingData.getTree();
		treeTrainingData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeTrainingData.setHeaderVisible(true);
		treeTrainingData.setLinesVisible(true);
		
		Composite composite = new Composite(compositeDataSet, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		progressBarDataSet = new ProgressBar(composite, SWT.NONE);
		progressBarDataSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfData = new Label(composite, SWT.NONE);
		lblNbOfData.setText("Number of Data: ");
		
		textNbOfData = new Text(composite, SWT.BORDER);
		textNbOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textNbOfData.setEditable(false);
		
		
		
//		#############################
//		##       ARCHITECTURE      ##
//		#############################
		
		TabItem tbtmArchitectures = new TabItem(tabFolder, SWT.NONE);
		tbtmArchitectures.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/action3.gif"));
		tbtmArchitectures.setText("Architectures");
		
		Composite compositeArchitecture = new Composite(tabFolder, SWT.NONE);
		tbtmArchitectures.setControl(compositeArchitecture);
		compositeArchitecture.setLayout(new GridLayout(1, false));
		
		Composite compositeArchitectureTop = new Composite(compositeArchitecture, SWT.NONE);
		compositeArchitectureTop.setLayout(new GridLayout(1, false));
		compositeArchitectureTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		treeViewerArchitecture = new TreeViewer(compositeArchitectureTop,SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewerArchitecture.setContentProvider(new NeuralConfiguationArchitectureContentProvider());
		treeViewerArchitecture.setInput(neuralConfiguration);
		
		treeArchitecture = treeViewerArchitecture.getTree();
		treeArchitecture.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mntmDeleteArchitecture.setEnabled(false);
				mntmTrainArchitecture.setEnabled(false);
				mntmTrainAll.setEnabled(false);
				mntmIsolate.setEnabled(false);
				
				if(e.button==3 && treeArchitecture.getSelection().length==1){
					
					TreeItem item=treeArchitecture.getSelection()[0];
					if(item.getData() instanceof NeuralArchitecture){
						NeuralArchitecture archi=(NeuralArchitecture) item.getData();
						mntmDeleteArchitecture.setEnabled(true);
						mntmTrainArchitecture.setEnabled(epoch<0);
						mntmTrainAll.setEnabled(epoch<0 && archi.getNeuralNetworks().size()>0);
					}
					else if(item.getData() instanceof NeuralNetwork){
						
						mntmDeleteArchitecture.setEnabled(true);
						mntmTrainArchitecture.setEnabled(epoch<0);
						
						NeuralNetwork network=(NeuralNetwork) item.getData();
						if(!network.isNEAT()){
							mntmIsolate.setEnabled(true);
						}
						
					}
					else if(item.getData() instanceof Object[]){
						Object[] genome_data=(Object[]) item.getData();
						if(genome_data.length==5 && genome_data[0]  instanceof Genome){
							mntmIsolate.setEnabled(true);
						}
					}
					
				}
				if(e.button==1 && treeArchitecture.getSelection().length==1){
					TreeItem item=treeArchitecture.getSelection()[0];
					if(item.getData() instanceof NeuralNetworkRating){
						NeuralNetworkRating rating=(NeuralNetworkRating)item.getData();
						eventBroker.post(IEventConstant.TEXT_INFO,rating.positionTrackingToString());
						
					}
				}
				
				
			}
		});
		treeArchitecture.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeArchitecture.setHeaderVisible(true);
		treeArchitecture.setLinesVisible(true);
		
		menuArchitecture = new Menu(treeArchitecture);
		treeArchitecture.setMenu(menuArchitecture);
		
		MenuItem mntmAddArchitecture = new MenuItem(menuArchitecture, SWT.NONE);
		mntmAddArchitecture.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/add_obj.gif"));
		mntmAddArchitecture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddNeuralArchitectureDialog dialog=new AddNeuralArchitectureDialog(shell);
				if (dialog.open() == Window.OK) {
//					logger.info("Architecture Name: "+dialog.getNeuralArchitecture().getName());
//					neuralConfiguration.getNeuralArchitectures().add(dialog.getNeuralArchitecture());
//					dialog.getNeuralArchitecture().setNeuralConfiguration(neuralConfiguration);
//					neuralProvider.updateNeuralArchitecture(neuralConfiguration);
					neuralProvider.addNeuralArchitecture(neuralConfiguration, dialog.getNeuralArchitecture());
					
//					logger.info("Nb of Architectures: "+neuralConfiguration.getNeuralArchitectures().size());
					
					treeViewerArchitecture.refresh();
				}
			}
		});
		mntmAddArchitecture.setText("Add");
		
		mntmTrainArchitecture = new MenuItem(menuArchitecture, SWT.NONE);
		mntmTrainArchitecture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if( treeArchitecture.getSelection().length==1){
					
					TreeItem item=treeArchitecture.getSelection()[0];
					if(item.getData() instanceof NeuralArchitecture){
//						Train the selected Architecture
						NeuralArchitecture architecture=(NeuralArchitecture) item.getData();
						if(architecture.getType()==ArchitectureType.Neat){
							trainNeatArchitecture(architecture);
						}
						else if(architecture.getType()==ArchitectureType.HyperNeat){
							trainHyperNeatArchitecture(architecture);
						}
						else if(architecture.getType()==ArchitectureType.NoveltySearchNeat){
							trainNoveltySearchArchitecture(architecture);
						}
						else{
							trainArchitecture(architecture);
						}
						
					}
					else if(item.getData() instanceof NeuralNetwork){
						trainNeuralNetwork((NeuralNetwork)item.getData() );
					}
					//trainNeuralNetork(NeuralNetwork network)
				}
			}
		});
		mntmTrainArchitecture.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/lrun_obj.gif"));
		mntmTrainArchitecture.setText("Train");
		
		mntmTrainAll = new MenuItem(menuArchitecture, SWT.NONE);
		mntmTrainAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeItem item=treeArchitecture.getSelection()[0];
				if(item.getData() instanceof NeuralArchitecture){
//					Train the selected Architecture
					trainAllNeuralNetworks((NeuralArchitecture) item.getData());
					
				}
				
			}
		});
		mntmTrainAll.setText("Train All");
		
		mntmEvaluateArchitecture = new MenuItem(menuArchitecture, SWT.NONE);
		mntmEvaluateArchitecture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(NeuralArchitecture architecture:neuralConfiguration.getNeuralArchitectures()){
					architecture.evaluateProfitAndRiskOfAllNetworks();
				}
				treeViewerArchitecture.refresh();
				treeViewerArchitecture.expandToLevel(2);
			}
		});
		mntmEvaluateArchitecture.setText("Evaluate");
		
		mntmIsolate = new MenuItem(menuArchitecture, SWT.NONE);
		mntmIsolate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=treeArchitecture.getSelection()[0];
				isolateNeuralNetwork(item.getData());
				
			}
		});
		mntmIsolate.setText("Isolate");
		
		new MenuItem(menuArchitecture, SWT.SEPARATOR);
		
		mntmDeleteArchitecture = new MenuItem(menuArchitecture, SWT.NONE);
		mntmDeleteArchitecture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if( treeArchitecture.getSelection().length==1){
					
					TreeItem item=treeArchitecture.getSelection()[0];
					if(item.getData() instanceof NeuralArchitecture){
						NeuralArchitecture architecture=(NeuralArchitecture) item.getData();
						neuralProvider.removeNeuralArchitecture(neuralConfiguration, architecture);
						treeViewerArchitecture.refresh();
//						treeViewerArchitecture.expandAll();
					}
					else if(item.getData() instanceof NeuralNetwork){
						NeuralNetwork network=(NeuralNetwork) item.getData();
						network.getNeuralArchitecture().getNeuralNetworks().remove(network);
						neuralProvider.updateNeuralArchitecture(neuralConfiguration);
						treeViewerArchitecture.refresh();
						treeViewerArchitecture.expandAll();
					}
					
				}
				
				
			}
		});
		mntmDeleteArchitecture.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/delete_obj.gif"));
		mntmDeleteArchitecture.setText("Delete");
		
		TreeViewerColumn treeViewerColumnArchiId = new TreeViewerColumn(treeViewerArchitecture, SWT.NONE);
		treeViewerColumnArchiId.setLabelProvider(new NeuralArchitectureIdLabelProvider());
		TreeColumn trclmnArchitectureId = treeViewerColumnArchiId.getColumn();
		trclmnArchitectureId.setWidth(100);
		trclmnArchitectureId.setText("Id");
		
		TreeViewerColumn treeViewerColumnArchiName = new TreeViewerColumn(treeViewerArchitecture, SWT.NONE);
		treeViewerColumnArchiName.setLabelProvider(new NeuralArchitectureNameLabelProvider());
		TreeColumn trclmnArchitectureName = treeViewerColumnArchiName.getColumn();
		trclmnArchitectureName.setWidth(100);
		trclmnArchitectureName.setText("Name/Score");
		
		TreeViewerColumn treeViewerColumnVolume = new TreeViewerColumn(treeViewerArchitecture, SWT.NONE);
		treeViewerColumnVolume.setLabelProvider(new NeuralArchitectureVolumeLabelProvider());
		TreeColumn trclmnVolume = treeViewerColumnVolume.getColumn();
		trclmnVolume.setWidth(100);
		trclmnVolume.setText("Volume/Tr. Profit");
		
		TreeViewerColumn treeViewerColumnArchiType = new TreeViewerColumn(treeViewerArchitecture, SWT.NONE);
		treeViewerColumnArchiType.setLabelProvider(new NeuralArchitectureTypeLabelProvider());
		TreeColumn trclmnArchitectureType = treeViewerColumnArchiType.getColumn();
		trclmnArchitectureType.setWidth(100);
		trclmnArchitectureType.setText("Type/Tr. Risk");
		
		TreeViewerColumn treeViewerColumnArchiHiddenLayer = new TreeViewerColumn(treeViewerArchitecture, SWT.NONE);
		treeViewerColumnArchiHiddenLayer.setLabelProvider(new NeuralArchitectureLayerDescriptionLabelProvider());
		TreeColumn trclmnHiddenLayer = treeViewerColumnArchiHiddenLayer.getColumn();
		trclmnHiddenLayer.setWidth(100);
		trclmnHiddenLayer.setText("Hidden Layer/B.T. Profit");
		
		TreeViewerColumn treeViewerColumnArchiActivation = new TreeViewerColumn(treeViewerArchitecture, SWT.NONE);
		treeViewerColumnArchiActivation.setLabelProvider(new NeuralArchitectureActivationFunctionLabelProvider());
		TreeColumn trclmnActivation = treeViewerColumnArchiActivation.getColumn();
		trclmnActivation.setWidth(100);
		trclmnActivation.setText("Activation/B.T. Risk");
		
		Composite compositeArchitectureBottom = new Composite(compositeArchitecture, SWT.NONE);
		compositeArchitectureBottom.setLayout(new GridLayout(2, false));
		compositeArchitectureBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		progressBarArchitecture = new ProgressBar(compositeArchitectureBottom, SWT.NONE);
		progressBarArchitecture.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBarArchitecture.setBounds(0, 0, 260, 26);
		
		Button btnCancelTraining = new Button(compositeArchitectureBottom, SWT.NONE);
		btnCancelTraining.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelTraining=true;
			}
		});
		btnCancelTraining.setText("Cancel");
		
		
//		########################
//		##    POST GUI FUNC    ##
//		########################
		
		postGuiFunc();
	}
	
	private void preGuiFunc(){
//		logger.info("Neural Config: "+neuralConfiguration);
		neuralProvider.loadNeuralInputs(neuralConfiguration);
		neuralProvider.loadNeuralArchitecture(neuralConfiguration);
		
		logger.info("Nb of Architectures: "+neuralConfiguration.getNeuralArchitectures().size());
		
//		Add the Novelty Search Persistor
		
		PersistorRegistry.getInstance().add(new PersistNoveltySearchPopulation());
		
		
	}
	
	private void postGuiFunc(){
//		neuralProvider.loadNeuralInputs(neuralConfiguration);
		treeViewerInputData.refresh();
		treeViewerInputData.expandAll();
		treeViewerArchitecture.refresh();
		btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
		btnEdit.setEnabled(neuralConfiguration.getNeuralTrainingElements().isEmpty());
		
		neuralProvider.loadTrainingData(neuralConfiguration);
		
//		Initialization of the bar size
		comboBarSize.setText(neuralConfiguration.getSize().toString());
		comboBarSize.setEnabled(neuralConfiguration.getNeuralTrainingElements().isEmpty());
		
//		Initialization of the reference data type
		if(neuralConfiguration.getReferenceData()!=null){
		switch (neuralConfiguration.getReferenceData()) {
		case MID_POINT:
			comboReferenceData.select(0);
			break;
		case BID_AND_ASK:
			comboReferenceData.select(1);
			break;
		default:
			comboReferenceData.select(0);
			break;
		}
		}
		comboReferenceData.setEnabled(false);
		
//		Initialization of the split strategy
		if(neuralConfiguration.getSplitStrategy()!=null){
		switch (neuralConfiguration.getSplitStrategy()) {
		case WEEK:
			comboSplitStrategy.select(0);
			break;
		case DAY:
			comboSplitStrategy.select(1);
			break;
		default:
			comboSplitStrategy.select(0);
			break;
		}
		}
		comboSplitStrategy.setEnabled(false);
		
//		Initialization of percent of training data
		spinnerPercentOfTrainingData.setSelection(neuralConfiguration.getPercentOfTrainingData());
		spinnerPercentOfTrainingData.setEnabled(neuralConfiguration.getNeuralTrainingElements().isEmpty());
		
		
		addColumn("Name",150,new NeuralTrainingDataLabelProvider());
		
	}
	
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider ){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewerTrainingData, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		//trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	private void removeAllInputsColumns(){
		while(treeTrainingData.getColumnCount()>1){
			treeTrainingData.getColumns()[treeTrainingData.getColumnCount()-1].dispose();
		}
	}
	
	private void createAllTrainingDataColumns(){
		
		for (NeuralInput input : neuralConfiguration.getNeuralInputs()) {
			for(NeuralInputComponent component: input.getComponents()){
				
				addColumn(component.getName() ,100,
						new NeuralInputComponentLabelProvider(component));
				
			}
		}
		
	}
	
	
	/**
	 * 1. Search the historical data of each neural indicator input
	 * and then compute the values and the range of each component
	 */
	private void resetMinMax(){
		
//		Search all needed bars and save them in the data collector
		fillTheNeuralInputsBarsCollector();
		
//		Compute the neural indicator values and reset the ranges of the components
		neuralConfiguration.computeAllNeuralIndicatorInputs(true);
		
		
		neuralProvider.updateNeuralInputs(neuralConfiguration);
		btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
		
		treeViewerInputData.refresh();
		treeViewerInputData.expandAll();
		
	}
	
	private void viewInputData(){
//		Clear the tree
		removeAllInputsColumns();
		
		treeViewerTrainingData.setInput(neuralConfiguration);
		
//		Refresh the tree
		createAllTrainingDataColumns();
		treeViewerTrainingData.refresh();
		
	}
	
	
	
	/**
	 * Search all needed bars and save them in the data collector
	 */
	private void fillTheNeuralInputsBarsCollector(){
		
		for(NeuralInput input:neuralConfiguration.getNeuralInputs()){
			if(input instanceof NeuralIndicatorInput){
				NeuralIndicatorInput nii=(NeuralIndicatorInput) input;
				
//				Get the collected bar key
				String key=nii.getCollectedBarKey();
				
				if(neuralConfiguration.getNeuralInputsBarsCollector().containsKey(key))
					continue;
				
//				logger.info("Bar Container: "+nii.getBarContainer().getId()+", size: "+nii.getSize().toString());
//				logger.info("Bar Container: "+nii.getBarContainer().getType()+", size: "+nii.getSize().toString());
//				
				
				List<ExBar> bars=historicalDataProvider.getAllTimeBars(nii.getBarContainer(), nii.getSize());
					
				neuralConfiguration.getNeuralInputsBarsCollector().put(key, bars);

			}
		}
		
	}
	
	
	/**
	 * 4. Train a architecture 
	 */
	private void trainArchitecture(NeuralArchitecture architecture){
		neuralArchitecture=architecture;
		
		TrainNeuralArchitectureDialog dialog=new TrainNeuralArchitectureDialog(shell);
		if (dialog.open() != Window.OK)return;
		
//		logger.info("Architecture Name: "+neuralArchitecture.getName());
		
		
		BasicNetwork network = neuralArchitecture.createNetwork();
		NeuralNetwork neuralNetwork=new NeuralNetwork();
		neuralNetwork.setNetwork(network);
		selectedNetworks.clear();selectedNetworks.add(neuralNetwork);
		
		prepareAndStartTraining( dialog);
		
	}
	
	private void trainNeatArchitecture(NeuralArchitecture architecture){
		neuralArchitecture=architecture;
		
		NeatTrainingDialog dialog=new NeatTrainingDialog(shell);
		if (dialog.open() != Window.OK)return;
		
		logger.info("Start Neat Trainig of Architecture Name: "+neuralArchitecture.getName());
		
//		Only use in order to get the number of input and ouput
		BasicNetwork network = neuralArchitecture.createNetwork();
		
		NEATPopulation pop = new NEATPopulation(network.getInputCount(),
												network.getOutputCount(),
												dialog.getPopulation());
//		pop.setNEATActivationFunction(network.getActivation(0));
		pop.setInitialConnectionDensity(dialog.getConnectionDensity());// not required, but speeds training
		pop.reset();
		
		EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,neuralArchitecture);
		train.setValidationMode(true);
		
		progressBarArchitecture.setMinimum(0);
		
		neuralArchitecture.prepareScoring(1,0);
		
		if(dialog.isTimeoutSet()){
			progressBarArchitecture.setMaximum((int)(dialog.getTimeout()/1000)+1);
			NeatTrainer trainer=new NeatTrainer(train, dialog.getTimeout(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		else{
			progressBarArchitecture.setMaximum(dialog.getNbOfEpoch()+1);
			NeatTrainer trainer=new NeatTrainer(train, dialog.getNbOfEpoch(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		
	}
	
	private void trainNoveltySearchArchitecture(NeuralArchitecture architecture){
		
		neuralArchitecture=architecture;
		
		NeatTrainingDialog dialog=new NeatTrainingDialog(shell,true);
		if (dialog.open() != Window.OK)return;
		
		logger.info("Start Neat Trainig of Architecture Name: "+neuralArchitecture.getName());
		
//		Only use in order to get the number of input and ouput
		BasicNetwork network = neuralArchitecture.createNetwork();
		
		NoveltySearchPopulation pop = new NoveltySearchPopulation(network.getInputCount(),
												network.getOutputCount(),
												dialog.getPopulation());
//		pop.setNEATActivationFunction(network.getActivation(0));
		pop.setInitialConnectionDensity(dialog.getConnectionDensity());// not required, but speeds training
		pop.reset();
		
		//TODO Neural Architecture has to be able to implements more than the scroing methode
		NoveltySearchEA ns_ea=NoveltySearchUtil.constructNoveltySearchTrainer(
				pop, neuralArchitecture, dialog.getBehaviorLimit());
		ns_ea.setValidationMode(true);
		ns_ea.setMaxArchiveSize(dialog.getArchiveSize());
		ns_ea.setNbOfNearestNeighbor(dialog.getNbOfneighbors());
		
		progressBarArchitecture.setMinimum(0);
		
		neuralArchitecture.prepareScoring(1,0);
		
		if(dialog.isTimeoutSet()){
			progressBarArchitecture.setMaximum((int)(dialog.getTimeout()/1000)+1);
			NoveltySearchTrainer trainer=new NoveltySearchTrainer(ns_ea, dialog.getTimeout(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		else{
			progressBarArchitecture.setMaximum(dialog.getNbOfEpoch()+1);
			NoveltySearchTrainer trainer=new NoveltySearchTrainer(ns_ea, dialog.getNbOfEpoch(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		
	}
	
	
	private void trainHyperNeatArchitecture(NeuralArchitecture architecture){
		neuralArchitecture=architecture;
		
		NeatTrainingDialog dialog=new NeatTrainingDialog(shell);
		if (dialog.open() != Window.OK)return;
		
		logger.info("Start Neat Trainig of Architecture Name: "+neuralArchitecture.getName());
		logger.info("Population Size: "+dialog.getPopulation());
		
//		Only use in order to get the number of input and ouput
//		BasicNetwork network = neuralArchitecture.createNetwork();
		Substrate substrate = neuralArchitecture.createHyperNeatSubstrat();
		NEATPopulation pop= new NEATPopulation(substrate, dialog.getPopulation());
//		pop.setNEATActivationFunction(network.getActivation(0));
		pop.setActivationCycles(4);
		pop.reset();
		
		EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,neuralArchitecture);
		train.setValidationMode(true);
		
		OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
		speciation.setCompatibilityThreshold(1);
		train.setSpeciation(speciation = new OriginalNEATSpeciation());
		train.setShouldIgnoreExceptions(false);
		//train.getM
		if(train instanceof BasicEA){
			BasicEA ea =(BasicEA) train;
			System.out.println("Max Operation error: "+ea.getMaxOperationErrors());
			ea.setMaxOperationErrors(5);
//			ea.setValidationMode(true);
		}
		
		progressBarArchitecture.setMinimum(0);
		
		neuralArchitecture.prepareScoring(1,0);
		
		if(dialog.isTimeoutSet()){
			progressBarArchitecture.setMaximum((int)(dialog.getTimeout()/1000)+1);
			NeatTrainer trainer=new NeatTrainer(train, dialog.getTimeout(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		else{
			progressBarArchitecture.setMaximum(dialog.getNbOfEpoch()+1);
			NeatTrainer trainer=new NeatTrainer(train, dialog.getNbOfEpoch(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		
	}
	
	
	
	private void trainNeuralNetwork(NeuralNetwork neuralNetwork){
		neuralArchitecture=neuralNetwork.getNeuralArchitecture();
		
		TrainNeuralArchitectureDialog dialog=new TrainNeuralArchitectureDialog(shell);
		if (dialog.open() != Window.OK)return;
		
		
		selectedNetworks.clear();selectedNetworks.add(neuralNetwork);
		
		prepareAndStartTraining( dialog);
		
	}
	
	private void trainAllNeuralNetworks(NeuralArchitecture architecture){
		neuralArchitecture=architecture;
		
		TrainNeuralArchitectureDialog dialog=new TrainNeuralArchitectureDialog(shell,false);
		if (dialog.open() != Window.OK)return;
		
		selectedNetworks.clear();
		for(NeuralNetwork neuralNetwork:neuralArchitecture.getNeuralNetworks()){
			selectedNetworks.add(neuralNetwork);
		}
		
		prepareAndStartTraining( dialog);
		
	}
	
	private void prepareAndStartTraining(TrainNeuralArchitectureDialog dialog){
		MLTrain train;
		
		if(dialog.getTrainingMethod()==TrainingMethod.SIMULATED_ANNEALING && selectedNetworks.size()==1){
			train = new NeuralSimulatedAnnealing(
					selectedNetworks.get(0).getNetwork(),
					neuralArchitecture,
					dialog.getStartTemperature(),
					dialog.getStopTemperature(),
					dialog.getCycles());
		}
		else{
			train = new MLMethodGeneticAlgorithm(
				new MethodFactory(){
					
					int nbOfCall=-1;
					@Override
					public MLMethod factor() {
						nbOfCall++;
						while(nbOfCall<selectedNetworks.size()){
							BasicNetwork network = selectedNetworks.get(0).getNetwork();
							network.reset();
							return network;
						}
						
						final BasicNetwork network=  neuralArchitecture.createNetwork();
						((MLResettable)network).reset();
						return network;
						
					}
				},
				neuralArchitecture,
				dialog.getPopulation());
		}
		
		progressBarArchitecture.setMinimum(0);
		
		
		neuralArchitecture.prepareScoring(1,-1);
		
		if(dialog.isTimeoutSet()){
			progressBarArchitecture.setMaximum((int)(dialog.getTimeout()/1000)+1);
			NetworkTrainer trainer=new NetworkTrainer(train, dialog.getTimeout(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
		else{
			progressBarArchitecture.setMaximum(dialog.getNbOfEpoch()+1);
			NetworkTrainer trainer=new NetworkTrainer(train, dialog.getNbOfEpoch(),
					dialog.getNbOfBackTestingEvaluation());
			trainer.schedule();
		}
	}
	
	
	/**
	 * 5. Isolate a network  
	 */
	private void isolateNeuralNetwork(Object network){
		System.out.println(network);
		if(network instanceof NeuralNetwork){
			NeuralNetwork neuralNetwork=(NeuralNetwork) network;
			NeuralArchitecture neuralArchitecture=(NeuralArchitecture) neuralNetwork.getNeuralArchitecture();
			
//			Creation and clean the isolated architecture
			IsolatedNeuralArchitecture isolatedNeuralArchitecture=new IsolatedNeuralArchitecture(neuralArchitecture);
			isolatedNeuralArchitecture.setId(0);
			isolatedNeuralArchitecture.getNeuralNetworks().clear();
			isolatedNeuralArchitecture.setNeuralConfiguration(null);
			
//			Creation of the isolated Network
			NeuralNetwork isolatedNetwork=neuralNetwork.copy();
			isolatedNetwork.setId(0);
			isolatedNetwork.setNeuralArchitecture(isolatedNeuralArchitecture);
			isolatedNeuralArchitecture.getNeuralNetworks().add(isolatedNetwork);
			
//			Save the new isolated architecture
			neuralProvider.addIsolatedNeuralArchitecture(neuralConfiguration, isolatedNeuralArchitecture);
			
			eventBroker.post(IEventConstant.CONTRACT_NEURAL_CONFIGURATION_CHANGED, neuralConfiguration.getContract());
			
		}
		else if(network instanceof Object[]){
			Object[] genome_data=(Object[]) network;
			if(genome_data.length==5 && genome_data[0]  instanceof Genome){
				
//				Listing of the input data
				Genome genome=(Genome) genome_data[0];
				NeuralNetwork neuralNetwork=(NeuralNetwork) genome_data[3];
				NEATPopulation pop=(NEATPopulation)genome_data[4];
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) neuralNetwork.getNeuralArchitecture();
				
//				Creation and clean the isolated architecture
				IsolatedNeuralArchitecture isolatedNeuralArchitecture=new IsolatedNeuralArchitecture(neuralArchitecture);
				isolatedNeuralArchitecture.setId(0);
				isolatedNeuralArchitecture.getNeuralNetworks().clear();
				isolatedNeuralArchitecture.setNeuralConfiguration(null);
				
//				Creation of the isolated Network
				NeuralNetwork isolatedNetwork=neuralNetwork.copy();
				isolatedNetwork.setId(0);
				isolatedNetwork.setParetoPopulation(null);
				isolatedNetwork.setNeuralArchitecture(isolatedNeuralArchitecture);
				isolatedNeuralArchitecture.getNeuralNetworks().add(isolatedNetwork);
				
				
//				Creation of the isolated Population
				NEATPopulation isolatedPopulation=new NEATPopulation(pop.getInputCount(),
						pop.getOutputCount(), 1);
				if(pop instanceof NoveltySearchPopulation){
					isolatedPopulation=new NoveltySearchPopulation(pop.getInputCount(),
						pop.getOutputCount(), 1);
				}
				isolatedPopulation.reset();
				Species species=isolatedPopulation.getSpecies().get(0);
				species.getMembers().clear();
				if(pop instanceof NoveltySearchPopulation){
					species.add(new NoveltySearchGenome((NoveltySearchGenome) genome ));
				}
				else{
					species.add(pop.getGenomeFactory().factor(genome));
				}
				species.setLeader(species.getMembers().get(0));
				
				isolatedNetwork.setNEATPopulation(isolatedPopulation);
				
				
//				Save the new isolated architecture
				neuralProvider.addIsolatedNeuralArchitecture(neuralConfiguration, isolatedNeuralArchitecture);
				
				eventBroker.post(IEventConstant.CONTRACT_NEURAL_CONFIGURATION_CHANGED, neuralConfiguration.getContract());
				
			}
		}
		
	}
	
	
	
 	private void createDropAdaptor(Viewer viewer){
		//Create a context instance
		IEclipseContext localContact=EclipseContextFactory.create();
		localContact.set(Viewer.class, viewer);
		localContact.setParent(context);
		
		//////////////////////////////////
		//Create the Drop Adapter       //
		//////////////////////////////////
		dropAdapter=ContextInjectionFactory.make( NeuralConfigurationInputTreeDropAdapter.class,localContact);
	}
	
	
	@PreDestroy
	public void preDestroy() {
	}
	
	
	@Focus
	public void onFocus() {
	}
	
	
	@Persist
	public void save() {
		//Save the Input
		neuralProvider.updateNeuralInputs(neuralConfiguration);
		
		btnEdit.setEnabled(neuralConfiguration.getNeuralTrainingElements().isEmpty());
		btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
		treeInputData.setEnabled(false);
		
		dirty.setDirty(false);
		
		treeViewerInputData.refresh();
		treeViewerInputData.expandAll();
		
	}
	
	
	
	//######################################
  	//##           Trainer Job            ##
  	//######################################
	private class NetworkTrainer extends Job{
		
		
		MLTrain train;
		int nbOfEpoch=Integer.MAX_VALUE;
		BestGenomes bestGenomes=new BestGenomes();
		long timeout=Long.MAX_VALUE;
		long startedTime;
		long currentTime;
		int nbOfBackTestingEvaluation;
		
		public NetworkTrainer(MLTrain train,int nbOfEpoch, int nbOfBackTestingEvaluation) {
			super("NetworkTrainer");
			this.train=train;
			this.nbOfEpoch=nbOfEpoch;
			this.nbOfBackTestingEvaluation=nbOfBackTestingEvaluation;
			cancelTraining=false;
		}
		
		public NetworkTrainer(MLTrain train,long timeout, int nbOfBackTestingEvaluation) {
			super("NetworkTrainer");
			this.train=train;
			this.timeout=timeout;
			this.nbOfBackTestingEvaluation=nbOfBackTestingEvaluation;
			cancelTraining=false;
		}
		

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			preTraining();
			int i=0;
//			for(int i=0;i<nbOfEpoch;i++) {
			while(true){
//				Test if the max period is reached
				if(i>=nbOfEpoch)break;
				
//				Test if the timeout is reached
				if(timeout<Long.MAX_VALUE){
					currentTime=Calendar.getInstance().getTimeInMillis();
					if(currentTime-startedTime>timeout)break;
					elapseTime=(int)((currentTime-startedTime)/1000);
				}
				
//				The training is cancel!
				if(cancelTraining){
					eventBroker.post(IEventConstant.TEXT_INFO,"Training is cancel!");	
					break;
				}
				
//				Start a new iteration
				train.iteration();
				
//				Start the post processing
				postIteration();
				
				i++;
			} 
			
			
//			The trainig is finished the data will be saved
			postTraining();
			
			return Status.OK_STATUS;
		}
		
		
		private void postIteration(){
			if(train instanceof MLMethodGeneticAlgorithm){
				MLMethodGeneticAlgorithm genTrain=(MLMethodGeneticAlgorithm) train;
				List<Genome> genomes=genTrain.getGenetic().getPopulation().flatten();
				int pos=0;
				for(Genome genome:genomes){
					if(pos>nbOfBackTestingEvaluation)break;
					
//					System.out.println("Genome Score: "+genome.getScore());
					
					if(bestGenomes.containsScore(genome.getScore()))continue;
					
					pos++;
					
					BasicNetwork network=(BasicNetwork)genTrain.getGenetic().getCODEC().decode(genome);
					GenomeEvaluation g_eval=new GenomeEvaluation(genome);
					
					
					NeuralNetworkRating backTestingRating=neuralArchitecture.calculateProfitAndRiskOfBlocks(neuralConfiguration.getBackTestingBlocks(), network);
					g_eval.setBackTestingScore(backTestingRating.getScore());
					bestGenomes.addGenomeEvaluation(g_eval);
				}
			}
			
			
//			Print the current state
			String text="Epoch #" + epoch + " Score:" + train.getError()+"\n";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
//			String[] texts=bestGenomes.toString().split("\n");
//			for(int i=0;i<texts.length;i++)
			eventBroker.post(IEventConstant.TEXT_INFO,bestGenomes.toString());
			
			//TODO Calculate the expected end of training
			
			epoch++;
			updateProgressBarArchitecture();
			
		}
		
		private void preTraining(){
			String text="Training is starting!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			startedTime=Calendar.getInstance().getTimeInMillis();
			epoch = 0;
			updateProgressBarArchitecture();
		}
		
		private void postTraining(){
			train.finishTraining();
			
			String text="Training finished, best score:" + train.getError();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			text="Please wait the network will be saved...";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			neuralArchitecture.addNeuralNetwork((BasicNetwork)train.getMethod());
//			Add also the best genomes
			if(train instanceof MLMethodGeneticAlgorithm){
				MLMethodGeneticAlgorithm genTrain=(MLMethodGeneticAlgorithm) train;
				for(GenomeEvaluation g_eval:bestGenomes.getBestGenomes()){
					BasicNetwork network=(BasicNetwork)genTrain.getGenetic().getCODEC().decode(g_eval.getGenome());
					neuralArchitecture.addNeuralNetwork(network);
				}
			}
			
			neuralProvider.updateNeuralArchitecture(neuralConfiguration);
			
			
			refreshTreeArchitecture();
			
			epoch++;
			updateProgressBarArchitecture();
			
			epoch=-1;
			elapseTime=0;
			
			text="Data are now saved!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
		}
		
		
	}
	
	private class NeatTrainer extends Job{
		
		EvolutionaryAlgorithm train;
		int nbOfEpoch=Integer.MAX_VALUE;
		
		BestGenomes bestGenomes=new BestGenomes();
		int nbOfBackTestingEvaluation;
		long timeout=Long.MAX_VALUE;
		long startedTime;
		long currentTime;
		
		
		

		public NeatTrainer(EvolutionaryAlgorithm train, int nbOfEpoch, int nbOfBackTestingEvaluation) {
			super("Neat Trainer");
			this.train=train;
			this.nbOfEpoch=nbOfEpoch;
			
			this.nbOfBackTestingEvaluation=nbOfBackTestingEvaluation;
			cancelTraining=false;
		}
		
		public NeatTrainer(EvolutionaryAlgorithm train,
				long timeout, int nbOfBackTestingEvaluation) {
			super("Novelty Search Trainer");
			this.train=train;
			this.timeout=timeout;
//			this.pop=pop;
			this.nbOfBackTestingEvaluation=nbOfBackTestingEvaluation;
			cancelTraining=false;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			preTraining();
			
			System.out.println("Timeout: "+BarUtils.format(timeout));
			
			int i=0;
//			for(int i=0;i<nbOfEpoch;i++) {
			while(true){
//				Test if the max period is reached
				if(i>=nbOfEpoch)break;
				
//				Test if the timeout is reached
				if(timeout<Long.MAX_VALUE){
					currentTime=Calendar.getInstance().getTimeInMillis();
					if(currentTime-startedTime>timeout)break;
					elapseTime=(int)((currentTime-startedTime)/1000);
				}
				
//				The training is cancel!
				if(cancelTraining){
					eventBroker.post(IEventConstant.TEXT_INFO,"Training is cancel!");	
					break;
				}
				
//				Start a new iteration
				train.iteration();
				
//				Start the post processing
				postIteration();
				
				i++;
			} 
			
			postTraining();
			
			return Status.OK_STATUS;
			
		}
		
		
		
		private void postIteration(){

//			Isolate the best genomes
			List<Genome> sortedGenomes=getSortedNoveltyGenomesFromPop(train.getPopulation());
			
			ExecutorService taskExecutor =Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			
			int pos=0;
			for(Genome genome:sortedGenomes){
				if(pos>nbOfBackTestingEvaluation)break;
				
//				System.out.println("Genome Score: "+genome.getScore());
				
				if(bestGenomes.containsScore(genome.getScore()))continue;
				
				taskExecutor.execute(new ParallelBackTestingEvalTask(train, bestGenomes, genome,
						neuralConfiguration.getBackTestingBlocks(), neuralArchitecture));
				
				
//				Calculate the back testing score
//				MLMethod method=train.getCODEC().decode(genome);
//				NeuralNetworkRating backTestingRating=neuralArchitecture.calculateProfitAndRiskOfBlocks(neuralConfiguration.getBackTestingBlocks(), method);
				
//				Create and add the new evaluation
//				GenomeEvaluation g_eval=new GenomeEvaluation(genome, genome.getScore());
//				g_eval.setBackTestingScore(backTestingRating.getScore());
//				bestGenomes.addGenomeEvaluation(g_eval);
				
				pos++;
			}
			
			taskExecutor.shutdown();
			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new GeneticError(e);
			}
			
			
			String text="Epoch #" + epoch + " Score:" + train.getError()+ ", Species:" + train.getPopulation().getSpecies().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			eventBroker.post(IEventConstant.TEXT_INFO,bestGenomes.toString());
			
			epoch++;
			updateProgressBarArchitecture();
		}
		
		private void preTraining(){
			String text="Training is starting!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			startedTime=Calendar.getInstance().getTimeInMillis();
			epoch = 0;
			updateProgressBarArchitecture();
		}
		
		private void postTraining(){
			train.finishTraining();
			
			String text="Training finished, best score:" + train.getError();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			text="Please wait the network will be saved...";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			NEATPopulation pop=(NEATPopulation)train.getPopulation();
			pop.setName(NoveltySearchPopulation.MAIN);
			
			text="Population size before reduction: "+pop.flatten().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			reducePopulationSize(100);
			
			NEATPopulation paretoPopulation=bestGenomesToParetoPopulation();
			paretoPopulation.setName(NoveltySearchPopulation.PARETO);
			
			text="Population size after reduction: "+pop.flatten().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			text="Pareto population: "+paretoPopulation.flatten().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
//			Add a new Neural Network to the architecture with the pareto front
			neuralArchitecture.addNeuralNetwork(train.getPopulation(),NoveltySearchPopulation.MAIN,
					paretoPopulation,NoveltySearchPopulation.PARETO);
			neuralProvider.updateNeuralArchitecture(neuralConfiguration);
//			
			
			refreshTreeArchitecture();
			
			epoch++;
			updateProgressBarArchitecture();
			
			epoch=-1;
			
			text="Data are now saved!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
		}
		
		private NEATPopulation bestGenomesToParetoPopulation(){
			NEATPopulation pop=(NEATPopulation)train.getPopulation();
			
			NEATPopulation paretoPop=new NEATPopulation(pop.getInputCount(),
					pop.getOutputCount(), bestGenomes.size());
			paretoPop.reset();
			Species species=paretoPop.getSpecies().get(0);
			species.getMembers().clear();
			for(GenomeEvaluation g_eval:bestGenomes){
				species.add(pop.getGenomeFactory().factor(g_eval.getGenome()));
				
			}
			species.setLeader(species.getMembers().get(0));
			
			return paretoPop;
		}
		
		
		private void reducePopulationSize( int maxPopSize){

//			Reduce the size of the population, keep only the 100 best genomes and the 100 best archives
			LinkedList<Genome> toKeep=new LinkedList<Genome>();
			NEATPopulation pop=(NEATPopulation)train.getPopulation();
			toKeep.add(pop.getBestGenome());
			for(Species species:pop.getSpecies()){
				for(Genome genome:species.getMembers()){
					int i=0;
					boolean isInserted=false;
					for(Genome k_genome:toKeep){
						if(genome.getScore()>k_genome.getScore()){
							toKeep.add(i, genome);
							if(toKeep.size()>maxPopSize){
								toKeep.removeLast();
							}
							isInserted=true;
							break;
						}
						i++;
					}
					
					if(!isInserted && toKeep.size()<maxPopSize){
						toKeep.add(genome);
					}
					
				}
			}
			
//			Remove the Genomes
			for(Species species:pop.getSpecies()){
				boolean noGenomeToRemove=true;
				while(noGenomeToRemove){
					noGenomeToRemove=false;
					for(Genome genome:species.getMembers()){
						if(!toKeep.contains(genome)){
							species.getMembers().remove(genome);
							noGenomeToRemove=true;
							break;
						}
					}
					if(species.getMembers().isEmpty())
						break;
					
				}
			}
			
//			Remove the empty Species
			boolean noSpeciesToRemove=true;
			while(noSpeciesToRemove){
				noSpeciesToRemove=false;
				for(Species species:pop.getSpecies()){
					if(species.getMembers().isEmpty()){
						pop.getSpecies().remove(species);
						noSpeciesToRemove=true;
						break;
					}
				}
			}
			
			
			
		}
		
		
	}
	
	
	private class NoveltySearchTrainer extends Job{
		
		NoveltySearchEA train;
//		NoveltySearchPopulation pop;
		
		int nbOfEpoch=Integer.MAX_VALUE;
		BestGenomes bestGenomes=new BestGenomes();
		int nbOfBackTestingEvaluation;
		long timeout=Long.MAX_VALUE;
		long startedTime;
		long currentTime;
		

		public NoveltySearchTrainer(NoveltySearchEA train,
				int nbOfEpoch, int nbOfBackTestingEvaluation) {
			super("Novelty Search Trainer");
			this.train=train;
			this.nbOfEpoch=nbOfEpoch;
//			this.pop=pop;
			this.nbOfBackTestingEvaluation=nbOfBackTestingEvaluation;
			cancelTraining=false;
		}
		
		public NoveltySearchTrainer(NoveltySearchEA train,
				long timeout, int nbOfBackTestingEvaluation) {
			super("Novelty Search Trainer");
			this.train=train;
			this.timeout=timeout;
//			this.pop=pop;
			this.nbOfBackTestingEvaluation=nbOfBackTestingEvaluation;
			cancelTraining=false;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			preTraining();
			
			int i=0;
//			for(int i=0;i<nbOfEpoch;i++) {
			while(true){
//				Test if the max period is reached
				if(i>=nbOfEpoch)break;
				
//				Test if the timeout is reached
				if(timeout<Long.MAX_VALUE){
					currentTime=Calendar.getInstance().getTimeInMillis();
					if(currentTime-startedTime>timeout)break;
					elapseTime=(int)((currentTime-startedTime)/1000);
				}
				
//				The training is cancel!
				if(cancelTraining){
					eventBroker.post(IEventConstant.TEXT_INFO,"Training is cancel!");	
					break;
				}
				
//				Start a new iteration
				train.iteration();
				
//				Start the post processing
				postIteration();
				
				i++;
			} 
			
			postTraining();
			
			return Status.OK_STATUS;
		}
		
		private void postIteration(){

//			Isolate the best genomes
			List<Genome> sortedGenomes=getSortedNoveltyGenomesFromPop(train.getPopulation());
			
			ExecutorService taskExecutor =Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			
			int pos=0;
			for(Genome genome:sortedGenomes){
				if(pos>nbOfBackTestingEvaluation)break;
				
//				System.out.println("Genome Score: "+genome.getScore());
				
				if(bestGenomes.containsScore(genome.getScore()))continue;
				
				taskExecutor.execute(new ParallelBackTestingEvalTask(train, bestGenomes, genome,
						neuralConfiguration.getBackTestingBlocks(), neuralArchitecture));
				
				
//				Calculate the back testing score
//				MLMethod method=train.getCODEC().decode(genome);
//				NeuralNetworkRating backTestingRating=neuralArchitecture.calculateProfitAndRiskOfBlocks(neuralConfiguration.getBackTestingBlocks(), method);
				
//				Create and add the new evaluation
//				GenomeEvaluation g_eval=new GenomeEvaluation(genome, ((NoveltySearchGenome)genome).getBehavior());
//				g_eval.setBackTestingScore(backTestingRating.getScore());
//				bestGenomes.addGenomeEvaluation(g_eval);
				
				pos++;
			}
			
			
			taskExecutor.shutdown();
			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new GeneticError(e);
			}
			
			
			String text="Epoch #" + epoch + " Score:" + train.getError()+ ", Species:" + train.getPopulation().getSpecies().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			eventBroker.post(IEventConstant.TEXT_INFO,bestGenomes.toString());
			
			epoch++;
			updateProgressBarArchitecture();
		}
		
		private void preTraining(){
			String text="Training is starting!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			startedTime=Calendar.getInstance().getTimeInMillis();
			epoch = 0;
			updateProgressBarArchitecture();
		}
		
		private void postTraining(){
			train.finishTraining();
			
			String text="Training finished, best score:" + train.getError();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			text="Please wait the network will be saved...";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			NoveltySearchPopulation pop=(NoveltySearchPopulation)train.getPopulation();
			pop.setName(NoveltySearchPopulation.MAIN);
			
			text="Population size before reduction: "+pop.flatten().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			text="Archive size before reduction: "+pop.getArchive().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			reducePopulationSize(100);
			
			NoveltySearchPopulation paretoPopulation=bestGenomesToParetoPopulation();
			paretoPopulation.setName(NoveltySearchPopulation.PARETO);
			
			text="Population size after reduction: "+pop.flatten().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			text="Archive size after reduction: "+pop.getArchive().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			text="Pareto population: "+paretoPopulation.flatten().size();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
//			Add a new Neural Network to the architecture with the pareto front
			neuralArchitecture.addNeuralNetwork(train.getPopulation(),NoveltySearchPopulation.MAIN,
					paretoPopulation,NoveltySearchPopulation.PARETO);
			neuralProvider.updateNeuralArchitecture(neuralConfiguration);
//			
			
			refreshTreeArchitecture();
			
			epoch++;
			updateProgressBarArchitecture();
			
			epoch=-1;
			
			text="Data are now saved!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
		}
		
		private NoveltySearchPopulation bestGenomesToParetoPopulation(){
			NoveltySearchPopulation pop=(NoveltySearchPopulation)train.getPopulation();
			
			NoveltySearchPopulation paretoPop=new NoveltySearchPopulation(pop.getInputCount(),
					pop.getOutputCount(), bestGenomes.size());
			paretoPop.reset();
			Species species=paretoPop.getSpecies().get(0);
			species.getMembers().clear();
			for(GenomeEvaluation g_eval:bestGenomes){
//				species.add(NoveltySearchGenomeFactory.g_eval.getGenome());
				
				species.add(new NoveltySearchGenome((NoveltySearchGenome)g_eval.getGenome()));
				
			}
			species.setLeader(species.getMembers().get(0));
//			getSpecies().add(species);
			
			return paretoPop;
		}
		
		
		private void reducePopulationSize( int maxPopSize){

//			Reduce the size of the population, keep only the 100 best genomes and the 100 best archives
			LinkedList<Genome> toKeep=new LinkedList<Genome>();
			NoveltySearchPopulation pop=(NoveltySearchPopulation)train.getPopulation();
			toKeep.add(pop.getBestGenome());
			for(Species species:pop.getSpecies()){
				for(Genome genome:species.getMembers()){
					int i=0;
					boolean isInserted=false;
					for(Genome k_genome:toKeep){
						if(genome.getScore()>k_genome.getScore()){
							toKeep.add(i, genome);
							if(toKeep.size()>maxPopSize){
								toKeep.removeLast();
							}
							isInserted=true;
							break;
						}
						i++;
					}
					
					if(!isInserted && toKeep.size()<maxPopSize){
						toKeep.add(genome);
					}
					
				}
			}
			
//			Remove the Genomes
			for(Species species:pop.getSpecies()){
				boolean noGenomeToRemove=true;
				while(noGenomeToRemove){
					noGenomeToRemove=false;
					for(Genome genome:species.getMembers()){
						if(!toKeep.contains(genome)){
							species.getMembers().remove(genome);
							noGenomeToRemove=true;
							break;
						}
					}
					if(species.getMembers().isEmpty())
						break;
					
				}
			}
			
//			Remove the empty Species
			boolean noSpeciesToRemove=true;
			while(noSpeciesToRemove){
				noSpeciesToRemove=false;
				for(Species species:pop.getSpecies()){
					if(species.getMembers().isEmpty()){
						pop.getSpecies().remove(species);
						noSpeciesToRemove=true;
						break;
					}
				}
			}
			
//			Reduce the size of the archive
			while(pop.getArchive().size()>maxPopSize){
				pop.getArchive().removeLast();
			}
			
			
		}
		
		
	}
	
	
	private List<Genome> getSortedNoveltyGenomesFromPop(Population pop){
		List<Genome> genomes=pop.flatten();
		
		
		List<Genome> sortedGenomes=new LinkedList<Genome>();
		for(Genome genome:genomes){
//			System.out.println(genome.getScore());
			
			NoveltySearchGenome nov_gen=(NoveltySearchGenome) genome;
			
			if(sortedGenomes.isEmpty()){
				sortedGenomes.add(genome);continue;
			}
			
			int i=0;
			boolean isInserted=false;
			for(Genome sortedGenome:sortedGenomes){
				NoveltySearchGenome sorted_nov_gen=(NoveltySearchGenome) sortedGenome;
				
				
				if(sorted_nov_gen.getBehavior() < nov_gen.getBehavior()){
					sortedGenomes.add(i, genome);
					isInserted=true;
					break;
				}
				i++;
			}
			
			if(!isInserted){
				sortedGenomes.add(genome);
			}
			
			
		}
		
		return sortedGenomes;
	}
	
	private void updateProgressBarArchitecture(){
		Display.getDefault().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
				if(elapseTime>0){
					progressBarArchitecture.setSelection(elapseTime);
				}
				else{
					progressBarArchitecture.setSelection(epoch);
				}
			}
		}
		);
				
	}
	
	private void refreshTreeArchitecture(){
		Display.getDefault().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
			treeViewerArchitecture.refresh();
			}
		}
		);
				
	}
	
	
	//######################################
  	//##   Search And Distribute Job      ##
  	//######################################
	
	private class SearchAndDistribute extends Job{

		public SearchAndDistribute() {
			super("Search and distribute");
			readGuiData();
		}
		
		private void readGuiData(){
//			Save the bar Size
			neuralConfiguration.setSize(BarUtils.getBarSizeFromString(comboBarSize
					.getText()));
			
//			Save the reference data
			if (comboReferenceData.getSelectionIndex() == 0) {
				neuralConfiguration.setReferenceData(ReferenceData.MID_POINT);
			} else if (comboReferenceData.getSelectionIndex() == 1) {
				neuralConfiguration.setReferenceData(ReferenceData.BID_AND_ASK);
			}
			
//			Clear the tree
			removeAllInputsColumns();
			
//			Save the percent of training data
			neuralConfiguration.setPercentOfTrainingData(spinnerPercentOfTrainingData.getSelection());
			
//			Save the split strategy
			if (comboSplitStrategy.getSelectionIndex() == 0) {
				neuralConfiguration.setSplitStrategy(SplitStrategy.WEEK);
			} else if (comboSplitStrategy.getSelectionIndex() == 1) {
				neuralConfiguration.setSplitStrategy(SplitStrategy.DAY);
			}
			
			progressBarDataSet.setMaximum(8);
			progressBarDataSet.setSelection(0);
			dataSetCounter=0;
			
		}
		

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			searchReferenceDataFunc();
			
			distributeDataFunc();
			
			return Status.OK_STATUS;
		}
		
		/**
		 * 2. Search the reference data
		 */
		private void searchReferenceDataFunc(){
			

//			Search the reference data
			neuralConfiguration.clearAllCollectedBars();
			updateProgressBarDataSet();
			
			List<BarContainer> containers = historicalDataProvider
					.getAllBarContainers(neuralConfiguration.getContract());
			
			for (BarContainer container : containers) {

				// Collect the mid point data
				if (/*neuralConfiguration.getReferenceData() == ReferenceData.MID_POINT
						&& */container.getType() == WhatToShow.MIDPOINT) {
					List<ExBar> allBars = historicalDataProvider.getAllTimeBars(
							container, neuralConfiguration.getSize());
					neuralConfiguration.setAllMidPointBars(allBars);
				}

				// Collect the Ask Data
				if (/*neuralConfiguration.getReferenceData() == ReferenceData.BID_AND_ASK
						&& */container.getType() == WhatToShow.ASK) {
					List<ExBar> allBars = historicalDataProvider.getAllTimeBars(
							container, neuralConfiguration.getSize());
					neuralConfiguration.setAllAskBars(allBars);
				}

				// Collect the Bid Data
				if (/*neuralConfiguration.getReferenceData() == ReferenceData.BID_AND_ASK
						&& */container.getType() == WhatToShow.BID) {
					List<ExBar> allBars = historicalDataProvider.getAllTimeBars(
							container, neuralConfiguration.getSize());
					neuralConfiguration.setAllBidBars(allBars);
				}

			}
			
			updateProgressBarDataSet();
			refreshNbOfDataText();
			
		}
		
		
		/**
		 * 3. Split the data and distribute them 
		 */
		private void distributeDataFunc(){
			printMemoryUsage("Start distribute");
			
//			Synchronize the received Data
			neuralConfiguration.synchronizedReceivedBars();
			printMemoryUsage("Synchronized the received data!");
			
//			Split the data
			neuralConfiguration.splitReferenceData();
			updateProgressBarDataSet();
			printMemoryUsage("Split the data");
			
//			Fill the neural input bar collector
			fillTheNeuralInputsBarsCollector();
			updateProgressBarDataSet();
			printMemoryUsage("Fill the neural input bar collector");
			
//			Compute the neural indicator values and reset the ranges of the components
			neuralConfiguration.computeAllNeuralIndicatorInputs();
			updateProgressBarDataSet();
			printMemoryUsage("Compute the neural indicator values and reset the ranges of the components");
			
//			In order to save memory clear the data collector
			neuralConfiguration.getNeuralInputsBarsCollector().clear();
			updateProgressBarDataSet();
			System.gc();
			printMemoryUsage("In order to save memory clear the data collector");
			
//			Compute the adapted Data of each components
			neuralConfiguration.computeAdaptedDataOfEachComponents();
			updateProgressBarDataSet();
			printMemoryUsage("Compute the adapted Data of each components");
			
//			save the configuration
			neuralProvider.updateTrainingData(neuralConfiguration.getId(),
					neuralConfiguration.getNeuralTrainingElements());
			updateProgressBarDataSet();
			
			printMemoryUsage("End distribute");
			System.gc();
			printMemoryUsage("After garbage collector");
			
		}
		
		private void printMemoryUsage(String message){
			long total = Runtime.getRuntime().totalMemory();
			long used  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println(message+", total="+total+ ", used="+used);
			
		}
		
		
			
	}
	
	
	private void refreshNbOfDataText(){
		Display.getDefault().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
				
				textNbOfData.setText(String.valueOf((neuralConfiguration
						.getAllAskBars().size() + neuralConfiguration
						.getAllBidBars().size()+ neuralConfiguration.getAllMidPointBars().size()) / 3));
				
//				switch (neuralConfiguration.getReferenceData()) {
//				case MID_POINT:
//					textNbOfData.setText(String.valueOf(neuralConfiguration
//							.getAllMidPointBars().size()));
//					break;
//				case BID_AND_ASK:
//					textNbOfData.setText(String.valueOf((neuralConfiguration
//							.getAllAskBars().size() + neuralConfiguration
//							.getAllBidBars().size()) / 2));
//					break;
//				}
			}
		}
		);

	}
	
	
	private void updateProgressBarDataSet(){
		Display.getDefault().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
				dataSetCounter++;
				progressBarDataSet.setSelection(dataSetCounter);
				if(dataSetCounter==8)
					btnViewTrainingData.setEnabled(true);
			}
		}
		);
				
	}
	
	
	

	//##################################
	//##     Column Label Provider    ##
	//##################################
	
//	Name
	class InputDataNameLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralInput || element instanceof NeuralIndicatorInput){
				NeuralInput ni=(NeuralInput) element;
				return String.valueOf(ni.getName());
			}
			else if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return nic.getComponentType().toString();
			}
			return super.getText(element);
		}
	}
	
//	Source
	class InputDataSourceLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralIndicatorInput){
				NeuralIndicatorInput nii=(NeuralIndicatorInput) element;
				return String.valueOf(nii.getContract().getSymbol());
			}
			
			return "";
		}
	}	
	
//	Period
	class InputDataPeriodLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralIndicatorInput){
				NeuralIndicatorInput nii=(NeuralIndicatorInput) element;
				return String.valueOf(nii.getSize().toString());
			}
			else if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				if(nic.getComponentType()!=ComponentType.DIRECT){
					return String.valueOf(nic.getPeriod());
				}
				else{
					return "-";
				}
			}
			
			return "";
		}
	}
	
//	Offset
	class InputDataOffsetLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return String.valueOf(nic.getOffset());
			}
			
			return "";
		}
	}
	
//	Type
	class InputDataTypeLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralIndicatorInput){
				NeuralIndicatorInput nii=(NeuralIndicatorInput) element;
				return nii.getBarContainer().getType().toString();
			}
			else if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return nic.getComponentType().toString();
			}
			
			return "";
		}
	}
	
//	Min
	class InputDataMinLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return String.valueOf(nic.getLowerRange());
			}
			
			return "";
		}
	}
	
//	Max
	class InputDataMaxLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return String.valueOf(nic.getUpperRange());
			}
			
			return "";
		}
	}	
	
	
//	Neural Training Data name
	class NeuralTrainingDataLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof ExBar){
				ExBar bar=(ExBar) element;
				return BarUtils.format(bar.getTimeInMs());
			}
			else if(element instanceof List<?>){
				List<?> list=(List<?>) element;
				String suffix="";
				
				if(neuralConfiguration.getTrainingBlocks().contains(list))
					suffix=" [*]";
				
				ExBar bar=(ExBar)list.get(0);
				Calendar day=BarUtils.getCurrentDayOf(bar.getTimeInMs());
				return BarUtils.format(day.getTimeInMillis())+suffix;

//				switch (neuralConfiguration.getSplitStrategy()) {
//				case WEEK:
//					Calendar sunday=IbBar.getLastSundayOfDate(bar.getTimeInMs());
//					return IbBar.format(sunday.getTimeInMillis())+suffix;
//					
//				case DAY:
//					Calendar day=IbBar.getCurrentDayOf(bar.getTimeInMs());
//					return IbBar.format(day.getTimeInMillis())+suffix;
//				}
			}
			
			
			return "";
		}
	}
	
//	NeuralInputComponent
	class NeuralInputComponentLabelProvider extends ColumnLabelProvider{
		
		private NeuralInputComponent component;
		
		public NeuralInputComponentLabelProvider(NeuralInputComponent component){
			this.component=component;
		}
		
		@Override
		public String getText(Object element) {
			if(neuralConfiguration.getAdpatedTimesMap().isEmpty())return "";
			
			if(element instanceof ExBar){
				ExBar bar=(ExBar) element;
				long time=bar.getTimeInMs();
				if(neuralConfiguration.getAdpatedTimesMap()==null)
					return "";
				if(!neuralConfiguration.getAdpatedTimesMap().containsKey(time))
					return "No Data";
				int daptedValueIndex=neuralConfiguration.getAdpatedTimesMap().get(time);
				return String.valueOf(component.getAdaptedValues()[daptedValueIndex]);
			}
			
			
			return "";
		}
		
	}
	
	
//	Neural Architecture Id
	class NeuralArchitectureIdLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralArchitecture){
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
				return String.valueOf(neuralArchitecture.getId());
			}
			else if(element instanceof NeuralNetwork){
				NeuralNetwork neuralNetwork=(NeuralNetwork) element;
				return String.valueOf(neuralNetwork.getId());
			}
			else if(element instanceof Object[]){
				Object[] objects =(Object[]) element;
//				System.out.println("Object[0]: "+objects[0].getClass().getSimpleName());
//				System.out.println("Object[1]: "+objects[1].getClass().getSimpleName());
				if(objects.length==2 && objects[1] instanceof NEATPopulation ){
					NEATPopulation pop=(NEATPopulation) objects[1];
//					System.out.println("Name: "+pop.getName());
					return pop.getName();
				}
				else if(objects.length==5 && objects[0] instanceof Genome){
					Genome genome=(Genome) objects[0];
					return "Genome: "+String.valueOf(genome.getBirthGeneration());
					
				}
			}
			else if(element instanceof NeuralNetworkRating){
				NeuralNetworkRating rating=(NeuralNetworkRating)element;
				if(rating.getChildren().isEmpty()){
					return BarUtils.format(rating.getId());
				}
				else{
					return rating.getName();
				}
			}
			
			return "";
		}
	}
	
//	Neural Architecture Name
	class NeuralArchitectureNameLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralArchitecture){
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
				return String.valueOf(neuralArchitecture.getName());
			}
			else if(element instanceof NeuralNetwork){
				NeuralNetwork neuralNetwork=(NeuralNetwork) element;
				return String.format ("%.2f",neuralNetwork.getTrainingRating().getScore());
			}
			else if(element instanceof NeuralNetworkRating){
				NeuralNetworkRating rating=(NeuralNetworkRating)element;
				return String.format ("%.2f",rating.getScore());
				
			}
			else if(element instanceof Object[]){
				Object[] objects =(Object[]) element;
				if(objects.length==5 && objects[0] instanceof Genome){
					Genome genome=(Genome) objects[0];
					NeuralNetworkRating training=(NeuralNetworkRating) objects[1];
					NeuralNetworkRating b_testing=(NeuralNetworkRating) objects[2];
					return  String.format ("%.2f",training.getScore())+"/"+
					String.format ("%.2f",b_testing.getScore());
					
				}
			}
			
			return "";
		}
	}
	
//	Neural Architecture Volume
	class NeuralArchitectureVolumeLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralArchitecture){
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
				return String.valueOf(neuralArchitecture.getVolume());
			}
			else if(element instanceof NeuralNetwork){
				NeuralNetwork neuralNetwork=(NeuralNetwork) element;
				return String.format ("%.2f",neuralNetwork.getTrainingRating().getProfit());
			}
			else if(element instanceof NeuralNetworkRating){
				NeuralNetworkRating rating=(NeuralNetworkRating)element;
				return String.format ("%.2f",rating.getProfit());
			}
			
			return "";
		}
	}
	
//	Neural Architecture Type
	class NeuralArchitectureTypeLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralArchitecture){
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
				return neuralArchitecture.getType().toString();
			}
			else if(element instanceof NeuralNetwork){
				NeuralNetwork neuralNetwork=(NeuralNetwork) element;
				return String.format ("%.2f",neuralNetwork.getTrainingRating().getRisk());
			}
			else if(element instanceof NeuralNetworkRating){
				NeuralNetworkRating rating=(NeuralNetworkRating)element;
				if(rating.getChildren().isEmpty()){
					return String.format ("%.2f",rating.getMaxRisk());
				}
				else{
					return String.format ("%.2f",rating.getRisk());
				}
			}
			
			return "";
		}
	}
	
//	Neural Architecture Layer Description
	class NeuralArchitectureLayerDescriptionLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralArchitecture){
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
				return neuralArchitecture.getHiddenLayerDescription();
			}
			else if(element instanceof NeuralNetwork){
				NeuralNetwork neuralNetwork=(NeuralNetwork) element;
				return String.format ("%.2f",neuralNetwork.getBackTestingRating().getProfit());
			}
			
			return "";
		}
	}
	
//	Neural Architecture Activation Function
	class NeuralArchitectureActivationFunctionLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof NeuralArchitecture){
				NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
				return neuralArchitecture.getActivation().toString();
			}
			else if(element instanceof NeuralNetwork){
				NeuralNetwork neuralNetwork=(NeuralNetwork) element;
				return String.format ("%.2f",neuralNetwork.getBackTestingRating().getRisk());
			}
			
			return "";
		}
	}
	
	
	//###################################
	//##     Column Editing Support    ##
	//###################################
	
//	Type
	class InputDataTypeEditingSupport extends EditingSupport{
		
		TreeViewer viewer;
		String[] values;

		public InputDataTypeEditingSupport(TreeViewer viewer) {
			super(viewer);
			this.viewer=viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			String[] values = { ComponentType.DIRECT.toString(),
							ComponentType.DIFF.toString(),
							ComponentType.MEAN.toString()};
			this.values=values;
			return new ComboBoxCellEditor(viewer.getTree(), values);
		}

		@Override
		protected boolean canEdit(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent component=(NeuralInputComponent) element;
				return component.getNeuralInput() instanceof NeuralIndicatorInput;
			}
			
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				for(int i=0;i<this.values.length;i++){
					if(values[i].equals(nic.getComponentType().toString()))
						return i;
				}
			}
			return 0;
		}

		@Override
		protected void setValue(Object element, Object value) {
			NeuralInputComponent nic=(NeuralInputComponent) element;
			String typeStr=values[(int) value];
			if(typeStr.equals(ComponentType.DIRECT.toString()) &&
					nic.getComponentType()!=ComponentType.DIRECT){
				nic.setComponentType(ComponentType.DIRECT);
				nic.setUpperRange(0);
				nic.setLowerRange(0);
				dirty.setDirty(true);
			}
			else if(typeStr.equals(ComponentType.DIFF.toString()) &&
					nic.getComponentType()!=ComponentType.DIFF){
				nic.setComponentType(ComponentType.DIFF);
				nic.setUpperRange(0);
				nic.setLowerRange(0);
				dirty.setDirty(true);
			}
			else if(typeStr.equals(ComponentType.MEAN.toString()) &&
					nic.getComponentType()!=ComponentType.MEAN){
				nic.setComponentType(ComponentType.MEAN);
				nic.setUpperRange(0);
				nic.setLowerRange(0);
				dirty.setDirty(true);
			}
			
			viewer.refresh();
			
		}
		
	}
	
//	Period
	class InputDataPeriodEditingSupport extends EditingSupport{
		
		TreeViewer viewer;

		public InputDataPeriodEditingSupport(TreeViewer viewer) {
			super(viewer);
			this.viewer=viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(viewer.getTree(), SWT.NONE);
		}

		@Override
		protected boolean canEdit(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent component=(NeuralInputComponent) element;
				return component.getComponentType()!=ComponentType.DIRECT && component.getNeuralInput() instanceof NeuralIndicatorInput;
			}
			
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return String.valueOf(nic.getPeriod());
			}
			return "0";
		}

		@Override
		protected void setValue(Object element, Object value) {
			NeuralInputComponent nic=(NeuralInputComponent) element;
			try {
				int period=Integer.valueOf((String)value);
				if(nic.getPeriod()!=period){
					nic.setPeriod(period);
					nic.setUpperRange(0);
					nic.setLowerRange(0);
					dirty.setDirty(true);
					viewer.refresh();
				}
			} catch (Exception e) {	}
		}
		
	}
	
//	Offset
	class InputDataOffsetEditingSupport extends EditingSupport{
		
		TreeViewer viewer;

		public InputDataOffsetEditingSupport(TreeViewer viewer) {
			super(viewer);
			this.viewer=viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(viewer.getTree(), SWT.NONE);
		}

		@Override
		protected boolean canEdit(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent component=(NeuralInputComponent) element;
				return component.getNeuralInput() instanceof NeuralIndicatorInput;
			}
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof NeuralInputComponent){
				NeuralInputComponent nic=(NeuralInputComponent) element;
				return String.valueOf(nic.getOffset());
			}
			return "0";
		}

		@Override
		protected void setValue(Object element, Object value) {
			NeuralInputComponent nic=(NeuralInputComponent) element;
			try {
				int offset=Integer.valueOf((String)value);
				if(nic.getOffset()!=offset){
					nic.setOffset(offset);
					nic.setUpperRange(0);
					nic.setLowerRange(0);
					dirty.setDirty(true);
					viewer.refresh();
				}
			} catch (Exception e) {	}
		}
		
	}
}