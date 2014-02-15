// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.real.benchmarkFunctions;

import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;

/**
 * A function f1 which computes the sum of the cosines of the logarithms of
 * all elements in a vector See Equation 26.1 in
 * Task 64.
 *
 * @author Thomas Weise
 */
public final class SumCosLn extends OptimizationModule implements
    IObjectiveFunction<double[]> {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Instantiate the sum-cos-ln objective */
  public SumCosLn() {
    super();
  }

  /**
   * Compute the function value according to
   * Equation 26.1.
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
      res += Math.cos(Math.log(1d + Math.abs(x[i])));
    }

    return (0.5d - ((1d / (x.length << 1)) * res));
  }

  /**
   * Get the name of the optimization module
   *
   * @param longVersion
   *          true if the long name should be returned, false if the short
   *          name should be returned
   * @return the name of the optimization module
   */
  @Override
  public String getName(final boolean longVersion) {
    if (longVersion) {
      return super.getName(true);
    }
    return "f1"; //$NON-NLS-1$
  }
}