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
package de.bitsandbook.textkernel.TaxonomyEnricher.io.xmlmodel;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.CodeInstance;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CodeRecord")
public class XMLAttractor implements Attractor {
	
	@XmlElement
	private String CodeID;
	
	@XmlElement
	private String CodeDescription;
	
	@XmlElement(name="CodeProperty")
	private List<XMLAttractorProperty> codeProperties;
	
	@XmlElementWrapper(name = "InstanceList")
	@XmlElement(name = "Instance")
	private List<XMLCodeInstance> instanceList;

	
	@Override
	public String getCodeID() {
		return CodeID;
	}

	@Override
	public String getCodeDescription() {
		return CodeDescription;
	}

	@Override
	public List<CodeInstance> getCodeInstances() {
		List<CodeInstance> res = new Vector<>(instanceList);
		return res;
	}
	
	public void addCodeInstance(XMLCodeInstance instance) {
		if (instanceList != null) {
			instanceList.add(instance);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%-5s: %-45s %s", CodeID, CodeDescription, instanceList);
	}
	
	

}
