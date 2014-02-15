// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * An order is a set of goods that must be taken from one place to another
 * one. It requires a single of container and has timewindoes for pickup
 * and delivery as well as a start and an end location.
 *
 * @author Thomas Weise
 */
public class Order extends InputObject {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the list of orders */
  public static final InputObjectList<Order> ORDERS = new InputObjectList<Order>(
      Order.class);

  /** no orders */
  public static final Order[] NO_ORDERS = new Order[0];

  /** the headers */
  private static final List<String> HEADERS;

  static {
    HEADERS = new ArrayList<String>();
    HEADERS.addAll(IHEADERS);
    HEADERS.add("pickup_window_start");//$NON-NLS-1$
    HEADERS.add("pickup_window_end");//$NON-NLS-1$
    HEADERS.add("pickup_location");//$NON-NLS-1$
    HEADERS.add("delivery_window_start");//$NON-NLS-1$
    HEADERS.add("delivery_window_end");//$NON-NLS-1$
    HEADERS.add("delivery_location");//$NON-NLS-1$
  }

  /** the beginning of the pickup time */
  public final long pickupWindowStart;

  /** the ending of the pickup time */
  public final long pickupWindowEnd;

  /** the pickup location */
  public final Location pickupLocation;

  /** the beginning of the delivery time */
  public final long deliveryWindowStart;

  /** the ending of the deliver time */
  public final long deliveryWindowEnd;

  /** the delivery location */
  public final Location deliveryLocation;

  /**
   * Create a new order
   *
   * @param pid
   *          the id
   * @param ppickupWindowStart
   *          the beginning of the pickup time
   * @param ppickupWindowEnd
   *          the ending of the pickup time
   * @param ppickupLocation
   *          the pickup location
   * @param pdeliveryWindowStart
   *          the beginning of the delivery time
   * @param pdeliveryWindowEnd
   *          the ending of the deliver time
   * @param pdeliveryLocation
   *          the delivery location
   */
  public Order(final int pid, final long ppickupWindowStart,
      final long ppickupWindowEnd, final Location ppickupLocation,
      final long pdeliveryWindowStart, final long pdeliveryWindowEnd,
      final Location pdeliveryLocation) {
    super(pid, ORDERS);
    this.pickupWindowStart = ppickupWindowStart;
    this.pickupWindowEnd = ppickupWindowEnd;
    this.pickupLocation = ppickupLocation;
    this.deliveryWindowStart = pdeliveryWindowStart;
    this.deliveryWindowEnd = pdeliveryWindowEnd;
    this.deliveryLocation = pdeliveryLocation;
  }

  /**
   * Create a new order
   *
   * @param data
   *          the order data
   */
  public Order(final String[] data) {
    super(data, ORDERS);

    this.pickupWindowStart = Long.parseLong(data[1]);
    this.pickupWindowEnd = Long.parseLong(data[2]);
    this.pickupLocation = Location.LOCATIONS.find(extractId(data[3]));
    this.deliveryWindowStart = Long.parseLong(data[4]);
    this.deliveryWindowEnd = Long.parseLong(data[5]);
    this.deliveryLocation = Location.LOCATIONS.find(extractId(data[6]));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final char getIDPrefixChar() {
    return 'O';
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
   * Fill in the data of this object into a string array which can be used
   * to serialize the object in a nice way
   *
   * @param output
   *          the output
   */
  @Override
  public void fillInCSVData(final String[] output) {
    super.fillInCSVData(output);
    output[1] = String.valueOf(this.pickupWindowStart);
    output[2] = String.valueOf(this.pickupWindowEnd);
    output[3] = String.valueOf(this.pickupLocation.getIDString());
    output[4] = String.valueOf(this.deliveryWindowStart);
    output[5] = String.valueOf(this.deliveryWindowEnd);
    output[6] = String.valueOf(this.deliveryLocation.getIDString());
  }
}