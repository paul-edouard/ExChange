package com.munch.exchange.parts.neuralnetwork.error;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.ResourceManager;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizer.OptInfo;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager.NNOptManagerInfo;
import com.munch.exchange.job.objectivefunc.NetworkArchitectureObjFunc.NetworkArchitectureOptInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.parts.neuralnetwork.error.TreeWorkerContentProvider.Worker;
import com.munch.exchange.parts.neuralnetwork.error.TreeWorkerContentProvider.Workers;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.INeuralNetworkProvider;

import org.eclipse.swt.custom.TableTree;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;

public class NeuralNetworkErrorPart {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkErrorPart.class);
	
	public static final String NEURALNETWORK_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.neuralnetworkerroreditor";
	
	@Inject
	private Stock stock;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	INeuralNetworkProvider nnprovider;
	
	//@Inject
	//NeuralNetworkOptimizer optimizer;
	
	@Inject
	NeuralNetworkOptimizerManager optimizerManager;
	
	
	private TreeWorkerContentProvider provider=new TreeWorkerContentProvider("Workers");
	
	private Button btnStop;
	private ProgressBar progressBarNetworkError;
	
	private Composite compositeChart;
	private JFreeChart chart;
	private XYSeriesCollection errorData;
	private XYSeries lastSeries;
	
	private HashMap<Integer, XYSeries> dimSerieMap=new HashMap<Integer, XYSeries>();
	private HashMap<Integer, Integer> dimSerieSteps=new HashMap<Integer, Integer>();
	private int nbOfOptSteps=0;
	private Tree tree;
	private TreeViewer treeViewer;
	
	public NeuralNetworkErrorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmGraph = new TabItem(tabFolder, SWT.NONE);
		tbtmGraph.setText("Graph");
		
		Composite compositeGraph = new Composite(tabFolder, SWT.NONE);
		tbtmGraph.setControl(compositeGraph);
		compositeGraph.setLayout(new GridLayout(1, false));
		
		//====================
		//Chart Creation
		//====================
		/*
		Composite compositeChart = new Composite(compositeGraph, SWT.NONE);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		*/
		
		createChart();
		compositeChart = new ChartComposite(compositeGraph, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		//====================
		
		
		
		Composite compositeGraphBottom = new Composite(compositeGraph, SWT.NONE);
		GridLayout gl_compositeGraphBottom = new GridLayout(2, false);
		gl_compositeGraphBottom.marginHeight = 0;
		gl_compositeGraphBottom.marginWidth = 0;
		gl_compositeGraphBottom.verticalSpacing = 0;
		compositeGraphBottom.setLayout(gl_compositeGraphBottom);
		compositeGraphBottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		progressBarNetworkError = new ProgressBar(compositeGraphBottom, SWT.NONE);
		progressBarNetworkError.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnStop = new Button(compositeGraphBottom, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//optimizer.cancel();
				optimizerManager.cancel();
				btnStop.setEnabled(false);
				progressBarNetworkError.setEnabled(false);
			}
		});
		btnStop.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/delete.png"));
		btnStop.setText("Stop");
		
		TabItem tbtmWorkers = new TabItem(tabFolder, SWT.NONE);
		tbtmWorkers.setText("Workers");
		
		Composite compositeTable = new Composite(tabFolder, SWT.NONE);
		tbtmWorkers.setControl(compositeTable);
		compositeTable.setLayout(new GridLayout(1, false));
		
		treeViewer = new TreeViewer(compositeTable, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL);
		treeViewer.setAutoExpandLevel(1);
		treeViewer.setContentProvider(provider);
		treeViewer.setInput(provider.getWorkers());
		
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new IdLabelProvider());
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(100);
		trclmnId.setText("Id");
		
		TreeViewerColumn treeViewerColumn_0 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_0.setLabelProvider(new DimensionLabelProvider());
		TreeColumn trclmnDimension = treeViewerColumn_0.getColumn();
		trclmnDimension.setWidth(100);
		trclmnDimension.setText("Dimension");
		
		TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_1.setLabelProvider(new LastReactionLabelProvider());
		TreeColumn trclmnLastReaction = treeViewerColumn_1.getColumn();
		trclmnLastReaction.setWidth(100);
		trclmnLastReaction.setText("Last Reaction");
		
		TreeViewerColumn treeViewerColumn_2 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_2.setLabelProvider(new StatusLabelProvider());
		TreeColumn trclmnStatusManager = treeViewerColumn_2.getColumn();
		trclmnStatusManager.setWidth(100);
		trclmnStatusManager.setText("Manager Status");
		
		TreeViewerColumn treeViewerColumn_3 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_3.setLabelProvider(new StatusArchiLabelProvider());
		TreeColumn trclmnStatusArchitecture = treeViewerColumn_3.getColumn();
		trclmnStatusArchitecture.setWidth(100);
		trclmnStatusArchitecture.setText("Architecture Status");
		
		TreeViewerColumn treeViewerColumn_4 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_4.setLabelProvider(new StatusOptLabelProvider());
		TreeColumn trclmnStatusOpt = treeViewerColumn_4.getColumn();
		trclmnStatusOpt.setWidth(100);
		trclmnStatusOpt.setText("Optimization Status");
		
		TreeViewerColumn treeViewerColumn_5 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_5.setLabelProvider(new StatusLeaningLabelProvider());
		TreeColumn trclmnStatusLearning = treeViewerColumn_5.getColumn();
		trclmnStatusLearning.setWidth(100);
		trclmnStatusLearning.setText("Learning Status");
		
	}
	
	
	 /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {
    	//====================
    	//===  Main Axis   ===
    	//====================
    	NumberAxis domainAxis =createDomainAxis();
    	
    	//====================
    	//===  Main Plot   ===
    	//====================
        XYPlot plot1 = createMainPlot(domainAxis);
        
        //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart("",
                JFreeChart.DEFAULT_TITLE_FONT, plot1, true);
        chart.setBackgroundPaint(Color.white);
      
        return chart;
    	
    }
	
    private NumberAxis createDomainAxis(){
   	 //Axis
       NumberAxis domainAxis = new NumberAxis("Step");
       domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
       domainAxis.setAutoRange(true);
       domainAxis.setLowerMargin(0.01);
       domainAxis.setUpperMargin(0.01);
       return domainAxis;
   }
    /**
     * Create the Main Plot
     * 
     * @return
     */
    private XYPlot createMainPlot( NumberAxis domainAxis){
    	
    	//====================
    	//=== Main Curves  ===
    	//====================
    	//Creation of data Set
        //XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close);
    	errorData = new XYSeriesCollection();
    	
    	
        //Renderer
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.0"), new DecimalFormat("0.0000")));
                
        if (renderer1 instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) renderer1;
            renderer.setBaseStroke(new BasicStroke(2.0f));
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesPaint(1, Color.DARK_GRAY);
            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
        }
        
        
        NumberAxis rangeAxis1 = new NumberAxis("Error");
      //  rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("0.00000");
        rangeAxis1.setNumberFormatOverride(format);
        rangeAxis1.setAutoRangeIncludesZero(false);
        
        //Plot
        XYPlot plot1 = new XYPlot(errorData, null, rangeAxis1, renderer1);
        plot1.setBackgroundPaint(Color.lightGray);
        plot1.setDomainGridlinePaint(Color.white);
        plot1.setRangeGridlinePaint(Color.white);
        plot1.setDomainAxis(domainAxis);
        
        
        return plot1;
    	
    }
	
    private XYSeries getLastSerie(){
		if(lastSeries==null)
			resetLastSeries();
		
		return lastSeries;
	}
	
	private void resetLastSeries(){
		lastSeries = new XYSeries("Error");
	}
    
	private void initDimSerieMap(NNOptManagerInfo info){
		
		dimSerieSteps.clear();
		
		//Delete old series
		Set<Integer> keySet=dimSerieMap.keySet();
		if(keySet==null)return;
		for(int i:keySet){
			if(i<info.getMinDim() || i>info.getMaxDim()){
				errorData.removeSeries(dimSerieMap.get(i));
				dimSerieMap.remove(i);
			}
		}
		
		//Add or clear the new series
		for(int i=info.getMinDim();i<=info.getMaxDim();i++){
			if(dimSerieMap.containsKey(i)){
				dimSerieMap.get(i).clear();
			}
			else{
				XYSeries series = new XYSeries("Dim "+i);
				dimSerieMap.put(i, series);
				errorData.addSeries(series);
			}
				
		}
		
		keySet=dimSerieMap.keySet();
		for(int i:keySet){
			dimSerieSteps.put(i, 0);
		}
		
	}
	
	private void updateProgressBar(OptInfo info){
		
		int step=info.getMaximum()-info.getStep()-1;
		if(nbOfOptSteps==0)
			nbOfOptSteps=info.getMaximum();
		
		
		dimSerieSteps.put(info.getNumberOfInnerNeurons(), step);
		int totalNbOfSteps=0;
		for(int i:dimSerieSteps.keySet()){
			totalNbOfSteps+=dimSerieSteps.get(i);
		}
		
		int total=nbOfOptSteps*dimSerieSteps.size();
		progressBarNetworkError.setSelection(totalNbOfSteps);
		progressBarNetworkError.setToolTipText(String.valueOf(100*totalNbOfSteps/total)+"%");
		progressBarNetworkError.setMaximum(total);
		
	}
	
	private void updateChart(OptInfo info){
		if(info.getResults().getResults().isEmpty())return;
		//Search the best results
		boolean[] bestArchi=info.getResults().getBestResult().getBooleanArray();
    	NetworkArchitecture archi=stock.getNeuralNetwork().getConfiguration().searchArchitecture(bestArchi,
    			nnprovider.getNetworkArchitecturesLocalSavePath(stock));
    	double error=archi.getBestValue();
		
    	XYSeries series = dimSerieMap.get(info.getNumberOfInnerNeurons());
    	if(series==null)return;
    	series.add(info.getMaximum()-info.getStep(), error);
	}
	
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
	
	
	/*
	public void setOptimizer(NeuralNetworkOptimizer optimizer) {
		this.optimizer = optimizer;
	}
	*/

	//################################
	//##  EVENT REACTIONS          ##
	//################################
	private boolean isAbleToReact(String rate_uuid){
		
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || stock == null || btnStop == null)
			return false;
		if (!incoming.getUUID().equals(stock.getUUID()))
			return false;
		
		return true;
	}
	
	//MANAGER
	@Inject
	private void networkOptManagerStarted(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_STARTED) NNOptManagerInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		
		provider.getWorkers().clear();
		
		btnStop.setEnabled(true);
		progressBarNetworkError.setEnabled(true);
		progressBarNetworkError.setSelection(0);
		
		initDimSerieMap(info);
		//chart.fireChartChanged();
		treeViewer.refresh();
	}
	
	@Inject
	private void networkOptManagerNewWorkerState(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED) NNOptManagerInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		//logger.info("WORKER_STATE_CHANGED: ");
		
		for(Integer i:info.getOptimizerStatusMap().keySet()){
			Worker worker = provider.getWorkers().getWorkerFromId(i);
			if(worker==null){
				if(!info.getOptimizerDimensionMap().containsKey(i))return;
				worker =provider.new Worker(i, info.getOptimizerDimensionMap().get(i));
				provider.getWorkers().addChild(worker);
			}
			worker.dimension=info.getOptimizerDimensionMap().get(i);
			worker.statusManager=info.getOptimizerStatusMap().get(i);
			
		}
		
		//treeViewer.setInput(provider.getWorkers());
		treeViewer.refresh();
		
	}
	
	@Inject
	private void networkOptManagerFinished(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED) NNOptManagerInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		btnStop.setEnabled(false);
		progressBarNetworkError.setSelection(0);
		progressBarNetworkError.setEnabled(false);
		
	}
	
	//ARCHITECTURE
	@Inject
	private void networkArchitectureNewStep(@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_STEP) OptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		//logger.info("New Step: "+info.getStep());
		
		if(progressBarNetworkError !=null && !progressBarNetworkError.isDisposed()){
			if(progressBarNetworkError.isEnabled()){
				
				//Update progress Bar
				updateProgressBar(info);
				
				//Update the chart
				updateChart(info);
		    	
			}
		}
		
		Worker worker = provider.getWorkers().getWorkerFromDimension(info.getDimension());
		if(worker==null)return;
		
		worker.resetLastReaction();
		worker.statusArchitecture="New Step: "+info.getStep()+"/"+info.getMaximum();
		
		treeViewer.refresh();
		
	}
	
	@Inject
	private void networkArchitectureNewBest(@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(progressBarNetworkError !=null && !progressBarNetworkError.isDisposed()){
			if(progressBarNetworkError.isEnabled()){
			
				//Update progress Bar
				updateProgressBar(info);
				
				//Update the chart
				updateChart(info);
			}
		}
		
		Worker worker = provider.getWorkers().getWorkerFromDimension(info.getDimension());
		if(worker==null)return;
		
		worker.resetLastReaction();
		worker.statusArchitecture="New Best at Step: "+info.getStep();
		
		treeViewer.refresh();
	}
	
	//OPTIMIZATION
	@Inject
	private void networkOptimizationStarted(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_STARTED) NetworkArchitectureOptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		
		Worker worker = provider.getWorkers().getWorkerFromDimension(info.getDimension());
		if(worker==null)return;
		
		worker.resetLastReaction();
		worker.statusOptimization="Started, Step: "+info.getStep()+"/"+info.getMaximum();
		
		treeViewer.refresh();
		
	}
	
	@Inject
	private void networkOptimizationLoop(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_LOOP) NetworkArchitectureOptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		reactOnNetworkOptimizationEvent(info,"LOOP");
	}
	
	@Inject
	private void networkOptimizationStep(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_NEW_STEP) NetworkArchitectureOptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		reactOnNetworkOptimizationEvent(info, "STEP");
	}
	
	@Inject
	private void networkOptimizationFinished(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_FINISHED) NetworkArchitectureOptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		reactOnNetworkOptimizationEvent(info, "FINISHED");
	}
	
	@Inject
	private void networkOptimizationNewBest(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL) NetworkArchitectureOptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		reactOnNetworkOptimizationEvent(info,"BEST");
	}
	
	private void reactOnNetworkOptimizationEvent(NetworkArchitectureOptInfo info, String suffix){
		Worker worker = provider.getWorkers().getWorkerFromDimension(info.getDimension());
		if(worker==null)return;
		
		worker.resetLastReaction();
		worker.statusOptimization="Loop"+info.getLoop()+", Step: "+info.getStep()+"/"+info.getMaximum()+ " ["+suffix+"]";
		
		treeViewer.refresh();
	}
	
	//LEARNING
	@Inject
	private void networkOptimizationLeaning(@Optional @UIEventTopic(IEventConstant.NETWORK_LEARNING_STARTED) NetworkArchitectureOptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		Worker worker = provider.getWorkers().getWorkerFromDimension(info.getDimension());
		if(worker==null)return;
		
		worker.resetLastReaction();
		worker.statusLearning="Id: "+info.getLearningId()+"/"+info.getLearningMax();
		
		treeViewer.refresh();
	}
	
	
	//################################
	//##     ColumnLabelProvider    ##
	//################################
	
	class IdLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Workers){
				Workers el=(Workers) element;
				return el.groupName;
			}
			else if(element instanceof Worker){
				Worker el=(Worker) element;
				return String.valueOf(el.id);
			}
			return super.getText(element);
		}
		
	}
	
	class DimensionLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Worker){
				Worker el=(Worker) element;
				return String.valueOf(el.dimension);
			}
			return super.getText(element);
		}
		
	}
	
	class LastReactionLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Worker){
				Worker el=(Worker) element;
				Calendar diff=Calendar.getInstance();
				long secondes=(diff.getTimeInMillis()-el.lastReaction.getTimeInMillis())/1000;
				return String.valueOf(secondes)+"s";
			}
			return "";
		}
		
	}
	
	class StatusLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Worker){
				Worker el=(Worker) element;
				return String.valueOf(el.statusManager);
			}
			return "";
		}
		
	}
	
	class StatusArchiLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Worker){
				Worker el=(Worker) element;
				return String.valueOf(el.statusArchitecture);
			}
			return "";
		}
		
	}
	
	class StatusOptLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Worker){
				Worker el=(Worker) element;
				return String.valueOf(el.statusOptimization);
			}
			return "";
		}
		
	}
	
	class StatusLeaningLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof Worker){
				Worker el=(Worker) element;
				return String.valueOf(el.statusLearning);
			}
			return "";
		}
		
	}
	
	
	//################################
	//##          STATIC            ##
	//################################
	
	public static MPart openNeuralNetworkErrorPart(
			Stock stock,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			//NeuralNetworkOptimizer optimizer,
			NeuralNetworkOptimizerManager optimizerManager,
			IEclipseContext context){
		
		MPart part=searchPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID,stock.getUUID(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setPartContext(part,stock,optimizerManager,context);
			}
			/*
				if(part instanceof NeuralNetworkErrorPart){
					NeuralNetworkErrorPart nne_part=(NeuralNetworkErrorPart) part;
					nne_part.setOptimizer(optimizer);
				}
				*/
			
				partService.bringToTop(part);
				return  part;
		}
		
		
		//Create the part
		part=createPart(stock,optimizerManager,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	private static MPart createPart(
			Stock stock,
			//NeuralNetworkOptimizer optimizer,
			NeuralNetworkOptimizerManager optimizerManager,
			EPartService partService,
			IEclipseContext context){
		MPart part = partService.createPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(stock.getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(stock.getUUID());
		//part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setPartContext(part,stock,optimizerManager,context);
		
		//OptimizationErrorPart p=(OptimizationErrorPart) part;
		//p.setType(Optimizer.stringToOptimizationType(optimizationType));
		
		return part;
	}
	
	private static void setPartContext(
			MPart part,
			Stock stock,
			//NeuralNetworkOptimizer optimizer,
			NeuralNetworkOptimizerManager optimizerManager,
			IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(Stock.class, stock);
		//part.getContext().set(NeuralNetworkOptimizer.class, optimizer);
		part.getContext().set(NeuralNetworkOptimizerManager.class, optimizerManager);
		part.getContext().set(MDirtyable.class, new MyMDirtyable(part));
	}
	
	private static MPart searchPart(String partId,String tag,EModelService modelService,MApplication application){
		
		List<MPart> parts=getPartList(partId,tag,modelService, application);
		if(parts.isEmpty())return null;
		return parts.get(0);
	}
	
	private static List<MPart> getPartList(String partId,String tag,EModelService modelService,MApplication application){
		List<String> tags=new LinkedList<String>();
		tags.add(tag);
		//tags.add(optimizationType);
			
		List<MPart> parts=modelService.findElements(application,
				partId, MPart.class,tags );
		return parts;
	}
}
