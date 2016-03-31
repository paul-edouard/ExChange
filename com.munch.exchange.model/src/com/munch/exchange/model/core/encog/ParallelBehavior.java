package com.munch.exchange.model.core.encog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.parallel.ParallelScoreTask;
import org.encog.ml.ea.species.Species;
import org.encog.ml.genetic.GeneticError;
import org.encog.util.concurrency.MultiThreadable;

public class ParallelBehavior implements MultiThreadable {
		/**
		 * The population to score.
		 */
		private final Population population;
		
		/**
		 * The CODEC used to create genomes.
		 */
		private final GeneticCODEC codec;
		
		/**
		 * The novelty function.
		 */
		private final CalculateNovelty theNoveltyFunction;
		
		/**
		 * The scoring function.
		 */
		private final CalculateScore scoreFunction;
		
		/**
		 * The score adjuster.
		 */
		private final List<AdjustScore> adjusters;
		
		/**
		 * The number of requested threads.
		 */
		private int threads;
		
		/**
		 * The actual number of threads.
		 */
		private int actualThreads;

		/**
		 * Construct the parallel score calculation object.
		 * @param thePopulation The population to score.
		 * @param theCODEC The CODEC to use.
		 * @param theAdjusters The score adjusters to use.
		 * @param theScoreFunction The score function.
		 * @param theThreadCount The requested thread count.
		 */
		public ParallelBehavior(Population thePopulation, GeneticCODEC theCODEC,
				List<AdjustScore> theAdjusters, CalculateNovelty theNoveltyFunction,
				CalculateScore scoreFunction, int theThreadCount) {
			this.codec = theCODEC;
			this.population = thePopulation;
			this.theNoveltyFunction = theNoveltyFunction;
			this.scoreFunction = scoreFunction;
			this.adjusters = theAdjusters;
			this.actualThreads = 0;
		}

		/**
		 * @return the population
		 */
		public Population getPopulation() {
			return population;
		}

		/**
		 * @return the scoreFunction
		 */
		public CalculateNovelty getNoveltyFunction() {
			return theNoveltyFunction;
		}

		/**
		 * @return the codec
		 */
		public GeneticCODEC getCodec() {
			return codec;
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

			for (Species species : this.population.getSpecies()) {
				for (Genome genome : species.getMembers()) {
					taskExecutor.execute(new ParallelBehaviorTask(genome, this));
				}
			}

			taskExecutor.shutdown();
			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new GeneticError(e);
			}
		}

		/**
		 * @return The score adjusters.
		 */
		public List<AdjustScore> getAdjusters() {
			return this.adjusters;
		}

		/**
		 * @return The desired number of threads.
		 */
		@Override
		public int getThreadCount() {
			return this.threads;
		}

		/**
		 * @param numThreads The desired thread count.
		 */
		@Override
		public void setThreadCount(int numThreads) {
			this.threads = numThreads;
		}

}
