package com.munch.exchange.model.core.ib.chart.trend;

public class TrendLineFunction extends DerivableFunction{
	
	private double[] prices;
	private long[] times;
	private double powFac;
	private double sign=1;
	

	public TrendLineFunction(double[] variables,long[] times,double[] prices,double factor) {
		super(variables);
		
		this.times=times;
		this.prices=prices;
		//this.factor=factor;
		powFac=Math.pow(10, factor);
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
		
		//double powFac=Math.pow(10, factor);
		
		double F = 0;
		for(int i=0;i<prices.length;i++){
			double y=a*(times[i]-times[0])+b;
			
			double abs=(prices[i]-y)*sign;
			double abs_quad=abs*abs;
			
			if(abs>0){
				F+=abs_quad;
			}
			else{
				//F+=powFac*abs_quad;
				F+=abs_quad;
			}
		}
		
		return F;
	}

	@Override
	public double[] calculateGradients() {
		double a=this.getVariables()[0];
		double b=this.getVariables()[1];
		
		double[] gradients = new double[this.getVariables().length];
		gradients[0]=0;gradients[1]=0;
		
		for(int i=0;i<prices.length;i++){
			long diffTime=times[i]-times[0];
			double y=a*diffTime+b;	
			double abs=(prices[i]-y)*sign;
			
			if(abs>0){
				gradients[0]+=2*sign*diffTime*abs;
				gradients[1]+=2*sign*abs;
			}
			else{
				//gradients[0]+=2*sign*diffTime*abs*powFac;
				//gradients[1]+=2*sign*abs*powFac;
				gradients[0]+=2*sign*diffTime*abs;
				gradients[1]+=2*sign*abs;
			}
		}
		
		return gradients;
	}
	
	

}
