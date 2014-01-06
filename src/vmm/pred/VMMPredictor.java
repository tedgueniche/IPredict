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

package vmm.pred;

/**
 * <p>Title: On prediction using VMM</p>
 * <p>Description: Variable Markov Model Code</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Technion</p>
 * @author Ron Begleiter
 * @version 1.0
 */

public interface VMMPredictor {

  /**
   * This VMMPredictor use trainingSequence and consturcts its model.
   * @param trainingSequence a sequence
   */
  void learn(CharSequence trainingSequence);

  /**
   * Predicts the next symbol according to some context.
   *
   * @param symbol next symbol
   * @param context a context sequence
   * @return the likelihood prediction
   * @throws VMMNotTrainedException when predicting without training
   */
  double predict(int symbol, CharSequence context);

  /**
   * Predicts the log-likelihood of the testSequence, according
   * to this VMMPredictor model (consturcted upon training sequence) and empty
   * initial context.
   *
   * @param testSequence a sequence
   * @return the -log( Probability (testSequence | trainingSequence) )
   * @throws VMMNotTrainedException when predicting without training
   */
  double logEval(CharSequence testSequence);

  /**
   * Predicts the log-likelihood of the testSequence, according
   * to this VMMPredictor model (consturcted upon training sequence) and
   * initialContext.
   *
   * @param testSequence CharSequence to be predicted
   * @param initialContext CharSequence the initial context (length == VMM's order)
   * @return double logEval
   * @throws VMMNotTrainedException when predicting without training
   */
  double logEval(CharSequence testSequence, CharSequence initialContext);
}
