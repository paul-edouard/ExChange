// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.utils;

import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Truck;
import demos.org.goataa.vrpBenchmark.optimization.Compute;

/**
 * The truck manager
 *
 * @author Thomas Weise
 */
public class TruckManager extends ObjectManager<Truck> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Create a new truck manager */
  public TruckManager() {
    super(Truck.TRUCKS);
  }

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @return the location
   */
  @Override
  final Location getLocation(final Truck t) {
    return t.start;
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
  final Location getLocation(final Truck t, final Compute c) {
    Location l;
    l = c.getLocation(t);
    if (l != null) {
      return l;
    }
    return this.getLocation(t);
  }
}