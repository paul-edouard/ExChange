// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.strings.queens;

import org.goataa.impl.algorithms.ea.SimpleGenerationalEA;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.algorithms.eda.EDA;
import org.goataa.impl.algorithms.eda.umda.UMDAModelBuilder2;
import org.goataa.impl.algorithms.eda.umda.UMDAModelCreator;
import org.goataa.impl.algorithms.eda.umda.UMDAModelSampler;
import org.goataa.impl.gpms.IdentityMapping;
import org.goataa.impl.searchOperations.strings.bits.booleans.binary.BooleanArrayUniformCrossover;
import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;
import org.goataa.impl.searchOperations.strings.bits.booleans.unary.BooleanArraySingleBitFlipMutation;
import org.goataa.impl.termination.StepLimit;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IBinarySearchOperation;
import org.goataa.spec.IGPM;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.IUnarySearchOperation;

/**
 * A test of the n-queens optimization task.
 *
 * @author Thomas Weise
 */
public class QueensTest {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments which are ignored here
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final QueensFitnessFunction f;
    final INullarySearchOperation<boolean[]> create;
    final IUnarySearchOperation<boolean[]> sp;
    final IBinarySearchOperation<boolean[]> ux;
    Individual<?, boolean[]> ind;
    SimpleGenerationalEA<boolean[], boolean[]> EA;
    EDA<boolean[], boolean[], double[]> UMDA;

    final int dim;

    dim = 12;
    f = new QueensFitnessFunction();

    create = new BooleanArrayUniformCreation(dim * dim);
    sp = BooleanArraySingleBitFlipMutation.BOOLEAN_ARRAY_SINGLE_BIT_FLIP_MUTATION;
    ux = BooleanArrayUniformCrossover.BOOLEAN_ARRAY_UNIFORM_CROSSOVER;

    EA = new SimpleGenerationalEA<boolean[], boolean[]>();
    EA.setBinarySearchOperation(ux);
    EA.setUnarySearchOperation(sp);
    EA.setNullarySearchOperation(create);
    EA.setMutationRate(0.5);
    EA.setCrossoverRate(0.5);
    EA.setPopulationSize(1000);
    EA.setSelectionAlgorithm(new TournamentSelection(3));
    EA.setMatingPoolSize(200);
    EA.setGPM((IGPM) (IdentityMapping.IDENTITY_MAPPING));
    EA.setTerminationCriterion(new StepLimit(100000));
    EA.setObjectiveFunction(f);

    ind = EA.call().get(0);

    System.out.println("Fitness: " + ind.v); //$NON-NLS-1$
    System.out.println("Errors : " + f.decomposeErrors(ind.v));//$NON-NLS-1$
    System.out.println("Queens : " + f.decomposeQueens(ind.v));//$NON-NLS-1$
    System.out.println(f.toString(ind.x));

    System.out.println();
    System.out.println();

    UMDA = new EDA<boolean[], boolean[], double[]>();
    UMDA.setGPM(EA.getGPM());
    UMDA.setPopulationSize(EA.getPopulationSize());
    UMDA.setMatingPoolSize(EA.getMatingPoolSize());
    UMDA.setSelectionAlgorithm(EA.getSelectionAlgorithm());
    UMDA.setModelCreator(new UMDAModelCreator(dim * dim));
    UMDA.setModelBuilder(new UMDAModelBuilder2());
    UMDA.setModelSampler(new UMDAModelSampler());
    UMDA.setTerminationCriterion(EA.getTerminationCriterion());
    UMDA.setObjectiveFunction(f);

    ind = UMDA.call().get(0);

    System.out.println("Fitness: " + ind.v); //$NON-NLS-1$
    System.out.println("Errors : " + f.decomposeErrors(ind.v));//$NON-NLS-1$
    System.out.println("Queens : " + f.decomposeQueens(ind.v));//$NON-NLS-1$
    System.out.println(f.toString(ind.x));

  }
}