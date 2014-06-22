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
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
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
import com.munch.exchange.job.objectivefunc.NMAWObjFunc;
import com.munch.exchange.job.objectivefunc.RelativeStrengthIndexObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

public class NMAWComposite extends Composite {
	
	public static final String NMAW="NMAW";
	
	
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
	private int period1;
	public static int maxPeriod1=200;
	public static int minPeriod1=1;
	private double facPeriod1;	//facPeriod1=period1/maxPeriod1
	
	private int period2;
	
	private double lamda;	//lamda=period1/period2
	private double facLamda;	//lamda=period1/period2
	public static double minLamda=2;
	public static double maxLamda=100;
	
	private int aroonPeriod;
	public static int maxAroonPeriod=50;
	public static int minAroonPeriod=1;
	private double facAroonPeriod;
	
	private NMAWObjFunc nMAWObjFunc;
	
	private Optimizer<double[]> optimizer = new Optimizer<double[]>();
	
	private Button rsiBtn;
	private XYSeries nmawSeries;
	private Composite OptButtons;
	private Button btnLoad;
	private Button btnOpt;
	private Label lblNewLabel;
	private Button btnReset;
	
	//Alpha
	private Label nmawLblPeriod1;
	private Slider nmawSliderPeriod1;
	//Upper Max
	private Label nmawLblLamda;
	private Slider nmawSliderLamda;
	//Upper Min Fac
	private Label nmawLblPeriod;
	private Slider nmawSliderArronPeriod;
	
	
	//Profit and limits
	private Label  lblProfit ;
	private Label  rsiProfitlbl;
	
	private Label  lblLimitType;
	private Label  lblLimitDouble;
	
	
	private void resetParamValues(){
		//Parameters
		period1=89;
		facPeriod1=(period1-minPeriod1)/((double)maxPeriod1);
		
		lamda=4.0;	//lamda=period1/period2
		facLamda=(lamda-minLamda)/(maxLamda);
		period2=(int) (period1/lamda);
		
		aroonPeriod=5;
		facAroonPeriod=(aroonPeriod-minAroonPeriod)/((double)maxAroonPeriod);
	}
	
	
	
	@Inject
	public NMAWComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		resetParamValues();
		
		this.setLayout(new GridLayout(3, false));
		
		rsiBtn = new Button(this, SWT.CHECK);
		rsiBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nmawSliderPeriod1.setEnabled(rsiBtn.getSelection());
				nmawSliderLamda.setEnabled(rsiBtn.getSelection());
				nmawSliderArronPeriod.setEnabled(rsiBtn.getSelection());
				
				
				btnReset.setEnabled(rsiBtn.getSelection());
				btnOpt.setEnabled(rsiBtn.getSelection());
				
				resetChartDataSet();
				
				if(!rsiBtn.getSelection())
					fireCollectionRemoved();
				
			}
		});
		rsiBtn.setText("NMAW");
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
				
				reset();

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
				
				
				double[] g = rate.getOptResultsMap().get(Type.RELATIVE_STRENGTH_INDEX)
						.getResults().getFirst().getDoubleArray();

				getObjFunc().setPeriod(period);
				double v = getObjFunc().compute(g, null);
				resetGuiData(g, v);
				
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
				
				
				int dimension = 6;
				getObjFunc().setPeriod(period);

				final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));

				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						getObjFunc(), gpm, dimension, 0, 1, rate
								.getOptResultsMap().get(Type.NMAW));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;

				// Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(rate,
						optimizer, OptimizationResults
								.OptimizationTypeToString(Type.NMAW),
						partService, modelService, application, context);

				optimizer.initOptimizationInfo(eventBroker, Type.NMAW,
						rate, wizard.getAlgorithm(), wizard.getTerm());
				optimizer.schedule();

				btnOpt.setEnabled(false);
				rsiBtn.setEnabled(false);
				

			}
		});
		btnOpt.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1));
		btnOpt.setText("Opt.");
		btnOpt.setEnabled(false);
		new Label(OptButtons, SWT.NONE);
		new Label(OptButtons, SWT.NONE);
		new Label(OptButtons, SWT.NONE);
		
		////////////////////////////////
		//        Period 1            //
		////////////////////////////////
		
		Label lblPeriod1 = new Label(this, SWT.NONE);
		lblPeriod1.setText("Period 1:");
		
		nmawLblPeriod1 = new Label(this, SWT.NONE);
		nmawLblPeriod1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		nmawLblPeriod1.setText(String.valueOf(period1));
		
		nmawSliderPeriod1 = new Slider(this, SWT.NONE);
		nmawSliderPeriod1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nmawSliderPeriod1.setMaximum(maxPeriod1);
		nmawSliderPeriod1.setMinimum(minPeriod1);
		nmawSliderPeriod1.setSelection(period1);
		nmawSliderPeriod1.setEnabled(false);
		nmawSliderPeriod1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				period1=nmawSliderPeriod1.getSelection();
				String alphaStr = String.valueOf(period1);
				nmawLblPeriod1.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		
		////////////////////////////////
		//            lamda          //
		////////////////////////////////
		Label lblLamda = new Label(this, SWT.NONE);
		lblLamda.setText("Lamda:");
		
		nmawLblLamda = new Label(this, SWT.NONE);
		nmawLblLamda.setText(String.format("%,.2f%%",  lamda));
		
		nmawSliderLamda = new Slider(this, SWT.NONE);
		nmawSliderLamda.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nmawSliderLamda.setMaximum((int)maxLamda*10);
		nmawSliderLamda.setMinimum((int)minLamda*10);
		nmawSliderLamda.setSelection((int) lamda*10);
		nmawSliderLamda.setEnabled(false);
		nmawSliderLamda.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lamda=(((double)nmawSliderLamda.getSelection())/10);
				String alphaStr = String.format("%.2f", lamda);
				nmawLblLamda.setText(alphaStr.replace(",", "."));
				if(rsiBtn.isEnabled())
					fireCollectionRemoved();
				
			}
		});
		
		////////////////////////////////
		//            Aroon           //
		////////////////////////////////
		Label lblAroonPeriod = new Label(this, SWT.NONE);
		lblAroonPeriod.setText("Aroon period:");
		
		nmawLblPeriod = new Label(this, SWT.NONE);
		nmawLblPeriod.setText( String.valueOf(aroonPeriod));
		
		nmawSliderArronPeriod = new Slider(this, SWT.NONE);
		nmawSliderArronPeriod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nmawSliderArronPeriod.setMaximum(maxAroonPeriod);
		nmawSliderArronPeriod.setMinimum(minAroonPeriod);
		nmawSliderArronPeriod.setSelection(aroonPeriod);
		nmawSliderArronPeriod.setEnabled(false);
		nmawSliderArronPeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				aroonPeriod=nmawSliderArronPeriod.getSelection();
				String alphaStr = String.valueOf(aroonPeriod);
				nmawLblPeriod.setText(alphaStr.replace(",", "."));
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
		facPeriod1=g[0];
		facLamda=g[1];
		facAroonPeriod=g[2];
		
		period1=(int) (facPeriod1*maxPeriod1+minPeriod1);
		lamda=facLamda*maxLamda+minLamda;
		period2=(int) (period1/lamda);
		aroonPeriod=(int)(facAroonPeriod*maxAroonPeriod+minAroonPeriod);
		

		//Profit
		float profit=maxProfit- (float)v;
		String movAvgProfitString = String.format("%,.2f%%",
				profit * 100);
		rsiProfitlbl.setText(movAvgProfitString);
		
		reset();
		
	}
	
	private void reset(){
		
		nmawLblPeriod1.setText(String.valueOf(period1));
		nmawSliderPeriod1.setSelection(period1);
		
		nmawLblLamda.setText(String.format("%.2f", lamda));
		nmawSliderLamda.setSelection((int)lamda*10);
		
		nmawLblPeriod.setText(String.valueOf(aroonPeriod));
		nmawSliderArronPeriod.setSelection(aroonPeriod);
		
		
		fireCollectionRemoved();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.NMAW).getResults().isEmpty()){
			double[] g=rate.getOptResultsMap().get(Type.NMAW).getResults().getFirst().getDoubleArray();
			
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
		
		if(info.getType()==Type.NMAW){
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
		
		if(info.getType()==Type.NMAW){
			Individual<double[], double[]> individual=info.getBest();
			resetGuiData(individual.g,individual.v);
		}
		
	}
	
	/**
	 * create the Bollinger Band function
	 * @return
	 */
	private NMAWObjFunc getObjFunc(){
		if(nMAWObjFunc!=null)return nMAWObjFunc;
		
		nMAWObjFunc=new NMAWObjFunc(
				 HistoricalPoint.FIELD_Close,
				 RateChart.PENALTY,
				 rate.getHistoricalData().getNoneEmptyPoints(),
				 maxProfit
				 );
		return nMAWObjFunc;
	}	
	
	
	
	private void  clearCollections(){
		
		removeSerie(mainCollection,NMAWObjFunc.NMAW);
		removeSerie(mainCollection,NMAWObjFunc.NMAW_GD);
		
		
		removeSerie(mainCollection,NMAWObjFunc.NMAW_Buy_Signal);
		removeSerie(mainCollection,NMAWObjFunc.NMAW_Sell_Signal);
		
		
		removeDevSerie(deviationPercentCollection,NMAWObjFunc.NMAW_UpperBand);
		removeDevSerie(deviationPercentCollection,NMAWObjFunc.NMAW_LowerBand);
		
		
		removeSerie(secondCollection,NMAWObjFunc.NMAW_Profit);
		removeSerie(percentCollection,NMAWObjFunc.NMAW_DITF);
		
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
		
		
		facPeriod1=(period1-minPeriod1)/((double)maxPeriod1);
		facLamda=(lamda-minLamda)/(maxLamda);
		facAroonPeriod=(aroonPeriod-minAroonPeriod)/((double)maxAroonPeriod);
		
		//Refresh the main plot
		double[] x=new double[3];
		x[0]=facPeriod1;
		x[1]=facLamda;
		x[2]=facAroonPeriod;
		
				
		getObjFunc().setPeriod(period);
		getObjFunc().compute(x, null);
		
		
		if(rsiBtn.getSelection()){
			nmawSeries=getObjFunc().getNmawSeries();
			//mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(nmawSeries,period));
			
			mainCollection.addSeries(nmawSeries);
			int pos=mainCollection.indexOf(NMAWObjFunc.NMAW);
			if(pos>=0){
				mainPlotRenderer.setSeriesShapesVisible(pos, false);
				mainPlotRenderer.setSeriesLinesVisible(pos, true);
				mainPlotRenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				mainPlotRenderer.setSeriesPaint(pos, Color.DARK_GRAY);
			}
			
			mainCollection.addSeries(getObjFunc().getNmawGDSeries());
			pos=mainCollection.indexOf(NMAWObjFunc.NMAW_GD);
			if(pos>=0){
				mainPlotRenderer.setSeriesShapesVisible(pos, false);
				mainPlotRenderer.setSeriesLinesVisible(pos, true);
				mainPlotRenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				mainPlotRenderer.setSeriesPaint(pos, Color.RED);
			}
			
			percentCollection.addSeries(getObjFunc().getDitfSeries());
			pos=percentCollection.indexOf(NMAWObjFunc.NMAW_DITF);
			if(pos>=0){
				percentPlotrenderer.setSeriesShapesVisible(pos, false);
				percentPlotrenderer.setSeriesLinesVisible(pos, true);
				percentPlotrenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				percentPlotrenderer.setSeriesPaint(pos, Color.DARK_GRAY);
			}
			
			//logger.info("Get number of points: "+getObjFunc().getUpperBandSeries().getItemCount());
			/*
			addDeviationSerie(deviationPercentPlotRenderer,deviationPercentCollection,
					getObjFunc().getUpperBandSeries(),new Color(150,150,150));
			addDeviationSerie(deviationPercentPlotRenderer,deviationPercentCollection,
					getObjFunc().getLowerBandSeries(),Color.WHITE);
					*/
			
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
			
			/*
			lblLimitType.setText("No Limit");
			lblLimitDouble.setText("[000.000-000.000]");
			*/
			if(getObjFunc().isBought()){
				lblLimitType.setText("Sell:");
				
				String daySellLimits_str="["+
						String.format("%,.3f",getObjFunc().getDaySellUpLimit())+
						((getObjFunc().isDaySellUpLimitIsActivated())?"*":"")+
						"-"+
						String.format("%,.3f",getObjFunc().getDaySellDownLimit())+
						((getObjFunc().isDaySellDownLimitIsActivated())?"*":"")+
						"]";
				
				lblLimitDouble.setText(daySellLimits_str);
				
			}
			else{
				lblLimitType.setText("Buy:");
				
				String dayBuyLimits_str="["+
						String.format("%,.3f",getObjFunc().getDayBuyUpLimit())+
						((getObjFunc().isDayBuyUpLimitIsActivated())?"*":"")+
						"-"+
						String.format("%,.3f",getObjFunc().getDayBuyDownLimit())+
						((getObjFunc().isDayBuyDownLimitIsActivated())?"*":"")+
						"]";
				
				lblLimitDouble.setText(dayBuyLimits_str);
			}
			
			
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