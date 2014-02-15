// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.moveBased;

import java.util.Random;

import org.goataa.impl.objectiveFunctions.ObjectiveFunction;

import demos.org.goataa.vrpBenchmark.optimization.Compute;
import demos.org.goataa.vrpBenchmark.optimization.Move;

/**
 * A simple objective function for the freight transportation problem
 *
 * @author Thomas Weise
 */
public class SimpleObjective extends ObjectiveFunction<Move[]> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the fitness computer */
  private final Compute computer;

  /**
   * Create the simple objective
   */
  public SimpleObjective() {
    super();
    this.computer = new Compute();
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
  public double compute(final Move[] x, final Random r) {
    final Compute comp;

    comp = this.computer;
    comp.init();
    comp.move(x);
    comp.finish();
    return this.compute(comp);
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
  protected double compute(final Compute comp) {
    int a;
    long b, c;
    double res;

    a = comp.getOrderErrors() + comp.getTruckErrors();// comp.getAllErrors();
    b = comp.getAllTimeViolations();
    c = comp.getAllCosts();

    res = (1d - (1d / Math.log(c + 3l)));

    if (b > 0) {
      res = (2d - (1d / Math.log((b + 2l) + res)));
    }

    if (a > 0) {
      res = (3d - (1d / Math.log((a + 1l) + res)));
    }

    return res;
  }

}