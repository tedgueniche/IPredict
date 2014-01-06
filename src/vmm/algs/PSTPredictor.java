/* HEADER
 If you use this code don’t forget to reference us :) BibTeX: http://www.cs.technion.ac.il/~rani/el-yaniv_bib.html#BegleiterEY04

 This code is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This code is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License (<a href="http://www.gnu.org/copyleft/gpl.html">GPL</a>) for more details.*/

package vmm.algs;

import vmm.pred.*;

import vmm.algs.pst.*;
import vmm.util.*;

/**
 * <p><b>Probability Suffix Tree Predictor</b></p>
 * <p>Usage example: <br>
 * pst.init(256, 0.001, 0.0, 0.0001, 1.05, 20);
 * pst.learn("abracadabra");
 * System.out.println("logeval : " + pst.logEval("cadabra"));
 * System.out.println("P(c|abra) : " + pst.predict('c', "abra"));
 * </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author <a href="http://www.cs.technion.ac.il/~ronbeg">Ron Begleiter</a>
 * @version 1.0
 */
public class PSTPredictor
    implements VMMPredictor {

  private static final double NEGTIVE_INVERSE_LOG_2 = - (1 / Math.log(2.0));

  private int abSize;
  private double pMin;
  private double alpha;
  private double gamma;
  private double r;
  private int vmmOrder;

  private PSTNodeInterface pst;

  public PSTPredictor() {
  }

  /**
   * Initializes this PSTPredictor.
   *
   * @param abSize alphabet size
   * @param pMin refer to the paper
   * @param alpha refer to the paper
   * @param gamma refer to the paper
   * @param r refer to the paper
   * @param vmmOrder refer to the paper
   */
  public void init(int abSize, double pMin,
                   double alpha, double gamma, double r, int vmmOrder) {
    this.abSize = abSize;
    this.pMin = pMin;
    this.alpha = alpha;
    this.gamma = gamma;
    this.r = r;
    this.vmmOrder = vmmOrder;
  }

  public void learn(CharSequence trainingSequence) {
    PSTBuilder builder = new PSTBuilder(abSize);
    SampleIterator2SamplesBridge samples = new SampleIterator2SamplesBridge();
    samples.init(new StringSampleIter(trainingSequence.toString()));
    pst = builder.build(samples, pMin, alpha, gamma, r, vmmOrder);
  }

  public double predict(int symbol, CharSequence context) {
    try {
      double pArr[] = new double[abSize];

      PSTArithPredictor pstPredictor = new PSTArithPredictor(pst);

      for (int i = 0, sym = -1; i < context.length(); ++i) {
        sym = (int) context.charAt(i);
        pstPredictor.predict(pArr);
        pstPredictor.increment(sym);
      }
      pstPredictor.predict(pArr);
      return pArr[symbol];
    }
    catch (NullPointerException npe) {
      if (pst == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }
  }

  public double logEval(CharSequence testSequence) {
    try {
      double pArr[] = new double[abSize];
      double eval = 0.0;

      PSTArithPredictor pstPredictor = new PSTArithPredictor(pst);

      for (int i = 0, sym = -1; i < testSequence.length(); ++i) {
        sym = (int) testSequence.charAt(i);
        pstPredictor.predict(pArr);
        eval += Math.log(pArr[sym]);
        pstPredictor.increment(sym);
      }

      return eval * NEGTIVE_INVERSE_LOG_2;
    }
    catch (NullPointerException npe) {
      if (pst == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

  public double logEval(CharSequence testSequence, CharSequence initialContext) {
    try {
      double pArr[] = new double[abSize];
      double eval = 0.0;

      PSTArithPredictor pstPredictor = new PSTArithPredictor(pst);

      for (int i = 0, sym = -1; i < initialContext.length(); ++i) {
        sym = (int) initialContext.charAt(i);
        pstPredictor.increment(sym);
      }

      for (int i = 0, sym = -1; i < testSequence.length(); ++i) {
        sym = (int) testSequence.charAt(i);
        pstPredictor.predict(pArr);
        eval += Math.log(pArr[sym]);
        pstPredictor.increment(sym);
      }

      return eval * NEGTIVE_INVERSE_LOG_2;
    }
    catch (NullPointerException npe) {
      if (pst == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

}
