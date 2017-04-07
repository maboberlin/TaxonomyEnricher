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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.tartarus.snowball.SnowballProgram;

import de.bitsandbook.textkernel.TaxonomyEnricher.data.TaxonomyBaseData;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.util.SynonymValued;
import de.bitsandbook.textkernel.TaxonomyEnricher.util.WordOccurencesMapCreator;

/**
 * Reduces an attractor to one synonym. Creates a map of occurences of each word of the attractor.<br>
 * Then multiplies each occurence count with a factor that measures the relative occurence of the word compared to its overall occurence (for all taxonomies)<br>
 * for example: if 'manager' makes 1% of all attractor words but occurs 2% of all taxonomy words, factor will be 1/2.<br>
 * 
 * @author mabo
 *
 */
public class SignificantOneAttractorReduce implements AttractorReduce {

	// --------------------------------- ATTRIBUTE

	@Autowired
	private TaxonomyBaseData baseData;
	
	@Autowired
	private SnowballProgram stemmer;

	// ----------------------------------- METHOD

	@Override
	public List<Synonym> apply(List<Synonym> attractor) {
		Map<String, Integer> wordOccurencesMap = new HashMap<>();
		WordOccurencesMapCreator.createWordOccurencesMap(attractor, wordOccurencesMap, true);
		Map<String, Double> adjustedWordOccurences = adjustWordOccurenceValues(wordOccurencesMap);
		List<Synonym> significant = get3SignificantSynonym(adjustedWordOccurences, attractor);
		return significant;
	}

	private Map<String, Double> adjustWordOccurenceValues(Map<String, Integer> attractorWordOccurencesMap) {
		Map<String, Double> result = new HashMap<>();
		int allWordsCount = baseData.getAllWordsCount();
		int attractorWordsCount = attractorWordOccurencesMap.values().stream().mapToInt(Integer::intValue).sum();
		for (Map.Entry<String, Integer> el : attractorWordOccurencesMap.entrySet()) {
			String word = el.getKey();
			int attrCnt = el.getValue();
			int allCnt = baseData.getStemmedWordCount(word);
			// if a word occurs in the attractor twice as often as in all taxonomy words, its occurences will be valued by factor 2
			double relFactor = (((double) attrCnt / (double) attractorWordsCount)
					/ ((double) allCnt / (double) allWordsCount));
			double val = relFactor * (double) attrCnt;
			result.put(word, val);
		}
		return result;
	}

	/**
	 * the synonym whose word with overall few occurences has the highest relative count.
	 * @param adjustedWordOccurences
	 * @param attractorSyns
	 * @return
	 */
	private List<Synonym> get3SignificantSynonym(Map<String, Double> adjustedWordOccurences, List<Synonym> attractorSyns) {
		TreeSet<SynonymValued> res = new TreeSet<>();
		for (Synonym synonym : attractorSyns) {
			String text = synonym.getText();
			String signWordStem = getSignificantWordStem(text);
			double val = adjustedWordOccurences.get(signWordStem);
			SynonymValued synVal = new SynonymValued(val, synonym, true);
			res.add(synVal);
		}
		List<Synonym> result = new ArrayList<>();
		int cnt = 0;
		for (Iterator<SynonymValued> it = res.descendingIterator(); it.hasNext() && cnt < 3 /*TODO SET AS CONSTANT*/; ) {
			SynonymValued el = it.next();
			result.add(el.synonym);
			cnt++;
		}
		return result;
	}
	
	/**
	 * word with few occurences in all words. 
	 * @param text
	 * @return
	 */
	private String getSignificantWordStem(String text) {
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
