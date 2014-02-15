// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.permutation.binPacking;

import java.util.Arrays;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.utils.TextUtils;

/**
 * We discussed bin packing both in
 * Task 67 and
 * Example E1.1. This object holds the
 * information of an instance of the bin packing problem. We took the
 * example instances from [SchKle2003]: Armin Scholl and Robert Klein. Bin
 * Packing. Friedrich Schiller University of Jena,
 * Wirtschaftswissenschaftliche Fakultaet: Jena, Thuringia, Germany,
 * September 2, 2003. See http://www.wiwi.uni-jena.de/Entscheidung/binpp/
 *
 * @author Thomas Weise
 */
public class BinPackingInstance extends OptimizationModule {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N1C1W1_A = new BinPackingInstance(
      100, new int[] { 50, 100, 99, 99, 96, 96, 92, 92, 91, 88, 87, 86,
          85, 76, 74, 72, 69, 67, 67, 62, 61, 56, 52, 51, 49, 46, 44, 42,
          40, 40, 33, 33, 30, 30, 29, 28, 28, 27, 25, 24, 23, 22, 21, 20,
          17, 14, 13, 11, 10, 7, 7, 3 }, 25, "N1C1W1_A"); //$NON-NLS-1$

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N1C2W2_D = new BinPackingInstance(
      120, new int[] { 50, 120, 99, 98, 98, 97, 96, 90, 88, 86, 82, 82,
          80, 79, 76, 76, 76, 74, 69, 67, 66, 64, 62, 59, 55, 52, 51, 51,
          50, 49, 44, 43, 41, 41, 41, 41, 41, 37, 35, 33, 32, 32, 31, 31,
          31, 30, 29, 23, 23, 22, 20, 20 }, 24, "N1C2W2_D");//$NON-NLS-1$

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N2C1W1_A = new BinPackingInstance(
      100, new int[] { 100, 100, 99, 97, 95, 95, 94, 92, 91, 89, 86, 86,
          85, 84, 80, 80, 80, 80, 80, 79, 76, 76, 75, 74, 73, 71, 71, 69,
          65, 64, 64, 64, 63, 63, 62, 60, 59, 58, 57, 54, 53, 52, 51, 50,
          48, 48, 48, 46, 44, 43, 43, 43, 43, 42, 41, 40, 40, 39, 38, 38,
          38, 38, 37, 37, 37, 37, 36, 35, 34, 33, 32, 30, 29, 28, 26, 26,
          26, 24, 23, 22, 21, 21, 19, 18, 17, 16, 16, 15, 14, 13, 12, 12,
          11, 9, 9, 8, 8, 7, 6, 6, 5, 1 }, 48, "N2C1W1_A");//$NON-NLS-1$

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N2C3W2_C = new BinPackingInstance(
      150, new int[] { 100, 150, 100, 99, 99, 98, 97, 97, 97, 96, 96, 95,
          95, 95, 94, 93, 93, 93, 92, 91, 89, 88, 87, 86, 84, 84, 83, 83,
          82, 81, 81, 81, 78, 78, 75, 74, 73, 72, 72, 71, 70, 68, 67, 66,
          65, 64, 63, 63, 62, 60, 60, 59, 59, 58, 57, 56, 56, 55, 54, 51,
          49, 49, 48, 47, 47, 46, 45, 45, 45, 45, 44, 44, 44, 44, 43, 41,
          41, 40, 39, 39, 39, 37, 37, 37, 35, 35, 34, 32, 31, 31, 30, 28,
          26, 25, 24, 24, 23, 23, 22, 21, 20, 20 }, 41, "N2C3W2_C");//$NON-NLS-1$

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N3C3W4_M = new BinPackingInstance(
      150, new int[] { 200, 150, 100, 100, 100, 99, 99, 98, 98, 98, 98,
          97, 96, 95, 94, 94, 94, 94, 93, 93, 93, 93, 93, 92, 92, 92, 91,
          90, 90, 90, 90, 90, 90, 89, 89, 88, 88, 87, 87, 86, 86, 86, 86,
          86, 85, 85, 85, 85, 84, 84, 83, 83, 83, 82, 82, 82, 82, 82, 81,
          81, 80, 80, 79, 79, 79, 79, 79, 79, 78, 78, 78, 77, 77, 76, 76,
          76, 76, 75, 75, 75, 74, 74, 74, 74, 74, 73, 73, 73, 73, 72, 72,
          71, 69, 69, 69, 69, 68, 68, 68, 67, 67, 66, 65, 65, 65, 63, 63,
          63, 62, 61, 61, 61, 61, 60, 60, 59, 59, 59, 59, 58, 58, 58, 58,
          58, 56, 56, 56, 55, 55, 54, 54, 54, 53, 53, 53, 53, 53, 52, 52,
          52, 52, 51, 51, 51, 51, 51, 50, 50, 49, 49, 49, 48, 48, 47, 46,
          46, 46, 46, 45, 45, 45, 44, 44, 44, 42, 42, 42, 41, 41, 39, 39,
          38, 38, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 36, 36, 36, 36,
          35, 35, 35, 34, 34, 34, 33, 32, 31, 30, 30, 30, 30, 30, 30 },
      88, "N3C3W4_M");//$NON-NLS-1$

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N4C2W1_I = new BinPackingInstance(
      120, new int[] { 500, 120, 100, 100, 100, 100, 100, 99, 99, 99, 99,
          99, 99, 99, 99, 99, 99, 99, 98, 98, 98, 98, 98, 98, 98, 97, 97,
          97, 96, 96, 96, 96, 96, 96, 96, 96, 95, 95, 95, 95, 94, 94, 94,
          94, 94, 93, 92, 92, 92, 92, 91, 91, 91, 91, 91, 91, 90, 90, 90,
          90, 90, 89, 89, 89, 89, 89, 88, 88, 88, 88, 88, 87, 87, 87, 86,
          86, 86, 86, 85, 85, 85, 85, 84, 84, 84, 84, 84, 84, 83, 83, 83,
          83, 83, 83, 82, 82, 82, 82, 82, 82, 82, 82, 81, 81, 81, 81, 81,
          80, 80, 80, 80, 79, 79, 79, 79, 78, 78, 78, 77, 77, 77, 76, 76,
          75, 75, 74, 74, 74, 74, 74, 73, 73, 73, 72, 72, 72, 72, 72, 72,
          72, 72, 71, 71, 71, 71, 71, 70, 70, 70, 70, 70, 70, 70, 70, 69,
          69, 69, 69, 68, 68, 67, 67, 67, 67, 67, 67, 67, 66, 66, 66, 65,
          65, 65, 65, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 63, 63, 63,
          63, 63, 63, 63, 62, 62, 62, 62, 62, 61, 61, 61, 61, 61, 60, 60,
          60, 59, 59, 58, 58, 58, 58, 58, 58, 57, 57, 57, 57, 56, 56, 56,
          56, 55, 55, 55, 55, 55, 55, 54, 54, 54, 54, 53, 53, 53, 52, 52,
          52, 52, 52, 51, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, 50,
          50, 49, 49, 49, 48, 48, 48, 47, 47, 47, 47, 47, 47, 46, 46, 46,
          46, 46, 45, 45, 45, 45, 44, 44, 44, 43, 43, 43, 43, 43, 43, 43,
          42, 42, 42, 42, 42, 42, 42, 42, 41, 41, 41, 41, 41, 41, 40, 40,
          40, 40, 40, 40, 40, 39, 39, 39, 39, 39, 38, 38, 38, 38, 37, 37,
          37, 37, 37, 36, 36, 36, 36, 36, 36, 36, 36, 35, 35, 34, 34, 34,
          34, 34, 34, 34, 34, 33, 33, 33, 33, 33, 32, 32, 31, 31, 31, 31,
          31, 31, 30, 29, 29, 29, 28, 28, 28, 28, 28, 28, 28, 27, 27, 27,
          27, 26, 26, 26, 26, 26, 26, 26, 26, 25, 25, 25, 25, 25, 25, 24,
          24, 24, 24, 24, 24, 24, 24, 24, 23, 23, 23, 22, 22, 22, 21, 21,
          21, 21, 21, 21, 20, 20, 20, 20, 20, 20, 19, 19, 19, 19, 18, 18,
          18, 18, 18, 18, 18, 18, 18, 18, 17, 17, 17, 17, 17, 16, 16, 16,
          16, 16, 15, 15, 15, 15, 15, 14, 14, 14, 14, 14, 13, 13, 13, 13,
          13, 12, 12, 12, 12, 11, 11, 11, 11, 11, 11, 10, 10, 10, 10, 10,
          9, 9, 9, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 5, 5, 5, 5, 5,
          5, 5, 5, 5, 4, 4, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1,
          1, }, 209, "N4C2W1_I");//$NON-NLS-1$

  /** a bin packing instance from [SchKle2003] */
  public static final BinPackingInstance N4C3W1_L = new BinPackingInstance(
      150, new int[] { 500, 150, 100, 100, 100, 100, 100, 99, 99, 99, 98,
          98, 98, 98, 98, 98, 97, 97, 97, 97, 97, 97, 97, 97, 97, 96, 96,
          95, 95, 94, 94, 94, 94, 93, 93, 93, 93, 93, 93, 92, 92, 92, 92,
          92, 92, 91, 91, 91, 91, 91, 90, 89, 89, 88, 88, 88, 88, 88, 87,
          87, 87, 87, 86, 85, 85, 85, 85, 84, 84, 84, 83, 83, 83, 83, 82,
          81, 81, 81, 81, 81, 81, 81, 80, 80, 79, 79, 79, 79, 79, 79, 79,
          79, 78, 78, 78, 78, 78, 78, 77, 77, 77, 77, 77, 77, 77, 76, 76,
          76, 76, 76, 75, 75, 75, 74, 74, 74, 74, 74, 74, 74, 74, 73, 73,
          73, 73, 72, 72, 72, 72, 72, 72, 71, 71, 71, 71, 70, 70, 70, 70,
          70, 69, 69, 69, 69, 68, 68, 68, 68, 67, 67, 67, 67, 67, 66, 66,
          66, 66, 66, 66, 65, 65, 65, 65, 65, 64, 64, 64, 64, 64, 64, 64,
          63, 63, 63, 63, 63, 63, 63, 62, 62, 61, 61, 61, 60, 60, 60, 60,
          59, 59, 59, 59, 59, 58, 58, 58, 58, 57, 57, 57, 57, 57, 57, 57,
          57, 56, 56, 56, 56, 56, 55, 55, 55, 54, 54, 54, 53, 53, 53, 52,
          52, 52, 52, 52, 52, 52, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50,
          49, 49, 49, 49, 49, 48, 48, 48, 48, 48, 48, 48, 48, 48, 47, 47,
          47, 47, 47, 47, 46, 46, 46, 46, 46, 46, 45, 45, 45, 45, 45, 45,
          45, 44, 44, 44, 44, 44, 44, 44, 43, 43, 43, 43, 43, 43, 43, 43,
          43, 43, 42, 42, 42, 42, 42, 42, 42, 41, 41, 41, 41, 40, 40, 40,
          40, 39, 39, 39, 39, 39, 38, 38, 38, 38, 38, 38, 37, 37, 37, 36,
          36, 36, 36, 36, 35, 35, 35, 35, 35, 35, 35, 35, 34, 34, 34, 33,
          32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 31, 31, 31, 31,
          31, 31, 30, 30, 30, 30, 30, 29, 29, 29, 29, 28, 28, 28, 28, 28,
          28, 28, 28, 27, 27, 27, 27, 26, 26, 26, 26, 26, 26, 26, 26, 26,
          26, 25, 25, 25, 25, 25, 25, 25, 24, 24, 24, 23, 23, 23, 23, 23,
          23, 23, 23, 23, 22, 22, 22, 22, 22, 21, 21, 21, 21, 21, 21, 21,
          21, 20, 20, 20, 19, 19, 18, 18, 18, 17, 17, 17, 17, 16, 16, 16,
          15, 15, 14, 14, 14, 14, 14, 14, 13, 13, 13, 13, 13, 13, 13, 12,
          12, 11, 11, 11, 11, 11, 11, 11, 11, 11, 10, 10, 10, 10, 10, 10,
          9, 9, 9, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 5, 5,
          5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1,
          1, }, 163, "N4C3W1_L");//$NON-NLS-1$

  /** a list with the the bin packing instances */
  public static final BinPackingInstance[] ALL_INSTANCES = new BinPackingInstance[] {
      N1C1W1_A, N1C2W2_D, N2C1W1_A, N2C3W2_C, N3C3W4_M, N4C2W1_I, N4C3W1_L };

  /** the size of each bin */
  public final int b;

  /** the sizes of the objects, sorted from the smallest to largest */
  public final int[] a;

  /**
   * the minimum number of bins of size b known to be able to facilitate
   * the objects of sizes a; -1 if unknown
   */
  public final int bestK;

  /** the instance name, null if unknown */
  public final String name;

  /**
   * Instantiate the bin packing instance
   *
   * @param pb
   *          the bin size
   * @param pa
   *          the object sizes
   * @param k
   *          the best solution; -1 if unknown
   * @param pname
   *          the intance name, null if unknown
   */
  public BinPackingInstance(final int pb, final int[] pa, final int k,
      final String pname) {
    super();
    this.b = pb;
    this.a = pa.clone();
    Arrays.sort(this.a);
    this.bestK = k;
    this.name = pname;
  }

  /**
   * Instantiate the bin packing instance
   *
   * @param copy
   *          the bin packing instance to copy
   */
  public BinPackingInstance(final BinPackingInstance copy) {
    this(copy.b, copy.a, copy.bestK, copy.name);
  }

  /**
   * Instantiate the bin packing instance
   *
   * @param copy
   *          the bin packing instance to copy
   * @param on
   *          the override name
   */
  BinPackingInstance(final BinPackingInstance copy, final String on) {
    this(copy.b, copy.a, copy.bestK, (copy.name == null) ? on : (on + '('
        + copy.name + ')'));
  }

  /**
   * Get the name of the optimization module
   *
   * @param longVersion
   *          true if the long name should be returned, false if the short
   *          name should be returned
   * @return the name of the optimization module
   */
  @Override
  public String getName(final boolean longVersion) {
    if (this.name != null) {
      return this.name;
    }

    return super.getName(longVersion);
  }

  /**
   * Get the full configuration which holds all the data necessary to
   * describe this object.
   *
   * @param longVersion
   *          true if the long version should be returned, false if the
   *          short version should be returned
   * @return the full configuration
   */
  @Override
  public String getConfiguration(final boolean longVersion) {
    boolean bx;
    StringBuilder sb;

    bx = (this.getClass() != BinPackingInstance.class);

    if (bx && (this.name != null)) {
      return "";//$NON-NLS-1$
    }

    sb = new StringBuilder();

    if (!bx) {
      if (longVersion) {
        sb.append("sizes=");//$NON-NLS-1$
      }
      TextUtils.toStringBuilder(this.a, sb);
      sb.append(',');
    }

    if (longVersion) {
      sb.append("bin=");//$NON-NLS-1$
    }
    sb.append(this.b);

    sb.append(',');
    if ((this.bestK > 0) && (this.bestK < Integer.MAX_VALUE)) {
      if (longVersion) {
        sb.append("best=");//$NON-NLS-1$
      }
      sb.append(this.bestK);
    }

    return sb.toString();
  }

}