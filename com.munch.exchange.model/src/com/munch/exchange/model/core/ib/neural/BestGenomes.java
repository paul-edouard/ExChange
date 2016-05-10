package com.munch.exchange.model.core.ib.neural;

import java.util.LinkedList;

import org.encog.ml.ea.genome.Genome;

import com.munch.exchange.model.core.encog.NoveltySearchGenome;


public class BestGenomes extends LinkedList<GenomeEvaluation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8051600227014549295L;
	
	private int maxGenomes=10;
	

	public BestGenomes() {
		super();
	}
	
	
	private double getScore(GenomeEvaluation g_eval){
		double score=g_eval.getScore();
		if(g_eval.getGenome() instanceof NoveltySearchGenome){
			NoveltySearchGenome nov_gen=(NoveltySearchGenome) g_eval.getGenome();
			score=nov_gen.getBehavior();
		}
		
		return score;
	}
	
	public synchronized void addGenomeEvaluation(GenomeEvaluation g_eval){
		
		if(this.contains(g_eval))return;
		
		if(this.isEmpty()){
			this.add(g_eval);
			return;
		}
		
		
		
//		Test if the new genome is bitten
		for(GenomeEvaluation pareto_elt:this){
			if(getScore(pareto_elt) >= getScore(g_eval) 
					&& pareto_elt.getBackTestingScore() >= g_eval.getBackTestingScore() ){
				return;
			}
		}
		
//		Remove the genomes that are bitten from the new one
		LinkedList<GenomeEvaluation> toDelete=new LinkedList<GenomeEvaluation>();
		
		for(int i=0;i<this.size();i++){
				if(g_eval.getBackTestingScore() >= this.get(i).getBackTestingScore() &&
						getScore(g_eval) > getScore(this.get(i))){
					toDelete.add(this.get(i));
				}
				else if(g_eval.getBackTestingScore() > this.get(i).getBackTestingScore() &&
						getScore(g_eval) >= getScore(this.get(i))){
					toDelete.add(this.get(i));
				}
		}
		this.removeAll(toDelete);
		
		
//		Insert the new genome
		for(int i=0;i<this.size();i++){
			if(getScore(g_eval) > getScore(this.get(i))){
				this.add(i, g_eval);
				return;
			}
			
		}
		
		
		this.addLast(g_eval);
		
		
	}
	
	
	public boolean containsScore(double score){
		for(GenomeEvaluation g_eval:this){
			if(g_eval.getGenome().getScore()==score)
				return true;
		}
		return false;
	}
	

	public void setMaxGenomes(int maxGenomes) {
		this.maxGenomes = maxGenomes;
	}

	@Override
	public String toString() {
		
		String content="Best genome: \n";
		for(int i=0;i<this.size();i++){
			Genome genome=this.get(i).getGenome();
			
			if(genome instanceof NoveltySearchGenome){
				NoveltySearchGenome nov_gen=(NoveltySearchGenome) genome;
				content+="Pos: "+i+
						", score="+ String.format ("%.2f",nov_gen.getBehavior())+
						", back testing= "+ String.format ("%.2f",this.get(i).getBackTestingScore())+
						", novelty= "+ String.format ("%.2f",nov_gen.getNovelty())+
						", generation="+genome.getBirthGeneration()+"\n";
			}
			else{
			content+="Pos: "+i+
					", score="+ String.format ("%.2f",genome.getScore())+
					", back testing= "+ String.format ("%.2f",this.get(i).getBackTestingScore())+
					", generation="+genome.getBirthGeneration()+"\n";
			}
		}
		
		
		return content;
	}
	
	public LinkedList<GenomeEvaluation> getBestGenomes(){
		LinkedList<GenomeEvaluation> best=new LinkedList<GenomeEvaluation>();
		
		for(GenomeEvaluation g_eval:this){
			if(best.size()>=maxGenomes)break;
			best.add(g_eval);
		}
		return best;
	}


}
