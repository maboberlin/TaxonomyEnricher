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
package de.bitsandbook.textkernel.TaxonomyEnricher.enrich;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbook.textkernel.TaxonomyEnricher.enrich.criteria.CriteriaHandler;
import de.bitsandbook.textkernel.TaxonomyEnricher.enrich.criteria.EnrichCriteriaHelper;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.CodeTable;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbook.textkernel.TaxonomyEnricher.util.SynonymValued;

/**
 * Class for handling the enrichment process.<br>
 * Methods for enriching, initializing first synonyms, undo enrichment
 * @author mabo
 *
 */
@Component
public class EnrichHandler {

	// ------------------------------ ATTRIBUTES

	@Autowired
	private CriteriaHandler criteriaHandler;

	private CodeTable codeTable;

	private Map<TaxonomyCode, List<Set<Synonym>>> newSynonyms;
	private Map<TaxonomyCode, Set<Synonym>> deletedSynonyms;

	// ------------------------------- CONSTRUCTOR

	public EnrichHandler() {
		newSynonyms = new HashMap<>();
		deletedSynonyms = new HashMap<>();
	}

	// ------------------------------- SETTER

	public void setCodeTable(CodeTable codeTable) {
		this.codeTable = codeTable;
	}

	// ------------------------------ ENRICH / UNDO METHODS

	/**
	 * ENRICH METHOD
	 * @param code
	 * @param criteria
	 * @param enrichTypeList
	 */
	public void enrich(TaxonomyCode code, EnrichCriteriaHelper criteria, List<EnrichTypeIF> enrichTypeList) {
		// check if synonyms for code have to be initialized
		Set<Synonym> foundSynonyms = codeTable.getSynonyms4Code(code);
		boolean initial = foundSynonyms == null || foundSynonyms.size() == 0;
		if (initial) {
			enrichInit(code, criteria, enrichTypeList);
		} else {
			enrichLoop(code, criteria, enrichTypeList);
		}
	}

	/**
	 * UNDO METHOD
	 * @param code
	 */
	public void undo(TaxonomyCode code) {
		List<Set<Synonym>> allNewSynonyms = newSynonyms.get(code);
		if (allNewSynonyms != null && allNewSynonyms.size() != 0) {
			Set<Synonym> lastStepSynonyms = allNewSynonyms.remove(allNewSynonyms.size() - 1);
			codeTable.removeSynonyms4Code(code, lastStepSynonyms);
		}
	}

	/**
	 * DELETE METHOD
	 * @param code
	 * @param synonyms
	 */
	public void deleteSynonyms(TaxonomyCode code, List<Synonym> synonyms) {
		// get new synonym entries
		List<Set<Synonym>> allNewSynonyms = newSynonyms.get(code);
		Set<Synonym> lastStepSynonyms = (allNewSynonyms != null && allNewSynonyms.size() != 0) ? allNewSynonyms.get(allNewSynonyms.size() - 1) : null; 
		// get deleted entries
		if (!deletedSynonyms.containsKey(code)) 
			deletedSynonyms.put(code, new HashSet<>());
		Set<Synonym> deletedSynonyms4Code = deletedSynonyms.get(code);
		// update synonym data
		for (Synonym synonym : synonyms) {
			if (lastStepSynonyms != null)
				lastStepSynonyms.remove(synonym);
			codeTable.removeSynonyms4Code(code, synonym);
			deletedSynonyms4Code.add(synonym);
		}
	}

	// ----------------------------------- SYNONYM INITIALIZATION METHOD

	private void enrichInit(TaxonomyCode code, EnrichCriteriaHelper criteria, List<EnrichTypeIF> enrichTypeList) {
		Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults = new HashMap<>();
		Synonym codeAsSynonym = new Synonym(code.getCodeId());
		for (EnrichTypeIF enrichTypeIF : enrichTypeList) {
			Map<Synonym, List<SynonymValued>> typeResults = new HashMap<>();
			// get valued synonyms from given synonym
			List<SynonymValued> enrichResult = enrichTypeIF.gainSynonyms(code, codeAsSynonym, true);
			// select synonyms by applying criteria
			List<SynonymValued> selectedSynonyms = criteriaHandler.preSelectAndSort(enrichResult, criteria);
			typeResults.put(codeAsSynonym, selectedSynonyms);
			allEnrichResults.put(enrichTypeIF.getType(), typeResults);
		}
		// select synonyms from all possible synonym result of this enrichment step
		Set<Synonym> stepEnrichResults = criteriaHandler.finalSelect(allEnrichResults, criteria);
		// put to final results
		storeResults(code, stepEnrichResults);
	}

	// ---------------------------------- CORE ENRICH METHOD

	/**
	 * enrichs the taxonomy for the number of steps given by criteria value
	 * 
	 * @param code
	 * @param criteria
	 * @return new synonyms - i.e. all enriched synonyms
	 */
	private void enrichLoop(TaxonomyCode code, EnrichCriteriaHelper criteria, List<EnrichTypeIF> enrichTypeList) {
		int steps = criteria.steps;
		for (int i = 0; i < steps; i++) {
			enrichStep(code, criteria, enrichTypeList);
		}
	}

	/**
	 * main method for taxonomy enrichment. loops over all enrichment types and
	 * in inner loop over all new synonyms of the last step. in each inner loop
	 * gains valued synonyms for each enrichment type * new synonym and
	 * preselects by the given criteria. at the end selects best synonym
	 * candidates by the given criteria.
	 * 
	 * @param code
	 * @param criteria
	 * @param enrichTypeList
	 * @param initial
	 */
	private void enrichStep(TaxonomyCode code, EnrichCriteriaHelper criteria, List<EnrichTypeIF> enrichTypeList) {
		Map<String, Map<Synonym, List<SynonymValued>>> allEnrichResults = new HashMap<>();
		List<Set<Synonym>> allNewSynonyms = newSynonyms.get(code);
		Set<Synonym> thisStepSynonyms = (allNewSynonyms != null && allNewSynonyms.size() != 0) ? allNewSynonyms.get(allNewSynonyms.size() - 1) : new HashSet<>();
		// loop over all enrichment types
		for (EnrichTypeIF enrichTypeIF : enrichTypeList) {
			Map<Synonym, List<SynonymValued>> typeResults = new HashMap<>();
			// loop over all actual synonyms
			for (Synonym synonym : thisStepSynonyms) {
				// get valued synonyms from given synonym
				List<SynonymValued> enrichResult = enrichTypeIF.gainSynonyms(code, synonym, false);
				// select synonyms by applying criteria
				List<SynonymValued> selectedSynonyms = criteriaHandler.preSelectAndSort(enrichResult, criteria);
				// put to type results
				typeResults.put(synonym, selectedSynonyms);
			}
			allEnrichResults.put(enrichTypeIF.getType(), typeResults);
		}
		// select synonyms from all possible synonym result of this enrichment step
		Set<Synonym> stepEnrichResults = criteriaHandler.finalSelect(allEnrichResults, criteria);
		// put to final results
		storeResults(code, stepEnrichResults);
	}

	private void storeResults(TaxonomyCode code, Set<Synonym> stepEnrichResults) {
		// add set of new synonyms to all new synonyms list
		List<Set<Synonym>> allNewSynonyms = newSynonyms.get(code);
		if (allNewSynonyms == null)
			allNewSynonyms = new ArrayList<>();
		allNewSynonyms.add(stepEnrichResults);
		newSynonyms.put(code, allNewSynonyms);
		// add new synonyms to code table
		codeTable.addSynonyms(code, stepEnrichResults);
	}

	// --------------------------------------- GETTER

	public Set<Synonym> alreadySelectedSynonyms4Code(TaxonomyCode code) {
		Set<Synonym> res = new HashSet<Synonym>();
		Set<Synonym> set1 = codeTable.getSynonyms4Code(code);
		Set<Synonym> set2 = deletedSynonyms.get(code);
		if (set1 != null && set1.size() > 0) {
			res.addAll(set1);
		}
		if (set2 != null && set2.size() > 0) {
			res.addAll(set2);
		}
		return res;
	}

}
