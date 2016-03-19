package com.munch.exchange.model.core.encog;

import java.io.Serializable;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.selection.SelectionOperator;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.ea.train.basic.BasicEA;

public class NoveltySearchTournamentSelection implements Serializable,
		SelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The number of rounds.
	 */
	private int rounds;
	
	/**
	 * The trainer being used.
	 */
	private EvolutionaryAlgorithm trainer;
	
	
	public NoveltySearchTournamentSelection(final EvolutionaryAlgorithm theTrainer, 
			final int theRounds) {
		super();
		this.trainer = theTrainer;
		this.rounds = theRounds;
	}

	@Override
	public int performSelection(Random rnd, Species species) {
		
//		System.out.println("Tournament Started! Species size="+species.getMembers().size());
		
		int bestIndex = rnd.nextInt(species.getMembers().size());
		NoveltySearchGenome best = (NoveltySearchGenome) species.getMembers().get(bestIndex);
		BasicEA.calculateScoreAdjustment(best, this.trainer.getScoreAdjusters());
		
//		System.out.println("Random Selection: behavior="+best.getBehavior()+", score="+best.getScore()+", ajusted scrore="+best.getAdjustedScore()+", novelty="+best.getNovelty());

		for (int i = 0; i < this.rounds; i++) {
			final int competitorIndex = rnd.nextInt(species.getMembers().size());
			final NoveltySearchGenome competitor =(NoveltySearchGenome) species.getMembers().get(competitorIndex);
			BasicEA.calculateScoreAdjustment(competitor,this.trainer.getScoreAdjusters());
			
//			System.out.println("Round="+i+" -> Competitor: behavior="+competitor.getBehavior()+", score="+competitor.getScore()+", ajusted scrore="+competitor.getAdjustedScore()+", novelty="+competitor.getNovelty());

			
			// only evaluate valid genomes
			if (!Double.isInfinite(competitor.getAdjustedScore())
					&& !Double.isNaN(competitor.getAdjustedScore())) {
				
				if (competitor.getNovelty()>best.getNovelty()) {
					best = competitor;
					bestIndex = competitorIndex;
				}
			}
			
//			System.out.println("Best: behavior="+best.getBehavior()+", score="+best.getScore()+", ajusted scrore="+best.getAdjustedScore()+", novelty="+best.getNovelty());

			
			
		}
		return bestIndex;
	}

	@Override
	public int performAntiSelection(Random rnd, Species species) {
		int worstIndex = rnd.nextInt(species.getMembers().size());
		NoveltySearchGenome worst = (NoveltySearchGenome)species.getMembers().get(worstIndex);
		BasicEA.calculateScoreAdjustment(worst,
				this.trainer.getScoreAdjusters());
		
		System.out.println("Perform Selection: behavior="+worst.getBehavior()+", score="+worst.getScore()+", ajusted scrore="+worst.getAdjustedScore()+", novelty="+worst.getNovelty());


		for (int i = 0; i < this.rounds; i++) {
			final int competitorIndex = rnd.nextInt(species.getMembers().size());
			final Genome competitor = species.getMembers().get(competitorIndex);

			// force an invalid genome to lose
			if (Double.isInfinite(competitor.getAdjustedScore())
					|| Double.isNaN(competitor.getAdjustedScore())) {
				return competitorIndex;
			}

			BasicEA.calculateScoreAdjustment(competitor,
					this.trainer.getScoreAdjusters());
			if (!this.trainer.getSelectionComparator().isBetterThan(competitor,
					worst)) {
				worst = (NoveltySearchGenome)competitor;
				worstIndex = competitorIndex;
			}
		}
		return worstIndex;
	}

	@Override
	public EvolutionaryAlgorithm getTrainer() {
		return this.trainer;
	}

}
