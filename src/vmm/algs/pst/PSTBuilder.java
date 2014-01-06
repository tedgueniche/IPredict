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

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import vmm.util.*;

/**
 * <p>Title: Probabilistic Suffix Tree</p>
 * <p>Description: Initializing the PST algorithm</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Foretell</p>
 * @author Ron Begleiter
 * @version 1.0
 *
 * Notes:
 * 1)
 */

public class PSTBuilder {

  private static final int ALPHABET_RANGE = 256;//ascii
  private static final int S_INITIAL_SIZE = 1024;//ascii
  private static final int UNSIGNED_BYTE_MASK = 0xFF;

  private int alphabetSize;
  private boolean []seenAlphabet;
  private int strHits;
  private int []strCharHits;
  private int []charStrHits;

  private int []charStrHitsPerSample;/*@ron 1/03 for Golan's*/

  private Samples samples;

  //seperated to save object creation.
  private List queryStrs;
  private List suffStrNextSymProb;
  private PSTNodeInterface pstRoot;

  public PSTBuilder() {
    strHits = 0;
    alphabetSize = ALPHABET_RANGE;
    seenAlphabet = new boolean[ALPHABET_RANGE];
    strCharHits = new int[ALPHABET_RANGE];
    charStrHits = new int[ALPHABET_RANGE];
  }

  public PSTBuilder(int abSize) {
    strHits = 0;
    alphabetSize = abSize;
    seenAlphabet = new boolean[alphabetSize];
    strCharHits = new int[alphabetSize];
    charStrHits = new int[alphabetSize];
    charStrHitsPerSample = new int[alphabetSize];
  }


  public PSTNodeInterface build(Samples samples, double pMin, double alpha,
                                double nextSymProbMin, double addedValThreshold,
                                int strMaxLength){
     this.samples = samples;
     init(pMin, nextSymProbMin);

     //start building the PST
     String str;
     double []strNSymProb;
     double []suffStrNSymProb;

     while(queryStrs.size()>0){
       str = (String)queryStrs.remove(0);
       suffStrNSymProb = (double[])suffStrNextSymProb.remove(0);

       initHitCounts(str);
       strNSymProb = computeNextSymProb();

       if(isConditionB(strNSymProb, suffStrNSymProb, alpha,
                        nextSymProbMin, addedValThreshold)){
         addToTree(str, strNSymProb, nextSymProbMin);
       }
       if(str.length()<strMaxLength)
         updateQueryStrs(str, strNSymProb, pMin);
     }

     return pstRoot;
  }

  /** @ron 1/03
   * Golan's PST version.
   *
   * @param samples
   * @param pMin
   * @param alpha
   * @param nextSymProbMin
   * @param addedValThreshold
   * @param strMaxLength
   * @return
   */
  public PSTNodeInterface buildGolanPST(Samples samples, int nMin, int minNumHits,
                                double nextSymProbMin, double r,
                                int strMaxLength){
    this.samples = samples;
    initGolan(nextSymProbMin);

    //start building the PST
    String str;
    double []strNSymProb;
    double []suffStrNSymProb;

    while(queryStrs.size()>0){
      str = (String)queryStrs.remove(0);
      suffStrNSymProb = (double[])suffStrNextSymProb.remove(0);

      initHitCounts(str);
      strNSymProb = computeNextSymProb();

      if(isConditionBGolan(strNSymProb, suffStrNSymProb, minNumHits, r)){
        addToTree(str, strNSymProb, nextSymProbMin);
      }
      if(str.length()<strMaxLength)
        updateQueryStrsGolan(str, strNSymProb, nMin);
    }

    return pstRoot;
  }


  private void init(double pMin, double nextSymProbMin){
    if(nextSymProbMin*alphabetSize>1){
      throw new PSTException("ILL - smooth :  gamma*|alphabet|>1");
    }

    //seperated to save object creation.
    queryStrs = new ArrayList(S_INITIAL_SIZE);
    suffStrNextSymProb = new ArrayList(S_INITIAL_SIZE);

    int numOfSamples = samples.size();
    int allLength = samples.allLength();

    Arrays.fill(strCharHits,0);

    for(int val = 0,sampleSize=0,sampleID=0; sampleID<numOfSamples; ++sampleID){
      sampleSize = samples.size(sampleID);
      for(int i=0; i<sampleSize;++i){
        val = samples.get(sampleID,i)&UNSIGNED_BYTE_MASK;
        seenAlphabet[val] = true;
        strCharHits[val]++;
      }
    }

    //init seenALPHABET & queryStrs
    double []prob = new double[alphabetSize];
    for(int i=0; i<alphabetSize; ++i){
      //if(seenAlphabet[i]) alphabetSize++;
      prob[i] = strCharHits[i]/(double)allLength;
      if(prob[i]>pMin){
        queryStrs.add(""+(char)i);
        suffStrNextSymProb.add(prob);
      }
    }

    double[] rootsProb = new double[alphabetSize];
    System.arraycopy(prob,0,rootsProb,0,alphabetSize);
    pstRoot = createPSTRoot(smooth(rootsProb, nextSymProbMin));

  }


  private void initGolan(double nextSymProbMin){
      if(nextSymProbMin*alphabetSize>1){
        throw new PSTException("ILL - smooth :  gamma*|alphabet|>1");
      }

      //seperated to save object creation.
      queryStrs = new ArrayList(S_INITIAL_SIZE);
      suffStrNextSymProb = new ArrayList(S_INITIAL_SIZE);

      int numOfSamples = samples.size();
      int allLength = samples.allLength();

      Arrays.fill(strCharHits,0);

      for(int val = 0,sampleSize=0,sampleID=0; sampleID<numOfSamples; ++sampleID){
        sampleSize = samples.size(sampleID);
        for(int i=0; i<sampleSize;++i){
          val = samples.get(sampleID,i)&UNSIGNED_BYTE_MASK;
          seenAlphabet[val] = true;
          strCharHits[val]++;
        }
      }

      //init seenALPHABET & queryStrs
      double []prob = new double[alphabetSize];
      for(int i=0; i<alphabetSize; ++i){
        prob[i] = ((double)strCharHits[i])/allLength;
        queryStrs.add(""+(char)i);
        suffStrNextSymProb.add(prob);
      }

      double[] rootsProb = new double[alphabetSize];
      System.arraycopy(prob,0,rootsProb,0,alphabetSize);
      pstRoot = createPSTRoot(smooth(rootsProb, nextSymProbMin));

    }


  private boolean isConditionB(double []StrNSymProb, double []suffStrNSymProb,
                               double alpha, double nextSymProbMin,
                               double addedValThreshold){
    double factor = 0;
    for (int i=0; i<alphabetSize; ++i){
      if(StrNSymProb[i]>=(1+alpha)*nextSymProbMin){
        factor = StrNSymProb[i]/suffStrNSymProb[i];
        if((factor>=addedValThreshold)||(factor<=1/addedValThreshold)){
          return true;
        }
      }
    }
    return false;
  }

  private boolean isConditionBGolan(double []StrNSymProb, double []suffStrNSymProb,
                                    int numHits, double r){
    double factor = 0;
    for (int i=0; i<alphabetSize; ++i){
      if(strCharHits[i]>numHits){
        factor = StrNSymProb[i]/suffStrNSymProb[i];
        if((factor>=r)||(factor<=1/r)){
          return true;
        }
      }
    }
    return false;
  }

  private void updateQueryStrsGolan(String str, double []nextSymProb, int nMin){

    for(int i=0; i<alphabetSize; ++i){
      if( charStrHitsPerSample[i]> nMin){
        queryStrs.add(((char)i)+str);
        suffStrNextSymProb.add(nextSymProb);
      }
    }
  }


  private void updateQueryStrs(String str, double []nextSymProb,double pMin){
    int allPossibleMatches = 0;

    for(int i=0,test=samples.size(),chStrLen=str.length()+1; i<test;++i){
      allPossibleMatches += samples.size(i)-chStrLen+1;
    }
    for(int i=0; i<alphabetSize; ++i){
      if((double)charStrHits[i]/allPossibleMatches>=pMin){
        queryStrs.add(((char)i)+str);
        suffStrNextSymProb.add(nextSymProb);
      }
    }
  }

  private double[] smooth(double []prob, double nsMinP){
    double factor=1-prob.length*nsMinP;
    for(int i=0; i<prob.length; ++i){
      //if(seenAlphabet[i]){
        prob[i] = factor*prob[i]+nsMinP;
      //}
    }
    return prob;
  }

  private PSTNodeInterface createPSTRoot(double[] nextSymProb){
    return new DefaultPSTNode("", nextSymProb);
  }

  private void addToTree(String str,double [] strNSymProb,
                         double nextSymProbMin){
    PSTNodeInterface deepestNode = pstRoot.get(str);
    if(deepestNode.getString().length()==str.length()-1){
      deepestNode.insert(str.charAt(0), smooth(strNSymProb, nextSymProbMin));
    }
    else{
      int[] savedStrChHits = new int[strCharHits.length];
      int[] savedChStrHits = new int[charStrHits.length];
      System.arraycopy(charStrHits,0,savedChStrHits,0,charStrHits.length);
      System.arraycopy(strCharHits,0,savedStrChHits,0,strCharHits.length);

      double []prob;
      for(int i=str.length()-deepestNode.getString().length()-1; i>-1;
          deepestNode=deepestNode.get(str.charAt(i)), --i){
        initHitCounts(str.substring(i));
        prob = computeNextSymProb();
        deepestNode.insert(str.charAt(i),smooth(prob,nextSymProbMin));
      }

      System.arraycopy(savedChStrHits,0,charStrHits,0,charStrHits.length);
      System.arraycopy(savedStrChHits,0,strCharHits,0,strCharHits.length);
    }
  }

  private void initHitCounts(String str){
    Arrays.fill(strCharHits,0);
    Arrays.fill(charStrHits,0);
    strHits = 0;

    /*@ron 1/03*/
    Arrays.fill(charStrHitsPerSample, 0);
    boolean isUpdatePerSample[] = new boolean[charStrHitsPerSample.length];
    /*@ron 1/03*/

    byte []strBytes = str.getBytes();
    for(int sampleID=0,numOfSamples=samples.size(); sampleID<numOfSamples; ++sampleID){
      Arrays.fill(isUpdatePerSample, true); /*@ron 1/03*/
      for (int sampleSize=samples.size(sampleID),loopTest=sampleSize-strBytes.length,j=0,i=0;
            i<loopTest; ++i) {
         for(j=0; j<strBytes.length; ++j){
           if (samples.get(sampleID, i+j)!= strBytes[j]) break;
         }
         if(j==strBytes.length){
           strHits++;
           if (i+j<sampleSize)
             strCharHits[samples.get(sampleID, i+j)&UNSIGNED_BYTE_MASK]++;
           if (i>0) {
             int charId = samples.get(sampleID, i - 1) & UNSIGNED_BYTE_MASK;
             charStrHits[charId]++;
             if(isUpdatePerSample[charId]) { /*@ron 1/03*/
               isUpdatePerSample[charId] = false;
               charStrHitsPerSample[charId]++;
             } /*@ron 1/03*/
           }
         }
       }
    }
  }

  private double[] computeNextSymProb(){
    double []retVal = new double[strCharHits.length];
    int strCharAll = 0;
    for(int i=0; i<strCharHits.length; ++i){
      strCharAll += strCharHits[i];
    }
    for(int i=0; i<retVal.length; ++i){
      retVal[i] = (double)strCharHits[i]/strCharAll;
    }
    return retVal;
  }


  /*--- MAIN ---*/
  public static void main(String[] args) {

    String samplesDir    = "./checkSamples";
    String pstXMLFile    = "./log/t4.xml";
    String pstArcFile    = "./pst/s.arc";
    String toPredictFile = "./corpora/paper5.txt";

    PSTBuilder PSTBuilder1 = new PSTBuilder();
    System.out.println("START> "+Calendar.getInstance().getTime());
    Samples samples = new Samples();
    samples.init(samplesDir);
    PSTNodeInterface pst = null;
    pst = PSTBuilder1.build(samples,/*pMin*/0.001,/*alpha*/0.01,
                           /*nextSymMin*/0.001,/*info threshold*/1.05,/*maxLn*/12);
    System.out.println("\nENDS> "+Calendar.getInstance().getTime());
    try{
      FileOutputStream fout = new FileOutputStream(pstXMLFile);

      fout.write( ("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n").getBytes("ascii"));
      fout.write( ("<pst>").getBytes());
      fout.write(pst.toString().getBytes("ascii"));
      fout.write( ("</pst>").getBytes());
      fout.close();

      pst.save(new File(pstArcFile));
      /*
      DefaultPSTNode pst = new DefaultPSTNode();
      pst.load(new File("./files/pst/pst1.arc"));
      fout = new FileOutputStream("./files/results/t2.xml");

      fout.write( ("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n").getBytes("ascii"));
      fout.write( ("<pst>").getBytes());
      fout.write(pst2.toString().getBytes("ascii"));
      fout.write( ("</pst>").getBytes());
      fout.close();
      */
    }catch(Exception e){
    }

    System.out.println(" -- prediction --");
    System.out.println("START> "+Calendar.getInstance().getTime());
    try {
      FileInputStream fin = new FileInputStream(toPredictFile);
      FileChannel in = fin.getChannel();
      ByteBuffer bbuff = ByteBuffer.allocate((int)in.size());
      in.read(bbuff,0);
      double prediction = pst.predict(bbuff.array());
      System.out.println("prediction total size="+" size="+
                         (-Math.log(prediction)/Math.log(2.0)));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    System.out.println("\nENDS> "+Calendar.getInstance().getTime());
  }

}
