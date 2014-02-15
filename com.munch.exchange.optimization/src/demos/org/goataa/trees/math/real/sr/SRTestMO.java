// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.math.real.sr;

import java.util.List;

import org.goataa.impl.algorithms.ea.SimpleGenerationalMOEA;
import org.goataa.impl.algorithms.ea.fitnessAssignment.ParetoRanking;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.comparators.Pareto;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.objectiveFunctions.TreeSizeObjective;
import org.goataa.impl.searchOperations.trees.binary.TreeRecombination;
import org.goataa.impl.searchOperations.trees.nullary.TreeRampedHalfAndHalf;
import org.goataa.impl.searchOperations.trees.unary.TreeMutator;
import org.goataa.impl.searchSpaces.trees.NodeTypeSet;
import org.goataa.impl.searchSpaces.trees.ReflectionNodeType;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Abs;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Add;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Div;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Exp;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Mul;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Pow;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Sqrt;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Sub;
import org.goataa.impl.searchSpaces.trees.math.real.basic.ConstantType;
import org.goataa.impl.searchSpaces.trees.math.real.basic.VariableType;
import org.goataa.impl.searchSpaces.trees.math.real.trig.Sin;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.MOIndividual;
import org.goataa.spec.IBinarySearchOperation;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.IUnarySearchOperation;

/**
 * An example test program for Symbolic Regression as discussed in
 * Section 49.1.
 *
 * @author Thomas Weise
 */
public class SRTestMO extends Examples {

  /** the training cases */
  static final TrainingCase[] DATA = createTrainingCases(F1, 250);

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final IObjectiveFunction<RealFunction>[] f;
    final INullarySearchOperation<RealFunction> create;
    final IUnarySearchOperation<RealFunction> mutate;
    final IBinarySearchOperation<RealFunction> crossover;
    final NodeTypeSet<RealFunction> nts;
    final NodeTypeSet<RealFunction>[] binary, unary;
    final SimpleGenerationalMOEA<RealFunction, RealFunction> EA;
    MOIndividual<RealFunction, RealFunction> ind;
    List<MOIndividual<RealFunction, RealFunction>> l;
    int i;

    f = new IObjectiveFunction[] { new SRObjectiveFunction1(DATA),
        TreeSizeObjective.TREE_SIZE_OBJECTIVE };

    nts = new NodeTypeSet<RealFunction>();
    binary = new NodeTypeSet[] { nts, nts };
    unary = new NodeTypeSet[] { nts };
    nts.add(new VariableType(DATA[0].data.length));
    nts.add(ConstantType.DEFAULT_CONSTANT_TYPE);
    nts.add(new ReflectionNodeType<Add, RealFunction>(Add.class, binary));
    nts.add(new ReflectionNodeType<Sub, RealFunction>(Sub.class, binary));
    nts.add(new ReflectionNodeType<Mul, RealFunction>(Mul.class, binary));
    nts.add(new ReflectionNodeType<Div, RealFunction>(Div.class, binary));
    nts.add(new ReflectionNodeType<Pow, RealFunction>(Pow.class, binary));
    nts.add(new ReflectionNodeType<Exp, RealFunction>(Exp.class, unary));
    nts.add(new ReflectionNodeType<Abs, RealFunction>(Abs.class, unary));
    nts.add(new ReflectionNodeType<Sin, RealFunction>(Sin.class, unary));
    nts.add(new ReflectionNodeType<Sqrt, RealFunction>(Sqrt.class, unary));

    create = new TreeRampedHalfAndHalf<RealFunction>(nts, 15);
    mutate = ((IUnarySearchOperation) (new TreeMutator(15)));
    crossover = ((IBinarySearchOperation) (new TreeRecombination(15)));

    EA = new SimpleGenerationalMOEA<RealFunction, RealFunction>();
    EA.setCrossoverRate(0.5);
    EA.setMutationRate(0.5);
    EA.setComparator(Pareto.PARETO_COMPARATOR);
    EA.setFitnesAssignmentProcess(ParetoRanking.PARETO_RANKING);
    EA.setNullarySearchOperation(create);
    EA.setUnarySearchOperation(mutate);
    EA.setBinarySearchOperation(crossover);
    EA.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    EA.setSelectionAlgorithm(new TournamentSelection(2));
    EA.setTerminationCriterion(new StepLimit(1000000));
    EA.setPopulationSize(4096);
    EA.setMatingPoolSize(2048);
    EA.setObjectiveFunctions(f);

    l = EA.call();

    for (i = l.size(); (--i) >= 0;) {
      ind = l.get(i);
      System.out.println("f(p)=(" + ind.f[0] + //$NON-NLS-1$
          ", " + ind.f[1] + ")");//$NON-NLS-1$//$NON-NLS-2$
      System.out.println("p(x)=" + ind.x.toString()); //$NON-NLS-1$
      System.out.println();
    }

  }
}