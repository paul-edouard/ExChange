// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.permutation.binPacking;

import java.util.List;

import org.goataa.impl.algorithms.RandomSampling;
import org.goataa.impl.algorithms.RandomWalk;
import org.goataa.impl.algorithms.de.DifferentialEvolution1;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.algorithms.hc.HillClimbing;
import org.goataa.impl.algorithms.sa.SimulatedAnnealing;
import org.goataa.impl.algorithms.sa.temperatureSchedules.Exponential;
import org.goataa.impl.algorithms.sa.temperatureSchedules.Logarithmic;
import org.goataa.impl.gpms.RandomKeys;
import org.goataa.impl.searchOperations.strings.permutation.nullary.IntPermutationUniformCreation;
import org.goataa.impl.searchOperations.strings.permutation.unary.IntPermutationMultiSwapMutation;
import org.goataa.impl.searchOperations.strings.permutation.unary.IntPermutationSingleSwapMutation;
import org.goataa.impl.searchOperations.strings.real.ternary.DoubleArrayDEbin;
import org.goataa.impl.searchOperations.strings.real.ternary.DoubleArrayDEexp;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.BufferedStatistics;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;
import org.goataa.spec.ITemperatureSchedule;
import org.goataa.spec.ITernarySearchOperation;
import org.goataa.spec.IUnarySearchOperation;

/**
 * An attempt to solve the bin packing problem introduced in
 * Example E1.1 as stated in
 * Task 67 and
 * Task 70 with some different optimization
 * algorithms, objective functions, and search operations.
 *
 * @author Thomas Weise
 */
public class BinPackingTest {

  /** the objective functions */
  static IObjectiveFunction<int[]>[] FS;

  /**
   * The main routine which executes the experiment described in
   * Task 67
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    BinPackingInstance inst;
    INullarySearchOperation<int[]> create;
    final IUnarySearchOperation<int[]>[] mutate;
    final int maxSteps, runs;
    ITemperatureSchedule[] schedules;
    int i, j, k, l, mi, li, ri;
    HillClimbing<int[], int[]> HC;
    RandomWalk<int[], int[]> RW;
    RandomSampling<int[], int[]> RS;
    SimulatedAnnealing<int[], int[]> SA;
    EvolutionStrategy<int[]> ES;
    DifferentialEvolution1<int[]> DE;
    ITernarySearchOperation[] tern;
    int[] rhos, lambdas, mus;

    maxSteps = 100000;
    runs = 100;

    System.out.println("Maximum Number of Steps: " + maxSteps); //$NON-NLS-1$
    System.out.println("Number of Runs         : " + runs); //$NON-NLS-1$
    System.out.println();

    mutate = new IUnarySearchOperation[] {//
        IntPermutationSingleSwapMutation.INT_PERMUTATION_SINGLE_SWAP_MUTATION,//
        IntPermutationMultiSwapMutation.INT_PERMUTATION_MULTI_SWAP_MUTATION };

    HC = new HillClimbing<int[], int[]>();
    SA = new SimulatedAnnealing<int[], int[]>();
    RW = new RandomWalk<int[], int[]>();
    RS = new RandomSampling<int[], int[]>();
    ES = new EvolutionStrategy<int[]>();
    ES.setGPM(new RandomKeys());
    DE = new DifferentialEvolution1<int[]>();
    DE.setGPM(new RandomKeys());

    rhos = new int[] { 2, 10 };
    lambdas = new int[] { 50, 100, 200 };
    mus = new int[] { 10, 20 };

    for (i = 0; i < BinPackingInstance.ALL_INSTANCES.length; i++) {
      inst = BinPackingInstance.ALL_INSTANCES[i];
      create = new IntPermutationUniformCreation(inst.a.length);

      FS = new IObjectiveFunction[] {//
      new BinPackingNumberOfBins(inst),//
          new BinPackingFreeSpace(inst),//
          new BinPackingNumberOfBinsAndFreeSpace(inst),//
          new BinPackingNumberOfBinsAndLastFree(inst),//
          new BinPackingNumberOfBinsAndMaxFree(inst) };//

      System.out.println();
      System.out.println();
      System.out.println("Instance Name          : " + inst.name); //$NON-NLS-1$
      System.out.println("Best Possible Solution : " + inst.bestK); //$NON-NLS-1$
      System.out.println();

      // Hill Climbing (Algorithm 26.1)
      HC.setNullarySearchOperation(create);
      for (k = 0; k < mutate.length; k++) {
        HC.setUnarySearchOperation(mutate[k]);
        for (j = 0; j < FS.length; j++) {
          HC.setObjectiveFunction(FS[j]);
          testRuns(HC, runs, maxSteps);
        }
      }

      // Simulated Annealing (Algorithm 27.1)
      // and Simulated Quenching (Section 27.3.2)
      SA.setNullarySearchOperation(create);

      schedules = new ITemperatureSchedule[] {//
          new Logarithmic(inst.a.length - inst.bestK + 1),//
          new Logarithmic(2 * (inst.a[inst.a.length - 1] - inst.a[0])),//
          new Exponential(inst.a.length - inst.bestK + 1, 1e-5),//
          new Exponential(2 * (inst.a[inst.a.length - 1] - inst.a[0]),
              1e-5) //
      };
      for (l = 0; l < schedules.length; l++) {
        SA.setTemperatureSchedule(schedules[l]);
        for (k = 0; k < mutate.length; k++) {
          SA.setUnarySearchOperation(mutate[k]);
          for (j = 0; j < FS.length; j++) {
            SA.setObjectiveFunction(FS[j]);
            testRuns(SA, runs, maxSteps);
          }
        }
      }

      // Evolution Strategies: Algorithm 30.1
      ES.setNullarySearchOperation(//
          new org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation(
              inst.a.length, 0d, 1d));
      ES.setDimension(inst.a.length);
      ES.setMinimum(0d);
      ES.setMaximum(1d);

      for (j = 0; j < FS.length; j++) {
        ES.setObjectiveFunction(FS[j]);

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
      }

      // Differential Evolution: Chapter 33
      tern = new ITernarySearchOperation[] {
          new DoubleArrayDEbin(0d, 1d, 0.3d, 0.7d),
          new DoubleArrayDEbin(0d, 1d, 0.7d, 0.3d),
          new DoubleArrayDEbin(0d, 1d, 0.5d, 0.5d),
          new DoubleArrayDEexp(0d, 1d, 0.3d, 0.7d),
          new DoubleArrayDEexp(0d, 1d, 0.7d, 0.3d),
          new DoubleArrayDEexp(0d, 1d, 0.5d, 0.5d), };

      for (j = 0; j < FS.length; j++) {
        DE.setObjectiveFunction(FS[j]);
        DE.setNullarySearchOperation(//
            new org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation(
                inst.a.length, 0d, 1d));
        for (li = 0; li < lambdas.length; li++) {
          DE.setPopulationSize(lambdas[li]);
          for (mi = 0; mi < tern.length; mi++) {
            DE.setTernarySearchOperation(tern[mi]);
            testRuns(DE, runs, maxSteps);
          }
        }
      }

      // Random Walks (Section 8.2)
      RW.setNullarySearchOperation(create);
      for (k = 0; k < mutate.length; k++) {
        RW.setUnarySearchOperation(mutate[k]);
        RW.setObjectiveFunction(FS[0]);
        testRuns(RW, runs, maxSteps);
      }

      // Random Sampling (Section 8.1)
      RS.setNullarySearchOperation(create);
      RS.setObjectiveFunction(FS[0]);
      testRuns(RS, runs, maxSteps);

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
      final ISOOptimizationAlgorithm<?, int[], ?> algorithm,
      final int runs, final int steps) {
    int i;
    BufferedStatistics stat;
    List<Individual<?, int[]>> solutions;
    Individual<?, int[]> individual;

    stat = new BufferedStatistics();
    algorithm.setTerminationCriterion(new StepLimit(steps));

    for (i = 0; i < runs; i++) {
      algorithm.setRandSeed(i);
      solutions = (List) (algorithm.call());
      individual = solutions.get(0);
      stat.add(FS[0].compute(individual.x, null));
    }

    System.out.println(stat.getConfiguration(false) + ' '
        + algorithm.toString(false));
  }
}