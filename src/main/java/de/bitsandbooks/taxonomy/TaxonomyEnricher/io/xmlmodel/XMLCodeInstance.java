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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.io.xmlmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.model.CodeInstance;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLCodeInstance implements CodeInstance {

	@XmlElement
	private String InstanceDescription;

	@XmlElement(name = "InstanceProperty")
	private List<XMLCodeInstanceProperty> instanceProperties;

	@Override
	public String getInstanceText() {
		return InstanceDescription;
	}
	
	public void setInstanceText(String txt) {
		InstanceDescription = txt;
	}

	public void setInstanceDescription(String instanceDescription) {
		InstanceDescription = instanceDescription;
	}

	public void setInstanceProperties(
			List<XMLCodeInstanceProperty> instanceProperties) {
		this.instanceProperties = instanceProperties;
	}

	@Override
	public Map<String, String> getInstanceProperties() {
		Map<String, String> res = new HashMap<>();
		if (instanceProperties != null) {
			for (XMLCodeInstanceProperty property : instanceProperties) {
				res.put(property.getName(), property.getText());
			}
		}
		return res;
	}

	@Override
	public String toString() {
		return InstanceDescription;
	}

}
