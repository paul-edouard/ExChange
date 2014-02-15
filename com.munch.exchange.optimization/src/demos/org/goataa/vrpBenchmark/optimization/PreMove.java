// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.optimization;

import java.util.ArrayList;

import demos.org.goataa.vrpBenchmark.objects.Container;
import demos.org.goataa.vrpBenchmark.objects.DistanceMatrix;
import demos.org.goataa.vrpBenchmark.objects.Driver;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.objects.TransportationObject;
import demos.org.goataa.vrpBenchmark.objects.Truck;

/**
 * A move
 *
 * @author Thomas Weise
 */
public class PreMove extends TransportationObject {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the truck */
  public Truck truck;

  /** the involved orders */
  public final ArrayList<Order> orders;

  /** the involved drivers */
  public final ArrayList<Driver> drivers;

  /** the involved containers */
  public final ArrayList<Container> containers;

  /** the starting location */
  public Location from;

  /** the start time */
  public long startTime;

  /** the destination location */
  public Location to;

  /** Create a new freight object */
  public PreMove() {
    super();

    this.containers = new ArrayList<Container>();
    this.drivers = new ArrayList<Driver>();
    this.orders = new ArrayList<Order>();
  }

  /**
   * Compile this move
   *
   * @return the move object described by this pre move
   */
  public final Move compile() {

    return new Move(this.truck,

    this.orders.toArray(new Order[this.orders.size()]), this.drivers
        .toArray(new Driver[this.drivers.size()]), this.containers
        .toArray(new Container[this.containers.size()]), this.from,
        this.startTime, this.to);

  }

  /**
   * Clear this pre move
   */
  public final void clear() {
    this.from = null;
    this.to = null;
    this.containers.clear();
    this.drivers.clear();
    this.orders.clear();
  }

  /**
   * Get the earliest leave time
   *
   * @return the leave time
   */
  public final long getEarliestLeaveTime() {
    long arr;
    int i;
    Order o;

    arr = this.startTime
        + DistanceMatrix.MATRIX.getTimeNeeded(this.from, this.to,
            this.truck);

    for (i = this.orders.size(); (--i) >= 0;) {
      o = this.orders.get(i);
      if (o.deliveryLocation == this.to) {
        arr = Math.max(arr, o.deliveryWindowStart);
      }
    }

    return arr;
  }

  /**
   * As next move from
   *
   * @param m
   *          the move
   */
  public final void asNext(final PreMove m) {
    int i;
    Order o;

    this.from = m.to;
    this.truck = m.truck;
    this.to = null;

    this.containers.clear();
    this.containers.addAll(m.containers);

    this.drivers.clear();
    this.drivers.addAll(m.drivers);

    this.orders.clear();
    for (i = m.orders.size(); (--i) >= 0;) {
      o = m.orders.get(i);
      if (o.deliveryLocation != m.to) {
        this.orders.add(o);
      }
    }

    this.startTime = m.getEarliestLeaveTime();
  }

}