package com.munch.exchange.model.core.encog;


import java.util.concurrent.Callable;

import org.encog.ml.MLContext;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.basic.TrainEA;

public class NoveltySearchEA extends TrainEA {
	
	
	private CalculateNovelty theNoveltyFunction;
	
	/**
	 * this parameter is used to reduce the size of the Archive
	 */
	private int maxArchiveSize=1000;
	
	/**
	 * set the number of neighbor considerated for the evaluation of the novelty
	 */
	int nbOfNearestNeighbor=100;
	
	

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -5707285229695116729L;

	public NoveltySearchEA(Population thePopulation,
			CalculateNovelty theNoveltyFunction) {
		super(thePopulation, theNoveltyFunction);
		
		
		this.theNoveltyFunction=theNoveltyFunction;
		
	}

	
	@Override
	public void calculateScore(Genome g) {
		
		// try rewrite
		this.getRules().rewrite(g);
		
		// decode
		final MLMethod phenotype = getCODEC().decode(g);
		
		NoveltySearchGenome g_novel=(NoveltySearchGenome) g;
		
		double behavior;

		// deal with invalid decode
		if (phenotype == null) {
			if (getBestComparator().shouldMinimize()) {
				behavior = Double.POSITIVE_INFINITY;
			} else {
				behavior = Double.NEGATIVE_INFINITY;
			}
		} else {
			if (phenotype instanceof MLContext) {
				((MLContext) phenotype).clearContext();
			}
//			score = getScoreFunction().calculateScore(phenotype);
			behavior=theNoveltyFunction.calculateBehavior(phenotype);
			
			
		}
		
		g_novel.setBehavior(behavior);
		
		
		// now set the scores
//		g.setScore(score);
//		g.setAdjustedScore(score);
		
		
		//Calculate now the novelty
		
		
		
	}
	
	

//	@Override
//	public Callable<Object> createWorker(Species species) {
//		return new NoveltySearchWorker(this, species);
//	}

	public int getMaxArchiveSize() {
		return maxArchiveSize;
	}

	public void setMaxArchiveSize(int maxArchiveSize) {
		this.maxArchiveSize = maxArchiveSize;
	}


	public int getNbOfNearestNeighbor() {
		return nbOfNearestNeighbor;
	}


	public void setNbOfNearestNeighbor(int nbOfNearestNeighbor) {
		this.nbOfNearestNeighbor = nbOfNearestNeighbor;
	}

	
	
	
	

}
