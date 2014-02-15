// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.real;

import java.util.List;

import org.goataa.impl.algorithms.RandomSampling;
import org.goataa.impl.algorithms.RandomWalk;
import org.goataa.impl.algorithms.de.DifferentialEvolution1;
import org.goataa.impl.algorithms.ea.SimpleGenerationalEA;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.algorithms.ea.selection.TruncationSelection;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.algorithms.hc.HillClimbing;
import org.goataa.impl.algorithms.sa.SimulatedAnnealing;
import org.goataa.impl.algorithms.sa.temperatureSchedules.Exponential;
import org.goataa.impl.algorithms.sa.temperatureSchedules.Logarithmic;
import org.goataa.impl.searchOperations.strings.real.binary.DoubleArrayWeightedMeanCrossover;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.searchOperations.strings.real.ternary.DoubleArrayDEbin;
import org.goataa.impl.searchOperations.strings.real.ternary.DoubleArrayDEexp;
import org.goataa.impl.searchOperations.strings.real.unary.DoubleArrayAllNormalMutation;
import org.goataa.impl.searchOperations.strings.real.unary.DoubleArrayAllUniformMutation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.BufferedStatistics;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;
import org.goataa.spec.ISelectionAlgorithm;
import org.goataa.spec.ITemperatureSchedule;
import org.goataa.spec.ITernarySearchOperation;
import org.goataa.spec.IUnarySearchOperation;

import demos.org.goataa.strings.real.benchmarkFunctions.CosLnSum;
import demos.org.goataa.strings.real.benchmarkFunctions.MaxSqr;
import demos.org.goataa.strings.real.benchmarkFunctions.Sphere;
import demos.org.goataa.strings.real.benchmarkFunctions.SumCosLn;

/**
 * The application of several optimization methods to the benchmark
 * functions defined in Task 64. This
 * program is a more extensive version of the one given in
 * Paragraph 57.1.1.2.2.
 *
 * @author Thomas Weise
 */
public class FunctionTest {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final IObjectiveFunction<double[]>[] f;
    INullarySearchOperation<double[]> create;
    IUnarySearchOperation<double[]>[] mutate;
    final int maxSteps, runs;
    ITemperatureSchedule[] schedules;
    int i, k, l, n, ri, li, mi;
    HillClimbing<double[], double[]> HC;
    RandomWalk<double[], double[]> RW;
    RandomSampling<double[], double[]> RS;
    SimulatedAnnealing<double[], double[]> SA;
    SimpleGenerationalEA<double[], double[]> GA;
    EvolutionStrategy<double[]> ES;
    DifferentialEvolution1<double[]> DE;
    ISelectionAlgorithm[] sel;
    int[] ns, rhos, lambdas, mus;
    ITernarySearchOperation[] tern;

    maxSteps = 100000;
    runs = 100;

    System.out.println("Maximum Number of Steps: " + maxSteps); //$NON-NLS-1$
    System.out.println("Number of Runs         : " + runs); //$NON-NLS-1$
    System.out.println();

    f = new IObjectiveFunction[] { new Sphere(), new SumCosLn(),
        new CosLnSum(), new MaxSqr() };

    HC = new HillClimbing<double[], double[]>();
    SA = new SimulatedAnnealing<double[], double[]>();
    RW = new RandomWalk<double[], double[]>();
    RS = new RandomSampling<double[], double[]>();
    GA = new SimpleGenerationalEA<double[], double[]>();
    ES = new EvolutionStrategy<double[]>();
    DE = new DifferentialEvolution1<double[]>();

    GA
        .setBinarySearchOperation(DoubleArrayWeightedMeanCrossover.DOUBLE_ARRAY_WEIGHTED_MEAN_CROSSOVER);
    sel = new ISelectionAlgorithm[] { new TournamentSelection(2),
        new TournamentSelection(3), new TournamentSelection(5),
        new TruncationSelection(10), new TruncationSelection(50) };

    schedules = new ITemperatureSchedule[] {//
    new Logarithmic(1d),//
        new Logarithmic(1d),//
        new Logarithmic(0.1d),//
        new Logarithmic(1e-3d),//
        new Logarithmic(1e-4d),//
        new Exponential(1d, 1e-5),//
        new Exponential(1d, 1e-4),//
        new Exponential(0.1d, 1e-5), //
        new Exponential(0.1d, 1e-4), //
        new Exponential(1e-3d, 1e-5),//
        new Exponential(1e-3d, 1e-4),//
        new Exponential(1e-4d, 1e-5), //
        new Exponential(1e-4d, 1e-4) };

    ns = new int[] { 2, 5, 10 };

    rhos = new int[] { 2, 10 };
    lambdas = new int[] { 50, 100, 200 };
    mus = new int[] { 10, 20 };

    for (n = 0; n < ns.length; n++) {

      System.out.println();
      System.out.println();
      System.out.println("Dimensions: " + ns[n]); //$NON-NLS-1$
      System.out.println();

      tern = new ITernarySearchOperation[] {
          new DoubleArrayDEbin(-10d, 10d, 0.3d, 0.7d),
          new DoubleArrayDEbin(-10d, 10d, 0.7d, 0.3d),
          new DoubleArrayDEbin(-10d, 10d, 0.5d, 0.5d),
          new DoubleArrayDEexp(-10d, 10d, 0.3d, 0.7d),
          new DoubleArrayDEexp(-10d, 10d, 0.7d, 0.3d),
          new DoubleArrayDEexp(-10d, 10d, 0.5d, 0.5d), };

      mutate = new IUnarySearchOperation[] {//
      new DoubleArrayAllNormalMutation(-10.0d, 10.0d),//
          new DoubleArrayAllUniformMutation(-10.0d, 10.0d) };
      create = new DoubleArrayUniformCreation(ns[n], -10.0d, 10.0d);

      for (i = 0; i < f.length; i++) {
        System.out.println();
        System.out.println();
        System.out.println(f[i].getClass().getSimpleName());
        System.out.println();

        // Hill Climbing (Algorithm 26.1)
        HC.setObjectiveFunction(f[i]);
        HC.setNullarySearchOperation(create);
        for (k = 0; k < mutate.length; k++) {
          HC.setUnarySearchOperation(mutate[k]);
          testRuns(HC, runs, maxSteps);
        }

        // Simulated Annealing (Algorithm 27.1)
        // and Simulated Quenching
        // (Section 27.3.2)
        SA.setObjectiveFunction(f[i]);
        SA.setNullarySearchOperation(create);
        for (l = 0; l < schedules.length; l++) {
          SA.setTemperatureSchedule(schedules[l]);
          for (k = 0; k < mutate.length; k++) {
            SA.setUnarySearchOperation(mutate[k]);
            testRuns(SA, runs, maxSteps);
          }
        }

        // Generational Genetic/Evolutionary Algorithm
        // (Chapter 28,
        // Section 28.1.4.1,
        // Section 29.3)
        GA.setObjectiveFunction(f[i]);
        GA.setNullarySearchOperation(create);
        for (l = 0; l < sel.length; l++) {
          GA.setSelectionAlgorithm(sel[l]);
          for (k = 0; k < mutate.length; k++) {
            GA.setUnarySearchOperation(mutate[k]);
            testRuns(GA, runs, maxSteps);
          }
        }

        // Evolution Strategies: Algorithm 30.1
        ES.setObjectiveFunction(f[i]);
        ES.setNullarySearchOperation(create);
        ES.setDimension(ns[n]);
        ES.setMinimum(-10d);
        ES.setMaximum(10d);

        for (mi = 0; mi < mus.length; mi++) {
          ES.setMu(mus[mi]);
          for (li = 0; li < lambdas.length; li++) {
            ES.setLambda(lambdas[li]);
            for (ri = 0; ri < rhos.length; ri++) {
              ES.setRho(rhos[ri]);

              ES.setPlus(true);
              testRuns(ES, runs, maxSteps);

              ES.setPlus(false);
              testRuns(ES, runs, maxSteps);
            }
          }
        }

        // Differential Evolution: Chapter 33
        DE.setObjectiveFunction(f[i]);
        DE.setNullarySearchOperation(create);
        for (li = 0; li < lambdas.length; li++) {
          DE.setPopulationSize(lambdas[li]);
          for (mi = 0; mi < tern.length; mi++) {
            DE.setTernarySearchOperation(tern[mi]);
            testRuns(DE, runs, maxSteps);
          }
        }

        // Random Walks (Section 8.2)
        RW.setObjectiveFunction(f[i]);
        RW.setNullarySearchOperation(create);
        for (k = 0; k < mutate.length; k++) {
          RW.setUnarySearchOperation(mutate[k]);
          testRuns(RW, runs, maxSteps);
        }

        // Random Sampling (Section 8.1)
        RS.setObjectiveFunction(f[i]);
        RS.setNullarySearchOperation(create);
        testRuns(RS, runs, maxSteps);
      }
    }
  }

  /**
   * Perform the test runs
   *
   * @param algorithm
   *          the algorithm configuration to test
   * @param runs
   *          the number of runs to perform
   * @param steps
   *          the number of steps to execute per run
   */
  @SuppressWarnings("unchecked")
  private static final void testRuns(
      final ISOOptimizationAlgorithm<?, double[], ?> algorithm,
      final int runs, final int steps) {
    int i;
    BufferedStatistics stat;
    List<Individual<?, double[]>> solutions;
    Individual<?, double[]> individual;

    stat = new BufferedStatistics();
    algorithm.setTerminationCriterion(new StepLimit(steps));

    for (i = 0; i < runs; i++) {
      algorithm.setRandSeed(i);
      solutions = ((List<Individual<?, double[]>>) (algorithm.call()));
      individual = solutions.get(0);
      stat.add(individual.v);
    }

    System.out.println(stat.getConfiguration(false) + ' '
        + algorithm.toString(false));
  }
}