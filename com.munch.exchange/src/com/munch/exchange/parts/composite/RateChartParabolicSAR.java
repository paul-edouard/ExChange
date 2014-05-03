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
	
	//set the period and max profit
	private int[] period=new int[2];
	private float maxProfit=0;
	
	
	private double accMaxIncrement = 10;
	private double maxAccMaxIncrement = 40;
	
	private double acceleratorStart=0.02;
	private double maxAcceleratorStart=0.5;
	
	private double acceleratorIncrement=0.02;
	private double maxAcceleratorIncrement=0.5;
	
	private ParabolicSARObjFunc parabolicSARObjFunc;
	
	private Optimizer<double[]> optimizer = new Optimizer<double[]>();
	private Text acceleratorStartText;
	private Button btnParabilicSAR;
	private Label lblacceleratorStart;
	private Label lblacceleratorInc;
	private Text acceleratorIncText;
	private Slider acceleratorStartSlider;
	private Slider acceleratorIncSlider;
	private Composite OptButtons;
	private Button btnLoad;
	private Button btnOpt;
	private Label lblNewLabel;
	private Button btnReset;
	private Label lblProfit;
	private Label parabilicSARProfitlbl;
	private Label lblaccMaxInc;
	private Text accMaxIncText;
	private Slider accMaxIncSlider;
	private Label lblLimitType;
	private Label lblLimitDouble;
	
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
				
				acceleratorStartSlider.setEnabled(btnParabilicSAR.getSelection());
				accMaxIncSlider.setEnabled(btnParabilicSAR.getSelection());
				acceleratorIncSlider.setEnabled(btnParabilicSAR.getSelection());
				
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
				
				accMaxIncrement = 10;
				acceleratorStart=0.02;
				acceleratorIncrement=0.02;
				
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

		////////////////////////////////
		//       Acc  Start           //
		////////////////////////////////
		
		lblacceleratorStart = new Label(this, SWT.NONE);
		lblacceleratorStart.setText("Start:");
		
		acceleratorStartText = new Text(this, SWT.BORDER);
		acceleratorStartText.setEditable(false);
		acceleratorStartText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		acceleratorStartText.setText(String.format("%,.3f%%",  acceleratorStart));
		
		acceleratorStartSlider = new Slider(this, SWT.NONE);
		acceleratorStartSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		acceleratorStartSlider.setThumb(1);
		acceleratorStartSlider.setPageIncrement(1);
		acceleratorStartSlider.setEnabled(false);
		acceleratorStartSlider.setMaximum((int)( 1000 *maxAcceleratorStart) );
		acceleratorStartSlider.setMinimum(1);
		acceleratorStartSlider.setSelection((int)(acceleratorStart*1000));
		acceleratorStartSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				acceleratorStart=(((double) acceleratorStartSlider.getSelection())/1000.0);
				acceleratorStartText.setText(String.format("%,.3f%%",  acceleratorStart));
				
				if(btnParabilicSAR.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		////////////////////////////////
		//       Acc  Inc.           //
		////////////////////////////////
		
		lblacceleratorInc = new Label(this, SWT.NONE);
		lblacceleratorInc.setText("Increment:");
		
		acceleratorIncText = new Text(this, SWT.BORDER);
		acceleratorIncText.setEditable(false);
		acceleratorIncText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		acceleratorIncText.setText(String.format("%,.3f%%",  acceleratorIncrement));
		
		acceleratorIncSlider = new Slider(this, SWT.NONE);
		acceleratorIncSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		acceleratorIncSlider.setThumb(1);
		acceleratorIncSlider.setPageIncrement(1);
		acceleratorIncSlider.setEnabled(false);
		acceleratorIncSlider.setMaximum((int)( 1000 *maxAcceleratorIncrement) );
		acceleratorIncSlider.setMinimum(1);
		acceleratorIncSlider.setSelection((int)(acceleratorIncrement*1000));
		acceleratorIncSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				acceleratorIncrement=(((double) acceleratorIncSlider.getSelection())/1000.0);
				acceleratorIncText.setText(String.format("%,.3f%%",  acceleratorIncrement));
				
				if(btnParabilicSAR.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		////////////////////////////////
		//       Acc Max Inc          //
		////////////////////////////////		
		lblaccMaxInc = new Label(this, SWT.NONE);
		lblaccMaxInc.setText("Max Increment [N]:");
		
		accMaxIncText = new Text(this, SWT.BORDER);
		accMaxIncText.setEditable(false);
		accMaxIncText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		accMaxIncText.setText(String.valueOf((int)accMaxIncrement));
		
		accMaxIncSlider = new Slider(this, SWT.NONE);
		accMaxIncSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		accMaxIncSlider.setThumb(1);
		accMaxIncSlider.setPageIncrement(1);
		accMaxIncSlider.setEnabled(false);
		accMaxIncSlider.setMaximum((int)( maxAccMaxIncrement) );
		accMaxIncSlider.setMinimum(1);
		accMaxIncSlider.setSelection((int)(accMaxIncrement));
		accMaxIncSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				accMaxIncrement=(((double) accMaxIncSlider.getSelection()));
				accMaxIncText.setText(String.valueOf((int)accMaxIncrement));
				
				if(btnParabilicSAR.isEnabled())
					fireCollectionRemoved();
			}
		});
		
		////////////////////////////////
		//       Acc Max Inc          //
		////////////////////////////////		

		lblProfit = new Label(this, SWT.NONE);
		lblProfit.setText("Profit:");
		new Label(this, SWT.NONE);
		
		parabilicSARProfitlbl = new Label(this, SWT.NONE);
		parabilicSARProfitlbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		parabilicSARProfitlbl.setText("000.000%");
		
		lblLimitType = new Label(this, SWT.NONE);
		lblLimitType.setText("No Limit");
		new Label(this, SWT.NONE);
		
		lblLimitDouble = new Label(this, SWT.NONE);
		lblLimitDouble.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblLimitDouble.setText("000.000");
		
		
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
		if (incoming == null || rate == null || parabilicSARProfitlbl == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	private void resetGuiData(double[] g,double v){
		
		
		//Acc
		acceleratorStart=g[0]*maxAcceleratorStart;
		acceleratorIncrement=g[1]*maxAcceleratorIncrement;
		accMaxIncrement=Math.round(g[2]*maxAccMaxIncrement);
		if(accMaxIncrement<1)
			accMaxIncrement=1;
		
		//Profit
		float profit=maxProfit- (float)v;
		String movAvgProfitString = String.format("%,.2f%%",
				profit * 100);
		parabilicSARProfitlbl.setText(movAvgProfitString);
		
		reset();
		
	}
	
	private void reset(){
		
		acceleratorStartText.setText(String.format("%,.3f",  acceleratorStart));
		acceleratorStartSlider.setSelection((int)(acceleratorStart*1000));
		
		acceleratorIncText.setText(String.format("%,.3f",  acceleratorIncrement));
		acceleratorIncSlider.setSelection((int)(acceleratorIncrement*1000));
		
		accMaxIncText.setText(String.valueOf((int)accMaxIncrement));
		accMaxIncSlider.setSelection((int)(accMaxIncrement));
		
		fireCollectionRemoved();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.PARABOLIC_SAR).getResults().isEmpty()){
			double[] g=rate.getOptResultsMap().get(Type.PARABOLIC_SAR).getResults().getFirst().getDoubleArray();
			
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
		
		if(info.getType()==Type.PARABOLIC_SAR){
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
		
		if(info.getType()==Type.PARABOLIC_SAR){
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
				 maxAcceleratorStart,
				 maxAcceleratorIncrement,
				 maxAccMaxIncrement,
				 maxProfit
				 );
		return parabolicSARObjFunc;
	}
	
	private void  clearCollections(){
		
		removeSerie(mainCollection,ParabolicSARObjFunc.Parabolic_SAR_Bullish_Trend);
		removeSerie(mainCollection,ParabolicSARObjFunc.Parabolic_SAR_Bearish_Trend);
		
		
		removeSerie(mainCollection,ParabolicSARObjFunc.Parabolic_SAR_Buy_Signal);
		removeSerie(mainCollection,ParabolicSARObjFunc.Parabolic_SAR_Sell_Signal);
		
		
		removeSerie(secondCollection,ParabolicSARObjFunc.Parabolic_SAR_Profit);
			
	}
	
	private void removeSerie(XYSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}

	
	private void resetChartDataSet() {
		clearCollections();
		
		if(!btnParabilicSAR.getSelection())return;
		
		//Refresh the main plot
		double[] x=new double[3];
		x[0]=acceleratorStart/maxAcceleratorStart;
		x[1]=acceleratorIncrement/maxAcceleratorIncrement;
		x[2]=accMaxIncrement/maxAccMaxIncrement;
		
		
		getObjFunc().setPeriod(period);
		getObjFunc().compute(x, null);
		
		
		//Parabolic
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getObjFunc().getBullishTrendSeries(),
				ShapeUtilities.createRegularCross(3f, 0.5f),Color.BLUE,false);
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getObjFunc().getBearishTrendSeries(),
				ShapeUtilities.createRegularCross(3f, 0.5f),Color.RED,false);
		
		//Profit
		
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getObjFunc().getBuySignalSeries(),
				ShapeUtilities.createUpTriangle(5),Color.GREEN,true);
		addSeriesAsShape(mainPlotRenderer,mainCollection,
				getObjFunc().getSellSignalSeries(),
				ShapeUtilities.createDownTriangle(5),Color.RED,true);
		
		
		addSeriesAsLine(secondPlotrenderer,secondCollection,getObjFunc().getProfitSeries(),Color.WHITE);
		
		
		String movAvgProfitString = String.format("%,.2f%%",
				getObjFunc().getProfit() * 100);
		parabilicSARProfitlbl.setText(movAvgProfitString);
		
		if(getObjFunc().getStartBuyLimit()>0){
			lblLimitType.setText("Start Buy:");
			lblLimitDouble.setText(String.format("%,.3f",getObjFunc().getStartBuyLimit()));
		}
		else{
			lblLimitType.setText("Stop Loss:");
			lblLimitDouble.setText(String.format("%,.3f",getObjFunc().getStopLossLimit()));
		}
		
		
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
	
	private void addSeriesAsShape(XYLineAndShapeRenderer rend,  XYSeriesCollection col, XYSeries series,Shape shape,Color color,boolean useOutlinePaint){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			
			rend.setSeriesShapesVisible(pos, true);
			rend.setSeriesLinesVisible(pos, false);
			rend.setSeriesShape(pos,shape);
			rend.setSeriesShapesFilled(pos, true);
			rend.setSeriesPaint(pos, color);
			if(useOutlinePaint){
				rend.setSeriesOutlinePaint(pos, Color.BLACK);
				rend.setSeriesOutlineStroke(pos, new BasicStroke(0.5f));
				rend.setUseOutlinePaint(useOutlinePaint);
			}
			else{
				rend.setSeriesOutlinePaint(pos, color);
			}
			
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
