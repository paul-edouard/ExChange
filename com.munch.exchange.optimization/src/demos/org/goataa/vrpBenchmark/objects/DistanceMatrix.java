/*
 * Copyright (c) 2010 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.vrpBenchmark.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import org.goataa.impl.utils.TextUtils;

/**
 * A distance matrix calculates the distance between two locations
 *
 * @author Thomas Weise
 */
public class DistanceMatrix extends TextSerializable {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the globally shared distance matrix instance */
  public static final DistanceMatrix MATRIX = new DistanceMatrix();

  /** the matrix */
  private int[][] matrix;

  /**
   * Create a new distance matrix
   */
  public DistanceMatrix() {
    super();
  }

  /**
   * Initialize to a given matrix
   *
   * @param m
   *          the matrix
   */
  public void init(final int[][] m) {
    int[] drow;
    int i, j;

    this.matrix = m;

    if (m != null) {
      i = m.length;

      for (; (--i) >= 0;) {
        drow = m[i];

        for (j = drow.length; (--j) >= 0;) {
          if (i != j) {
            drow[j] = Math.max(50, (50 * ((drow[j] + 49) / 50)));
          } else {
            drow[j] = 0;
          }
        }
      }
    }
  }

  /**
   * Obtain the distance between two locations
   *
   * @param l1
   *          the first location
   * @param l2
   *          the second location
   * @return the distance in meters
   */
  public final int getDistance(final Location l1, final Location l2) {
    if (l1 == l2) {
      return 0;
    }
    return this.matrix[l1.id][l2.id];
  }

  /**
   * Get the time needed by a given truck for getting from l1 to l2. We
   * assume that a truck will drive in average with 66% of its top speed.
   *
   * @param l1
   *          the first location
   * @param l2
   *          the second location
   * @param t
   *          the truck
   * @return the time needed, in milliseconds
   */
  public final int getTimeNeeded(final Location l1, final Location l2,
      final Truck t) {

    if (l1 == l2) {
      return 0;
    }

    return (int) (0.5d + (3600 * (this.matrix[l1.id][l2.id] / t.avgSpeedKmH)));
  }

  /**
   * Serialize to a writer
   *
   * @param w
   *          the writer
   * @throws IOException
   *           if anything goes wrong
   */
  @Override
  public void serialize(final Writer w) throws IOException {
    int[][] ma;
    int[] l;
    int i, j;

    ma = this.matrix;
    for (i = 0; i < ma.length; i++) {
      if (i > 0) {
        w.write(TextUtils.NEWLINE);
      }
      l = ma[i];
      for (j = 0; j < l.length; j++) {
        if (j > 0) {
          w.write('\t');
        }
        w.write(String.valueOf(l[j]));
      }
    }
  }

  /**
   * Deserialize from a buffered reader
   *
   * @param r
   *          reader
   * @throws IOException
   *           if something goes wrong
   */
  @Override
  public void deserialize(final BufferedReader r) throws IOException {
    String[] s;
    String l;
    int[][] ma;
    int[] q;
    int v, i, j;

    l = r.readLine();
    s = TransportationObjectList.SPLIT.split(l);

    v = s.length;
    this.matrix = ma = new int[v][v];
    for (i = 0; i < v; i++) {
      q = ma[i];
      for (j = 0; j < v; j++) {
        q[j] = Integer.parseInt(s[j]);
      }
      if (i < (v - 1)) {
        s = TransportationObjectList.SPLIT.split(r.readLine());
      }
    }

  }
}