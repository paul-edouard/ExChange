package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.Optimizer;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.job.objectivefunc.ParabolicSARObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

public class RateChartParabolicSAR extends Composite {
	
	
	private static Logger logger = Logger.getLogger(RateChartMovingAverageComposite.class);
	
	@Inject
	IEclipseContext context;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	EPartService partService;
	
	@Inject
	private MApplication application;
	
	@Inject
	private Shell shell;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private ExchangeRate rate;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	
	//Renderers
	private XYLineAndShapeRenderer mainPlotRenderer;
	private XYLineAndShapeRenderer secondPlotrenderer;
	
	//Series Collections
	private XYSeriesCollection mainCollection;
	private XYSeriesCollection secondCollection;
	
	//TODO set the period and max profit
	private int[] period=new int[2];
	private float maxProfit=0;
	
	
	private double accFacLimit = 10;
	private double maxAccFacLimit = 40;
	
	private double accelerator=0.02;
	private double maxAccelerator=0.5;
	
	private ParabolicSARObjFunc parabolicSARObjFunc;
	
	private Optimizer<double[]> optimizer = new Optimizer<double[]>();
	private Text acceleratorTextext;
	private Button btnParabilicSAR;
	private Label lblBandFactor;
	private Slider acceleratorSlider;
	private Composite OptButtons;
	private Button btnLoad;
	private Button btnOpt;
	private Label lblNewLabel;
	private Button btnReset;
	private Label lblProfit;
	private Label bollingerBandProfitlbl;
	private Label lblDamperd;
	private Text accFacLimitText;
	private Slider accFacLimitSlider;
	
	@Inject
	public RateChartParabolicSAR(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		btnParabilicSAR = new Button(this, SWT.CHECK);
		btnParabilicSAR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				acceleratorSlider.setEnabled(btnParabilicSAR.getSelection());
				accFacLimitSlider.setEnabled(btnParabilicSAR.getSelection());
				
				btnReset.setEnabled(btnParabilicSAR.getSelection());
				//btnLoad.setEnabled(btnBollingerBands.getSelection());
				btnOpt.setEnabled(btnParabilicSAR.getSelection());
				
				resetChartDataSet();
				
				if(!btnParabilicSAR.getSelection())
					fireCollectionRemoved();
				
			}
		});
		btnParabilicSAR.setText("Parabolic SAR");
		new Label(this, SWT.NONE);
		
		
		////////////////////////////////
		//           BUTTONS          //
		////////////////////////////////
		
		OptButtons = new Composite(this, SWT.NONE);
		GridLayout gl_OptButtons = new GridLayout(7, false);
		gl_OptButtons.verticalSpacing = 1;
		gl_OptButtons.marginWidth = 1;
		gl_OptButtons.marginHeight = 1;
		gl_OptButtons.horizontalSpacing = 0;
		OptButtons.setLayout(gl_OptButtons);
		OptButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		btnReset = new Button(OptButtons, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				accFacLimit = 10;
				accelerator=0.02;
				
				reset();
				
			}
		});
		btnReset.setText("Reset");
		btnReset.setEnabled(false);
		
		lblNewLabel = new Label(OptButtons, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnLoad = new Button(OptButtons, SWT.NONE);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				double[] g=rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().getFirst().getDoubleArray();
				
				getObjFunc().setPeriod(period);
				double v=getObjFunc().compute(g, null);
				resetGuiData(g,v);
				
			}
		});
		btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnLoad.setText("Load");
		btnLoad.setEnabled(false);
		
		btnOpt = new Button(OptButtons, SWT.NONE);
		btnOpt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int dimension=6;
				getObjFunc().setPeriod(period);
				
				
				final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));
				
				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						getObjFunc(), gpm, dimension, 0, 1, rate.getOptResultsMap()
								.get(Type.PARABOLIC_SAR));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;
				
				
				//Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(
						rate,optimizer,
						OptimizationResults.OptimizationTypeToString(Type.PARABOLIC_SAR),
						partService, modelService, application, context);
				
				
				optimizer.initOptimizationInfo(eventBroker,Type.PARABOLIC_SAR,rate, wizard.getAlgorithm(), wizard.getTerm());
				optimizer.schedule();
				
				btnOpt.setEnabled(false);
				btnParabilicSAR.setEnabled(false);
				
				
				
			}
		});
		btnOpt.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnOpt.setText("Opt.");
		btnOpt.setEnabled(false);
		new Label(OptButtons, SWT.NONE);
		new Label(OptButtons, SWT.NONE);
		new Label(OptButtons, SWT.NONE);
		
		lblBandFactor = new Label(this, SWT.NONE);
		lblBandFactor.setText("Accelerator [alpha]:");
		
		acceleratorTextext = new Text(this, SWT.BORDER);
		acceleratorTextext.setEditable(false);
		acceleratorTextext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		acceleratorTextext.setText(String.format("%,.1f%%",  accelerator));
		
		acceleratorSlider = new Slider(this, SWT.NONE);
		acceleratorSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		acceleratorSlider.setThumb(1);
		acceleratorSlider.setPageIncrement(1);
		acceleratorSlider.setEnabled(false);
		acceleratorSlider.setMaximum((int)( 1000 *maxAccelerator) );
		acceleratorSlider.setMinimum(1);
		acceleratorSlider.setSelection((int)(accelerator*1000));
		acceleratorSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				accelerator=(((double) acceleratorSlider.getSelection())/1000.0);
				acceleratorTextext.setText(String.format("%,.1f%%",  accelerator));
				
				if(btnParabilicSAR.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		lblDamperd = new Label(this, SWT.NONE);
		lblDamperd.setText("Acc. Fac. Limit [k]:");
		
		accFacLimitText = new Text(this, SWT.BORDER);
		accFacLimitText.setEditable(false);
		accFacLimitText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		accFacLimitText.setText(String.valueOf((int)accFacLimit));
		
		accFacLimitSlider = new Slider(this, SWT.NONE);
		accFacLimitSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		accFacLimitSlider.setThumb(1);
		accFacLimitSlider.setPageIncrement(1);
		accFacLimitSlider.setEnabled(false);
		accFacLimitSlider.setMaximum((int)( maxAccFacLimit) );
		accFacLimitSlider.setMinimum(1);
		accFacLimitSlider.setSelection((int)(accFacLimit));
		accFacLimitSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				accFacLimit=(((double) accFacLimitSlider.getSelection()));
				accFacLimitText.setText(String.valueOf((int)accFacLimit));
				
				if(btnParabilicSAR.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		lblProfit = new Label(this, SWT.NONE);
		lblProfit.setText("Profit:");
		new Label(this, SWT.NONE);
		
		bollingerBandProfitlbl = new Label(this, SWT.NONE);
		bollingerBandProfitlbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		bollingerBandProfitlbl.setText("000.00%");
		
		
	}
	
	public void setRenderers(XYLineAndShapeRenderer mainPlotRenderer,XYLineAndShapeRenderer secondPlotrenderer){
		this.mainPlotRenderer=mainPlotRenderer;
		this.secondPlotrenderer=secondPlotrenderer;
	}
	
	public void setSeriesCollections(XYSeriesCollection mainCollection,XYSeriesCollection secondCollection){
		this.mainCollection=mainCollection;
		this.secondCollection=secondCollection;
	}

	public void setPeriodandMaxProfit(int[] period,float maxProfit){
		this.period=period;
		this.maxProfit=maxProfit;
		
		resetChartDataSet();
	}
	
	
	/////////////////////////////
	////  EVENT REACTIONS    ////
	/////////////////////////////
	private boolean isCompositeAbleToReact(String rate_uuid){
		if (this.isDisposed())
			return false;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || rate == null || bollingerBandProfitlbl == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	private void resetGuiData(double[] g,double v){
		
		
		//Upper
		accelerator=g[1]*maxAccelerator;
		accFacLimit=Math.round(g[1]*maxAccFacLimit);
		if(accFacLimit<1)
			accFacLimit=1;
		
		//Profit
		float profit=maxProfit- (float)v;
		String movAvgProfitString = String.format("%,.2f%%",
				profit * 100);
		bollingerBandProfitlbl.setText(movAvgProfitString);
		
		reset();
		
	}
	
	private void reset(){
		
		acceleratorTextext.setText(String.format("%,.1f%%",  accelerator));
		acceleratorSlider.setSelection((int)(accelerator*1000));
		accFacLimitText.setText(String.valueOf((int)accFacLimit));
		accFacLimitSlider.setSelection((int)(accFacLimit));
		
		fireCollectionRemoved();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().isEmpty()){
			double[] g=rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().getFirst().getDoubleArray();
			
			getObjFunc().setPeriod(period);
			double v=getObjFunc().compute(g, null);
			resetGuiData(g,v);
			
			btnLoad.setEnabled(true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerFinished(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_FINISHED) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.BILLINGER_BAND){
			btnParabilicSAR.setEnabled(true);
			btnOpt.setEnabled(true);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.BILLINGER_BAND){
			Individual<double[], double[]> individual=info.getBest();
			resetGuiData(individual.g,individual.v);
		}
		
	}
	
	/**
	 * create the Bollinger Band function
	 * @return
	 */
	private ParabolicSARObjFunc getObjFunc(){
		if(parabolicSARObjFunc!=null)return parabolicSARObjFunc;
		
		parabolicSARObjFunc=new ParabolicSARObjFunc(
				 HistoricalPoint.FIELD_Close,
				 RateChart.PENALTY,
				 rate.getHistoricalData().getNoneEmptyPoints(),
				 maxAccFacLimit,
				 0,
				 maxProfit
				 );
		return parabolicSARObjFunc;
	}
	
	private void  clearCollections(){
		
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_MovingAverage_Upper);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_MovingAverage_Lower);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_MovingAverage);
		
		
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_UpperBand_Max);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_UpperBand_Min);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_LowerBand_Max);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_LowerBand_Min);
		
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_Buy_Signal);
		removeSerie(mainCollection,BollingerBandObjFunc.BollingerBand_Buy_Signal);
		
		
		removeSerie(secondCollection,BollingerBandObjFunc.BollingerBand_Profit);
			
	}
	
	private void removeSerie(XYSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}

	
	private void resetChartDataSet() {
		clearCollections();
		
		if(!btnParabilicSAR.getSelection())return;
		
		//Refresh the main plot
		double[] x=new double[6];
		x[0]=accelerator/maxAccelerator;
		x[1]=accFacLimit/maxAccFacLimit;
		
		
		getObjFunc().setPeriod(period);
		getObjFunc().compute(x, null);
		
		//Moving Average Series
		//addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getMovingAverageUpperSeries(),Color.GRAY);
		//addSeriesAsLine(mainPlotRenderer,mainCollection,getBollingerBandObjFunc().getMovingAverageLowerSeries(),Color.GRAY);
		addSeriesAsLine(mainPlotRenderer,mainCollection,getObjFunc().getMovingAverageSeries(),Color.GRAY);
		
		//Bands
		addSeriesAsLine(mainPlotRenderer,mainCollection,getObjFunc().getUpperBandMaxSeries(),Color.ORANGE);
		addSeriesAsLine(mainPlotRenderer,mainCollection,getObjFunc().getUpperBandMinSeries(),Color.ORANGE);
		
		addSeriesAsLine(mainPlotRenderer,mainCollection,getObjFunc().getLowerBandMaxSeries(),Color.WHITE);
		addSeriesAsLine(mainPlotRenderer,mainCollection,getObjFunc().getLowerBandMinSeries(),Color.WHITE);
		
		//Profit
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getObjFunc().getBuySignalSeries(),
				ShapeUtilities.createUpTriangle(5),Color.GREEN);
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getObjFunc().getSellSignalSeries(),
				ShapeUtilities.createDownTriangle(5),Color.RED);
		
		
		addSeriesAsLine(secondPlotrenderer,secondCollection,getObjFunc().getProfitSeries(),Color.WHITE);
		
		
		String movAvgProfitString = String.format("%,.2f%%",
				getObjFunc().getProfit() * 100);
		bollingerBandProfitlbl.setText(movAvgProfitString);
		
		
		
		
	}
	
	private void addSeriesAsLine(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Color color){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			rend.setSeriesShapesVisible(pos, false);
			rend.setSeriesLinesVisible(pos, true);
			rend.setSeriesStroke(pos,new BasicStroke(2.0f));
			rend.setSeriesPaint(pos, color);
		}
	}
	
	private void addSeriesAsShape(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Shape shape,Color color){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			
			rend.setSeriesShapesVisible(pos, true);
			rend.setSeriesLinesVisible(pos, false);
			rend.setSeriesShape(pos,shape);
			rend.setSeriesShapesFilled(pos, true);
			rend.setSeriesPaint(pos, color);
			rend.setSeriesOutlinePaint(pos, Color.BLACK);
			rend.setSeriesOutlineStroke(pos, new BasicStroke(1.0f));
			rend.setUseOutlinePaint(true);
		}
	}
	
	// ///////////////////////////
	// // LISTERNER ////
	// ///////////////////////////
	private List<CollectionRemovedListener> listeners = new LinkedList<CollectionRemovedListener>();
	
	

	public void addCollectionRemovedListener(CollectionRemovedListener l) {
		listeners.add(l);
	}

	public void removeCollectionRemovedListener(CollectionRemovedListener l) {
		listeners.remove(l);
	}

	private void fireCollectionRemoved() {
		for (CollectionRemovedListener l : listeners)
			l.CollectionRemoved();
	}
	
}
