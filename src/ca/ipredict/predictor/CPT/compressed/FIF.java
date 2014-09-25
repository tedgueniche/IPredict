package ca.ipredict.predictor.CPT.compressed;

import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

public interface FIF {

	public List<List<Item>> findFrequentItemsets(List<Sequence> seqs, int minLength, int maxlength, int minSup);
}
