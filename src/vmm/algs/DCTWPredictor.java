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

import vmm.algs.decomp.*;
import vmm.util.*;

/**
 * <p>Title: Decomposed CTW Predictor </p>
 *
 * DCTWPredictor dctw = new DCTWPredictor();
 * dctw.init(256, 5);
 * dctw.learn("abracadabra");
 * System.out.println("logeval : " + dctw.logEval("cadabra"));
 * System.out.println("P(c|abra) : " + dctw.predict('c', "abra"));
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * @author <a href="http://www.cs.technion.ac.il">Ron Begleiter</a>
 * @version 1.0
 */

public class DCTWPredictor
    implements VMMPredictor {

  private static final double NEGTIVE_INVERSE_LOG_2 = - (1 / Math.log(2.0));

  private StaticDecompositionNode dctw;

  private int abSize;
  private int vmmOrder;

  public DCTWPredictor() {
  }

  public void init(int abSize, int vmmOrder) {
    this.abSize = abSize;
    this.vmmOrder = vmmOrder;
  }

  public void learn(CharSequence trainingSequence) {
    DecompositionTreeBuilder builder = new DecompositionTreeBuilder(abSize,
        vmmOrder);

    dctw = builder.buildStatic((SampleIterator)new StringSampleIter( trainingSequence.toString()));

    Context context = new DefaultContext(vmmOrder);

    for(int i=0, symbol=-1; i<trainingSequence.length(); ++i) {
      symbol = (int)trainingSequence.charAt(i);
      dctw.train(symbol, context);
      context.add(symbol);
    }
  }

  public double predict(int symbol, CharSequence context) {
    try {
      Context ctwContext = new DefaultContext(vmmOrder);
      for (int i = 0; i < context.length(); ++i) {
        ctwContext.add( (int) context.charAt(i));
      }

      return dctw.predict(symbol, ctwContext);
    }
    catch (NullPointerException npe) {
      if (dctw == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

  public double logEval(CharSequence testSequence) {
    return logEval(testSequence, "");

  }

  public double logEval(CharSequence testSequence, CharSequence initialContext) {
    try {
      Context context = new DefaultContext(vmmOrder);
      for (int i = 0; i < initialContext.length(); ++i) {
        context.add( (int) initialContext.charAt(i));
      }

      double eval = 0.0;
      for (int i = 0, sym = 0; i < testSequence.length(); ++i) {
        sym = (int) testSequence.charAt(i);
        eval += Math.log(dctw.predict(sym, context));
        context.add(sym);
      }

      return eval * NEGTIVE_INVERSE_LOG_2;
    }
    catch (NullPointerException npe) {
      if (dctw == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }
  }

}
