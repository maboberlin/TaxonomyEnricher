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

import java.util.HashMap;

import org.springframework.stereotype.Component;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.file.TaxonomyFileType;

@Component
public class ParserMap extends HashMap<TaxonomyFileType, TaxonomyParser> {

	private static final long serialVersionUID = 1898254963294927058L;

	private ParserMap() {
		super();
		put(TaxonomyFileType.XML, new XMLTaxonomyParser());
		put(TaxonomyFileType.TAB, new TABTaxonomyParser());
	}

}
