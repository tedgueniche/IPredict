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

public class DecompVolfNode {
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

  private DecompVolfNode[] children;// mem consuming //

  public DecompVolfNode() {

  }

  /**
   * Initializes this VolfNode
   *
   * @param alphabetSize size of the samples AB
   * @param alphaFactor as defined in Volf's thesis, for the 0 order estimator.
   */
  public void init(int symABSize, int alphaFactor) {
    abSize = symABSize;
    alpha = alphaFactor;

    alphaInversed = 1.0/alpha;
    abSizeDivAlpha = 2/(double)alpha;/*ZZZ*/

    symCountArr = new int[2];/*ZZZ*/
    Arrays.fill(symCountArr,0);
    allCount = 0;
    children = null;//lazy instantiation
    beta = 1.0;
  }

  public double predict(int symbol, int symSetLabel, ContextIterator context) {
    double [] pwArr = predict(context);
    vmm.util.PerdictAssertion.assertThis(pwArr);

    return pwArr[symSetLabel];
  }

  public double learn(int symbol, int symSetLabel, ContextIterator context) {
    double []res = learnAll(symbol, symSetLabel, context);
    vmm.util.PerdictAssertion.assertThis(res);
    return res[symSetLabel];
  }

  private double[] predict(ContextIterator context) {
    if (context.hasNext()) {
      DecompVolfNode childOnContextPath = getChild(context.nextSymbol());
      double []ethaArr = childOnContextPath.predict(context);
      double denominator = 0.0;
      //1. compute b'
      double betaTag = beta / (allCount + abSizeDivAlpha);
      //2. compute intermediate result etha(context, sym)
      for(int sym=0; sym<2; ++sym) {
        ethaArr[sym] += betaTag * (symCountArr[sym]+alphaInversed);/**@todo (2)*/
        denominator += ethaArr[sym];
      }
      //3. compute conditional weighted probabilities
      for(int sym=0; sym<2; ++sym) {
        ethaArr[sym] /= denominator;
      }
      //4., 5. appear in learn method (no updates)
      return ethaArr;
    }
    else { //leaf - 0 order prediction
      double peArr[] = new double[2];
      for(int sym=0; sym<2; ++sym) {
        peArr[sym] = (symCountArr[sym]+alphaInversed)/(allCount + abSizeDivAlpha);
      }
      return peArr;
    }
  }



  private double[] learnAll(int newSymbol, int symSetLabel, ContextIterator context) {
    if (context.hasNext()) {

      DecompVolfNode childOnContextPath = getChild(context.nextSymbol());
      double []ethaArr = childOnContextPath.learnAll(newSymbol, symSetLabel, context);
      double childOnContextPw = ethaArr[symSetLabel];
      double denominator = 0.0;
      //1. compute b'
      double betaTag = beta / (allCount + abSizeDivAlpha);
      //2. compute intemediate results into etha(context, sym)
      for(int sym=0; sym<2; ++sym) {
        ethaArr[sym] += betaTag * (symCountArr[sym]+alphaInversed);
        denominator += ethaArr[sym];
      }
      //3. compute conditional weighted probabilities
      for(int sym=0; sym<2; ++sym) {
        ethaArr[sym] /= denominator;
      }

      //4. update beta
      if(beta>1500000) {
        beta /= 2.0;
      }
      else if (beta<(1.0/1500000)){
        beta *= 2.0;
      }
      else {
        beta = (betaTag * (symCountArr[symSetLabel] + alphaInversed)) / childOnContextPw;
      }

      //5. update symbol count
      symCountArr[symSetLabel]++;
      allCount++;
      if (symCountArr[symSetLabel]>MAX_COUNT_BEFORE_COUNT_HALVING)
        rescaleCounts();

      return ethaArr;
    }
    else { //leaf - 0 order prediction
      double peArr[] = new double[2];
      for(int sym=0; sym<2; ++sym) {
        peArr[sym] = (symCountArr[sym]+alphaInversed)/(allCount + abSizeDivAlpha);
      }
      symCountArr[symSetLabel]++;
      allCount++;
      if (symCountArr[symSetLabel]>MAX_COUNT_BEFORE_COUNT_HALVING)
        rescaleCounts();

      return peArr;
    }
  }

  private DecompVolfNode getChild(int sym) {
    if (children==null) {
      children = new DecompVolfNode[abSize];
    }
    if(children[sym]==null) {
      children[sym] = new DecompVolfNode();
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
