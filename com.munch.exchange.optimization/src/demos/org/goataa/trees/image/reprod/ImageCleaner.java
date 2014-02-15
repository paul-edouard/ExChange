// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.image.reprod;

import java.io.File;
import java.util.regex.Pattern;

/**
 *An image cleaner
 *
 * @author Thomas Weise
 */
public class ImageCleaner {

  /** compile the pattern */
  private static final Pattern P = Pattern.compile("_"); //$NON-NLS-1$

  /**
   * Clean all images
   */
  static final void clean() {

    File[] l;
    int i, j, r, s;
    try {
      l = ImageTestMO.DIR.listFiles();

      s = l.length;
      outer: for (i = s; (--i) > 0;) {
        for (j = i; (--j) >= 0;) {
          r = compare(l[i], l[j]);
          if (r < 0) {
            try {
              l[j].delete();
            } catch (Throwable t) {/**/
              return;
            }
            l[j] = l[--s];
          } else {
            if (r > 0) {
              try {
                l[i].delete();
              } catch (Throwable t) {/**/
                return;
              }
              continue outer;
            }
          }
        }
      }
    } catch (Throwable tt) {
      return;
    }
  }

  /**
   * Compare two files
   *
   * @param a
   *          the first file
   * @param b
   *          the second file
   * @return the comparison result
   */
  private static final int compare(final File a, final File b) {
    String n1, n2;
    String[] ns1, ns2;
    double f, g, h, i;
    int r, v, w;

    n1 = a.getName();
    n2 = b.getName();

    if (!(n1.startsWith(ImageTestMO.IMAGE_PREFIX))) {
      return 0;
    }
    if (!(n2.startsWith(ImageTestMO.IMAGE_PREFIX))) {
      return 0;
    }

    ns1 = P.split(n1);
    if ((ns1 == null) || (ns1.length != 4)) {
      return 0;
    }

    ns2 = P.split(n2);
    if ((ns2 == null) || (ns2.length != 4)) {
      return 0;
    }

    try {
      f = Double.parseDouble(ns1[1]);
    } catch (Throwable t) {
      return 0;
    }

    try {
      g = Double.parseDouble(ns1[2]);
    } catch (Throwable t) {
      return 0;
    }

    try {
      h = Double.parseDouble(ns2[1]);
    } catch (Throwable t) {
      return 0;
    }

    try {
      i = Double.parseDouble(ns2[2]);
    } catch (Throwable t) {
      return 0;
    }

    v = Double.compare(f, h);
    w = Double.compare(g, i);

    r = ((v < 0) ? (-1) : ((v > 0) ? 1 : 0));

    if (r < 0) {
      if (w <= 0) {
        return -1;
      }
      return 0;
    }

    if (r > 0) {
      if (w >= 0) {
        return 1;
      }
      return 0;
    }

    return w;
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  public static final void main(final String[] args) {
    clean();
  }
}