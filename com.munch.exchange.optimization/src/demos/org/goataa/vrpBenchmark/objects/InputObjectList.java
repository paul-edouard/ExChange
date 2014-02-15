// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A list of transportation objects
 *
 * @param <TOT>
 *          the transportation object type
 * @author Thomas Weise
 */
public final class InputObjectList<TOT extends InputObject> extends
    TransportationObjectList<TOT> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the internal comparator for sorting */
  private static final Comparator<InputObject> CMP = new Comp();

  /** are we sorted? */
  private transient boolean sorted;

  /**
   * Create a new list
   *
   * @param clazz
   *          the instance class
   */
  InputObjectList(final Class<TOT> clazz) {
    super(clazz);
  }

  /**
   * IfThenElse a new transportation object
   *
   * @param object
   *          the object
   */
  @Override
  public void add(final TOT object) {
    super.add(object);
    this.sorted = false;
  }

  /**
   * Sort the object list
   */
  public final void sort() {
    if (!(this.sorted)) {
      Collections.sort(this.list, CMP);
      this.sorted = true;
    }
  }

  /**
   * Find an input object by id
   *
   * @param id
   *          the id
   * @return the object
   */
  public final TOT find(final int id) {
    final List<TOT> l;
    TOT o;
    int low, high, mid;

    this.sort();

    l = this.list;
    low = 0;
    high = (l.size() - 1);

    while (low <= high) {
      mid = ((low + high) >>> 1);
      o = l.get(mid);
      if (o.id == id) {
        return o;
      }

      if (o.id < id) {
        low = (mid + 1);
      } else {
        high = (mid - 1);
      }
    }

    return null;// object not found
  }

  /**
   * The comparator class
   *
   * @author Thomas Weise
   */
  private static final class Comp implements Comparator<InputObject> {

    /**
     * Create
     */
    Comp() {
      super();
    }

    /**
     * {@inheritDoc}
     */
    public final int compare(final InputObject o1, final InputObject o2) {
      if (o1.id < o2.id) {
        return (-1);
      }
      if (o1.id > o2.id) {
        return 1;
      }
      return 0;
    }

  }
}