package com.munch.exchange.model.core.encog;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.sort.MinimizeScoreComp;

public class MinimizeBehaviorComp extends MinimizeScoreComp {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6438334995307100348L;

	@Override
	public boolean isBetterThan(Genome prg, Genome betterThan) {
		
		if(prg instanceof NoveltySearchGenome  &&
				betterThan instanceof NoveltySearchGenome){
			return this.isBetterThan(((NoveltySearchGenome)prg).getBehavior(),
					((NoveltySearchGenome)betterThan).getBehavior());
		}
		
		
		return super.isBetterThan(prg, betterThan);
	}
	
	
	

}
