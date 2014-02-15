// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.sr;

import java.util.Random;

import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;

/**
 * An objective RealFunction for Symbolic Regression which evaluates a program
 * on basis of test cases. It incorporates the approximation accuracy of
 * the program into the fitness as well as its size.
 *
 * @author Thomas Weise
 */
public class SRObjectiveFunction2 extends SRObjectiveFunction1 {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Create a new instance of the symbolic regression objective RealFunction.
   *
   * @param t
   *          the training case
   */
  public SRObjectiveFunction2(final TrainingCase[] t) {
    super(t);
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
    int i;
    double res;

    res = super.compute(x, r);

    i = Math.max(3, x.getWeight());

    return (res * Math.log(i));
  }

}