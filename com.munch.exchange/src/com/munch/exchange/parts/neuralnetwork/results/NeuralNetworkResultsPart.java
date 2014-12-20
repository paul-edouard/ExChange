 
package com.munch.exchange.parts.neuralnetwork.results;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
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
	
	private Configuration config=null;
	private Label lblSelectedConfig;
	private Tree tree;
	private TreeViewer treeViewer;
	@Inject
	public NeuralNetworkResultsPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
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
		
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		addColumn("Id",100,new IdLabelProvider());
		addColumn("Inner Neurons",100,new InnerNeuronsLabelProvider());
		addColumn("Best Result",100,new BestResultsLabelProvider());
		
	}
	
	private TreeViewerColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider ){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		
		return treeViewerColumn;
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
				double val=el.getBestValue();
				if(val==Constants.WORST_FITNESS)return "No Results";
				return String.valueOf(val);
			}
			return super.getText(element);
		}
		
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
	private void neuralNetworkDataLoaded(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Stock stock) {
	
		if (!isCompositeAbleToReact())return;
		
		config=stock.getNeuralNetwork().getConfiguration();
		lblSelectedConfig.setText(stock.getFullName()+": "+config.getName());
		
		
		treeViewer.setInput(config);
		treeViewer.refresh();
		
	}
	
	
	
	
}