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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Code table consisting of a map of codes and corresponding attractors (set of synonyms).
 * @author mabo
 *
 */
public class CodeTable {

	// ------------------------------ ATTRIBUTES

	private String description;

	private Map<TaxonomyCode, Set<Synonym>> codeMap;

	// ------------------------------ CONSTRUCTOR + INITIALIZATION

	public CodeTable() {
		super();
		this.codeMap = new HashMap<>();
	}

	public CodeTable(String description) {
		super();
		this.description = description;
		this.codeMap = new HashMap<>();
	}

	// ------------------------------ METHODS

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addSynonyms(TaxonomyCode code, Set<Synonym> synonyms) {
		 Set<Synonym> oldSynonyms = codeMap.get(code);
		 if (oldSynonyms == null) {
			 codeMap.put(code, synonyms);
		 } else {
			 oldSynonyms.addAll(synonyms);
			 codeMap.put(code, oldSynonyms);
		 }
		
	}

	public Set<Map.Entry<TaxonomyCode, Set<Synonym>>> getAllTaxonomyCodes() {
		return codeMap.entrySet();
	}

	public Set<Synonym> getSynonyms4Code(TaxonomyCode code) {
		return codeMap.get(code);
	}

	public Set<TaxonomyCode> getAllCodes() {
		return codeMap.keySet();
	}

	public void removeSynonyms4Code(TaxonomyCode code, Set<Synonym> toDelete) {
		Set<Synonym> synonyms = codeMap.get(code);
		if (synonyms != null && synonyms.size() > 0) {
			synonyms.removeAll(toDelete);
		}
	}

	public void removeSynonyms4Code(TaxonomyCode code, Synonym synonym) {
		Set<Synonym> synonyms = codeMap.get(code);
		if (synonyms != null && synonyms.size() > 0) {
			synonyms.remove(synonym);
		}
	}

	public TaxonomyCode getTaxonomyCode4String(String code) {
		for (TaxonomyCode taxCode : codeMap.keySet()) {
			if (taxCode.getCodeId().equals(code)) {
				return taxCode;
			}
		}
		return null;
	}

	// --------------------------------- TO STRING METHODS
	
	public String toResponseString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<TaxonomyCode, Set<Synonym>> el : codeMap.entrySet()) {
			TaxonomyCode key = el.getKey();
			Set<Synonym> value = el.getValue();
			sb.append(key.getCodeId() + "\t" + key.getDescription() + "\r\n");
			for (Synonym syn : value) {
				sb.append("-" + "\t" + syn.getText() + "\r\n");
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}

}
