// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.permutation.binPacking;

import java.util.Random;

import org.goataa.spec.IObjectiveFunction;

/**
 * The first objective function (f1) mentioned in the bin packing
 * Task 67. An objective function which
 * computes the number of bins of size b required to store n objects with
 * the sizes a0 to an-1 according to a given sequence x. It takes a
 * permutation x of the first n natural numbers (or better, from 0 to n-1)
 * as argument and returns the number of bins required to facilitate the
 * objects.
 *
 * @author Thomas Weise
 */
public class BinPackingNumberOfBins extends BinPackingInstance implements
    IObjectiveFunction<int[]> {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Instantiate the bin packing objective function with the raw
   * parameters.
   *
   * @param pb
   *          the bin size
   * @param pa
   *          the object sizes
   */
  public BinPackingNumberOfBins(final int pb, final int[] pa) {
    super(pb, pa, -1, "f1"); //$NON-NLS-1$
  }

  /**
   * Instantiate the bin packing objective function based on an instance
   * object
   *
   * @param bpi
   *          the bin packing instance
   */
  public BinPackingNumberOfBins(final BinPackingInstance bpi) {
    super(bpi, "f1");//$NON-NLS-1$
  }

  /**
   * Compute the number k of bins required by the object permutation x.
   *
   * @param x
   *          the phenotype to be rated
   * @param r
   *          the randomizer
   * @return the number k of bins
   */
  public final double compute(final int[] x, final Random r) {
    int k, free, size, i;

    free = 0;
    k = 0;
    // for each object in the sequence
    for (i = 0; i < x.length; i++) {
      // get the size of the current object
      size = this.a[x[i]];

      // does the object fit into the current bin?
      if (size <= free) {
        // if so, subtract its size from the remaining free size
        free -= size;
      } else {
        // otherwise, start a new bin and put the object into it
        free = (this.b - size);
        k++;
      }
    }

    return k;
  }
}