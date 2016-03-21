package com.munch.exchange.model.core.ib.neural;

import org.encog.ml.ea.genome.Genome;

public class GenomeEvaluation {
	
	
	Genome genome;
	double backTestingScore;
	double score;
	
	
	public GenomeEvaluation(Genome genome) {
		super();
		this.genome = genome;
		this.score=this.genome.getScore();
	}
	
	public GenomeEvaluation(Genome genome, double score) {
		super();
		this.genome = genome;
		this.score=score;
	}
	


	public Genome getGenome() {
		return genome;
	}


	public double getBackTestingScore() {
		return backTestingScore;
	}
	
	


	public void setBackTestingScore(double backTestingScore) {
		this.backTestingScore = backTestingScore;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(backTestingScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((genome == null) ? 0 : genome.hashCode());
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	
	
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenomeEvaluation other = (GenomeEvaluation) obj;
		if (Double.doubleToLongBits(backTestingScore) != Double
				.doubleToLongBits(other.backTestingScore))
			return false;
		if (genome == null) {
			if (other.genome != null)
				return false;
		} else if (!genome.equals(other.genome))
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		return true;
	}


	public double getScore() {
		return score;
	}


	public void setScore(double score) {
		this.score = score;
	}


	
	
	
	
	

}
