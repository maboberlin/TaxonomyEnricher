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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.io.writer;

import java.util.HashMap;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.file.TaxonomyFileType;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.parser.ParserMap;

public class WriterMap extends HashMap<TaxonomyFileType, TaxonomyWriter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5919539997146259883L;

	/**
	 * static Singleton instance.
	 */
	private static volatile WriterMap instance;

	/**
	 * Private constructor for singleton.
	 */
	private WriterMap() {
		super();
		put(TaxonomyFileType.TAB, new TABTaxonomyWriter());
	}

	/**
	 * Return a singleton instance of ParserMap.
	 */
	public static WriterMap getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (ParserMap.class) {
				if (instance == null) {
					instance = new WriterMap();
				}
			}
		}
		return instance;
	}

}
