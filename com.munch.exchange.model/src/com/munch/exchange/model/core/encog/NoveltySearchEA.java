package com.munch.exchange.model.core.encog;


import java.util.ArrayList;

import org.encog.ml.MLContext;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.parallel.ParallelScore;
import org.encog.ml.ea.train.basic.TrainEA;

import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

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
		
		
		// Reset the score best compare method based on behavior
		
		if (this.theNoveltyFunction.shouldMinimize()) {
			this.setBestComparator(new MinimizeBehaviorComp());
		} else {
			this.setBestComparator(new MaximizeBehaviorComp());
		}
		
		
	}

	
	@Override
	public void calculateScore(Genome g) {
		
		// try rewrite
		this.getRules().rewrite(g);
		
		// decode
		final MLMethod phenotype = getCODEC().decode(g);
		
		NoveltySearchGenome g_novel=(NoveltySearchGenome) g;
		
		double behavior;
		NeuralNetworkRating rating=null;

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
			rating = theNoveltyFunction.calculateBehavior(phenotype);
			behavior=rating.getScore();
			
			
		}
		g_novel.setRating(rating);
		g_novel.setBehavior(behavior);
		
//		The score is temporaly set to equals to the behavior in oder to get throught the best comparision
		g_novel.setScore(behavior);
		g_novel.setAdjustedScore(behavior);
		
		
	}
	
	

	
	@Override
	public void initializeStartPopulation() {
		final ParallelBehavior pscore = new ParallelBehavior(getPopulation(),
				getCODEC(), new ArrayList<AdjustScore>(), theNoveltyFunction, getScoreFunction(),
				this.getThreadCount());
		pscore.setThreadCount(this.getThreadCount());
		pscore.process();
		this.setThreadCount(pscore.getThreadCount());
	}


	@Override
	public double getError() {
		// do we have a best genome, and does it have an error?
				if (this.getBestGenome() != null) {
					double err = ((NoveltySearchGenome)this.getBestGenome()).getBehavior();
					if( !Double.isNaN(err) ) {
						return err;
					}
				} 
				
				// otherwise, assume the worst!
				if (theNoveltyFunction.shouldMinimize()) {
					return Double.POSITIVE_INFINITY;
				} else {
					return Double.NEGATIVE_INFINITY;
				}
	}


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
