// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.sr;

import java.util.Arrays;
import java.util.Random;

import org.goataa.impl.searchSpaces.trees.math.real.RealContext;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;

/**
 * Some examples to be used for testing the Symbolic Regression (see
 * Section 49.1). capabilities if GP.
 *
 * @author Thomas Weise
 */
public class Examples {

  /** the first example RealFunction */
  public static final ExampleFunct F1 = new ExampleFunct(1, -100, 100) {

    /**
     * compute the result
     *
     * @param d
     *          the vector
     * @return the result
     */
    @Override
    final double compute(final double[] d) {
      return Math.exp(Math.sin(d[0])) + 3d * Math.sqrt(Math.abs(d[0]));
    }
  };

  /** the first example RealFunction */
  public static final ExampleFunct F2 = new ExampleFunct(1, 0, 1) {

    /**
     * compute the result
     *
     * @param d
     *          the vector
     * @return the result
     */
    @Override
    final double compute(final double[] d) {
      return Math.pow(d[0], 0.2) - Math.sin(Math.abs(d[0]));
    }
  };

  /**
   * Create new random training cases for a RealFunction
   *
   * @param f
   *          the RealFunction
   * @param cnt
   *          the number of training cases to create
   * @return the new random training cases
   */
  public static final TrainingCase[] createTrainingCases(
      final ExampleFunct f, final int cnt) {
    double[] d;
    TrainingCase[] tc;
    int i, j;
    Random r;

    r = new Random();
    tc = new TrainingCase[cnt];
    for (i = cnt; (--i) >= 0;) {
      j = f.dim;
      d = new double[j];
      for (; (--j) >= 0;) {
        d[j] = f.min + ((f.max - f.min) * r.nextDouble());
      }
      tc[i] = new TrainingCase(d, f.compute(d));
    }
    Arrays.sort(tc);
    return tc;
  }

  /**
   * Print the training case result
   *
   * @param tc
   *          the training case
   * @param f
   *          the RealFunction
   */
  static final void print(final TrainingCase[] tc, final RealFunction f) {
    int i, j;
    RealContext d;

    d = new RealContext(10000, tc[0].data.length);

    for (i = 0; i < tc.length; i++) {
      d.copy(tc[i].data);
      for (j = 0; j < d.getMemorySize(); j++) {
        System.out.print(String.valueOf(d.read(j)));
        System.out.print('\t');
      }
      System.out.print(String.valueOf(tc[i].result));
      System.out.print('\t');
      System.out.println(f.compute(d));
    }
  }

  /**
   * The internal class for example functions
   *
   * @author Thomas Weise
   */
  private static abstract class ExampleFunct {
    /** the dimension */
    final int dim;

    /** the minimum */
    final double min;

    /** the maximum */
    final double max;

    /**
     * Create a new example RealFunction
     *
     * @param d
     *          the dimension
     * @param mi
     *          the minimum
     * @param ma
     *          the maximum
     */
    ExampleFunct(final int d, final double mi, final double ma) {
      super();
      this.dim = d;
      this.min = mi;
      this.max = ma;
    }

    /**
     * compute the result
     *
     * @param d
     *          the vector
     * @return the result
     */
    abstract double compute(final double[] d);
  }
}