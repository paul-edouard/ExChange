package com.munch.exchange.model.core.encog;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;

import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public class NoveltySearchSpeciation extends OriginalNEATSpeciation {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2926336464403147988L;
	
	
	NoveltySearchPopulation population;
	CalculateNovelty calculateNovelty;
	NoveltySearchEA noveltySearchEA;
	

	public NoveltySearchSpeciation(NoveltySearchEA noveltySearchEA, NoveltySearchPopulation population,CalculateNovelty calculateNovelty) {
		super();
		this.population = population;
		this.calculateNovelty = calculateNovelty;
		this.noveltySearchEA = noveltySearchEA;
	}


	@Override
	public void performSpeciation(List<Genome> genomeList) { 
		
//		TODO Minimal Criteria Novelty Search, remove some bad genomes

		
		System.out.println("Start the Speciation for "+genomeList.size()+" genomes");
		
//		Recalculate the novelty of each members
		List<NoveltySearchGenome> newGenomes=convertToNoveltySearchGenome(genomeList);
//		Set the behavior equals to the score if NaN
		for(NoveltySearchGenome nov_genome:newGenomes){
			if(Double.isNaN(nov_genome.getBehavior())){
				nov_genome.setBehavior(nov_genome.getScore());
			}
		}
		
		
		List<NoveltySearchGenome> popGenomes=convertToNoveltySearchGenome(population.flatten());
		List<NoveltySearchGenome> allGenomes=new LinkedList<NoveltySearchGenome>();
		allGenomes.addAll(newGenomes);
		allGenomes.addAll(popGenomes);
		allGenomes.addAll(population.getArchive());
		
		

		
//		Calculate the novelty of the genomes		
//		Recalculate the novelty of the current population
		List<NoveltySearchGenome> genomesToCalculate=new LinkedList<NoveltySearchGenome>();
		genomesToCalculate.addAll(newGenomes);
		genomesToCalculate.addAll(popGenomes);
		ParallelNovelty parallelNovelty=new ParallelNovelty(genomesToCalculate,
				calculateNovelty, noveltySearchEA, allGenomes);
		parallelNovelty.process();
		
//		Update the archive		
		List<Genome> validGenomes= updateArchive(newGenomes);
		
		System.out.println("Population before neat speciation: "+population.size());
		System.out.println("Size of the Archive: "+population.getArchive().size());
		
		printBestGenomes("Old population", popGenomes);
		printBestGenomes("Valid genomes", convertToNoveltySearchGenome(validGenomes));
		
//		Perform the normal speciation the score is now replace with the novelty
		super.performSpeciation(validGenomes);
		
		System.out.println("Population after neat speciation: "+population.size());
		printBestGenomes("New population", convertToNoveltySearchGenome(population.flatten()));
		
//		Clean the relDist Maps of the genomes
		List<NoveltySearchGenome> newPopGenomes=convertToNoveltySearchGenome(population.flatten());
		List<NoveltySearchGenome> toDeleteGenomes=new LinkedList<NoveltySearchGenome>();
		toDeleteGenomes.addAll(popGenomes);
		toDeleteGenomes.removeAll(newPopGenomes);
		Set<NeuralNetworkRating> toDelRatings=new HashSet<NeuralNetworkRating>();
		for(NoveltySearchGenome genome:toDeleteGenomes)
			toDelRatings.add(genome.getRating());
		
		
		for(NoveltySearchGenome genome:newPopGenomes){
			genome.getRating().cleanRelDistMap(toDelRatings);
		}
		for(NoveltySearchGenome genome:population.getArchive()){
			genome.getRating().cleanRelDistMap(toDelRatings);
		}
		
		
	}

	private List<Genome> updateArchive(List<NoveltySearchGenome> newGenomes){
		List<Genome> validGenomes= new LinkedList<Genome>();
		
		for(NoveltySearchGenome genome:newGenomes){	
			
//			Save the first novelty in order to be able to add the genome to the archive
			genome.setBirthNovelty(genome.getNovelty());
			
			BasicEA.calculateScoreAdjustment(genome,this.noveltySearchEA.getScoreAdjusters());
			if(Double.isNaN(genome.getAdjustedScore())){
//				System.out.println("Sorry the genome is no valid!");
				continue;
			}
			validGenomes.add(genome);
			
			
//			Add the new genome to the archive
			if(population.getArchive().isEmpty()){
				population.getArchive().add(genome);continue;
			}
			
			if(population.getArchive().size()==noveltySearchEA.getMaxArchiveSize() &&
					population.getArchive().getLast().getBirthNovelty()>genome.getBirthNovelty()){
				continue;
			}
			
			int i=0;
			boolean isInserted=false;
			for(NoveltySearchGenome neighbor:population.getArchive()){
				if(genome.getBirthNovelty()>neighbor.getBirthNovelty()){
					population.getArchive().add(i, genome);
					isInserted=true;
					break;
				}
				i++;
			}
			
			if(!isInserted)
				population.getArchive().add(genome);
			
			if(population.getArchive().size()>noveltySearchEA.getMaxArchiveSize())
				population.getArchive().removeLast();
			
		}
		
		return validGenomes;
	}
	
	private void printBestGenomes(String message, List<NoveltySearchGenome> genomes){
		if(genomes.isEmpty()){
			System.out.println("\n"+message+ " list is empty!");
			return;
		}
		
		NoveltySearchGenome bestnovelty=getBestNovelty(genomes);
		NoveltySearchGenome bestbehavior=getBestBehavior(genomes);
		
		System.out.println("\n"+message);
		System.out.println("Best novelty: novelty="+bestnovelty.getNovelty()+", behavior="+bestnovelty.getBehavior());
		System.out.println("Best behavior: novelty="+bestbehavior.getNovelty()+", behavior="+bestbehavior.getBehavior());
		
	}
	
	
	private NoveltySearchGenome getBestNovelty(List<NoveltySearchGenome> genomes){
		if(genomes.isEmpty())return null;
		NoveltySearchGenome best=genomes.get(0);
		for(NoveltySearchGenome genome:genomes){
			if(genome.getNovelty() > best.getNovelty())
				best=genome;
		}
		
		return best;
	}
	
	private NoveltySearchGenome getBestBehavior(List<NoveltySearchGenome> genomes){
		if(genomes.isEmpty())return null;
		NoveltySearchGenome best=genomes.get(0);
		for(NoveltySearchGenome genome:genomes){
			if(genome.getBehavior() > best.getBehavior())
				best=genome;
		}
		
		return best;
	}
	
	
	
	private List<NoveltySearchGenome> convertToNoveltySearchGenome(List<Genome> genomeList){
		List<NoveltySearchGenome> novSearchGenomes=new LinkedList<NoveltySearchGenome>();
		for(Genome genome:genomeList){
			if(genome instanceof NoveltySearchGenome){
				novSearchGenomes.add((NoveltySearchGenome) genome);
			}
		}
		return novSearchGenomes;
	}
	
	
	
	
	
	
	
	
	
}
