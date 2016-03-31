package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.encog.ml.MLMethod;

import sun.swing.plaf.synth.Paint9Painter.PaintType;

import com.munch.exchange.model.core.encog.NoveltySearchGenome;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.Position;

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
	
	private HashMap<Long, Position> positionTracking=new HashMap<Long, Position>();
	private HashMap<Long, Double> profitTracking=new HashMap<Long, Double>();
	
	private HashMap<NeuralNetworkRating, Double> relDistMap=new HashMap<NeuralNetworkRating, Double>();
	
	
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

	public void newPosition(long time, Position position){
		nbOfPosition++;
		positionTracking.put(time, position);
		profitTracking.put(time, profit);
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
	
	public String positionTrackingToString(){
		String text="Number of position="+nbOfPosition+"\n";
		
		LinkedList<Long> times=new LinkedList<Long>();
		times.addAll(positionTracking.keySet());
		Collections.sort(times);
		
		for(long time:times){
			text+=IbBar.format(time)+": "+positionTracking.get(time).toString()+", "+ String.format ("%.2f",profitTracking.get(time))+"\n";
		}
		
		
		return text;
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
	
	

	public HashMap<Long, Position> getPositionTracking() {
		return positionTracking;
	}

	public HashMap<Long, Double> getProfitTracking() {
		return profitTracking;
	}
	
	private HashMap<Long, Position> getAllPositionTracking(){
		HashMap<Long, Position> allPositionTracking=new HashMap<Long, Position>();
		
		for(NeuralNetworkRating child:this.children){
			allPositionTracking.putAll(child.getAllPositionTracking());
		}
		
		allPositionTracking.putAll(positionTracking);
		
		return allPositionTracking;
	}
	
	private HashMap<Long, Position> getAllPositionTrackingOfTimes(Set<Long> times){
		HashMap<Long, Position> allPositionTracking=getAllPositionTracking();
		LinkedList<Long> sortedKeys=new LinkedList<Long>();
		sortedKeys.addAll(allPositionTracking.keySet());
		Collections.sort(sortedKeys);
		
		for(Long time:times){
			if(allPositionTracking.containsKey(time))continue;
			
			Position lastPos=Position.NEUTRAL;
			for(Long key:sortedKeys){
				if(key>time){
					allPositionTracking.put(key, lastPos);
				}
				
				lastPos=allPositionTracking.get(key);
			}
			
		}
		
		return allPositionTracking;
	}
	
	
	private HashMap<Long, Double> getAllProfitTracking(){
		HashMap<Long, Double> allProfitTracking=new HashMap<Long, Double>();
		
		for(NeuralNetworkRating child:this.children){
			allProfitTracking.putAll(child.getAllProfitTracking());
		}
		
		allProfitTracking.putAll(profitTracking);
		
		return allProfitTracking;
	}
	
	private HashMap<Long, Double> getAllProfitTrackingOfTimes(Set<Long> times){
		HashMap<Long, Double> allProfitTracking=getAllProfitTracking();
		LinkedList<Long> sortedKeys=new LinkedList<Long>();
		sortedKeys.addAll(allProfitTracking.keySet());
		Collections.sort(sortedKeys);
		
		for(Long time:times){
			if(allProfitTracking.containsKey(time))continue;
			
			double lastProfit=0;
			for(Long key:sortedKeys){
				if(key>time){
					allProfitTracking.put(key, lastProfit);
				}
				
				lastProfit=allProfitTracking.get(key);
			}
			
		}
		
		return allProfitTracking;
	}
	
	
	public double calculateRelativDistance(NeuralNetworkRating other){
		if(other.containsRelDist(this))
			return other.getRelDist(this);
		if(this.containsRelDist(other))
			return this.getRelDist(other);
		
		
		Set<Long> otherTimes=other.getAllPositionTracking().keySet();
		Set<Long> times=this.getAllPositionTracking().keySet();
		
		HashMap<Long, Position> otherAllPositionTracking=other.getAllPositionTrackingOfTimes(times);
		HashMap<Long, Position> allPositionTracking=this.getAllPositionTrackingOfTimes(otherTimes);
		
		LinkedList<Long> sortedKeys=new LinkedList<Long>();
		sortedKeys.addAll(allPositionTracking.keySet());
		Collections.sort(sortedKeys);
		
		double relDist=0;
		
		Long lastKey=sortedKeys.getFirst();
		for(Long key:sortedKeys){
			Position otherPos=otherAllPositionTracking.get(key);
			Position pos=allPositionTracking.get(key);
			
			double dist=Math.abs(Position.getInt(otherPos)-Position.getInt(pos));
			
			relDist+=dist*((key-lastKey)/1000);
			lastKey=key;
		}
		
//		other.addRelDist(this, relDist);
		this.addRelDist(other, relDist);
		
		
		return relDist;
		
	}
	
	public synchronized void addRelDist(NeuralNetworkRating other, double relDist){
		this.relDistMap.put(other, relDist);
	}
	
	public synchronized boolean containsRelDist(NeuralNetworkRating other){
		return this.relDistMap.containsKey(other);
	}
	
	public synchronized double getRelDist(NeuralNetworkRating other){
		return this.relDistMap.get(other);
	}
	
	public synchronized void cleanRelDistMap(Set<NeuralNetworkRating> toDeleteSet){
		for(NeuralNetworkRating key:toDeleteSet){
			this.relDistMap.remove(key);
		}
	}
	
	
	
	
	
	
}
