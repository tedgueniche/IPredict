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
import vmm.algs.ctw.*;
import vmm.util.Context;

/**
 * <p>Title: AbsDNode</p>
 * <p>Description: Abstract implementation for the DecompositionNode</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public abstract class AbsBinaryDNode
    implements DecompositionNode{

  protected static final int RIGHT = 0;
  protected static final int LEFT  = 1;

  //private static final int SOFTCLASSIFIER_DEPTH = 16;
  private static final int BRANCHING_FACTOR = 2;

  protected DecompVolfNode softClasifier;
  protected BitSet descendants;
  protected AbsBinaryDNode []children;

  protected AbsBinaryDNode(){
    softClasifier = null;
    descendants = null;
    children = null;
  }

  public AbsBinaryDNode(int abSize, AbsBinaryDNode rightChild, AbsBinaryDNode leftChild, int softModelDepth) {
    descendants = new BitSet(abSize);// all bits are initially false
    children = new AbsBinaryDNode[BRANCHING_FACTOR];
    children[RIGHT] = rightChild;
    children[LEFT] = leftChild;

    // -- ASSERT --
    BitSet test = new BitSet(abSize);
    test.or(rightChild.descendants);
    test.and(leftChild.descendants);
    if (test.cardinality()!=0) {
      throw new RuntimeException("ILL decomp node init: duplicated descendant");
    }

    descendants.or(rightChild.descendants);
    descendants.or(leftChild.descendants);

    softClasifier = new DecompVolfNode();
    softClasifier.init(abSize,VolfNode.DEFAULT_ALPHA_FACTOR);
  }

  abstract public double predict(int symbol, Context context);

  public int hashCode(){
    return descendants.hashCode();
  }

  public boolean equals(Object obj){
    try{
      return descendants.equals(((AbsBinaryDNode)obj).descendants);
    }
    catch(ClassCastException cce){
      return false;
    }
  }

  public SortedMap getDescendantsByLevel(){
    SortedMap levelsMap = new TreeMap();
    buildLevels(this, levelsMap, new Integer(0));
    return levelsMap;
  }

  private void buildLevels(AbsBinaryDNode root, SortedMap levelsMap, Integer level){
    if (root instanceof BinDLeaf){
      if(levelsMap.get(level)==null){
        levelsMap.put(level, new TreeMap());
      }
      ((SortedMap)levelsMap.get(level)).put(new Integer(((BinDLeaf)root).symbol()), root);
      return;
    }
    buildLevels(root.children[LEFT], levelsMap, new Integer(level.intValue()+1));
    buildLevels(root.children[RIGHT], levelsMap, new Integer(level.intValue()+1));
  }

}
