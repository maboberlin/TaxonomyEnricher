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
package de.bitsandbook.textkernel.TaxonomyEnricher.functions;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.search.spell.StringDistance;

import de.bitsandbook.textkernel.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;

public class LuceneSynonymAttractorStringDifference extends SynonymAttractorStringDistance {

	// ----------------------------- ATTRIBUTES

	private StringDistance distance;

	// -------------------------------- CONSTRUCTOR

	public LuceneSynonymAttractorStringDifference(String className) {
		try {
			// instantiate string distance object
			Class<?> cls = Class.forName(className);
			Object obj = cls.newInstance();
			distance = (StringDistance) obj;
			// init calculation map
			calculatedDistances = new HashMap<>();
		} catch (ClassCastException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			Logger4TaxonomyEnricher.LOGGER
					.warn(String.format("[%s] Lucene string distance class not found for name: %s",
							LuceneSynonymAttractorStringDifference.class, className), e1);
		}
	}

	@Override
	public Double apply(Synonym syn, List<Synonym> attractorSyn) {
		Double res = 0.0d;
		if (distance != null) {
			for (Synonym syn2 : attractorSyn) {
				SynonymPair synPair = new SynonymPair(syn, syn2);
				Double val = calculatedDistances.get(synPair);
				if (val == null) {
					val = this.distance(syn.getText(), syn2.getText());
					calculatedDistances.put(synPair, val);
				}
				res = Math.max(val, res);
				if (res == 1.0d) {
					return res;
				}
			}
		}
		return res;
	}

	@Override
	synchronized public double distance(String s1, String s2) {
		return distance.getDistance(s1, s2);
	}

}
