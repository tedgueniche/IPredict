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
 * <p>Title: Context</p>
 * <p>Description: Multi-Alphabet Fixed Length Context
 * Each symbol identity is an integer (f: N-->AB).
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Ron Begleiter
 * @version 1.0
 */

public interface Context {

  /**
   * Adds symbol to this Context
   */
  void add(int symbol);
  /**
   * @returns a <code>ContextIterator</code> over this Context
   */
  ContextIterator getIterator();
}
