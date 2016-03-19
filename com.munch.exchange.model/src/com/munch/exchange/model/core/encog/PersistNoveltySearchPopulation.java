package com.munch.exchange.model.core.encog;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.neural.hyperneat.FactorHyperNEATGenome;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.HyperNEATGenome;
import org.encog.neural.neat.FactorNEATGenome;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATInnovationList;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;

public class PersistNoveltySearchPopulation implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return NoveltySearchPopulation.class.getSimpleName();
	}

	@Override
	public Object read(InputStream is) {
		long nextInnovationID = 0;
		long nextGeneID = 0;

		final NoveltySearchPopulation result = new NoveltySearchPopulation();
		final NEATInnovationList innovationList = new NEATInnovationList();
		innovationList.setPopulation(result);
		result.setInnovations(innovationList);
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("NOVELTY-SEARCH-POPULATION")
					&& section.getSubSectionName().equals("INNOVATIONS")) {
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					final NEATInnovation innovation = new NEATInnovation();
					final int innovationID = Integer.parseInt(cols.get(1));
					innovation.setInnovationID(innovationID);
					innovation.setNeuronID(Integer.parseInt(cols.get(2)));
					result.getInnovations().getInnovations()
							.put(cols.get(0), innovation);
					nextInnovationID = Math.max(nextInnovationID,
							innovationID + 1);
				}
			} else if (section.getSectionName().equals("NOVELTY-SEARCH-POPULATION")
					&& section.getSubSectionName().equals("SPECIES")) {
				NoveltySearchGenome lastGenome = null;
				BasicSpecies lastSpecies = null;

				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);

					if (cols.get(0).equalsIgnoreCase("s")) {
						lastSpecies = new BasicSpecies();
						lastSpecies.setPopulation(result);
						lastSpecies.setAge(Integer.parseInt(cols.get(1)));
						lastSpecies.setBestScore(CSVFormat.EG_FORMAT.parse(cols
								.get(2)));
						lastSpecies.setGensNoImprovement(Integer.parseInt(cols
								.get(3)));
						result.getSpecies().add(lastSpecies);
					} else if (cols.get(0).equalsIgnoreCase("g")) {
						final boolean isLeader = lastGenome == null;
						lastGenome = new NoveltySearchGenome();
						lastGenome.setInputCount(result.getInputCount());
						lastGenome.setOutputCount(result.getOutputCount());
						lastGenome.setSpecies(lastSpecies);
						lastGenome.setAdjustedScore(CSVFormat.EG_FORMAT
								.parse(cols.get(1)));
						lastGenome.setScore(CSVFormat.EG_FORMAT.parse(cols
								.get(2)));
						lastGenome.setBirthGeneration(Integer.parseInt(cols
								.get(3)));
						
						lastGenome.setBehavior(CSVFormat.EG_FORMAT.parse(cols
								.get(4)));
						lastGenome.setNovelty(CSVFormat.EG_FORMAT.parse(cols
								.get(5)));
						lastGenome.setBirthNovelty(CSVFormat.EG_FORMAT.parse(cols
								.get(6)));
						
						
						lastSpecies.add(lastGenome);
						if (isLeader) {
							lastSpecies.setLeader(lastGenome);
						}
					} else if (cols.get(0).equalsIgnoreCase("n")) {
						final NEATNeuronGene neuronGene = new NEATNeuronGene();
						final int geneID = Integer.parseInt(cols.get(1));
						neuronGene.setId(geneID);

						final ActivationFunction af = EncogFileSection
								.parseActivationFunction(cols.get(2));
						neuronGene.setActivationFunction(af);

						neuronGene.setNeuronType(PersistNEATPopulation
								.stringToNeuronType(cols.get(3)));
						neuronGene
								.setInnovationId(Integer.parseInt(cols.get(4)));
						lastGenome.getNeuronsChromosome().add(neuronGene);
						nextGeneID = Math.max(geneID + 1, nextGeneID);
					} else if (cols.get(0).equalsIgnoreCase("l")) {
						final NEATLinkGene linkGene = new NEATLinkGene();
						linkGene.setId(Integer.parseInt(cols.get(1)));
						linkGene.setEnabled(Integer.parseInt(cols.get(2)) > 0);
						linkGene.setFromNeuronID(Integer.parseInt(cols.get(3)));
						linkGene.setToNeuronID(Integer.parseInt(cols.get(4)));
						linkGene.setWeight(CSVFormat.EG_FORMAT.parse(cols
								.get(5)));
						linkGene.setInnovationId(Integer.parseInt(cols.get(6)));
						lastGenome.getLinksChromosome().add(linkGene);
					}
				}

			} else if (section.getSectionName().equals("NOVELTY-SEARCH-POPULATION")
					&& section.getSubSectionName().equals("CONFIG")) {
				final Map<String, String> params = section.parseParams();

				final String afStr = params
						.get(NEATPopulation.PROPERTY_NEAT_ACTIVATION);

				if (afStr.equalsIgnoreCase(PersistNEATPopulation.TYPE_CPPN)) {
					HyperNEATGenome.buildCPPNActivationFunctions(result
							.getActivationFunctions());
				} else {
					result.setNEATActivationFunction(EncogFileSection
							.parseActivationFunction(params,
									NEATPopulation.PROPERTY_NEAT_ACTIVATION));
				}

				result.setActivationCycles(EncogFileSection.parseInt(params,
						PersistConst.ACTIVATION_CYCLES));
				result.setInputCount(EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT));
				result.setOutputCount(EncogFileSection.parseInt(params,
						PersistConst.OUTPUT_COUNT));
				result.setPopulationSize(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_POPULATION_SIZE));
				result.setSurvivalRate(EncogFileSection.parseDouble(params,
						NEATPopulation.PROPERTY_SURVIVAL_RATE));
				result.setActivationCycles(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_CYCLES));
				
			} else if (section.getSectionName().equals("NOVELTY-SEARCH-POPULATION")
				&& section.getSubSectionName().equals("ARCHIVE")) {
				NoveltySearchGenome lastGenome = null;
//				BasicSpecies lastSpecies = null;

				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					
					if (cols.get(0).equalsIgnoreCase("g")) {
//					final boolean isLeader = lastGenome == null;
					lastGenome = new NoveltySearchGenome();
					lastGenome.setInputCount(result.getInputCount());
					lastGenome.setOutputCount(result.getOutputCount());
//					lastGenome.setSpecies(lastSpecies);
					lastGenome.setAdjustedScore(CSVFormat.EG_FORMAT
							.parse(cols.get(1)));
					lastGenome.setScore(CSVFormat.EG_FORMAT.parse(cols
							.get(2)));
					lastGenome.setBirthGeneration(Integer.parseInt(cols
							.get(3)));
					
					lastGenome.setBehavior(CSVFormat.EG_FORMAT.parse(cols
							.get(4)));
					lastGenome.setNovelty(CSVFormat.EG_FORMAT.parse(cols
							.get(5)));
					lastGenome.setBirthNovelty(CSVFormat.EG_FORMAT.parse(cols
							.get(6)));
					
					
//					lastSpecies.add(lastGenome);
//					if (isLeader) {
//						lastSpecies.setLeader(lastGenome);
//					}
				} else if (cols.get(0).equalsIgnoreCase("n")) {
					final NEATNeuronGene neuronGene = new NEATNeuronGene();
					final int geneID = Integer.parseInt(cols.get(1));
					neuronGene.setId(geneID);

					final ActivationFunction af = EncogFileSection
							.parseActivationFunction(cols.get(2));
					neuronGene.setActivationFunction(af);

					neuronGene.setNeuronType(PersistNEATPopulation
							.stringToNeuronType(cols.get(3)));
					neuronGene
							.setInnovationId(Integer.parseInt(cols.get(4)));
					lastGenome.getNeuronsChromosome().add(neuronGene);
					nextGeneID = Math.max(geneID + 1, nextGeneID);
				} else if (cols.get(0).equalsIgnoreCase("l")) {
					final NEATLinkGene linkGene = new NEATLinkGene();
					linkGene.setId(Integer.parseInt(cols.get(1)));
					linkGene.setEnabled(Integer.parseInt(cols.get(2)) > 0);
					linkGene.setFromNeuronID(Integer.parseInt(cols.get(3)));
					linkGene.setToNeuronID(Integer.parseInt(cols.get(4)));
					linkGene.setWeight(CSVFormat.EG_FORMAT.parse(cols
							.get(5)));
					linkGene.setInnovationId(Integer.parseInt(cols.get(6)));
					lastGenome.getLinksChromosome().add(linkGene);
				}
				}
				
		}
		}

		// set factories
		if (result.isHyperNEAT()) {
			result.setGenomeFactory(new FactorHyperNEATGenome());
			result.setCODEC(new HyperNEATCODEC());
		} else {
			result.setGenomeFactory(new NoveltySearchGenomeFactory());
			result.setCODEC(new NEATCODEC());
		}

		// set the next ID's
		result.getInnovationIDGenerate().setCurrentID(nextInnovationID);
		result.getGeneIDGenerate().setCurrentID(nextGeneID);

		// find first genome, which should be the best genome
		double bestBehavior=Double.NEGATIVE_INFINITY;
		for(final Species species:result.getSpecies()){
			
			for(Genome genome:species.getMembers()){
				if(((NoveltySearchGenome) genome).getBehavior() > bestBehavior){
					bestBehavior=((NoveltySearchGenome) genome).getBehavior();
					result.setBestGenome(genome);
				}
			}
		}
		
//		if (result.getSpecies().size() > 0) {
//			final Species species = result.getSpecies().get(0);
//			if (species.getMembers().size() > 0) {
//				result.setBestGenome(species.getMembers().get(0));
//			}
//		}

		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final NoveltySearchPopulation pop = (NoveltySearchPopulation) obj;
		out.addSection("NOVELTY-SEARCH-POPULATION");
		out.addSubSection("CONFIG");
		out.writeProperty(PersistConst.ACTIVATION_CYCLES,
				pop.getActivationCycles());

		if (pop.isHyperNEAT()) {
			out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION,
					PersistNEATPopulation.TYPE_CPPN);
		} else {
			final ActivationFunction af = pop.getActivationFunctions()
					.getList().get(0).getObj();
			out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION, af);
		}

		out.writeProperty(PersistConst.INPUT_COUNT, pop.getInputCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, pop.getOutputCount());
		out.writeProperty(NEATPopulation.PROPERTY_CYCLES,
				pop.getActivationCycles());
		out.writeProperty(NEATPopulation.PROPERTY_POPULATION_SIZE,
				pop.getPopulationSize());
		out.writeProperty(NEATPopulation.PROPERTY_SURVIVAL_RATE,
				pop.getSurvivalRate());
		out.addSubSection("INNOVATIONS");
		if (pop.getInnovations() != null) {
			for (final String key : pop.getInnovations().getInnovations()
					.keySet()) {
				final NEATInnovation innovation = pop.getInnovations()
						.getInnovations().get(key);
				out.addColumn(key);
				out.addColumn(innovation.getInnovationID());
				out.addColumn(innovation.getNeuronID());
				out.writeLine();
			}
		}

		out.addSubSection("SPECIES");

		// make sure the best species goes first
		final Species bestSpecies = pop.determineBestSpecies();
		if (bestSpecies != null) {
			saveSpecies(out, bestSpecies);
		}

		// now write the other species, other than the best one
		for (final Species species : pop.getSpecies()) {
			if (species != bestSpecies) {
				saveSpecies(out, species);
			}
		}
		
		
		out.addSubSection("ARCHIVE");
		for(NoveltySearchGenome genome:pop.getArchive()){
			saveGenome(out, genome);
		}
		
		
		out.flush();
	}
	
	private void saveSpecies(final EncogWriteHelper out, final Species species) {
		out.addColumn("s");
		out.addColumn(species.getAge());
		out.addColumn(species.getBestScore());
		out.addColumn(species.getGensNoImprovement());
		out.writeLine();

		for (final Genome genome : species.getMembers()) {
			final NoveltySearchGenome neatGenome = (NoveltySearchGenome) genome;
			saveGenome(out, neatGenome);

		}

	}
	
	private void saveGenome(final EncogWriteHelper out, final NoveltySearchGenome neatGenome){
		out.addColumn("g");
		out.addColumn(neatGenome.getAdjustedScore());
		out.addColumn(neatGenome.getScore());
		out.addColumn(neatGenome.getBirthGeneration());
		out.addColumn(neatGenome.getBehavior());
		out.addColumn(neatGenome.getNovelty());
		out.addColumn(neatGenome.getBirthNovelty());
		out.writeLine();

		for (final NEATNeuronGene neatNeuronGene : neatGenome
				.getNeuronsChromosome()) {
			out.addColumn("n");
			out.addColumn(neatNeuronGene.getId());
			out.addColumn(neatNeuronGene.getActivationFunction());
			out.addColumn(PersistNEATPopulation
					.neuronTypeToString(neatNeuronGene.getNeuronType()));
			out.addColumn(neatNeuronGene.getInnovationId());
			out.writeLine();
		}
		for (final NEATLinkGene neatLinkGene : neatGenome
				.getLinksChromosome()) {
			out.addColumn("l");
			out.addColumn(neatLinkGene.getId());
			out.addColumn(neatLinkGene.isEnabled());
			out.addColumn(neatLinkGene.getFromNeuronID());
			out.addColumn(neatLinkGene.getToNeuronID());
			out.addColumn(neatLinkGene.getWeight());
			out.addColumn(neatLinkGene.getInnovationId());
			out.writeLine();
		}
	}
	

	@Override
	public int getFileVersion() {
		return 1;
	}

}
