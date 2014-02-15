/*
 * Copyright (c) 2011 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.trees.image.syn;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.goataa.impl.utils.Individual;
import org.goataa.impl.utils.MOIndividual;
import org.goataa.impl.utils.Utils;
import org.goataa.spec.IFitnessAssignmentProcess;
import org.goataa.spec.IIndividualComparator;

/**
 * The human-based fitness assignment process
 *
 * @author Thomas Weise
 */
public class HBFA extends JPanel implements IFitnessAssignmentProcess {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the population display */
  final PopDisplay pdp;

  /** the ok button */
  final JButton ok;

  /** the synchronizer */
  final Object sync;

  /**
   * the human-based fitness assignment step
   *
   * @param size
   *          the image size
   * @param maxDepth
   *          the maximum depth
   * @param addMem
   *          the additional memory to be used by the programs
   * @param steps
   *          the maximum computation steps
   */
  public HBFA(final int size, final int maxDepth, final int addMem,
      final int steps) {
    super();

    GridBagLayout gbl;
    GridBagConstraints gbc;
    JComponent incl;

    this.pdp = new PopDisplay(size, maxDepth, addMem, steps);
    this.sync = new Object();

    this.ok = new JButton("OK"); //$NON-NLS-1$
    this.ok.setEnabled(false);
    this.ok.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        try {
          synchronized (HBFA.this.sync) {
            HBFA.this.sync.notifyAll();
          }
        } catch (Throwable t) {
          //
        }
      }
    });

    incl = new JScrollPane(this.pdp,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    this.add(incl);
    this.add(this.ok);
    gbl = new GridBagLayout();
    this.setLayout(gbl);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1d;
    gbc.weighty = 1d;
    gbc.ipadx = 4;
    gbc.ipadx = 4;
    gbl.addLayoutComponent(incl, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 1d;
    gbc.weighty = 0d;
    gbc.ipadx = 4;
    gbc.ipadx = 4;
    gbl.addLayoutComponent(this.ok, gbc);

  }

  /**
   * Assign the fitness
   *
   * @param pop
   *          the population of individuals
   * @param start
   *          the index of the first individual in the population
   * @param count
   *          the number of individuals in the population
   * @param cmp
   *          the individual comparator which tells which individual is
   *          better than another one
   * @param r
   *          than randomizer
   */
  public void assignFitness(final MOIndividual<?, ?>[] pop,
      final int start, final int count, final IIndividualComparator cmp,
      final Random r) {

    try {
      SwingUtilities.invokeAndWait(new StartFA(pop, start, count));
    } catch (Throwable t) {
      Utils.gc();
    }

    try {
      synchronized (this.sync) {
        this.sync.wait();
      }
    } catch (Throwable t) {
      Utils.gc();
    }

    try {
      SwingUtilities.invokeAndWait(new EndFA());
    } catch (Throwable t) {
      Utils.gc();
    }

    Utils.gc();
  }

  /**
   * Get the configuration
   *
   * @param longVersion
   *          should we print a long version?
   * @return the configuration
   */
  @Override
  public String getConfiguration(boolean longVersion) {
    return ""; //$NON-NLS-1$
  }

  /**
   * Get the name
   *
   * @param longVersion
   *          should we print a long version?
   * @return the name
   */
  @Override
  public String getName(boolean longVersion) {
    return (longVersion) ? "Human-besed Image Fitness Assignment" : //$NON-NLS-1$
        "HBIFA"; //$NON-NLS-1$
  }

  /**
   * Convert to string
   *
   * @param longVersion
   *          should we print a long version?
   * @return the string
   */
  @Override
  public String toString(boolean longVersion) {
    return this.getName(longVersion);
  }

  /**
   * A job for starting the fitness assignment
   *
   * @author Thomas Weise
   */
  @SuppressWarnings("unchecked")
  private final class StartFA implements Runnable {
    /** the individuals */
    private final Individual[] ind;

    /** the start index */
    private final int start;

    /** the count */
    private final int count;

    /**
     * Create a new evaluation job
     *
     * @param is
     *          the individuals
     * @param st
     *          the start index
     * @param ct
     *          the count
     */
    StartFA(final Individual[] is, final int st, final int ct) {
      super();
      this.ind = is;
      this.start = st;
      this.count = ct;
    }

    /** run */
    public final void run() {
      HBFA.this.pdp.beginEvaluation(this.ind, this.start, this.count);
      HBFA.this.ok.setEnabled(true);
    }
  }

  /**
   * End the fitness evaluation process
   *
   * @author Thomas Weise
   */
  private final class EndFA implements Runnable {
    /** end the fitness assignment step */
    EndFA() {
      super();
    }

    /** run! */
    public final void run() {
      HBFA.this.ok.setEnabled(false);
      HBFA.this.pdp.endEvaluation();
    }
  }
}