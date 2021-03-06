package com.munch.exchange.parts.neuralnetwork.data;

import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NNDataSetRaw;
import com.munch.exchange.model.core.neuralnetwork.training.TrainingBlock;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.services.INeuralNetworkProvider;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class NeuralNetworkTrainingDataComposite extends Composite{
	
	
	private static Logger logger = Logger.getLogger(NeuralNetworkTrainingDataComposite.class);
	
	private Configuration config=null;
	
	private Text textNbOfAvailableData;
	private Text textTrainingRate;
	private Text textNbOfBlocks;
	private Slider sliderNbOfBlocks;
	private Slider sliderTrainingRate;
	
	private NeuralNetworkTrainingDataContentProvider contentProvider=new NeuralNetworkTrainingDataContentProvider();
	private Tree tree;
	private TreeViewer treeViewer;
	
	@Inject
	private INeuralNetworkProvider neuralNetworkProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private Stock stock;
	
	
	private Button btnDistribute;
	
	
	@Inject
	public NeuralNetworkTrainingDataComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(this, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(3, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfAvailableData = new Label(compositeHeader, SWT.NONE);
		lblNbOfAvailableData.setText("Nb. of Data:");
		
		textNbOfAvailableData = new Text(compositeHeader, SWT.BORDER);
		textNbOfAvailableData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textNbOfAvailableData.setEditable(false);
		
		btnDistribute = new Button(compositeHeader, SWT.NONE);
		btnDistribute.setEnabled(false);
		btnDistribute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				config.getTrainingBlocks().setNbOfBlocks(sliderNbOfBlocks.getSelection());
				config.getTrainingBlocks().setTrainingRate(((double) sliderTrainingRate.getSelection() )/100.0);
				
				
				createTrainingBlocks();
				
			}
		});
		btnDistribute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnDistribute.setText("Distribute");
		
		Label lblPerOfTraining = new Label(compositeHeader, SWT.NONE);
		lblPerOfTraining.setText("Training Rate:");
		
		sliderTrainingRate = new Slider(compositeHeader, SWT.NONE);
		sliderTrainingRate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textTrainingRate.setText(sliderTrainingRate.getSelection()+"%");
			}
		});
		sliderTrainingRate.setMaximum(101);
		sliderTrainingRate.setMinimum(1);
		sliderTrainingRate.setSelection(70);
		sliderTrainingRate.setPageIncrement(1);
		
		textTrainingRate = new Text(compositeHeader, SWT.BORDER);
		textTrainingRate.setText("70%");
		textTrainingRate.setEditable(false);
		textTrainingRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfBlocks = new Label(compositeHeader, SWT.NONE);
		lblNbOfBlocks.setText("Nb. of Blocks:");
		
		sliderNbOfBlocks = new Slider(compositeHeader, SWT.NONE);
		sliderNbOfBlocks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNbOfBlocks.setText(String.valueOf(sliderNbOfBlocks.getSelection()));
			}
		});
		sliderNbOfBlocks.setSelection(20);
		sliderNbOfBlocks.setPageIncrement(1);
		
		textNbOfBlocks = new Text(compositeHeader, SWT.BORDER);
		textNbOfBlocks.setText("20");
		textNbOfBlocks.setEditable(false);
		textNbOfBlocks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		treeViewer = new TreeViewer(this,SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setAutoExpandLevel(1);
		
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		addColumn("Name",100,new IdLabelProvider());
		addColumn("D. Output",100,new DesiredOutputLabelProvider());
		
	}
	
	
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider ){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		//trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	
	private void removeAllInputsColumns(){
		while(tree.getColumnCount()>2){
			tree.getColumns()[tree.getColumnCount()-1].dispose();
		}
	}
	
	private void createAllInputsColumns(){
		LinkedList<String> names=this.config.getInputNeuronNames();
		int pos=0;
		for(String columnName:names){
			addColumn(columnName,100,new InputLabelProvider(pos));
			pos++;
		}
	}

	
	private void createTrainingBlocks(){
		config.getTrainingBlocks().createBlocks(config.getDataSet());
		config.setResultsCalculationNeeded(true);
		config.setDirty(true);
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_DIRTY,config);
		
		InfoPart.postInfoText(eventBroker, String.valueOf(config.getTrainingBlocks()));
	}
	
	private void adaptTrainingBlocks(){
		
		int nbOfRows=config.getDataSet().size();
		TrainingBlock block=config.getTrainingBlocks().getBlocks().getLast();
		
		if(block.getEnd()!=nbOfRows-1){
			
			if(nbOfRows-1>block.getEnd()){
				if(block.isTraining()){
					TrainingBlock lastBlock=new TrainingBlock(block.getEnd()+1,nbOfRows-1);
					config.getTrainingBlocks().addLast(lastBlock);
				}
				else{
					block.setEnd(nbOfRows-1);
				}
			}
			else{
				config.getTrainingBlocks().reduceBlocksTo(nbOfRows-1);
				config.setResultsCalculationNeeded(true);
			}
			config.setDirty(true);
			eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_DIRTY,config);
		
		}
	}
	
	
	private void resetGui(){
		textNbOfAvailableData.setText("");
		removeAllInputsColumns();
		
		//treeViewer.setInput(new Configuration());
		tree.setEnabled(false);
		treeViewer.refresh();
	}
	
	public void refreshGui(){
		
		logger.info("Refresh called!");
		config=stock.getNeuralNetwork().getConfiguration();
		if(config==null){
			resetGui();
			return;
		}
		if(this.config.getDataSet()==null){
			InfoPart.postInfoText(eventBroker, "Data set is null: Try to create them");
			neuralNetworkProvider.createAllValuePoints(this.config,false);
		}
		
		
		if(this.config.getDataSet()==null){
			InfoPart.postInfoText(eventBroker, "Cannot refresh Training data composite: Training set is null!");
			resetGui();
			return;
		}
		
		
		
		if(this.config.getTrainingBlocks().getNbOfBlocks()<=0){
			this.config.getTrainingBlocks().setNbOfBlocks(20);
			this.config.getTrainingBlocks().setTrainingRate(0.7);
			createTrainingBlocks();
		}
		
		adaptTrainingBlocks();
		
		
		textNbOfAvailableData.setText(String.valueOf(this.config.getDataSet().size()));
		btnDistribute.setEnabled(true);
		//if(this.config.getTrainingBlocks().getNbOfBlocks()>0){
			this.sliderNbOfBlocks.setSelection(this.config.getTrainingBlocks().getNbOfBlocks());
			this.textNbOfBlocks.setText(String.valueOf(sliderNbOfBlocks.getSelection()));
			
			this.sliderTrainingRate.setSelection( (int)(this.config.getTrainingBlocks().getTrainingRate()*100));
			this.textTrainingRate.setText(sliderTrainingRate.getSelection()+"%");
		/*
		}
		else{
			this.sliderNbOfBlocks.setSelection(20);
			this.sliderTrainingRate.setSelection(70);
		}
		*/
		
		
		removeAllInputsColumns();
		createAllInputsColumns();
		
		tree.setEnabled(true);
		treeViewer.setInput(this.config);
		treeViewer.refresh();
	}
	
	
	public void setEnabled(boolean enabled){
		
		//tree.setEnabled(enabled);
		sliderNbOfBlocks.setEnabled(enabled);
		sliderTrainingRate.setEnabled(enabled);
		btnDistribute.setEnabled(enabled);
		
	}
	
	
	//################################
	//##     ColumnLabelProvider    ##
	//################################	
	
 	class IdLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof DataSet){
				DataSet el=(DataSet) element;
				if(el.getLabel().equals(Configuration.ROOT_DATA_SET)){
					return "Inputs";
				}
				
				return el.getLabel();
			}
			else if(element instanceof NNDataSetRaw){
				NNDataSetRaw el=(NNDataSetRaw) element;
				return DateTool.dateToDayString(el.getDate());
			}
			else if(element instanceof DataSetRow){
				DataSetRow el=(DataSetRow) element;
				return "raw";
			}
			else if(element instanceof double[]){
				return "Last Input: ";
			}
			
			return super.getText(element);
		}
		
	}
	
	class DesiredOutputLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			
			if(element instanceof DataSetRow){
				DataSetRow el=(DataSetRow) element;
				return String.format("%.0f", el.getDesiredOutput()[0]);
			}
			if(element instanceof double[]){
				return "?";
			}
			
			return "";
		}
		
	}
	
	class InputLabelProvider extends ColumnLabelProvider{
		
		private int pos;
		
		public InputLabelProvider(int pos){
			this.pos=pos;
		}
		
		@Override
		public String getText(Object element) {
			
			if(element instanceof DataSetRow){
				DataSetRow el=(DataSetRow) element;
				return String.format("%.3f", el.getInput()[pos]);
			}
			if(element instanceof double[]){
				double[] lastInput=(double[]) element;
				return String.format("%.3f", lastInput[pos]);
			}
			
			return "";
		}
		
	}
	

	//################################
	//##       Event Reaction       ##
	//################################
	/*
	private boolean isCompositeAbleToReact(){
		if (textNbOfAvailableData == null  )
			return false;
				
		if (textNbOfAvailableData.isDisposed())
			return false;

		return true;
	}
	*/
	/*
	@Inject
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Configuration config) {
		
		if(!isCompositeAbleToReact())return;
		if(config==null)return;
		//if(config.getTrainingSet()==null)return;
		
		this.config=config;
		neuralNetworkProvider.createAllValuePoints(this.config,false);
		
		refreshGui();
	}
	*/
	/*
	@Inject
	private void neuralNetworkConfigResultsCalculated(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_CALCULATED) Configuration config) {
		
		if(!isCompositeAbleToReact())return;
		if(config==null)return;
		//if(config!=this.config)return;
		
		this.config=config;
		
		logger.info("Refresh RESULTS_CALCULATED!");
		
		refreshGui();
	}
	*/
	

}
