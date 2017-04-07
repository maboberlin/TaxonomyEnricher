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
package de.bitsandbook.textkernel.TaxonomyEnricher.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbook.textkernel.TaxonomyEnricher.functions.LuceneSynonymAttractorSignificantDifference;

@Component
public class SignificantStringDiffTable extends SynonymAttractorDiffTable<LuceneSynonymAttractorSignificantDifference> {

	// ------------------------------- INITIALIZATION

	@Autowired
	public SignificantStringDiffTable(LuceneSynonymAttractorSignificantDifference function) {
		super(function);
	}

	// ------------------------------ BUILD METHOD

	@Override
	public void buildTable() {
		initAttributes();
	}
}
