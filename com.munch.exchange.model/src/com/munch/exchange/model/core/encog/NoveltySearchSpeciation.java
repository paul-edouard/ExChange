package com.munch.exchange.model.core.encog;

import java.util.LinkedList;
import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;

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
		
		
		List<Genome> validGenomes= new LinkedList<Genome>();
		
//		Calculate the novelty of the new genomes
		for(NoveltySearchGenome genome:newGenomes){
			calculateNovelty(genome, allGenomes);
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
		
		
//		Recalculate the novelty of the current population
		for(NoveltySearchGenome genome:popGenomes){
			calculateNovelty(genome, allGenomes);
		}
		
//		Reduce the list to new genome with the one that 
		
		
		System.out.println("Population before neat speciation: "+population.size());
		System.out.println("Size of the Archive: "+population.getArchive().size());
		
		printBestGenomes("Old population", popGenomes);
		printBestGenomes("Valid genomes", convertToNoveltySearchGenome(validGenomes));
		
//		Perform the normal speciation the score is now replace with the novelty
		super.performSpeciation(validGenomes);
		
		System.out.println("Population after neat speciation: "+population.size());
		printBestGenomes("New population", convertToNoveltySearchGenome(population.flatten()));
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
	
	
	private void calculateNovelty(NoveltySearchGenome n_genome, List<NoveltySearchGenome> allGenomes){
		
		LinkedList<NoveltySearchGenome> nearestNeighbors=extractNeighbors(n_genome, allGenomes);
		double novelty=0;
		for(NoveltySearchGenome neighbor:nearestNeighbors){
			novelty+=neighbor.getRelativeDistance();
		}
		novelty/=nearestNeighbors.size();
	
		n_genome.setNovelty(novelty);
		
//		System.out.println("Genome: novelty="+n_genome.getNovelty()+", behavior="+n_genome.getBehavior());
		
//		Now the score is really set equals to the novelty
		n_genome.setScore(novelty);
		n_genome.setAdjustedScore(novelty);
		
	}
	
	
	private LinkedList<NoveltySearchGenome> extractNeighbors(NoveltySearchGenome n_genome, List<NoveltySearchGenome> allGenomes){
		
		LinkedList<NoveltySearchGenome> nearestNeighbors=new LinkedList<NoveltySearchGenome>();
		
		for(NoveltySearchGenome genome:allGenomes){
			if(genome==n_genome)continue;
			
//			double relativeDistance=Math.abs(genome.getBehavior()-n_genome.getBehavior());
			genome.setRelativeDistance(Math.abs(genome.getBehavior()-n_genome.getBehavior()));
			
			if(nearestNeighbors.isEmpty()){
				nearestNeighbors.add(genome);
				continue;
			}
			
//			The current relative distance is lower than the lowest one of the current neighbors
			if(nearestNeighbors.size()==noveltySearchEA.getNbOfNearestNeighbor() &&
					nearestNeighbors.getLast().getRelativeDistance() < genome.getRelativeDistance()){
				continue;
			}
			
			int i=0;
			boolean isInserted=false;
			for(NoveltySearchGenome neighbor:nearestNeighbors){
				if(genome.getRelativeDistance() < neighbor.getRelativeDistance()){
					nearestNeighbors.add(i, genome);
					isInserted=true;
					break;
				}
				i++;
			}
			
			if(!isInserted)
				nearestNeighbors.add(genome);
			
			if(nearestNeighbors.size()>noveltySearchEA.getNbOfNearestNeighbor())
				nearestNeighbors.removeLast();
			
		}
		
		return nearestNeighbors;
		
	}
	
	
	
	
}
