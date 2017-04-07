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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbook.textkernel.TaxonomyEnricher.data.SignificantStringDiffTable;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbook.textkernel.TaxonomyEnricher.util.SynonymValued;

@Component
public class SynAttrSignificantWordStringDifferenceET extends SynonymAttractorET<SignificantStringDiffTable> {

	// ------------------------ CONSTRUCTOR

	@Autowired
	public SynAttrSignificantWordStringDifferenceET(SignificantStringDiffTable table) {
		super(table);
		this.distanceFunction = dataTable.getFunction();
	}

	// ---------------------------- ENRICH METHOD

	@Override
	public List<SynonymValued> gainSynonyms(TaxonomyCode code, Synonym synonym, boolean initial) {
		if (initial) {
			return this.initialSynonyms(code);
		} else {
			return this.stepSynonyms(code, synonym);
		}
	}

	@Override
	public String getType() {
		return "significantstringdifference";
	}
}
