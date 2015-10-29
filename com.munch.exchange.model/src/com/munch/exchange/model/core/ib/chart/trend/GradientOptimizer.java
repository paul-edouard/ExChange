package com.munch.exchange.model.core.ib.chart.trend;

import java.util.Arrays;

public class GradientOptimizer {
	
	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The momentum.
	 */
	private double momentum;

	/**
	 * The last delta values.
	 */
	private double[] lastDelta;
	
	/**
	 * The function to optimize
	 */
	private DerivableFunction func;
	
	
	private double minChanges=0.001;
	
	private int maxIterartions=10000;

	public GradientOptimizer(double learningRate, double momentum,
			DerivableFunction func) {
		super();
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.func = func;
		this.lastDelta = new double[this.func.getVariables().length];
		for(int i=0;i<this.lastDelta.length;i++)
			this.lastDelta[i]=0;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getMomentum() {
		return momentum;
	}

	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	public DerivableFunction getFunc() {
		return func;
	}

	public void setFunc(DerivableFunction func) {
		this.func = func;
	}
	
	
	public double getMinChanges() {
		return minChanges;
	}

	public void setMinChanges(double minChanges) {
		this.minChanges = minChanges;
	}

	public double updateVariables(final double[] gradients, final int index) {
		final double delta = (gradients[index] * this.learningRate)
				+ (this.lastDelta[index] * this.momentum);
		this.lastDelta[index] = delta;
		return delta;
	}
	
	public void optimize(){
		
		System.out.println("Starting values: "+Arrays.toString(func.getVariables()));
		
		double oldValue=func.calculate();
		System.out.println("Start Value: "+oldValue);
		for(int i=0;i<this.maxIterartions;i++){
			double[] variables=func.getVariables();
			double[] copy=Arrays.copyOf(variables, variables.length);
			double[] gradients=func.calculateGradients();
			//System.out.println("Gradient: "+Arrays.toString(gradients));
			for(int j=0;j<variables.length;j++){
				variables[j]+=updateVariables(gradients, j);
			}
			
			func.setVariables(variables);
			double newValue=func.calculate();
			
			//System.out.println("New Value: "+newValue);
			//System.out.println("Old Variables: "+Arrays.toString(copy));
			//System.out.println("New Variables: "+Arrays.toString(variables));
			//System.out.println("Old Value: "+oldValue);
			
			if(newValue>=oldValue){
				this.learningRate=learningRate/10;
				this.momentum=this.momentum/10;
				func.setVariables(copy);
				continue;
			}
			else{
				this.learningRate=learningRate*5;
				this.momentum=this.momentum*5;
			}
			
					
			
			double changes=(newValue-oldValue)/oldValue;
			oldValue=newValue;
			
			/*
			if(changes<minChanges){
				System.out.println("Break optimization due to min change");
				break;
			}
			*/
			
		}
		System.out.println("New Value: "+func.calculate());
		System.out.println("New values: "+Arrays.toString(func.getVariables()));
	}
	
	
	
	
	

}
