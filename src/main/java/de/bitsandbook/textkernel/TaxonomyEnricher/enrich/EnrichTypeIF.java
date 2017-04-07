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

import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbook.textkernel.TaxonomyEnricher.util.SynonymValued;

/**
 * EnrichType gains valued synonyms for a given code / synonym.
 * @author mabo
 *
 */
public interface EnrichTypeIF {
	
	public List<SynonymValued> gainSynonyms(TaxonomyCode code, Synonym synonym, boolean initial);
	
	public String getType();

}
