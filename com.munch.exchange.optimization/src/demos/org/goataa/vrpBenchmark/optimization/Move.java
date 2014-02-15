// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import demos.org.goataa.vrpBenchmark.objects.Container;
import demos.org.goataa.vrpBenchmark.objects.Driver;
import demos.org.goataa.vrpBenchmark.objects.InputObject;
import demos.org.goataa.vrpBenchmark.objects.Location;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.objects.TransportationObject;
import demos.org.goataa.vrpBenchmark.objects.Truck;

/**
 * A move
 *
 * @author Thomas Weise
 */
public class Move extends TransportationObject {

  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** no moves */
  public static final Move[] NO_MOVES = new Move[0];

  /** split */
  static final Pattern SPLIT = Pattern.compile("[,]+"); //$NON-NLS-1$

  /** the headers */
  private static final List<String> HEADERS;

  static {
    HEADERS = new ArrayList<String>();
    HEADERS.add("truck");//$NON-NLS-1$
    HEADERS.add("orders"); //$NON-NLS-1$
    HEADERS.add("drivers");//$NON-NLS-1$
    HEADERS.add("containers");//$NON-NLS-1$
    HEADERS.add("from");//$NON-NLS-1$
    HEADERS.add("start_time");//$NON-NLS-1$
    HEADERS.add("to");//$NON-NLS-1$
  }

  /** the truck */
  public final Truck truck;

  /** the involved orders */
  public final Order[] orders;

  /** the involved drivers */
  public final Driver[] drivers;

  /** the involved containers */
  public final Container[] containers;

  /** the starting location */
  public final Location from;

  /** the start time */
  public final long startTime;

  /** the destination location */
  public final Location to;

  /**
   * Create a new freight object
   *
   * @param t
   *          the truck
   * @param o
   *          the orders
   * @param d
   *          the drivers
   * @param c
   *          the container
   * @param lfrom
   *          the starting location
   * @param lstartTime
   *          the start time
   * @param lto
   *          the destination location
   */
  public Move(final Truck t, final Order[] o, final Driver[] d,
      final Container[] c, final Location lfrom, long lstartTime,
      final Location lto) {
    super();

    this.truck = t;
    this.orders = (((o != null) && (o.length > 0)) ? o : Order.NO_ORDERS);
    this.drivers = (((d != null) && (d.length > 0)) ? d
        : Driver.NO_DRIVERS);
    this.containers = (((c != null) && (c.length > 0)) ? c
        : Container.NO_CONTAINERS);
    this.from = lfrom;
    this.startTime = lstartTime;
    this.to = lto;
  }

  /**
   * Create a move from a textual representation
   *
   * @param s
   *          the strings
   */
  public Move(final String[] s) {
    this(Truck.TRUCKS.find(InputObject.extractId(s[0])), getOrders(s[1]),
        getDrivers(s[2]), getContainers(s[3]), Location.LOCATIONS
            .find(InputObject.extractId(s[4])), Long.parseLong(s[5]),
        Location.LOCATIONS.find(InputObject.extractId(s[4])));
  }

  /**
   * Get the drivers
   *
   * @param s
   *          the strings
   * @return the drivers
   */
  private static final Driver[] getDrivers(final String s) {
    int i, l;
    boolean b;
    String q;
    String[] qs;
    Driver[] o;

    if ((s == null) || ((l = (s.length())) <= 0)) {
      return Driver.NO_DRIVERS;
    }

    i = 0;
    b = false;
    if (s.charAt(0) == '(') {
      i++;
      b = true;
    }
    if (s.charAt(l - 1) == ')') {
      l--;
      b = true;
    }
    if (b) {
      q = s.substring(i, l);
    } else {
      q = s;
    }

    qs = SPLIT.split(q);
    if ((qs == null) || ((l = qs.length) <= 0)) {
      return Driver.NO_DRIVERS;
    }
    o = new Driver[l];
    for (i = 0; i < l; i++) {
      o[i] = Driver.DRIVERS.find(InputObject.extractId(qs[i]));
    }

    return o;
  }

  /**
   * Get the orders
   *
   * @param s
   *          the strings
   * @return the orders
   */
  private static final Order[] getOrders(final String s) {
    int i, l;
    boolean b;
    String q;
    String[] qs;
    Order[] o;

    if ((s == null) || ((l = (s.length())) <= 0)) {
      return Order.NO_ORDERS;
    }

    i = 0;
    b = false;
    if (s.charAt(0) == '(') {
      i++;
      b = true;
    }
    if (s.charAt(l - 1) == ')') {
      l--;
      b = true;
    }
    if (b) {
      q = s.substring(i, l);
    } else {
      q = s;
    }

    qs = SPLIT.split(q);
    if ((qs == null) || ((l = qs.length) <= 0)) {
      return Order.NO_ORDERS;
    }
    o = new Order[l];
    for (i = 0; i < l; i++) {
      o[i] = Order.ORDERS.find(InputObject.extractId(qs[i]));
    }

    return o;
  }

  /**
   * Get the containers
   *
   * @param s
   *          the strings
   * @return the containers
   */
  private static final Container[] getContainers(final String s) {
    int i, l;
    boolean b;
    String q;
    String[] qs;
    Container[] o;

    if ((s == null) || ((l = (s.length())) <= 0)) {
      return Container.NO_CONTAINERS;
    }

    i = 0;
    b = false;
    if (s.charAt(0) == '(') {
      i++;
      b = true;
    }
    if (s.charAt(l - 1) == ')') {
      l--;
      b = true;
    }
    if (b) {
      q = s.substring(i, l);
    } else {
      q = s;
    }

    qs = SPLIT.split(q);
    if ((qs == null) || ((l = qs.length) <= 0)) {
      return Container.NO_CONTAINERS;
    }
    o = new Container[l];
    for (i = 0; i < l; i++) {
      o[i] = Container.CONTAINERS.find(InputObject.extractId(qs[i]));
    }

    return o;
  }

  /**
   * Get the column headers for a csv data representation. Child classes
   * may add additional fields.
   *
   * @return the column headers for representing the data of this object in
   *         csv format
   */
  @Override
  public List<String> getCSVColumnHeader() {
    return HEADERS;
  }

  /**
   * Convert an object array to a string
   *
   * @param src
   *          the source
   * @return the string
   */
  private static final String toString(final InputObject[] src) {
    StringBuffer sb;
    int i;
    boolean b;
    InputObject x;

    sb = new StringBuffer();
    sb.append('(');
    b = false;

    for (i = 0; i < src.length; i++) {

      x = src[i];
      if (x != null) {
        if (b) {
          sb.append(',');
        }
        sb.append(x.getIDString());
        b = true;
      }

    }

    sb.append(')');
    return sb.toString();
  }

  /**
   * Fill in the data of this object into a string array which can be used
   * to serialize the object in a nice way
   *
   * @param output
   *          the output
   */
  @Override
  public void fillInCSVData(final String[] output) {
    output[0] = this.truck.getIDString();
    output[1] = toString(this.orders);
    output[2] = toString(this.drivers);
    output[3] = toString(this.containers);
    output[4] = this.from.getIDString();
    output[5] = String.valueOf(this.startTime);
    output[6] = this.to.getIDString();
  }

  /**
   * Does this move use the given container?
   *
   * @param c
   *          the container
   * @return true if the move uses the container, false otherwise
   */
  public final boolean usesContainer(final Container c) {
    int i;

    for (i = this.containers.length; (--i) >= 0;) {
      if (this.containers[i] == c) {
        return true;
      }
    }

    return false;
  }

  /**
   * Does this move use the given driver?
   *
   * @param c
   *          the driver
   * @return true if the move uses the driver, false otherwise
   */
  public final boolean usesDriver(final Driver c) {
    int i;

    for (i = this.drivers.length; (--i) >= 0;) {
      if (this.drivers[i] == c) {
        return true;
      }
    }

    return false;
  }
}