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

public class DynamicBinDNode extends AbsBinaryDNode{

  public DynamicBinDNode(int abSize, AbsBinaryDNode rightChild,
                         AbsBinaryDNode leftChild, int softModelDepth) {
    super(abSize, rightChild, leftChild, softModelDepth);
  }

  public double predict(int symbol, Context context){
    int direction = (super.children[RIGHT].descendants.get(symbol))?
        RIGHT :
        LEFT;

    double prediction = super.softClasifier.learn(symbol, direction, context.getIterator());
    /**@todo
     * new TernaryContext(children[0].descendants, children[1].descendants, context)
     * */
    double childP = super.children[direction].predict(symbol, context);
    return prediction * childP;
  }

}
