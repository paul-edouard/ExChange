package com.munch.exchange.model.core.encog;

import java.util.List;
import java.util.Random;

import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public class NoveltySearchGenome extends NEATGenome {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7861162145149450525L;
	
	
	private double novelty=Double.NaN;
	
	private double birthNovelty=Double.NaN;	
	
	private double behavior=Double.NaN;
	
	private double relativeDistance=Double.NaN;
	
	private NeuralNetworkRating rating=null;

	public NoveltySearchGenome() {
		super();
	}

	public NoveltySearchGenome(NoveltySearchGenome other) {
		super(other);
		
		novelty=other.novelty;
		behavior=other.behavior;
		
	}

	public NoveltySearchGenome(List<NEATNeuronGene> neurons,
			List<NEATLinkGene> links, int inputCount, int outputCount) {
		super(neurons, links, inputCount, outputCount);
		
	}

	public NoveltySearchGenome(Random rnd, NoveltySearchPopulation pop, int inputCount,
			int outputCount, double connectionDensity) {
		super(rnd, pop, inputCount, outputCount, connectionDensity);
	}
	

	public double getNovelty() {
		return novelty;
	}

	public void setNovelty(double novelty) {
		this.novelty = novelty;
	}

	public double getBehavior() {
		return behavior;
	}

	public void setBehavior(double behavior) {
		this.behavior = behavior;
	}

	public double getBirthNovelty() {
		return birthNovelty;
	}

	public void setBirthNovelty(double birthNovelty) {
		this.birthNovelty = birthNovelty;
	}
	

	public double getRelativeDistance() {
		return relativeDistance;
	}

	public void setRelativeDistance(double relativeDistance) {
		this.relativeDistance = relativeDistance;
	}
	

	public NeuralNetworkRating getRating() {
		return rating;
	}

	public void setRating(NeuralNetworkRating rating) {
		this.rating = rating;
	}
	
	

}
