package com.munch.exchange.model.core.ib.neural;

import java.util.LinkedList;

import org.encog.ml.ea.genome.Genome;


public class BestGenomes extends LinkedList<GenomeEvaluation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8051600227014549295L;
	
	private int maxGenomes=10;
	

	public BestGenomes() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void addGenomeEvaluation(GenomeEvaluation g_eval){
		
		if(this.isEmpty()){
			this.add(g_eval);
			return;
		}
		
		for(int i=0;i<this.size();i++){
			if(g_eval.getBackTestingScore() > this.get(i).getBackTestingScore()){
				this.add(i, g_eval);
				return;
			}
		}
		
		this.addLast(g_eval);
		
//		while(this.size()>maxGenomes)
//			this.removeLast();
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
		for(int i=0;i<maxGenomes;i++){
			Genome genome=this.get(i).getGenome();
			content+="Pos: "+i+
					", score="+genome.getScore()+
					", back testing= "+this.get(i).getBackTestingScore()+
					", generation="+genome.getBirthGeneration()+"\n";
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
