// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.real;

import java.util.Random;

import org.goataa.impl.algorithms.RandomSampling;
import org.goataa.impl.algorithms.RandomWalk;
import org.goataa.impl.algorithms.hc.HillClimbing;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.searchOperations.strings.real.unary.DoubleArrayAllNormalMutation;
import org.goataa.impl.searchOperations.strings.real.unary.DoubleArrayAllUniformMutation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.BufferedStatistics;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ITerminationCriterion;
import org.goataa.spec.IUnarySearchOperation;

import demos.org.goataa.strings.real.benchmarkFunctions.Sphere;

/**
 * A test of some optimization methods applied to the sphere function (see
 * Section 50.3.1.1). A more extensive test is given in
 * Paragraph 57.1.1.2.1.
 *
 * @author Thomas Weise
 */
public class SphereTest {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final IObjectiveFunction<double[]> f;
    final INullarySearchOperation<double[]> create;
    final IUnarySearchOperation<double[]> uniform, normal;
    final IGPM<double[], double[]> gpm;
    final ITerminationCriterion term;
    double[] x;
    int maxRuns, maxSteps;
    final BufferedStatistics stat;
    int i;

    maxRuns = 10;
    maxSteps = 10000;

    System.out.println("Maximum Number of Steps: " + maxSteps); //$NON-NLS-1$
    System.out.println("Number of Runs         : " + maxRuns); //$NON-NLS-1$
    System.out.println();

    f = new Sphere();
    term = new StepLimit(maxSteps);

    create = new DoubleArrayUniformCreation(10, -3.0, 3.0);
    normal = new DoubleArrayAllNormalMutation(-3.0, 3.0);
    uniform = new DoubleArrayAllUniformMutation(-3.0, 3.0);
    gpm = ((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    stat = new BufferedStatistics();

    // Hill Climbing (Algorithm 26.1)
    stat.clear();
    for (i = 0; i < maxRuns; i++) {
      term.reset();
      x = HillClimbing.hillClimbing(f, create, uniform, gpm, term,
          new Random()).x;
      stat.add(f.compute(x, null));
    }
    System.out.println("HC      + uniform: best =" + stat.min() + //$NON-NLS-1$
        "\n                   med  =" + stat.median() + //$NON-NLS-1$
        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
        "\n                   worst=" + stat.max());//$NON-NLS-1$

    stat.clear();
    for (i = 0; i < maxRuns; i++) {
      term.reset();
      x = HillClimbing.hillClimbing(f, create, normal, gpm, term,
          new Random()).x;
      stat.add(f.compute(x, null));
    }
    System.out.println("\nHC      + normal : best =" + stat.min() + //$NON-NLS-1$
        "\n                   med  =" + stat.median() + //$NON-NLS-1$
        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
        "\n                   worst=" + stat.max());//$NON-NLS-1$

    // Random Walks (Section 8.2)
    stat.clear();
    for (i = 0; i < maxRuns; i++) {
      term.reset();
      x = RandomWalk.randomWalk(f, create, uniform, gpm, term,
          new Random()).x;
      stat.add(f.compute(x, null));
    }
    System.out.println("\nRW      + uniform: best =" + stat.min() + //$NON-NLS-1$
        "\n                   med  =" + stat.median() + //$NON-NLS-1$
        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
        "\n                   worst=" + stat.max());//$NON-NLS-1$

    stat.clear();
    for (i = 0; i < maxRuns; i++) {
      term.reset();
      x = RandomWalk
          .randomWalk(f, create, normal, gpm, term, new Random()).x;
      stat.add(f.compute(x, null));
    }
    System.out.println("\nRW      + normal : best =" + stat.min() + //$NON-NLS-1$
        "\n                   med  =" + stat.median() + //$NON-NLS-1$
        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
        "\n                   worst=" + stat.max());//$NON-NLS-1$

    // Random Sampling (Section 8.1)
    stat.clear();
    for (i = 0; i < maxRuns; i++) {
      term.reset();
      x = RandomSampling
          .randomSampling(f, create, gpm, term, new Random()).x;
      stat.add(f.compute(x, null));
    }
    System.out.println("\nRS               : best =" + stat.min() + //$NON-NLS-1$
        "\n                   med  =" + stat.median() + //$NON-NLS-1$
        "\n                   mean =" + stat.mean() + //$NON-NLS-1$
        "\n                   worst=" + stat.max());//$NON-NLS-1$
  }
}