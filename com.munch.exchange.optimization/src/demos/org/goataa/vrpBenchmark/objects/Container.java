// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * A container is an object which can be used to package one order.
 *
 * @author Thomas Weise
 */
public class Container extends InputObject {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the drivers */
  public static final InputObjectList<Container> CONTAINERS = new InputObjectList<Container>(
      Container.class);

  /** no containers */
  public static final Container[] NO_CONTAINERS = new Container[0];

  /** the headers */
  private static final List<String> HEADERS;

  static {
    HEADERS = new ArrayList<String>();
    HEADERS.addAll(IHEADERS);
    HEADERS.add("start");//$NON-NLS-1$
  }

  /** the start location */
  public final Location start;

  /**
   * Create a new container
   *
   * @param pid
   *          the id
   * @param lstart
   *          the start location
   */
  public Container(final int pid, final Location lstart) {
    super(pid, CONTAINERS);
    this.start = lstart;
  }

  /**
   * Create a new truck
   *
   * @param data
   *          the data
   */
  public Container(final String[] data) {
    super(data, CONTAINERS);
    this.start = Location.LOCATIONS.find(InputObject.extractId(data[1]));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final char getIDPrefixChar() {
    return 'C';
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
  }
}