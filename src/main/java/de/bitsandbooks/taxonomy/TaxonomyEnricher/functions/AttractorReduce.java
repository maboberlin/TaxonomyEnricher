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

import java.util.List;
import java.util.function.Function;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Synonym;

/**
 * Interface for reducing an attractor to a subset.
 * @author mabo
 *
 */
public interface AttractorReduce extends Function<List<Synonym>, List<Synonym>> {

}
