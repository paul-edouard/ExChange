// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.utils;

import java.io.Serializable;
import java.util.Random;

import demos.org.goataa.vrpBenchmark.objects.DistanceMatrix;
import demos.org.goataa.vrpBenchmark.objects.InputObject;
import demos.org.goataa.vrpBenchmark.objects.InputObjectList;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.optimization.Compute;

/**
 * A simple utility class which hels us keeping track of used objects
 *
 * @param <T>
 *          the input object type
 * @author Thomas Weise
 */
public abstract class ObjectManager<T extends InputObject> implements
    Serializable {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the list used for copying data */
  private static int[] cpy = null;

  /** the object list */
  private final InputObjectList<T> list;

  /** the free indices */
  private final int[] free;

  /** the object count */
  private int count;

  /**
   * Create a new object manager
   *
   * @param l
   *          the object list
   */
  ObjectManager(final InputObjectList<T> l) {
    super();

    int s;

    this.list = l;
    s = l.size();
    this.free = new int[s];

    if ((cpy == null) || (cpy.length < s)) {
      cpy = new int[s];
      for (; (--s) >= 0;) {
        cpy[s] = s;
      }
    }
  }

  /**
   * Are all objects used already
   *
   * @return true if there is no more free object in this lit
   */
  public final boolean isEmpty() {
    return (this.count <= 0);
  }

  /** Initialize */
  public final void init() {
    this.count = this.free.length;
    System.arraycopy(cpy, 0, this.free, 0, this.free.length);
  }

  /**
   * remove an element
   *
   * @param object
   *          the object to remove
   */
  public final void remove(final T object) {
    int q, i, c;
    int[] x;

    q = object.id;
    x = this.free;
    c = this.count;

    if (q < c) {
      x[q] = x[--c];
    } else {
      outer: for (i = c; (--i) >= 0;) {
        if (x[i] == q) {
          x[i] = x[--c];
          break outer;
        }
      }
    }

    this.count = c;
  }

  /**
   * Get a random element
   *
   * @param r
   *          the randomizer
   * @param delete
   *          should we delete the object?
   * @return the random object
   */
  public final T getRandom(final Random r, final boolean delete) {
    int i, j, k;

    i = this.count;
    if (i <= 0) {
      return null;
    }

    j = r.nextInt(i);
    k = this.free[j];
    if (delete) {
      this.free[j] = this.free[--i];
      this.count = i;
    }

    return this.list.get(k);
  }

  /**
   * Get an element close to another one
   *
   * @param r
   *          the randomizer
   * @param l
   *          the other location
   * @param rtp
   *          the retry probability
   * @param del
   *          should we delete the object?
   * @return the object
   */
  public final T getCloseTo(final Location l, final double rtp,
      final Random r, final boolean del) {
    int i, j, jbest, k, d, dbest;
    T best, cur;
    InputObjectList<T> lst;

    i = this.count;
    if (i <= 0) {
      return null;
    }

    lst = this.list;
    jbest = -1;
    dbest = Integer.MAX_VALUE;
    best = null;
    outer: do {
      j = r.nextInt(i);
      k = this.free[j];
      cur = lst.get(k);
      d = DistanceMatrix.MATRIX.getDistance(l, this.getLocation(cur));
      if (d < dbest) {
        dbest = d;
        jbest = j;
        best = cur;
        if (d <= 0) {
          break outer;
        }
      }
    } while (r.nextDouble() < rtp);

    if (del) {
      this.free[jbest] = this.free[--i];
      this.count = i;
    }

    return best;
  }

  /**
   * Get an element close to another one
   *
   * @param r
   *          the randomizer
   * @param l
   *          the other location
   * @param c
   *          a computer
   * @param rtp
   *          the retry probability
   * @param del
   *          should we delete the object?
   * @return the object
   */
  public final T getCloseTo(final Location l, final Compute c,
      final double rtp, final Random r, final boolean del) {
    int i, j, jbest, k, d, dbest;
    T best, cur;
    InputObjectList<T> lst;

    i = this.count;
    if (i <= 0) {
      return null;
    }

    lst = this.list;
    jbest = -1;
    dbest = Integer.MAX_VALUE;
    best = null;
    do {
      j = r.nextInt(i);
      k = this.free[j];
      cur = lst.get(k);
      d = DistanceMatrix.MATRIX.getDistance(l, this.getLocation(cur, c));
      outer: if (d < dbest) {
        dbest = d;
        jbest = j;
        best = cur;
        if (d <= 0) {
          break outer;
        }
      }
    } while (r.nextDouble() < rtp);

    if (del) {
      this.free[jbest] = this.free[--i];
      this.count = i;
    }

    return best;
  }

  /**
   * Get another object at the given location
   *
   * @param l
   *          the location
   * @param r
   *          the randomizer
   * @param del
   *          should the object be deleted
   * @return the object, or null if none could be found
   */
  public T getAtLocation(final Location l, final Random r,
      final boolean del) {
    int i, j, k;
    InputObjectList<T> lst;
    T c;

    i = this.count;
    if (i <= 0) {
      return null;
    }

    lst = this.list;

    j = r.nextInt(i);
    for (k = i; (--k) >= 0;) {
      c = lst.get(this.free[j]);
      if (this.getLocation(c) == l) {
        if (del) {
          this.free[j] = this.free[--i];
          this.count = i;
        }
        return c;
      }

      j = ((j + 1) % i);
    }

    return null;
  }

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @return the location
   */
  abstract Location getLocation(final T t);

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @param c
   *          the computer
   * @return the location
   */
  Location getLocation(final T t, final Compute c) {
    return this.getLocation(t);
  }
}