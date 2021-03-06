package com.munch.exchange.model.core.ib.chart.signals;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;

public class IbChartSignalProblem extends AbstractProblem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5571326766704654121L;
	
	private static final int NB_OF_OBJECTIVES=2;
	
	
	public static int numberOfEval=0;
	
	private IbChartSignal chartSignal;
	private List<IbBar> bars;
	

	public IbChartSignalProblem(IbChartSignal chartSignal) {
		super(chartSignal.getParameters().size(), NB_OF_OBJECTIVES);
		this.chartSignal=chartSignal;
		this.bars=chartSignal.getOptimizationBars();
	}

	private void setChartSignalParameters(IbChartSignal signal,
			Solution solution) {
		// Set the Chart Signal parameters
		int index = 0;
		for (IbChartParameter param : signal.getParameters()) {
			switch (param.getType()) {
			case DOUBLE:
				param.setValue(EncodingUtils.getReal(solution
						.getVariable(index)));
				//System.out.println("Double: " + param.getValue());
				break;
			case INTEGER:
				param.setValue(EncodingUtils.getInt(solution.getVariable(index)));
				//System.out.println("Integer: " + param.getValue());
				break;

			default:
				break;
			}

			index++;
		}
		signal.setDirty(true);
	}
	
	private void setObjectives(IbChartSignal signal,
			Solution solution){
		
		double[] profitAndRisk=extractProfitAndRiskFromChartSignal(signal);
		
		solution.setObjective(0, -profitAndRisk[0]);
		solution.setObjective(1, profitAndRisk[1]);
	}
	
	public static double[] extractProfitAndRiskFromChartSignal(IbChartSignal signal){
		
		double[] profitAndRisk=new double[2];
		
		if(!signal.isBatch()){
		
		List<IbChartPoint> profitPoints=signal.getProfitSerie().getPoints();
		List<IbChartPoint> riskPoints=signal.getRiskSerie().getPoints();
		
		double endProfit=profitPoints.get(profitPoints.size()-1).getValue();
		double maxRisk=0;
		for(IbChartPoint point:riskPoints){
			if(maxRisk<(-point.getValue()))
				maxRisk=-point.getValue();
		}
		
//		double[] profitAndRisk=new double[2];
		profitAndRisk[0]=endProfit;
		profitAndRisk[1]=maxRisk;
		
		}
		else{
//		double[] profitAndRisk=new double[2];
		profitAndRisk[0]=signal.getTotalProfit();
		profitAndRisk[1]=signal.getMaxRisk();
		}
		
		
		return profitAndRisk;
	}
	

	@Override
	public void evaluate(Solution solution) {
		IbChartSignal signal=(IbChartSignal) this.chartSignal.copy();
		
		// Set the Chart Signal parameters
		setChartSignalParameters(signal, solution);
		
		//Calculate the Signal
		signal.setBatch(true);
		signal.compute(bars);
		
		//Evaluate Profit and Risk
		setObjectives(signal, solution);
		
//		numberOfEval++;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(getNumberOfVariables(), 
				getNumberOfObjectives());
		
		int index=0;
		for(IbChartParameter param:chartSignal.getParameters()){
			switch (param.getType()) {
			case DOUBLE:
				solution.setVariable(index,  EncodingUtils.newReal(param.getMinValue(), param.getMaxValue()));
				break;
			case INTEGER:
				solution.setVariable(index, EncodingUtils.newInt((int)param.getMinValue(), (int)param.getMaxValue()));
				break;

			default:
				break;
			}
			
			index++;
		}
		
		
		return solution;
	}
	
	private Solution createStartSolution(){
		return createSolutionFromIbChartParameter(chartSignal.getParameters());
	}
	
	private Solution createRandomSolution() {
		Solution solution = new Solution(getNumberOfVariables(),
				getNumberOfObjectives());

		int index = 0;
		for (IbChartParameter param : chartSignal.getParameters()) {
			switch (param.getType()) {
			case DOUBLE:
				RealVariable rvar = EncodingUtils.newReal(param.getMinValue(),
						param.getMaxValue());
				rvar.setValue(ThreadLocalRandom.current().nextDouble(
						param.getMinValue(), param.getMaxValue()));
				solution.setVariable(index, rvar);
				break;
			case INTEGER:
				RealVariable ivar = EncodingUtils.newInt(
						(int) param.getMinValue(), (int) param.getMaxValue());
				ivar.setValue(ThreadLocalRandom.current().nextInt(
						(int) param.getMinValue(), (int) param.getMaxValue()));
				solution.setVariable(index, ivar);
				break;

			default:
				break;
			}

			index++;
		}

		return solution;
	}
	
	public List<Solution> createStartSolutions() {
		
		List<Solution> solutions=new LinkedList<Solution>();
		
		
		Solution startSolution =createStartSolution();
		solutions.add(startSolution);
		
		//Add the Save optimized solution
		for(IbChartSignalOptimizedParameters optimizedParameters:this.chartSignal.getOptimizedSet()){
			Solution solution=createSolutionFromIbChartParameter(optimizedParameters.getParameters());
			this.evaluate(solution);
			solutions.add(solution);
		}
		
		return solutions;
	}
	
	private NondominatedPopulation newArchive(double epsilon) {
		if (epsilon == 0) {
			return new NondominatedPopulation(new ParetoDominanceComparator());
		} else {
			//System.out.println("EpsilonBoxDominanceArchive: "+epsilon);
			return new EpsilonBoxDominanceArchive(epsilon);
		}
	}
	
	public Population  createStartPopulation(double epsilon){
		Population population = newArchive(epsilon);
		
		//Add the start solutions
		Solution startSol=createStartSolution();
		this.evaluate(startSol);
		population.add(startSol);
		
		//Add the Save optimized solution
		for(IbChartSignalOptimizedParameters optimizedParameters:this.chartSignal.getOptimizedSet()){
			Solution solution=createSolutionFromIbChartParameter(optimizedParameters.getParameters());
			this.evaluate(solution);
			population.add(solution);
		}
		
		/*
		int i=0;
		while(population.size()<nbOfSolution && i<100){
			Solution ranDomSol=createRandomSolution();
			this.evaluate(ranDomSol);
			population.add(ranDomSol);
			i++;
		}
		*/
		
		//In order to run the population size have to be a least bigger than 1
		if(population.size()<2){
			Solution ranDomSol=createRandomSolution();
			ranDomSol.setObjective(0, startSol.getObjective(0)+1000);
			ranDomSol.setObjective(1, startSol.getObjective(1)-1000);
			population.add(ranDomSol);
		}
		
		return population;
		
	}
	
 	public void setBars(List<IbBar> bars) {
		this.bars = bars;
	}
	
	
 	/**
 	 * transform a list of ib chart parameters into a mea framework solution
 	 * 
 	 * @param parameters
 	 * @return
 	 */
 	public static Solution createSolutionFromIbChartParameter( List<IbChartParameter> parameters){
 		Solution solution = new Solution(parameters.size(), NB_OF_OBJECTIVES);
 		
 		int index=0;
		for(IbChartParameter param:parameters){
			switch (param.getType()) {
			case DOUBLE:
				RealVariable rvar=EncodingUtils.newReal(param.getMinValue(), param.getMaxValue());
				rvar.setValue(param.getValue());
				solution.setVariable(index,rvar);
				break;
			case INTEGER:
				RealVariable ivar=EncodingUtils.newInt((int)param.getMinValue(), (int)param.getMaxValue());
				ivar.setValue(param.getValue());
				solution.setVariable(index,ivar);
				break;
			//TODO create the String case

			default:
				break;
			}
			
			index++;
		}
 		
 		return solution;
 	}
 	
 	
 	/**
 	 * transform a mea solution into a list of ibChart parameters
 	 * 
 	 * @param solution
 	 * @param parametersTemplate
 	 * @return
 	 */
 	public static List<IbChartParameter> createIbChartParametersFromSolution(Solution solution, List<IbChartParameter> parametersTemplate){
 		List<IbChartParameter> parameters=new LinkedList<IbChartParameter>();
 		
 		
 		int index=0;
		for(IbChartParameter param:parametersTemplate){
			switch (param.getType()) {
			case DOUBLE:
				RealVariable r_v=(RealVariable)solution.getVariable(index);
				IbChartParameter c_d_p=param.copy();
				c_d_p.setId(0);
				c_d_p.setValue(r_v.getValue());
				c_d_p.setIndicator(null);
				//System.out.println("Double value: "+r_v.getValue());
				parameters.add(c_d_p);
				break;
			case INTEGER:
				RealVariable i_v=(RealVariable)solution.getVariable(index);
				IbChartParameter c_i_p=param.copy();
				c_i_p.setId(0);
				c_i_p.setValue(i_v.getValue());
				c_i_p.setIndicator(null);
				//System.out.println("Integer value: "+i_v.getValue());
				parameters.add(c_i_p);
				break;
			//TODO create the String case

			default:
				break;
			}
			
			index++;
		}
 		
 		
 		return parameters;
 	}
 	
 	

 	
}
