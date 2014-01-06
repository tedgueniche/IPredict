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

import java.util.*;
import vmm.util.*;

/**
 * <p>Title: DLeaf </p>
 * <p>Description: Decomposition Tree Leaf </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BinDLeaf extends AbsBinaryDNode{
  private int sym;

  public BinDLeaf(int symbol, int absize) {
    super();
    super.descendants = new BitSet(absize);
    sym = symbol;
    super.descendants.set(sym, true);
  }

  public int symbol(){
    return sym;
  }

  public double predict(int sym, Context context){
    if(sym==this.sym){
      return 1.0;
    }
    else{
      throw new RuntimeException("Never Should Happen! with sym="+sym+" this.sym="+this.sym);
    }
  }

  public void train(int symbol){
    //NoOp
  }

  public boolean equals(Object obj){
    return ((obj instanceof BinDLeaf)&&(((BinDLeaf)obj).sym==this.sym));
  }

  public int hashCode(){
    return this.sym;
  }

  public String toString(){
    return ""+sym;
  }

}
