// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.optimization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.goataa.impl.utils.TextUtils;

/**
 * An internal class which encapsulates updating the performance counters
 * for the compute class.
 *
 * @author Thomas Weise
 */
class PerformanceValues implements Serializable {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the container errors */
  private int containerErrors;

  /** the new container errors */
  private int newContainerErrors;

  /** the driver errors */
  private int driverErrors;

  /** the new driver errors */
  private int newDriverErrors;

  /** the order errors */
  private int orderErrors;

  /** the new order errors */
  private int newOrderErrors;

  /** the truck errors */
  private int truckErrors;

  /** the new truck errors */
  private int newTruckErrors;

  /** the new pickup error time */
  private long newPickupErrorTime;

  /** the total pickup error time */
  private long pickupErrorTime;

  /** the new truck costs */
  private long newTruckCosts;

  /** the costs caused by truck driving */
  private long truckCosts;

  /** the new driver costs */
  private int newDriverCosts;

  /** the driver costs */
  private int driverCosts;

  /** the new delivery times errors */
  private long newDeliveryErrorTime;

  /** the new delivery time errors */
  private long deliveryErrorTime;

  /** Create a new performance counters instance */
  public PerformanceValues() {
    super();
  }

  /** reset all performance counters */
  void init() {
    this.containerErrors = 0;
    this.driverErrors = 0;
    this.newDriverErrors = 0;
    this.orderErrors = 0;
    this.truckErrors = 0;
    this.pickupErrorTime = 0l;
    this.truckCosts = 0l;
    this.driverCosts = 0;
    this.deliveryErrorTime = 0l;
    this.clearNew();
  }

  /** clear the new errors */
  final void clearNew() {
    this.newContainerErrors = 0;
    this.newDriverErrors = 0;
    this.newOrderErrors = 0;
    this.newTruckErrors = 0;
    this.newPickupErrorTime = 0l;
    this.newTruckCosts = 0l;
    this.newDriverCosts = 0;
    this.newDeliveryErrorTime = 0l;
  }

  /** commit the new errors */
  final void commitNew() {
    this.truckErrors += this.newTruckErrors;
    this.containerErrors += this.newContainerErrors;
    this.driverErrors += this.newDriverErrors;
    this.orderErrors += this.newOrderErrors;
    this.pickupErrorTime += this.newPickupErrorTime;
    this.truckCosts += this.newTruckCosts;
    this.driverCosts += this.newDriverCosts;
    this.deliveryErrorTime += this.newDeliveryErrorTime;
  }

  /**
   * IfThenElse i truck errors
   *
   * @param i
   *          the number of errors
   */
  final void addTruckError(final int i) {
    this.newTruckErrors += Math.max(1, i);
  }

  /**
   * IfThenElse i container errors
   *
   * @param i
   *          the number of errors
   */
  final void addContainerError(final int i) {
    this.newContainerErrors += Math.max(1, i);
  }

  /**
   * IfThenElse i driver errors
   *
   * @param i
   *          the number of errors
   */
  final void addDriverError(final int i) {
    this.newDriverErrors += Math.max(1, i);
  }

  /**
   * IfThenElse i order errors
   *
   * @param i
   *          the number of errors
   */
  final void addOrderError(final int i) {
    this.newOrderErrors += Math.max(1, i);
  }

  /**
   * IfThenElse some pickup error time
   *
   * @param i
   *          the error time in milliseconds
   */
  final void addPickupErrorTime(final long i) {
    this.newPickupErrorTime += Math.max(1l, i);
  }

  /**
   * IfThenElse i truck costs
   *
   * @param i
   *          the costs
   */
  final void addTruckCosts(final long i) {
    this.newTruckCosts += Math.max(1l, i);
  }

  /**
   * IfThenElse i driver costs
   *
   * @param i
   *          the costs
   */
  final void addDriverCosts(final long i) {
    this.newDriverCosts += Math.max(1l, i);
  }

  /**
   * IfThenElse some delivery error time
   *
   * @param i
   *          the delivery time in milliseconds
   */
  final void addDeliveryErrorTime(final long i) {
    this.newDeliveryErrorTime += Math.max(1l, i);
  }

  /**
   * Convert this object to a string
   *
   * @return the string
   */
  @Override
  public final String toString() {
    StringBuilder sb;

    sb = new StringBuilder();

    sb.append("Total Errors        : "); //$NON-NLS-1$
    sb.append(this.driverErrors + this.containerErrors + this.truckErrors
        + this.orderErrors);

    sb.append(TextUtils.NEWLINE);
    sb.append("Total Costs         : ");//$NON-NLS-1$
    sb.append(this.truckCosts + this.driverCosts);

    sb.append(TextUtils.NEWLINE);
    sb.append("Total Time Error    : ");//$NON-NLS-1$
    sb.append(this.deliveryErrorTime + this.pickupErrorTime);

    sb.append(TextUtils.NEWLINE);
    sb.append("Container Errors    : ");//$NON-NLS-1$
    sb.append(this.containerErrors);

    sb.append(TextUtils.NEWLINE);
    sb.append("Driver Errors       : ");//$NON-NLS-1$
    sb.append(this.driverErrors);

    sb.append(TextUtils.NEWLINE);
    sb.append("Order Errors        : ");//$NON-NLS-1$
    sb.append(this.orderErrors);

    sb.append(TextUtils.NEWLINE);
    sb.append("Truck Errors        : ");//$NON-NLS-1$
    sb.append(this.truckErrors);

    sb.append(TextUtils.NEWLINE);
    sb.append("Pickup Time Error   : ");//$NON-NLS-1$
    sb.append(this.pickupErrorTime);

    sb.append(TextUtils.NEWLINE);
    sb.append("Delivery Time Error : ");//$NON-NLS-1$
    sb.append(this.deliveryErrorTime);

    sb.append(TextUtils.NEWLINE);
    sb.append("Truck Costs         : ");//$NON-NLS-1$
    sb.append(this.truckCosts);

    sb.append(TextUtils.NEWLINE);
    sb.append("Driver Costs        : ");//$NON-NLS-1$
    sb.append(this.driverCosts);

    return sb.toString();
  }

  /**
   * Serialize this object to a writer
   *
   * @param w
   *          the writer
   * @throws IOException
   *           if something goes wrong
   */
  public final void serialize(final Writer w) throws IOException {
    w.write(this.toString());
  }

  /**
   * Serialize to a file
   *
   * @param f
   *          the file
   */
  public final void serialize(final File f) {
    FileWriter w;

    try {
      w = new FileWriter(f);
      try {
        this.serialize(w);
        w.flush();
      } finally {
        w.close();
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Get the container errors, i.e., all situations in which a container
   * was transported from a location where it did not reside (physically
   * impossible) or where an order was packed into a non-existing
   * container.
   *
   * @return the container errors
   */
  public final int getContainerErrors() {
    return this.containerErrors;
  }

  /**
   * Get the new container errors, i.e., all container errors caused by the
   * last move.
   *
   * @return the new container errors (caused by the last move)
   */
  public final int getNewContainerErrors() {
    return this.newContainerErrors;
  }

  /**
   * Get the driver errors, i.e., all situations in which a driver drives
   * from a location where it did not reside (physically impossible) or
   * where a truck was driving without a driver.
   *
   * @return the driver errors
   */
  public final int getDriverErrors() {
    return this.driverErrors;
  }

  /**
   * Get the new driver errors, i.e., all driver errors caused by the last
   * move.
   *
   * @return the new driver errors (caused by the last move)
   */
  public final int getNewDriverErrors() {
    return this.newDriverErrors;
  }

  /**
   * Get the order errors, i.e., all situations in which a order was
   * transported from a location where it did not reside (physically
   * impossible), where an order was packed into a non-existing container,
   * or where a timing constraint was violated.
   *
   * @return the order errors
   */
  public final int getOrderErrors() {
    return this.orderErrors;
  }

  /**
   * Get the new order errors, i.e., all order errors caused by the last
   * move.
   *
   * @return the new order errors (caused by the last move)
   */
  public final int getNewOrderErrors() {
    return this.newOrderErrors;
  }

  /**
   * Get the truck errors, i.e., all situations in which a truck was
   * driving from a location where it did not reside (physically
   * impossible).
   *
   * @return the truck errors
   */
  public final int getTruckErrors() {
    return this.truckErrors;
  }

  /**
   * Get the new truck errors, i.e., all truck errors caused by the last
   * move.
   *
   * @return the new truck errors (caused by the last move)
   */
  public final int getNewTruckErrors() {
    return this.newTruckErrors;
  }

  /**
   *Get the truck costs in cent
   *
   * @return the costs in cent caused by moving trucks
   */
  public final long getTruckCosts() {
    return this.truckCosts;
  }

  /**
   * Get the new truck costs
   *
   * @return the new truck costs (caused by the last move)
   */
  public final long getNewTruckCosts() {
    return this.newTruckCosts;
  }

  /**
   *Get the driver costs in cent
   *
   * @return the costs in cent caused by driving trucks
   */
  public final long getDriverCosts() {
    return this.driverCosts;
  }

  /**
   * Get the new driver costs in cent
   *
   * @return the new driver costs in cent (caused by the last move)
   */
  public final long getNewDriverCosts() {
    return this.newDriverCosts;
  }

  /**
   *Get the total time violation caused by pickups (in ms)
   *
   * @return total time violation caused by pickups (in ms)
   */
  public final long getPickupErrorTime() {
    return this.pickupErrorTime;
  }

  /**
   *Get the new time violation caused by pickups (in ms)
   *
   * @return new time violation caused by pickups (in ms, caused by the
   *         last move)
   */
  public final long getNewPickupErrorTime() {
    return this.newPickupErrorTime;
  }

  /**
   *Get the total time violation caused by deliveries (in ms)
   *
   * @return total time violation caused by deliveries (in ms)
   */
  public final long getDeliveryErrorTime() {
    return this.deliveryErrorTime;
  }

  /**
   *Get the new time violation caused by deliveries (in ms)
   *
   * @return new time violation caused by deliveries (in ms, caused by the
   *         last move)
   */
  public final long getNewDeliveryErrorTime() {
    return this.newDeliveryErrorTime;
  }

  /**
   * Get all the errors
   *
   * @return all errors that occured
   */
  public final int getAllErrors() {
    return this.driverErrors + this.containerErrors + this.orderErrors
        + this.truckErrors;
  }

  /**
   * Get all the time window violations
   *
   * @return all the time window violations
   */
  public final long getAllTimeViolations() {
    return this.deliveryErrorTime + this.pickupErrorTime;
  }

  /**
   * Get all the costs
   *
   * @return the costs
   */
  public final long getAllCosts() {
    return this.driverCosts + this.truckCosts;
  }

}