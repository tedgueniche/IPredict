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

import vmm.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CTWVolfModel {

  private Context context;
  private VolfNode ctwRoot;

  private int d;

  public CTWVolfModel() {
  }

  public void init(int alphabetSize, int depth, int alphaFactor) {
    context = new DefaultContext(depth);
    ctwRoot = new VolfNode();
    ctwRoot.init(alphabetSize, alphaFactor);
    d = depth;
  }


  public void init(int alphabetSize, int depth) {
    init(alphabetSize, depth, VolfNode.DEFAULT_ALPHA_FACTOR);
  }

  public double learn(int symbol) {
    double res = ctwRoot.learn(symbol,context.getIterator());
    context.add(symbol);
    return res;
  }

  // don't loose context
  public double predict(int symbol) {
    double res = ctwRoot.predict(symbol,context.getIterator());
    context.add(symbol);
    return res;
  }

  public void clearContext(){
    context = new DefaultContext(d);
  }

  public static void main(String args[]) {
    CTWVolfModel ctw = new CTWVolfModel();
    ctw.init(2, 2);

    int data[] = {0,1,0,0,1,1,0,1};
    for( int i=0; i<data.length; ++i) {
      ctw.learn(data[i]);
    }
    ctw.predict(1);
    ctw.predict(0);
    ctw.predict(1);
  }

}
