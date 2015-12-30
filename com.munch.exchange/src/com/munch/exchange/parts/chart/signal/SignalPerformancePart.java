package com.munch.exchange.parts.chart.signal;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import com.munch.exchange.model.core.ib.statistics.RevenueStatistics;
import com.munch.exchange.model.core.ib.statistics.StabilityStatistics;
import com.munch.exchange.model.core.ib.statistics.TimeStatistics;
import com.munch.exchange.model.core.ib.statistics.TradeStatistics;

public class SignalPerformancePart {
	
	public static final String SIGNAL_PERFORMANCE_ID="com.munch.exchange.part.chart.signal.performance";
	
	private static Logger logger = Logger.getLogger(SignalPerformancePart.class);
	
	@Inject
	private Shell shell;
	
	private Composite parent;
	private Label lblSelection;
	
	private Tree tree;
	private TreeViewer treeViewer;
	
	
	private IbChartSignal chartSignal;
	private IbChartSignalOptimizedParameters optParameters;
	private PerformanceMetrics performanceMetrics;
	
	

	public SignalPerformancePart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		this.parent=parent;
		
		lblSelection = new Label(parent, SWT.NONE);
		lblSelection.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblSelection.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblSelection.setText("Waitig of a signal selection");
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
		treeViewer.setContentProvider(new SignalPerformanceTreeContentProvider());
		treeViewer.setInput(performanceMetrics);
		treeViewer.setAutoExpandLevel(1);
		
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		
		addColumn("Name",300,new NameLabelProvider());
		addColumn("Value",300,new ValueLabelProvider());
		
		
		treeViewer.refresh();
		treeViewer.expandAll();
		
		
		parent.layout();
		
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
	
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
	
	
	// ################################
	// ## ColumnLabelProvider ##
	// ################################

	class NameLabelProvider extends ColumnLabelProvider {

			@Override
			public String getText(Object element) {

				if (element instanceof TradeStatistics) {
					return "Trade Statistics:";
				}
				if (element instanceof TimeStatistics) {
					return "Time Statistics:";
				}
				if (element instanceof StabilityStatistics) {
					return "Stability Statistics:";
				}
				if (element instanceof RevenueStatistics) {
					return "Revenue Statistics:";
				}
				if (element instanceof String) {
					String el = (String) element;
					return el.split(", ")[0];
				}
				return super.getText(element);
			}
	}
	
	class ValueLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {

			if (element instanceof String) {
				String el = (String) element;
				return el.split(", ")[1];
			}
			return "";
		}
}
	
	
	
	//################################
  	//##       Event Reaction       ##
  	//################################
	private boolean isCompositeAbleToReact(){
		if (shell.isDisposed())
			return false;
		
		if(chartSignal==null && optParameters==null)return false;
		
		return true;
	}
	
	@Inject
	public void analyseIbSelection( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_SELECTED) IbChartIndicator selIndic){
		
		if(!(selIndic instanceof IbChartSignal))return;
		IbChartSignal selSignal=(IbChartSignal)selIndic;
		
		logger.info("Analyse IB Chart Indiator selection!!");
		 
		if(chartSignal!=null && chartSignal==selSignal)
			return;
		 
		 
		chartSignal=selSignal;
		
	    if(isCompositeAbleToReact()){
	    	performanceMetrics=chartSignal.getPerformanceMetrics();
	    	update();
	    }
	}
	
	//IB_CHART_INDICATOR_OPTIMIZED_PARAMETERS_SELECTED
	
	@Inject
	public void analyseSelection( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_OPTIMIZED_PARAMETERS_SELECTED) IbChartSignalOptimizedParameters optimizedParameters){
		
		logger.info("Analyse IB Optimized Parameters selection!!");
		 
		if(optParameters!=null && optParameters==optimizedParameters)
			return;
		
		if(optimizedParameters==null)
			return;
		
		logger.info("Test if the Performance metrics are over there!");
		logger.info("Performance metric: "+optimizedParameters.getPerformanceMetrics()!=null);
		
		/*
		if(optimizedParameters==null || optimizedParameters.getPerformanceMetrics()==null)
			return;
		*/
		logger.info("Set the Performance metrics!");
		
		optParameters=optimizedParameters;
		
	    if(isCompositeAbleToReact()){
	    	
	    	logger.info("Update the Performance metrics!");
	    	performanceMetrics=optParameters.getPerformanceMetrics();
	    	update();
	    }
	}
	
	
	
	@Inject
	public void analyseParameterChanged( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_PARAMETER_CHANGED) IbChartIndicator selIndic){
		
		if(!(selIndic instanceof IbChartSignal))return;
		IbChartSignal selSignal=(IbChartSignal)selIndic;
		
		 logger.info("Analyse IB Chart Indiator Parameter changed");
		 
		 if(chartSignal!=selSignal)return;
		 
		 if(isCompositeAbleToReact()){
			performanceMetrics=chartSignal.getPerformanceMetrics();
	    	update();
		 }
	}
	
	

}
