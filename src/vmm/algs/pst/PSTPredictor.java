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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * <p>Title: Probabilistic Suffix Tree</p>
 * <p>Description: Initializing the PST algorithm</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Foretell</p>
 * @author Ron Begleiter
 * @version 1.0
 */

public class PSTPredictor {
  private static final String PST_ARCHIVE_FILE = "./files/pst/gutenberg.arc";
  private static final String PREDICTION_TARGET = "./files/calgary/book1.txt";

  private PSTPredictor() {
  }

  public static void main(String[] args) {
    System.out.println(" -- loading pst from "+PST_ARCHIVE_FILE+" --");
    PSTNodeInterface pst = new DefaultPSTNode().load(new File(PST_ARCHIVE_FILE));
    System.out.println(" ---\n");
    System.out.println(" -- prediction --");
    System.out.println("START> "+Calendar.getInstance().getTime());
    try {
      FileInputStream fin = new FileInputStream(PREDICTION_TARGET);
      FileChannel in = fin.getChannel();
      ByteBuffer bbuff = ByteBuffer.allocate((int)in.size());
      in.read(bbuff,0);
      AsciiCharSequence ascii = new AsciiCharSequence(bbuff);
      double prediction = pst.predict(ascii);
      System.out.println("prediction="+prediction+" size="+
                         (-Math.log(prediction)/Math.log(2.0)));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

}
