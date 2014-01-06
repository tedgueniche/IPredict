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
 * <p>Title: SampleIterator</p>
 * <p>Description: Iterates a sample.
 * A samples is a sequence of symbols.
 * This SampleIterator will iterate in respect to the sample symbol order.
 * Example:
 *  sampleIter  iterates sample == <0,1,0,0,1>
 *  ..
 *  while( sampleIter.hasNext() ){
 *    System.out.print(" "+sampleIter.next());
 *  }
 *  ..
 * output: 0 1 0 0 1
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface SampleIterator {
  /**
   * Indicates wether this SampleIterator has more symbols to iterate.
   * @return true if this sampleIterator has another symbol to iterate
   * otherwise returns false
   */
  boolean hasNext();

  /**
   * Iterates to the next symbol.
   * @return the next iterating symbol
   * @throws NoSuchElementException if there is no such element
   * @todo (1) should do restart whenever finished iteration
   */
  int next();

  /**
   * Restarts iteration.
   * throws RuntimeException if can not restart iteration
   * @todo rem this method see (1)
   */
  void restart();

  /**
   * Number of symbols to iterate
   * @return num of symbols to iterate
   */
  long size();


}
