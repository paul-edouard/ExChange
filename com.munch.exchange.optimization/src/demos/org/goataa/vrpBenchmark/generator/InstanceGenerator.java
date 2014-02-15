// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.generator;

import java.io.File;

import demos.org.goataa.vrpBenchmark.instances.InstanceLoader;
import demos.org.goataa.vrpBenchmark.objects.TextSerializable;
import demos.org.goataa.vrpBenchmark.objects.TransportationObjectList;
import demos.org.goataa.vrpBenchmark.optimization.Compute;
import demos.org.goataa.vrpBenchmark.optimization.Move;

/**
 * Generate the test instances for the transportation problem
 *
 * @author Thomas Weise
 */
public class InstanceGenerator {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  public static final void main(final String[] args) {
    ProblemGenerator pg;
    File baseDir;

    InstanceLoader.setupInstancesDir(args);
    baseDir = InstanceLoader.getInstancesDir();

    // simple problem: 20 locations, 10 orders, and 10 additional
    // trucks, tours,drivers,and containers
    TextSerializable.clearAll();
    pg = new ProblemGenerator(1);
    pg.createLocations(10000, 10000, 20);
    pg.createDistances();
    pg.createTours(10, 10);
    pg.createTrucks(10);
    pg.createDrivers(10);
    pg.createContainers(10);
    serialize(InstanceLoader.CASE_01, baseDir, pg);

    // larger problem: 50 locations, 30 orders, and 20 additional
    // trucks, tours,drivers,and containers
    TextSerializable.clearAll();
    pg = new ProblemGenerator(2);
    pg.createLocations(15000, 15000, 50);
    pg.createDistances();
    pg.createTours(30, 10);
    pg.createTrucks(20);
    pg.createDrivers(20);
    pg.createContainers(20);
    serialize(InstanceLoader.CASE_02, baseDir, pg);

    // yet larger problem: 100 locations, 100 orders, and 30 additional
    // trucks, tours,drivers,and containers
    TextSerializable.clearAll();
    pg = new ProblemGenerator(3);
    pg.createLocations(30000, 30000, 100);
    pg.createDistances();
    pg.createTours(100, 10);
    pg.createTrucks(30);
    pg.createDrivers(30);
    pg.createContainers(30);
    serialize(InstanceLoader.CASE_03, baseDir, pg);

    // yet larger problem: 150 locations, 300 orders, and 40 additional
    // trucks, tours,drivers,and containers
    TextSerializable.clearAll();
    pg = new ProblemGenerator(4);
    pg.createLocations(50000, 50000, 150);
    pg.createDistances();
    pg.createTours(200, 10);
    pg.createTrucks(40);
    pg.createDrivers(40);
    pg.createContainers(40);
    serialize(InstanceLoader.CASE_04, baseDir, pg);

    // really large problem: 200 locations, 500 orders, and 50 additional
    // trucks, tours,drivers,and containers
    TextSerializable.clearAll();
    pg = new ProblemGenerator(5);
    pg.createLocations(50000, 50000, 200);
    pg.createDistances();
    pg.createTours(500, 10);
    pg.createTrucks(50);
    pg.createDrivers(50);
    pg.createContainers(50);
    serialize(InstanceLoader.CASE_05, baseDir, pg);

    // really large problem: 300 locations, 1000 orders, and 25 additional
    // trucks, tours,drivers,and containers
    TextSerializable.clearAll();
    pg = new ProblemGenerator(3);
    pg.createLocations(70000, 70000, 300);
    pg.createDistances();
    pg.createTours(1000, 10);
    pg.createTrucks(25);
    pg.createDrivers(25);
    pg.createContainers(25);
    serialize(InstanceLoader.CASE_06, baseDir, pg);
  }

  /**
   * Serialize
   *
   * @param f
   *          the case
   * @param baseDir
   *          the base dir
   * @param pg
   *          the problem generator
   */
  private static final void serialize(final String f, final File baseDir,
      final ProblemGenerator pg) {
    File dir;
    Compute c;
    TransportationObjectList<Move> l;

    dir = new File(baseDir, f);
    dir.mkdirs();

    TextSerializable.serializeAll(dir);

    l = pg.getSolution();
    l.serialize(new File(dir, "solution.txt"));//$NON-NLS-1$

    c = new Compute();
    c.init();
    c.move(l);
    c.finish();
    c.serialize(new File(dir, "result.txt"));//$NON-NLS-1$
  }

}