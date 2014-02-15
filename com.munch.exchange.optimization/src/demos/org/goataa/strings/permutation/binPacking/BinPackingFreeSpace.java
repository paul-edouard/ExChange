// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.permutation.binPacking;

import java.util.Random;

import org.goataa.spec.IObjectiveFunction;

/**
 * The second objective function (f2) mentioned in the bin packing
 * Task 67. An objective function which
 * computes the squares of the free space left over in each bins of size b
 * required to store n objects with the sizes a0 to an-1 according to a
 * given sequence x. It takes a permutation x of the first n natural
 * numbers (or better, from 0 to n-1) as argument and returns the free
 * space left over in the bins.
 *
 * @author Thomas Weise
 */
public class BinPackingFreeSpace extends BinPackingInstance implements
    IObjectiveFunction<int[]> {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Instantiate the bin packing objective function with the raw parameters
   *
   * @param pb
   *          the bin size
   * @param pa
   *          the object sizes
   */
  public BinPackingFreeSpace(final int pb, final int[] pa) {
    super(pb, pa, -1, "f2"); //$NON-NLS-1$
  }

  /**
   * Instantiate the bin packing objective function based on an instance
   * object
   *
   * @param bpi
   *          the bin packing instance
   */
  public BinPackingFreeSpace(final BinPackingInstance bpi) {
    super(bpi, "f2");//$NON-NLS-1$
  }

  /**
   * Compute the quares of the wasted space in the bins required by the
   * object permutation x.
   *
   * @param x
   *          the phenotype to be rated
   * @param r
   *          the randomizer
   * @return the number k of bins
   */
  public final double compute(final int[] x, final Random r) {
    int leftOver, free, size, i;

    free = 0;
    leftOver = 0;
    // for each object in the sequence
    for (i = 0; i < x.length; i++) {
      // get the size of the current object
      size = this.a[x[i]];

      // does the object fit into the current bin?
      if (size <= free) {
        // if so, subtract its size from the remaining free size
        free -= size;
      } else {
        // add up the left-over, wasted space
        leftOver += free;
        // otherwise, start a new bin and put the object into it
        free = (this.b - size);
      }
    }

    return (((double) leftOver) * leftOver);
  }
}