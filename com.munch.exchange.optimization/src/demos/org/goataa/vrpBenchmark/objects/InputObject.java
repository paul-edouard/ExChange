// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The base class for freight objects
 *
 * @author Thomas Weise
 */
public class InputObject extends TransportationObject implements
    Serializable {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the headers */
  static final List<String> IHEADERS;

  static {
    IHEADERS = new ArrayList<String>();
    IHEADERS.add("id");//$NON-NLS-1$
  }

  /** the location id */
  public final int id;

  /** the id string */
  private transient String idString;

  /**
   * Create a new freight object
   *
   * @param pid
   *          the id
   * @param l
   *          the transportation object list
   */
  @SuppressWarnings("unchecked")
  InputObject(final int pid, final InputObjectList l) {
    super();
    this.id = pid;
    l.add(this);
  }

  /**
   * Create a new freight object
   *
   * @param data
   *          the data
   * @param l
   *          the object list
   */
  @SuppressWarnings("unchecked")
  InputObject(final String[] data, final InputObjectList l) {
    this(extractId(data[0]), l);
  }

  /**
   * Extract the id from a string
   *
   * @param s
   *          the string
   * @return the id
   */
  public static final int extractId(final String s) {
    return Integer.parseInt(s.substring(1));
  }

  /**
   * Get the id prefix char
   *
   * @return the id prefix char
   */
  char getIDPrefixChar() {
    return 'F';
  }

  /**
   * Get the id string
   *
   * @return the id string
   */
  public String getIDString() {
    String s;
    int i;
    StringBuilder sb;

    s = this.idString;
    if (s == null) {

      sb = new StringBuilder(7);

      sb.append(this.getIDPrefixChar());

      i = this.id;

      if (i < 100000) {
        sb.append('0');
        if (i < 10000) {
          sb.append('0');
          if (i < 1000) {
            sb.append('0');
            if (i < 100) {
              sb.append('0');
              if (i < 10) {
                sb.append('0');
              }
            }
          }
        }
      }

      sb.append(this.id);
      this.idString = s = sb.toString();
    }

    return s;
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
    return IHEADERS;
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
    output[0] = this.getIDString();
  }
}