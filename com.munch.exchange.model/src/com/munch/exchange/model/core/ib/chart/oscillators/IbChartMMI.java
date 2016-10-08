package com.munch.exchange.model.core.ib.chart.oscillators;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MMI;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartMMI extends IbChartIndicator {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7599234527907676636L;
	
	public static final String MMI_Serie="Market Meanness Index";
	
	public static final String PERIOD="Period";
	
	public static final String PRICE="Price";

	
	
	public IbChartMMI() {
		super();
	}
	
	
	public IbChartMMI(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartMMI();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Market Meanness Index";
	}


	@Override
	public void createSeries() {
		
//		MMI
		this.series.add(new IbChartSerie(this,MMI_Serie,RendererType.PERCENT,true,true,50, 44, 89));
		
		
	}


	@Override
	public void createParameters() {
		

		
//		SIGNAL PERIOD
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 14, 1, 200, 0);
		this.parameters.add(param);
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int validAtPosition=period;
		
		
//		MMI
		double[] mmi=MMI.compute(prices,period);
		
		
		
		refreshSerieValues(MMI_Serie, 			reset, times, mmi, 									validAtPosition);
		
		
		
	}
	
	
	
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
