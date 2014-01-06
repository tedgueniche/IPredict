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
 
package vmm.algs.pst;

import vmm.util.*;

/**
 * <p>Title: PSTArithPredictor</p>
 * <p>Description: PST Implementation of the ArithPredictor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Technion</p>
 * @author Ron Begleiter
 * @version 1.0
 */

public class PSTArithPredictor{

  private PSTNodeInterface pst;
  private Context context;

  public PSTArithPredictor(PSTNodeInterface pstRoot) {
    this.pst = pstRoot;
    context = new DefaultContext(pst.subTreeHeight());
  }


  /**
   * @see arith.ArithPredictor
   */
  public void predict(double prediction[]){
    PSTNodeInterface contextNode = pst.get(context.getIterator());
    contextNode.predict(prediction);
  }

  /**
   * @see arith.ArithPredictor
   */
  public void increment(int symbol){
    context.add(symbol);
  }

  /**
   * @see arith.ArithPredictor
   */
  public int alphabetSize(){
    return pst.getAlphabetSize();
  }

}
