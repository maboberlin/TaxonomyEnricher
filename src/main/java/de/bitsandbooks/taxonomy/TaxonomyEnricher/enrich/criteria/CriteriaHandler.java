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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.data.TaxonomyBaseData;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.SynAttrStringDifferenceET;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Synonym;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.properties.EnrichProperties;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.util.SynonymValued;

/**
 * Class for selecting the new synonyms out of the valued synonyms for each enrichment step.<br>
 * Method 1: for preselecting synonyms after each synonyms gain step.<br>
 * Method 2: for final selections of synonyms for one enrichment step<br>
 * @author mabo
 *
 */
@Component
public class CriteriaHandler {

	// -------------------------------- ATTRIBUTE

	@Autowired
	private TaxonomyBaseData baseData;

	@Autowired
	protected EnrichProperties enrichProps;

	// ---------------------------------------- SELECT METHOD 1

	public List<SynonymValued> preSelectAndSort(List<SynonymValued> synonymsValued, EnrichCriteriaHelper criteria) {
		int synonymsCnt = baseData.getAllSynonyms().length;
		EnrichCriteria synCriteria = criteria.criteriaType;
		String criteriaVal = criteria.criteriaValue;
		Collections.sort(synonymsValued);
		Collections.reverse(synonymsValued);
		switch (synCriteria) {
		case THRESHOLD:
			return thresholdCriteria(synonymsValued, criteriaVal);
		case TOTALNUMBER:
			return totalNumberCriteria(synonymsValued, criteriaVal);
		case RELATIVENUMBER:
			return relativeNumberCriteria(synonymsValued, criteriaVal, synonymsCnt);
		default:
			return null;
		}
	}

	public List<SynonymValued> thresholdCriteria(List<SynonymValued> synonymsValued, String criteriaVal) {
		try {
			List<SynonymValued> result = new ArrayList<>();
			double val = Double.parseDouble(criteriaVal);
			for (SynonymValued synonymVal : synonymsValued) {
				if (synonymVal.value > val) {
					result.add(synonymVal);
				} else {
					break;
				}
			}
			return result;
		} catch (NumberFormatException e) {
			logFormatException(e);
			return null;
		}
	}

	public List<SynonymValued> totalNumberCriteria(List<SynonymValued> synonymsValued, String criteriaVal) {
		try {
			List<SynonymValued> result = new ArrayList<>();
			int val = Integer.parseInt(criteriaVal);
			for (SynonymValued synonymVal : synonymsValued) {
				if (result.size() < val) {
					result.add(synonymVal);
				} else {
					break;
				}
			}
			return result;
		} catch (NumberFormatException e) {
			logFormatException(e);
			return null;
		}
	}

	public List<SynonymValued> relativeNumberCriteria(List<SynonymValued> synonymsValued, String criteriaVal, int synonymsCnt) {
		try {
			List<SynonymValued> result = new ArrayList<>();
			double val = Double.parseDouble(criteriaVal);
			for (SynonymValued synonymVal : synonymsValued) {
				double rel = (double) result.size() / (double) synonymsCnt;
				if (rel < val) {
					result.add(synonymVal);
				} else {
					break;
				}
			}
			return result;
		} catch (NumberFormatException e) {
			logFormatException(e);
			return null;
		}
	}

	// ------------------------------------------------- SELECT METHOD 2

	public Set<Synonym> finalSelect(Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults, EnrichCriteriaHelper criteria) {
		int synonymsCnt = baseData.getAllSynonyms().length;
		EnrichCriteria synCriteria = criteria.criteriaType;
		String criteriaVal = criteria.criteriaValue;
		switch (synCriteria) {
		case THRESHOLD:
			return thresholdCriteria(allEnrichResults, criteriaVal);
		case TOTALNUMBER:
			return totalNumberCriteria(allEnrichResults, criteriaVal);
		case RELATIVENUMBER:
			return relativeNumberCriteria(allEnrichResults, criteriaVal, synonymsCnt);
		default:
			return null;
		}
	}

	private Set<Synonym> thresholdCriteria(Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults, String criteriaVal) {
		try {
			Set<Synonym> result = new HashSet<>();
			double val = Double.parseDouble(criteriaVal);
			for (Map<Synonym, List<SynonymValued>> resultMap : allEnrichResults.values()) {
				for (List<SynonymValued> synonymList : resultMap.values()) {
					for (SynonymValued synonymValued : synonymList) {
						if (synonymValued.value > val) {
							result.add(synonymValued.synonym);
						}
					}
				}
			}
			return result;
		} catch (NumberFormatException e) {
			logFormatException(e);
			return null;
		}
	}

	private Set<Synonym> totalNumberCriteria(Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults, String criteriaVal) {
		try {
			Set<Synonym> result = new HashSet<>();
			int val = Integer.parseInt(criteriaVal);
			SynonymValued[][] allResultSets = initAllResultSets(allEnrichResults);
			SynonymValued[] synonymCnt = initSynonymCount(allEnrichResults);
			List<SynonymValued> row = new ArrayList<>();
			int allResultsCnt = IntStream.range(0, allResultSets.length).map(ix -> allResultSets[ix].length).sum();
			int allResultsIx = 0, allResultsRowIx = 0, synonymCntIx = 0;
			boolean fromAllSets = true;
			while (result.size() < val && synonymCntIx < synonymCnt.length && allResultsIx < allResultsCnt) {
				if (fromAllSets) {
					// get one synonym of all sets list
					row = row.size() == 0 ? getRow(allResultsRowIx++, allResultSets) : row;
					SynonymValued el = row.remove(0);
					result.add(el.synonym);
					allResultsIx++;
					fromAllSets = false;
				} else {
					// get one synonym of synonym count list
					SynonymValued el = synonymCnt[synonymCntIx++];
					result.add(el.synonym);
					fromAllSets = true;
				}
			}
			return result;
		} catch (NumberFormatException e) {
			logFormatException(e);
			return null;
		}
	}

	private Set<Synonym> relativeNumberCriteria(Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults, String criteriaVal, int synonymsCnt) {
		try {
			Set<Synonym> result = new HashSet<>();
			int val = Integer.parseInt(criteriaVal);
			SynonymValued[][] allResultSets = initAllResultSets(allEnrichResults);
			SynonymValued[] synonymCnt = initSynonymCount(allEnrichResults);
			List<SynonymValued> row = new ArrayList<>();
			int allResultsRowIx = 0, synonymCntIx = 0;
			boolean fromAllSets = true;
			double rel = (double) result.size() / (double) synonymsCnt;
			while (rel < val && synonymCntIx < synonymCnt.length && allResultsRowIx < allResultSets.length) {
				if (fromAllSets) {
					// get one synonym of all sets list
					row = row.size() == 0 ? getRow(allResultsRowIx++, allResultSets) : row;
					SynonymValued el = row.remove(0);
					result.add(el.synonym);
					fromAllSets = false;
				} else {
					// get one synonym of synonym count list
					SynonymValued el = synonymCnt[synonymCntIx++];
					result.add(el.synonym);
					fromAllSets = true;
				}
				rel = (double) result.size() / (double) synonymsCnt;
			}
			return result;
		} catch (NumberFormatException e) {
			logFormatException(e);
			return null;
		}
	}

	// ----------------------------------------------- HELPER METHODS

	private SynonymValued[][] initAllResultSets(Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults) {
		int resCnt = allEnrichResults.values().stream().map(el -> el.size()).mapToInt(Integer::intValue).sum();
		SynonymValued[][] result = new SynonymValued[resCnt][];
		int ix = 0;
		for (Map<Synonym, List<SynonymValued>> synonymMap : allEnrichResults.values()) {
			for (List<SynonymValued> synonymSet : synonymMap.values()) {
				SynonymValued[] synonymValuedField = synonymSet.toArray(new SynonymValued[synonymSet.size()]);
				result[ix++] = synonymValuedField;
			}
		}
		return result;
	}

	private SynonymValued[] initSynonymCount(Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults) {
		Map<Synonym, Integer> cntMap = new HashMap<>();
		for (Map<Synonym, List<SynonymValued>> resultMap : allEnrichResults.values()) {
			for (List<SynonymValued> synonymList : resultMap.values()) {
				for (SynonymValued synonymValued : synonymList) {
					Integer cnt = cntMap.get(synonymValued.synonym);
					if (cnt == null) {
						Synonym newEl = synonymValued.synonym;
						cntMap.put(newEl, 1);
					} else {
						cntMap.put(synonymValued.synonym, cnt + 1);
					}
				}
			}
		}
		boolean useSynonymInfos = enrichProps.isUseSynonymInfoOrdering();
		List<SynonymValued> resultList = cntMap.entrySet().stream()
				.map(el -> new SynonymValued((double) el.getValue(), el.getKey(), useSynonymInfos)).sorted()
				.collect(Collectors.toList());
		SynonymValued[] result = resultList.toArray(new SynonymValued[resultList.size()]);
		return result;
	}

	private List<SynonymValued> getRow(int allResultsRowIx, SynonymValued[][] allResultSets) {
		List<SynonymValued> result = new ArrayList<>();
		for (int i = 0; i < allResultSets.length; i++) {
			if (allResultSets[i].length > allResultsRowIx) {
				SynonymValued el = allResultSets[i][allResultsRowIx];
				result.add(el);
			}
		}
		Collections.sort(result);
		Collections.reverse(result);
		return result;
	}

	public void logFormatException(NumberFormatException e) {
		Logger4TaxonomyEnricher.LOGGER.warn(
				String.format("[%s] Invalid format for synonym criteria!", SynAttrStringDifferenceET.class.toString()),
				e);
	}

}
