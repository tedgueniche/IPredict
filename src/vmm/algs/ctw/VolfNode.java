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

package vmm.algs.ctw;

import java.util.*;

import vmm.util.ContextIterator;

/**
 * <p>Title: VolfNode</p>
 * <p>Description: Coding Paul Volf imp.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Ron
 * @version 1.0
 */

public class VolfNode {
  /**
   * Value defined in Volf's thesis, section 4.3.2
   */
  public static final int DEFAULT_ALPHA_FACTOR = 16;

  //value defined in Volf thesis section 4.3.2
  private static final int MAX_COUNT_BEFORE_COUNT_HALVING = 256;

  private int abSize;
  private int alpha;//as defined in Volf's thesis, for the 0 order estimator.
  private int symCountArr[];
  private int allCount;
  private double beta;

  private double alphaInversed;
  private double abSizeDivAlpha;

  private VolfNode[] children;// mem consuming //


  public VolfNode() {

  }

  /**
   * Initializes this VolfNode
   *
   * @param alphabetSize size of the samples AB
   * @param alphaFactor as defined in Volf's thesis, for the 0 order estimator.
   */
  public void init(int alphabetSize, int alphaFactor) {
    abSize = alphabetSize;
    alpha = alphaFactor;
    alphaInversed = 1.0/alpha;
    abSizeDivAlpha = abSize/(double)alpha;

    symCountArr = new int[abSize];
    Arrays.fill(symCountArr,0);
    allCount = 0;
    children = null;//lazy instantiation
    beta = 1.0;
  }


  public double predict(int symbol, ContextIterator context) {
    double [] pwArr = predict(context);
    vmm.util.PerdictAssertion.assertThis(pwArr);
    return pwArr[symbol];
  }

  public double learn(int symbol, ContextIterator context) {
    double []res = learnAll(symbol, context);
    vmm.util.PerdictAssertion.assertThis(res);
    return res[symbol];
  }

  private double[] predict(ContextIterator context) {
    if (context.hasNext()) {
      VolfNode childOnContextPath = getChild(context.nextSymbol());
      double []ethaArr = childOnContextPath.predict(context);
      double denominator = 0.0;
      //1. compute b'
      double betaTag = beta / (allCount + abSizeDivAlpha);
      //2. compute intermediate result etha(context, sym)
      for(int sym=0; sym<abSize; ++sym) {
        ethaArr[sym] += betaTag * (symCountArr[sym]+alphaInversed);/**@todo (2)*/
        denominator += ethaArr[sym];
      }
      //3. compute conditional weighted probabilities
      for(int sym=0; sym<abSize; ++sym) {
        ethaArr[sym] /= denominator;
      }
      //4., 5. appear in learn method (no updates)
      return ethaArr;
    }
    else { //leaf - 0 order prediction
      double peArr[] = new double[abSize];
      for(int sym=0; sym<abSize; ++sym) {
        peArr[sym] = (symCountArr[sym]+alphaInversed)/(allCount + abSizeDivAlpha);
      }
      return peArr;
    }
  }


  private double[] learnAll(int newSymbol, ContextIterator context) {
    if (context.hasNext()) {

      VolfNode childOnContextPath = getChild(context.nextSymbol());
      double []ethaArr = childOnContextPath.learnAll(newSymbol, context);
      double childOnContextPw = ethaArr[newSymbol];
      double denominator = 0.0;
      //1. compute b'
      double betaTag = beta / (allCount + abSizeDivAlpha);
      //2. compute intemediate results into etha(context, sym)
      for(int sym=0; sym<abSize; ++sym) {
        ethaArr[sym] += betaTag * (symCountArr[sym]+alphaInversed);
        denominator += ethaArr[sym];
      }
      //3. compute conditional weighted probabilities
      for(int sym=0; sym<abSize; ++sym) {
        ethaArr[sym] /= denominator;
      }

      //4. update beta
      //     consts are from volf thesis
      beta = ((beta>1500000)||(beta<(1.0/1500000)))?
          beta/2.0 :
          (betaTag * (symCountArr[newSymbol]+alphaInversed))/childOnContextPw;

      //5. update symbol count
      symCountArr[newSymbol]++;
      allCount++;
      if (symCountArr[newSymbol]>MAX_COUNT_BEFORE_COUNT_HALVING)
        rescaleCounts();

      return ethaArr;
    }
    else { //leaf - 0 order prediction
      double peArr[] = new double[abSize];
      for(int sym=0; sym<abSize; ++sym) {
        peArr[sym] = (symCountArr[sym]+alphaInversed)/(allCount + abSizeDivAlpha);
      }
      symCountArr[newSymbol]++;
      allCount++; /* ver. JULY 2007 (due versions confusion?) */
      if (symCountArr[newSymbol]>MAX_COUNT_BEFORE_COUNT_HALVING)
        rescaleCounts();

      return peArr;
    }
  }

  private VolfNode getChild(int sym) {
    if (children==null) {
      children = new VolfNode[abSize];
    }
    if(children[sym]==null) {
      children[sym] = new VolfNode();
      children[sym].init(abSize, alpha);
    }
    return children[sym];
  }

  private void rescaleCounts() {
    allCount = 0;
    for(int i=0, rounding=0; i<symCountArr.length; ++i){
      rounding = symCountArr[i]%2;
      symCountArr[i] = symCountArr[i]>>1 + rounding;
      allCount += symCountArr[i];
    }
  }
}
