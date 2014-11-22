package com.munch.exchange.parts.neuralnetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.NeuralNetworkOptimizer.OptInfo;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.wb.swt.ResourceManager;
import org.goataa.impl.utils.Individual;
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

public class NeuralNetworkErrorPart {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkErrorPart.class);
	
	public static final String NEURALNETWORK_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.neuralnetworkerroreditor";
	
	@Inject
	private Stock stock;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	private Button btnStop;
	private ProgressBar progressBarNetworkError;
	
	private Composite compositeChart;
	private JFreeChart chart;
	private XYDataset errorData;
	private XYSeries lastSeries;
	
	
	
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
		btnStop.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/delete.png"));
		btnStop.setText("Stop");
		
		TabItem tbtmTable = new TabItem(tabFolder, SWT.NONE);
		tbtmTable.setText("Table");
		
		Composite compositeTable = new Composite(tabFolder, SWT.NONE);
		tbtmTable.setControl(compositeTable);
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
        chart = new JFreeChart("Error Graph",
                JFreeChart.DEFAULT_TITLE_FONT, plot1, false);
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
    	errorData = new XYSeriesCollection(this.getLastSerie());
    	
        //Renderer
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.0"), new DecimalFormat("0.00")));
                
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
        DecimalFormat format = new DecimalFormat("0.00");
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
    
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
	
	
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
	
	@Inject
	private void networkArchitectureStarted(@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_STARTED) OptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		btnStop.setEnabled(true);
		progressBarNetworkError.setEnabled(true);
		progressBarNetworkError.setMaximum(info.getMaximum());
		progressBarNetworkError.setSelection(0);
		this.getLastSerie().clear();
		
	}
	
	@Inject
	private void networkArchitectureNewStep(@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_STEP) OptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		//logger.info("New Step: "+info.getStep());
		
		if(progressBarNetworkError !=null && !progressBarNetworkError.isDisposed()){
			if(progressBarNetworkError.isEnabled()){
				
				int val=info.getMaximum()-info.getStep()-1;
				
				//progressBarNetworkError.setMaximum(info.getMaximum());
				progressBarNetworkError.setSelection(val);
				progressBarNetworkError.setToolTipText(String.valueOf(100*val/info.getMaximum())+"%");
				
				if(info.getResults().getResults().isEmpty())return;
				//Search the best results
				boolean[] bestArchi=info.getResults().getBestResult().getBooleanArray();
		    	NetworkArchitecture archi=stock.getNeuralNetwork().getConfiguration().searchArchitecture(bestArchi);
		    	double error=archi.getOptResults().getBestResult().getValue();
				
				this.getLastSerie().add(info.getMaximum()-info.getStep(), error);
			}
		}
	}
	
	@Inject
	private void networkArchitectureNewBest(
			@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(progressBarNetworkError !=null && !progressBarNetworkError.isDisposed()){
			if(progressBarNetworkError.isEnabled()){
			
				progressBarNetworkError.setSelection(info.getMaximum()-info.getStep()-1);
				
				boolean[] bestArchi=info.getResults().getBestResult().getBooleanArray();
		    	NetworkArchitecture archi=stock.getNeuralNetwork().getConfiguration().searchArchitecture(bestArchi);
		    	double error=archi.getOptResults().getBestResult().getValue();
				
				this.getLastSerie().addOrUpdate(info.getMaximum()-info.getStep(), error);
			}
		}
	}
	
	@Inject
	private void networkArchitectureFinished(
			@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED) OptInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		btnStop.setEnabled(true);
		progressBarNetworkError.setSelection(0);
		progressBarNetworkError.setEnabled(true);
		
	}
	
	
	//################################
	//##          STATIC            ##
	//################################
	
	public static MPart openNeuralNetworkErrorPart(
			Stock stock,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			IEclipseContext context){
		
		MPart part=searchPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID,stock.getUUID(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setPartContext(part,stock,context);
			}
			
				partService.bringToTop(part);
				return  part;
		}
		
		
		//Create the part
		part=createPart(stock,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	private static MPart createPart(Stock stock,EPartService partService,IEclipseContext context){
		MPart part = partService.createPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(stock.getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(stock.getUUID());
		//part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setPartContext(part,stock,context);
		
		//OptimizationErrorPart p=(OptimizationErrorPart) part;
		//p.setType(Optimizer.stringToOptimizationType(optimizationType));
		
		return part;
	}
	
	private static void setPartContext(MPart part,Stock stock,IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(Stock.class, stock);
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
