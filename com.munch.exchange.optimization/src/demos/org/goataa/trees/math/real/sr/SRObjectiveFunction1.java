// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.sr;

import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.searchSpaces.trees.math.real.RealContext;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;
import org.goataa.impl.utils.Constants;
import org.goataa.spec.IObjectiveFunction;

/**
 * An objective RealFunction for Symbolic Regression which evaluates a
 * program on basis of test cases. It incorporates the approximation
 * accuracy of the program into the fitness as well as its size.
 *
 * @author Thomas Weise
 */
public class SRObjectiveFunction1 extends OptimizationModule implements
    IObjectiveFunction<RealFunction> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the training data */
  private final TrainingCase[] tc;

  /** the real context */
  private final RealContext rc;

  /**
   * Create a new instance of the symbolic regression objective
   * RealFunction.
   *
   * @param t
   *          the training case
   */
  public SRObjectiveFunction1(final TrainingCase[] t) {
    super();
    this.tc = t;
    this.rc = new RealContext(10000, t[0].data.length);
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
  public double compute(final RealFunction x, final Random r) {
    TrainingCase[] y;
    int i;
    TrainingCase q;
    double res, v;
    RealContext c;

    res = 0d;
    y = this.tc;
    c = this.rc;
    for (i = y.length; (--i) >= 0;) {
      q = y[i];
      c.beginProgram();
      c.copy(q.data);
      v = (q.result - x.compute(c));
      res += (v * v);
      c.endProgram();
    }

    if (Double.isInfinite(res) || Double.isNaN(res)) {
      return Constants.WORST_OBJECTIVE;
    }

    return res;
  }

}