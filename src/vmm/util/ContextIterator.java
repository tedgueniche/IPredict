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
 * <p>Title: ContextIterator</p>
 * <p>Description:
 * Iterates a specific <code>Context</code>.
 * Example:
 *  Context = <X1,X2,..,Xt>
 *  Iteration first symbol will be Xt and last symbol
 *   (for which hasNext()==true) is X1.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Ron Begleiter
 * @version 1.0
 */
public interface ContextIterator {
  boolean hasNext();
  int nextSymbol();
}
