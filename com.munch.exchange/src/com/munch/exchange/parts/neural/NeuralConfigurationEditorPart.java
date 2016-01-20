 
package com.munch.exchange.parts.neural;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration.ReferenceData;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration.SplitStrategy;
import com.munch.exchange.model.core.ib.neural.NeuralDayOfWeekInput;
import com.munch.exchange.model.core.ib.neural.NeuralDayPositionInput;
import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;
import com.munch.exchange.model.core.ib.neural.NeuralInput;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent;
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
	IEclipseContext context;
	
	@Inject
	private NeuralConfiguration neuralConfiguration;
	
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
	private Button btnDistribute;
	private Button btnSearch;
	private Combo comboReferenceData;
	private Combo comboSplitStrategy;
	private Combo comboBarSize;
	
	
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
				searchReferenceDataFunc();
			}
		});
		btnSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSearch.setText("Search");
		
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
		comboSplitStrategy.select(0);
		
		btnDistribute = new Button(compositeDataSetCommandItems, SWT.NONE);
		btnDistribute.setEnabled(false);
		btnDistribute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				distributeDataFunc();
			}
		});
		btnDistribute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnDistribute.setText("Distribute");
		
		treeViewerTrainingData = new TreeViewer(compositeDataSet,SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewerTrainingData.setContentProvider(new NeuralConfigurationTrainingDataContentProvider());
		treeViewerTrainingData.setInput(neuralConfiguration);
		
		treeTrainingData = treeViewerTrainingData.getTree();
		treeTrainingData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeTrainingData.setHeaderVisible(true);
		treeTrainingData.setLinesVisible(true);
		
		Composite composite = new Composite(compositeDataSet, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		ProgressBar progressBarDataSet = new ProgressBar(composite, SWT.NONE);
		progressBarDataSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfData = new Label(composite, SWT.NONE);
		lblNbOfData.setText("Number of Data: ");
		
		textNbOfData = new Text(composite, SWT.BORDER);
		textNbOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textNbOfData.setEditable(false);
		
		TabItem tbtmArchitectures = new TabItem(tabFolder, SWT.NONE);
		tbtmArchitectures.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/eclipse/action3.gif"));
		tbtmArchitectures.setText("Architectures");
		
		Composite compositeArchitectures = new Composite(tabFolder, SWT.NONE);
		tbtmArchitectures.setControl(compositeArchitectures);
		compositeArchitectures.setLayout(new GridLayout(1, false));
		
		
//		########################
//		##    POST GUI FUNC    ##
//		########################
		
		postGuiFunc();
	}
	
	private void preGuiFunc(){
		logger.info("Neural Config: "+neuralConfiguration);
		neuralProvider.loadNeuralInputs(neuralConfiguration);
	}
	
	private void postGuiFunc(){
//		neuralProvider.loadNeuralInputs(neuralConfiguration);
		treeViewerInputData.refresh();
		treeViewerInputData.expandAll();
		btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
		
		neuralProvider.loadTrainingData(neuralConfiguration);
		
//		Initialization of the bar size
		comboBarSize.setText(neuralConfiguration.getSize().toString());
		
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
		
		treeViewerInputData.refresh();
		treeViewerInputData.expandAll();
		neuralProvider.updateNeuralInputs(neuralConfiguration);
		btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
	}
	

	/**
	 * 2. Search the reference data
	 */
	private void searchReferenceDataFunc(){
		
//		Save the bar Size
		neuralConfiguration.setSize(IbBar.getBarSizeFromString(comboBarSize
				.getText()));
		
//		Save the reference data
		if (comboReferenceData.getSelectionIndex() == 0) {
			neuralConfiguration.setReferenceData(ReferenceData.MID_POINT);
		} else if (comboReferenceData.getSelectionIndex() == 1) {
			neuralConfiguration.setReferenceData(ReferenceData.BID_AND_ASK);
		}

//		Search the reference data
		neuralConfiguration.clearAllCollectedBars();
		
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

		btnDistribute.setEnabled(true);
		
	}
	
	
	/**
	 * 3. Split the data and distribute them 
	 */
	private void distributeDataFunc(){
		
//		Clear the tree
		removeAllInputsColumns();
		
//		Save the percent of training data
		neuralConfiguration.setPercentOfTrainingData(spinnerPercentOfTrainingData.getSelection());
		
//		Save the split strategy
		if (comboSplitStrategy.getSelectionIndex() == 0) {
			neuralConfiguration.setSplitStrategy(SplitStrategy.WEEK);
		} else if (comboSplitStrategy.getSelectionIndex() == 1) {
			neuralConfiguration.setSplitStrategy(SplitStrategy.DAY);
		}
		
//		Split the data
		neuralConfiguration.splitReferenceData();
		
//		Fill the neural input bar collector
		fillTheNeuralInputsBarsCollector();
		
//		Compute the neural indicator values and reset the ranges of the components
		neuralConfiguration.computeAllNeuralIndicatorInputs();
		
//		In order to save memory clear the data collector
		neuralConfiguration.getNeuralInputsBarsCollector().clear();
		
//		Compute the adapted Data of each components
		neuralConfiguration.computeAdaptedDataOfEachComponents();
		
//		Refresh the tree
		createAllTrainingDataColumns();
		treeViewerTrainingData.refresh();
		
//		save the configuration
		neuralProvider.updateTrainingData(neuralConfiguration);
		
		
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
		
		btnEdit.setEnabled(true);
		btnResetMinmax.setEnabled(neuralConfiguration.isResetMinMaxNeeded());
		treeInputData.setEnabled(false);
		
		dirty.setDirty(false);
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