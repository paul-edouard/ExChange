package com.munch.exchange.model.core.encog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.ml.genetic.GeneticError;
import org.encog.util.concurrency.MultiThreadable;

public class ParallelNovelty implements MultiThreadable {
	
	
	/**
	 * The genomes to calculate the novelty for.
	 */
	private final List<NoveltySearchGenome> genomesToCalculate;
	
	/**
	 * The score function.
	 */
	private final CalculateNovelty theNoveltyFunction;
	
	
	private final NoveltySearchEA noveltySearchEA;
	
	
	private final List<NoveltySearchGenome> allGenomes;
	
	/**
	 * The number of requested threads.
	 */
	private int threads;
	
	/**
	 * The actual number of threads.
	 */
	private int actualThreads;
	

	public ParallelNovelty(List<NoveltySearchGenome> genomesToCalculate,
			CalculateNovelty theNoveltyFunction,
			NoveltySearchEA noveltySearchEA,
			List<NoveltySearchGenome> allGenomes) {
		super();
		this.genomesToCalculate = genomesToCalculate;
		this.theNoveltyFunction = theNoveltyFunction;
		this.noveltySearchEA = noveltySearchEA;
		this.allGenomes = allGenomes;
	}
	
	
	
	/**
	 * Calculate the scores.
	 */
	public void process() {
		// determine thread usage
		if (this.theNoveltyFunction.requireSingleThreaded()) {
			this.actualThreads = 1;
		} else if (threads == 0) {
			this.actualThreads = Runtime.getRuntime().availableProcessors();
		} else {
			this.actualThreads = threads;
		}

		// start up
		ExecutorService taskExecutor = null;

		if (this.threads == 1) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.actualThreads);
		}

		for(NoveltySearchGenome genome:genomesToCalculate){
				taskExecutor.execute(new ParallelNoveltyTask(genome, theNoveltyFunction, noveltySearchEA, allGenomes));
		}

		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new GeneticError(e);
		}
	}
	
	

	@Override
	public int getThreadCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setThreadCount(int numThreads) {
		// TODO Auto-generated method stub

	}

}
