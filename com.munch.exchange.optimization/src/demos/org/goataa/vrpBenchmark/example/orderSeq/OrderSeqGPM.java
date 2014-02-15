/*
 * Copyright (c) 2010 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.vrpBenchmark.example.orderSeq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.goataa.impl.gpms.GPM;

import demos.org.goataa.vrpBenchmark.example.utils.ContainerManager;
import demos.org.goataa.vrpBenchmark.example.utils.DriverManager;
import demos.org.goataa.vrpBenchmark.example.utils.TruckManager;
import demos.org.goataa.vrpBenchmark.objects.Container;
import demos.org.goataa.vrpBenchmark.objects.DistanceMatrix;
import demos.org.goataa.vrpBenchmark.objects.Driver;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.objects.Truck;
import demos.org.goataa.vrpBenchmark.optimization.Move;
import demos.org.goataa.vrpBenchmark.optimization.PreMove;

/**
 * A gpm determining the order sequence. The idea is that an integer string
 * represents the sequence in which the orders should be processed. The GPM
 * is randomized to a certain degree: It use probabilities to involve
 * objects which are close to each other in the solution construction. The
 * permutation-genotypes include additional dummy elements which are used
 * to configure the random number generator and probabilities. Hence, the
 * GPM becomes deterministic.
 *
 * @author Thomas Weise
 */
public class OrderSeqGPM extends GPM<int[], Move[]> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the move list */
  private final ArrayList<Move> temp;

  /** the randomizer */
  private final Random rand;

  /** the pre move buffer */
  private final ArrayList<PreMove> buf;

  /** the cache */
  private final ArrayList<PreMove> cache;

  /** the container manager */
  private final ContainerManager cm;

  /** the container manager */
  private final TruckManager tm;

  /** the container manager */
  private final DriverManager dm;

  /** Create the order sequence gpm */
  public OrderSeqGPM() {
    super();
    this.temp = new ArrayList<Move>();
    this.rand = new Random();
    this.buf = new ArrayList<PreMove>();
    this.cache = new ArrayList<PreMove>();
    this.cm = new ContainerManager();
    this.tm = new TruckManager();
    this.dm = new DriverManager();
  }

  /**
   * IfThenElse a move
   *
   * @param m
   *          the move to add
   */
  public final void addMove(final Move m) {
    this.temp.add(m);
  }

  /**
   * Create the moves
   *
   * @return the moves
   */
  public final Move[] createMoves() {
    Move[] m;

    m = this.temp.toArray(new Move[this.temp.size()]);
    this.temp.clear();

    return m;
  }

  /**
   * Allocate a pre move
   *
   * @return a pre move
   */
  protected final PreMove allocatePreMove() {
    int i;

    i = this.cache.size();
    if (i > 0) {
      return this.cache.remove(i - 1);
    }
    return new PreMove();
  }

  /**
   * Dispose a pre move
   *
   * @param m
   *          the pre move
   */
  protected final void disposePreMove(final PreMove m) {
    m.clear();
    this.cache.add(m);
  }

  /**
   * buffer a pre move
   *
   * @param p
   *          the pre move
   */
  protected void bufferPreMove(final PreMove p) {
    this.buf.add(p);
  }

  /** Flush the buffer */
  protected void flushBuffer() {
    int i, s;
    final ArrayList<PreMove> b;
    PreMove v, m1, m2;

    b = this.buf;
    s = b.size();

    if (s <= 0) {
      return;
    }

    for (i = 0; i < s; i++) {
      m1 = b.get(i);
      this.addMove(m1.compile());
      if ((i > 0) && (i < (s - 1))) {
        this.disposePreMove(m1);
      }
    }

    m1 = b.get(0);
    m2 = b.get(s - 1);

    if (m2.to != m1.from) {
      v = this.allocatePreMove();
      v.asNext(m2);
      v.to = m1.from;
      this.addMove(v.compile());
      this.disposePreMove(v);
      this.disposePreMove(m1);
      if (m1 != m2) {
        this.disposePreMove(m2);
      }
    }

    b.clear();
  }

  /**
   * Get the last move
   *
   * @return move
   */
  protected PreMove lastMove() {
    int i;
    i = this.buf.size();
    if (i > 0) {
      return this.buf.get(i - 1);
    }
    return null;
  }

  /**
   * Try to deliver the order or
   *
   * @param o
   *          the order
   * @param r
   *          the randomizer
   * @return true if the order could easily be added
   */
  protected boolean tryDeliver(final Order o, final Random r) {
    int i, j, k;
    final List<PreMove> l;
    PreMove m, x, z;
    long start, arrive, start2, arrive2;
    final int s;
    Container c;

    l = this.buf;
    s = l.size();

    outer: for (i = s; (--i) >= 0;) {
      m = l.get(i);

      start = m.startTime;

      if (i > 0) {
        arrive = l.get(i - 1).getEarliestLeaveTime();
      } else {
        arrive = 0;
      }

      if ((m.from == o.pickupLocation)
          && ((start <= o.pickupWindowEnd) || (arrive >= o.pickupWindowStart))
          && (m.orders.size() < m.truck.maxContainers)) {
        // ok, so we could pick up the order from here...

        // check if it is completely on our route
        for (j = (i + 1); j < s; j++) {
          x = l.get(j);

          // ok, we are over capacity - there is nothing we can do
          if (x.orders.size() >= m.truck.maxContainers) {
            continue outer;
          }

          // it is too late
          if (x.startTime > o.deliveryWindowEnd) {
            continue outer;
          }

          // aha, it would be possible to go from m to x
          if (x.to == o.deliveryLocation) {
            start2 = x.startTime
                + DistanceMatrix.MATRIX.getTimeNeeded(x.from, x.to,
                    x.truck);
            if (j < (s - 1)) {
              arrive2 = l.get(j + 1).startTime;
            } else {
              arrive2 = Long.MAX_VALUE;
            }

            // but can we do it in time?
            if ((start2 <= o.deliveryWindowEnd)
                && (arrive2 >= o.deliveryWindowStart)) {
              // do we have enough containers?
              if (m.containers.size() <= m.orders.size()) {
                // ok, we need another container
                c = this.cm.getAtLocation(m.from, r, true);
                if (c == null) {
                  continue outer;
                }
                // now we need to carry it along
                addContainers: for (k = i; k < s; k++) {
                  z = l.get(k);
                  if (z.containers.size() < z.truck.maxContainers) {
                    l.get(k).containers.add(c);
                  } else {
                    break addContainers;
                  }
                }
              }

              // so there are enough containers
              for (k = i; k <= j; k++) {
                // let us add the order
                l.get(k).orders.add(o);
              }
              // perfect, nothing else is necessary
              return true;
            }

          }

        }

        // all intermediate steps have been done, but we cannot drop off
        // the order, we need to add another tour
        z = l.get(s - 1);
        start2 = z.getEarliestLeaveTime();
        arrive2 = start2
            + DistanceMatrix.MATRIX.getTimeNeeded(z.to,
                o.deliveryLocation, z.truck);
        if (arrive2 > o.deliveryWindowEnd) {
          continue outer;
        }

        // do we have enough containers?
        if (m.containers.size() <= m.orders.size()) {
          // ok, we need another container
          c = this.cm.getAtLocation(m.from, r, true);
          if (c == null) {
            continue outer;
          }
          // now we need to carry it along
          addContainers: for (k = i; k < s; k++) {
            z = l.get(k);
            if (z.containers.size() < z.truck.maxContainers) {
              l.get(k).containers.add(c);
            } else {
              break addContainers;
            }
          }
        }

        // so there are enough containers
        for (k = i; k < s; k++) {
          // let us add the order
          l.get(k).orders.add(o);
        }
        z = this.allocatePreMove();
        z.asNext(l.get(s - 1));
        z.to = o.deliveryLocation;

        // perfect, nothing else is necessary
        return true;

      }
    }

    // no delivery was possible
    return false;
  }

  /**
   * This function carries out the genotype-phenotype mapping as defined in
   * Definition D4.11. In other words, it translates one genotype (an
   * element in the search space) to one element in the problem space,
   * i.e., a phenotype.
   *
   * @param g
   *          the genotype (Definition D4.2)
   * @param rnd
   *          the randomizer
   * @return the phenotype (see Definition D2.2) corresponding to the
   *         genotype g
   */
  @Override
  public Move[] gpm(final int[] g, final Random rnd) {
    Random r;
    final double rp, cp;
    int i, ord;
    Order next;

    r = this.rand;

    r.setSeed(this.getSeed(g));
    rp = this.getRetryProb(g);
    cp = this.getContinueProb(g);

    this.tm.init();
    this.dm.init();
    this.cm.init();

    outer: for (i = 0; i < g.length; i++) {

      ord = g[i];
      if (this.fieldType(ord) != 0) {
        continue outer;
      }

      next = Order.ORDERS.get(ord);

      if (r.nextDouble() < cp) {
        if (this.tryDeliver(next, r)) {
          continue outer;
        }
      }

      this.flushBuffer();

      if (this.deliver(next, rp, r)) {
        continue outer;
      }

      if (this.tm.isEmpty()) {
        this.tm.init();
      }
      if (this.cm.isEmpty()) {
        this.cm.init();
      }
      if (this.dm.isEmpty()) {
        this.dm.init();
      }
      this.deliver(next, rp, r);
    }

    this.flushBuffer();
    return this.createMoves();
  }

  /**
   * do deliver
   *
   * @param o
   *          the order
   * @param rtp
   *          the retry probability
   * @param r
   *          the randomizer
   * @return true if the delivery was done
   */
  protected boolean deliver(final Order o, final double rtp, final Random r) {
    Truck t;
    long st, containerToOrder, truckToContainer, truckToOrder;
    Driver d;
    Container c;
    PreMove pm;
    Location l;

    containerer: {
      driverer: for (;;) {
        trucker: for (;;) {
          t = this.tm.getCloseTo(o.pickupLocation, rtp, r, true);
          if (t == null) {
            return false;
          }
          st = truckToOrder = DistanceMatrix.MATRIX.getTimeNeeded(t.start,
              o.pickupLocation, t);

          if (st < o.pickupWindowEnd) {
            break trucker;
          }
        }
        d = this.dm.getAtLocation(t.start, r, true);
        if (d != null) {
          break driverer;
        }
      }

      c = null;
      if (r.nextBoolean()) {
        c = this.cm.getAtLocation(t.start, r, true);

        if (c != null) {
          containerToOrder = truckToOrder;
          truckToContainer = 0;
          break containerer;
        }
      }

      if (r.nextBoolean()) {
        c = this.cm.getAtLocation(o.pickupLocation, r, true);

        if (c != null) {
          truckToContainer = truckToOrder;
          containerToOrder = 0;
          break containerer;
        }
      }

      for (;;) {
        c = this.cm.getCloseTo(r.nextBoolean() ? t.start
            : o.pickupLocation, rtp, r, true);
        if (c == null) {
          return false;
        }
        truckToContainer = DistanceMatrix.MATRIX.getTimeNeeded(t.start,
            c.start, t);
        containerToOrder = DistanceMatrix.MATRIX.getTimeNeeded(c.start,
            o.pickupLocation, t);
        st = truckToContainer + containerToOrder;
        if (st < o.pickupWindowEnd) {
          break containerer;
        }
      }
    }

    // just to prevent compiler warnings
    if (c == null) {
      return false;
    }

    st = Math.max(0, o.pickupWindowStart - containerToOrder
        - truckToContainer);
    l = t.start;
    if (c.start != l) {
      pm = this.allocatePreMove();

      pm.startTime = st;
      st += truckToContainer;
      pm.from = t.start;
      pm.to = l = c.start;
      pm.truck = t;
      pm.drivers.add(d);
      this.bufferPreMove(pm);
    }

    if (l != o.pickupLocation) {
      pm = this.allocatePreMove();
      pm.startTime = st;
      st += containerToOrder;
      pm.from = l;
      pm.to = o.pickupLocation;
      pm.drivers.add(d);
      pm.containers.add(c);
      pm.truck = t;
      this.bufferPreMove(pm);
    }

    pm = this.allocatePreMove();
    pm.startTime = st;
    pm.from = o.pickupLocation;
    pm.to = o.deliveryLocation;
    pm.truck = t;
    pm.drivers.add(d);
    pm.containers.add(c);
    pm.orders.add(o);
    this.bufferPreMove(pm);
    return true;
  }

  /**
   * get the field type
   *
   * @param i
   *          the field
   * @return the field type
   */
  protected int fieldType(final int i) {
    if ((i >= 0) && (i < Order.ORDERS.size())) {
      return 0;
    }
    return (Math.abs(i % 3) + 1);
  }

  /**
   * A very stupid and primitive way to aggregate some values
   *
   * @param g
   *          the genotype
   * @param t
   *          the field type
   * @param bits
   *          the maximum bits
   * @return the aggregate
   */
  protected long aggregate(final int[] g, final int t, final int bits) {
    long l;
    int i, tt;

    l = 0;
    for (i = g.length; (--i) >= 0;) {
      tt = this.fieldType(g[i]);
      if (tt == t) {
        l ^= (1l << (i % bits));
      }
    }

    return l;
  }

  /**
   * Get the random seed
   *
   * @param g
   *          the genotype
   * @return the seed
   */
  protected long getSeed(final int[] g) {
    return this.aggregate(g, 1, 64);
  }

  /**
   * Get the retry probability
   *
   * @param g
   *          the genotype
   * @return the retry probability
   */
  protected double getRetryProb(final int[] g) {
    return (this.aggregate(g, 2, 10)) / 1024d;
  }

  /**
   * Get the continue probability
   *
   * @param g
   *          the genotype
   * @return the continue probability
   */
  protected double getContinueProb(final int[] g) {
    return (this.aggregate(g, 3, 10) / 1024d);
  }

}