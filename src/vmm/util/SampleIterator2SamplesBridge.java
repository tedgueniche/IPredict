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

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SampleIterator2SamplesBridge extends Samples {

  // PHILIPPE: 2013:  Modified to remove dependency to t.rove
  List<Integer> symList;

  public SampleIterator2SamplesBridge() {

    symList = new ArrayList<Integer>();

  }

  public void init(SampleIterator sampleIter) {

    for (int sym=-1 ;sampleIter.hasNext();) {
      sym = sampleIter.next();
      symList.add(sym);
    }
  }


  public void init(String SamplesPath){
    throw new java.lang.UnsupportedOperationException
        ("Method init(String SamplesPath) not yet implemented.");
  }

  /**
   * inits with sample of index i of samples
   */
  public void init(Samples sourceSamples, int i){
    throw new java.lang.UnsupportedOperationException
        ("Method init(String SamplesPath) not yet implemented.");
  }

  public void disposeAll(){
    symList.clear();
    System.gc();
  }

  public String toString(int sampleInd){
    return "SIter2SBridge";
  }

  /**
   * discarding sampleIndex
   * @param sampleIndex not relevant
   * @param index within this SampleIterator2SamplesBridge
   * @return sym as byte
   * @todo change byte to int!!
   */
  public byte get(int sampleIndex, int index){
    return (byte)symList.get(index).intValue();
  }


  public int size(int sampleIndex){
    if (0==sampleIndex) {
      return symList.size();
    }
    return 0;
 }


  public int size(){
    /*Only one sample*/
    return 1;
  }

  public int allLength(){
    return symList.size();
  }

}
