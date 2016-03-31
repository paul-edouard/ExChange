package com.munch.exchange.model.core.encog;

import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public interface CalculateNovelty extends CalculateScore {
	
	
	void calculateNovelty(NoveltySearchGenome n_genome, List<NoveltySearchGenome> allGenomes, int nbOfNearestNeighbor);
	
	NeuralNetworkRating calculateBehavior(MLMethod method);

}
