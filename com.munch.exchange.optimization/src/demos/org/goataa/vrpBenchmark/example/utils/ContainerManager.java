// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.utils;

import demos.org.goataa.vrpBenchmark.objects.Container;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.optimization.Compute;

/**
 * The container manager
 *
 * @author Thomas Weise
 */
public class ContainerManager extends ObjectManager<Container> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** Create a new container manager */
  public ContainerManager() {
    super(Container.CONTAINERS);
  }

  /**
   * Get the location
   *
   * @param t
   *          the object
   * @return the location
   */
  @Override
  final Location getLocation(final Container t) {
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
  final Location getLocation(final Container t, final Compute c) {
    Location l;
    l = c.getLocation(t);
    if (l != null) {
      return l;
    }
    return this.getLocation(t);
  }
}