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
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.services.INeuralNetworkProvider;

public class NeuralNetworkTrainingDataComposite extends Composite{
	
	
	private static Logger logger = Logger.getLogger(NeuralNetworkTrainingDataComposite.class);
	
	private Configuration config=null;
	
	private Text textNbOfAvailableData;
	private Text textPercentOfTraining;
	private Text textNbOfBlocks;
	private Slider sliderNbOfBlocks;
	private Slider sliderPercentOfTraining;
	
	private NeuralNetworkTrainingDataContentProvider contentProvider=new NeuralNetworkTrainingDataContentProvider();
	private Tree tree;
	private TreeViewer treeViewer;
	
	@Inject
	private INeuralNetworkProvider neuralNetworkProvider;
	
	
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
		new Label(compositeHeader, SWT.NONE);
		
		Label lblPerOfTraining = new Label(compositeHeader, SWT.NONE);
		lblPerOfTraining.setText("Per. of Training:");
		
		sliderPercentOfTraining = new Slider(compositeHeader, SWT.NONE);
		
		textPercentOfTraining = new Text(compositeHeader, SWT.BORDER);
		textPercentOfTraining.setEditable(false);
		textPercentOfTraining.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfBlocks = new Label(compositeHeader, SWT.NONE);
		lblNbOfBlocks.setText("Nb. of Blocks:");
		
		sliderNbOfBlocks = new Slider(compositeHeader, SWT.NONE);
		
		textNbOfBlocks = new Text(compositeHeader, SWT.BORDER);
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

	
	private void refreshGui(){
		
		logger.info("Refresh called!");
		textNbOfAvailableData.setText(String.valueOf(this.config.getTrainingSet().size()));
		
		if(this.config.getTrainingSet()==null){
			logger.info("Training set is null!");
			return;
		}
		removeAllInputsColumns();
		createAllInputsColumns();
		
		treeViewer.setInput(this.config);
		treeViewer.refresh();
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
			if(element instanceof DataSetRow){
				DataSetRow el=(DataSetRow) element;
				return "raw";
			}
			if(element instanceof double[]){
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
	
	private boolean isCompositeAbleToReact(){
		if (textNbOfAvailableData == null  )
			return false;
				
		if (textNbOfAvailableData.isDisposed())
			return false;

		return true;
	}
	
	
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
