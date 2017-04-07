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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.model;

public class SynonymInfo {

	private TaxonomyCode taxonomyCode;

	private Double confidence;

	private String source;

	private Transfer transferType;

	public SynonymInfo() {
		super();
	}

	public SynonymInfo(TaxonomyCode taxonomyCode, Double confidence,
			String source, Transfer transferType) {
		super();
		this.taxonomyCode = taxonomyCode;
		this.confidence = confidence;
		this.source = source;
		this.transferType = transferType;
	}

	public TaxonomyCode getTaxonomyCode() {
		return taxonomyCode;
	}

	public void setTaxonomyCode(TaxonomyCode taxonomyCode) {
		this.taxonomyCode = taxonomyCode;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Transfer getTransferType() {
		return transferType;
	}

	public void setTransferType(Transfer transferType) {
		this.transferType = transferType;
	}

	@Override
	public String toString() {
		return "SynonymInfo [taxonomyCode=" + taxonomyCode + ", confidence="
				+ confidence + ", source=" + source + ", transferType="
				+ transferType + "]";
	}

}
