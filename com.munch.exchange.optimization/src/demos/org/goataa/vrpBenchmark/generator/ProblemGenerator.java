/*
 * Copyright (c) 2010 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.vrpBenchmark.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import demos.org.goataa.vrpBenchmark.objects.Container;
import demos.org.goataa.vrpBenchmark.objects.DistanceMatrix;
import demos.org.goataa.vrpBenchmark.objects.Driver;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.objects.TransportationObjectList;
import demos.org.goataa.vrpBenchmark.objects.Truck;
import demos.org.goataa.vrpBenchmark.optimization.Move;

/**
 * Create new problems
 *
 * @author Thomas Weise
 */
public class ProblemGenerator {

  /** the randomizer */
  private final Random rand;

  /** the locations */
  private final List<LocationHolder> locations;

  /** the depots */
  private final List<Location> depots;

  /** the maximum orders */
  int maxOrders;

  /** the list with the moves */
  private final TransportationObjectList<Move> moves;

  /**
   * Create a new problem
   *
   * @param seed
   *          the randomizer seed
   */
  public ProblemGenerator(final long seed) {
    super();
    this.locations = new ArrayList<LocationHolder>();
    this.rand = new Random(seed);
    this.depots = new ArrayList<Location>();
    this.moves = new TransportationObjectList<Move>(Move.class);
  }

  /**
   * Create the distances
   */
  public final void createDistances() {
    Random r;
    int i, j, k, d1, d2, d3, ibest, jbest, dbest, cnt, v;
    int[][] mat;
    int[] q;
    List<LocationHolder> h;
    boolean first, b, vvv;

    r = this.rand;
    cnt = (h = this.locations).size();
    mat = new int[cnt][cnt];

    do {
      this.createPath(mat);
    } while (r.nextInt(cnt >>> 2) > 0);

    first = true;

    do {
      vvv = false;

      // for the first time, we just use the paths
      if (first) {
        first = false;
        vvv = true;
      } else {
        System.out.println("adding direct connection");//$NON-NLS-1$
        outer: for (i = cnt; (--i) >= 0;) {
          q = mat[i];
          for (j = cnt; (--j) >= 0;) {
            if ((i != j) && (q[j] == 0)) {
              v = dist(h.get(i).x, h.get(i).y, h.get(j).x, h.get(j).y);
              mat[i][j] = distify(r, v);
              mat[j][i] = distify(r, v);
              vvv = true;
              break outer;
            }
          }
        }
      }

      System.out.println("utilizing connections");//$NON-NLS-1$
      do {
        b = false;
        ibest = jbest = dbest = -1;

        for (i = cnt; (--i) >= 0;) {
          for (j = cnt; (--j) >= 0;) {
            if ((i != j) && (mat[i][j] <= 0)) {
              for (k = cnt; (--k) >= 0;) {
                if ((i != k) && (j != k)) {
                  d1 = mat[i][k];
                  if (d1 != 0) {
                    d2 = mat[k][j];
                    if (d2 != 0) {
                      d3 = (r.nextInt(100) + d1 + d2);
                      if ((d3 > 0) && ((dbest < 0) || (dbest > d3))) {
                        dbest = d3;
                        ibest = i;
                        jbest = j;
                      }
                    }
                  }
                }
              }
            }
          }
        }

        if (dbest > 0) {
          mat[ibest][jbest] = dbest;
          b = true;
        }

      } while (b);

    } while (vvv);
    DistanceMatrix.MATRIX.init(mat);
  }

  /**
   * distify
   *
   * @param r
   *          the randomizer
   * @param dist
   *          the distance
   * @return the distance
   */
  private static final int distify(final Random r, final int dist) {
    int edm;
    edm = dist;
    do {
      edm = Math.min(edm + 1000, ((int) (Math.ceil(edm
          * (1d + (r.nextDouble() * 0.01d))))));
    } while (r.nextBoolean());
    return edm;
  }

  /**
   * Distify
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return the distance
   */
  private static final int dist(final int x1, final int y1, final int x2,
      final int y2) {
    double w, h;
    w = (x1 - x2);
    h = (y1 - y2);
    return (int) (Math.ceil(Math.sqrt((w * w) + (h * h))));
  }

  /**
   * Create a direct road
   *
   * @param di
   *          the distances
   */
  private final void createPath(final int[][] di) {
    int ed, edm, edm2, cnt;
    List<LocationHolder> hl;
    LocationHolder a, b, d;
    Random r;
    int len, maxt;

    hl = this.locations;
    cnt = hl.size();

    r = this.rand;

    b = hl.get(r.nextInt(cnt));
    len = 1;
    maxt = 100000;
    outer: {
      do {
        a = b;
        edm = Integer.MAX_VALUE;

        b = null;
        while ((b == null) || (r.nextInt(10) > 0)) {
          if ((--maxt) <= 0) {
            break outer;
          }
          d = hl.get(r.nextInt(cnt));
          if (d != a) {
            if ((b == null) || (di[a.location.id][d.location.id] <= 0)) {
              ed = dist(a.x, a.y, d.x, d.y);
              if (ed < edm) {
                b = d;
                edm = ed;
              }
            }
          }
        }
        edm2 = distify(r, edm);
        edm = distify(r, edm);
        ed = (int) (0.45 * edm + 0.55 * edm2);
        edm = (int) (0.55 * edm + 0.45 * edm2);
        di[a.location.id][b.location.id] = ed;
        di[b.location.id][a.location.id] = edm;
        len++;
      } while (r.nextInt(10) > 0);

    }

    System.out.println("New path of length: " + len);//$NON-NLS-1$
  }

  /**
   * Create locations
   *
   * @param width
   *          the area width
   * @param height
   *          the area height
   * @param count
   *          the number of locations
   */
  public final void createLocations(final int width, final int height,
      final int count) {
    Random r;
    int z, v, sx, sy, sw, sh;
    List<LocationHolder> l;
    Location d;

    r = this.rand;
    l = this.locations;
    z = 2000;
    v = Math.max(4, (int) (0.01d * l.size()));
    while ((l.size() < v) && (l.size() < count) && ((--z) >= 0)) {
      sx = r.nextInt(width);
      sy = r.nextInt(height);
      if (checkLoc(sx, sy, l)) {
        d = new Location(l.size(), true);
        this.depots.add(d);
        l.add(new LocationHolder(d, sx, sy));
        System.out.println(" new depot at (" + sx + //$NON-NLS-1$
            ", " + sy + //$NON-NLS-1$
            ")");//$NON-NLS-1$
      }
    }

    while (l.size() < count) {

      switch (r.nextInt(2)) {
      case 0: {
        z = (count >>> 1);
        break;
      }
      case 1: {
        z = (int) (Math.ceil(Math.sqrt(count)));
        break;
      }
      default: {
        z = 20;
        break;
      }
      }

      z = Math.min(count - l.size(), (1 + r.nextInt(z + 1)));

      inner: for (;;) {
        sx = r.nextInt(width);
        sy = r.nextInt(height);
        do {
          sw = 100 + r.nextInt(width - sx);
        } while (sw > 20000);
        do {
          sh = 100 + r.nextInt(height - sy);
        } while (sh > 20000);

        if ((sw * sh) < (33 * z)) {
          continue inner;
        }
        break inner;
      }

      this.doCreateLocations(sx, sy, sw, sh, z);

    }

  }

  /**
   * check a location
   *
   * @param px
   *          the x-coordinate
   * @param py
   *          the y-coordinate
   * @param l
   *          the location holder
   * @return true if the location if ok
   */
  private static final boolean checkLoc(final int px, final int py,
      final List<LocationHolder> l) {
    int j;
    LocationHolder a;
    for (j = l.size(); (--j) >= 0;) {
      a = l.get(j);
      if ((Math.abs(a.x - px) < 10) && (Math.abs(a.y - py) < 10)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Create locations
   *
   * @param startX
   *          the minimum x
   * @param startY
   *          the minimum y
   * @param width
   *          the area width
   * @param height
   *          the area height
   * @param count
   *          the number of locations
   */
  private final void doCreateLocations(final int startX, final int startY,
      final int width, final int height, final int count) {
    final List<LocationHolder> l;
    final Random r;
    int px, py, i, zz;
    int cities;
    Location loc;

    l = this.locations;
    r = this.rand;
    zz = 10000;
    System.out.println("new area (" + startX + //$NON-NLS-1$
        ", " + startY + //$NON-NLS-1$
        ", " + width + //$NON-NLS-1$
        ", " + height + ")");//$NON-NLS-1$//$NON-NLS-2$

    cities = 0;
    outer: {
      for (i = count; (--i) >= 0;) {

        inner: for (;;) {
          if ((--zz) <= 0) {
            break outer;
          }
          px = startX + r.nextInt(width);
          py = startY + r.nextInt(height);
          if (!(checkLoc(px, py, l))) {
            continue inner;
          }
          // for (j = l.size(); (--j) >= 0;) {
          // a = l.get(j);
          // if ((Math.abs(a.x - px) < 10) && (Math.abs(a.y - py) < 10)) {
          // continue inner;
          // }
          // }
          loc = new Location(l.size(), cities <= 0);
          if (loc.isDepot) {
            this.depots.add(loc);
            System.out.println(" new depot at (" + px + //$NON-NLS-1$
                ", " + py + //$NON-NLS-1$
                ")");//$NON-NLS-1$
          }
          cities++;

          l.add(new LocationHolder(loc, px, py));
          break inner;
        }
      }
    }
    System.out.println(" number of cities in area: " + cities);//$NON-NLS-1$
  }

  /**
   * Create a new set of orders combined by tours
   *
   * @param count
   *          the number of orders connected by tours to create
   * @param days
   *          the number of days over which the tours should be distributed
   */
  public final void createTours(final int count, final long days) {
    boolean b;
    Random r;
    long s, s2, mt, h;

    h = 60l * 60l * 100l;
    mt = 24 * h;
    h >>>= 2l;
    r = this.rand;
    s2 = 0l;
    this.maxOrders = count + Order.ORDERS.size();
    b = true;

    while (Order.ORDERS.size() < this.maxOrders) {

      if (b || (r.nextInt(10) == 0)) {
        s2 = (long) (r.nextDouble() * mt);
      }
      b = false;

      do {
        s = (long) (r.nextDouble() * mt);
        s = (((s + s2 + s2 + s2) / (h << 2)) * h);
      } while ((s <= 0l) || (s >= mt));

      this.createTour(s);
    }
  }

  /**
   * Create a new tour
   *
   * @param startTime
   *          the start time
   */
  private final void createTour(final long startTime) {
    List<LocationHolder> l;
    List<Location> dep;
    Random r;
    final Truck t;
    Location depot, curPos, nextPos;
    int maxtrials, i, drivers, containers;
    long curT;
    boolean b, first;
    int ordersdone;
    final Driver[] dr;
    final Container[] cnt;
    final TransportationObjectList<Move> tl;
    final OrderManager om;

    r = this.rand;
    l = this.locations;
    dep = this.depots;
    tl = this.moves;

    depot = dep.get(r.nextInt(dep.size()));

    t = this.randomTruck(depot);
    drivers = 1 + r.nextInt(t.maxDrivers);
    dr = new Driver[drivers];
    for (i = 0; i < drivers; i++) {
      dr[i] = this.randomDriver(depot);
    }

    containers = 1 + r.nextInt(t.maxContainers);
    cnt = new Container[containers];

    ordersdone = 0;
    curT = startTime;
    curPos = depot;
    om = new OrderManager();

    all: {
      outer: for (;;) {
        maxtrials = 10000;

        inner: for (;;) {
          if ((--maxtrials) <= 0) {
            break outer;
          }

          do {
            nextPos = l.get(r.nextInt(l.size())).location;
          } while (nextPos == curPos);

          if (drivers > 1) {
            break inner;
          }
          if ((DistanceMatrix.MATRIX.getTimeNeeded(curPos, nextPos, t)
              + DistanceMatrix.MATRIX.getTimeNeeded(nextPos, depot, t)
              + curT - startTime) < Driver.MAX_DRIVING_TIME) {
            break inner;
          }
        }

        tl.add(new Move(t, om.currentOrders(), dr, cnt, curPos, curT,
            nextPos));
        curT += DistanceMatrix.MATRIX.getTimeNeeded(curPos, nextPos, t);
        curPos = nextPos;

        b = false;

        // aha, there is an order in the truck, unload!
        if (om.size() > 0) {
          b = true;
          do {
            if (om.random(r, curPos, curT, false)) {
              ordersdone++;
            }
            if (Order.ORDERS.size() >= this.maxOrders) {
              tl.add(new Move(t, Order.NO_ORDERS, dr, cnt, curPos, curT,
                  depot));
              break all;
            }
          } while ((om.size() > 0) && (r.nextBoolean()));
        }

        while (((!b) && (om.size() <= 0))
            || ((om.size() < containers) && (r.nextBoolean()) && ((depot != curPos) || (r
                .nextInt(3) == 0)))) {
          om.push(curPos, curT);
        }

        if ((drivers <= 1)
            && ((curT - startTime + DistanceMatrix.MATRIX.getTimeNeeded(
                curPos, depot, t)) >= (0.9d * Driver.MAX_DRIVING_TIME))) {
          if ((om.size() <= 0) && (ordersdone <= 0)) {
            om.push(curPos, curT);
          }
          break outer;
        }

        if ((ordersdone > 0) && (r.nextInt(4) == 0)) {
          break outer;
        }
      }

      tl.add(new Move(t, om.currentOrders(), dr, cnt, curPos, curT,//
          depot));
      curT += DistanceMatrix.MATRIX.getTimeNeeded(curPos, depot, t);

      first = true;
      // realize a few orders back to the depot
      loop: while ((om.size() > 0) && (first || r.nextBoolean())) {
        first = false;
        if (om.random(r, depot, curT, true)) {
          ordersdone++;
        }
        if (Order.ORDERS.size() >= this.maxOrders) {
          break loop;
        }
      }
    }

    for (i = om.getMaxOrders(); (--i) >= 0;) {
      cnt[i] = this.randomContainer(depot);
    }

  }

  /**
   * Get the simple solution
   *
   * @return the simple (not necessarily optimal) solution
   */
  public final TransportationObjectList<Move> getSolution() {
    return this.moves;
  }

  /**
   * Create a number of random drivers
   *
   * @param count
   *          the number of drivers to create
   */
  public final void createDrivers(final int count) {
    int i;
    Random r;
    List<Location> l;

    l = this.depots;
    r = this.rand;
    for (i = 0; i < count; i++) {
      this.randomDriver(l.get(r.nextInt(l.size())));
    }

  }

  /**
   * Create a number of random containers
   *
   * @param count
   *          the number of containers to create
   */
  public final void createContainers(final int count) {
    int i;
    Random r;
    List<Location> l;

    l = this.depots;
    r = this.rand;
    for (i = 0; i < count; i++) {
      this.randomContainer(l.get(r.nextInt(l.size())));
    }

  }

  /**
   * Create a number of random trucks
   *
   * @param count
   *          the number of trucks to create
   */
  public final void createTrucks(final int count) {
    int i;
    Random r;
    List<Location> l;

    l = this.depots;
    r = this.rand;
    for (i = 0; i < count; i++) {
      this.randomTruck(l.get(r.nextInt(l.size())));
    }

  }

  /**
   * Create a random container
   *
   * @param l
   *          the location
   * @return the container
   */
  private final Container randomContainer(final Location l) {
    Container c;
    c = new Container(Container.CONTAINERS.size(), l);
    System.out.println("new container: " + c);//$NON-NLS-1$
    return c;
  }

  /**
   * Create a random driver
   *
   * @param l
   *          the location
   * @return the driver
   */
  private final Driver randomDriver(final Location l) {
    Random r;
    int c;
    Driver d;

    r = this.rand;
    c = (7000 + (r.nextInt(5) * 1000));
    d = new Driver(Driver.DRIVERS.size(), l, c);
    System.out.println("new driver: " + d);//$NON-NLS-1$
    return d;
  }

  /**
   * Create a random truck
   *
   * @param l
   *          the starting location
   * @return the truck
   */
  private final Truck randomTruck(final Location l) {
    int cost, cap, drv;
    Random r;
    Truck t;
    int s;

    r = this.rand;

    cap = 1 + r.nextInt(3);
    drv = 1 + r.nextInt(2);

    cost = (7 * (cap + cap + drv + 1));
    cost *= (r.nextInt(3) + 1);
    cost = (5 * ((cost + 4) / 5));
    cost += 100;

    do {
      s = 70 - ((int) ((0.5d + (20 * (r.nextInt(cap + 1) * //
      (1d - (0.2d * r.nextGaussian())))))));
      s = Math.max(1, (((s + 3) / 5) * 5));
    } while ((s < 30) || (s > 60));

    t = new Truck(Truck.TRUCKS.size(), l, cap, drv, cost, s);

    System.out.println("new truck: " + t);//$NON-NLS-1$
    return t;
  }

  /**
   * the location holder
   *
   * @author Thomas Weise
   */
  private static final class LocationHolder {
    /** the location */
    public final Location location;

    /** the x-coordinate */
    public final int x;

    /** the y-coordinate */
    public final int y;

    /**
     * Create a new location holder
     *
     * @param l
     *          the location
     * @param i
     *          the x-coordinate
     * @param j
     *          the y-coordinate
     */
    LocationHolder(final Location l, final int i, final int j) {
      super();
      this.location = l;
      this.x = i;
      this.y = j;
    }
  }

  /**
   * An order place holder
   *
   * @author Thomas Weise
   */
  private static final class OPH {

    /** the placeholder id */
    final int id;

    /** the start */
    final long start;

    /** from where */
    final Location from;

    /**
     * Create a new order place holder
     *
     * @param i
     *          the id
     * @param s
     *          the start time
     * @param f
     *          from
     */
    OPH(final int i, final long s, final Location f) {
      super();
      this.id = i;
      this.start = s;
      this.from = f;
    }
  }

  /**
   * The order manager
   *
   * @author Thomas Weise
   */
  static final class OrderManager {

    /** the oph */
    private final List<OPH> stack;

    /** the id counter */
    private int idc;

    /** the orders to resolve */
    private final List<Order[]> resolve;

    /** the id resolver */
    private final List<int[]> idr;

    /** instantiate the order manager */
    OrderManager() {
      super();
      this.stack = new ArrayList<OPH>();
      this.resolve = new ArrayList<Order[]>();
      this.idr = new ArrayList<int[]>();
    }

    /**
     * Push an order
     *
     * @param from
     *          where the order comes from
     * @param start
     *          the start time
     */
    final void push(final Location from, final long start) {
      this.stack.add(new OPH(this.idc++, start, from));
    }

    /**
     * The number of pending orders
     *
     * @return the number of pending orders
     */
    final int size() {
      return (this.stack.size());
    }

    /**
     * Get the current orders
     *
     * @return an array with placeholders for the current orders
     */
    final Order[] currentOrders() {
      Order[] res;
      int[] ids;
      int l;
      List<OPH> ss;

      ss = this.stack;
      l = ss.size();
      res = new Order[l];
      this.resolve.add(res);
      ids = new int[l];
      this.idr.add(ids);

      for (; (--l) >= 0;) {
        ids[l] = ss.get(l).id;
      }

      return res;
    }

    /**
     * Pick a random order
     *
     * @param r
     *          the randomizer
     * @param dest
     *          the destination
     * @param dt
     *          the destination time
     * @param force
     *          force delete
     * @return true if actually an order was added, false otherwise
     */
    final boolean random(final Random r, final Location dest,
        final long dt, final boolean force) {
      OPH h;
      Order n;
      int[] y;
      final List<int[]> ys;
      final List<Order[]> xs;
      final List<OPH> z;
      int i, j;

      z = this.stack;
      j = 10000;
      do {
        i = z.size();
        if (i <= 0) {
          return false;
        }
        i = r.nextInt(i);
        if (force) {
          h = z.remove(i);
        } else {
          h = z.get(i);
        }
        if ((--j) <= 0) {
          return false;
        }
      } while (h.from == dest);

      if (!force) {
        z.remove(i);
      }

      n = randomOrder(r, h.from, h.start, dest, dt);
      ys = this.idr;
      xs = this.resolve;

      for (i = ys.size(); (--i) >= 0;) {
        y = ys.get(i);

        for (j = y.length; (--j) >= 0;) {
          if (y[j] == h.id) {
            xs.get(i)[j] = n;
          }
        }

      }

      return true;
    }

    /**
     * Create an order
     *
     * @param r
     *          the randomizer
     * @param startT
     *          the start time
     * @param start
     *          the start location
     * @param end
     *          the end location
     * @param endT
     *          the end time
     * @return the Order
     */
    private static final Order randomOrder(final Random r,
        final Location start, long startT, final Location end, long endT) {
      Order o;
      long mins, maxs, mine, maxe, h;

      mins = Math.max(0, (900000l * (startT / 900000l)));
      maxs = Math.max(mins, Math.max(startT,
          (900000l * ((startT + 899999l) / 900000l))));
      mine = Math.max(mins, Math.min(endT, (900000l * (endT / 900000l))));
      maxe = Math.max(maxs, Math.max(endT,
          (900000l * ((endT + 899999l) / 900000l))));

      if ((mins > maxs) || (mins > mine) || (maxs > maxe) || (mine > maxe)) {
        throw new RuntimeException();
      }

      do {

        if (r.nextBoolean()) {
          h = mins - 900000;
          if (h >= 0) {
            mins = h;
          }
        }

        if (r.nextBoolean()) {
          h = maxs + 900000;
          if (h < maxe) {
            maxs = h;
          }
        }

        if (r.nextBoolean()) {
          h = mine - 900000;
          if (mine > mins) {
            mine = h;
          }
        }

        if (r.nextBoolean()) {
          maxe = maxe + 900000;
        }

      } while (r.nextInt(3) > 0);

      o = new Order(Order.ORDERS.size(), mins, maxs, start, mine, maxe,
          end);
      System.out.println("new order: " + o);//$NON-NLS-1$

      return o;
    }

    /**
     * Return the maximum number of actually used orders
     *
     * @return the maximum number of actually used orders
     */
    final int getMaxOrders() {
      int i, j, max, s;
      final List<Order[]> lo;
      Order[] x;

      lo = this.resolve;
      max = 0;
      for (i = lo.size(); (--i) >= 0;) {
        x = lo.get(i);
        s = 0;
        for (j = x.length; (--j) >= 0;) {
          if (x[j] != null) {
            s++;
          }
        }
        if (s > max) {
          max = s;
        }
      }

      return max;
    }
  }
}