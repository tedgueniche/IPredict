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

import vmm.pred.VMMPredictor;
import vmm.pred.VMMNotTrainedException;

import vmm.algs.oppm.*;

import java.io.*;


/**
 * <p><b>PPMC Predictor</b></p>
 * <p>
 * Usage example:
 *
 * PPMCPredictor ppmc = new PPMCPredictor();
 * ppmc.init(256, 5);
 * ppmc.learn("abracadabra");
 * System.out.println("logeval : " + ppmc.logEval("cadabra"));
 * System.out.println("P(c|abra) : " + ppmc.predict('c', "abra"));
 * </p>
 *
 * Using <a href="http://www.colloquial.com/carp/">Bob Carpenter</a> code.
 * <p>Copyright: Copyright (c) 2004</p>
 * @author <a href="http://www.cs.technion.ac.il/~ronbeg">Ron Begleiter</a>
 * @version 1.0
 */

public final class PPMCPredictor
    implements VMMPredictor {

  private static final double NEGTIVE_INVERSE_LOG_2 = - (1 / Math.log(2.0));

  private OfflinePPMModel ppmc;

  public PPMCPredictor() {
    ppmc = null;
  }

  /**
   * initializes this PPMPredictor
   * @param abSize alphabet size
   * @param vmmOrder VMM order
   */
  public void init(int abSize, int vmmOrder) {
    ppmc = new OfflinePPMModel(vmmOrder, abSize);
  }

  public void learn(CharSequence trainingSequence) {
    for (int symIndex = 0; symIndex < trainingSequence.length(); ++symIndex) {
      ppmc.use(trainingSequence.charAt( (int) symIndex));
    }
  }

  public double predict(int symbol, CharSequence context) {
    try {
      ppmc.clearContext();
      for (int i = 0; i < context.length(); ++i) {
        ppmc.predict( (int) context.charAt(i)); //updates the ppmc context
      }
      return ppmc.predict(symbol);
    }
    catch (NullPointerException npe) {
      if (ppmc == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }
  }

  public double logEval(CharSequence testSequence) {
    try {
      ppmc.clearContext();

      double value = 0.0;

      for (int i = 0; i < testSequence.length(); ++i) {
        value += Math.log(ppmc.predict( (int) testSequence.charAt(i)));
      }
      return value * NEGTIVE_INVERSE_LOG_2; // the Math.log is in natural base
    }
    catch (NullPointerException npe) {
      if (ppmc == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

  public double logEval(CharSequence testSequence, CharSequence initialContext) {
    for (int symIndex = 0; symIndex < initialContext.length(); ++symIndex) {
      ppmc.use(initialContext.charAt( (int) symIndex));
    }
    return logEval(testSequence);
  }

}
