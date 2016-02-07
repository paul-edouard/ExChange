 
package com.munch.exchange.parts.neural;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

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
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.AddNeuralArchitectureDialog;
import com.munch.exchange.dialog.TrainNeuralArchitectureDialog;
import com.munch.exchange.model.core.ib.IbContract.TradingPeriod;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
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
	private static int dataSetCounter=-1;
	private MenuItem mntmEvaluateArchitecture;
	
	
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
		for(String bSize:IbBar.getAllBarSizesAsString())
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
				
				if(e.button==3 && treeArchitecture.getSelection().length==1){
					
					TreeItem item=treeArchitecture.getSelection()[0];
					if(item.getData() instanceof NeuralArchitecture){
						mntmDeleteArchitecture.setEnabled(true);
						mntmTrainArchitecture.setEnabled(epoch<0);
					}
					else if(item.getData() instanceof NeuralNetwork){
						mntmDeleteArchitecture.setEnabled(true);
						mntmTrainArchitecture.setEnabled(epoch<0);
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
					neuralConfiguration.getNeuralArchitectures().add(dialog.getNeuralArchitecture());
					dialog.getNeuralArchitecture().setNeuralConfiguration(neuralConfiguration);
					neuralProvider.updateNeuralArchitecture(neuralConfiguration);
					
					logger.info("Nb of Architectures: "+neuralConfiguration.getNeuralArchitectures().size());
					
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
						trainArchitecture((NeuralArchitecture) item.getData());
						
					}
					else if(item.getData() instanceof NeuralNetwork){
						trainNeuralNetork((NeuralNetwork)item.getData() );
					}
					//trainNeuralNetork(NeuralNetwork network)
				}
			}
		});
		mntmTrainArchitecture.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/lrun_obj.gif"));
		mntmTrainArchitecture.setText("Train");
		
		mntmEvaluateArchitecture = new MenuItem(menuArchitecture, SWT.NONE);
		mntmEvaluateArchitecture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(NeuralArchitecture architecture:neuralConfiguration.getNeuralArchitectures()){
					architecture.evaluateProfitAndRiskOfAllNetworks();
				}
				treeViewerArchitecture.refresh();
				treeViewerArchitecture.expandAll();
			}
		});
		mntmEvaluateArchitecture.setText("Evaluate");
		
		new MenuItem(menuArchitecture, SWT.SEPARATOR);
		
		mntmDeleteArchitecture = new MenuItem(menuArchitecture, SWT.NONE);
		mntmDeleteArchitecture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if( treeArchitecture.getSelection().length==1){
					
					TreeItem item=treeArchitecture.getSelection()[0];
					if(item.getData() instanceof NeuralArchitecture){
						NeuralArchitecture architecture=(NeuralArchitecture) item.getData();
						neuralConfiguration.getNeuralArchitectures().remove(architecture);
						neuralProvider.updateNeuralArchitecture(neuralConfiguration);
						treeViewerArchitecture.refresh();
						treeViewerArchitecture.expandAll();
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
		compositeArchitectureBottom.setLayout(new GridLayout(1, false));
		compositeArchitectureBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		progressBarArchitecture = new ProgressBar(compositeArchitectureBottom, SWT.NONE);
		progressBarArchitecture.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBarArchitecture.setBounds(0, 0, 260, 26);
		
		
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
				
				List<IbBar> bars=historicalDataProvider.getAllBars(nii.getBarContainer(), nii.getSize());
					
				neuralConfiguration.getNeuralInputsBarsCollector().put(key, bars);

			}
		}
		
	}
	
	
	/**
	 * 4. Train a architecture 
	 */
	private void trainArchitecture(NeuralArchitecture architecture){
		neuralArchitecture=architecture;
		
		
//		logger.info("Train Architecture: "+neuralArchitecture.getName());
		TrainNeuralArchitectureDialog dialog=new TrainNeuralArchitectureDialog(shell);
		if (dialog.open() != Window.OK)return;
		
//		logger.info("Architecture Name: "+neuralArchitecture.getName());
		
		BasicNetwork network = neuralArchitecture.createNetwork();
		MLTrain train;
		
		if(dialog.getTrainingMethod()==TrainingMethod.SIMULATED_ANNEALING){
			train = new NeuralSimulatedAnnealing(
					network,
					neuralArchitecture,
					dialog.getStartTemperature(),
					dialog.getStopTemperature(),
					dialog.getCycles());
		}
		else{
			train = new MLMethodGeneticAlgorithm(
				new MethodFactory(){
					@Override
					public MLMethod factor() {
						final BasicNetwork network = neuralArchitecture.createNetwork();
						((MLResettable)network).reset();
						return network;
					}
				},
				neuralArchitecture,
				dialog.getPopulation());
		}
		
		progressBarArchitecture.setMinimum(0);
		progressBarArchitecture.setMaximum(dialog.getNbOfEpoch()+1);
		
		neuralArchitecture.prepareScoring();
		
		NetworkTrainer trainer=new NetworkTrainer(train, dialog.getNbOfEpoch());
		trainer.schedule();
		
		
//		int epoch = 1;
//
//		for(int i=0;i<dialog.getNbOfEpoch();i++) {
//			train.iteration();
//			System.out
//					.println("Epoch #" + epoch + " Score:" + train.getError());
//			epoch++;
//		} 
//		train.finishTraining();
		
	}
	
	private void trainNeuralNetork(NeuralNetwork network){
		logger.info("Layer count: "+network.getNetwork().getLayerCount());
//		network.getNetwork().getLayerCount()
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
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
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
			
			if(element instanceof IbBar){
				IbBar bar=(IbBar) element;
				return IbBar.format(bar.getTimeInMs());
			}
			else if(element instanceof List<?>){
				List<?> list=(List<?>) element;
				String suffix="";
				
				if(neuralConfiguration.getTrainingBlocks().contains(list))
					suffix=" [*]";
				
				IbBar bar=(IbBar)list.get(0);

				switch (neuralConfiguration.getSplitStrategy()) {
				case WEEK:
					Calendar sunday=IbBar.getLastSundayOfDate(bar.getTimeInMs());
					return IbBar.format(sunday.getTimeInMillis())+suffix;
					
				case DAY:
					Calendar day=IbBar.getCurrentDayOf(bar.getTimeInMs());
					return IbBar.format(day.getTimeInMillis())+suffix;
				}
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
			
			if(element instanceof IbBar){
				IbBar bar=(IbBar) element;
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
				return String.valueOf(neuralNetwork.getScore());
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
				return String.valueOf(neuralNetwork.getTrainingProfit());
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
				return String.valueOf(neuralNetwork.getTrainingRisk());
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
				return String.valueOf(neuralNetwork.getBackTestingProfit());
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
				return String.valueOf(neuralNetwork.getBackTestingRisk());
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
	
	
	//######################################
  	//##           Trainer Job            ##
  	//######################################
	private class NetworkTrainer extends Job{
		
		MLTrain train;
		int nbOfEpoch;
		
		public NetworkTrainer(MLTrain train,int nbOfEpoch) {
			super("NetworkTrainer");
			this.train=train;
			this.nbOfEpoch=nbOfEpoch;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			String text="Training is starting!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			epoch = 0;
			updateProgressBarArchitecture();
			for(int i=0;i<nbOfEpoch;i++) {
				train.iteration();
				text="Epoch #" + epoch + " Score:" + train.getError();
				eventBroker.post(IEventConstant.TEXT_INFO,text);
				
				//TODO Calculate the expected end of training
				
				epoch++;
				updateProgressBarArchitecture();
			} 
			train.finishTraining();
			
			text="Training finished, best score:" + train.getError();
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			
			text="Please wait the network will be saved...";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			neuralArchitecture.addNeuralNetwork((BasicNetwork)train.getMethod());
			neuralProvider.updateNeuralArchitecture(neuralConfiguration);
			refreshTreeArchitecture();
			
			epoch++;
			updateProgressBarArchitecture();
			
			epoch=-1;
			
			text="Data are now saved!";
			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			return Status.OK_STATUS;
		}
		
	}
	
	private void updateProgressBarArchitecture(){
		Display.getDefault().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
				progressBarArchitecture.setSelection(epoch);
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
			neuralConfiguration.setSize(IbBar.getBarSizeFromString(comboBarSize
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
			
			List<IbBarContainer> containers = historicalDataProvider
					.getAllExContractBars(neuralConfiguration.getContract());
			
			for (IbBarContainer container : containers) {

				// Collect the mid point data
				if (neuralConfiguration.getReferenceData() == ReferenceData.MID_POINT
						&& container.getType() == WhatToShow.MIDPOINT) {
					List<IbBar> allBars = historicalDataProvider.getAllBars(
							container, neuralConfiguration.getSize());
					neuralConfiguration.setAllMidPointBars(allBars);
				}

				// Collect the Ask Data
				if (neuralConfiguration.getReferenceData() == ReferenceData.BID_AND_ASK
						&& container.getType() == WhatToShow.ASK) {
					List<IbBar> allBars = historicalDataProvider.getAllBars(
							container, neuralConfiguration.getSize());
					neuralConfiguration.setAllAskBars(allBars);
				}

				// Collect the Bid Data
				if (neuralConfiguration.getReferenceData() == ReferenceData.BID_AND_ASK
						&& container.getType() == WhatToShow.BID) {
					List<IbBar> allBars = historicalDataProvider.getAllBars(
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
			
			
//			Split the data
			neuralConfiguration.splitReferenceData();
			updateProgressBarDataSet();
			
//			Fill the neural input bar collector
			fillTheNeuralInputsBarsCollector();
			updateProgressBarDataSet();
			
//			Compute the neural indicator values and reset the ranges of the components
			neuralConfiguration.computeAllNeuralIndicatorInputs();
			updateProgressBarDataSet();
			
//			In order to save memory clear the data collector
			neuralConfiguration.getNeuralInputsBarsCollector().clear();
			updateProgressBarDataSet();
			
//			Compute the adapted Data of each components
			neuralConfiguration.computeAdaptedDataOfEachComponents();
			updateProgressBarDataSet();
			
//			save the configuration
			neuralProvider.updateTrainingData(neuralConfiguration);
			updateProgressBarDataSet();
			
		}
			
	}
	
	
	private void refreshNbOfDataText(){
		Display.getDefault().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
				switch (neuralConfiguration.getReferenceData()) {
				case MID_POINT:
					textNbOfData.setText(String.valueOf(neuralConfiguration
							.getAllMidPointBars().size()));
					break;
				case BID_AND_ASK:
					textNbOfData.setText(String.valueOf((neuralConfiguration
							.getAllAskBars().size() + neuralConfiguration
							.getAllBidBars().size()) / 2));
					break;
				}
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
}