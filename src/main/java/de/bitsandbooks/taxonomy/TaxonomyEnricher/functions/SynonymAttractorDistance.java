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
import java.util.Map;
import java.util.function.BiFunction;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Synonym;

/**
 * calculates the difference between a synonym and an attractor. for example by
 * determining the minimal difference between the synonym and all attractor
 * synonyms.
 * 
 * @author mabo
 *
 */
public abstract class SynonymAttractorDistance implements BiFunction<Synonym, List<Synonym>, Double> {

	// ----------------------------------- ATTRIBUTES

	/**
	 * already calculated distances between two synonyms (cache attribute).
	 */
	protected Map<SynonymPair, Double> calculatedDistances;

	// -------------------------------- SYNONYM PAIR HELPER CLASS

	protected static class SynonymPair {
		Synonym syn1;
		Synonym syn2;

		public SynonymPair(Synonym syn1, Synonym syn2) {
			super();
			this.syn1 = syn1;
			this.syn2 = syn2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((syn1 == null) ? 0 : syn1.hashCode());
			result = prime * result + ((syn2 == null) ? 0 : syn2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SynonymPair other = (SynonymPair) obj;
			if (syn1 == null) {
				if (other.syn1 != null && other.syn2 != null)
					return false;
			} else if (!syn1.equals(other.syn1) && !syn1.equals(other.syn2))
				return false;
			if (syn2 == null) {
				if (other.syn1 != null && other.syn2 != null)
					return false;
			} else if (!syn2.equals(other.syn1) && !syn2.equals(other.syn2))
				return false;
			return true;
		}

	}

}
