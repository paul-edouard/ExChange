package com.munch.exchange.model.core.encog;

import org.encog.ml.ea.opp.CompoundOperator;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.training.opp.NEATCrossover;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import org.encog.neural.neat.training.opp.NEATMutateAddNode;
import org.encog.neural.neat.training.opp.NEATMutateRemoveLink;
import org.encog.neural.neat.training.opp.NEATMutateWeights;
import org.encog.neural.neat.training.opp.links.MutatePerturbLinkWeight;
import org.encog.neural.neat.training.opp.links.MutateResetLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;

public class NoveltySearchUtil {
	
	
	public static NoveltySearchEA constructNoveltySearchTrainer(
			final CalculateNovelty calculateScore, final int inputCount,
			final int outputCount, final int populationSize, double behaviorLimit) {
		final NoveltySearchPopulation pop = new NoveltySearchPopulation(inputCount, outputCount,
				populationSize);
		pop.reset();
		return constructNoveltySearchTrainer(pop, calculateScore, behaviorLimit);
	}

	/**
	 * Construct a NEAT (or HyperNEAT trainer.
	 * @param population The population.
	 * @param calculateScore The score function.
	 * @return The NEAT EA trainer.
	 */
	public static NoveltySearchEA constructNoveltySearchTrainer(final NoveltySearchPopulation population,
			final CalculateNovelty calculateScore, double behaviorLimit) {
		final NoveltySearchEA result = new NoveltySearchEA(population, calculateScore);
		result.setSpeciation(new NoveltySearchSpeciation(result, population, calculateScore) );

		result.setSelection(new NoveltySearchTournamentSelection(result, 4));
		result.addScoreAdjuster(new NoveltySearchScoreAjuster(behaviorLimit));
		
		final CompoundOperator weightMutation = new CompoundOperator();
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectFixed(1),
						new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectFixed(2),
						new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectFixed(3),
						new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectProportion(0.02),
						new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectFixed(1),
						new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectFixed(2),
						new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectFixed(3),
						new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(
				0.1125,
				new NEATMutateWeights(new SelectProportion(0.02),
						new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(
				0.03,
				new NEATMutateWeights(new SelectFixed(1),
						new MutateResetLinkWeight()));
		weightMutation.getComponents().add(
				0.03,
				new NEATMutateWeights(new SelectFixed(2),
						new MutateResetLinkWeight()));
		weightMutation.getComponents().add(
				0.03,
				new NEATMutateWeights(new SelectFixed(3),
						new MutateResetLinkWeight()));
		weightMutation.getComponents().add(
				0.01,
				new NEATMutateWeights(new SelectProportion(0.02),
						new MutateResetLinkWeight()));
		weightMutation.getComponents().finalizeStructure();

		result.setChampMutation(weightMutation);
		result.addOperation(0.5, new NEATCrossover());
		result.addOperation(0.494, weightMutation);
		result.addOperation(0.0005, new NEATMutateAddNode());
		result.addOperation(0.005, new NEATMutateAddLink());
		result.addOperation(0.0005, new NEATMutateRemoveLink());
		result.getOperators().finalizeStructure();

		if (population.isHyperNEAT()) {
			result.setCODEC(new HyperNEATCODEC());
		} else {
			result.setCODEC(new NEATCODEC());
		}

		return result;
	}
	
	

}
