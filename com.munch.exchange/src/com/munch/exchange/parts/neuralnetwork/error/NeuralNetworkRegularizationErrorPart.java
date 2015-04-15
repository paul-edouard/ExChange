 
package com.munch.exchange.parts.neuralnetwork.error;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
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

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkRegulizer;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizer.OptInfo;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager.NNOptManagerInfo;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkRegulizer.RegularizationInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.utils.ProfitUtils;

public class NeuralNetworkRegularizationErrorPart {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkRegularizationErrorPart.class);
	
	public static final String NEURALNETWORK_REGULARIZATION_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.neuralnetwork.regularizarion.error.editor";
	
	@Inject
	private NetworkArchitecture archi;
	
	@SuppressWarnings("rawtypes")
	private NeuralNetwork network=null;
	private DataSet trainingSet=null;
	private DataSet testSet=null;
	
	private HashMap<String, PlotElement> elementMap=new HashMap<String, PlotElement>();
	private LinkedList<Generation> generations=new LinkedList<Generation>();
	
	private NeuralNetworkRegulizer regulizer;
	private int step;
	
	
	private Button btnStop;
	private ProgressBar progressBarNetworkError;
	
	private Composite compositeChart;
	private JFreeChart chart;
	private XYSeriesCollection errorData = new XYSeriesCollection();;
	private XYSeries trainingSeries=new XYSeries("Training");
	private XYSeries testSeries=new XYSeries("Test");
	
	private XYSeries lastSeries;
	
	
	@Inject
	public NeuralNetworkRegularizationErrorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
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
				regulizer.cancel();
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
    	errorData.addSeries(trainingSeries);
    	errorData.addSeries(testSeries);
       	
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
    
	
	
	
    private void updateProgressBar(RegularizationInfo info){
	
		progressBarNetworkError.setSelection(info.getStep());
		progressBarNetworkError.setToolTipText(String.valueOf(100*info.getStep()/info.getMaxStep())+"%");
		progressBarNetworkError.setMaximum(info.getMaxStep());
		
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
	
	
	private class Generation{
		
		private LinkedList<PlotElement> elements=new LinkedList<PlotElement>();
		
		private int max=10;
		
		public Generation(LinkedList<ResultEntity> entities,int max){
			
			this.max=max;
			
			for(ResultEntity ent :entities ){
				if(elementMap.containsKey(ent.getId())){
					elements.add(elementMap.get(ent.getId()));
					continue;
				}
					
				PlotElement el=new PlotElement(ent);
				elementMap.put(ent.getId(), el);
				elements.add(el);
			}
			
			double trainingErrorTotal=0;
			double testErrorTotal=0;
			
			int pos=0;
			for(PlotElement el:elements){
				if(pos>=max)break;
				
				trainingErrorTotal+=el.getTrainingError();
				testErrorTotal+=el.getTestError();
				
				pos++;
			}
			
			trainingSeries.add(step,trainingErrorTotal/pos);
			testSeries.add(step,testErrorTotal/pos);
			
			
		}


		public LinkedList<PlotElement> getElements() {
			return elements;
		}
		
		
		
		
	}
	
	private class PlotElement{
		
		ResultEntity entity;
		private double trainingError;
		private double testError;
		
		public PlotElement(ResultEntity entity){
			this.entity=entity;
			
			calculateErrors();
		}
		
		private void calculateErrors(){
			network.setWeights(entity.getDoubleArray());
			
			
			double[][] outputs=NetworkArchitecture.calculateOutputsAndProfit(network, trainingSet);
			double train=outputs[5][outputs[5].length-1];
			double maxTrainProfit=outputs[6][outputs[6].length-1];
			
			trainingError=1-train/maxTrainProfit;
			
			
			outputs=NetworkArchitecture.calculateOutputsAndProfit(network, testSet);
			double test=outputs[5][outputs[5].length-1];
			double maxTestProfit=outputs[6][outputs[6].length-1];
			
			testError=1-test/maxTestProfit;
			
		}

		public double getTrainingError() {
			return trainingError;
		}

		public double getTestError() {
			return testError;
		}

		public ResultEntity getEntity() {
			return entity;
		}
		
		
		
		
	}
	
	//################################
	//##  EVENT REACTIONS          ##
	//################################
	
	private boolean isAbleToReact(RegularizationInfo info){
		if(info==null)return false;
		if(info.getArchi()!=this.archi)return false;
		if(!info.getArchi().getId().equals(this.archi.getId()))return false;
		if(btnStop == null)return false;
		
		updateProgressBar(info);
		
		return true;
	}
	
	
	@Inject
	private void regularizationStarted(@Optional @UIEventTopic(IEventConstant.REGULARIZATION_STARTED) RegularizationInfo info){
		
		if(!isAbleToReact(info))return;
		
		this.regulizer=info.getRegulizer();
		this.network=this.archi.getCopyOfFaMeNetwork();
		
		trainingSet=info.getTrainingSet();
		testSet=info.getTestSet();
		elementMap.clear();
		generations.clear();
		
		
		trainingSeries.clear();
		testSeries.clear();
		
		
	}
	
	@Inject
	private void regularizationFinished(@Optional @UIEventTopic(IEventConstant.REGULARIZATION_FINISHED) RegularizationInfo info){
		
		if(!isAbleToReact(info))return;
		
		
		
		
	}
	
	@Inject
	private void regularizationNewStep(@Optional @UIEventTopic(IEventConstant.REGULARIZATION_NEW_STEP) RegularizationInfo info){
		
		if(!isAbleToReact(info))return;
		
		step=info.getStep();
		
		generations.add(new Generation(info.getPopulation(),info.getNbOfIndividualsToTrain()));
	}
	
	
	//################################
	//##          STATIC            ##
	//################################
	
	public static MPart openPart(
			NetworkArchitecture archi,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			NeuralNetworkRegulizer regulizer,
			IEclipseContext context){
		
		MPart part=searchPart(NeuralNetworkRegularizationErrorPart.NEURALNETWORK_REGULARIZATION_ERROR_EDITOR_ID,archi.getId(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setPartContext(part,archi,regulizer,context);
			}
			
			partService.bringToTop(part);
			return  part;
		}
		
		
		//Create the part
		part=createPart( archi,regulizer,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	private static MPart createPart(
			NetworkArchitecture archi,
			NeuralNetworkRegulizer regulizer,
			EPartService partService,
			IEclipseContext context){
		MPart part = partService.createPart(NeuralNetworkRegularizationErrorPart.NEURALNETWORK_REGULARIZATION_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel("Regularization: " +archi.getParent().getParent().getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(archi.getId());
		//part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setPartContext(part,archi,regulizer,context);
		
		return part;
	}
	
	private static void setPartContext(
			MPart part,
			NetworkArchitecture archi,
			NeuralNetworkRegulizer regulizer,
			IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(NetworkArchitecture.class, archi);
		part.getContext().set(NeuralNetworkRegulizer.class, regulizer);
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