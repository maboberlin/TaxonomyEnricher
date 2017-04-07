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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import de.bitsandbook.textkernel.TaxonomyEnricher.data.SynonymAttractorDiffTable;
import de.bitsandbook.textkernel.TaxonomyEnricher.enrich.criteria.CriteriaHandler;
import de.bitsandbook.textkernel.TaxonomyEnricher.functions.SynonymAttractorDistance;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbook.textkernel.TaxonomyEnricher.properties.EnrichProperties;
import de.bitsandbook.textkernel.TaxonomyEnricher.util.SynonymValued;

/**
 * EnrichType basing on a type of synonym-attractor-difference table.
 * @author mabo
 *
 * @param <T> type of synonym-attractor-difference
 */
public abstract class SynonymAttractorET<T extends SynonymAttractorDiffTable<? extends SynonymAttractorDistance>> implements EnrichTypeIF {

	// -------------------------- ATTRIBUTESS

	@Autowired
	protected EnrichHandler manager;

	@Autowired
	protected CriteriaHandler criteriaHandler;

	protected T dataTable;

	protected SynonymAttractorDistance distanceFunction;

	@Autowired
	protected EnrichProperties enrichProps;

	// ----------------------------- CONSTRUCTOR

	public SynonymAttractorET(T dataTable) {
		this.dataTable = dataTable;
	}

	// ----------------------- INITIALIZING SYNONYMS

	/**
	 * initializing new synonyms for a code table code.
	 * @param code
	 * @return
	 */
	protected List<SynonymValued> initialSynonyms(TaxonomyCode code) {
		List<SynonymValued> result = new ArrayList<>();
		String codeDesc = code.getDescription();
		List<Synonym> toCompare = new ArrayList<>();
		toCompare.add(new Synonym(codeDesc));
		Synonym[] synonyms = dataTable.getSynonyms();
		boolean useSynonymInfos = enrichProps.isUseSynonymInfoOrdering();
		for (Synonym synonym : synonyms) {
			double distance = distanceFunction.apply(synonym, toCompare);
			SynonymValued synValues = new SynonymValued(distance, synonym, useSynonymInfos);
			result.add(synValues);
		}
		return result;
	}

	// ----------------------- ENRICH METHOD

	/**
	 * main method for finding new synonyms. get difference values from data
	 * table for the current synonym. then chooses code columns for which the
	 * threshold value criteria is fulfilled. then chooses the new synonyms
	 * within the selected columns.
	 * 
	 * @param taxCode
	 * @param synonym
	 * @param criteria
	 * @param attractorThreshold
	 * @param alreadySelectedSynonyms
	 * @return
	 */
	protected List<SynonymValued> stepSynonyms(TaxonomyCode taxCode, Synonym synonym) {
		List<SynonymValued> result = new ArrayList<>();
		Set<Synonym> alreadySelectedSynonyms = manager.alreadySelectedSynonyms4Code(taxCode);
		double attractorThreshold = enrichProps.getSynonymAttractorStringDiffColumnThreshold();
		// get values for synonym row of difference table
		double[] synonymDiffValues = dataTable.getSingleSynonymRow(synonym);
		// get indexes of code columns who fulfill threshold criteria
		List<Integer> codeIxs = IntStream.range(0, synonymDiffValues.length)
				.filter(ix -> synonymDiffValues[ix] >= attractorThreshold)
				.boxed().collect(Collectors.toList());
		// get all new synonyms for each selected code column valued by data table entry
		for (Integer codeIx : codeIxs) {
			TaxonomyCode code = dataTable.getCode(codeIx);
			addSynonymsValued4Code(result, alreadySelectedSynonyms, synonym, code);
		}
		return result;
	}

	// ------------------------------- HELPER METHOD

	private List<SynonymValued> addSynonymsValued4Code(List<SynonymValued> synonymsValued, Set<Synonym> alreadySelectedSynonyms, Synonym synonym, TaxonomyCode code) {
		double[] diffValues = dataTable.getSingleCodeColumn(code);
		double[] diffValuesAdjusted = adjustDiffValuesBySynonymDistance(diffValues, synonym);
		boolean useSynonymInfos = enrichProps.isUseSynonymInfoOrdering();
		for (int i = 0; i < diffValuesAdjusted.length; i++) {
			Synonym syn = dataTable.getSynonym(i);
			if (!alreadySelectedSynonyms.contains(syn)) {
				SynonymValued newEl = new SynonymValued(diffValuesAdjusted[i], syn, useSynonymInfos);
				synonymsValued.add(newEl);
			}
		}
		return synonymsValued;
	}

	
	private double[] adjustDiffValuesBySynonymDistance(double[] diffValues, Synonym synonym) {
		double[] result = new double[diffValues.length];
		List<Synonym> toCompare = new ArrayList<>();
		toCompare.add(synonym);
		Synonym[] synonyms = dataTable.getSynonyms();
		for (int i = 0; i < result.length; i++) {
			result[i] = diffValues[i] * distanceFunction.apply(synonyms[i], toCompare);
		}
		return result;
	}

}
