package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;
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
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.Optimizer;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.job.objectivefunc.MacdObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

public class RateChartMACDComposite extends Composite {
	
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
	
	//MACD
	private Button macdBtnCheck;
	private Slider macdSliderEmaFast;
	private Slider macdSliderEmaSlow;
	private Label macdLblEmaSlowAlpha;
	private Label macdLblEmaFastAlpha;
	
	private MacdObjFunc macdObjFunc;
	private double macdSlowAlpha=0;
	private double macdFastAlpha=0;
	private double macdSignalAlpha=0;
	
	private Optimizer<double[]> macdOptimizer=new Optimizer<double[]>();
	
	private Slider macdSliderSignalAlpha;
	private Label macdLblSignalAlpha;
	private Button macdbtnOpt;
	private Label lblProfit;
	private Label macdLblProfit;
	
	
	@Inject
	public RateChartMACDComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		
		this.setLayout(new GridLayout(3, false));
		
		macdBtnCheck = new Button(this, SWT.CHECK);
		macdBtnCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				macdSliderEmaFast.setEnabled(macdBtnCheck.getSelection());
				macdSliderEmaSlow.setEnabled(macdBtnCheck.getSelection());
				macdSliderSignalAlpha.setEnabled(macdBtnCheck.getSelection());
				macdbtnOpt.setEnabled(macdBtnCheck.getSelection());
				
				resetChartDataSet();
				
				if(!macdBtnCheck.getSelection())
					fireCollectionRemoved();
				
			}
		});
		macdBtnCheck.setText("MACD");
		new Label(this, SWT.NONE);
		
		macdbtnOpt = new Button(this, SWT.NONE);
		macdbtnOpt.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				final IGPM<double[], double[]> gpm = ((IGPM) (IdentityMapping.IDENTITY_MAPPING));

				
				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						getMacdObjFunc(), gpm, 3, 0.0d, 1.0d,rate.getOptResultsMap().get(Type.MACD));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;
				
				//Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(
						rate,macdOptimizer,
						OptimizationResults.OptimizationTypeToString(Type.MACD),
						partService, modelService, application, context);
				
				
				macdOptimizer.initOptimizationInfo(eventBroker,Type.MACD,rate, wizard.getAlgorithm(), wizard.getTerm());
				macdOptimizer.schedule();
				
				macdbtnOpt.setEnabled(false);
				macdBtnCheck.setEnabled(false);
				
			}
		});
		macdbtnOpt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macdbtnOpt.setText("Opt.");
		macdbtnOpt.setEnabled(false);
		
		Label lblEmaFast = new Label(this, SWT.NONE);
		lblEmaFast.setText("EMA Fast");
		
		macdLblEmaFastAlpha = new Label(this, SWT.NONE);
		macdLblEmaFastAlpha.setText("0.846");
		
		macdSliderEmaFast = new Slider(this, SWT.NONE);
		macdSliderEmaFast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderEmaFast.getSelection())/1000);
				macdLblEmaFastAlpha.setText(alphaStr.replace(",", "."));
				macdFastAlpha=0;
				
				if(macdSliderEmaFast.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderEmaFast.setMaximum(1000);
		macdSliderEmaFast.setMinimum(1);
		macdSliderEmaFast.setSelection(846);
		macdSliderEmaFast.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblEmaSlow = new Label(this, SWT.NONE);
		lblEmaSlow.setText("EMA Slow");
		
		macdLblEmaSlowAlpha = new Label(this, SWT.NONE);
		macdLblEmaSlowAlpha.setText("0.926");
		
		macdSliderEmaSlow = new Slider(this, SWT.NONE);
		macdSliderEmaSlow.setThumb(1);
		macdSliderEmaSlow.setPageIncrement(1);
		macdSliderEmaSlow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderEmaSlow.getSelection())/1000);
				macdLblEmaSlowAlpha.setText(alphaStr.replace(",", "."));
				macdFastAlpha=0;
				if(macdSliderEmaSlow.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderEmaSlow.setMaximum(1000);
		macdSliderEmaSlow.setMinimum(1);
		macdSliderEmaSlow.setSelection(926);
		macdSliderEmaSlow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label macdlblSignal = new Label(this, SWT.NONE);
		macdlblSignal.setText("Signal");
		
		macdLblSignalAlpha = new Label(this, SWT.NONE);
		macdLblSignalAlpha.setText("0.800");
		
		macdSliderSignalAlpha = new Slider(this, SWT.NONE);
		macdSliderSignalAlpha.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macdSliderSignalAlpha.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String alphaStr = String.format("%.3f", ( (float) macdSliderSignalAlpha.getSelection())/1000);
				macdLblSignalAlpha.setText(alphaStr.replace(",", "."));
				macdFastAlpha=0;
				if(macdSliderSignalAlpha.isEnabled())
					resetChartDataSet();
			}
		});
		macdSliderSignalAlpha.setThumb(1);
		macdSliderSignalAlpha.setPageIncrement(1);
		macdSliderSignalAlpha.setMaximum(1000);
		macdSliderSignalAlpha.setSelection(800);
		
		lblProfit = new Label(this, SWT.NONE);
		lblProfit.setText("Profit");
		new Label(this, SWT.NONE);
		
		macdLblProfit = new Label(this, SWT.NONE);
		macdLblProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macdLblProfit.setText("0,0%");
	}
	
	public Button getCheckButton(){
		return macdBtnCheck;
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
	
	private MacdObjFunc getMacdObjFunc(){
		if(macdObjFunc!=null)return macdObjFunc;
		
		macdObjFunc = new MacdObjFunc(rate.getHistoricalData()
				.getXYSeries(HistoricalPoint.FIELD_Close), period,
				maxProfit, RateChart.PENALTY);
		return macdObjFunc;
	}
	
	private void  clearCollections(){
		
		int ema_fast_pos=mainCollection.indexOf(MacdObjFunc.Macd_EMA_Fast);
		if(ema_fast_pos>=0){
			mainCollection.removeSeries(ema_fast_pos);
		}
		
		int ema_slow_pos=mainCollection.indexOf(MacdObjFunc.Macd_EMA_Slow);
		if(ema_slow_pos>=0){
			mainCollection.removeSeries(ema_slow_pos);
		}
		
		int buy_pos = mainCollection.indexOf(MacdObjFunc.Macd_Buy_Signal);
		if (buy_pos >= 0){
			mainCollection.removeSeries(buy_pos);
		}
		
		int sell_pos = mainCollection.indexOf(MacdObjFunc.Macd_Sell_Signal);
		if (sell_pos >= 0){
			mainCollection.removeSeries(sell_pos);
		}
		
		int profit_pos=secondCollection.indexOf(MacdObjFunc.Macd_Profit);
		if(profit_pos>=0){
			secondCollection.removeSeries(profit_pos);
		}
	}
	
	private void resetChartDataSet() {
		
		clearCollections();
		
		if(!macdBtnCheck.getSelection())return;
		
		double[] x=new double[3];
		
		if(macdFastAlpha>0){
			x[0]=macdFastAlpha;
			x[1]=macdSlowAlpha;
			x[2]=macdSignalAlpha;
		}
		else{
			x[0]=Double.parseDouble(macdLblEmaFastAlpha.getText());
			x[1]=Double.parseDouble(macdLblEmaSlowAlpha.getText());
			x[2]=Double.parseDouble(macdLblSignalAlpha.getText());
			
		}
		
		getMacdObjFunc().compute(x, null);
			
		mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(getMacdObjFunc().getMacdEmaFastSeries(),period));
		mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(getMacdObjFunc().getMacdEmaSlowSeries(),period));
		
		int ema_fast_pos=mainCollection.indexOf(MacdObjFunc.Macd_EMA_Fast);
		if(ema_fast_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(ema_fast_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(ema_fast_pos, true);
			mainPlotRenderer.setSeriesStroke(ema_fast_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(ema_fast_pos, Color.CYAN);
		}
		int ema_slow_pos=mainCollection.indexOf(MacdObjFunc.Macd_EMA_Slow);
		if(ema_slow_pos>=0){
			mainPlotRenderer.setSeriesShapesVisible(ema_slow_pos, false);
			mainPlotRenderer.setSeriesLinesVisible(ema_slow_pos, true);
			mainPlotRenderer.setSeriesStroke(ema_slow_pos,new BasicStroke(2.0f));
			mainPlotRenderer.setSeriesPaint(ema_slow_pos, Color.ORANGE);
		}
		
		mainCollection.addSeries(getMacdObjFunc().getBuySignalSeries());
		mainCollection.addSeries(getMacdObjFunc().getSellSignalSeries());

		int buy_pos = mainCollection.indexOf(MacdObjFunc.Macd_Buy_Signal);
		logger.info(" try Setting buy Signal! "+buy_pos);
		if (buy_pos >= 0) {
			 logger.info("Setting buy Signal! "+buy_pos);
			mainPlotRenderer.setSeriesShapesVisible(buy_pos, true);
			mainPlotRenderer.setSeriesLinesVisible(buy_pos, false);
			mainPlotRenderer.setSeriesShape(buy_pos,
					ShapeUtilities.createUpTriangle(5));
			mainPlotRenderer.setSeriesShapesFilled(buy_pos, true);
			mainPlotRenderer.setSeriesPaint(buy_pos, Color.GREEN);
			mainPlotRenderer.setSeriesOutlinePaint(buy_pos, Color.BLACK);
			mainPlotRenderer.setSeriesOutlineStroke(buy_pos,
					new BasicStroke(1.0f));
			mainPlotRenderer.setUseOutlinePaint(true);
		}

		int sell_pos = mainCollection.indexOf(MacdObjFunc.Macd_Sell_Signal);
		logger.info(" try Setting sell Signal! "+sell_pos);
		if (sell_pos >= 0) {
			logger.info("Setting sell Signal! "+sell_pos);
			mainPlotRenderer.setSeriesShapesVisible(sell_pos, true);
			mainPlotRenderer.setSeriesLinesVisible(sell_pos, false);
			mainPlotRenderer.setSeriesShape(sell_pos,
					ShapeUtilities.createDownTriangle(5));
			mainPlotRenderer.setSeriesShapesFilled(sell_pos, true);
			mainPlotRenderer.setSeriesPaint(sell_pos, Color.RED);
			mainPlotRenderer.setSeriesOutlinePaint(sell_pos, Color.BLACK);
			mainPlotRenderer.setSeriesOutlineStroke(sell_pos,
					new BasicStroke(1.0f));
		}
		
			
		secondCollection.addSeries(getMacdObjFunc().getProfitSeries());
		
		String macdProfitString = String.format("%,.2f%%", getMacdObjFunc().getProfit()*100);
		macdLblProfit.setText(macdProfitString);
		
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
		if (incoming == null || rate == null || macdBtnCheck == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	private void resetMACDGuiData(double[] g, double v) {

		float macdProfit = (maxProfit - (float) v) * 100;
		// Fast Alpha
		macdSliderEmaFast.setSelection((int) (g[0] * 1000));
		macdSliderEmaSlow.setSelection((int) (g[1] * 1000));
		macdSliderSignalAlpha.setSelection((int) (g[2] * 1000));

		String alphaFast = String.format("%.3f", ((float) g[0]));
		String alphaSlow = String.format("%.3f", ((float) g[1]));
		String alphaSignal = String.format("%.3f", ((float) g[2]));

		macdLblEmaFastAlpha.setText(alphaFast.replace(",", "."));
		macdLblEmaSlowAlpha.setText(alphaSlow.replace(",", "."));
		macdLblSignalAlpha.setText(alphaSignal.replace(",", "."));

		macdFastAlpha = g[0];
		macdSlowAlpha = g[1];
		macdSignalAlpha = g[2];

		String macdProfitString = String.format("%,.2f%%", macdProfit);
		macdLblProfit.setText(macdProfitString);

		resetChartDataSet();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.MACD).getResults().isEmpty()){
			//	logger.info("Setting MACD Data!!");
				double[] g=rate.getOptResultsMap().get(Type.MACD).getResults().getFirst().getDoubleArray();
				
				double v=getMacdObjFunc().compute(g, null);
				resetMACDGuiData(g,v);
			}
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerFinished(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_FINISHED) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.MACD){
			this.macdbtnOpt.setEnabled(true);
			this.macdBtnCheck.setEnabled(true);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.MACD){
			Individual<double[], double[]> individual=info.getBest();
			resetMACDGuiData(individual.g,individual.v);
		}
		
	}
	
	/*
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerNewStep(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_STEP) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==OptimizationType.MACD){
			logger.info("New MACD Optimizer Step: "+info.getStep());
		}
		
	}
	*/
	/////////////////////////////
	////      LISTERNER      ////
	/////////////////////////////
	private List<CollectionRemovedListener> listeners=new LinkedList<CollectionRemovedListener>();

	
	public void addCollectionRemovedListener(CollectionRemovedListener l) {
		listeners.add(l);
	}

	public void removeCollectionRemovedListener(CollectionRemovedListener l) {
		listeners.remove(l);
	}
	
	private void fireCollectionRemoved(){
		for(CollectionRemovedListener l: listeners)
			l.CollectionRemoved();
	}
	
}
