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
package de.bitsandbook.textkernel.TaxonomyEnricher.model;

public class TaxonomyCode {

	private String codeId;

	private String taxonomyId;

	private String description;

	public TaxonomyCode() {
		super();
	}

	public TaxonomyCode(String codeId, String taxonomyId, String description) {
		super();
		this.codeId = codeId;
		this.taxonomyId = taxonomyId;
		this.description = description;
	}

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getTaxonomyId() {
		return taxonomyId;
	}

	public void setTaxonomyId(String taxonomyId) {
		this.taxonomyId = taxonomyId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeId == null) ? 0 : codeId.hashCode());
		result = prime * result
				+ ((taxonomyId == null) ? 0 : taxonomyId.hashCode());
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
		TaxonomyCode other = (TaxonomyCode) obj;
		if (codeId == null) {
			if (other.codeId != null)
				return false;
		} else if (!codeId.equals(other.codeId))
			return false;
		if (taxonomyId == null) {
			if (other.taxonomyId != null)
				return false;
		} else if (!taxonomyId.equals(other.taxonomyId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaxonomyCode [codeId=" + codeId + ", taxonomyId=" + taxonomyId
				+ "]";
	}

}
