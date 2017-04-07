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

public class EnrichCriteriaHelper {

	public int steps;

	public EnrichCriteria criteriaType;
	public String criteriaValue;

	public EnrichCriteriaHelper(int steps, EnrichCriteria addSynonymCriteria, String addSynonymCriteriaValue) {
		super();
		this.steps = steps;
		this.criteriaType = addSynonymCriteria;
		this.criteriaValue = addSynonymCriteriaValue;
	}

}
