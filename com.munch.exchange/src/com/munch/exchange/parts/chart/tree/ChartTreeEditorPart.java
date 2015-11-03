 
package com.munch.exchange.parts.chart.tree;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.parts.chart.parameter.ChartParameterEditorPart;
import com.munch.exchange.parts.chart.tree.ChartTreeComposite.ActivatedLabelProvider;
import com.munch.exchange.parts.chart.tree.ChartTreeComposite.ColorLabelProvider;
import com.munch.exchange.parts.chart.tree.ChartTreeComposite.NameLabelProvider;
import com.munch.exchange.services.IBundleResourceLoader;

public class ChartTreeEditorPart {
	
	public static final String CHART_TREE_EDITOR_ID="com.munch.exchange.part.chart.tree.editor";
	
	private static Logger logger = Logger.getLogger(ChartTreeEditorPart.class);
	
	private Label lblSelection;
	
	private IbChartIndicatorGroup indicatorGroup;
	private Composite parent;
	
	private Tree tree;
	private TreeViewer treeViewer;
	
	@Inject
	IBundleResourceLoader loader;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	MDirtyable dirty;
	
	@Inject
	private Shell shell;
	
	@Inject
	EPartService partService;
	
	
	@Inject
	public ChartTreeEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		this.parent=parent;
		
		lblSelection = new Label(parent, SWT.NONE);
		lblSelection.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblSelection.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblSelection.setText("Waitig of a indicator group selection");
	}
	
	private void clear(){
		if(lblSelection!=null && !lblSelection.isDisposed())
			lblSelection.dispose();
		
		if(treeViewer!=null && !treeViewer.getTree().isDisposed())
			treeViewer.getTree().dispose();
		
		
		parent.update();
		parent.setLayout(new GridLayout(1, false));
		
		
		
	}
	
	private void update(){
		clear();
		
		//setLayout(new GridLayout(1, false));
		treeViewer = new TreeViewer(parent,  SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection=event.getSelection();
				if(selection instanceof IStructuredSelection){
					IStructuredSelection sel=(IStructuredSelection) selection;
					if(sel.size()==1 && sel.getFirstElement() instanceof IbChartIndicator){
						IbChartIndicator selInd=(IbChartIndicator) sel.getFirstElement();
						//logger.info("Chart indicator selected");
						eventBroker.post(IEventConstant.IB_CHART_INDICATOR_SELECTED, selInd);
							
					}
				}
				
			}
		});
		treeViewer.setContentProvider(new ChartTreeContentProvider());
		treeViewer.setInput(indicatorGroup);
		treeViewer.setAutoExpandLevel(2);
		
		//Add Drag Support
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		treeViewer.addDragSupport(operations, transferTypes , new ChartTreeDragSourceListener(treeViewer));

		
		ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE); 
		
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		
		addColumn("Name",300,new NameLabelProvider());
		addColumn("X",30,new ActivatedLabelProvider(), new ChartTreeActivatedEditingSupport(treeViewer,this));
		addColumn("C",30,new ColorLabelProvider(), new ChartTreeColorEditingSupport(treeViewer,this));
		
		treeViewer.refresh();
		
		MPart part=partService.findPart(ChartParameterEditorPart.CHART_PARAMETER_EDITOR_ID);
		if(part!=null){
			partService.showPart(part, PartState.CREATE);
			partService.bringToTop(part);
		}
		
		parent.layout();
		
		
	}
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider,EditingSupport editingSupport){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		treeViewerColumn.setEditingSupport(editingSupport);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		//trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		//trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	
	public void refresh() {
		
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
	
	
	public IEventBroker getEventBroker() {
		return eventBroker;
	}
	
	public void setDity(){
		dirty.setDirty(true);
	}
	
	
	
	// ################################
	// ## ColumnLabelProvider ##
	// ################################

	class NameLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {

			// logger.info("Ele class: "+element.getClass());

			if (element instanceof IbChartIndicatorGroup) {
				IbChartIndicatorGroup el = (IbChartIndicatorGroup) element;
				return el.getName();
			} else if (element instanceof IbChartIndicator) {
				IbChartIndicator el = (IbChartIndicator) element;
				return el.getName();
			} else if (element instanceof IbChartSerie) {
				IbChartSerie el = (IbChartSerie) element;
				return el.getName();
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof IbChartIndicatorGroup) {
				return loader.loadImage(getClass(),
						IImageKeys.RATE_CONTAINER_STOCKS);
			} else if (element instanceof IbChartIndicator) {
				return loader.loadImage(getClass(), IImageKeys.RATE_STOCK);
			} else if (element instanceof IbChartSerie) {
				return loader.loadImage(getClass(), IImageKeys.RATE_INDICE);
			}
			return super.getImage(element);
		}

	}

	class ActivatedLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			/*
			 * if(element instanceof ChartIndicatorGroup){ return ""; } else
			 * if(element instanceof ChartIndicator){ //ChartIndicator
			 * el=(ChartIndicator) element; return ""; } else if(element
			 * instanceof ChartSerie){ ChartSerie el=(ChartSerie) element;
			 * if(el.isActivated()) return "X"; }
			 */
			return null;
		}

		@Override
		public Image getImage(Object element) {

			if (element instanceof IbChartSerie) {
				IbChartSerie el = (IbChartSerie) element;
				if (el.isActivated())
					return loader.loadImage(getClass(), IImageKeys.CHECKED);
				else
					return loader.loadImage(getClass(), IImageKeys.UNCHECKED);
			} else if (element instanceof IbChartIndicator) {
				IbChartIndicator el = (IbChartIndicator) element;
				if (el.isActivated())
					return loader.loadImage(getClass(), IImageKeys.CHECKED);
				else
					return loader.loadImage(getClass(), IImageKeys.UNCHECKED);
			}
			return super.getImage(element);
		}

	}

	class ColorLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof IbChartSerie) {
				return "C";
			}
			return "";
		}

		@Override
		public Color getBackground(Object element) {
			if (element instanceof IbChartSerie) {
				IbChartSerie el = (IbChartSerie) element;
				Color color = new Color(null, el.getColor_R(),
						el.getColor_G(), el.getColor_B());

				return color;
			}

			return super.getBackground(element);
		}

		@Override
		public Color getForeground(Object element) {
			if (element instanceof IbChartSerie) {
				IbChartSerie el = (IbChartSerie) element;
				Color color = new Color(null, el.getColor_R(),
						el.getColor_G(), el.getColor_B());

				return color;
			}
			return super.getForeground(element);
		}

	}
	
	
	//################################
  	//##       Event Reaction       ##
  	//################################
	
	private boolean isCompositeAbleToReact(){
		if (shell.isDisposed())
			return false;
		
		if(indicatorGroup==null)return false;
		
		
		return true;
}
	
	@Inject
	public void analyseSelection( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_GROUP_SELECTED) IbChartIndicatorGroup selIncGroup){
		
		// logger.info("Analyse selection!!");
		 
		 if(indicatorGroup!=null && indicatorGroup==selIncGroup)
			return;
		 
		 
		indicatorGroup=selIncGroup;
	    if(isCompositeAbleToReact()){
	    	//logger.info("Selected  group recieved: "+indicatorGroup.getName());
	    	update();
	    }
	}
	
	
	
	
	
	
}