/*
 * Copyright (c) 2011 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.trees.image.syn;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.goataa.impl.utils.Individual;

/**
 * A population display
 *
 * @author Thomas Weise
 */
public class PopDisplay extends JPanel {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the controls */
  private ImageControl[] ic;

  /** the image size */
  private final int si;

  /** the maximum depth */
  private final int md;

  /** the additional memory to be used by the programs */
  private final int am;

  /** the maximum computation steps */
  private final int st;

  /**
   * create population display a panel
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
  public PopDisplay(final int size, final int maxDepth, final int addMem,
      final int steps) {
    super();
    this.setLayout(new ModifiedFlowLayout());
    this.si = size;
    this.am = addMem;
    this.st = steps;
    this.md = maxDepth;
  }

  /**
   * Begin with the evaluation process
   *
   * @param pop
   *          the population
   * @param start
   *          the start index
   * @param count
   *          the count
   */
  @SuppressWarnings("unchecked")
  public final void beginEvaluation(final Individual[] pop,
      final int start, final int count) {
    ImageControl[] icc;
    int i, c;

    icc = this.ic;
    if ((icc == null) || (icc.length != count)) {
      c = 0;
      if (icc != null) {
        c = icc.length;
        for (i = c; (--i) >= 0;) {
          this.remove(icc[i]);
        }
      }
      icc = new ImageControl[count];
      if (c > 0) {
        c = Math.min(count, c);
        System.arraycopy(this.ic, 0, icc, 0, c);
      }
      for (; c < count; c++) {
        icc[c] = new ImageControl(this.si, this.md, this.am, this.st);
      }
      this.ic = icc;

      for (i = icc.length; (--i) >= 0;) {
        this.add(icc[i]);
      }

      ((JFrame) (SwingUtilities.getWindowAncestor(this))).pack();
    }

    for (i = count; (--i) >= 0;) {
      icc[i].setIndividual(pop[start + i]);
    }
  }

  /** End the evaluation process */
  public final void endEvaluation() {
    for (ImageControl icx : this.ic) {
      icx.getIndividual();
    }
  }

  /**
   * A modified version of FlowLayout that allows containers using this
   * Layout to behave in a reasonable manner when placed inside a
   * JScrollPane. Found at
   * http://coding.derkeiler.com/Archive/Java/comp.lang
   * .java.gui/2004-03/0818.html
   *
   * @author Babu Kalakrishnan
   */
  private static final class ModifiedFlowLayout extends FlowLayout {
    /** a constant required by Java serialization */
    private static final long serialVersionUID = 1;

    /** the constructor */
    ModifiedFlowLayout() {
      super();
    }

    /**
     * Get the minimum layout size
     *
     * @param target
     *          the container
     * @return the minimum layout size
     */
    @Override
    public final Dimension minimumLayoutSize(final Container target) {
      return this.computeSize(target, false);
    }

    /**
     * Get the minimum layout size
     *
     * @param target
     *          the container
     * @return the minimum layout size
     */
    @Override
    public final Dimension preferredLayoutSize(final Container target) {
      return this.computeSize(target, true);
    }

    /**
     * Get the minimum layout size
     *
     * @param target
     *          the container
     * @param minimum
     *          the minimum
     * @return the minimum layout size
     */
    private final Dimension computeSize(final Container target,
        final boolean minimum) {
      synchronized (target.getTreeLock()) {
        int hgap, vgap, w, reqdWidth, maxwidth, n, x, y, rowHeight, i;
        Component c;
        Dimension d;
        Insets insets;

        hgap = getHgap();
        vgap = getVgap();
        w = target.getWidth();

        // Let this behave like a regular FlowLayout (single row)
        // if the container hasn't been assigned any size yet
        if (w == 0) {
          w = Integer.MAX_VALUE;
        }

        insets = target.getInsets();
        if (insets == null) {
          insets = new Insets(0, 0, 0, 0);
        }
        reqdWidth = 0;

        maxwidth = w - (insets.left + insets.right + hgap * 2);
        n = target.getComponentCount();
        x = 0;
        y = insets.top;
        rowHeight = 0;

        for (i = 0; i < n; i++) {
          c = target.getComponent(i);
          if (c.isVisible()) {
            d = minimum ? c.getMinimumSize() : c.getPreferredSize();
            if ((x == 0) || ((x + d.width) <= maxwidth)) {
              if (x > 0) {
                x += hgap;
              }
              x += d.width;
              rowHeight = Math.max(rowHeight, d.height);
            } else {
              x = d.width;
              y += vgap + rowHeight;
              rowHeight = d.height;
            }
            reqdWidth = Math.max(reqdWidth, x);
          }
        }
        y += rowHeight;
        return new Dimension(reqdWidth + insets.left + insets.right, y);
      }
    }
  }
}