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
package de.bitsandbook.textkernel.TaxonomyEnricher.util;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tartarus.snowball.SnowballProgram;

import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;

@Component
public class WordOccurencesMapCreator {
	
	private static SnowballProgram stemmer;
	
	@Autowired
	public WordOccurencesMapCreator(SnowballProgram stemmer) {
		WordOccurencesMapCreator.stemmer = stemmer;
	}
	
	public static Map<String, Integer> createWordOccurencesMap(Collection<Synonym> syonyomSet, Map<String, Integer> result, boolean stemmed) {
		for (Synonym synonym : syonyomSet) {
			String[] words = synonym.getText().split("\\s+");
			for (String word : words) {
				if (stemmed) {
					stemmer.setCurrent(word);
					stemmer.stem();
					word = stemmer.getCurrent();
				}
				Integer cnt = result.get(word);
				result.put(word, cnt == null ? 1 : cnt + 1);
			}
		}
		return result;
	}

}
