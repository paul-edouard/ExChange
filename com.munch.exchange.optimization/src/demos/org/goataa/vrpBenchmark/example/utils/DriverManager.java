// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.utils;

import demos.org.goataa.vrpBenchmark.objects.Driver;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.optimization.Compute;

/**
 * The driver manager
 *
 * @author Thomas Weise
 */
public class DriverManager extends ObjectManager<Driver> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Create a new driver manager */
  public DriverManager() {
    super(Driver.DRIVERS);
  }

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @return the location
   */
  @Override
  final Location getLocation(final Driver t) {
    return t.home;
  }

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @param c
   *          the computer
   */
  @Override
  final Location getLocation(final Driver t, final Compute c) {
    Location l;
    l = c.getLocation(t);
    if (l != null) {
      return l;
    }
    return this.getLocation(t);
  }
}