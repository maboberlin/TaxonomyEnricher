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

import java.util.List;
import java.util.Vector;

/**
 * Text of synonym and list of all infos (each info entry may stem from a different base data taxonomy.)
 * @author mabo
 *
 */
public class Synonym {

	private String text;

	private List<SynonymInfo> synonymInfoList;

	public Synonym(String text) {
		super();
		this.text = text;
		this.synonymInfoList = new Vector<SynonymInfo>();
	}

	public Synonym() {
		super();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<SynonymInfo> getSynonymInfoList() {
		return synonymInfoList;
	}

	public void setSynonymInfoList(List<SynonymInfo> synonymInfoList) {
		this.synonymInfoList = synonymInfoList;
	}

	public void addSynonymInfo(List<SynonymInfo> info) {
		for (SynonymInfo synonymInfo : info) {
			addSynonymInfo(synonymInfo);
		}
	}

	public void addSynonymInfo(SynonymInfo info) {
		if (synonymInfoList == null) {
			this.synonymInfoList = new Vector<SynonymInfo>();
		}
		synonymInfoList.add(info);
	}

	public Double getHighestConfidence() {
		Double res = 0.0d;
		for (SynonymInfo synonymInfo : synonymInfoList) {
			Double conf = synonymInfo.getConfidence();
			if (conf == null)
				continue;
			res = Math.max(res, conf);
		}
		return res;
	}

	public Transfer getHighestTransferType() {
		Transfer res = null;
		for (SynonymInfo synonymInfo : synonymInfoList) {
			Transfer trans = synonymInfo.getTransferType();
			if (trans == null)
				continue;
			if (res == null)
				res = trans;
			else
				res = res.compareTo(trans) < 0 ? res : trans;
		}
		return res;
	}

	@Override
	public String toString() {
		return "Synonym [text=" + text + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Synonym other = (Synonym) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
