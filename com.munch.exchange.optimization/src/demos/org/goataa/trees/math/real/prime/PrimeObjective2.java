// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.prime;

import java.util.Random;

import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;

/**
 * An objective RealFunction testing a program's capability to create prime
 * numbers.
 *
 * @author Thomas Weise
 */
public class PrimeObjective2 extends PrimeObjective1 {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Create a new instance of the prime number objective RealFunction.
   *
   * @param ms
   *          the memory size
   */
  public PrimeObjective2(final int ms) {
    super(ms);
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
  public final double compute(final RealFunction x, final Random r) {
    double d, i;

    i = super.compute(x, r);

    d = x.getWeight();
    d = Math.log(Math.max(d, Math.E));
    d = Math.log(Math.max(d, Math.E));

    d = (i / d);
    return (-d);
  }
}