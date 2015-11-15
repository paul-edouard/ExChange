package com.munch.exchange.model.core.ib.chart.signals;

import java.util.List;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;

public class IbChartSignalProblem extends AbstractProblem{
	
	private IbChartSignal chartSignal;
	private List<IbBar> bars;
	

	public IbChartSignalProblem(IbChartSignal chartSignal) {
		super(chartSignal.getParameters().size(), 2);
		this.chartSignal=chartSignal;
	}

	@Override
	public void evaluate(Solution solution) {
		IbChartSignal signal=(IbChartSignal) this.chartSignal.copy();
		
		// Set the Chart Signal parameters
		int index=0;
		for(IbChartParameter param:signal.getParameters()){
			switch (param.getType()) {
			case DOUBLE:
				param.setValue(EncodingUtils.getReal(solution.getVariable(index)));
				break;
			case INTEGER:
				param.setValue(EncodingUtils.getInt(solution.getVariable(index)));
				break;

			default:
				break;
			}
			
			index++;
		}
		signal.setDirty(true);
		
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
		
		solution.setObjective(0, -endProfit);
		solution.setObjective(1, maxRisk);
		
	}

	@Override
	public Solution newSolution() {
		// TODO Auto-generated method stub
		return null;
	}

}
