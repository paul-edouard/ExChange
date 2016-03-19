package com.munch.exchange.model.core.encog;

import java.util.LinkedList;
import java.util.Random;

import org.encog.ml.ea.species.BasicSpecies;
import org.encog.neural.hyperneat.FactorHyperNEATGenome;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.neat.FactorNEATGenome;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;

public class NoveltySearchPopulation extends NEATPopulation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6255665209476348120L;
	
	
	LinkedList<NoveltySearchGenome> archive=new LinkedList<NoveltySearchGenome>();
	

	public NoveltySearchPopulation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NoveltySearchPopulation(int inputCount, int outputCount,
			int populationSize) {
		super(inputCount, outputCount, populationSize);
		// TODO Auto-generated constructor stub
	}

	public NoveltySearchPopulation(Substrate theSubstrate, int populationSize) {
		super(theSubstrate, populationSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset() {
		// create the genome factory
		if (isHyperNEAT()) {
			this.setCODEC( new HyperNEATCODEC());
			setGenomeFactory(new FactorHyperNEATGenome());
		} else {
			this.setCODEC(  new NEATCODEC());
			setGenomeFactory(new NoveltySearchGenomeFactory());
		}
		
		// create the new genomes
		getSpecies().clear();
		
		//Reset the archive
		archive.clear();

		// reset counters
		getGeneIDGenerate().setCurrentID(1);
		getInnovationIDGenerate().setCurrentID(1);

		final Random rnd = this.getRandomNumberFactory().factor();

		// create one default species
		final BasicSpecies defaultSpecies = new BasicSpecies();
		defaultSpecies.setPopulation(this);
		
		// create the initial population
		for (int i = 0; i < getPopulationSize(); i++) {
			final NEATGenome genome = getGenomeFactory().factor(rnd, this,
					this.getInputCount(), this.getOutputCount(),
					this.getInitialConnectionDensity());
//			if(genome instanceof NoveltySearchGenome){
//				System.out.println("OK the genome is of type NoveltySearchGenome!");
//			}
			defaultSpecies.add(genome);
		}
		defaultSpecies.setLeader(defaultSpecies.getMembers().get(0));
		getSpecies().add(defaultSpecies);

		// create initial innovations
		setInnovations(new NEATInnovationList(this));
	}
	
	

	public LinkedList<NoveltySearchGenome> getArchive() {
		return archive;
	}
	
	
	
	

}
