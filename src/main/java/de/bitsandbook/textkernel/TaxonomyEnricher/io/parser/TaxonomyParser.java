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
package de.bitsandbook.textkernel.TaxonomyEnricher.io.parser;

import java.nio.charset.Charset;
import java.util.List;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.Attractor;

public interface TaxonomyParser {
	
	public List<Attractor> parse(String filename, Charset cs) throws Exception;

}
