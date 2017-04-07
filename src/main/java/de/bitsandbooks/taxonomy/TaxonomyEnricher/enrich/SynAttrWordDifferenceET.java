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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.data.WordDiffTable;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Synonym;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.util.SynonymValued;

@Component
public class SynAttrWordDifferenceET extends SynonymAttractorET<WordDiffTable> {

	// ------------------------ CONSTRUCTOR

	@Autowired
	public SynAttrWordDifferenceET(WordDiffTable table) {
		super(table);
		this.distanceFunction = dataTable.getFunction();
	}

	// ---------------------------- ENRICH METHOD

	@Override
	public List<SynonymValued> gainSynonyms(TaxonomyCode code, Synonym synonym, boolean initial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "worddifference";
	}

}
