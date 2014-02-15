// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.prime;

import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.searchSpaces.trees.math.real.RealContext;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;
import org.goataa.spec.IObjectiveFunction;

/**
 * An objective RealFunction testing a program's capability to create prime
 * numbers.
 *
 * @author Thomas Weise
 */
public class PrimeObjective1 extends OptimizationModule implements
    IObjectiveFunction<RealFunction> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** at most 200 steps */
  public static final int MAX_STEPS = 1024;

  /** the memory */
  private final RealContext mem;

  /** the floor buffer */
  private final PrimeBuffer fb;

  /**
   * Create a new instance of the prime number objective RealFunction.
   *
   * @param ms
   *          the memory size
   */
  public PrimeObjective1(final int ms) {
    super();
    this.mem = new RealContext(10000, ms);
    this.fb = new PrimeBuffer(MAX_STEPS);
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
  public double compute(final RealFunction x, final Random r) {
    int i;
    double d;
    PrimeBuffer bf;// , bc, br;
    RealContext m;

    m = this.mem;

    bf = this.fb;
    bf.clear();

    i = 1;
    while (i < MAX_STEPS) {
      m.beginProgram();
      m.write(0, i);
      d = x.compute(m);
      m.endProgram();
      if (m.hasError()) {
        break;
      }
      if ((d < 1d) || (d >= Integer.MAX_VALUE)) {
        break;
      }
      if (!(bf.check((int) (Math.floor(d) + 0.5d)))) {
        break;
      }
      i++;
    }

    return (-i);
  }

}