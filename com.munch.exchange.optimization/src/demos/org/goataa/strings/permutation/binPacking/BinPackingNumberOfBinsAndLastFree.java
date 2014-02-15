// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.permutation.binPacking;

import java.util.Random;

import org.goataa.spec.IObjectiveFunction;

/**
 * The fourth objective function (f5) mentioned in the bin packing
 * Task 67. An objective function which
 * computes the number of bins of size b required to store n objects with
 * the sizes a0 to an-1 according to a given sequence x. It takes a
 * permutation x of the first n natural numbers (or better, from 0 to n-1)
 * as argument and returns the number of bins required to facilitate the
 * objects. Different from f1, it also considers the space left over in the
 * last bin. Different from f1, it also considers the maximum space left
 * over in any bin. The idea is that if the space wasted in one bin
 * gradually increases, sooner or later, the bin may disappear alltogethre.
 *
 * @author Thomas Weise
 */
public class BinPackingNumberOfBinsAndLastFree extends BinPackingInstance
    implements IObjectiveFunction<int[]> {

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
  public BinPackingNumberOfBinsAndLastFree(final int pb, final int[] pa) {
    super(pb, pa, -1, "f5"); //$NON-NLS-1$
  }

  /**
   * Instantiate the bin packing objective function based on an instance
   * object
   *
   * @param bpi
   *          the bin packing instance
   */
  public BinPackingNumberOfBinsAndLastFree(final BinPackingInstance bpi) {
    super(bpi, "f5");//$NON-NLS-1$
  }

  /**
   * Compute the number k of bins required by the object permutation x and
   * include the space wasted in the last bin in the objective value.
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

    // We want to minimize the number of bins and maximize the free space
    // in the last bin (now stored in "free"). k is a natural number and
    // should be the dominating factor. We can add a value in [0,1) to
    // represent the free space. This factor should be the smaller, the
    // larger the free space is. By choosing 1/(1+free), we can realize
    // this. Amongst two solutions which have the same number of bins, now
    // the one with more free space will be preferred.
    return k + (1d / (1d + free));
  }
}