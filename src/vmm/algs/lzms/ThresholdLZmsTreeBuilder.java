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
 * GuardingLZMTree.java
 *
 * Created on October 4, 2002, 8:21 AM
 */

package vmm.algs.lzms;

/**
 *
 * @author  nmoti
 */
public class ThresholdLZmsTreeBuilder {

	int[][] _sequences;
    double[] _selfChecks;
    boolean _selfIdUpdated;

    int _minContext;
    int _numShifts;
    int _alphabetSize;

    double _limit;
    double _selfIdentification;

    double _ave;
    double _std;

    /** Creates a new instance of ThresholdLZmsTreeBuilder */
    public ThresholdLZmsTreeBuilder(int minContext, int numShifts, int alphabetSize, int[][] sequences) {
        _minContext = minContext;
        _numShifts = numShifts;
        _alphabetSize = alphabetSize;

        _sequences = sequences;

        calculateSelfChecksAndStats();
    }

    protected ThresholdLZmsTree getTree(double stdMult) {
    	_limit = getLimitCalc(stdMult);

    	ThresholdLZmsTree result = new ThresholdLZmsTree(_limit, _minContext, _numShifts, _alphabetSize);
    	for (int i = 0; i < _sequences.length; i++)
    		result.learnSequence(_sequences[i]);

		_selfIdUpdated = false;
		_selfIdentification = getSelfIdentification();

		return result;
    }

	private void calculateSelfChecksAndStats() {
		_selfChecks = new double[_sequences.length];
		_ave = 0;
		_std = 0;

		for (int i = 0; i < _selfChecks.length; i++)
			_selfChecks[i] = allButOne(i);

		for (int i = 0; i < _selfChecks.length; i++) {
			_ave += _selfChecks[i];
			_std += _selfChecks[i]*_selfChecks[i];
		}

		_ave /= _selfChecks.length;
		_std = Math.sqrt(_std/_selfChecks.length - _ave*_ave);

	}

	private double allButOne(int oddOneOut) {
		LZmsTree tempTree = new LZmsTree(_minContext, _numShifts, _alphabetSize);
		for (int i = 0; i < oddOneOut; i++)
			tempTree.learnSequence(_sequences[i]);
		for (int i = oddOneOut + 1; i < _sequences.length; i++)
			tempTree.learnSequence(_sequences[i]);
		return tempTree.rateSequence(_sequences[oddOneOut]);
	}

    public double getLimitCalc(double stdMult) {
        return _ave + stdMult * _std;
    }

    public double getLimit() {
        return _limit;
    }

    public void  setLimit(double limit) {
        _limit = limit;
        _selfIdUpdated = false;
    }

    public void setStdMult(double stdMult) {
        _limit = getLimitCalc(stdMult);
        _selfIdUpdated = false;
    }

    public double getSelfIdentification() {
        if (_selfIdUpdated)
            return _selfIdentification;

        _selfIdUpdated = true;
        return _selfIdentification =  getSelfIdentification(_limit);
    }

    private double getSelfIdentification(double limit) {
        int falseN = 0;
        for (int i = 0; i < _selfChecks.length; i++)
            if (_selfChecks[i] >= limit)
                falseN++;

        return 1 - (double)falseN/(_selfChecks.length);
    }

    public int[] getNumSuccessesForSelfIdentifications(double[] stdMults){
    	int[] results = new int[stdMults.length];
    	for (int i = 0; i < results.length; i++)
    		results[i] = 0;

    	for (int i = 0; i < stdMults.length; i++) {
    		double limit = getLimitCalc(stdMults[i]);
    		for (int j = 0; j < _selfChecks.length; j++)
    			if (_selfChecks[j] < limit)
    				results[i]++;
    	}

    	return results;
    }

    public int getNumSelfChecks() {
    	return _selfChecks.length;
    }


    public double[] getSelfIdentifications(double[] stdMults) {
        double[] results = new double[stdMults.length];

        for (int i = 0; i < stdMults.length; i++)
            results[i] = getSelfIdentification(getLimitCalc(stdMults[i]));

        return results;
    }

    public boolean[][] getSuccesses(double[] stdMults) {
        boolean[][] result = new boolean[_selfChecks.length][stdMults.length];

        for (int j = 0; j < stdMults.length; j++) {
            double limit = getLimitCalc(stdMults[j]);
            for (int i = 0; i < _selfChecks.length; i++)
                result[i][j] = (_selfChecks[i] < limit);
        }

        return result;
    }
}
