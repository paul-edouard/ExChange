/*
 * Copyright (c) 2011 Thomas Weise
 * http://www.it-weise.de/
 * tweise@gmx.de
 *
 * GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)
 */

package demos.org.goataa.trees.image.syn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.goataa.impl.searchSpaces.trees.image.GraphicsContext;
import org.goataa.impl.searchSpaces.trees.image.ImageUtils;
import org.goataa.impl.searchSpaces.trees.image.RecursiveContext;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;
import org.goataa.impl.utils.Constants;
import org.goataa.impl.utils.Individual;

/**
 * An image display control
 *
 * @author Thomas Weise
 */
public class ImageControl extends /* JTabbedPane */JPanel {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the image producing tree */
  Individual<RealFunction, RealFunction> indi;

  /** the context */
  RecursiveContext context;

  /** the image component */
  private final JComponent img;

  // /** the function text */
  // private final JTextArea txt;

  /** the fitness */
  private final JTextArea fitness;

  /**
   * Create a new image context
   *
   * @param size
   *          the image size
   * @param maxDepth
   *          the maximum depth
   * @param addMem
   *          the additional memory cells
   * @param steps
   *          the steps
   */
  public ImageControl(final int size, final int maxDepth,
      final int addMem, final int steps) {
    super();

    JPanel p;
    GridBagLayout gbl;
    GridBagConstraints gbc;
    // JComponent x;
    // Dimension d;
    JButton b;

    this.context = new RecursiveContext(steps, maxDepth, addMem, size,
        size);
    this.indi = null;

    this.img = new ImageDisplay();

    this.fitness = new JTextArea();
    this.fitness.setEditable(true);

    b = new JButton("save"); //$NON-NLS-1$
    b.addActionListener(new ActionListener() {
      public final void actionPerformed(final ActionEvent e) {
        ImageControl.this.save();
      }
    });

    p = this;// new JPanel();
    p.add(this.img);
    p.add(this.fitness);
    p.add(b);

    gbl = new GridBagLayout();
    p.setLayout(gbl);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1d;
    gbc.weighty = 1d;
    gbc.ipadx = 4;
    gbc.ipadx = 4;
    gbl.addLayoutComponent(this.img, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1d;
    gbc.weighty = 0d;
    gbc.ipadx = 4;
    gbc.ipadx = 4;
    gbl.addLayoutComponent(this.fitness, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0d;
    gbc.weighty = 0d;
    gbc.ipadx = 4;
    gbc.ipadx = 4;
    gbl.addLayoutComponent(b, gbc);

    //    this.addTab("Image", p); //$NON-NLS-1$
    //
    // this.txt = new JTextArea();
    // this.txt.setEditable(false);
    // this.txt.setWrapStyleWord(false);
    // // this.txt.setLineWrap(true);
    //
    // x = new JScrollPane(this.txt,
    // ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
    // ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    //
    // d = x.getPreferredSize();
    // d.width = Math.min(d.width, size);
    // x.setPreferredSize(d);
    //
    // d = x.getMinimumSize();
    // d.width = Math.min(d.width, size);
    // x.setMinimumSize(d);
    //
    // d = x.getMaximumSize();
    // d.width = Math.min(d.width, size);
    // x.setMaximumSize(d);
    //
    //    this.addTab("Source", x); //$NON-NLS-1$
  }

  /**
   * Save the image
   */
  final void save() {
    JFileChooser fc;
    int rv;
    File f;

    fc = new JFileChooser();

    rv = fc.showSaveDialog(this);
    if (rv == JFileChooser.APPROVE_OPTION) {
      f = fc.getSelectedFile();
      if (f != null) {
        ImageUtils.store(this.context.getImage(), f.getAbsolutePath());
      }
    }
  }

  /**
   * Set the individual
   *
   * @param ind
   *          the individual
   */
  public final void setIndividual(
      final Individual<RealFunction, RealFunction> ind) {
    this.indi = ind;

    this.context.draw(this.indi.x);

    if ((ind.v >= 0d) && (!(Double.isInfinite(ind.v)))
        && (!(Double.isNaN(ind.v)))) {
      this.fitness.setText(String.valueOf(ind.v));
    } else {
      this.fitness.setText("100"); //$NON-NLS-1$
    }

    // this.txt.setText(ind.x.toString());
    this.img.invalidate();
    // this.txt.invalidate();
    this.fitness.invalidate();
    this.invalidate();
    this.repaint();
  }

  /**
   * Get the individual
   *
   * @return the individual
   */
  public final Individual<RealFunction, RealFunction> getIndividual() {
    if (this.indi == null) {
      return null;
    }
    try {
      this.indi.v = Double.parseDouble(this.fitness.getText());
    } catch (Throwable t) {
      this.indi.v = Constants.WORST_FITNESS;
    }
    return this.indi;
  }

  /** The image display component */
  private final class ImageDisplay extends JPanel {
    /** a constant required by Java serialization */
    private static final long serialVersionUID = 1;

    /** the dimension */
    private final Dimension d;

    /** create the image display */
    public ImageDisplay() {
      super();

      GraphicsContext c;

      c = ImageControl.this.context;
      this.d = new Dimension(c.getWidth(), c.getHeight());
    }

    /**
     * Returns the size
     *
     * @return the size
     */
    @Override
    public Dimension getSize() {
      return this.d;
    }

    /**
     * Get the preferred size
     *
     * @return the preferred size
     */
    @Override
    public Dimension getPreferredSize() {
      return this.getSize();
    }

    /**
     * Get the minimum size
     *
     * @return the minimum size
     */
    @Override
    public Dimension getMinimumSize() {
      return this.getSize();
    }

    /**
     * Get the maximum size
     *
     * @return the maximum size
     */
    @Override
    public Dimension getMaximumSize() {
      return this.getSize();
    }

    /**
     * Pain this image
     *
     * @param g
     *          the graphics
     */
    @Override
    public void paint(final Graphics g) {
      g.drawImage(ImageControl.this.context.getImage(), 0, 0, Color.white,
          this);
    }
  }

}