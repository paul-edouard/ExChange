// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.vrpBenchmark.example.orderSeq;

import java.io.OutputStreamWriter;
import java.util.Random;

import org.goataa.impl.algorithms.sa.SimulatedAnnealing;
import org.goataa.impl.algorithms.sa.temperatureSchedules.Exponential;
import org.goataa.impl.searchOperations.strings.permutation.nullary.IntPermutationUniformCreation;
import org.goataa.impl.searchOperations.strings.permutation.unary.IntPermutationMultiSwapMutation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ITerminationCriterion;
import org.goataa.spec.IUnarySearchOperation;

import demos.org.goataa.vrpBenchmark.example.moveBased.SimpleObjective;
import demos.org.goataa.vrpBenchmark.instances.InstanceLoader;
import demos.org.goataa.vrpBenchmark.objects.Order;
import demos.org.goataa.vrpBenchmark.objects.TransportationObjectList;
import demos.org.goataa.vrpBenchmark.optimization.Compute;
import demos.org.goataa.vrpBenchmark.optimization.Move;

/**
 * A primitive test for the non-functional VRP benchmark solution approach.
 *
 * @author Thomas Weise
 */
public class Test {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  public static final void main(final String[] args) {
    final IObjectiveFunction<Move[]> f;
    final INullarySearchOperation<int[]> create;
    final IUnarySearchOperation<int[]> mutate;
    final IGPM<int[], Move[]> gpm;
    ITerminationCriterion term;
    Move[] res;
    TransportationObjectList<Move> list;
    Compute c;
    int len, add;

    InstanceLoader.setupInstancesDir(args);
    InstanceLoader.loadInstance(InstanceLoader.CASE_01);

    f = new SimpleObjective();

    len = Order.ORDERS.size();
    add = Math.max((len * 5) >>> 2, 9);
    create = new IntPermutationUniformCreation(len + add);
    mutate = IntPermutationMultiSwapMutation.INT_PERMUTATION_MULTI_SWAP_MUTATION;
    gpm = new OrderSeqGPM();
    term = new StepLimit(100000);

    res = SimulatedAnnealing.simulatedAnnealing(f, create, mutate, gpm,
        new Exponential(3d, 0.01d), term, new Random()).x;

    list = new TransportationObjectList<Move>(Move.class);
    list.add(res);

    c = new Compute();
    c.init();
    c.move(list);
    c.finish();

    System.out.println(c);
    System.out.println();
    try {
      list.serialize(new OutputStreamWriter(System.out));
    } catch (Throwable tt) {
      tt.printStackTrace();
    }
    System.out.println();
    System.out.println();
    System.out.println(f.compute(res, new Random()));

  }
}