// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.sr;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.utils.TextUtils;

/**
 * A training case
 *
 * @author Thomas Weise
 */
public class TrainingCase extends OptimizationModule implements
    Comparable<TrainingCase> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the data */
  public final double[] data;

  /** the result */
  public final double result;

  /**
   * Create a new training case
   *
   * @param t
   *          the data
   * @param r
   *          the result
   */
  public TrainingCase(final double[] t, double r) {
    super();
    this.data = t;
    this.result = r;
  }

  /**
   * Get the string representation of this object, i.e., the name and
   * configuration.
   *
   * @param longVersion
   *          true if the long version should be returned, false if the
   *          short version should be returned
   * @return the string version of this object
   */
  @Override
  public String toString(final boolean longVersion) {
    String s;

    s = TextUtils.toString(this.data);
    s = s.substring(1, s.length() - 2);

    return "g(" + s + //$NON-NLS-1$
        ")=" + this.result;//$NON-NLS-1$
  }

  /**
   * Compare to another training cases
   *
   * @param tc
   *          the other training case
   * @return the result of that comparison
   */
  public int compareTo(final TrainingCase tc) {
    double[] d1, d2;
    int i, r;

    d1 = this.data;
    d2 = tc.data;
    for (i = 0; i < d1.length; i++) {
      r = Double.compare(d1[i], d2[i]);
      if (r != 0) {
        return r;
      }
    }
    return Double.compare(this.result, tc.result);
  }
}