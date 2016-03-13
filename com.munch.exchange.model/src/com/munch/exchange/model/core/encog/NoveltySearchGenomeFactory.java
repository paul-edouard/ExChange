package com.munch.exchange.model.core.encog;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class NoveltySearchGenomeFactory implements NEATGenomeFactory, Serializable{

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 7045072945261438774L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NoveltySearchGenome factor() {
		return new NoveltySearchGenome();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NoveltySearchGenome factor(Genome other) {
		return new NoveltySearchGenome((NoveltySearchGenome) other);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NoveltySearchGenome factor(List<NEATNeuronGene> neurons,
			List<NEATLinkGene> links, int inputCount, int outputCount) {
		return new NoveltySearchGenome(neurons, links, inputCount, outputCount);
	}

	@Override
	public NoveltySearchGenome factor(Random rnd, NEATPopulation pop, int inputCount,
			int outputCount, double connectionDensity) {
		return new NoveltySearchGenome(rnd, (NoveltySearchPopulation) pop, inputCount, outputCount,
				connectionDensity);
	}

}
