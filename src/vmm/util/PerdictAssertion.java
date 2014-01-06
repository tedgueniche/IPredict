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
 
package vmm.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PerdictAssertion {
  private static final double THRESHOLD = 0.00001;
  private PerdictAssertion() {
  }

  public static void assertThis(double[] pArr) throws RuntimeException {
    double sum = 0.0;
    for (int i = 0; i < pArr.length; ++i) {
      sum += pArr[i];
    }
    if (sum <1.0-THRESHOLD||sum>1.0+THRESHOLD) {
      System.out.println("pArr len=" + pArr.length);
      System.out.println("pArr sum=" + sum);
      for (int i = 0; i < pArr.length; ++i) {
        System.out.print("["+pArr[i]+"] ");
      }
      throw new RuntimeException("Predict Assertion Failed!! sum is " + sum);
    }
  }

}
