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
 * Created on Apr 29, 2003
 */

package vmm.algs.lzms;


public class ModelRank {
	public static final double stdMultNormalization = 4.0;

	final byte minContext;
	final byte numShift;
	final byte stdMult;

	public ModelRank(int minContext, int numShift, int stdMult) {
		this.minContext = (byte)minContext;
		this.numShift = (byte)numShift;
		this.stdMult = (byte)stdMult;
	}

	public ModelRank(int minContext, int numShift, double stdMult) {
		this.minContext = (byte)minContext;
		this.numShift = (byte)numShift;
		this.stdMult = (byte)(stdMult * stdMultNormalization);
	}

	public byte getMinContext() {
		return minContext;
	}

	public byte getNumShift() {
		return numShift;
	}

	public double getOriginalStdMult() {
		return stdMult/stdMultNormalization;
	}
}
