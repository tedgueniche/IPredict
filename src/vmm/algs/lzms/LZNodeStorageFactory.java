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
 
/*
 * Created on Jul 11, 2003
 */

package vmm.algs.lzms;

/**
 * @author Moti
 *
 * LZNodeStorageFactory is used to generate LZNodeStorage
 */
public class LZNodeStorageFactory {

	public static LZNodeStorage create(int alphabetSize) {
          /* @ron try to solve the mem problem in intron-exon
		if (alphabetSize <= 5)
			return new LZNodeArrayStorage(alphabetSize);
          */
		return new LZNodeHashStorage();
	}

}
