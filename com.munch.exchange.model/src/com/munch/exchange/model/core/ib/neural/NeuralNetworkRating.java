package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.LinkedList;

import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;

public class NeuralNetworkRating implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9054304246294466182L;

	private long id=0;
	
	private double profit;
	private double risk;
	
	private double maxProfit;
	private double maxRisk;
	
	private double tradeProfit;
	
	private int nbOfPosition;
	
	private String name;
	
	private double score;
	
	private MLMethod method;
	
	
	private LinkedList<NeuralNetworkRating> children=new LinkedList<NeuralNetworkRating>();
	
	
	public NeuralNetworkRating() {
		super();
		profit=0;
		risk=0;
		maxProfit=0;
		maxRisk=0;
		tradeProfit=0.0;
		nbOfPosition=0;
	}

	public void updateProfit(double diff){
		updateProfitOnly(diff);
		tradeProfit+=diff;
		
//		Update the maxProfit
		if(profit>maxProfit)
			maxProfit=profit;
		
//		Calculate the risk
		risk=profit-maxProfit;
		
//		Update the maxRisk
		if(maxRisk<-risk){
			maxRisk=-risk;
		}
	}
	
	public void updateProfitOnly(double diff){
		profit+=diff;
	}
	
	public void resetTradeProfit(){
		tradeProfit=0;
	}

	public void newPosition(){
		nbOfPosition++;
	}
	
	public double getProfit() {
		return profit;
	}

	public double getRisk() {
		return risk;
	}

	public double getMaxProfit() {
		return maxProfit;
	}

	public double getMaxRisk() {
		return maxRisk;
	}

	public double getTradeProfit() {
		return tradeProfit;
	}

	public void setRisk(double risk) {
		this.risk = risk;
	}
	
	public void clearChildren(){
		children.clear();
	}
	
	public void addChildren(NeuralNetworkRating profitAndRisk){
		
		this.updateProfitOnly(profitAndRisk.getProfit());
		
		if(this.getRisk()<profitAndRisk.getMaxRisk())
			this.setRisk(profitAndRisk.getMaxRisk());
		
		
		children.add(profitAndRisk);
	}
	

	public LinkedList<NeuralNetworkRating> getChildren() {
		return children;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNbOfPosition() {
		return nbOfPosition;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	

	public MLMethod getMethod() {
		return method;
	}

	public void setMethod(MLMethod method) {
		this.method = method;
	}
	
	
	
	
}
