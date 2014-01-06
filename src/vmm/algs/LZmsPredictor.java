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

import java.util.ArrayList;
import java.util.List;

import vmm.pred.VMMPredictor;
import vmm.pred.VMMNotTrainedException;

import vmm.algs.lzms.*;

/**
 * <b>LZms Predictor</b>
 * Usage example:
 * LZmsPredictor lzms = new LZmsPredictor();
 * lzms.init(256, 2, 0);
 * lzms.learn("abracadabra");
 * System.out.println("logeval : " + lzms.logEval("cadabra"));
 * System.out.println("P(c|abra) : " + lzms.predict('c', "abra"));
 *
 * Using Moti Nisenson's code.
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author <a href="http://www.cs.technion.ac.il/~ronbeg">Ron Begleiter</a>
 * @version 1.0
 */

public class LZmsPredictor
    implements VMMPredictor {

  private LZmsTree lzms;

  private int abSize;
  private int mParam;
  private int sParam;

  public LZmsPredictor() {
  }

  public void init(int abSize, int mParam, int sParam) {
    this.abSize = abSize;
    this.mParam = mParam;
    this.sParam = sParam;
  }

  public void learn(CharSequence trainingSequence) {
	  // 2013: MODIFIED BY PHILIPPE TO REMOVE DEPEDENCY TO T.ROVE
//    List<Integer> symList = new ArrayList<Integer>();
	  int symList[] = new int[trainingSequence.length()];

    for (int i = 0; i < trainingSequence.length(); ++i) {
//      symList.add( (int) trainingSequence.charAt(i));
      symList[i] = (int) trainingSequence.charAt(i);
    }
    lzms = new LZmsTree(mParam, sParam, abSize);
    lzms.learnSequence(symList);
  }

  public double predict(int symbol, CharSequence context) {
    try {

      // using the following relation: P(a | X) = P(Xa)/P(X)
      StringBuffer seqWithSym = new StringBuffer();
      seqWithSym.append(context).append((char)symbol);
      StringBuffer seqNoSym = new StringBuffer();
      seqNoSym.append(context);

      return Math.pow( 2.0, - (logEval(seqWithSym) - logEval(seqNoSym)));
    }
    catch (NullPointerException npe) {
      if (lzms == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

  public double logEval(CharSequence testSequence) {
    try {

  	  // 2013: MODIFIED BY PHILIPPE TO REMOVE DEPEDENCY TO T.ROVE
  	  int symList[] = new int[testSequence.length()];
//      TIntArrayList symList = new TIntArrayList();
      for (int i = 0; i < testSequence.length(); ++i) {
//        symList.add( (int) testSequence.charAt(i));
        symList[i] = (int) testSequence.charAt(i);
      }
      return lzms.calcLogLikelihood(symList);
    }
    catch (NullPointerException npe) {
      if (lzms == null) {
        throw new VMMNotTrainedException();
      }
      else {
        throw npe;
      }
    }

  }

  public double logEval(CharSequence testSequence, CharSequence initialContext) {
    logEval(initialContext);
    return logEval(testSequence);
  }

}
