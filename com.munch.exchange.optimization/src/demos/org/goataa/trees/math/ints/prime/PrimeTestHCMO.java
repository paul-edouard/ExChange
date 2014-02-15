// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.ints.prime;

import java.util.Collections;
import java.util.List;

import org.goataa.impl.algorithms.hc.MOHillClimbing;
import org.goataa.impl.comparators.Lexicographic;
import org.goataa.impl.comparators.Pareto;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.objectiveFunctions.TreeSizeObjective;
import org.goataa.impl.searchOperations.MultiMutator;
import org.goataa.impl.searchOperations.trees.nullary.TreeRampedHalfAndHalfRed;
import org.goataa.impl.searchOperations.trees.unary.TreeMutatorRed;
import org.goataa.impl.searchOperations.trees.unary.TreeWrapperRed;
import org.goataa.impl.searchSpaces.trees.NodeTypeSet;
import org.goataa.impl.searchSpaces.trees.ReflectionNodeType;
import org.goataa.impl.searchSpaces.trees.math.ints.Context;
import org.goataa.impl.searchSpaces.trees.math.ints.IntFunction;
import org.goataa.impl.searchSpaces.trees.math.ints.TransactContext;
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
import org.goataa.impl.utils.MOIndividual;
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
public class PrimeTestHCMO {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final IObjectiveFunction<IntFunction>[] f;
    final INullarySearchOperation<IntFunction> create;
    final IUnarySearchOperation<IntFunction> mutate;
    final NodeTypeSet<IntFunction> nts;
    final NodeTypeSet<IntFunction>[] binary, unary;
    final MOHillClimbing<IntFunction, IntFunction> HC;
    int i, j, k;
    MOIndividual<IntFunction, IntFunction> ind;
    List<MOIndividual<IntFunction, IntFunction>> l;
    Context cx;

    cx = new TransactContext(100000, 3, 500);
    // cx = new SimpleContext(100000, 3);
    f = new IObjectiveFunction[] { new PrimeObjective1(cx),
        TreeSizeObjective.TREE_SIZE_OBJECTIVE };

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
    // nts.add(new ReflectionNodeType<Div, IntFunction>(Div.class,
    // binary));
    nts.add(new ReflectionNodeType<Glue, IntFunction>(Glue.class, binary));
    nts.add(new ReflectionNodeType<WhileLoop, IntFunction>(
        WhileLoop.class, binary));

    create = new TreeRampedHalfAndHalfRed<IntFunction>(nts, 15);
    mutate = new MultiMutator(//
        new TreeMutatorRed(nts, 15),//
        new TreeWrapperRed(nts, 15));//

    HC = new MOHillClimbing<IntFunction, IntFunction>();
    HC.setComparator(Pareto.PARETO_COMPARATOR);// Pareto.PARETO_COMPARATOR);
    HC.setNullarySearchOperation(create);
    HC.setUnarySearchOperation(mutate);
    HC.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    HC.setTerminationCriterion(new StepLimit(//
        1000 * 1000));
    HC.setObjectiveFunctions(f);

    l = HC.call();
    Collections.sort(l, Lexicographic.LEXICOGRAPHIC_COMPARATOR);

    for (j = 0; j < l.size(); j++) {
      System.out.println();
      System.out.println();
      ind = l.get(j);

      System.out.println("f(p)   =(" + ind.f[0] + //$NON-NLS-1$
          ", " + ind.f[1] + ")");//$NON-NLS-1$//$NON-NLS-2$
      System.out.println("p(x[0])=" + ind.x.toString());//$NON-NLS-1$
      System.out.println("i\tp(x[0])");//$NON-NLS-1$

      k = ((int) (-ind.f[0] - 0.5d));
      for (i = 1; i <= k; i++) {
        cx.beginProgram();
        cx.write(0, i);
        cx.commit();
        ind.x.compute(cx);
        cx.endProgram();
        System.out.println(i + "\t" + cx.read(cx.getMemorySize() - 1)); //$NON-NLS-1$

      }
    }
  }
}