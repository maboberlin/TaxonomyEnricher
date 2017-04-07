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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.util;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Synonym;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.Transfer;

/**
 * Helper class that enables synonyms to get ordered by their difference to something (for example: attractor).<br>
 * Can use synonym info for extra ordering info.
 * @author mabo
 *
 */
public class SynonymValued implements Comparable<SynonymValued> {

	// ---------------------------------- ATTRIBUTES

	public double value;
	public Synonym synonym;
	private boolean useSynonymInfos;

	// ---------------------------------- CONSTRUCTOR

	public SynonymValued(double value, Synonym synonym, boolean useSynonymInfos) {
		super();
		this.value = value;
		this.synonym = synonym;
		this.useSynonymInfos = useSynonymInfos;
	}

	// ------------------------------------ COMPARE METHODS

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((synonym == null) ? 0 : synonym.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		SynonymValued other = (SynonymValued) obj;
		if (synonym == null) {
			if (other.synonym != null)
				return false;
		} else if (!synonym.equals(other.synonym))
			return false;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(SynonymValued other) {
		int compare = Double.compare(value, other.value);
		if (compare == 0.0d && useSynonymInfos) {
			Double otherConf = other.synonym.getHighestConfidence();
			Double thisConf = synonym.getHighestConfidence();
			if (Double.compare(thisConf, otherConf) == 0.0d) {
				Transfer otherTrans = other.synonym.getHighestTransferType();
				Transfer thisTrans = synonym.getHighestTransferType();
				compare = (otherTrans == null || thisTrans == null) ? 0 : thisTrans.compareTo(otherTrans);
			}
		}
		return compare;
	}

	@Override
	public String toString() {
		return "SynonymValued [value=" + value + ", synonym=" + synonym + "]";
	}

}
