// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.ints.prime;

import java.util.Random;

import org.goataa.impl.searchSpaces.trees.math.ints.Context;
import org.goataa.impl.searchSpaces.trees.math.ints.IntFunction;

/**
 * An objective function testing a program's capability to create prime
 * numbers.
 *
 * @author Thomas Weise
 */
public class PrimeObjective2 extends PrimeObjective1 {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Create a new instance of the prime number objective function.
   *
   * @param ctx
   *          the memory size
   */
  public PrimeObjective2(final Context ctx) {
    super(ctx);
  }

  /**
   * Compute the objective value, i.e., determine the utility of the
   * solution candidate x as specified in
   * Definition D2.3.
   *
   * @param x
   *          the phenotype to be rated
   * @param r
   *          the randomizer
   * @return the objective value of x, the lower the better (see
   *         Section 6.3.4)
   */
  @Override
  public double compute(final IntFunction x, final Random r) {
    double d, i;

    i = super.compute(x, r);

    d = Math.log(Math.E + x.getWeight());
    d = Math.nextAfter(d, Double.POSITIVE_INFINITY);
    d = 1d / (Math.ceil(d));

    return (i - d);
  }
}