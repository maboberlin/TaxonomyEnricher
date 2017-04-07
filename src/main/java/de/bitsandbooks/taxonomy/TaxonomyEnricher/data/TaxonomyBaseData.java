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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tartarus.snowball.SnowballProgram;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.functions.AttractorReduce;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.CodeTableIOConverter;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.file.TaxonomyFileType;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.model.CodeInstance;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Synonym;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.SynonymInfo;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.util.WordOccurencesMapCreator;

/**
 * All data for a taxonomy.<br>
 * That is:<br> 
 * a) Synoynms, Attractors, Codes<br>
 * b) word occurences, stemmed words, stemmed word occurences, most significant synonyms for each attractor
 * @author mabo
 *
 */
@Component
public class TaxonomyBaseData {

	// -------------------- ATTRIBUTES

	private LinkedHashMap<TaxonomyCode, TaxonomyCode> codeList;

	private LinkedHashMap<Synonym, Synonym> allSynonymList;

	private Map<TaxonomyCode, List<Synonym>> attractorList;

	private Map<TaxonomyCode, List<Synonym>> mostSignificantSynonymList;

	private Map<String, Integer> wordOccurences;
	
	private Map<String, Integer> stemmedWordOccurences;

	private Map<String, String> stemmedWords;
	
	private int allWordsCount;

	@Autowired
	private AttractorReduce attractorReduce;
	
	@Autowired
	private SnowballProgram stemmer;

	// -------------------- CONSTRUCTOR

	private TaxonomyBaseData() {
		super();
		allSynonymList = new LinkedHashMap<>();
		codeList = new LinkedHashMap<>();
		attractorList = new LinkedHashMap<>();
		mostSignificantSynonymList = new LinkedHashMap<>();
		stemmedWordOccurences = new LinkedHashMap<>();
		wordOccurences = new LinkedHashMap<>();
	}

	// -------------------- GETTER AND SETTER

	public TaxonomyCode[] getAllTaxonomyCodes() {
		Set<TaxonomyCode> keySet = codeList.keySet();
		TaxonomyCode[] res = keySet.toArray(new TaxonomyCode[keySet.size()]);
		return res;
	}

	public Synonym[] getAllSynonyms() {
		Set<Synonym> keySet = allSynonymList.keySet();
		Synonym[] res = keySet.toArray(new Synonym[keySet.size()]);
		return res;
	}

	public List<Synonym> getAttractor(TaxonomyCode code) {
		List<Synonym> res = attractorList.get(code);
		return res;
	}

	public Integer getWordCount(String word) {
		Integer cnt = wordOccurences.get(word);
		return cnt;
	}
	
	public Integer getStemmedWordCount(String word) {
		Integer cnt = stemmedWordOccurences.get(word);
		return cnt;
	}

	public String getStemmedWord(String word) {
		return stemmedWords.get(word);
	}

	public List<Synonym> getMostSignificantSynonyms4Code(TaxonomyCode taxonomyCode) {
		return mostSignificantSynonymList.get(taxonomyCode);
	}
	
	public int getAllWordsCount() {
		return allWordsCount;
	}

	// -------------------- INITIALIZATION

	/**
	 * Main method for base data initialization.
	 * @param attractorList
	 * @param taxonomyName
	 * @param fileType
	 */
	public void addAttractor(List<Attractor> attractorList, String taxonomyName, TaxonomyFileType fileType) {
		for (Attractor attractor : attractorList) {
			// prepare taxonomy code
			TaxonomyCode code = putCode(attractor, taxonomyName);
			// prepare synonyms
			List<Synonym> list = putSynonyms(attractor);
			// add codeId as instance
			if (fileType == TaxonomyFileType.TAB) {
				Synonym synonym = addCodeAsSynonym(attractor);
				list.add(synonym);
			}
			// prepare attractor
			this.attractorList.put(code, list);
		}

	}

	private TaxonomyCode putCode(Attractor attractor, String taxonomyName) {
		String codeId = attractor.getCodeID();
		String codeDescription = attractor.getCodeDescription();
		TaxonomyCode code = new TaxonomyCode(codeId, taxonomyName, codeDescription);
		if (codeList.containsKey(code)) {
			Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Multiple occurences of code: %s",
					TaxonomyBaseData.class.toString(), code.toString()));
		}
		codeList.put(code, code);
		return code;
	}

	private List<Synonym> putSynonyms(Attractor attractor) {
		List<Synonym> list = new Vector<>();
		for (CodeInstance instance : attractor.getCodeInstances()) {
			Synonym newSynonym = CodeTableIOConverter.getSynonym(instance);
			Synonym oldSynonym = allSynonymList.get(newSynonym);
			if (oldSynonym != null) {
				List<SynonymInfo> synonymInfoList = newSynonym.getSynonymInfoList();
				oldSynonym.addSynonymInfo(synonymInfoList);
			} else {
				allSynonymList.put(newSynonym, newSynonym);
			}
			list.add(newSynonym);
		}
		return list;
	}

	private Synonym addCodeAsSynonym(Attractor attractor) {
		String codeId = attractor.getCodeID();
		String description = attractor.getCodeDescription();
		String text = description != null && description.length() > 0 ? description : codeId;
		Synonym synonym = new Synonym(text);
		allSynonymList.put(synonym, synonym);
		return synonym;
	}

	// --------------------- INITIALIZATION OF EXTENDED DATA

	public void initExtendedData() {
		initWordCountMap();
		initStemmedWordMap();
		initStemmedWordCountMap();
		initAllWordCount();
		initMostSignificantSynonyms();
	}

	private void initWordCountMap() {
		WordOccurencesMapCreator.createWordOccurencesMap(allSynonymList.keySet(), wordOccurences, false);
	}

	private void initStemmedWordMap() {
		stemmedWords = new HashMap<>();
		for (Synonym synonym : allSynonymList.keySet()) {
			String[] words = synonym.getText().split("\\s+");
			for (String word : words) {
				stemmer.setCurrent(word);
				stemmer.stem();
				String stemmedWord = stemmer.getCurrent();
				stemmedWords.put(word, stemmedWord);
			}
		}
	}
	
	private void initStemmedWordCountMap() {
		WordOccurencesMapCreator.createWordOccurencesMap(allSynonymList.keySet(), stemmedWordOccurences, true);	
	}
	
	private void initAllWordCount() {
		allWordsCount = stemmedWordOccurences.values().stream().mapToInt(Integer::intValue).sum();
	}

	/**
	 * calculates the most significant synonyms for each attractor.
	 */
	private void initMostSignificantSynonyms() {
		for (Map.Entry<TaxonomyCode, List<Synonym>> el : attractorList.entrySet()) {
			List<Synonym> attractor = el.getValue();
			List<Synonym> reducedAttractor = attractorReduce.apply(attractor);
			mostSignificantSynonymList.put(el.getKey(), reducedAttractor);
		}
	}
}
