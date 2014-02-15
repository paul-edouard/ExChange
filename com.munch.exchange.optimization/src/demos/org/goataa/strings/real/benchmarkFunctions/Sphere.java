// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.real.benchmarkFunctions;

import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;

/**
 * The sphere function introduced in Section 50.3.1.1
 * is a very simple benchmark function for real-vector based problem
 * spaces. It just adds up the squares of the elements of the candidate
 * solutions (Definition D2.2).
 *
 * @author Thomas Weise
 */
public final class Sphere extends OptimizationModule implements
    IObjectiveFunction<double[]> {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Instantiate the sphere objective function */
  public Sphere() {
    super();
  }

  /**
   * Compute the value of the sphere function
   * (Section 50.3.1.1)
   *
   * @param x
   *          the candidate solution (Definition D2.2)
   * @param r
   *          the randomizer
   * @return the objective value
   */
  public final double compute(final double[] x, final Random r) {
    double res;
    int i;

    res = 0d;
    for (i = x.length; (--i) >= 0;) {
      res += (x[i] * x[i]);
    }

    return res;
  }

}