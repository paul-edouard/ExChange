package com.munch.exchange.model.core.encog;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.basic.BasicEA;

public class NoveltySearchEA extends BasicEA {

	public NoveltySearchEA(Population thePopulation,
			CalculateScore theScoreFunction) {
		super(thePopulation, theScoreFunction);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calculateScore(Genome g) {
		// TODO Auto-generated method stub
		super.calculateScore(g);
	}
	
	
	

}
