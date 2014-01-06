package vmm;

import vmm.algs.BinaryCTWPredictor;
import vmm.algs.DCTWPredictor;
import vmm.algs.LZmsPredictor;
import vmm.algs.PPMCPredictor;
import vmm.algs.PSTPredictor;

public class MainTestVMMPredictors {

	/**
	 * THE SOURCE CODE IN THIS PACKAGE COME FROM:
	 * http://www.cs.technion.ac.il/~ronbeg/vmm/code_index.html
	 * 
	 * IT IS GPL 2 CODE
	 */
	public static void main(String[] args) {

		BinaryCTWPredictor bctw = new BinaryCTWPredictor();
		// absSize, vmmOrder
		bctw.init(256, 5);
		bctw.learn("abracadabra");
		System.out.println("logeval : " + bctw.logEval("cadabra"));
		System.out.println("P(a|abra) : " + bctw.predict('a', "abra"));
		System.out.println("P(b|abra) : " + bctw.predict('b', "abra"));
		System.out.println("P(c|abra) : " + bctw.predict('c', "abra"));
		System.out.println("P(d|abra) : " + bctw.predict('d', "abra"));
		System.out.println();

		LZmsPredictor lzms = new LZmsPredictor();
		// absSize, mParam, sParam
		lzms.init(256, 2, 0);
		lzms.learn("abracadabra");
		System.out.println("logeval : " + lzms.logEval("cadabra"));
		System.out.println("P(a|abra) : " + lzms.predict('a', "abra"));
		System.out.println("P(b|abra) : " + lzms.predict('b', "abra"));
		System.out.println("P(c|abra) : " + lzms.predict('c', "abra"));
		System.out.println("P(d|abra) : " + lzms.predict('d', "abra"));
		System.out.println();

		DCTWPredictor dctw = new DCTWPredictor();
		// absSize, vmmOrder
		dctw.init(256, 5);
		dctw.learn("abracadabra");
		System.out.println("logeval : " + dctw.logEval("cadabra"));
		System.out.println("P(a|abra) : " + dctw.predict('a', "abra"));
		System.out.println("P(b|abra) : " + dctw.predict('b', "abra"));
		System.out.println("P(c|abra) : " + dctw.predict('c', "abra"));
		System.out.println("P(d|abra) : " + dctw.predict('d', "abra"));
		System.out.println();

		PPMCPredictor ppmc = new PPMCPredictor();
		// absSize, vmmOrder
		ppmc.init(256, 5);
		ppmc.learn("abracadabra");
		System.out.println("logeval : " + ppmc.logEval("cadabra"));
		System.out.println("P(a|abra) : " + ppmc.predict('a', "abra"));
		System.out.println("P(b|abra) : " + ppmc.predict('b', "abra"));
		System.out.println("P(c|abra) : " + ppmc.predict('c', "abra"));
		System.out.println("P(d|abra) : " + ppmc.predict('d', "abra"));
		System.out.println();

		PSTPredictor pst = new PSTPredictor();
		// // absSize, pMin, alpha, gamma, r, vmmOrder
		pst.init(256, 0.001, 0.0, 0.0001, 1.05, 20);
		pst.learn("abracadabra");
		System.out.println("logeval : " + pst.logEval("cadabra"));
		System.out.println("P(a|abra) : " + pst.predict('a', "abra"));
		System.out.println("P(b|abra) : " + pst.predict('b', "abra"));
		System.out.println("P(c|abra) : " + pst.predict('c', "abra"));
		System.out.println("P(d|abra) : " + pst.predict('d', "abra"));
		
		int val = 25200;
		char charVal = (char) val;
		int intVal = charVal;
		System.out.println(" " + val + " as char: "  + charVal + " as int: " + intVal);
	}

}
