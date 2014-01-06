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
 
package vmm.algs.pst;

import vmm.util.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/*!*/

/**
 * Title:
 * Description:
 *               Updated: get(CharSequence str)
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author
 * @version 1.2
 */

public class DefaultPSTNode implements PSTNodeInterface, Serializable{

  private static final String XML_NODE_ST = "<node>";
  private static final String XML_NODE_ET = "</node>";
  private static final String TOSTRING_INIT = "\n ";
  private static final String XML_EXCEP_AND = "&";
  private static final String XML_EXCEP_LT = "<";
  private static final String XML_ID_CDATA_ST = "<id><![CDATA[";
  private static final String XML_ID_CDATA_ET = "]]></id>";
  private static final String XML_ID_ST = "<id>'";
  private static final String XML_ID_ET = "'</id>";
  private static final String XML_PROB_ST = "<probability>";
  private static final String XML_PROB_ET = "</probability>";
  private static final String XML_PROB_CDATA_ST = "<probability><![CDATA[";
  private static final String XML_PROB_CDATA_ET = "]]></probability>";
  private static final String XML_CHILDREN_ST ="<children>";
  private static final String XML_CHILDREN_ET ="</children>";

  private static final int ABSIZE = 256;


  public String idStr;/*ZZZ*/
  public double[] nextSymProbability;/*ZZZ*/
  public DefaultPSTNode[] children;/*ZZZ*/
  public boolean isLeaf;/*ZZZ*/

  private int absize;


  public DefaultPSTNode(){
    idStr = "";
    absize = ABSIZE;
    nextSymProbability = new double[ABSIZE];
    children  = new DefaultPSTNode[ABSIZE];/*ZZZ*/
    isLeaf = true;
  }

  public DefaultPSTNode(int alphabetSize){
    idStr = "";
    absize = alphabetSize;
    nextSymProbability = new double[absize];
    children  = new DefaultPSTNode[absize];
    isLeaf = true;
  }


  public DefaultPSTNode(String idStr, double[] nextSymProbability) {
         this.idStr = idStr;
         this.nextSymProbability = nextSymProbability;
         absize = nextSymProbability.length;
         this.children = new DefaultPSTNode[absize];
  }

  public int getAlphabetSize(){
    return absize;
  }

  public String getString(){
         return idStr;
  }

  public double predict(char ch){
    return nextSymProbability[ch];
  }

  public double predict(CharSequence charSeq){
    double prediction = 1;
    PSTNodeInterface node;
    for(int i=0,test=charSeq.length(); i<test; ++i){
      node = get(charSeq.subSequence(0,i));
      prediction *= node.predict(charSeq.charAt(i));
    }
    return prediction;
  }

  public double predict(byte b){
    return predict((char)UnsignedByteConverter.value(b));
  }

  /**
   * @param Assert param length is this.getAlphabetSizeI()
   * initializes pArr s.a. pArr[sym] = this node sym prediction
   */
  public void predict(double[] pArr){
    if(pArr.length!=absize){
      throw new RuntimeException("ILL pArr size in predict");
    }
    System.arraycopy(nextSymProbability,0,pArr,0,absize);
  }


  public double predict(byte []bytes){
    double prediction = 1;
    PSTNodeInterface node;
    System.out.println("-- prediction --");
    double p = 0;
    for(int i=0, test=bytes.length; i<test; ++i){
      node = get(bytes,i-1);
      p = node.predict(bytes[i]);

      prediction *= p;
      System.out.println(i+") "+p+" "+node.getString());
    }
    System.out.println("--");
    return prediction;
  }

  /**
   * @returns PSTNodeInterface corresponds to the largest suffix of the string defined
   *          by seq.
   */
  public PSTNodeInterface get(CharSequence seq){
    StringBuffer sbuff = new StringBuffer(seq.toString());
    return get(sbuff);
  }

  /**
   * @returns PSTNodeInterface corresponds to the largest suffix of the byteSeq
   */
  public PSTNodeInterface get(byte []byteSeq){
    return this.get(byteSeq, byteSeq.length-1);
  }

  public PSTNodeInterface get(ContextIterator context){
    if(context.hasNext()){
      int symbol = context.nextSymbol();
      return (children[symbol]!=null)?
          children[symbol].get(context) :
          this;
    }
    else{
      return this;
    }
  }

  public PSTNodeInterface get(StringBuffer sbuff){
    int strLen = sbuff.length();
    if(strLen==0){
      return this;
    }
    else{
      int nextSymIndex = sbuff.charAt(strLen-1);
      sbuff.setLength(strLen-1);
      if(children[nextSymIndex]!=null){
        PSTNodeInterface ret = children[nextSymIndex].get(sbuff);
        sbuff.setLength(strLen);
        return ret;
      }
      else{// therefore this corresponds to the largest suffix.
        sbuff.setLength(strLen);
        return this;
      }
    }
  }


  public PSTNodeInterface get(char symbol){
         return children[symbol];
  }

  private PSTNodeInterface get(byte []bytes, int currentByteIndex){
    if(currentByteIndex==-1){
      return this;
    }
    else{
      int nextSymIndex = UnsignedByteConverter.value(bytes[currentByteIndex]);
      if(children[nextSymIndex]!=null){
        PSTNodeInterface ret = children[nextSymIndex].get(bytes, currentByteIndex-1);
        return ret;
      }
      else{// therefore this corresponds to the largest suffix.
        return this;
      }
    }
  }

  public void insert(char symbol, double[] nextSymProbability){
         if(isLeaf){
           isLeaf = false;
           children = new DefaultPSTNode[absize];
         }
         DefaultPSTNode newNode = new DefaultPSTNode(symbol+idStr,nextSymProbability);
         children[symbol] = newNode;
  }

  public PSTNodeInterface load(File source){
    try{
      FileInputStream fin = new FileInputStream(source);
      ObjectInputStream in = new ObjectInputStream(fin);
      readObject(in);
      in.close();
    }catch(Exception e){
      e.printStackTrace();
    }
    return this;
  }

  public void save(File dest){
    try{
      FileOutputStream fout = new FileOutputStream(dest);
      ObjectOutputStream out = new ObjectOutputStream(fout);
      writeObject(out);
      out.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public String toString(){
         StringBuffer toString = new StringBuffer(1024);
         StringBuffer tabs = new StringBuffer(TOSTRING_INIT);
         for(int i=0, test=idStr.length(); i<test; ++i)
           tabs.append('\t');

         toString.append(XML_NODE_ST);
         if(idStr.indexOf(XML_EXCEP_AND)>=0||idStr.indexOf(XML_EXCEP_LT)>=0){
            toString.append(XML_ID_CDATA_ST).append(idStr).append(XML_ID_CDATA_ET);
         }
         else{
            toString.append(XML_ID_ST).append(idStr).append(XML_ID_ET);
         }
         toString.append(XML_PROB_CDATA_ST).append(prob2Str()).append(XML_PROB_CDATA_ET);
         toString.append(XML_CHILDREN_ST);
         for(int i=0,test=children.length; i<test; ++i){
                 if(children[i]!=null){
                    toString.append(tabs);
                    toString.append(children[i]);
                 }
         }
         toString.append(XML_CHILDREN_ET);
         toString.append(XML_NODE_ET);
         return toString.toString();
  }

  /**
   * @return this node sub tree height
   */
  public int subTreeHeight(){
    int height = 0;
    if(isLeaf){
      return 0;
    }
    else{
      for (int i = 0, childH; i < children.length; ++i) {
        if (children[i] != null) {
          childH = children[i].subTreeHeight();
          height = Math.max(height, childH);
        }
      }
      return height + 1;
    }
  }

  private String prob2Str(){
    StringBuffer sbuff = new StringBuffer(nextSymProbability.length*2+2);
    sbuff.append('[');
    for(int i=0; i<nextSymProbability.length; ++i){
      if(nextSymProbability[i]!=0){
        sbuff.append((char)i).append('=').append(nextSymProbability[i]).append(',');
      }
    }
    sbuff.setCharAt(sbuff.length()-1,']');
    return sbuff.toString();
  }


  private void writeObject(java.io.ObjectOutputStream out) throws IOException{
    out.writeObject(idStr);
    out.writeInt(nextSymProbability.length);
    for(int i=0; i<nextSymProbability.length; ++i)
      out.writeDouble(nextSymProbability[i]);
    out.writeInt(children.length);
    Vector v = new Vector(children.length);
    Vector vInd = new Vector(children.length);
    for(int i=0; i<children.length; ++i){
      if(children[i]!=null){ v.add(children[i]); vInd.add(new Integer(i)); }
    }
    out.writeInt(v.size());
    for(int i=0, test=v.size(); i<test; ++i){
      out.writeInt(((Integer)vInd.elementAt(i)).intValue());
      out.writeObject(v.elementAt(i));
    }

    out.writeObject(v);
  }
  private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException{
   idStr = (String)in.readObject();
   int nsPLen = in.readInt();
   nextSymProbability = new double[nsPLen];
   for(int i=0; i<nsPLen; ++i)
     nextSymProbability[i] = in.readDouble();
   int childLen = in.readInt();
   children = new DefaultPSTNode[childLen];
   int elmSize = in.readInt();
   for(int i=0,ind=0; i<elmSize; ++i){
     ind = in.readInt();
     children[ind] = (DefaultPSTNode)in.readObject();
   }
 }

}
