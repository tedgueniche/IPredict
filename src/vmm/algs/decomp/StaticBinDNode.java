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
 
package vmm.algs.decomp;

import vmm.util.Context;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class StaticBinDNode
    extends AbsBinaryDNode
    implements StaticDecompositionNode {

  public StaticBinDNode(int abSize, AbsBinaryDNode rightChild,
                         AbsBinaryDNode leftChild, int softModelDepth) {
    super(abSize, rightChild, leftChild, softModelDepth);
  }

  public double predict(int symbol, Context context){
    int direction = (super.children[RIGHT].descendants.get(symbol)) ?
        RIGHT :
        LEFT;
    /**@todo */
    double prediction = super.softClasifier.predict(symbol, direction, context.getIterator());
    return prediction * super.children[direction].predict(symbol, context);
  }

 public void train(int symbol, Context context){
   int direction = (super.children[RIGHT].descendants.get(symbol)) ?
        RIGHT :
        LEFT;

    try {
      super.softClasifier.learn(symbol, direction, context.getIterator());
      ( (StaticBinDNode) children[direction]).train(symbol, context);//ugly code!!
    }
    catch(Exception e) {
      // a leaf
    }
 }
}
