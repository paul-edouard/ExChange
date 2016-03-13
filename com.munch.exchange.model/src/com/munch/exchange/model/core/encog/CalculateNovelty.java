package com.munch.exchange.model.core.encog;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

public interface CalculateNovelty extends CalculateScore {
	
	
	double calculateNovelty(MLMethod method, NoveltySearchPopulation population);
	
	double calculateBehavior(MLMethod method);

}
