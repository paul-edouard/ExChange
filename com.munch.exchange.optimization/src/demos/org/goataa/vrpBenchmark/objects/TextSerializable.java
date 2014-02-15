/*
 * Copyright (c) 2010 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.vrpBenchmark.objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.goataa.impl.OptimizationModule;

/**
 * The textual serializable object
 *
 * @author Thomas Weise
 */
public class TextSerializable extends OptimizationModule {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Serialize to a writer
   *
   * @param w
   *          the writer
   * @throws IOException
   *           if anything goes wrong
   */
  public void serialize(final Writer w) throws IOException {
    //
  }

  /**
   * Serialize to a file
   *
   * @param f
   *          the file
   */
  public final void serialize(final File f) {
    Writer w;
    BufferedWriter bw;

    try {
      w = new FileWriter(f);
      try {

        bw = new BufferedWriter(w);
        try {

          this.serialize(bw);

        } finally {
          bw.close();
          w = null;
          bw = null;
        }

      } finally {
        if (w != null) {
          w.close();
          w = null;
        }
      }

    } catch (Throwable t) {
      t.printStackTrace();
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
  public void deserialize(final BufferedReader r) throws IOException {
    //
  }

  /**
   * Deserialize from a file
   *
   * @param f
   *          the file
   */
  public final void deserialize(final File f) {
    Reader r;
    BufferedReader br;

    try {
      r = new FileReader(f);
      try {

        br = new BufferedReader(r);
        try {
          this.deserialize(br);
        } finally {
          br.close();
          r = null;
          br = null;
        }

      } finally {
        if (r != null) {
          r.close();
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Serialize all things
   *
   * @param directory
   *          the directory to store in
   */
  public static final void serializeAll(final File directory) {
    File f;
    try {
      f = directory.getCanonicalFile();
      f.mkdirs();

      Location.LOCATIONS.serialize(new File(f, "locations.txt"));//$NON-NLS-1$
      Container.CONTAINERS.serialize(new File(f, "containers.txt"));//$NON-NLS-1$
      Driver.DRIVERS.serialize(new File(f, "drivers.txt"));//$NON-NLS-1$
      Truck.TRUCKS.serialize(new File(f, "trucks.txt"));//$NON-NLS-1$
      Order.ORDERS.serialize(new File(f, "orders.txt"));//$NON-NLS-1$
      DistanceMatrix.MATRIX.serialize(new File(f, "distances.txt"));//$NON-NLS-1$

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * De-Serialize all things
   *
   * @param directory
   *          the directory to store in
   */
  public static final void deserializeAll(final File directory) {
    File f;
    try {
      f = directory.getCanonicalFile();
      f.mkdirs();

      Location.LOCATIONS.deserialize(new File(f, "locations.txt"));//$NON-NLS-1$
      Container.CONTAINERS.deserialize(new File(f, "containers.txt"));//$NON-NLS-1$
      Driver.DRIVERS.deserialize(new File(f, "drivers.txt"));//$NON-NLS-1$
      Truck.TRUCKS.deserialize(new File(f, "trucks.txt"));//$NON-NLS-1$
      Order.ORDERS.deserialize(new File(f, "orders.txt"));//$NON-NLS-1$
      DistanceMatrix.MATRIX.deserialize(new File(f, "distances.txt"));//$NON-NLS-1$

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Clear all lists and objects
   */
  public static final void clearAll() {
    Location.LOCATIONS.clear();
    Container.CONTAINERS.clear();
    Driver.DRIVERS.clear();
    Order.ORDERS.clear();
    Truck.TRUCKS.clear();
    DistanceMatrix.MATRIX.init(null);
  }
}