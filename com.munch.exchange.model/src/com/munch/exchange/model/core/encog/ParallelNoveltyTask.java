package com.munch.exchange.model.core.encog;

import java.util.List;

import org.encog.ml.ea.genome.Genome;

public class ParallelNoveltyTask implements Runnable {
	
	
	/**
	 * The genome to calculate the score for.
	 */
	private final NoveltySearchGenome genome;
	
	/**
	 * The score function.
	 */
	private final CalculateNovelty theNoveltyFunction;
	
	
	private final NoveltySearchEA noveltySearchEA;
	
	
	private final List<NoveltySearchGenome> allGenomes;
	
	
	

	public ParallelNoveltyTask(NoveltySearchGenome genome,
			CalculateNovelty theNoveltyFunction,
			NoveltySearchEA noveltySearchEA,
			List<NoveltySearchGenome> allGenomes) {
		super();
		this.genome = genome;
		this.theNoveltyFunction = theNoveltyFunction;
		this.noveltySearchEA = noveltySearchEA;
		this.allGenomes = allGenomes;
	}




	@Override
	public void run() {
		theNoveltyFunction.calculateNovelty(genome, allGenomes, noveltySearchEA.getNbOfNearestNeighbor());

	}

}
