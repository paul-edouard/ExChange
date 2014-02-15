// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.image.reprod;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.goataa.impl.algorithms.ea.SimpleGenerationalMOEA;
import org.goataa.impl.algorithms.ea.fitnessAssignment.ParetoRankingEq;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.comparators.Pareto;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.objectiveFunctions.TreeSizeObjective;
import org.goataa.impl.searchOperations.trees.binary.TreeRecombination;
import org.goataa.impl.searchOperations.trees.nullary.TreeRampedHalfAndHalf;
import org.goataa.impl.searchOperations.trees.unary.TreeMutator;
import org.goataa.impl.searchSpaces.trees.NodeTypeSet;
import org.goataa.impl.searchSpaces.trees.ReflectionNodeType;
import org.goataa.impl.searchSpaces.trees.image.GraphicsContext;
import org.goataa.impl.searchSpaces.trees.image.ImageUtils;
import org.goataa.impl.searchSpaces.trees.image.RecursiveContext;
import org.goataa.impl.searchSpaces.trees.image.recurse.CallType;
import org.goataa.impl.searchSpaces.trees.image.recurse.Push;
import org.goataa.impl.searchSpaces.trees.image.shapes.Ellipse;
import org.goataa.impl.searchSpaces.trees.image.shapes.Rectangle;
import org.goataa.impl.searchSpaces.trees.image.transforms.FractionDiv;
import org.goataa.impl.searchSpaces.trees.image.transforms.GridDivision;
import org.goataa.impl.searchSpaces.trees.image.transforms.Movement;
import org.goataa.impl.searchSpaces.trees.image.transforms.Rotation;
import org.goataa.impl.searchSpaces.trees.image.transforms.Scale;
import org.goataa.impl.searchSpaces.trees.image.transforms.Shearing;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Add;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Div;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Exp;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Log;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Mul;
import org.goataa.impl.searchSpaces.trees.math.real.arith.Sub;
import org.goataa.impl.searchSpaces.trees.math.real.basic.ConstantType;
import org.goataa.impl.searchSpaces.trees.math.real.basic.VariableType;
import org.goataa.impl.searchSpaces.trees.math.real.basic.WriteType;
import org.goataa.impl.searchSpaces.trees.math.real.trig.ASin;
import org.goataa.impl.searchSpaces.trees.math.real.trig.Sin;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.MOIndividual;
import org.goataa.spec.IBinarySearchOperation;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.IUnarySearchOperation;

/**
 * An example test program for Symbolic Regression as discussed in Section
 * 49.1.
 *
 * @author Thomas Weise
 */
public class ImageTestMO extends
    SimpleGenerationalMOEA<RealFunction, RealFunction> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the directory */
  public static final File DIR = new File("e:\\test\\");//$NON-NLS-1$;

  /** the source image */
  public static final BufferedImage SOURCE = //
  ImageUtils.load(new File(DIR, "input.png").toString()); //$NON-NLS-1$;

  /** the context */
  public static final GraphicsContext CTX = new RecursiveContext(//
      SOURCE.getWidth() * SOURCE.getHeight() * 100,//
      30,// the maximum call depth
      2,// give two additional variables
      SOURCE.getWidth(), SOURCE.getHeight());

  /** the image prefix */
  public static final String IMAGE_PREFIX = "resimg"; //$NON-NLS-1$

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
    final NodeTypeSet<RealFunction>[] ternary, binary, unary; // ,quin;
    final ImageTestMO EA;
    MOIndividual<RealFunction, RealFunction> ind;
    List<MOIndividual<RealFunction, RealFunction>> l;
    int i;

    f = new IObjectiveFunction[] { new ImageSimilarity(SOURCE, CTX),
        TreeSizeObjective.TREE_SIZE_OBJECTIVE };

    nts = new NodeTypeSet<RealFunction>();
    ternary = new NodeTypeSet[] { nts, nts, nts };
    // quin = new NodeTypeSet[] { nts, nts, nts, nts, nts };
    binary = new NodeTypeSet[] { nts, nts };
    unary = new NodeTypeSet[] { nts };
    nts.add(new VariableType(CTX.getMemorySize()));
    nts.add(new WriteType(CTX.getMemorySize(), unary));
    nts.add(ConstantType.DEFAULT_CONSTANT_TYPE);
    nts.add(new ReflectionNodeType<Add, RealFunction>(Add.class, binary));
    nts.add(new ReflectionNodeType<Sub, RealFunction>(Sub.class, binary));
    nts.add(new ReflectionNodeType<Mul, RealFunction>(Mul.class, binary));
    nts.add(new ReflectionNodeType<Div, RealFunction>(Div.class, binary));
    nts.add(new ReflectionNodeType<Exp, RealFunction>(Exp.class, unary));
    nts.add(new ReflectionNodeType<Log, RealFunction>(Log.class, unary));
    nts.add(new ReflectionNodeType<Sin, RealFunction>(Sin.class, unary));
    nts.add(new ReflectionNodeType<ASin, RealFunction>(ASin.class, unary));
    nts.add(new ReflectionNodeType<Ellipse, RealFunction>(//
        Ellipse.class, binary));
    nts.add(new ReflectionNodeType<Rectangle, RealFunction>(//
        Rectangle.class, binary));
    nts.add(new ReflectionNodeType<FractionDiv, RealFunction>(//
        FractionDiv.class, ternary));
    nts.add(new ReflectionNodeType<Shearing, RealFunction>(//
        Shearing.class, ternary));
    nts.add(new ReflectionNodeType<Rotation, RealFunction>(//
        Rotation.class, binary));
    nts.add(new ReflectionNodeType<Scale, RealFunction>(//
        Scale.class, ternary));
    nts.add(new ReflectionNodeType<Movement, RealFunction>(//
        Movement.class, ternary));
    nts.add(new ReflectionNodeType<GridDivision, RealFunction>(//
        GridDivision.class, binary));
    // nts.add(new ReflectionNodeType<Placement, RealFunction>(//
    // Placement.class, quin));

    nts.add(new ReflectionNodeType<Push, RealFunction>(//
        Push.class, binary));
    nts.add(CallType.DEFAULT_CALL_TYPE);

    create = new TreeRampedHalfAndHalf<RealFunction>(nts, 15);
    mutate = ((IUnarySearchOperation) (new TreeMutator(15)));
    crossover = ((IBinarySearchOperation) (new TreeRecombination(15)));

    EA = new ImageTestMO();
    EA.setNullarySearchOperation(create);
    EA.setUnarySearchOperation(mutate);
    EA.setBinarySearchOperation(crossover);
    EA.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    EA.setTerminationCriterion(new StepLimit(100));
    EA.setObjectiveFunctions(f);
    EA.setCrossoverRate(0.5);
    EA.setMutationRate(0.5);
    EA.setComparator(Pareto.PARETO_COMPARATOR);
    EA.setFitnesAssignmentProcess(ParetoRankingEq.PARETO_RANKING_EQ);
    EA.setSelectionAlgorithm(new TournamentSelection(3));
    EA.setPopulationSize(1024);
    EA.setMatingPoolSize(EA.getPopulationSize());
    EA.setMaxSolutions(100);

    l = EA.call();
    for (i = l.size(); (--i) >= 0;) {
      ind = l.get(i);

      System.out.println("f(p)=(" + ind.f[0] + //$NON-NLS-1$
          ", " + ind.f[1] + ")");//$NON-NLS-1$//$NON-NLS-2$
      System.out.println("p(x)=" + ind.x.toString()); //$NON-NLS-1$
    }

  }

}