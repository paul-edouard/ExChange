package com.munch.exchange.model.core.ib.chart.trend;

public class TrendLineFunction extends DerivableFunction{
	
	
	private double[] prices;
	private long[] times;
	private double fac;
	private double sign=1;
	

	public TrendLineFunction(long[] times,double[] prices,double factor) {
		super(new double[2]);
		
		this.times=times;
		this.prices=prices;
		//this.factor=factor;
		this.fac=factor;
		
		
		setStartValues();
	}
	
	public void setUpwardTrend(){
		sign=1.0;
	}
	
	public void setDownwardTrend(){
		sign=-1.0;
	}
	
	@Override
	public double calculate() {
		double a=this.getVariables()[0];
		double b=this.getVariables()[1];
		
		double U = 0;
		for(int i=0;i<prices.length;i++){
			double diff=times[i]-times[0];
			double x=a*diff+b-prices[i];
//			U +=0.5*x*x+Math.exp(fac*x)*(x-1/fac)/fac;
//			U +=x*x/2*((fac-1)*Math.tanh(x)+fac+1);
			U +=x*x/2*((fac-1)*Math.tanh(x)+fac+1);
		}
		
		U*=sign;
		
		return U;
	}

	@Override
	public double[] calculateGradients() {
		double a=this.getVariables()[0];
		double b=this.getVariables()[1];
		
		double[] gradients = new double[this.getVariables().length];
		gradients[0]=0;gradients[1]=0;
		
		for(int i=0;i<prices.length;i++){
			double diff=times[i]-times[0];
			double x=a*diff+b-prices[i];
//			double xx=x*x;
			
//			double F=x*(Math.exp(fac*x)+1);
//			double F=x*((fac-1)*Math.tanh(x)+fac+1)+xx/2*(fac-1)/(1+xx);
			
			double F=x;
			gradients[0]+=F;
			gradients[1]+=F;
			
		}
		
		gradients[0]*=sign;
		gradients[1]*=sign;
		
		return gradients;
	}
	
	
	private void setStartValues(){
		
		
		if(prices.length<2)return ;
		
		double startValue=prices[0];
		double endValue=prices[prices.length-1];
		
		long startTime=times[0];
		long endTime=times[times.length-1];
		
		double[] values=new double[2];
		values[0]=(startValue-endValue)/(startTime-endTime);
		values[1]=startValue;
		
		this.setVariables(values);
		
		
	}
	
	

}
