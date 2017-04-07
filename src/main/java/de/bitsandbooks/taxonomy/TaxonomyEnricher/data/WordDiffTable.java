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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.functions.SynonymAttractorWordDifference;

@Component
public class WordDiffTable extends SynonymAttractorDiffTable<SynonymAttractorWordDifference> {

	@Autowired
	public WordDiffTable(SynonymAttractorWordDifference function) {
		super(function);
	}

	@Override
	public void buildTable() {
		// TODO not ready yet ...

	}

}
