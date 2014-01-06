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

import vmm.util.*;
import java.util.*;


/**
 * <p>Title: DecompositionTreeBuilder</p>
 * <p>Description: Builds a Decomposition Tree from Samples</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author ron
 * @version 1.0
 */

public class DecompositionTreeBuilder {
  private int absize;
  private int softModelDepth;

  public DecompositionTreeBuilder(int absize, int softModelDepth) {
    this.absize = absize;
    this.softModelDepth = softModelDepth;
  }


  /**
   * Build dynamic
   */
  public DecompositionNode build(SampleIterator sample){
    SortedMap countMap = calculateAbCount(sample);

    AbsBinaryDNode root = buildHuf(countMap);
    countMap = null;
    System.gc();

    SortedMap levelsMap = root.getDescendantsByLevel();
    root = null;

    Integer level, sym1, sym2, superSym;
    SortedMap oneLevel;
    AbsBinaryDNode node1, node2, superNode;

    while((levelsMap.size()>1)
          ||
          ((levelsMap.size()==1)&&((SortedMap)levelsMap.get
            (levelsMap.lastKey())).size()>1)){

      level = (Integer)levelsMap.lastKey();
      oneLevel = (SortedMap)levelsMap.get(level);

      sym1 = (Integer)oneLevel.lastKey();
      node1 = (AbsBinaryDNode)oneLevel.get(sym1);
      oneLevel.remove(sym1);
      sym2 = (Integer)oneLevel.lastKey();
      node2 = (AbsBinaryDNode)oneLevel.get(sym2);
      oneLevel.remove(sym2);
      if(oneLevel.size()==0){
        levelsMap.remove(level);
      }

      superNode = new DynamicBinDNode(absize, node1, node2,softModelDepth);

      if(node2 instanceof BinDLeaf){
        superSym = sym2;
      }
      else if (node1 instanceof BinDLeaf){
        superSym = sym1;
      }
      else{
        superSym = sym2;
      }

      level = new Integer(level.intValue()-1);
      if(levelsMap.get(level)==null) levelsMap.put(level, new TreeMap());
      oneLevel = (SortedMap)levelsMap.get(level);
      oneLevel.put(superSym, superNode);
    }

    oneLevel = (SortedMap)levelsMap.get(levelsMap.lastKey());

    System.gc();

    return (DecompositionNode)oneLevel.get(oneLevel.lastKey());
  }



  /***/
  /**
 * build static
 * (should never use code replication again..)
 */
public StaticDecompositionNode buildStatic(SampleIterator samples){
  SortedMap countMap = calculateAbCount(samples);

  AbsBinaryDNode root = buildHuf(countMap);
  countMap = null;
  System.gc();

  SortedMap levelsMap = root.getDescendantsByLevel();
  root = null;

  Integer level, sym1, sym2, superSym;
  SortedMap oneLevel;
  AbsBinaryDNode node1, node2, superNode;

  while((levelsMap.size()>1)
        ||
        ((levelsMap.size()==1)&&((SortedMap)levelsMap.get
          (levelsMap.lastKey())).size()>1)){

    level = (Integer)levelsMap.lastKey();
    oneLevel = (SortedMap)levelsMap.get(level);

    sym1 = (Integer)oneLevel.lastKey();
    node1 = (AbsBinaryDNode)oneLevel.get(sym1);
    oneLevel.remove(sym1);
    sym2 = (Integer)oneLevel.lastKey();
    node2 = (AbsBinaryDNode)oneLevel.get(sym2);
    oneLevel.remove(sym2);
    if(oneLevel.size()==0){
      levelsMap.remove(level);
    }

    superNode = new StaticBinDNode(absize, node1, node2, softModelDepth);

    if(node2 instanceof BinDLeaf){
      superSym = sym2;
    }
    else if (node1 instanceof BinDLeaf){
      superSym = sym1;
    }
    else{
      superSym = sym2;
    }

    level = new Integer(level.intValue()-1);
    if(levelsMap.get(level)==null) levelsMap.put(level, new TreeMap());
    oneLevel = (SortedMap)levelsMap.get(level);
    oneLevel.put(superSym, superNode);
  }

  oneLevel = (SortedMap)levelsMap.get(levelsMap.lastKey());

  System.gc();

  return (StaticDecompositionNode)oneLevel.get(oneLevel.lastKey());
}

  /***/


  private AbsBinaryDNode buildHuf(SortedMap countMap){
    TreeMap lowCount, superCountVal;
    AbsBinaryDNode node1, node2;
    Integer count1, count2;
    DynamicBinDNode dnode;
    Integer superSym, superCount;

    while((countMap.size()>1)||
          ((((TreeMap)countMap.get(countMap.firstKey())).size()>1)&&
           (countMap.size()==1)
           )
          ){
      lowCount = (TreeMap)countMap.get(countMap.firstKey());//lowest key @see SortedMap
      count1 = (Integer)countMap.firstKey();

      superSym = (Integer)lowCount.lastKey();
      node1 = (AbsBinaryDNode)lowCount.get(lowCount.lastKey());
      lowCount.remove(lowCount.lastKey());

      if(lowCount.size()==0){
        countMap.remove(countMap.firstKey());
        lowCount = (TreeMap)countMap.get(countMap.firstKey());
      }
      count2 = (Integer)countMap.firstKey();

      node2 = (AbsBinaryDNode)lowCount.get(lowCount.lastKey());

      if(superSym.compareTo((Integer)lowCount.lastKey()) < 0){
            superSym =  (Integer)lowCount.lastKey();
            swap(node2, node1);
      }

      lowCount.remove(lowCount.lastKey());
      if(lowCount.size()==0){
        countMap.remove(countMap.firstKey());
      }

      dnode = new DynamicBinDNode(absize,node1,node2, softModelDepth);

      superCount = new Integer(count1.intValue()+count2.intValue());
      superCountVal = (countMap.get(superCount)!=null)?
          (TreeMap)countMap.get(superCount) : new TreeMap();

      superCountVal.put(superSym, dnode);
      countMap.put(superCount, superCountVal);
    }
    superCountVal = (TreeMap)countMap.get(countMap.firstKey());
    return (AbsBinaryDNode)superCountVal.get(superCountVal.firstKey());
  }



  /*****/

  /*****/



  private SortedMap calculateAbCount(SampleIterator sample){
    int countArr[] = new int[absize];
    Arrays.fill(countArr, 0);

    while(sample.hasNext()) {
      countArr[sample.next()]++;
    }

    TreeMap countMap = new TreeMap();

    Integer sym, count;
    for(int i=0; i<countArr.length; ++i){
      sym = new Integer(i);
      count = new Integer(countArr[i]);
      if(!countMap.containsKey(count)){
        countMap.put(count,new TreeMap());
      }
      ((TreeMap)countMap.get(count)).put(sym, new BinDLeaf(sym.intValue(), absize));
    }

    return countMap;
  }


  private void swap(AbsBinaryDNode n1, AbsBinaryDNode n2){
    AbsBinaryDNode tmp = n1;
    n1 = n2;
    n2 = tmp;
    tmp = null;
  }


}
