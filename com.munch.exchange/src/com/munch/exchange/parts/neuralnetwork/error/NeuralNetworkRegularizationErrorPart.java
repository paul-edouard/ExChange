 
package com.munch.exchange.parts.neuralnetwork.error;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.wb.swt.SWTResourceManager;

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
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.ShapeUtilities;
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
import com.munch.exchange.parts.neuralnetwork.results.NeuralNetworkResultChartComposite;
import com.munch.exchange.parts.neuralnetwork.results.ResultDataItem;
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
	
	
	//Pareto Chart
	private XYLineAndShapeRenderer rendererPareto =new XYLineAndShapeRenderer(true, false);
	private XYSeriesCollection collectionPareto=new XYSeriesCollection();
	private JFreeChart chartPareto;
	private NeuralNetworkResultChartComposite compositeChartPareto;
	
	
	
	
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
		
		TabItem tbtmPareto = new TabItem(tabFolder, SWT.NONE);
		tbtmPareto.setText("Pareto");
		
		Composite compositePareto = new Composite(tabFolder, SWT.NONE);
		tbtmPareto.setControl(compositePareto);
		compositePareto.setLayout(new GridLayout(1, false));
		
		//==================================================
	  	//==                 PARETO                       ==    
	  	//==================================================
		createParetoChart();
	  	compositeChartPareto = new NeuralNetworkResultChartComposite(compositePareto, SWT.NONE,chartPareto);
	  	compositeChartPareto.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
	  	compositeChartPareto.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	  	compositeChartPareto.addChartMouseListener(new ChartMouseListener() {
			
			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}
	
	//==================================================
	//==                 PARETO                       ==    
	//==================================================
	
	private JFreeChart createParetoChart(){
		//====================
    	//===  Main Axis   ===
    	//====================
    	NumberAxis trainErrorAxis =	createParetoAxis("Train Error");
    	NumberAxis testErrorAxis  =	createParetoAxis("Test Error");
    	
    	
    	//updateSeries();
    	
    	//====================
    	//===  Main Plot   ===
    	//====================
    	XYPlot plot = createParetoPlot(collectionPareto,rendererPareto,trainErrorAxis,testErrorAxis);
    	
    
    	 //=========================
    	//=== Create the Chart  ===
    	//=========================
    	chartPareto = new JFreeChart("",
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
    	chartPareto.setBackgroundPaint(Color.white);
      
        return chartPareto;
	}
	
	private XYPlot createParetoPlot(XYSeriesCollection xySeriesCollection,XYLineAndShapeRenderer lineAndShapeRenderer, NumberAxis domainAxis, NumberAxis valueAxis){
    	//Plot
    	XYPlot plot = new XYPlot(xySeriesCollection, domainAxis, valueAxis, lineAndShapeRenderer);
    	//plot.setDomainAxis(valueAxis);
    	plot.setRenderer(lineAndShapeRenderer);
    	plot.setDataset(xySeriesCollection);
    	
    	plot.setDomainPannable(true);
        plot.setRangePannable(true);
        
        plot.setBackgroundPaint(Color.lightGray);
        //plot1.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
         
        //bubbleRenderer.setSeriesPaint(0, Color.blue);
    	
    	return plot;
    }
	
	private NumberAxis createParetoAxis(String name){
     	 //Axis
         NumberAxis domainAxis = new NumberAxis(name);
         domainAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
         domainAxis.setAutoRange(true);
         domainAxis.setLowerMargin(0.01);
         domainAxis.setUpperMargin(0.01);
         domainAxis.setVisible(true);
         domainAxis.setAutoRangeIncludesZero(false);
         return domainAxis;
     }
	
	private void updateParetoSeries(){
	    	collectionPareto.removeAllSeries();
	    	rendererPareto.clearSeriesPaints(false);
	    	rendererPareto.clearSeriesStrokes(false);
	    	
	    	HashSet<String> elIds=new HashSet<>();
	    	
	    	LinkedList<PlotElement> paretoFront=new LinkedList<>();
	    	LinkedList<PlotElement> toRemoveFromPareto=new LinkedList<>();
	    	
	    	int pos=0;
	    	for(Generation gen:generations){
	    		XYSeries serie =new XYSeries(String.valueOf("Gen: "+pos));
	    		for(PlotElement el:gen.getElements()){
	    			if(!elIds.contains(el.getEntity().getId())){
	    				serie.add(el.getTrainingError(), el.getTestError());
	    				elIds.add(el.getEntity().getId());
	    				
	    				//Add to pareto
	    				toRemoveFromPareto.clear();
	    				if(paretoFront.isEmpty())
	    					paretoFront.add(el);
	    				
	    				boolean toAdd=true;
	    				for(PlotElement p_el:paretoFront){
	    					if(el.getTestError()>p_el.getTestError() &&
	    					el.getTrainingError()>p_el.getTrainingError()){
	    						toAdd=false;
	    						break;
	    					}
	    				}
	    				
	    				if(toAdd){
	    					int p_pos=0;
	    					for(PlotElement p_el:paretoFront){
	    						if(p_el.getTrainingError()>el.getTrainingError())
	    							break;
	    						p_pos++;
	    					}
	    					paretoFront.add(p_pos, el);
	    				}
	    				
	    				
	    				
	    				for(PlotElement p_el:paretoFront){
	    					if(el.getTestError()<p_el.getTestError() &&
	    							el.getTrainingError()<p_el.getTrainingError()){
	    						toRemoveFromPareto.add(p_el);
	    					}
	    				}
	    				
	    				//logger.info("Pareto length before: "+paretoFront.size());
	    				if(toRemoveFromPareto.size()>0){
	    				//	logger.info("To remove from Pareto length: "+toRemoveFromPareto.size());
	    					
	    					for(PlotElement p_el:toRemoveFromPareto){
		    					paretoFront.remove(p_el);
		    				}
	    				}
	    				//logger.info("Pareto length after: "+paretoFront.size());
	    				
	    			}
	    		}
	    		
	    		Color col=Color.BLUE;
	    		if(pos==0)
	    			col=Color.WHITE;
	    		if(pos==generations.size()-1)
	    			col=Color.RED;
	    		
	    		addSeriesAsShape(rendererPareto, collectionPareto, serie,
	    				ShapeUtilities.createRegularCross(5f, 1.5f),col,false);
	    		
	    		pos++;
	    	}
	    	
	    	
	    	
	    	//logger.info("Pareto length: "+paretoFront.size());
	    	
	    	XYSeries serie =new XYSeries("Pareto");
	    	int par_pos=0;
	    	for(PlotElement p_el:paretoFront){
	    		if(par_pos==0)
	    			serie.add(p_el.getTrainingError(), 1);
	    		
	    		serie.add(p_el.getTrainingError(), p_el.getTestError());
	    		
	    		if(par_pos==paretoFront.size()-1)
	    			serie.add(1,  p_el.getTestError());
	    		
	    		par_pos++;
	    	}
	    	
	    	addSeriesAsLine(rendererPareto, collectionPareto, serie,
    				Color.ORANGE,true);
	    	
	    	
	    }
	
	private void addSeriesAsShape(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Shape shape,Color color,boolean useOutlinePaint){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			
			rend.setSeriesShapesVisible(pos, true);
			rend.setSeriesLinesVisible(pos, false);
			rend.setSeriesShape(pos,shape);
			rend.setSeriesShapesFilled(pos, true);
			rend.setSeriesPaint(pos, color);
			rend.setSeriesStroke(pos, new BasicStroke(1f));
			
			if(useOutlinePaint){
				rend.setSeriesOutlinePaint(pos, Color.BLACK);
				rend.setSeriesOutlineStroke(pos, new BasicStroke(1f));
				rend.setUseOutlinePaint(useOutlinePaint);
			}
			else{
				rend.setSeriesOutlinePaint(pos, color);
			}
			
		}
	}
	
	private void addSeriesAsLine(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Color color,boolean useOutlinePaint){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			
			rend.setSeriesShapesVisible(pos, false);
			rend.setSeriesLinesVisible(pos, true);
			//rend.setSeriesShape(pos,shape);
			rend.setSeriesShapesFilled(pos, false);
			rend.setSeriesPaint(pos, color);
			rend.setSeriesStroke(pos, new BasicStroke(3f));
           
			if(useOutlinePaint){
				rend.setSeriesOutlinePaint(pos, Color.BLACK);
				rend.setSeriesOutlineStroke(pos, new BasicStroke(3f));
				rend.setUseOutlinePaint(useOutlinePaint);
			}
			else{
				rend.setSeriesOutlinePaint(pos, color);
			}
			
		}
	}
	
	//==================================================
	//==                 RESULTS                      ==    
	//==================================================
	 
	 
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
			
			logger.info("Number of recieved entities: "+entities.size());
			
			for(ResultEntity ent :entities ){
				if(elementMap.containsKey(ent.getId())){
					elements.add(elementMap.get(ent.getId()));
					continue;
				}
					
				PlotElement el=new PlotElement(ent);
				elementMap.put(ent.getId(), el);
				elements.add(el);
			}
			
			logger.info("Number of elements: "+elements.size());
			
			double trainingErrorTotal=0;
			double testErrorTotal=0;
			
			int pos=0;
			for(PlotElement el:elements){
				//if(pos>=max)break;
				
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
		
		updateParetoSeries();
		
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