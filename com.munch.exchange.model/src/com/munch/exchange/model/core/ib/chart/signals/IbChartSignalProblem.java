package com.munch.exchange.model.core.ib.chart.signals;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.moeaframework.core.Solution;
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
	
	
	private IbChartSignal chartSignal;
	private List<IbBar> bars;
	

	public IbChartSignalProblem(IbChartSignal chartSignal) {
		super(chartSignal.getParameters().size(), 2);
		this.chartSignal=chartSignal;
		this.bars=chartSignal.getOptimizationBars();
	}

	@Override
	public void evaluate(Solution solution) {
		IbChartSignal signal=(IbChartSignal) this.chartSignal.copy();
		
		System.out.println("Nb of bars: "+bars.size());
		
		// Set the Chart Signal parameters
		int index=0;
		for(IbChartParameter param:signal.getParameters()){
			switch (param.getType()) {
			case DOUBLE:
				param.setValue(EncodingUtils.getReal(solution.getVariable(index)));
				System.out.println("Double: "+param.getValue());
				break;
			case INTEGER:
				param.setValue(EncodingUtils.getInt(solution.getVariable(index)));
				System.out.println("Integer: "+param.getValue());
				break;

			default:
				break;
			}
			
			index++;
		}
		signal.setDirty(true);
		
		//TODO Create a batch modus to speed up the computation
		
		//Calculate the Signal
		signal.compute(bars);
		
		//Evaluate Profit and Risk
		List<IbChartPoint> profitPoints=signal.getProfitSerie().getPoints();
		List<IbChartPoint> riskPoints=signal.getRiskSerie().getPoints();
		
		double endProfit=profitPoints.get(profitPoints.size()-1).getValue();
		double maxRisk=0;
		for(IbChartPoint point:riskPoints){
			if(maxRisk<(-point.getValue()))
				maxRisk=-point.getValue();
		}
		
		System.out.println("Profit: "+endProfit+", risk: "+maxRisk);
		
		solution.setObjective(0, -endProfit);
		solution.setObjective(1, maxRisk);
		
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
	
	
	public List<Solution> createStartSolutions() {
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
		
		
		List<Solution> solutions=new LinkedList<Solution>();
		solutions.add(solution);
		
		return solutions;
		
	}
	
	

	public void setBars(List<IbBar> bars) {
		this.bars = bars;
	}
	
	

}
