// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.ArrayList;
import java.util.List;

import org.goataa.impl.OptimizationModule;

/**
 * The base class for freight objects
 *
 * @author Thomas Weise
 */
public class TransportationObject extends OptimizationModule {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Create a new freight object
   */
  protected TransportationObject() {
    super();
  }

  /**
   * Get the column headers for a csv data representation. Child classes
   * may add additional fields.
   *
   * @return the column headers for representing the data of this object in
   *         csv format
   */
  public List<String> getCSVColumnHeader() {
    return new ArrayList<String>();
  }

  /**
   * Fill in the data of this object into a string array which can be used
   * to serialize the object in a nice way
   *
   * @param output
   *          the output
   */
  public void fillInCSVData(final String[] output) {
    //
  }

  /**
   * Get the full configuration which holds all the data necessary to
   * describe this object.
   *
   * @param longVersion
   *          true if the long version should be returned, false if the
   *          short version should be returned
   * @return the full configuration
   */
  @Override
  public String getConfiguration(final boolean longVersion) {
    List<String> l;
    String[] ss;
    StringBuilder sb;
    int i;

    l = this.getCSVColumnHeader();
    ss = new String[l.size()];
    this.fillInCSVData(ss);
    sb = new StringBuilder();
    for (i = 0; i < ss.length; i++) {
      if (i > 0) {
        sb.append(',');
        if (longVersion) {
          sb.append(' ');
        }
      }
      if (longVersion) {
        sb.append(l.get(i));
        sb.append('=');
      }
      sb.append(ss[i]);
    }
    return sb.toString();
  }
}