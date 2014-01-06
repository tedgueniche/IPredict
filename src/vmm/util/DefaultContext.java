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
 * <p>Description: Default Context implementation</p>
 * Note that this implementation is not iteration safe!
 * meaning that only one iteration can be done at a time,
 * and while itterating one should not add a symbol to this context.
 * @see getIterator
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DefaultContext implements Context, ContextIterator{
  private static final int ILL_INDEX = -1;

  /*Context collection, a symbol is access by its int id.
     ie ASCII id.
   */
  private int []context;
  private int nextAddIndex;

  /*Indicates the size of the population in context[]*/
  private int population;

  /*Iteration index*/
  private int iterInd;
  private int iterCount;

  public DefaultContext(int maxlength) {
    _assert(maxlength > 0, "context length<=0");
    context = new int[maxlength];
    nextAddIndex = 0;
    population = 0;
    iterInd = 0;
    iterCount = 0;
  }

  public void add(int symbol){
    context[nextAddIndex] = symbol;
    nextAddIndex = (nextAddIndex+1)%context.length;
    population = isFull()? population : population+1;
  }

  /*Note: this implemntation is not safe*/
  public ContextIterator getIterator(){
    iterInd = indexBefore(nextAddIndex);
    iterCount = 0;
    return this;
  }

  public boolean hasNext(){
    return iterCount != population;
  }

  public int nextSymbol(){
    int sym = context[iterInd];
    iterInd = indexBefore(iterInd);
    iterCount++;
    return sym;
  }

  private int indexBefore(int index){
    return (index==0)? context.length-1 : (index-1);
  }

  private boolean isFull(){
    return (population==context.length);
  }

  private void _assert(boolean condition, String descp) {
    if (false == condition) {
      throw new RuntimeException("Assertion Failed: " + descp);
    }
  }

  public static void main(String [] args){
    Context c = new DefaultContext(3);
    c.add(1);c.add(2);c.add(3);c.add(4);c.add(5);
    ContextIterator iter = c.getIterator();
    while(iter.hasNext()){
      System.out.println(""+(int)iter.nextSymbol());
    }
  }
}
