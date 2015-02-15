 
package com.munch.exchange.parts.neuralnetwork.results;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizer.OptInfo;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager.NNOptManagerInfo;
import com.munch.exchange.job.objectivefunc.NetworkArchitectureObjFunc.NetworkArchitectureOptInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.core.neuralnetwork.training.TrainingBlock;
import com.munch.exchange.model.core.neuralnetwork.training.TrainingBlocks;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.parts.composite.RateChart;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.utils.ProfitUtils;

import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.goataa.impl.utils.Constants;
import org.neuroph.core.data.DataSet;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Button;

public class NeuralNetworkResultsPart {
	
	public static final String NEURAL_NETWORK_RESULTS_ID="com.munch.exchange.part.networkresults";
	
	private static Logger logger = Logger.getLogger(NeuralNetworkResultsPart.class);
	
	private Stock stock=null;
	private Configuration config=null;
	private Label lblSelectedConfig;
	private Tree tree;
	private TreeViewer treeViewer;
	private TreeNNResultViewerComparator comparator=new TreeNNResultViewerComparator();
	
	private LinkedList<TreeColumn> optColumns=new LinkedList<TreeColumn>();
	private LinkedList<TreeColumn> trainColumns=new LinkedList<TreeColumn>();
	
	private ResultsLoader resultLoader=new ResultsLoader();
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	ESelectionService selectionService;
	
	@Inject
	INeuralNetworkProvider nn_provider;
	
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
		compositeHeader.setLayout(new GridLayout(3, false));
		
		lblSelectedConfig = new Label(compositeHeader, SWT.NONE);
		lblSelectedConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSelectedConfig.setBounds(0, 0, 81, 25);
		lblSelectedConfig.setText("Selected Config:");
		
		btnOptInfo = new Button(compositeHeader, SWT.CHECK);
		btnOptInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int width=0;
				if(btnOptInfo.getSelection())width=100;
				
				for(TreeColumn col:optColumns){
					col.setWidth(width);
				}
				
			}
		});
		btnOptInfo.setText("Opt. Info");
		
		btnTrainInfo = new Button(compositeHeader, SWT.CHECK);
		btnTrainInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int width=0;
				if(btnTrainInfo.getSelection())width=100;
				
				for(TreeColumn col:trainColumns){
					col.setWidth(width);
				}
			}
		});
		btnTrainInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnTrainInfo.setText("Train Info");
		
		treeViewer = new TreeViewer(parent, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION );
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection=event.getSelection();
				if(selection instanceof IStructuredSelection){
					IStructuredSelection sel=(IStructuredSelection) selection;
					if(sel.size()==1 && sel.getFirstElement() instanceof NetworkArchitecture){
						NetworkArchitecture selArchi=(NetworkArchitecture) sel.getFirstElement();
						selectionService.setSelection(selArchi);
					}
				}
				
			}
		});
		treeViewer.setAutoExpandLevel(1);
		treeViewer.setContentProvider(new TreeNNResultsContentProvider());
		treeViewer.setComparator(comparator);
		ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE); 
		
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		TreeColumn firstColumn=addColumn("Id",50,new IdLabelProvider(),0);
		addColumn("Inner Neurons",50,new InnerNeuronsLabelProvider(),1);
		addColumn("Best Result",100,new BestResultsLabelProvider(),2);
		
		optColumns.add(addColumn("Best Opt. Rate",0,new BestOptimizationRateLabelProvider(),3));
		optColumns.add(addColumn("Middle Opt. Rate",0,new MiddleOptimizationRateLabelProvider(),4));
		optColumns.add(addColumn("Nb. Of Opt.",0,new NbOfOptimizationRateLabelProvider(),5));
		optColumns.add(addColumn("Last Opt.",0,new LastOptimizationLabelProvider(),9));
		
		trainColumns.add(addColumn("Best Tr. Rate",0,new BestTrainingRateLabelProvider(),6));
		trainColumns.add(addColumn("Middle Tr. Rate",0,new MiddleTrainingRateLabelProvider(),7));
		trainColumns.add(addColumn("Nb. Of Tr.",0,new NbOfTrainingRateLabelProvider(),8));
		trainColumns.add(addColumn("Last Tr.",0,new LastTrainingLabelProvider(),10));
		
		addColumn("Prediction",100,new PredictionLabelProvider(),11);
		
		addColumn("Tot. Profit %",100,new TotalProfitLabelProvider(),12);
		addColumn("Train. Profit %",100,new TrainProfitLabelProvider(),13);
		addColumn("Val. Profit %",100,new ValidateProfitLabelProvider(),14);
		
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
			if(element instanceof ResultEntity){
				ResultEntity res=(ResultEntity) element;
				return res.getId()+"["+res.getStringParam(ResultEntity.GENERATED_FROM)+"]";
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
			if(element instanceof ResultEntity){
				ResultEntity res=(ResultEntity) element;
				NetworkArchitecture el=(NetworkArchitecture)config.searchArchitecture(res.getParentId());
				if(el==null)return "";
				return String.valueOf(el.getNumberOfInnerNeurons());
			}
			return "";
		}
		
	}
	
	class BestResultsLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			double val=Double.NaN;
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				val=el.getBestValue();
				
				//return String.valueOf(val);
			}
			if(element instanceof ResultEntity){
				ResultEntity res=(ResultEntity) element;
				val=res.getValue();
			}
			
			if(val==Constants.WORST_FITNESS)return "No Results";
			//return String.valueOf(val);
			return String.format("%.4f", val);
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
			return "";
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
			return "";
		}
		
	}
	
	class NbOfOptimizationRateLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				
				int val=el.getNumberOfOptimization();
				return String.valueOf(val);
				//return String.valueOf(val);
			}
			return "";
		}
		
	}
	
	class LastOptimizationLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return DateTool.dateToString(el.getLastOptimization()).replace("T", " ");
				//return String.valueOf(val);
			}
			return "";
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
			return "";
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
			return "";
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
			return "";
		}
		
	}
	
	class LastTrainingLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return DateTool.dateToString(el.getLastTraining()).replace("T", " ");
			}
			return "";
		}
		
	}
	
	
	class PredictionLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				double pred=getResultsInfo(el).prediction;
				return String.format("%.2f", pred);
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				double pred=getResultsInfo(el).prediction;
				return String.format("%.2f", pred);
			}
			return super.getText(element);
		}

		@Override
		public Color getBackground(Object element) {
			
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				double pred=getResultsInfo(el).prediction;
				if(pred>ProfitUtils.SIGNAL_LIMIT)
					return new Color(null, 0, 255, 0);
				else
					return new Color(null, 255, 0, 0);
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				double pred=getResultsInfo(el).prediction;
				if(pred>ProfitUtils.SIGNAL_LIMIT)
					return new Color(null, 0, 255, 0);
				else
					return new Color(null, 255, 0, 0);
			}
			
			return super.getBackground(element);
		}
		
	}
	
	
	class TotalProfitLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return getResultsInfo(el).getTotalProfitString();
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				return getResultsInfo(el).getTotalProfitString();
			}
			return super.getText(element);
		}
		
		@Override
		public String getToolTipText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return getResultsInfo(el).getTotalProfitToolTip();
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				return getResultsInfo(el).getTotalProfitToolTip();
			}
			return super.getToolTipText(element);
		}

		
	}
	
	class TrainProfitLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return getResultsInfo(el).getTrainProfitString();
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				return getResultsInfo(el).getTrainProfitString();
			}
			return super.getText(element);
		}
		
		
		@Override
		public String getToolTipText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return getResultsInfo(el).getTrainProfitToolTip();
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				return getResultsInfo(el).getTrainProfitToolTip();
			}
			return super.getToolTipText(element);
		}
		
	}
	
	class ValidateProfitLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return getResultsInfo(el).getValidateProfitString();
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				return getResultsInfo(el).getValidateProfitString();
			}
			return super.getText(element);
		}

		@Override
		public String getToolTipText(Object element) {
			if(element instanceof NetworkArchitecture){
				NetworkArchitecture el=(NetworkArchitecture) element;
				return getResultsInfo(el).getValidateProfitToolTip();
			}
			if(element instanceof ResultEntity){
				ResultEntity el=(ResultEntity) element;
				return getResultsInfo(el).getValidateProfitToolTip();
			}
			return super.getToolTipText(element);
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

	
	//#######################################
	//##       Results Worker & Info       ##
	//#######################################	
	
	private HashMap<String, ResultsInfo> resultsInfoMap=new HashMap<String, ResultsInfo>();
	private Button btnOptInfo;
	private Button btnTrainInfo;
	
	private synchronized ResultsInfo getResultsInfo(NetworkArchitecture archi){
		if(!resultsInfoMap.containsKey(archi.getId()))
			resultsInfoMap.put(archi.getId(), new ResultsInfo());
		
		return resultsInfoMap.get(archi.getId());
	}
	
	private synchronized ResultsInfo getResultsInfo(ResultEntity ent){
		if(!resultsInfoMap.containsKey(ent.getId()))
			resultsInfoMap.put(ent.getId(), new ResultsInfo());
		
		return resultsInfoMap.get(ent.getId());
	}
	
	
	class ResultsInfo{
		public double prediction=Double.NaN;
		public double totalProfit=Double.NaN;
		public double totalProfitTarget=Double.NaN;
		
		public double trainProfit=Double.NaN;
		public double trainProfitTarget=Double.NaN;
		
		public double validateProfit=Double.NaN;
		public double validateProfitTarget=Double.NaN;
		
		
		private String getProfitString(double profit,double profitTarget){
			String per=String.format("%.1f",profit/profitTarget*100);
			return per;
		}
		
		private String getProfitTooltip(double profit,double profitTarget){
			String pro=String.format("%.1f",profit);
			String proTarget=String.format("%.1f",profitTarget);
			
			return "["+pro+"/"+proTarget+"]";
		}
		
		public String getTotalProfitString(){
			return getProfitString(totalProfit,totalProfitTarget);
		}
		public String getTotalProfitToolTip(){
			return getProfitTooltip(totalProfit,totalProfitTarget);
		}
		
		public String getTrainProfitString(){
			return getProfitString(trainProfit,trainProfitTarget);
		}
		public String getTrainProfitToolTip(){
			return getProfitTooltip(trainProfit,trainProfitTarget);
		}
		
		public String getValidateProfitString(){
			return getProfitString(validateProfit,validateProfitTarget);
		}
		public String getValidateProfitToolTip(){
			return getProfitTooltip(validateProfit,validateProfitTarget);
		}
		
	}
	
	class ResultsLoader extends Job {
		
		private boolean loadResultsEntities=true;
		
		
		public void setLoadResultsEntities(boolean loadResultsEntities) {
			this.loadResultsEntities = loadResultsEntities;
		}

		public ResultsLoader() {
			super("Result loader");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			resultsInfoMap.clear();
			
			if (monitor.isCanceled())return Status.CANCEL_STATUS;
			
			
			
			nn_provider.createAllValuePoints(config,false);
			
			double[] input=config.getLastInput();
			DataSet dataset=config.getDataSet();
			//DataSet trainSet=config.getTrainingDataSet();
			//DataSet valSet=config.getValidateDataSet();
			
			//RateChart.PENALTY;
			
			for(NetworkArchitecture archi:config.getNetworkArchitecturesCopy()){
				boolean wasLoaded=false;
				
				if(archi.isResultLoaded() && loadResultsEntities){
					logger.info("Results here!!"+archi.getId());
					wasLoaded=true;
					for(ResultEntity ent:archi.getResultsEntities()){
						if (monitor.isCanceled())return Status.CANCEL_STATUS;
						
						ResultsInfo info=getResultsInfo(ent);
						
						//Prediction
						double pred=archi.calculateNetworkOutput(input, ent.getDoubleArray());
						info.prediction=pred;
						
						//Profit
						calculateProfits(info,archi,dataset,ent.getDoubleArray());
						
					}
				}
				
				
				if (monitor.isCanceled())return Status.CANCEL_STATUS;
				
				ResultsInfo info=getResultsInfo(archi);
				
				if(archi.getBestResultEntity()==null)continue;
				
				//Prediction
				double pred=archi.calculateNetworkOutputFromBestResult(input);
				info.prediction=pred;
				
				calculateProfits(info,archi,dataset,archi.getBestResultEntity().getDoubleArray());
				
				
				/*
				//Total Profit
				double[][] outputs=archi.calculateNetworkOutputsAndProfitFromBestResult(dataset, ProfitUtils.PENALTY);
				if(outputs==null)continue;
				double[] profit=outputs[5];
				info.totalProfit=profit[profit.length-1];
				
				//Train Profit
				outputs=archi.calculateNetworkOutputsAndProfitFromBestResult(trainSet, ProfitUtils.PENALTY);
				if(outputs==null)continue;profit=outputs[5];
				info.trainProfit=profit[profit.length-1];
				
				//Validate Profit
				if(valSet==null)continue;
				outputs=archi.calculateNetworkOutputsAndProfitFromBestResult(valSet,  ProfitUtils.PENALTY);
				if(outputs==null)continue;profit=outputs[5];
				info.validateProfit=profit[profit.length-1];
				*/
				
				
				if(!wasLoaded)
					archi.clearResultsAndNetwork(false);
			}
			
			
			eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_CALCULATED,config);
			
			
			return Status.OK_STATUS;
		}
		
		
		private void calculateProfits(ResultsInfo info,NetworkArchitecture archi,DataSet dataset, double[] weigths){
			
			//Calculate the outputs
			double[][] outputs=archi.calculateNetworkOutputsAndProfit(dataset, weigths, ProfitUtils.PENALTY);
			if(outputs==null)return;
			
			double[] profit=outputs[5];
			double[] targetProfit=outputs[6];
			
			info.totalProfit=0;info.totalProfitTarget=0;
			info.validateProfit=0;info.validateProfitTarget=0;
			info.trainProfit=0;info.trainProfitTarget=0;
			
			TrainingBlocks tb=archi.getParent().getTrainingBlocks();
			for(TrainingBlock block:tb.getBlocks()){
    			for(int i=block.getStart();i<=block.getEnd();i++){
    				double diff=profit[i]-info.totalProfit;
    				double diffTarget=targetProfit[i]-info.totalProfitTarget;
    				
    				info.totalProfit+=diff;
    				info.totalProfitTarget+=diffTarget;
    				
    				
    				if(block.isTraining()){
    					info.trainProfit+=diff;
    					info.trainProfitTarget+=diffTarget;
    				}
    				else{
    					info.validateProfit+=diff;
    					info.validateProfitTarget+=diffTarget;
    				}
    			}
    		}
			
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
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Configuration config) {
		
		if(config==null)return;
		stock=config.getParent();
		if(stock==null)return;
		setStock(stock);
		
		resultLoader.schedule();
	}
	
	@Inject
	private void neuralNetworkResutsCalculated(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_CALCULATED) Configuration config) {
		
		if(config==null)return;
		if(this.config!=config)return;
		
		if (!isCompositeAbleToReact())return;
		treeViewer.refresh();
	}
	
	
	@Inject
	private void neuralNetworkResutsRefreshCalled(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_REFRESH_CALLED) Configuration config) {
		
		if(config==null)return;
		stock=config.getParent();
		if(stock==null)return;
		setStock(stock);
		
		resultLoader.schedule();
	}
	
	
	@Inject
	private void neuralNetworkResutsLoadingCalled(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_LOADING_CALLED) NetworkArchitecture archi) {
		
		if(archi==null)return;
		if(archi.getParent()!=config)return;
		
		resultLoader.schedule();
	}
	
	@Inject
	private void neuralNetworkResutsUnloadingCalled(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_UNLOADING_CALLED) NetworkArchitecture archi) {
		
		if(archi==null)return;
		if(archi.getParent()!=config)return;
		
		refresh();
	}
	
	

	@Inject
    private void optimizationStarted(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_STARTED) NNOptManagerInfo info){

    	if(info==null)return;
    	if (!isCompositeAbleToReact())return;
    	if(stock.getNeuralNetwork().getConfiguration()!=info.getConfiguration())return;
    	
    	resultLoader.setLoadResultsEntities(false);
	}
    
	
	
	@Inject
    private void optimizationFinished(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED) NNOptManagerInfo info){
    	
    	
		if(info==null)return;
    	if (!isCompositeAbleToReact())return;
    	if(stock.getNeuralNetwork().getConfiguration()!=info.getConfiguration())return;
    	
    	resultLoader.setLoadResultsEntities(true);
    
    	
    }
	
	
		
	
	
	
}