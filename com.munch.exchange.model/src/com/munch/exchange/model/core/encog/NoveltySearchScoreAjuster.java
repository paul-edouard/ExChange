package com.munch.exchange.model.core.encog;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.AdjustScore;

public class NoveltySearchScoreAjuster implements AdjustScore {
	
	
	double behaviorLimit=0;
	
	
	
	public NoveltySearchScoreAjuster(double behaviorLimit) {
		super();
		this.behaviorLimit = behaviorLimit;
	}



	@Override
	public double calculateAdjustment(Genome genome) {
		
		if(!(genome instanceof NoveltySearchGenome))
			return 0;
		
		
		NoveltySearchGenome nov_genome=(NoveltySearchGenome) genome;
		if(!Double.isNaN(nov_genome.getBehavior())){
			if(nov_genome.getBehavior()<behaviorLimit)
				return Double.NaN;
		}
		
		return 0;
		
	}

}
