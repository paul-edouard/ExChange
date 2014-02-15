// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.ints.prime;

import org.goataa.impl.algorithms.ea.SimpleGenerationalEA;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.searchOperations.trees.binary.TreeRecombinationRed;
import org.goataa.impl.searchOperations.trees.nullary.TreeRampedHalfAndHalfRed;
import org.goataa.impl.searchOperations.trees.unary.TreeMutatorRed;
import org.goataa.impl.searchSpaces.trees.NodeTypeSet;
import org.goataa.impl.searchSpaces.trees.ReflectionNodeType;
import org.goataa.impl.searchSpaces.trees.math.ints.IntFunction;
import org.goataa.impl.searchSpaces.trees.math.ints.SimpleContext;
import org.goataa.impl.searchSpaces.trees.math.ints.arith.Add;
import org.goataa.impl.searchSpaces.trees.math.ints.arith.Mod;
import org.goataa.impl.searchSpaces.trees.math.ints.arith.Mul;
import org.goataa.impl.searchSpaces.trees.math.ints.arith.Sub;
import org.goataa.impl.searchSpaces.trees.math.ints.basic.ConstantType;
import org.goataa.impl.searchSpaces.trees.math.ints.basic.VariableType;
import org.goataa.impl.searchSpaces.trees.math.ints.basic.WriteType;
import org.goataa.impl.searchSpaces.trees.math.ints.ctrl.Glue;
import org.goataa.impl.searchSpaces.trees.math.ints.ctrl.WhileLoop;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IBinarySearchOperation;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.IUnarySearchOperation;

/**
 * An example program which tries to find a formula which generates prime
 * numbers.
 *
 * @author Thomas Weise
 */
public class PrimeTest {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final IObjectiveFunction<IntFunction> f;
    final INullarySearchOperation<IntFunction> create;
    final IUnarySearchOperation<IntFunction> mutate;
    final IBinarySearchOperation<IntFunction> crossover;
    final NodeTypeSet<IntFunction> nts;
    final NodeTypeSet<IntFunction>[] binary, unary;
    final SimpleGenerationalEA<IntFunction, IntFunction> EA;
    int i, k;
    Individual<IntFunction, IntFunction> ind;
    SimpleContext cx;

    cx = new SimpleContext(10000, 3);
    f = new PrimeObjective2(cx);

    nts = new NodeTypeSet<IntFunction>();
    binary = new NodeTypeSet[] { nts, nts };
    unary = new NodeTypeSet[] { nts };
    nts.add(new VariableType(cx.getMemorySize()));
    nts.add(new WriteType(cx.getMemorySize(), unary));
    nts.add(ConstantType.DEFAULT_CONSTANT_TYPE);
    nts.add(new ReflectionNodeType<Add, IntFunction>(Add.class, binary));
    nts.add(new ReflectionNodeType<Sub, IntFunction>(Sub.class, binary));
    nts.add(new ReflectionNodeType<Mul, IntFunction>(Mul.class, binary));
    nts.add(new ReflectionNodeType<Mod, IntFunction>(Mod.class, binary));
    nts.add(new ReflectionNodeType<Glue, IntFunction>(Glue.class, binary));
    nts.add(new ReflectionNodeType<WhileLoop, IntFunction>(
        WhileLoop.class, binary));

    create = new TreeRampedHalfAndHalfRed<IntFunction>(nts, 15);
    mutate = ((IUnarySearchOperation) (new TreeMutatorRed(nts, 15)));
    crossover = ((IBinarySearchOperation) (new TreeRecombinationRed(nts,
        15)));

    EA = new SimpleGenerationalEA<IntFunction, IntFunction>();
    EA.setCrossoverRate(0.5);
    EA.setMutationRate(0.5);
    EA.setNullarySearchOperation(create);
    EA.setUnarySearchOperation(mutate);
    EA.setBinarySearchOperation(crossover);
    EA.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    EA.setSelectionAlgorithm(new TournamentSelection(3));
    EA.setPopulationSize(1000);
    EA.setTerminationCriterion(new StepLimit(//
        EA.getPopulationSize() * 1000));
    EA.setMatingPoolSize(EA.getPopulationSize());
    EA.setObjectiveFunction(f);

    ind = EA.call().get(0);

    System.out.println(ind.v);
    System.out.println(ind.x.toString());
    System.out.println(ind.x.getWeight() + " " + ind.x.getHeight()); //$NON-NLS-1$
    System.out.println();

    System.out.println("f(p)   =" + ind.v);//$NON-NLS-1$
    System.out.println("p(x[0])=" + ind.x.toString());//$NON-NLS-1$
    System.out.println("i\tp(x[0])");//$NON-NLS-1$

    k = 100;
    for (i = 1; i < k; i++) {
      cx.beginProgram();
      cx.write(0, i);
      ind.x.compute(cx);
      System.out.println(i + "\t" + cx.read(cx.getMemorySize() - 1)); //$NON-NLS-1$
      cx.endProgram();
    }
  }
}