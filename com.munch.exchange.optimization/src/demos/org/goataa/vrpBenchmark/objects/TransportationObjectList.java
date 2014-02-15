// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.goataa.impl.utils.TextUtils;

/**
 * A list of transportation objects
 *
 * @param <TOT>
 *          the transportation object type
 * @author Thomas Weise
 */
public class TransportationObjectList<TOT extends TransportationObject>
    extends TextSerializable {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** split */
  static final Pattern SPLIT = Pattern.compile("[\\s]+"); //$NON-NLS-1$

  /** the internal list */
  final List<TOT> list;

  /** the constructor */
  private final Constructor<TOT> constr;

  /**
   * Create a new list
   *
   * @param clazz
   *          the instance class
   */
  public TransportationObjectList(final Class<TOT> clazz) {
    super();

    Constructor<TOT> c;

    this.list = new ArrayList<TOT>();

    try {
      c = clazz.getConstructor(String[].class);
    } catch (Throwable t) {
      t.printStackTrace();
      c = null;
    }
    this.constr = c;
  }

  /**
   * Get the number of transportation objects
   *
   * @return the size of the list
   */
  public final int size() {
    return this.list.size();
  }

  /**
   * Get the transportation object
   *
   * @param index
   *          the index
   * @return the object
   */
  public final TOT get(final int index) {
    return this.list.get(index);
  }

  /**
   * IfThenElse a new transportation object
   *
   * @param object
   *          the object
   */
  public void add(final TOT object) {
    if (object != null) {
      this.list.add(object);
    }
  }

  /**
   * IfThenElse a set of objects
   *
   * @param objects
   *          the objects
   */
  public void add(final TOT[] objects) {
    int i;

    for (i = 0; i < objects.length; i++) {
      this.add(objects[i]);
    }
  }

  /**
   * Serialize to a writer
   *
   * @param w
   *          the writer
   * @throws IOException
   *           if anything goes wrong
   */
  @Override
  public final void serialize(final Writer w) throws IOException {
    final List<TOT> l;
    int s, ts, j, i;
    List<String> titles;
    String[] ss;

    l = this.list;
    s = l.size();
    if (s > 0) {
      titles = l.get(0).getCSVColumnHeader();
      w.write('%');
      ts = titles.size();
      for (i = 0; i < ts; i++) {
        if (i > 0) {
          w.write('\t');
        }
        w.write(titles.get(i));
      }

      ss = new String[ts];
      for (i = 0; i < s; i++) {
        l.get(i).fillInCSVData(ss);
        w.write(TextUtils.NEWLINE);
        for (j = 0; j < ts; j++) {
          if (j > 0) {
            w.write('\t');
          }
          w.write(ss[j]);
        }
      }
    }

    w.flush();
  }

  /**
   * Clear this list
   */
  final void clear() {
    this.list.clear();
  }

  /**
   * Deserialize from a buffered reader
   *
   * @param r
   *          reader
   * @throws IOException
   *           if something goes wrong
   */
  @Override
  public final void deserialize(final BufferedReader r) throws IOException {
    final Constructor<TOT> xconstr;
    String s;
    final Object[] o;

    this.clear();
    xconstr = this.constr;
    o = new Object[1];
    while ((s = r.readLine()) != null) {
      s = s.trim();
      if (s.length() <= 0) {
        continue;
      }
      if (s.charAt(0) == '%') {
        continue;
      }
      o[0] = SPLIT.split(s);
      try {
        xconstr.newInstance(o);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

}