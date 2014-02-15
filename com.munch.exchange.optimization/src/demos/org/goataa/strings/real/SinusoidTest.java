// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.real;

import java.util.List;

import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.gpms.SinusoidMapping;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.BufferedStatistics;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;

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
public class SinusoidTest {

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
    final int maxSteps, runs;
    int i,n, ri, li, mi;
    EvolutionStrategy<double[]> ES;
    int[] ns, rhos, lambdas, mus;

    maxSteps = 500000;
    runs = 10;

    System.out.println("Maximum Number of Steps: " + maxSteps); //$NON-NLS-1$
    System.out.println("Number of Runs         : " + runs); //$NON-NLS-1$
    System.out.println();

    f = new IObjectiveFunction[] { new Sphere(), new SumCosLn(),
        new CosLnSum(), new MaxSqr() };

    ES = new EvolutionStrategy<double[]>();

    ns = new int[] { 20 };

    rhos = new int[] { 10 };
    lambdas = new int[] {  100};
    mus = new int[] { 20 };

    for(i=0; i< f.length;i++){
    for (n = 0; n < ns.length; n++) {

      System.out.println();
      System.out.println();
      System.out.println("Dimensions: " + ns[n]); //$NON-NLS-1$
      System.out.println();



      // Evolution Strategies: Algorithm 30.1
      ES.setObjectiveFunction(f[i]);

      ES.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
      create = new DoubleArrayUniformCreation(ns[n], -1.0d, 1.0d);
      ES.setNullarySearchOperation(create);
      ES.setDimension(ns[n]);
      ES.setMinimum(-1d);
      ES.setMaximum(1d);

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


      ES.setGPM(new SinusoidMapping(ns[n]));
      create = new DoubleArrayUniformCreation(1, -1000.0d, 1000.0d);
      ES.setMinimum(-1000d);
      ES.setMaximum(1000d);
      ES.setNullarySearchOperation(create);
      ES.setDimension(1);

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