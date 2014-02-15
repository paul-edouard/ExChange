// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.instances;

import java.io.File;
import java.net.URL;

import demos.org.goataa.vrpBenchmark.objects.TextSerializable;

/**
 * The instance loader
 *
 * @author Thomas Weise
 */
public class InstanceLoader {

  /** the instances directory */
  private static File INSTANCES_DIR = new File("."); //$NON-NLS-1$

  /** case 01 */
  public static final String CASE_01 = "case01"; //$NON-NLS-1$

  /** case 02 */
  public static final String CASE_02 = "case02"; //$NON-NLS-1$

  /** case 03 */
  public static final String CASE_03 = "case03"; //$NON-NLS-1$

  /** case 04 */
  public static final String CASE_04 = "case04"; //$NON-NLS-1$

  /** case 05 */
  public static final String CASE_05 = "case05"; //$NON-NLS-1$

  /** case 06 */
  public static final String CASE_06 = "case06"; //$NON-NLS-1$

  /**
   * Obtain the instance directory
   *
   * @return the instance directory
   */
  public static final File getInstancesDir() {
    return INSTANCES_DIR;
  }

  /**
   * Load an instance
   *
   * @param caseName
   *          the name of the instance
   */
  public static final void loadInstance(final String caseName) {
    TextSerializable.deserializeAll(new File(INSTANCES_DIR, caseName));
  }

  /**
   * Set the instances directory
   *
   * @param args
   *          the command line arguments
   */
  public static final void setupInstancesDir(final String[] args) {
    URL u;
    String s;
    int i;

    if (args != null) {
      if (args.length > 0) {
        try {
          INSTANCES_DIR = new File(args[0]);
          INSTANCES_DIR = INSTANCES_DIR.getCanonicalFile();
          return;
        } catch (Throwable t) {
          //
        }
      }
    }

    try {
      s = InstanceLoader.class.getSimpleName() + ".class"; //$NON-NLS-1$
      u = InstanceLoader.class.getResource(s);
      s = null;
      if (u != null) {
        if ("file".equalsIgnoreCase(u.getProtocol())) { //$NON-NLS-1$
          s = u.getPath();
          if (s != null) {
            s = s.replace("/bin/", "/src/"); //$NON-NLS-1$ //$NON-NLS-2$
          }
        }
      }

      if (s == null) {
        s = InstanceLoader.class.getSimpleName() + ".java"; //$NON-NLS-1$
        u = InstanceLoader.class.getResource(s);
        s = null;
        if (u != null) {
          if ("file".equalsIgnoreCase(u.getProtocol())) { //$NON-NLS-1$
            s = u.getPath();
          }
        }
      }

      if (s != null) {
        i = s.lastIndexOf('/');
        if (i > 0) {
          s = s.substring(1, i);
        } else {
          s = s.substring(1);
        }
        if (File.separatorChar != '/') {
          s = s.replace('/', File.separatorChar);
        }
        INSTANCES_DIR = new File(s);
        INSTANCES_DIR = INSTANCES_DIR.getCanonicalFile();
        return;
      }
    } catch (Throwable t) {
      //
    }

    try {
      INSTANCES_DIR = new File(".");//$NON-NLS-1$
      INSTANCES_DIR = INSTANCES_DIR.getCanonicalFile();
      return;
    } catch (Throwable t) {//
    }
  }

}