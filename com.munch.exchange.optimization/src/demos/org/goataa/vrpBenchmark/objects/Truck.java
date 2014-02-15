// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * A truck is a car that can transport containers.
 *
 * @author Thomas Weise
 */
public class Truck extends InputObject {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the trucks */
  public static final InputObjectList<Truck> TRUCKS = new InputObjectList<Truck>(
      Truck.class);

  /** the headers */
  private static final List<String> HEADERS;

  static {
    HEADERS = new ArrayList<String>();
    HEADERS.addAll(IHEADERS);
    HEADERS.add("start_location");//$NON-NLS-1$
    HEADERS.add("max_containers");//$NON-NLS-1$
    HEADERS.add("max_drivers");//$NON-NLS-1$
    HEADERS.add("costs_per_h_km");//$NON-NLS-1$
    HEADERS.add("avg_speed_km_h");//$NON-NLS-1$
  }

  /** the starting location */
  public final Location start;

  /** the maximum number of containers this truck can transport */
  public final int maxContainers;

  /** the maximum number of drivers */
  public final int maxDrivers;

  /** the costs per kilometer in cent */
  public final int costsPerHKm;

  /** the average speed in km/h */
  public final int avgSpeedKmH;

  /**
   * Create a new truck
   *
   * @param pid
   *          the id
   * @param lstart
   *          the starting location
   * @param maxCont
   *          the maximum number of containers this truck can transport
   * @param maxDriv
   *          the maximum number of drivers that can sit in this truck
   * @param costsHKM
   *          the costs per hour*kilometer in cent
   * @param avgSpeed
   *          the average speed in km/h
   */
  public Truck(final int pid, final Location lstart, final int maxCont,
      final int maxDriv, final int costsHKM, final int avgSpeed) {
    super(pid, TRUCKS);
    this.start = lstart;
    this.maxContainers = maxCont;
    this.maxDrivers = maxDriv;
    this.costsPerHKm = costsHKM;
    this.avgSpeedKmH = avgSpeed;
  }

  /**
   * Create a new truck
   *
   * @param data
   *          the data
   */
  public Truck(final String[] data) {
    super(data, TRUCKS);
    this.start = Location.LOCATIONS.find(InputObject.extractId(data[1]));
    this.maxContainers = Integer.parseInt(data[2]);
    this.maxDrivers = Integer.parseInt(data[3]);
    this.costsPerHKm = Integer.parseInt(data[4]);
    this.avgSpeedKmH = Integer.parseInt(data[5]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final char getIDPrefixChar() {
    return 'T';
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
    output[1] = this.start.getIDString();
    output[2] = String.valueOf(this.maxContainers);
    output[3] = String.valueOf(this.maxDrivers);
    output[4] = String.valueOf(this.costsPerHKm);
    output[5] = String.valueOf(this.avgSpeedKmH);
  }
}