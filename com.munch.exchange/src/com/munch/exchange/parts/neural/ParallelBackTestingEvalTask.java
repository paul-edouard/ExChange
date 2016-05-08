package com.munch.exchange.parts.neural;

import java.util.LinkedList;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.neural.BestGenomes;
import com.munch.exchange.model.core.ib.neural.GenomeEvaluation;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public class ParallelBackTestingEvalTask implements Runnable {
	
	EvolutionaryAlgorithm train;
	
	BestGenomes bestGenomes;
	
	Genome genome;
	
	LinkedList<LinkedList<ExBar>> backTestingBlocks;
	
	private NeuralArchitecture neuralArchitecture;
	
	





	public ParallelBackTestingEvalTask(EvolutionaryAlgorithm train, BestGenomes bestGenomes, Genome genome,
			LinkedList<LinkedList<ExBar>> backTestingBlocks, NeuralArchitecture neuralArchitecture) {
		super();
		this.train = train;
		this.bestGenomes = bestGenomes;
		this.genome = genome;
		this.backTestingBlocks = backTestingBlocks;
		this.neuralArchitecture = neuralArchitecture;
	}







	@Override
	public void run() {
		
//		Calculate the back testing score
		MLMethod method=train.getCODEC().decode(genome);
		NeuralNetworkRating backTestingRating=neuralArchitecture.calculateProfitAndRiskOfBlocks(backTestingBlocks, method);
		
//		Create and add the new evaluation
		GenomeEvaluation g_eval=new GenomeEvaluation(genome, genome.getScore());
		g_eval.setBackTestingScore(backTestingRating.getScore());
		bestGenomes.addGenomeEvaluation(g_eval);

	}

}
