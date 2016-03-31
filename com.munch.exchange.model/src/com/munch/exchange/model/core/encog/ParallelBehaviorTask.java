package com.munch.exchange.model.core.encog;

import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.train.basic.BasicEA;

import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public class ParallelBehaviorTask implements Runnable {


	/**
	 * The genome to calculate the score for.
	 */
	private final Genome genome;
	
	/**
	 * The score function.
	 */
	private final CalculateNovelty theNoveltyFunction;
	
	/**
	 * The score adjusters.
	 */
	private final List<AdjustScore> adjusters;
	
	/**
	 * The owners.
	 */
	private final ParallelBehavior owner;

	/**
	 * Construct the parallel task.
	 * @param genome The genome.
	 * @param theOwner The owner.
	 */
	public ParallelBehaviorTask(Genome genome, ParallelBehavior theOwner) {
		super();
		this.owner = theOwner;
		this.genome = genome;
		this.theNoveltyFunction = theOwner.getNoveltyFunction();
		this.adjusters = theOwner.getAdjusters();
	}

	/**
	 * Perform the task.
	 */
	@Override
	public void run() {
		MLMethod phenotype = this.owner.getCodec().decode(this.genome);
		NoveltySearchGenome g_novel=(NoveltySearchGenome) this.genome;
		
		if (phenotype != null) {
//			double score;
			double behavior=Double.NaN;;
			NeuralNetworkRating rating=null;
			try {
				rating = theNoveltyFunction.calculateBehavior(phenotype);
				behavior=rating.getScore();
			} catch(EARuntimeError e) {
				behavior = Double.NaN;
			}
			
			g_novel.setRating(rating);
//			g_novel.setBehavior(behavior);
			
			genome.setScore(behavior);
			genome.setAdjustedScore(behavior);
			BasicEA.calculateScoreAdjustment(genome, adjusters);
		} else {
			
		}
	}

}
