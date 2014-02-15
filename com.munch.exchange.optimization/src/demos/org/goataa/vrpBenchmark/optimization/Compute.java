// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.optimization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import demos.org.goataa.vrpBenchmark.objects.Container;
import demos.org.goataa.vrpBenchmark.objects.DistanceMatrix;
import demos.org.goataa.vrpBenchmark.objects.Driver;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.objects.TransportationObjectList;
import demos.org.goataa.vrpBenchmark.objects.Truck;

/**
 * This class allows to compute the state of the world and, hence, the
 * objective values. The initial state of the world is t=0 and all objects
 * are at their starting locations. Then, we can make moves. Every move
 * consists of a truck driving from one location to another. By doing so,
 * it may carry objects (containers, orders) and is driven by at least one
 * driver (but may carry more). After the move, the involved objects are at
 * their new locations.
 *
 * @author Thomas Weise
 */
public class Compute extends PerformanceValues {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the internal comparator */
  private static final Comparator<Move> MOVE_COMPARATOR = new CMP();

  /** the drivers */
  private final DriverTrack[] drivers;

  /** the trucks */
  private final TruckTrack[] trucks;

  /** the container */
  private final Track[] containers;

  /** the order */
  private final Track[] orders;

  /** the temporary move list */
  private transient Move[] temp;

  /** Create a new compute instance */
  public Compute() {
    super();

    Track[] t;
    DriverTrack[] x;
    TruckTrack[] y;
    int i;

    i = Truck.TRUCKS.size();
    this.trucks = y = new TruckTrack[i];
    for (; (--i) >= 0;) {
      y[i] = new TruckTrack();
    }

    i = Driver.DRIVERS.size();
    this.drivers = x = new DriverTrack[i];
    for (; (--i) >= 0;) {
      x[i] = new DriverTrack();
    }

    i = Container.CONTAINERS.size();
    this.containers = t = new Track[i];
    for (; (--i) >= 0;) {
      t[i] = new Track();
    }

    i = Order.ORDERS.size();
    this.orders = t = new Track[i];
    for (; (--i) >= 0;) {
      t[i] = new Track();
    }

    this.temp = null;
  }

  /**
   * Initialize the computer: reset all variables and move all objects to
   * their default locations
   */
  @Override
  public final void init() {
    Track[] ts;
    DriverTrack[] dts;
    TruckTrack[] tts;
    TruckTrack q;
    Track t;
    DriverTrack dt;
    Truck a;
    Driver b;
    Container c;
    Order o;
    int i;

    super.init();

    tts = this.trucks;
    for (i = tts.length; (--i) >= 0;) {
      q = tts[i];
      a = Truck.TRUCKS.get(i);
      q.location = a.start;
      q.time = 0l;
      q.orders = null;
      q.lastTime = -1l;
    }

    dts = this.drivers;
    for (i = dts.length; (--i) >= 0;) {
      dt = dts[i];
      b = Driver.DRIVERS.get(i);
      dt.location = b.home;
      dt.time = 0l;
      dt.lastDay = -1;
    }

    ts = this.containers;
    for (i = ts.length; (--i) >= 0;) {
      t = ts[i];
      c = Container.CONTAINERS.get(i);
      t.location = c.start;
      t.time = 0l;
    }

    ts = this.orders;
    for (i = ts.length; (--i) >= 0;) {
      t = ts[i];
      o = Order.ORDERS.get(i);
      t.location = o.pickupLocation;
      t.time = o.pickupWindowStart;
    }

  }

  /**
   * Make a move: Truck t drives from the location "from" to the location
   * "to" at the given start time. It carries the drivers "d", the orders
   * "o", and the containers "c".
   *
   * @param t
   *          the truck
   * @param o
   *          the orders
   * @param d
   *          the drivers
   * @param c
   *          the container
   * @param from
   *          the starting location
   * @param startTime
   *          the start time
   * @param to
   *          the destination location
   */
  public final void move(final Truck t, final Order[] o, final Driver[] d,
      final Container[] c, final Location from, long startTime,
      final Location to) {
    final Track[] lcontainers, lorders;
    final DriverTrack[] ldrivers;
    Track track;
    final TruckTrack truck;
    Container cc;
    Location startLoc;
    long start, end, m, timeNeeded, lastTime;
    int totalContainers, totalOrders, totalDrivers;
    int i, ed, sd, td;
    Order oo;
    Driver dd;
    DriverTrack dt;
    Order[] o2;

    this.clearNew();

    ldrivers = this.drivers;
    lcontainers = this.containers;
    lorders = this.orders;

    // move the truck
    startLoc = from;
    start = startTime;

    if (t != null) {
      truck = this.trucks[t.id];
      lastTime = truck.lastTime;

      // is the truck at the right location?
      startLoc = truck.location;
      if (startLoc != from) {
        this.addTruckError(1);
      }

      // is it there at the right time
      if (truck.time > start) {
        this.addTruckError(1);
        start = truck.time;
      }

      // move the truck, if the start location is different from the end
      if (startLoc != to) {
        // compute the time to travel
        m = DistanceMatrix.MATRIX.getDistance(startLoc, to);
        timeNeeded = DistanceMatrix.MATRIX.getTimeNeeded(startLoc, to, t);

        this
            .addTruckCosts(((long) (0.5d + ((t.costsPerHKm * timeNeeded * m) / 3600000000.0d))));

        end = (start + timeNeeded);
        truck.location = to;
      } else {
        end = (start + 1l);
        this.addTruckCosts(1000l);
      }

      truck.time = end;
    } else {
      // no truck? -> error
      this.addTruckError(1);
      end = start + 10000000l;
      this.addTruckCosts(1000l);
      truck = null;
      lastTime = -1l;
    }

    // move all containers
    totalContainers = 0;
    if (c != null) {
      for (i = c.length; (--i) >= 0;) {
        cc = c[i];

        // aha, a container
        if (cc != null) {
          totalContainers++;
          track = lcontainers[cc.id];

          // check the current location of the container
          if (track.location != startLoc) {
            this.addContainerError(1);
          }

          // check the time at which the container is there
          if (track.time > start) {
            this.addContainerError(1);
          }

          // move the container
          track.location = to;
          track.time = end;
        }
      }

      // are there too many containers?
      i = (totalContainers - ((t == null) ? 0 : t.maxContainers));
      if (i > 0) {
        this.addContainerError(i);
      }
    }

    // move all orders
    totalOrders = 0;
    if (o != null) {
      for (i = o.length; (--i) >= 0;) {
        oo = o[i];

        // aha, an order
        if (oo != null) {
          totalOrders++;
          track = lorders[oo.id];

          // check the current location of the order
          if (track.location != startLoc) {
            this.addOrderError(1);
          }

          // is the order at the starting location?
          if ((track.location == oo.pickupLocation)
              && (track.time <= oo.pickupWindowStart)) {

            if (lastTime >= 0l) {
              m = windowIntersection(oo.pickupWindowStart,
                  oo.pickupWindowEnd, lastTime, start);
            } else {
              m = start;
            }

            m = Compute.windowViolation(oo.pickupWindowStart,
                oo.pickupWindowEnd, m);

            if (m > 0l) {
              this.addPickupErrorTime(m);
              this.addOrderError(1);
            }
          } else {
            // check the time at which the order is there
            if (track.time > start) {
              this.addOrderError(1);
            }
          }

          // move the order
          track.location = to;
          track.time = end;
        }
      }
    }

    // now let us check if delivery took place in the time windows by
    // possibly correcting drop-off times. It could be that a truck arrives
    // before the delivery time window but leaves after it
    if (truck != null) {

      if (lastTime >= 0l) {
        o2 = truck.orders;
        if (o2 != null) {
          // for each order
          for (i = o2.length; (--i) >= 0;) {
            oo = o2[i];
            if (oo != null) {
              // get the tracking object
              track = lorders[oo.id];
              // is the order still at the place where this truck dropped
              // it
              // off and is the order at the delivery location?
              if ((track.time == lastTime) && (track.location == startLoc)
                  && (track.location == oo.deliveryLocation)) {
                track.time = windowIntersection(oo.deliveryWindowStart,
                    oo.deliveryWindowEnd, lastTime, start);
              }
            }
          }
        }
      }

      truck.orders = o;
      truck.lastTime = end;
    }

    // are there more orders than containers?
    if (totalOrders > totalContainers) {
      this.addContainerError((totalOrders - totalContainers));
    }

    // move all drivers
    totalDrivers = 0;
    if (d != null) {
      sd = ((int) ((start / 86400000)));
      ed = ((int) ((end / 86400000)));
      td = (ed - sd + 1);

      for (i = d.length; (--i) >= 0;) {
        dd = d[i];

        // aha, a driver
        if (dd != null) {
          totalDrivers++;
          dt = ldrivers[dd.id];

          // check the current location of the driver
          if (dt.location != startLoc) {
            this.addDriverError(1);
          }

          // check the time at which the driver is there
          if (dt.time > start) {
            this.addDriverError(1);
          }

          // compute costs of that driver: each day, he receives a salary
          if (ed > dt.lastDay) {
            dt.lastDay = ed;
            this.addDriverCosts((td * dd.costsPerDay));
          }

          // move the driver
          dt.location = to;
          dt.time = end;
        }
      }
    }

    // no driver? that's an error
    if (totalDrivers <= 0) {
      this.addDriverError(1);
    }

    this.commitNew();
  }

  /**
   * Compute the time window violation.
   *
   * @param ws
   *          the window start
   * @param we
   *          the window end
   * @param t
   *          the time
   * @return the window violation
   */
  private static final long windowViolation(final long ws, final long we,
      final long t) {
    if (t < ws) {
      return (ws - t);
    }
    if (t > we) {
      return (t - we);
    }
    return 0l;
  }

  /**
   * Get the best window intersection time
   *
   * @param w1s
   *          the start of the first window
   * @param w1e
   *          the end of the first window
   * @param w2s
   *          the start of the second window
   * @param w2e
   *          the end of the second window
   * @return the earliest/best time within the second window which creates
   *         the smallest violation of the first window
   */
  private static final long windowIntersection(final long w1s,
      final long w1e, final long w2s, final long w2e) {

    if ((w2e >= w1s) && (w2s <= w1e)) {
      return Math.max(w1s, w2s);
    }

    if (w2e < w1s) {
      return w2e;
    }

    if (w2s > w1e) {
      return w2s;
    }

    throw new RuntimeException();
  }

  /**
   * Finalize the computation, i.e., check if everything is placed at
   * positions to which it belongs.
   */
  public final void finish() {
    final Track[] ltrucks, lcontainers, lorders;
    final DriverTrack[] ldrivers;
    Track track;
    int i;
    Order oo;
    long y;

    this.clearNew();

    // check the trucks
    ltrucks = this.trucks;
    for (i = ltrucks.length; (--i) >= 0;) {
      track = ltrucks[i];
      // all trucks need to be at a depot
      if (!(track.location.isDepot)) {
        this.addTruckError(1);
      }
    }

    // check the drivers
    ldrivers = this.drivers;
    for (i = ldrivers.length; (--i) >= 0;) {
      track = ldrivers[i];
      // all drivers need to be home at the end of the jobs
      if (track.location != Driver.DRIVERS.get(i).home) {
        this.addDriverError(1);
      }
    }

    // check the containers
    lcontainers = this.containers;
    for (i = lcontainers.length; (--i) >= 0;) {
      track = lcontainers[i];
      // all containers need to be at a depot
      if (!(track.location.isDepot)) {
        this.addContainerError(1);
      }
    }

    // check the orders
    lorders = this.orders;
    for (i = lorders.length; (--i) >= 0;) {
      track = lorders[i];
      oo = Order.ORDERS.get(i);
      // all orders need to be at their destination
      if (track.location != oo.deliveryLocation) {
        this.addOrderError(1);
      }
      y = windowViolation(oo.deliveryWindowStart, oo.deliveryWindowEnd,
          track.time);
      if (y > 0) {
        this.addOrderError(1);
        this.addDeliveryErrorTime(y);
      }
    }

    this.commitNew();
  }

  /**
   * Make a move
   *
   * @param m
   *          the move
   */
  public final void move(final Move m) {
    if (m != null) {
      this.move(m.truck, m.orders, m.drivers, m.containers, m.from,
          m.startTime, m.to);
    }
  }

  /**
   * Do some moves internally
   *
   * @param moves
   *          the moves
   * @param len
   *          the length
   */
  private final void move(final Move[] moves, final int len) {
    int i;

    Arrays.sort(moves, 0, len, MOVE_COMPARATOR);
    for (i = len; (--i) >= 0;) {
      this.move(moves[i]);
    }
  }

  /**
   * Get the internal move array
   *
   * @param len
   *          the length
   * @return the move array
   */
  private final Move[] getMoves(final int len) {
    Move[] m;

    m = this.temp;
    if ((m == null) || (m.length < len)) {
      return (this.temp = new Move[len]);
    }
    return m;
  }

  /**
   * Make the given moves
   *
   * @param ms
   *          the move list
   */
  public final void move(final TransportationObjectList<Move> ms) {
    int i, s;
    Move[] m;

    if (ms != null) {
      s = ms.size();
      if (s > 0) {
        m = this.getMoves(s);
        for (i = s; (--i) >= 0;) {
          m[i] = ms.get(i);
        }
        this.move(m, s);
      }
    }
  }

  /**
   * Make the given moves
   *
   * @param ms
   *          the moves
   */
  public final void move(final Move[] ms) {
    Move[] m;
    int i;

    if (ms != null) {
      i = ms.length;
      if (i > 0) {
        m = this.getMoves(i);
        System.arraycopy(ms, 0, m, 0, i);
        this.move(m, i);
      }
    }
  }

  /**
   * Get the location where the given container was placed last
   *
   * @param c
   *          the container
   * @return the location where the given container was placed last
   */
  public final Location getLocation(final Container c) {
    return this.containers[c.id].location;
  }

  /**
   * Get the location where the given truck was placed last
   *
   * @param c
   *          the truck
   * @return the location where the given truck was placed last
   */
  public final Location getLocation(final Truck c) {
    return this.trucks[c.id].location;
  }

  /**
   * Get the location where the given driver was placed last
   *
   * @param c
   *          the driver
   * @return the location where the given driver was placed last
   */
  public final Location getLocation(final Driver c) {
    return this.drivers[c.id].location;
  }

  /**
   * Get the location where the given order was placed last
   *
   * @param c
   *          the order
   * @return the location where the given order was placed last
   */
  public final Location getLocation(final Order c) {
    return this.orders[c.id].location;
  }

  /**
   * A class used for tracking
   *
   * @author Thomas Weise
   */
  private static class Track implements Serializable {
    /** a constant required by Java serialization */
    private static final long serialVersionUID = 1;

    /** the location */
    Location location;

    /** the time */
    long time;

    /** the track */
    Track() {
      super();
    }

  }

  /**
   * A class used for driver tracking
   *
   * @author Thomas Weise
   */
  private static class DriverTrack extends Track {
    /** a constant required by Java serialization */
    private static final long serialVersionUID = 1;

    /** the days during which a driver was driving */
    int lastDay;

    /** the track */
    DriverTrack() {
      super();
    }

  }

  /**
   * A class used for truck tracking
   *
   * @author Thomas Weise
   */
  private static class TruckTrack extends Track {
    /** a constant required by Java serialization */
    private static final long serialVersionUID = 1;

    /** the last time the truck arrived somewhere */
    long lastTime;

    /** orders which were dropped off */
    Order[] orders;

    /** the track */
    TruckTrack() {
      super();
    }

  }

  /**
   * The move comparator for sorting
   *
   * @author Thomas Weise
   */
  private static final class CMP implements Comparator<Move> {

    /** instantiate the comparator */
    CMP() {
      super();
    }

    /**
     * Compare two moves according to their start time
     *
     * @param a
     *          the first move
     * @param b
     *          the second move
     * @return the comparison result
     */
    public final int compare(final Move a, final Move b) {
      if (a.startTime < b.startTime) {
        return 1;
      }
      if (b.startTime < a.startTime) {
        return -1;
      }
      return 0;
    }
  }
}