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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrichTypeFactory {

	@Autowired
	private List<EnrichTypeIF> ets;

	private static final Map<String, EnrichTypeIF> enrichTypeCache = new HashMap<>();

	@PostConstruct
	public void initEnrichTypeCache() {
		for (EnrichTypeIF et : ets) {
			if (et != null) {
				enrichTypeCache.put(et.getType(), et);
			}
		}
	}

	public static EnrichTypeIF getET(String type) {
		EnrichTypeIF et = enrichTypeCache.get(type);
		return et;
	}

}
