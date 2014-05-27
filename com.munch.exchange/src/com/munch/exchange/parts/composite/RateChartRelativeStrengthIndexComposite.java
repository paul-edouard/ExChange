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
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.Optimizer;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.job.objectivefunc.MacdObjFunc;
import com.munch.exchange.job.objectivefunc.RelativeStrengthIndexObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

public class RateChartRelativeStrengthIndexComposite extends Composite {
	
	public static final String RELATIVE_STRENGTH_INDEX="Relative strength index";
	
	
	private static Logger logger = Logger.getLogger(RateChartEMAComposite.class);
	
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
	private DeviationRenderer deviationPercentPlotRenderer;
	private XYLineAndShapeRenderer percentPlotrenderer;
	
	
	//Series Collections
	private XYSeriesCollection mainCollection;
	private XYSeriesCollection secondCollection;
	private YIntervalSeriesCollection deviationPercentCollection;
	private XYSeriesCollection percentCollection;
	
	// set the period and max profit
	private int[] period=new int[2];
	private float maxProfit=0;
	
	
	//Parameters
	private double alpha=0.0714;
	
	private double upperMax=0.8;
	private double upperMinFac=0.75;
	
	//to calculate as 1-lowerMin
	private double lowerMin=0.8;
	private double lowerMaxFac=0.75;
	
	private RelativeStrengthIndexObjFunc relativeStrengthIndexObjFunc;
	
	private Button rsiBtn;
	private XYSeries rsiSeries;
	private Composite OptButtons;
	private Button btnLoad;
	private Button btnOpt;
	private Label lblNewLabel;
	private Button btnReset;
	
	//Alpha
	private Label rsiLblAlpha;
	private Slider rsiSlider;
	//Upper Max
	private Label rsiLblUpperMax;
	private Slider rsiSliderUpperMax;
	//Upper Min Fac
	private Label rsiLblUpperMinFac;
	private Slider rsiSliderUpperMinFac;
	
	//Lower Min
	private Label rsiLblLowerMin;
	private Slider rsiSliderLowerMin;
	//Lower Max Fac
	private Label rsiLblLowerMaxFac;
	private Slider rsiSliderLowerMaxFac;
	
	//Profit and limits
	private Label  lblProfit ;
	private Label  rsiProfitlbl;
	
	private Label  lblLimitType;
	private Label  lblLimitDouble;
	
	
	private void resetParamValues(){
		//Parameters
		alpha=0.0714;
		upperMax=0.8;
		upperMinFac=0.75;
		lowerMin=0.8;
		lowerMaxFac=0.75;
	}
	
	
	@Inject
	public RateChartRelativeStrengthIndexComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		this.setLayout(new GridLayout(3, false));
		
		rsiBtn = new Button(this, SWT.CHECK);
		rsiBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rsiSlider.setEnabled(rsiBtn.getSelection());
				rsiSliderLowerMaxFac.setEnabled(rsiBtn.getSelection());
				rsiSliderLowerMin.setEnabled(rsiBtn.getSelection());
				rsiSliderUpperMax.setEnabled(rsiBtn.getSelection());
				rsiSliderUpperMinFac.setEnabled(rsiBtn.getSelection());
				//if(emaBtn.getSelection())
				resetChartDataSet();
				
				if(!rsiBtn.getSelection())
					fireCollectionRemoved();
				
			}
		});
		rsiBtn.setText("RSI");
		new Label(this, SWT.NONE);
		
		// //////////////////////////////
		// BUTTONS //
		// //////////////////////////////

		OptButtons = new Composite(this, SWT.NONE);
		GridLayout gl_OptButtons = new GridLayout(7, false);
		gl_OptButtons.verticalSpacing = 1;
		gl_OptButtons.marginWidth = 1;
		gl_OptButtons.marginHeight = 1;
		gl_OptButtons.horizontalSpacing = 0;
		OptButtons.setLayout(gl_OptButtons);
		OptButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		btnReset = new Button(OptButtons, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetParamValues();
				
				//TODO Reset Gui
				//reset();

			}
		});
		btnReset.setText("Reset");
		btnReset.setEnabled(false);

		lblNewLabel = new Label(OptButtons, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		btnLoad = new Button(OptButtons, SWT.NONE);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				/*
				double[] g = rate.getOptResultsMap().get(Type.BILLINGER_BAND)
						.getResults().getFirst().getDoubleArray();

				getObjFunc().setPeriod(period);
				double v = getObjFunc().compute(g, null);
				resetGuiData(g, v);
				*/
			}
		});
		btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		btnLoad.setText("Load");
		btnLoad.setEnabled(false);

		btnOpt = new Button(OptButtons, SWT.NONE);
		btnOpt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				/*
				int dimension = 6;
				getObjFunc().setPeriod(period);

				final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));

				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						getObjFunc(), gpm, dimension, 0, 1, rate
								.getOptResultsMap().get(Type.PARABOLIC_SAR));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;

				// Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(rate,
						optimizer, OptimizationResults
								.OptimizationTypeToString(Type.PARABOLIC_SAR),
						partService, modelService, application, context);

				optimizer.initOptimizationInfo(eventBroker, Type.PARABOLIC_SAR,
						rate, wizard.getAlgorithm(), wizard.getTerm());
				optimizer.schedule();

				btnOpt.setEnabled(false);
				btnParabilicSAR.setEnabled(false);
				*/

			}
		});
		btnOpt.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1));
		btnOpt.setText("Opt.");
		btnOpt.setEnabled(false);
		
		////////////////////////////////
		//         Alpha              //
		////////////////////////////////
		
		Label lblAlpha = new Label(this, SWT.NONE);
		lblAlpha.setText("Alpha:");
		
		rsiLblAlpha = new Label(this, SWT.NONE);
		rsiLblAlpha.setText(String.format("%,.4f%%",  alpha));
		
		rsiSlider = new Slider(this, SWT.NONE);
		rsiSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rsiSlider.setMaximum(10000);
		rsiSlider.setMinimum(1);
		rsiSlider.setSelection((int)(alpha*10000));
		rsiSlider.setEnabled(false);
		rsiSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				alpha=(((double)rsiSlider.getSelection())/10000);
				String alphaStr = String.format("%.4f", alpha);
				rsiLblAlpha.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		
		////////////////////////////////
		//         Upper Max          //
		////////////////////////////////
		Label lblUpperMax = new Label(this, SWT.NONE);
		lblUpperMax.setText("Upper Max:");
		
		rsiLblUpperMax = new Label(this, SWT.NONE);
		rsiLblUpperMax.setText(String.format("%.3f",  upperMax));
		
		rsiSliderUpperMax = new Slider(this, SWT.NONE);
		rsiSliderUpperMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rsiSliderUpperMax.setMaximum(1000);
		rsiSliderUpperMax.setMinimum(1);
		rsiSliderUpperMax.setSelection((int)(upperMax*1000));
		rsiSliderUpperMax.setEnabled(false);
		rsiSliderUpperMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				upperMax=(((double)rsiSliderUpperMax.getSelection())/1000);
				String alphaStr = String.format("%.3f", upperMax);
				rsiLblUpperMax.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		////////////////////////////////
		//         Upper Min Fac      //
		////////////////////////////////
		Label lblUpperMinFac = new Label(this, SWT.NONE);
		lblUpperMinFac.setText("Upper Min Fac:");
		
		rsiLblUpperMinFac = new Label(this, SWT.NONE);
		rsiLblUpperMinFac.setText(String.format("%.3f",  upperMinFac));
		
		rsiSliderUpperMinFac = new Slider(this, SWT.NONE);
		rsiSliderUpperMinFac.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rsiSliderUpperMinFac.setMaximum(1000);
		rsiSliderUpperMinFac.setMinimum(1);
		rsiSliderUpperMinFac.setSelection((int)(upperMinFac*1000));
		rsiSliderUpperMinFac.setEnabled(false);
		rsiSliderUpperMinFac.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				upperMinFac=(((double)rsiSliderUpperMinFac.getSelection())/1000);
				String alphaStr = String.format("%.3f", upperMinFac);
				rsiLblUpperMinFac.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		

		////////////////////////////////
		//         Lower Min          //
		////////////////////////////////
		Label lblLowerMin = new Label(this, SWT.NONE);
		lblLowerMin.setText("Lower Min:");
		
		rsiLblLowerMin = new Label(this, SWT.NONE);
		rsiLblLowerMin.setText(String.format("%.3f",  lowerMin));
		
		rsiSliderLowerMin = new Slider(this, SWT.NONE);
		rsiSliderLowerMin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rsiSliderLowerMin.setMaximum(1000);
		rsiSliderLowerMin.setMinimum(1);
		rsiSliderLowerMin.setSelection((int)(lowerMin*1000));
		rsiSliderLowerMin.setEnabled(false);
		rsiSliderLowerMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lowerMin=(((double)rsiSliderLowerMin.getSelection())/1000);
				String alphaStr = String.format("%.3f", lowerMin);
				rsiLblLowerMin.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		

		////////////////////////////////
		//       Lower Max Fac        //
		////////////////////////////////
		Label lblLowerMaxFac = new Label(this, SWT.NONE);
		lblLowerMaxFac.setText("Lower Max Fac:");
		
		rsiLblLowerMaxFac = new Label(this, SWT.NONE);
		rsiLblLowerMaxFac.setText(String.format("%.3f",  lowerMaxFac));
		
		rsiSliderLowerMaxFac = new Slider(this, SWT.NONE);
		rsiSliderLowerMaxFac.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rsiSliderLowerMaxFac.setMaximum(1000);
		rsiSliderLowerMaxFac.setMinimum(1);
		rsiSliderLowerMaxFac.setSelection((int)(lowerMaxFac*1000));
		rsiSliderLowerMaxFac.setEnabled(false);
		rsiSliderLowerMaxFac.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lowerMaxFac=(((double)rsiSliderLowerMaxFac.getSelection())/1000);
				String alphaStr = String.format("%.3f", lowerMaxFac);
				rsiLblLowerMaxFac.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		////////////////////////////////
		//     Profit and limits      //
		////////////////////////////////
		
		lblProfit = new Label(this, SWT.NONE);
		lblProfit.setText("Profit:");
		new Label(this, SWT.NONE);
		
		rsiProfitlbl = new Label(this, SWT.NONE);
		rsiProfitlbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		rsiProfitlbl.setText("000.000%");
		
		lblLimitType = new Label(this, SWT.NONE);
		lblLimitType.setText("No Limit");
		new Label(this, SWT.NONE);
		
		lblLimitDouble = new Label(this, SWT.NONE);
		lblLimitDouble.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblLimitDouble.setText("[000.000-000.000]");		
		
	}
	
	public Button getCheckButton(){
		return rsiBtn;
	}
	
	public void setRenderers(XYLineAndShapeRenderer mainPlotRenderer,
			DeviationRenderer errorPercentPlotRenderer,
			XYLineAndShapeRenderer percentPlotrenderer,
			XYLineAndShapeRenderer secondPlotrenderer){
		this.mainPlotRenderer=mainPlotRenderer;
		this.deviationPercentPlotRenderer=errorPercentPlotRenderer;
		this.percentPlotrenderer=percentPlotrenderer;
		this.secondPlotrenderer=secondPlotrenderer;
	}
	
	public void setSeriesCollections(XYSeriesCollection mainCollection,
			YIntervalSeriesCollection errorPercentCollection,
			XYSeriesCollection percentCollection,
			XYSeriesCollection secondCollection){
		this.mainCollection=mainCollection;
		this.deviationPercentCollection=errorPercentCollection;
		this.percentCollection=percentCollection;
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
		if (incoming == null || rate == null || rsiBtn == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	private void resetGuiData(double[] g,double v){
		
		
		//Parameters
		alpha=g[0];
		
		upperMax=g[1];
		upperMinFac=g[2];
		
		lowerMin=g[3];
		lowerMaxFac=g[4];
		
		
		//Profit
		float profit=maxProfit- (float)v;
		String movAvgProfitString = String.format("%,.2f%%",
				profit * 100);
		rsiProfitlbl.setText(movAvgProfitString);
		
		reset();
		
	}
	
	private void reset(){
		
		rsiLblAlpha.setText(String.format("%,.4f",  alpha));
		rsiSlider.setSelection((int)(alpha*10000));
		
		rsiLblUpperMax.setText(String.format("%,.3f",  upperMax));
		rsiSliderUpperMax.setSelection((int)(upperMax*1000));
		
		rsiLblUpperMinFac.setText(String.format("%,.3f",  upperMinFac));
		rsiSliderUpperMinFac.setSelection((int)(upperMinFac*1000));
		
		rsiLblLowerMin.setText(String.format("%,.3f",  lowerMin));
		rsiSliderLowerMin.setSelection((int)(lowerMin*1000));
		
		rsiLblLowerMaxFac.setText(String.format("%,.3f",  lowerMaxFac));
		rsiSliderLowerMaxFac.setSelection((int)(lowerMaxFac*1000));
		
		
		fireCollectionRemoved();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.RELATIVE_STRENGTH_INDEX).getResults().isEmpty()){
			double[] g=rate.getOptResultsMap().get(Type.RELATIVE_STRENGTH_INDEX).getResults().getFirst().getDoubleArray();
			
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
		
		if(info.getType()==Type.RELATIVE_STRENGTH_INDEX){
			rsiBtn.setEnabled(true);
			btnOpt.setEnabled(true);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		if(info.getType()==Type.RELATIVE_STRENGTH_INDEX){
			Individual<double[], double[]> individual=info.getBest();
			resetGuiData(individual.g,individual.v);
		}
		
	}
	
	/**
	 * create the Bollinger Band function
	 * @return
	 */
	private RelativeStrengthIndexObjFunc getObjFunc(){
		if(relativeStrengthIndexObjFunc!=null)return relativeStrengthIndexObjFunc;
		
		relativeStrengthIndexObjFunc=new RelativeStrengthIndexObjFunc(
				 HistoricalPoint.FIELD_Close,
				 RateChart.PENALTY,
				 rate.getHistoricalData().getNoneEmptyPoints(),
				 maxProfit
				 );
		return relativeStrengthIndexObjFunc;
	}	
	
	
	
	private void  clearCollections(){
		
		removeSerie(percentCollection,RELATIVE_STRENGTH_INDEX);
		
		
		removeSerie(mainCollection,RelativeStrengthIndexObjFunc.RSI_Buy_Signal);
		removeSerie(mainCollection,RelativeStrengthIndexObjFunc.RSI_Sell_Signal);
		
		
		removeDevSerie(deviationPercentCollection,RelativeStrengthIndexObjFunc.RSI_UpperBand);
		removeDevSerie(deviationPercentCollection,RelativeStrengthIndexObjFunc.RSI_LowerBand);
		
		
		removeSerie(secondCollection,RelativeStrengthIndexObjFunc.RSI_Profit);
		
	}
	
	private void removeSerie(XYSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}
	
	private void removeDevSerie(YIntervalSeriesCollection col,String name){
		int pos=col.indexOf(name);
		if(pos>=0)col.removeSeries(pos);
	}
	
	private void resetChartDataSet() {
		
		clearCollections();
		
		if(!rsiBtn.getSelection())return;
		
		//Refresh the main plot
		double[] x=new double[5];
		x[0]=alpha;
		x[1]=upperMax;
		x[2]=upperMinFac;
		x[3]=lowerMin;
		x[4]=lowerMaxFac;
				
				
		getObjFunc().setPeriod(period);
		getObjFunc().compute(x, null);
		
		
		if(rsiBtn.getSelection()){
			rsiSeries=rate.getHistoricalData().getRSI((float) alpha,RELATIVE_STRENGTH_INDEX);
			percentCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(rsiSeries,period));
			
			int pos=percentCollection.indexOf(RELATIVE_STRENGTH_INDEX);
			if(pos>=0){
				percentPlotrenderer.setSeriesShapesVisible(pos, false);
				percentPlotrenderer.setSeriesLinesVisible(pos, true);
				percentPlotrenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				percentPlotrenderer.setSeriesPaint(pos, Color.DARK_GRAY);
			}
			
			//logger.info("Get number of points: "+getObjFunc().getUpperBandSeries().getItemCount());
			
			addDeviationSerie(deviationPercentPlotRenderer,deviationPercentCollection,
					getObjFunc().getUpperBandSeries(),new Color(150,150,150));
			addDeviationSerie(deviationPercentPlotRenderer,deviationPercentCollection,
					getObjFunc().getLowerBandSeries(),Color.WHITE);
			
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
			rsiProfitlbl.setText(movAvgProfitString);
			
			
			
		}
	}
	
	private void addDeviationSerie(DeviationRenderer rend,YIntervalSeriesCollection col,YIntervalSeries series,Color color ){
		
		col.addSeries(series);
		int pos=col.indexOf(series.getKey());
		if(pos>=0){
			rend.setSeriesPaint(pos, color);
			rend.setSeriesStroke(pos, new BasicStroke(0.3f, BasicStroke.CAP_ROUND,
	                BasicStroke.JOIN_ROUND));
			rend.setSeriesFillPaint(pos, color);
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
