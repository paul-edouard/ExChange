 
package com.munch.exchange.parts.neuralnetwork.results;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.goataa.impl.utils.Constants;

public class NeuralNetworkResultsPart {
	
	public static final String NEURAL_NETWORK_RESULTS_ID="com.munch.exchange.part.networkresults";
	
	private Stock stock=null;
	private Configuration config=null;
	private Label lblSelectedConfig;
	private Tree tree;
	private TreeViewer treeViewer;
	private TreeNNResultViewerComparator comparator=new TreeNNResultViewerComparator();
	
	
	@Inject
	public NeuralNetworkResultsPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		comparator=new TreeNNResultViewerComparator();
		
		parent.setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(parent, SWT.NONE);
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeHeader.setLayout(new GridLayout(1, false));
		
		lblSelectedConfig = new Label(compositeHeader, SWT.NONE);
		lblSelectedConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSelectedConfig.setBounds(0, 0, 81, 25);
		lblSelectedConfig.setText("Selected Config:");
		
		treeViewer = new TreeViewer(parent, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION );
		treeViewer.setAutoExpandLevel(1);
		treeViewer.setContentProvider(new TreeNNResultsContentProvider());
		treeViewer.setComparator(comparator);
		
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		TreeColumn firstColumn=addColumn("Id",50,new IdLabelProvider(),0);
		addColumn("Inner Neurons",50,new InnerNeuronsLabelProvider(),1);
		addColumn("Best Result",100,new BestResultsLabelProvider(),2);
		
		addColumn("Best Opt. Rate",100,new BestOptimizationRateLabelProvider(),3);
		addColumn("Middle Opt. Rate",100,new MiddleOptimizationRateLabelProvider(),4);
		addColumn("Nb. Of Opt.",50,new NbOfOptimizationRateLabelProvider(),5);
		
		addColumn("Best Tr. Rate",100,new BestTrainingRateLabelProvider(),6);
		addColumn("Middle Tr. Rate",100,new MiddleTrainingRateLabelProvider(),7);
		addColumn("Nb. Of Tr.",50,new NbOfTrainingRateLabelProvider(),8);
		
		tree.setSortColumn(firstColumn);
	    tree.setSortDirection(1);
		
		refresh();
	}
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider, int columnId ){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	
	private void refresh(){
		if(config==null || stock==null)return;
		lblSelectedConfig.setText(stock.getFullName()+": "+config.getName());
		treeViewer.setInput(config);
		treeViewer.refresh();
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
		//TODO Your code here
	}
	

	//################################
	//##     ColumnLabelProvider    ##
	//################################	
	
	public void setStock(Stock stock) {
		this.stock = stock;
		config=stock.getNeuralNetwork().getConfiguration();
		
		if (!isCompositeAbleToReact())return;
		refresh();
	}


	class IdLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return String.valueOf(el.getId());
			}
			return super.getText(element);
		}
		
	}
	
	class InnerNeuronsLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return String.valueOf(el.getNumberOfInnerNeurons());
			}
			return super.getText(element);
		}
		
	}
	
	class BestResultsLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				double val=el.getBestValue()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.format("%.3f", val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	class BestOptimizationRateLabelProvider extends ColumnLabelProvider{
		
		@Override
		public String getToolTipText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				double val=el.getBestOptimizationRate()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.valueOf(val);
			}
			return super.getToolTipText(element);
		}
		

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				double val=el.getBestOptimizationRate()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.format("%.3f", val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	class MiddleOptimizationRateLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				double val=el.getMiddleOptimzationRate()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.format("%.3f", val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	class NbOfOptimizationRateLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				int val=el.getNumberOfOptimization();
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.valueOf(val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	
	class BestTrainingRateLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				double val=el.getBestTrainingRate()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.format("%.3f", val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	class MiddleTrainingRateLabelProvider extends ColumnLabelProvider{

		@Override
		public String getToolTipText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				double val=el.getMiddleTrainingRate()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.valueOf(val);
			}
			return super.getToolTipText(element);
		}

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				double val=el.getMiddleTrainingRate()*100;
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.format("%.3f", val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	class NbOfTrainingRateLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				int val=el.getNumberOfTraining();
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.valueOf(val);
				//return String.valueOf(val);
			}
			return super.getText(element);
		}
		
	}
	
	
	private SelectionAdapter getSelectionAdapter(final  TreeColumn  column,
		      final int index) {
		    SelectionAdapter selectionAdapter = new SelectionAdapter() {
		      @Override
		      public void widgetSelected(SelectionEvent e) {
		        comparator.setColumn(index);
		        int dir = comparator.getDirection();
		        treeViewer.getTree().setSortDirection(dir);
		        treeViewer.getTree().setSortColumn(column);
		        treeViewer.refresh();
		      }
		    };
		    return selectionAdapter;
		  }
	
	
	//################################
	//##       Event Reaction       ##
	//################################
	
	private boolean isCompositeAbleToReact(){
		if (lblSelectedConfig == null  )
			return false;
				
		if (lblSelectedConfig.isDisposed())
			return false;

		return true;
	}
	
	@Inject
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Stock stock) {
		
		if(stock==null)return;
		setStock(stock);
		
		
	}
	
	
	
	
}