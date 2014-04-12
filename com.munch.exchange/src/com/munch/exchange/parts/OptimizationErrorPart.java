 
package com.munch.exchange.parts;

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

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IOptimizationResultsProvider;

public class OptimizationErrorPart {
	
	private static Logger logger = Logger.getLogger(OptimizationErrorPart.class);
	
	public static final String OPTIMIZATION_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.optimizationerroreditor";
	
	
	@Inject
	ExchangeRate rate;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	IOptimizationResultsProvider optimizationResultsProvider;
	
	@Inject
	Job optimizer;
	
	private ProgressBar progressBarOptimizationStep;
	private Button btnCancelOptimization;
	private Type type=null;
	private Composite compositeChart;
	private JFreeChart chart;
	private XYDataset errorData;
	private XYSeries lastSeries;
	private Button btnSave;
	//private OptimizationResults results=new OptimizationResults();
	
	@Inject
	public OptimizationErrorPart() {
		//TODO Your code here
	}
	
	private boolean isFromType(Type type){
		
		if(this.type!=null){
			//results.setType(type);
			return this.type==type;
		}
		
		if(this instanceof MPart){
			MPart part=(MPart) this;
			
			
			if(part.getTags().contains(OptimizationResults.OptimizationTypeToString(type))){
				//results.setType(type);
				this.type=type;
				return true;
			}
			
		}
		
		return false;
	}
	private XYSeries getLastSerie(){
		if(lastSeries==null)
			resetLastSeries();
		
		return lastSeries;
	}
	
	private void resetLastSeries(){
		lastSeries = new XYSeries("Error");
	}
	
	


	@PostConstruct
	public void postConstruct(Composite parent) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);
		
		chart = createChart();
		compositeChart = new ChartComposite(parent, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeCommands = new Composite(parent, SWT.NONE);
		compositeCommands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeCommands.setLayout(new GridLayout(3, false));
		
		progressBarOptimizationStep = new ProgressBar(compositeCommands, SWT.NONE);
		progressBarOptimizationStep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnSave = new Button(compositeCommands, SWT.NONE);
		btnSave.setEnabled(false);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO
				if(optimizationResultsProvider.save(rate)){
					btnSave.setEnabled(false);
				}
			}
		});
		btnSave.setText("Save");
		
		btnCancelOptimization = new Button(compositeCommands, SWT.NONE);
		btnCancelOptimization.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				optimizer.cancel();
			}
		});
		btnCancelOptimization.setText("Cancel");
		//TODO Your code here
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
	
	
	/////////////////////////////
	////  EVENT REACTIONS    ////
	/////////////////////////////
	private boolean isAbleToReact(String rate_uuid){
		
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || btnCancelOptimization == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerFinished(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_FINISHED) OptimizationInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(isFromType(info.getType()))return;
		
		btnCancelOptimization.setEnabled(false);
		btnSave.setEnabled(true);
		progressBarOptimizationStep.setSelection(0);
		progressBarOptimizationStep.setEnabled(false);
		
		//Save the Optimization results
		logger.info("Number of best results: "+info.getBestIndividuals().size());
		for(Object ind : info.getBestIndividuals() ){
			
			if(ind instanceof Individual<?, ?>){
				Individual<?, ?> i=(Individual<?, ?>) ind;
				if(i.g instanceof double[]){
					ResultEntity ent=new ResultEntity((double[]) i.g);
					rate.getOptResultsMap().get(info.getType()).addResult(ent);
				}
			}
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerStarted(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_STARTED) OptimizationInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(isFromType(info.getType()))return;
		
		btnCancelOptimization.setEnabled(true);
		btnSave.setEnabled(false);
		progressBarOptimizationStep.setEnabled(true);
		progressBarOptimizationStep.setMaximum(info.getMaximum());
		progressBarOptimizationStep.setSelection(0);
		this.getLastSerie().clear();
		
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerNewStep(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_STEP) OptimizationInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(isFromType(info.getType()))return;
		
		if(progressBarOptimizationStep !=null && !progressBarOptimizationStep.isDisposed()){
			if(progressBarOptimizationStep.isEnabled()){
			
				progressBarOptimizationStep.setSelection(info.getMaximum()-info.getStep()-1);
				this.getLastSerie().add(info.getMaximum()-info.getStep(), info.getBest().v);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerNewBest(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(isFromType(info.getType()))return;
		
		if(progressBarOptimizationStep !=null && !progressBarOptimizationStep.isDisposed()){
			if(progressBarOptimizationStep.isEnabled()){
			
				progressBarOptimizationStep.setSelection(info.getMaximum()-info.getStep()-1);
				this.getLastSerie().addOrUpdate(info.getMaximum()-info.getStep(), info.getBest().v);
			}
		}
	}
	
	
	
	
	/////////////////////////////
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	public static MPart openOptimizationErrorPart(
			ExchangeRate rate,
			Job optimizer,
			String optimizationType,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			IEclipseContext context){
		
		MPart part=searchPart(OptimizationErrorPart.OPTIMIZATION_ERROR_EDITOR_ID,optimizationType,rate.getUUID(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setOptimizationErrorEditorPartContext(part,rate,optimizer,context);
			}
			
				partService.bringToTop(part);
				return  part;
		}
		
		
		//Create the part
		part=createRateEditorPart(rate,optimizer,optimizationType,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	
	private static MPart createRateEditorPart(ExchangeRate rate,Job optimizer,String optimizationType,EPartService partService,IEclipseContext context){
		MPart part = partService.createPart(OptimizationErrorPart.OPTIMIZATION_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(optimizationType+" "+rate.getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(rate.getUUID());
		part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setOptimizationErrorEditorPartContext(part,rate,optimizer,context);
		
		//OptimizationErrorPart p=(OptimizationErrorPart) part;
		//p.setType(Optimizer.stringToOptimizationType(optimizationType));
		
		return part;
	}
	
	
	private static void setOptimizationErrorEditorPartContext(MPart part,ExchangeRate rate,Job optimizer,IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(ExchangeRate.class, rate);
		part.getContext().set(Job.class, optimizer);
		part.getContext().set(MDirtyable.class, new MyMDirtyable(part));
	}
	
	
	private static MPart searchPart(String partId,String optimizationType,String tag,EModelService modelService,MApplication application){
		
		List<MPart> parts=getPartList(partId,optimizationType,tag,modelService, application);
		if(parts.isEmpty())return null;
		return parts.get(0);
	}
	
	private static List<MPart> getPartList(String partId,String optimizationType,String tag,EModelService modelService,MApplication application){
		List<String> tags=new LinkedList<String>();
		tags.add(tag);
		tags.add(optimizationType);
			
		List<MPart> parts=modelService.findElements(application,
				partId, MPart.class,tags );
		return parts;
	}
	
	
	
}