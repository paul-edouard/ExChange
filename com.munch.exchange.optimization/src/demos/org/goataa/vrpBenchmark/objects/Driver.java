// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * A driver is a person that can drive a truck.
 *
 * @author Thomas Weise
 */
public class Driver extends InputObject {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the drivers */
  public static final InputObjectList<Driver> DRIVERS = new InputObjectList<Driver>(
      Driver.class);

  /** no drivers */
  public static final Driver[] NO_DRIVERS = new Driver[0];

  /** the headers */
  private static final List<String> HEADERS;

  static {
    HEADERS = new ArrayList<String>();
    HEADERS.addAll(IHEADERS);
    HEADERS.add("home");//$NON-NLS-1$
    HEADERS.add("costs_per_day");//$NON-NLS-1$
  }

  /** the maximum driving time */
  public static int MAX_DRIVING_TIME = 8 * 60 * 60 * 1000;

  /** the minimum break time */
  public static int MIN_BREAK_TIME = 6 * 60 * 60 * 1000;

  /** the home location */
  public final Location home;

  /** the costs for any day during which the driver works */
  public final int costsPerDay;

  /**
   * Create a new driver
   *
   * @param pid
   *          the id
   * @param lhome
   *          the home location
   * @param costs
   *          the costs per day
   */
  public Driver(final int pid, final Location lhome, final int costs) {
    super(pid, DRIVERS);
    this.home = lhome;
    this.costsPerDay = costs;
  }

  /**
   * Create a new truck
   *
   * @param data
   *          the data
   */
  public Driver(final String[] data) {
    super(data, DRIVERS);
    this.home = Location.LOCATIONS.find(InputObject.extractId(data[1]));
    this.costsPerDay = Integer.parseInt(data[2]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final char getIDPrefixChar() {
    return 'D';
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
    output[1] = this.home.getIDString();
    output[2] = String.valueOf(this.costsPerDay);
  }
}