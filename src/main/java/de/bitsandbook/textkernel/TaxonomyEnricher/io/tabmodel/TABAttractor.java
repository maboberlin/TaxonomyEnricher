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

import java.util.List;
import java.util.Vector;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.CodeInstance;

public class TABAttractor implements Attractor {

	private String codeID;
	
	private String codeDescription;
	
	private List<TABCodeInstance> codeInstanceList;
	
	public TABAttractor(String codeID, String codeDescription) {
		super();
		this.codeID = codeID;
		this.codeDescription = codeDescription;
		this.codeInstanceList = new Vector<>();
	}

	@Override
	public String getCodeID() {
		return codeID;
	}

	@Override
	public String getCodeDescription() {
		return codeDescription;
	}

	@Override
	public List<CodeInstance> getCodeInstances() {
		List<CodeInstance> res = new Vector<>(codeInstanceList);
		return res;
	}

	public void setCodeID(String codeID) {
		this.codeID = codeID;
	}

	public void setCodeDescription(String codeDescription) {
		this.codeDescription = codeDescription;
	}

	public void setSynonymList(List<TABCodeInstance> synonymList) {
		this.codeInstanceList = synonymList;
	}
	
	@Override
	public String toString() {
		return String.format("%-5s: %-45s %s", codeID, codeDescription, codeInstanceList);
	}

}
