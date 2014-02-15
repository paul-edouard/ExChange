// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.utils;

import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.optimization.Compute;

/**
 * The order manager
 *
 * @author Thomas Weise
 */
public class OrderManager extends ObjectManager<Order> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Create a new order manager */
  public OrderManager() {
    super(Order.ORDERS);
  }

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @return the location
   */
  @Override
  final Location getLocation(final Order t) {
    return t.pickupLocation;
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
  final Location getLocation(final Order t, final Compute c) {
    Location l;
    l = c.getLocation(t);
    if (l != null) {
      return l;
    }
    return this.getLocation(t);
  }
}