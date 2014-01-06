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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>Title: Probabilistic Suffix Tree</p>
 * <p>Description: Initializing the PST algorithm</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Foretell</p>
 *
 * implementation notes: measuring size as int instead of long
 * @author Ron Begleiter
 * @version 1.0
 */

public class Samples {

  private FileChannel []samples;
  private String []samplesNames;
  private String samplesPath;

  private ByteBuffer bbuf;
  private int bufSampleIndex;         //sample index

  public Samples() {
    samples = new FileChannel[0];
  }

  public void init(String SamplesPath){
    this.samplesPath = SamplesPath;
    initSamples();
    bbuf = ByteBuffer.allocateDirect(0);
    bufSampleIndex = -1;
  }

  /**
   * inits with sample of index i of samples
   */
  public void init(Samples sourceSamples, int i){
    samples = new FileChannel[1];
    samplesNames = new String[1];

    samples[0] = sourceSamples.samples[i];
    samplesNames[0] = sourceSamples.samplesNames[i];
    bbuf = ByteBuffer.allocateDirect(0);
    bufSampleIndex = -1;
  }

  public void disposeAll(){
    for(int i=0; i<samples.length; ++i){
      try{
        samples[i].close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public String toString(int sampleInd){
    return samplesNames[sampleInd];
  }

  public byte get(int sampleIndex, int index){
   if(sampleIndex!=bufSampleIndex){
      try{
        //see implementation note (1)
        bbuf = ByteBuffer.allocate((int)samples[sampleIndex].size());
        samples[sampleIndex].read(bbuf,0);
      }
      catch(IOException e){
        e.printStackTrace();
      }
      bufSampleIndex = sampleIndex;
    }

    return bbuf.get(index);
  }

  public int size(int sampleIndex){
    int ret = 0;
    try{
      ret = (int)samples[sampleIndex].size();
    }
    catch(IOException ioe){
      ioe.printStackTrace();
      ret = -1;
    }
    return ret;
  }

  public int size(){
    return samples.length;
  }

  public int allLength(){
    int ret = 0;
    for(int i=0; i<samples.length; ++i){
      ret += size(i);
    }
    return ret;
  }

  private void initSamples(){
    File f = new File(samplesPath);
    if(f.isDirectory()){
      initFromDir(f);
    }
    else{
      initFromFile(f);
    }

  }

  private void initFromDir(File dir){
    File[] files = dir.listFiles();

    samples = new FileChannel[files.length];
    samplesNames = new String[files.length];
    FileInputStream fin = null;
    for(int i=0; i<files.length; ++i){
      try {
        samplesNames[i] = files[i].toString();
        fin = new FileInputStream(files[i]);
        samples[i] = fin.getChannel();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  private void initFromFile(File sameplsFile){
    samples = new FileChannel[1];
    samplesNames = new String[1];
    FileInputStream fin = null;
    try{
      samplesNames[0] = sameplsFile.toString();
      fin = new FileInputStream(sameplsFile);
      samples[0] = fin.getChannel();
    }
    catch(IOException ex){
      ex.printStackTrace();
    }
  }


  /** T E S T -- M A I N **
  public static void main(String[] args) {
    Samples samples1 = new Samples();
    samples1.init("./files/gutenberg");
    System.out.println("---- TEST -----");
    for(int size=samples1.size(), i=0; i<size;++i){
      for(int j=0; j<samples1.size(i); ++j){
        System.out.print((char)samples1.get(i,j));
      }
    }
  }*/

}
