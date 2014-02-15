// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.image.syn;

import javax.swing.JFrame;

import org.goataa.impl.algorithms.ea.SimpleGenerationalMOEA;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.comparators.Pareto;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.objectiveFunctions.TreeSizeObjective;
import org.goataa.impl.searchOperations.trees.binary.TreeRecombinationRed;
import org.goataa.impl.searchOperations.trees.nullary.TreeRampedHalfAndHalfRed;
import org.goataa.impl.searchOperations.trees.unary.TreeMutatorRed;
import org.goataa.impl.searchSpaces.trees.NodeTypeSet;
import org.goataa.impl.searchSpaces.trees.ReflectionNodeType;
import org.goataa.impl.searchSpaces.trees.image.ImageUtils;
import org.goataa.impl.searchSpaces.trees.image.pixelOps.RasterFilterType;
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
import org.goataa.impl.searchSpaces.trees.math.real.ctrl.IfThenElse;
import org.goataa.impl.searchSpaces.trees.math.real.trig.ASin;
import org.goataa.impl.searchSpaces.trees.math.real.trig.Sin;
import org.goataa.impl.termination.TerminationCriterion;
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
public class HBImageEvolution extends JFrame {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the image size */
  private static final int SIZE = 250;

  /** the max depth */
  private static final int MAX_DEPTH = 20;

  /** the steps */
  private static final int STEPS = Integer.MAX_VALUE;// SIZE * SIZE * 300;

  /** the additional memory cells */
  private static final int ADD_MEM = 1;

  /** the hb-fa */
  private final HBFA hbfa;

  /**
   * Create the hb image frame
   */
  public HBImageEvolution() {
    super("Human-based Image Evolution"); //$NON-NLS-1$
    this.hbfa = new HBFA(SIZE, MAX_DEPTH, ADD_MEM, STEPS);
    this.setContentPane(this.hbfa);
    this.pack();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

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
    final NodeTypeSet<RealFunction>[] ternary, binary, unary;
    final SimpleGenerationalMOEA<RealFunction, RealFunction> EA;
    final HBImageEvolution HBEF;

    f = new IObjectiveFunction[] { TreeSizeObjective.TREE_SIZE_OBJECTIVE };

    HBEF = new HBImageEvolution();
    HBEF.pack();
    HBEF.setVisible(true);

    nts = new NodeTypeSet<RealFunction>();
    ternary = new NodeTypeSet[] { nts, nts, nts };
    binary = new NodeTypeSet[] { nts, nts };
    unary = new NodeTypeSet[] { nts };
    nts.add(new VariableType(ADD_MEM + ImageUtils.RESERVED_MEMORY_CELLS));
    nts.add(new WriteType(ADD_MEM + ImageUtils.RESERVED_MEMORY_CELLS,
        unary));
    // nts.add(new RasterFilterType(ADD_MEM +
    // ImageUtils.RESERVED_MEMORY_CELLS,
    // unary));

    nts.add(ConstantType.DEFAULT_CONSTANT_TYPE);
    nts.add(new ReflectionNodeType<Add, RealFunction>(Add.class, binary));
    nts.add(new ReflectionNodeType<Sub, RealFunction>(Sub.class, binary));
    nts.add(new ReflectionNodeType<Mul, RealFunction>(Mul.class, binary));
    nts.add(new ReflectionNodeType<Div, RealFunction>(Div.class, binary));
    nts.add(new ReflectionNodeType<Exp, RealFunction>(Exp.class, unary));
    nts.add(new ReflectionNodeType<Log, RealFunction>(Log.class, unary));
    nts.add(new ReflectionNodeType<Sin, RealFunction>(Sin.class, unary));
    nts.add(new ReflectionNodeType<ASin, RealFunction>(ASin.class, unary));

    nts.add(new ReflectionNodeType<IfThenElse, RealFunction>(
        IfThenElse.class, ternary));

    nts.add(new ReflectionNodeType<Ellipse, RealFunction>(//
        Ellipse.class, binary));
    nts.add(new ReflectionNodeType<Rectangle, RealFunction>(//
        Rectangle.class, binary));
    // nts.add(new ReflectionNodeType<GradientRectangle, RealFunction>(//
    // GradientRectangle.class, ternary));
    // nts.add(new ReflectionNodeType<GradientEllipse, RealFunction>(//
    // GradientEllipse.class, ternary));

    nts.add(new RasterFilterType(unary));

    nts.add(new ReflectionNodeType<FractionDiv, RealFunction>(//
        FractionDiv.class, ternary));
    nts.add(new ReflectionNodeType<Shearing, RealFunction>(//
        Shearing.class, ternary));
    nts.add(new ReflectionNodeType<Rotation, RealFunction>(//
        Rotation.class, binary));
    nts.add(new ReflectionNodeType<Scale, RealFunction>(//
        Scale.class, ternary));
    nts.add(new ReflectionNodeType<GridDivision, RealFunction>(//
        GridDivision.class, binary));
    nts.add(new ReflectionNodeType<Movement, RealFunction>(//
        Movement.class, ternary));

    // nts.add(new ReflectionNodeType<Push, RealFunction>(//
    // Push.class, binary));
    // nts.add(CallType.DEFAULT_CALL_TYPE);

    create = new TreeRampedHalfAndHalfRed<RealFunction>(nts, 15);
    mutate = ((IUnarySearchOperation) (new TreeMutatorRed(nts, 20)));
    crossover = ((IBinarySearchOperation) (new TreeRecombinationRed(nts,
        20)));

    EA = new SimpleGenerationalMOEA();
    EA.setCrossoverRate(0.5);
    EA.setMutationRate(0.5);
    EA.setComparator(Pareto.PARETO_COMPARATOR);
    EA.setFitnesAssignmentProcess(HBEF.hbfa);
    EA.setNullarySearchOperation(create);
    EA.setUnarySearchOperation(mutate);
    EA.setBinarySearchOperation(crossover);
    EA.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    EA.setPopulationSize(150);

    EA.setSelectionAlgorithm(new TournamentSelection(
        (int) (0.5d + (18d / Math.log(EA.getPopulationSize())))));

    EA.setMatingPoolSize(EA.getPopulationSize());
    EA.setTerminationCriterion(TerminationCriterion.NEVER_TERMINATE);
    EA.setObjectiveFunctions(f);
    EA.setMaxSolutions(1);

    new Thread(EA).start();
  }
}