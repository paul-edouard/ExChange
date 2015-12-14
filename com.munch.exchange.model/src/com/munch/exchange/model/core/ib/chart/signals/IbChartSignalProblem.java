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
	
	
	public static int numberOfEval=0;
	
	private IbChartSignal chartSignal;
	private List<IbBar> bars;
	

	public IbChartSignalProblem(IbChartSignal chartSignal) {
		super(chartSignal.getParameters().size(), 2);
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
		List<IbChartPoint> profitPoints=signal.getProfitSerie().getPoints();
		List<IbChartPoint> riskPoints=signal.getRiskSerie().getPoints();
		
		double endProfit=profitPoints.get(profitPoints.size()-1).getValue();
		double maxRisk=0;
		for(IbChartPoint point:riskPoints){
			if(maxRisk<(-point.getValue()))
				maxRisk=-point.getValue();
		}
		
		//System.out.println("Profit: "+endProfit+", risk: "+maxRisk);
		
		solution.setObjective(0, -endProfit);
		solution.setObjective(1, maxRisk);
	}

	@Override
	public void evaluate(Solution solution) {
		IbChartSignal signal=(IbChartSignal) this.chartSignal.copy();
		
		
		
		// Set the Chart Signal parameters
		setChartSignalParameters(signal, solution);
		
		//TODO Create a batch modus to speed up the computation
		
		//Calculate the Signal
		signal.compute(bars);
		
		//Evaluate Profit and Risk
		setObjectives(signal, solution);
		
		numberOfEval++;
		
		//System.out.println("Nb of eval: "+numberOfEval);
		
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
		Solution solution = new Solution(getNumberOfVariables(), 
				getNumberOfObjectives());
		
		int index=0;
		for(IbChartParameter param:chartSignal.getParameters()){
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

			default:
				break;
			}
			
			index++;
		}
		
		return solution;
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
		Solution solution =createStartSolution();
		
		
		List<Solution> solutions=new LinkedList<Solution>();
		solutions.add(solution);
		
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
	
	public Population  createStartPopulation(int nbOfSolution,double epsilon){
		Population population = newArchive(epsilon);
		
		//Add the start solutions
		Solution startSol=createStartSolution();
		this.evaluate(startSol);
		population.add(startSol);
		
		int i=0;
		while(population.size()<nbOfSolution && i<100){
			Solution ranDomSol=createRandomSolution();
			this.evaluate(ranDomSol);
			population.add(ranDomSol);
			i++;
		}
		
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
	
	

}
