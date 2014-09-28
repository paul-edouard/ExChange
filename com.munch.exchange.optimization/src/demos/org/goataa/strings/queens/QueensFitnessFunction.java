/*
 * Copyright (c) 2010 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.strings.queens;

import java.util.Arrays;
import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.utils.TextUtils;
import org.goataa.spec.IObjectiveFunction;

/**
 * The fitness function computes the number of clashes between the queens
 * on a square grid and combines it with the total number of queens. If it
 * is minimized, it will first minimize the number of clashes and,
 * lexicographically second, maximize the number of queens.
 *
 * @author Thomas Weise
 */
public class QueensFitnessFunction extends OptimizationModule implements
    IObjectiveFunction<boolean[]> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the rows */
  private transient int[] mrows;

  /** the cols */
  private transient int[] mcols;

  /** the diagonals */
  private transient int[] mdiagA;

  /** the diagonals */
  private transient int[] mdiagB;

  /** instantiate */
  public QueensFitnessFunction() {
    super();
  }

  /**
   * Compute the fitness value for the queens problem.
   *
   * @param x
   *          the candidate solution
   * @param r
   *          the randomizer
   * @return a combination of the number of clashes between queens the
   *         total number of queens
   */
  public double compute(final boolean[] x, final Random r) {
    final int c;
    int qc, ec;
    int i, j, idx;
    int[] rows, cols, diagA, diagB;

    c = (int) (Math.sqrt(x.length));
    rows = this.mrows;
    if ((rows == null) || (rows.length < c)) {
      this.mrows = rows = new int[c];
      this.mcols = cols = new int[c];
      this.mdiagA = diagA = new int[c + c /*- 1*/];
      this.mdiagB = diagB = new int[c + c  /*- 1*/];
    } else {
      Arrays.fill(rows = this.mrows, 0);
      Arrays.fill(cols = this.mcols, 0);
      Arrays.fill(diagA = this.mdiagA, 0);
      Arrays.fill(diagB = this.mdiagB, 0);
    }

    qc = 0;
    ec = 0;
    idx = 0;
    for (i = 0; i < c; i++) {
      for (j = 0; j < c; j++) {
        if (x[idx++]) {
          qc++;
          rows[i]++;
          cols[j]++;
          diagA[i + j]++;
          diagB[(c - i - 1) + j]++;
        }
      }
    }

    for (i = c; (--i) >= 0;) {
      ec += Math.max(0, rows[i] - 1);
      ec += Math.max(0, cols[i] - 1);
      ec += Math.max(0, diagA[i] - 1);
      ec += Math.max(0, diagA[i + c] - 1);
      ec += Math.max(0, diagB[i] - 1);
      ec += Math.max(0, diagB[i + c] - 1);
    }

    return this.compose(ec, qc);
  }

  /**
   * Compose the fitness value
   *
   * @param errors
   *          the number of errors
   * @param queens
   *          the number of queens
   * @return the composed objective value
   */
  public final double compose(final int errors, final int queens) {
    return (errors + ((1d / (queens + 2))));
  }

  /**
   * Extract the number of queens from a fitness value
   *
   * @param v
   *          the fitness value
   * @return the associated number of queens
   */
  public final int decomposeQueens(final double v) {
    return Math.max(0,
        ((int) (0.5d + (((1d / (v - Math.floor(v))) - 2d)))));
  }

  /**
   * Extract the number of errors from a fitness value
   *
   * @param v
   *          the fitness value
   * @return the number of errors
   */
  public final int decomposeErrors(final double v) {
    return ((int) (v));
  }

  /**
   * Transform the phenotype to a string
   *
   * @param x
   *          the phenotype
   * @return the string
   */
  public final String toString(final boolean[] x) {
    int idx, dim, i, j;
    StringBuilder sb;

    dim = ((int) (0.5d + Math.sqrt(x.length)));
    idx = 0;
    sb = new StringBuilder();
    outer: for (i = 0; i < dim; i++) {
      if (i > 0) {
        sb.append(TextUtils.NEWLINE);
      }
      for (j = 0; j < dim; j++) {
        if (j > 0) {
          sb.append(' ');
        }
        sb.append(x[idx++] ? 1 : 0);
        if (idx >= x.length) {
          break outer;
        }
      }
    }

    return sb.toString();
  }
}