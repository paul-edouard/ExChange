// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.prime;

import java.util.Arrays;

import org.goataa.impl.OptimizationModule;

/**
 * A buffer for prime numbers
 *
 * @author Thomas Weise
 */
public final class PrimeBuffer extends OptimizationModule {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** The sqare root of the maximum integer. */
  private static final int SQRT_MAX_INT = ((int) (Math
      .sqrt(Integer.MAX_VALUE)));

  /** The sqare root of the maximum integer threshold. */
  private static final int SQRT_MAX_INT_TH = (SQRT_MAX_INT * SQRT_MAX_INT);

  /** the highest possible 32-bit integer prime */
  public static final int MAX_PRIME = 2147483629;// getMaxPrime();

  /** the buffer */
  private final int[] buffer;

  /** the different prime counter */
  private int dc;

  /**
   * create the prime buffer
   *
   * @param maxPrimes
   *          the maximum number of primes
   */
  public PrimeBuffer(final int maxPrimes) {
    super();
    this.buffer = new int[maxPrimes];
  }

  /** clear */
  public final void clear() {
    this.dc = 0;
  }

  /**
   * Check a given number
   *
   * @param i
   *          the number
   * @return true if the result was a new prime
   */
  public final boolean check(final int i) {
    int j, d;
    int[] v;

    if (!(isPrime(i))) {
      return false;
    }

    v = this.buffer;
    d = this.dc;
    j = Arrays.binarySearch(v, 0, d, i);
    if (j >= 0) {
      return false;
    }

    j = (-j - 1);
    System.arraycopy(v, j, v, j + 1, d - j);
    v[j] = i;
    this.dc = (d + 1);
    return true;
  }

  /**
   * check whether a given number is prime or not
   *
   * @param num
   *          the number to check
   * @return <code>true</code> if it is, <code>false</code> otherwise
   */
  public static final boolean isPrime(final int num) {
    int i, m;

    if (num <= 1) {
      return false;
    }
    if (num <= 3) {
      return true;
    }

    if ((num & 1) == 0) {
      return false;
    }

    if (num > MAX_PRIME) {
      return false;
    }

    i = 3;

    if (num < SQRT_MAX_INT_TH) {
      do {
        if ((num % i) <= 0) {
          return false;
        }
        i += 2;
      } while ((i * i) <= num);
    } else {
      m = SQRT_MAX_INT;
      do {
        if ((num % i) <= 0) {
          return false;
        }
        i += 2;
      } while (i <= m);
    }

    return true;
  }
}