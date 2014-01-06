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
 
package vmm.algs.oppm;

import vmm.algs.com.colloquial.arithcode.*;

/**
 * Offline PPMC implementation.
 * Using <a href="http://www.colloquial.com/carp/">Bob Carpenter</a> code.
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author <a href="http://www.cs.technion.ac.il/~ronbeg">Ron Begleiter</a>
 * @version 1.0
 */
public class OfflinePPMModel
    extends PPMModel {

  private static int[] allRes = new int[3];
  private boolean isFirstPrediction;

  public OfflinePPMModel(int maxCodeLength, int absize) {
    super(maxCodeLength, absize);
    isFirstPrediction = true;
  }

  public double predict(int symbol) {
    if (isFirstPrediction) {
      isFirstPrediction = false;
      super._buffer = new ByteBuffer(super._maxContextLength + 1);
      super._contextLength = 0;
      super._contextNode = null;
    }

    double p = 1.0;
    while (super.escaped(symbol)) {
      interval(ArithCodeModel.ESCAPE, allRes);
      p *= (allRes[1] - allRes[0]) / (double) allRes[2];
    }
    interval(symbol, allRes);
    return (p * ( (allRes[1] - allRes[0]) / (double) allRes[2]));
  }

  public void use(int symbol) {
    while (super.escaped(symbol)) {
      super.interval(ArithCodeModel.ESCAPE, allRes); // have already done complete walk to compute escape
    }

    super.interval(symbol, allRes);
  }

  // specified in ArithCodeModel
  public void interval(int symbol, int[] result) {
    if (symbol == ArithCodeModel.EOF) {
      _backoffModel.intervalNoIncrement(EOF, result, _excludedBytes);
    }
    else if (symbol == ArithCodeModel.ESCAPE) {
      intervalEscape(result);
    }
    else {
      calcInterval(symbol, result); //will not increment symbol
    }
  }

  /**
   * Clears this OfflinePPMModel's context.
   * As a result the nexts symbol context will be the empty context.
   */
  public void clearContext() {
    super._buffer = new ByteBuffer(super._maxContextLength+1);//new context buffer
    super._contextLength = 0;//empty context length
  }

  /** Returns interval for byte specified as an integer in 0 to 255 range.
   * @param i Integer specification of byte in 0 to 255 range.
   * @param result Array specifying cumulative probability for byte i.
   */
  private void calcInterval(int i, int[] result) {
    if (_contextNode != null) {
      _contextNode.interval(i, _excludedBytes, result);
    }
    else {
      _backoffModel.intervalNoIncrement(i, result, _excludedBytes);

    }
    _buffer.buffer(i);
    _contextLength = Math.min(_maxContextLength, _buffer.length());
    getContextNodeBinarySearch();
    _excludedBytes.clear();
  }
}
