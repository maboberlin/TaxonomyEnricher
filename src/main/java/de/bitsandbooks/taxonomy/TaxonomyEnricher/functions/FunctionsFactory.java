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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.functions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.properties.EnrichProperties;

@Configuration
public class FunctionsFactory {

	// ------------------------- ATTRIBUTES

	@Autowired
	private EnrichProperties props;

	// --------------------------- GETTER

	@Bean
	public LuceneSynonymAttractorStringDifference getSynonymAttractorStringDistance() {
		String luceneStringDistanceClassName = props.getLuceneStringDistanceClassName();
		LuceneSynonymAttractorStringDifference synonymAttractorStringDistance = new LuceneSynonymAttractorStringDifference(luceneStringDistanceClassName);
		return synonymAttractorStringDistance;
	}

	@Bean
	public LuceneSynonymAttractorSignificantDifference getSynonymAttractorSigificantStringDistance() {
		String luceneSignificantStringDistanceClassName = props.getLuceneStringDistanceClassName();
		LuceneSynonymAttractorSignificantDifference synonymAttractorSignificantStringDistance = new LuceneSynonymAttractorSignificantDifference(luceneSignificantStringDistanceClassName);
		return synonymAttractorSignificantStringDistance;
	}

	@Bean
	public SynonymAttractorWordDifference getSynonymAttractorWordDifference() {
		return new SynonymAttractorWordDifference();
	}
	
	@Bean
	public AttractorReduce getAttractorReduce() {
		return new SignificantOneAttractorReduce();
	}

}
