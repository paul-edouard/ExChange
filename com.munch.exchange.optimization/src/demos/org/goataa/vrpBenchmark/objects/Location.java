// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a location. Each customer, depot, and halting
 * point is always associated with a location.
 *
 * @author Thomas Weise
 */
public class Location extends InputObject {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the locations */
  public static final InputObjectList<Location> LOCATIONS = new InputObjectList<Location>(
      Location.class);

  /** the headers */
  private static final List<String> HEADERS;

  static {
    HEADERS = new ArrayList<String>();
    HEADERS.addAll(IHEADERS);
    HEADERS.add("is_depot");//$NON-NLS-1$
  }

  /** is the location a depot? */
  public final boolean isDepot;

  /**
   * Create a new location of the given id
   *
   * @param pid
   *          the id
   * @param depot
   *          true if and only if the location is a depot
   */
  public Location(final int pid, final boolean depot) {
    super(pid, LOCATIONS);
    this.isDepot = depot;
  }

  /**
   * Create a new location
   *
   * @param data
   *          the data
   */
  public Location(final String[] data) {
    super(data, LOCATIONS);
    this.isDepot = Boolean.parseBoolean(data[1]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final char getIDPrefixChar() {
    return 'L';
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
    output[1] = String.valueOf(this.isDepot);
  }
}