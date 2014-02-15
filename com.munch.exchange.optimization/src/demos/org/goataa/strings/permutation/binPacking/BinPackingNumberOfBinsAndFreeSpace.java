// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.permutation.binPacking;

import java.util.Random;

import org.goataa.spec.IObjectiveFunction;

/**
 * The third objective function (f3) mentioned in the bin packing
 * Task 67. It combines the objectives
 * BinPackingNumberOfBins(f1) and BinPackingFreeSpace (f2) to f3(x)= f1(x)
 * - 1/(1+f2(x))
 *
 * @author Thomas Weise
 */
public class BinPackingNumberOfBinsAndFreeSpace extends BinPackingInstance
    implements IObjectiveFunction<int[]> {

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
  public BinPackingNumberOfBinsAndFreeSpace(final int pb, final int[] pa) {
    super(pb, pa, -1, "f3"); //$NON-NLS-1$
  }

  /**
   * Instantiate the bin packing objective function based on an instance
   * object
   *
   * @param bpi
   *          the bin packing instance
   */
  public BinPackingNumberOfBinsAndFreeSpace(final BinPackingInstance bpi) {
    super(bpi, "f3");//$NON-NLS-1$
  }

  /**
   * Compute f3(x)= f1(x) - 1/(1+f2(x)).
   *
   * @param x
   *          the phenotype to be rated
   * @param r
   *          the randomizer
   * @return the number k of bins
   */
  public final double compute(final int[] x, final Random r) {
    int k, leftOver, free, size, i;

    free = 0;
    leftOver = 0;
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
        // add up the left over (waste) space
        leftOver += free;
        // otherwise, start a new bin and put the object into it
        free = (this.b - size);
        k++;
      }
    }

    // f3(x)= f1(x) - 1/(1+f2(x))
    return (k - ((1d / ((((double) leftOver) * leftOver) + 1d))));
  }
}