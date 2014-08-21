package com.munch.exchange.job.objectivefunc;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.services.INeuralNetworkProvider;

public class NeuralNetworkOutputObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
	
	private static final long serialVersionUID = 1;
			
	private static Logger logger = Logger.getLogger(NeuralNetworkOutputObjFunc.class);
			
	// History point List
	private LinkedList<HistoricalPoint> noneZeroHisList = new LinkedList<HistoricalPoint>();

			
	// The history fled to optimize
	private String field;
	// the penalty for bay or sell
	private double penalty;
	
	// Max Profit from the given period
	private double maxProfit;
	private double profit;
	
	public void setMaxProfit(double maxProfit) {
		this.maxProfit = maxProfit;
	}

	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	public static int AddaptResultEntityToLastAvailablePoints(Stock stock, INeuralNetworkProvider provider){
		
		Configuration config=stock.getNeuralNetwork().getConfiguration();
		OptimizationResults res=stock.getOptResultsMap().get(config.getOptimizationResultType());
		ResultEntity res_ent=new ResultEntity(provider.calculateMaxProfitOutputList(stock).toDoubleArray());
		if(res.getResults().size()==0){
			res.addResult(res_ent);
		}
		else{
			for(ResultEntity oldRes:res.getResults()){
				if(oldRes.getGenome().size()>=res_ent.getGenome().size())continue;
				
				for(int i=oldRes.getGenome().size();i<res_ent.getGenome().size();i++){
					oldRes.getGenome().add(res_ent.getGenome().get(i));
				}
			}
		}
		
		return res_ent.getGenome().size();
		
	}
	
	public 	NeuralNetworkOutputObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList,double maxProfit){
		
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;
		this.maxProfit = maxProfit;
	}
	

	@Override
	public double compute(double[] x, Random r) {
		
		if(x.length!=this.noneZeroHisList.size()-1)
			return 0;
		
		// Init Profit
		profit = 0;
		boolean bought = false;
		
		//String content="";
		for(int i=0;i<x.length;i++){
			if(x[i]>0.5){
				float diff=noneZeroHisList.get(i+1).get(this.field)
						- noneZeroHisList.get(i).get(this.field);
				profit+=diff;
				if(!bought){
					bought=true;
					profit=profit-penalty*noneZeroHisList.get(i).get(this.field);
				}
			//	content+="1,";
			}
			else{
				if(bought){
					bought=false;
					profit=profit-penalty*noneZeroHisList.get(i).get(this.field);
				}
			//	content+="0,";
			}
			
			
		}
		
		profit = profit / noneZeroHisList.get(0).get(field);
		
		//logger.info("Input:\n["+content+"]");
		
		//logger.info("Profit: "+profit);

		return maxProfit - profit;
	}

}
