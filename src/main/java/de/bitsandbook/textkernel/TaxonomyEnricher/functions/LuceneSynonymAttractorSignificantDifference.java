/**
 * "Taxonomy Enricher"
 *
 * Copyright (C) 2017 Matthias Boesinger (boesingermatthias@gmail.com).
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 *
 * @license GPL-3.0+ <http://spdx.org/licenses/GPL-3.0+>
 */
package de.bitsandbook.textkernel.TaxonomyEnricher.functions;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.search.spell.StringDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.tartarus.snowball.SnowballProgram;

import de.bitsandbook.textkernel.TaxonomyEnricher.data.TaxonomyBaseData;
import de.bitsandbook.textkernel.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;

/**
 * Compares the most significant words of synonyms. That is: word with overall few occurences within the synonym.<br>
 * Method returns the best difference between the synonym and one of the synonym list.
 * @author mabo
 *
 */
public class LuceneSynonymAttractorSignificantDifference extends SynonymAttractorDistance {

	// ----------------------------- ATTRIBUTES

	private StringDistance distance;

	@Autowired
	private TaxonomyBaseData baseData;

	@Autowired
	private SnowballProgram stemmer;

	// -------------------------------- CONSTRUCTOR

	public LuceneSynonymAttractorSignificantDifference() {
	}

	public LuceneSynonymAttractorSignificantDifference(String className) {
		try {
			// instantiate string distance object
			Class<?> cls = Class.forName(className);
			Object obj = cls.newInstance();
			distance = (StringDistance) obj;
			// init calculation map
			calculatedDistances = new HashMap<>();
		} catch (ClassCastException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			Logger4TaxonomyEnricher.LOGGER
					.warn(String.format("[%s] Lucene string distance class not found for name: %s",
							LuceneSynonymAttractorStringDifference.class, className), e1);
		}
	}

	// --------------------------------------- METHODS

	@Override
	public Double apply(Synonym syn1, List<Synonym> attractorSyn) {
		Double res = 0.0d;
		if (distance != null) {
			for (Synonym syn2 : attractorSyn) {
				SynonymPair synPair = new SynonymPair(syn1, syn2);
				Double val = calculatedDistances.get(synPair);
				if (val == null) {
					val = this.compareSynonyms(syn1, syn2);
					calculatedDistances.put(synPair, val);
				}
				res = Math.max(val, res);
				if (res == 1.0d) {
					return res;
				}
			}
		}
		return res;
	}

	private double compareSynonyms(Synonym syn1, Synonym syn2) {
		String word1 = getSignificantWord(syn1.getText());
		String word2 = getSignificantWord(syn2.getText());
		return distance.getDistance(word1, word2);
	}

	/**
	 * word with few occurences in all words.
	 * 
	 * @param text
	 * @return
	 */
	private String getSignificantWord(String text) {
		String res = null;
		String[] words = text.split("\\s+");
		int cntMin = Integer.MAX_VALUE;
		for (String word : words) {
			String stemmedWord = baseData.getStemmedWord(word);
			if (stemmedWord == null) {
				stemmer.setCurrent(word);
				stemmer.stem();
				stemmedWord = stemmer.getCurrent();
			}
			Integer cnt = baseData.getStemmedWordCount(stemmedWord);
			if ((cnt != null && cnt < cntMin) || (cntMin == Integer.MAX_VALUE)) {
				res = stemmedWord;
			}
		}
		return res;
	}

}
