package com.munch.exchange.parts.composite;

import java.awt.BasicStroke;
import java.awt.Color;

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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import com.munch.exchange.job.objectivefunc.MacdObjFunc;
import com.munch.exchange.job.objectivefunc.MovingAverageObjFunc;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.parts.OptimizationErrorPart;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.wizard.OptimizationWizard;

public class RateChartMovingAverageComposite extends Composite {
	
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
	
	// Moving Average
	private Text movAvgTextByLimit;
	private Text movAvgTextSellLimit;
	private Label movAvgLabelProfit;
	private Button movAvgBtnCheck;
	private Combo movAvgDaysCombo;
	private Slider movAvgSliderBuyLimit;
	private Button movAvgBtnOpt;
	private Label movAvgLblBuyLimit;
	private Label movAvgLblSellLimit;
	private Slider movAvgSliderSellLimit;
	private Label movAvgLblProfit;


	private MovingAverageObjFunc movAvgObjFunc = null;

	private double movAvgBuyLimit = 0;
	private double movAvgSellLimit = 0;
	final private int movAvgMaxDays = 30;

	private Optimizer<double[]> movAvgOptimizer = new Optimizer<double[]>();

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public RateChartMovingAverageComposite(Composite parent) {
		super(parent, SWT.NONE);
	
		//Composite movAvgComposite = new Composite(expandBar, SWT.NONE);
		//xpndtmMovingAvg.setControl(movAvgComposite);
		
		this.setLayout(new GridLayout(3, false));
		
		movAvgBtnCheck = new Button(this, SWT.CHECK);
		movAvgBtnCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgBtnCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				movAvgDaysCombo.setEnabled(movAvgBtnCheck.getSelection());
				movAvgBtnOpt.setEnabled(movAvgBtnCheck.getSelection());
				movAvgSliderBuyLimit.setEnabled(movAvgBtnCheck.getSelection());
				movAvgSliderSellLimit.setEnabled(movAvgBtnCheck.getSelection());
				//if(movAvgBtnCheck.getSelection())
				resetChartDataSet();
			}
		});
		movAvgBtnCheck.setText("Average:");
		
		
	
		movAvgDaysCombo = new Combo(this, SWT.NONE);
		movAvgDaysCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetChartDataSet();
			}
		});
		movAvgDaysCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgDaysCombo.setEnabled(false);
		movAvgDaysCombo.setText("5");
		movAvgDaysCombo.add("2");
		movAvgDaysCombo.add("3");
		movAvgDaysCombo.add("5");
		movAvgDaysCombo.add("10");
		movAvgDaysCombo.add("20");
		//movAvgDaysCombo.add("30");
		//movAvgDaysCombo.add("50");
		movAvgDaysCombo.add(String.valueOf(movAvgMaxDays));
		
		movAvgBtnOpt = new Button(this, SWT.NONE);
		movAvgBtnOpt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		movAvgBtnOpt.setText("Opt.");
		movAvgBtnOpt.setEnabled(false);
		movAvgBtnOpt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				//TODO Moving Average
				double max=getMovAvgObjFunc().getMax();
				double min=0;
				int dimension=3;
				getMovAvgObjFunc().setFromAndDownTo(period[0], period[1]);
				
				
				final IGPM<double[], double[]> gpm = ((IGPM<double[], double[]>) (IdentityMapping.IDENTITY_MAPPING));
				
				OptimizationWizard<double[]> wizard = new OptimizationWizard<double[]>(
						getMovAvgObjFunc(), gpm, dimension, min, max, rate.getOptResultsMap()
								.get(Type.MOVING_AVERAGE));
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() != Window.OK)
					return;
				
				
				//Open the Optimization error part
				OptimizationErrorPart.openOptimizationErrorPart(
						rate,movAvgOptimizer,
						OptimizationResults.OptimizationTypeToString(Type.MOVING_AVERAGE),
						partService, modelService, application, context);
				
				
				movAvgOptimizer.initOptimizationInfo(eventBroker,Type.MOVING_AVERAGE,rate, wizard.getAlgorithm(), wizard.getTerm());
				movAvgOptimizer.schedule();
				
				movAvgBtnOpt.setEnabled(false);
				movAvgBtnCheck.setEnabled(false);
					
			}
		});
		
		movAvgLblBuyLimit = new Label(this, SWT.NONE);
		movAvgLblBuyLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgLblBuyLimit.setText("Buy limit:");
		
		movAvgTextByLimit = new Text(this, SWT.BORDER);
		movAvgTextByLimit.setEditable(false);
		movAvgTextByLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgTextByLimit.setText("0.0");
		
		movAvgSliderBuyLimit = new Slider(this, SWT.NONE);
		movAvgSliderBuyLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		movAvgSliderBuyLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//logger.info(movAvgSliderBuyLimit.getSelection());
				movAvgBuyLimit=((float) movAvgSliderBuyLimit.getSelection())/getMovAvgObjFunc().getMovAvgSliderBuyFac();
				String buyLimit = String.format("%,.2f%%",  (float)movAvgBuyLimit);
				movAvgTextByLimit.setText(buyLimit);
				
				movAvgBuyLimit=-1;
				
				if(movAvgTextByLimit.isEnabled())
					resetChartDataSet();
				
				
			}
		});
		
		movAvgLblSellLimit = new Label(this, SWT.NONE);
		movAvgLblSellLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgLblSellLimit.setText("Sell limit:");
		
		movAvgTextSellLimit = new Text(this, SWT.BORDER);
		movAvgTextSellLimit.setEditable(false);
		movAvgTextSellLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		movAvgTextSellLimit.setText("0.0");
		
		movAvgSliderSellLimit = new Slider(this, SWT.NONE);
		movAvgSliderSellLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		movAvgSliderSellLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//logger.info(movAvgSliderSellLimit.getSelection());
				movAvgSellLimit=( (float) movAvgSliderSellLimit.getSelection())/getMovAvgObjFunc().getMovAvgSliderSellFac();
				String buyLimit = String.format("%,.2f%%",  (float) movAvgSellLimit);
				movAvgTextSellLimit.setText(buyLimit);
				
				movAvgSellLimit=-1;
				
				if(movAvgSliderSellLimit.isEnabled())
					resetChartDataSet();
			}
		});
		
		movAvgLblProfit = new Label(this, SWT.NONE);
		movAvgLblProfit.setText("Profit:");
		new Label(this, SWT.NONE);
		
		movAvgLabelProfit = new Label(this, SWT.NONE);
		movAvgLabelProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		movAvgLabelProfit.setText("00,00%");

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
	/**
	 * create the Moving average function
	 * @return
	 */
	private MovingAverageObjFunc getMovAvgObjFunc(){
		if(movAvgObjFunc!=null)return movAvgObjFunc;
		
		movAvgObjFunc=new MovingAverageObjFunc(
				 HistoricalPoint.FIELD_Close,
				 RateChart.PENALTY,
				 rate.getHistoricalData().getNoneEmptyPoints(),
				 maxProfit,
				 movAvgMaxDays
				 );
		return movAvgObjFunc;
	}
	
	private void  clearCollections(){
		int mov_ave_pos=mainCollection.indexOf("Moving Average");
		if(mov_ave_pos>=0)mainCollection.removeSeries(mov_ave_pos);
		
		int buy_pos=mainCollection.indexOf(MovingAverageObjFunc.Moving_Average_Buy_Signal);
		if(buy_pos>=0)mainCollection.removeSeries(buy_pos);
		
		int sell_pos=mainCollection.indexOf(MovingAverageObjFunc.Moving_Average_Sell_Signal);
		if(sell_pos>=0)mainCollection.removeSeries(sell_pos);
		
		int profit_pos=secondCollection.indexOf(MovingAverageObjFunc.Moving_Average_Profit);
		if(profit_pos>=0)secondCollection.removeSeries(profit_pos);
	}
	
	private void resetChartDataSet() {
		
		clearCollections();
		
		if (movAvgBtnCheck.getSelection()) {
			//Refresh the main plot
			double[] x=new double[3];
			if(movAvgBuyLimit>0){
				x[0]=movAvgBuyLimit;
				x[1]=movAvgSellLimit;
			}
			else{
				x[0]=( (float) movAvgSliderBuyLimit.getSelection())/getMovAvgObjFunc().getMovAvgSliderBuyFac();
				x[1]=( (float) movAvgSliderSellLimit.getSelection())/getMovAvgObjFunc().getMovAvgSliderSellFac();
			}
			x[2]=Double.valueOf(movAvgDaysCombo.getText())/getMovAvgObjFunc().getMovAvgMaxDayFac();
			
			getMovAvgObjFunc().setFromAndDownTo(period[0], period[1]);
			getMovAvgObjFunc().compute(x, null);
			
			logger.info("Profit: "+getMovAvgObjFunc().getProfit());
			
			XYSeries movAvgSeries=rate.getHistoricalData().getMovingAvg(HistoricalPoint.FIELD_Close,Integer.parseInt(movAvgDaysCombo.getText()),"Moving Average");
			mainCollection.addSeries(MacdObjFunc.reduceSerieToPeriod(movAvgSeries,period));
			
			int mov_ave_pos=mainCollection.indexOf("Moving Average");
			if(mov_ave_pos>=0){
				mainPlotRenderer.setSeriesShapesVisible(mov_ave_pos, false);
				mainPlotRenderer.setSeriesLinesVisible(mov_ave_pos, true);
				mainPlotRenderer.setSeriesPaint(mov_ave_pos, Color.GRAY);
			}
			
			mainCollection.addSeries(getMovAvgObjFunc().getBuySignalSeries());
			mainCollection.addSeries(getMovAvgObjFunc().getSellSignalSeries());
			
			int buy_pos=mainCollection.indexOf(MovingAverageObjFunc.Moving_Average_Buy_Signal);
            if(buy_pos>=0){
            	//logger.info("Signal found!!");
            	mainPlotRenderer.setSeriesShapesVisible(buy_pos, true);
            	mainPlotRenderer.setSeriesLinesVisible(buy_pos, false);
            	mainPlotRenderer.setSeriesShape(buy_pos, ShapeUtilities.createUpTriangle(5));
            	mainPlotRenderer.setSeriesShapesFilled(buy_pos, true);
            	mainPlotRenderer.setSeriesPaint(buy_pos, Color.GREEN);
            	mainPlotRenderer.setSeriesOutlinePaint(buy_pos, Color.BLACK);
            	mainPlotRenderer.setSeriesOutlineStroke(buy_pos, new BasicStroke(1.0f));
            	mainPlotRenderer.setUseOutlinePaint(true);
     
            }
            
            int sell_pos=mainCollection.indexOf(MovingAverageObjFunc.Moving_Average_Sell_Signal);
            if(sell_pos>=0){
            	mainPlotRenderer.setSeriesShapesVisible(sell_pos, true);
            	mainPlotRenderer.setSeriesLinesVisible(sell_pos, false);
            	mainPlotRenderer.setSeriesShape(sell_pos, ShapeUtilities.createDownTriangle(5));
            	mainPlotRenderer.setSeriesShapesFilled(sell_pos, true);
            	mainPlotRenderer.setSeriesPaint(sell_pos, Color.RED);
            	mainPlotRenderer.setSeriesOutlinePaint(sell_pos, Color.BLACK);
            	mainPlotRenderer.setSeriesOutlineStroke(sell_pos, new BasicStroke(1.0f));
            }
			
			
			//Refresh the second plot
			secondCollection.addSeries(getMovAvgObjFunc().getProfitSeries());

			String movAvgProfitString = String.format("%,.2f%%",
					getMovAvgObjFunc().getProfit() * 100);
			movAvgLabelProfit.setText(movAvgProfitString);

		}
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
		if (incoming == null || rate == null || movAvgTextByLimit == null)
			return false;
		if (!incoming.getUUID().equals(rate.getUUID()))
			return false;
		
		return true;
	}
	
	/**
	 * update the GUI
	 * @param g
	 * @param v
	 */
	private void resetMovingAverageGuiData(double[] g,double v){
		//Buy Limit
	    movAvgSliderBuyLimit.setSelection( (int) (g[0]*getMovAvgObjFunc().getMovAvgSliderBuyFac()) );
	    String buyLimit = String.format("%,.2f%%", ( (float)g[0]));
	    movAvgTextByLimit.setText(buyLimit);
	    movAvgBuyLimit=g[0];
	    
	    //Sell Limit
	    movAvgSliderSellLimit.setSelection( (int) (g[1]*getMovAvgObjFunc().getMovAvgSliderSellFac()) );
	    String sellLimit = String.format("%,.2f%%", ( (float)g[1]));
	    movAvgTextSellLimit.setText(sellLimit);
	    movAvgSellLimit=g[1];
	    
	    //Number of days
	    movAvgDaysCombo.setText(String.valueOf((int) (g[2]*getMovAvgObjFunc().getMovAvgMaxDayFac()) ));
	    
	    float movAvgProfit=maxProfit- (float)v;
		
		String movAvgProfitString = String.format("%,.2f%%", movAvgProfit*100);
		movAvgLabelProfit.setText(movAvgProfitString);
		
	    
	    resetChartDataSet();
	}
	
	@Inject 
	void optimizationResultsLoaded(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid){
		if(!isCompositeAbleToReact(rate_uuid))return;
		
		
		if(!rate.getOptResultsMap().get(Type.MOVING_AVERAGE).getResults().isEmpty()){
			double[] g=rate.getOptResultsMap().get(Type.MOVING_AVERAGE).getResults().getFirst().getDoubleArray();
			
			getMovAvgObjFunc().setFromAndDownTo(period[0], period[1]);
			double v=getMovAvgObjFunc().compute(g, null);
			resetMovingAverageGuiData(g,v);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Inject
	private void OptimizerFinished(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_FINISHED) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		else if(info.getType()==Type.MOVING_AVERAGE){
			movAvgBtnOpt.setEnabled(true);
			movAvgBtnCheck.setEnabled(true);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isCompositeAbleToReact(info.getRate().getUUID()))return;
		
		else if(info.getType()==Type.MOVING_AVERAGE){
			Individual<double[], double[]> individual=info.getBest();
			resetMovingAverageGuiData(individual.g,individual.v);
		}
		
	}

}
