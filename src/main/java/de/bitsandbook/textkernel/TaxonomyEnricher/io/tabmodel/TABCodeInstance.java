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
package de.bitsandbook.textkernel.TaxonomyEnricher.io.tabmodel;

import java.util.Map;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.CodeInstance;

public class TABCodeInstance implements CodeInstance {

	private String instanceText;

	private Map<String, String> instanceProperties;

	public TABCodeInstance(String instanceText) {
		super();
		this.instanceText = instanceText;
	}

	@Override
	public String getInstanceText() {
		return instanceText;
	}

	public void setInstanceText(String codeInstance) {
		this.instanceText = codeInstance;
	}

	@Override
	public Map<String, String> getInstanceProperties() {
		return instanceProperties;
	}

	public void setInstanceProperties(Map<String, String> instanceProperties) {
		this.instanceProperties = instanceProperties;
	}

	@Override
	public String toString() {
		return "TABCodeInstance [instanceText=" + instanceText + "]";
	}
	

}
