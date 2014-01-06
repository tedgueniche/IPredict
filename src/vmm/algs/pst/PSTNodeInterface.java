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

import java.io.File;
import java.nio.ByteBuffer;

import vmm.util.ContextIterator;

public interface PSTNodeInterface {
       String getString();
       double predict(char ch);
       double predict(CharSequence ch);

       double predict(byte b);
       double predict(byte []bytes);

       /**
        * @param Assert param length is this.getAlphabetSizeI()
        * initializes pArr s.a. pArr[sym] = this node sym prediction
        */
       void predict(double[] pArr);

       /**
        * @returns PSTNodeInterface corresponds to the largest suffix of the string defined
        *          by seq.
        */
       PSTNodeInterface get(byte[] byteSeq);//descended
       PSTNodeInterface get(CharSequence seq);//descended
       PSTNodeInterface get(ContextIterator contextIter);//descended
       PSTNodeInterface get(char symbol);//child

       void insert(char symbol, double[] nextSymProbability);

       /**
        * @return this node sub tree height
        */
       int subTreeHeight();

       int getAlphabetSize();


       PSTNodeInterface load(File source);
       void save(File dest);
}
