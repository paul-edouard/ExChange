// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.ints.prime;

import java.util.Random;

import org.goataa.impl.objectiveFunctions.ObjectiveFunction;
import org.goataa.impl.searchSpaces.trees.math.ints.Context;
import org.goataa.impl.searchSpaces.trees.math.ints.IntFunction;

import demos.org.goataa.trees.math.real.prime.PrimeBuffer;

/**
 * An objective function testing a program's capability to create prime
 * numbers.
 *
 * @author Thomas Weise
 */
public class PrimeObjective1 extends ObjectiveFunction<IntFunction> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** at most 200 steps */
  public static final int MAX_STEPS = 1024;

  /** the memory */
  private final Context mem;

  /** the floor buffer */
  private final PrimeBuffer fb;

  /**
   * Create a new instance of the prime number objective function.
   *
   * @param ctx
   *          the memory size
   */
  public PrimeObjective1(final Context ctx) {
    super();
    this.mem = ctx;
    this.fb = new PrimeBuffer(
        demos.org.goataa.trees.math.real.prime.PrimeObjective1.MAX_STEPS);
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
    int i, rr;
    boolean b;
    PrimeBuffer bf;// , bc, br;
    Context m;

    m = this.mem;

    bf = this.fb;
    bf.clear();

    i = 1;
    while (i < MAX_STEPS) {
      m.beginProgram();
      m.write(0, i);
      m.commit();
      x.compute(m);
      m.endProgram();
      b = m.hasError();
      rr = m.read(m.getMemorySize() - 1);

      if (b) {
        break;
      }

      if (!(bf.check(rr))) {
        break;
      }
      i++;
    }

    return (-i);
  }
}