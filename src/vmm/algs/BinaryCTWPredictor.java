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

import vmm.algs.ctw.*;

/**
 * <p><b> Binary CTW Predictor </b></p>
 *
 * Binary CTW Predictor
 * Projects the sequences on the binary alphabet
 * AB = { 1, 2, 3 }
 * X = 122313
 * then bin(X) = 011010110111
 *
 * Then predicts over the binary sequence using the classical
 * CTW predictor.
 *
 * Example usage:
 *
 * DCTWPredictor dctw = new DCTWPredictor();
 * dctw.init(256, 5);
 * dctw.learn("abracadabra");
 * System.out.println("logeval : " + dctw.logEval("cadabra"));
 * System.out.println("P(c|abra) : " + dctw.predict('c', "abra"));
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Ron Begleiter
 * @version 1.0
 */

public class BinaryCTWPredictor
    implements VMMPredictor {

  private static final double NEGTIVE_INVERSE_LOG_2 = - (1 / Math.log(2.0));

  private static final int KT_ALPHA = 2; // defines the ctw zero-order evaluator;
  // KT_ALPHA=2 <==> using the KT-estimator.

  private static final int BINARY_AB_SIZE = 2;

  private static final int BYTE_SIZE = 8;
  private static final int BIT_MASKS[] = {
      1, 2, 4, 8, 16, 32, 64, 128, 256,
      512, 1024,
      (int) Math.pow(2, 11),
      (int) Math.pow(2, 12)}; // should be enough, otherwise exception

  private CTWVolfModel ctw;

  int abSymSizeInBits;

  public BinaryCTWPredictor() {

  }

  public void init(int abSize, int vmmOrder) {
    ctw = new CTWVolfModel();
    ctw.init(BINARY_AB_SIZE, vmmOrder, KT_ALPHA);
    abSymSizeInBits = (int) Math.ceil( -Math.log(abSize) *
                                      NEGTIVE_INVERSE_LOG_2);
  }

  /**
   * The trainingSequence is translated into a binary sequence
   * e.g.,
   * alphabet = { 1, 2, 3 }
   * trainingSequence = 122313
   * then bin(trainingSequence) = 011010110111
   * @param trainingSequence a sequence over a general alphabet
   */
  public void learn(CharSequence trainingSequence) {
    for (int i = 0, sym = -1; i < trainingSequence.length(); ++i) {
      sym = (int) trainingSequence.charAt(i);
      for (int bit = abSymSizeInBits - 1; bit >= 0; --bit) {
        ctw.learn( (sym & BIT_MASKS[bit]) >> bit);
      }
    }
  }

  public double predict(int symbol, CharSequence context) {
    try {
      for (int i = 0, sym = -1; i < context.length(); ++i) {
        sym = (int) context.charAt(i);
        for (int bit = abSymSizeInBits - 1; bit >= 0; --bit) {
          ctw.predict( (sym & BIT_MASKS[bit]) >> bit); //update the ctw context
        }
      }

      double p = 1.0;
      for (int bit = abSymSizeInBits - 1; bit >= 0; --bit) {
        p *= ctw.predict( (symbol & BIT_MASKS[bit]) >> bit); //update the ctw context
      }
      return p;
    }
    catch (NullPointerException npe) {
      if (ctw == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

  public double logEval(CharSequence testSequence) {
    ctw.clearContext();
    return logEval(testSequence, "");
  }

  public double logEval(CharSequence testSequence, CharSequence initialContext) {
    try {
      double eval = 0.0;

      for (int i = 0, sym = -1; i < initialContext.length(); ++i) {
        sym = (int) initialContext.charAt(i);
        for (int bit = abSymSizeInBits - 1; bit >= 0; --bit) {
          ctw.predict( (sym & BIT_MASKS[bit]) >> bit);
        }
      }

      for (int i = 0, sym = -1; i < testSequence.length(); ++i) {
        sym = (int) testSequence.charAt(i);
        for (int bit = abSymSizeInBits - 1; bit >= 0; --bit) {
          eval += Math.log(ctw.predict( (sym & BIT_MASKS[bit]) >> bit));
        }
      }
      return eval * NEGTIVE_INVERSE_LOG_2;
    }
    catch (NullPointerException npe) {
      if (ctw == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

   public static void main(String args[]) {
       BinaryCTWPredictor p = new BinaryCTWPredictor();
       p.init(6,8);
       char [] data = {1,2,5,1,3,1,4,1,2,5,1};
       String seq = new String(data);
       p.learn(seq);
       System.out.println(seq);
       System.out.println(""+p.logEval(seq, seq));

        /*  int sym = 'a';
          for (int bit = 6; bit >= 0; --bit) {
              System.out.println(""+ ((sym & BIT_MASKS[bit]) >> bit) );
          }*/
  }


}
