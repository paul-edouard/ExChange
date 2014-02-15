// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.moveBased;

import demos.org.goataa.vrpBenchmark.optimization.Compute;

/**
 * A simple objective function for the freight transportation problem
 *
 * @author Thomas Weise
 */
public class SimpleObjective2 extends SimpleObjective {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Create the simple objective */
  public SimpleObjective2() {
    super();
  }

  /**
   * Perform the objective value computation. Here we do a trick:
   * basically, we want to 1) minimize the errors, 2) minimize the time
   * window violations, and 3) minimize the costs. Since we can return only
   * one value, we fold these three objectives in the order of their
   * priority into each other (see the code). Basically: A return value r<1
   * indicates that no errors happened and represents the pure costs. A
   * value 1<=r<2 indicates that some time windows were violated, but not
   * physics. Finally, an error of 2<=r<3 indicates that physical
   * constraints were violated.
   *
   * @param comp
   *          a computer which fully has evaluated the candidate solution
   * @return the objective value
   */
  @Override
  protected double compute(final Compute comp) {
    int a, d;
    long b, c;
    double res;

    a = comp.getAllErrors();
    b = comp.getAllTimeViolations();
    c = comp.getAllCosts();
    d = comp.getOrderErrors();

    res = (1d - (1d / Math.log(c + 3l)));

    if (b > 0) {
      res = (2d - (1d / Math.log((b + 2l) + res)));
    }

    if (a > 0) {
      res = (3d - (1d / Math.log((a + 1l) + res)));
    }

    if (d > 0) {
      res = (4d - (1d / Math.log(d + res)));
    }

    return res;
  }
}