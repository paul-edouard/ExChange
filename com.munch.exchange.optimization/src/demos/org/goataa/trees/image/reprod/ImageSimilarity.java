// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package demos.org.goataa.trees.image.reprod;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.searchSpaces.trees.image.GraphicsContext;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;
import org.goataa.spec.IObjectiveFunction;

/**
 * An objective function judging image similarity
 *
 * @author Thomas Weise
 */
public class ImageSimilarity extends OptimizationModule implements
    IObjectiveFunction<RealFunction> {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /** the execution environment */
  private final GraphicsContext exec;

  /** the rgb disection of the image */
  private final RGB[][] rgb;

  /**
   * Create a new image similarity measure
   *
   * @param c
   *          the comparison data
   * @param e
   *          the execution context
   */
  public ImageSimilarity(final BufferedImage c, final GraphicsContext e) {
    super();
    this.exec = e;

    final int w, h;
    final RGB[][] px;
    int i, j;

    h = c.getHeight();
    w = c.getWidth();
    this.rgb = px = new RGB[h][w];

    for (i = h; (--i) >= 0;) {
      for (j = w; (--j) >= 0;) {
        px[i][j] = new RGB(c.getRGB(j, i));
      }
    }
  }

  /**
   * Compute the objective value, i.e., determine the utility of the
   * solution candidate x as specified in Definition D2.3.
   *
   * @param x
   *          the phenotype to be rated
   * @param r
   *          the randomizer
   * @return the objective value of x, the lower the better (see Section
   *         6.3.4)
   */
  public double compute(final RealFunction x, final Random r) {
    final BufferedImage res;
    final GraphicsContext e;
    final RGB[][] pxx;
    RGB[] px;
    int i, j;
    long sum;

    e = this.exec;

    e.draw(x);

    sum = 0l;
    res = e.getImage();
    pxx = this.rgb;
    for (i = Math.min(pxx.length, res.getHeight()); (--i) >= 0;) {
      px = pxx[i];
      for (j = Math.min(px.length, res.getWidth()); (--j) >= 0;) {
        sum += px[j].score(res.getRGB(j, i));
      }
    }

    return sum;
  }

  /**
   * The rgb class
   *
   * @author Thomas Weise
   */
  private static final class RGB {
    /** red */
    final int r;

    /** green */
    final int g;

    /** blue */
    final int b;

    /**
     * Create a new rgb
     *
     * @param px
     *          the pixel
     */
    RGB(final int px) {
      this(px & 0xff, (px >>> 8) & 0xff, (px >>> 16) & 0xff);
    }

    /**
     * Create a new rgb
     *
     * @param rr
     *          the red
     * @param gg
     *          the green
     * @param bb
     *          the blue
     */
    private RGB(final int rr, final int gg, final int bb) {
      super();
      this.r = rr;
      this.g = gg;
      this.b = bb;
    }

    /**
     * Score the pixel
     *
     * @param px
     *          the pixel
     * @return the score
     */
    final long score(final int px) {
      long x, y, z;

      x = (px & 0xff) - this.r;
      y = ((px >>> 8) & 0xff) - this.g;
      z = ((px >>> 16) & 0xff) - this.b;

      return (x * x) + (y * y) + (z * z);
    }
  }

}