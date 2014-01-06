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
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DecompCTWVolfModel {
  private Context context;
  private DecompVolfNode ctwRoot;

  public DecompCTWVolfModel() {
  }

  public void init(int alphabetSize, int depth, int alphaFactor) {
    context = new DefaultContext(depth);
    ctwRoot = new DecompVolfNode();
    ctwRoot.init(alphabetSize, alphaFactor);
  }

  public void init(int alphabetSize, int depth) {
    init(alphabetSize, depth, VolfNode.DEFAULT_ALPHA_FACTOR);
  }

  public double learn(int symbol, int symSetLabel) {
    double res = ctwRoot.learn(symbol, symSetLabel, context.getIterator());
    context.add(symbol);
    return res;
  }

// don't loose context
  public double predict(int symbol, int symSetLabel) {
    double res = ctwRoot.predict(symbol, symSetLabel, context.getIterator());
    context.add(symbol);
    return res;
  }

}
