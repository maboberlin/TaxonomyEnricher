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
package de.bitsandbook.textkernel.TaxonomyEnricher.io;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.CodeInstance;
import de.bitsandbook.textkernel.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.CodeTable;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.SynonymInfo;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Transfer;

public class CodeTableIOConverter {

	private static final String[] CONFIDENCE = { "confidence" };
	private static final String[] TRANSFER = { "source" };
	private static final String[] SOURCE = { "transfer" };

	public static CodeTable convertAttractor2CodeTable(
			List<Attractor> attractors, String name) {
		CodeTable table = new CodeTable();
		for (Attractor attractor : attractors) {
			String codeId = attractor.getCodeID();
			String codeDescription = attractor.getCodeDescription();
			TaxonomyCode code = new TaxonomyCode(codeId, name, codeDescription);
			List<CodeInstance> instanceList = attractor.getCodeInstances();
			Set<Synonym> synonyms = new HashSet<>();
			for (CodeInstance codeInstance : instanceList) {
				Synonym newSynonym = getSynonym(codeInstance);
				synonyms.add(newSynonym);
			}
			table.addSynonyms(code, synonyms);
		}
		return table;
	}

	public static Synonym getSynonym(CodeInstance instance) {
		String text = instance.getInstanceText();
		Map<String, String> properties = instance.getInstanceProperties();
		Synonym synonym = new Synonym(text);
		SynonymInfo synonymInfo = getInfo(properties);
		synonym.addSynonymInfo(synonymInfo);
		return synonym;
	}

	public static SynonymInfo getInfo(Map<String, String> properties) {
		SynonymInfo newVal = new SynonymInfo();
		if (properties != null) {
			for (Map.Entry<String, String> el : properties.entrySet()) {
				String key = el.getKey();
				String value = el.getValue();
				if (ArrayUtils.contains(CONFIDENCE, key)) {
					try {
						double conf = Double.parseDouble(value);
						newVal.setConfidence(conf);
					} catch (NumberFormatException e) {
						Logger4TaxonomyEnricher.LOGGER
								.warn(String
										.format("[%s] Unvalid confidence value in taxonomy file",
												CodeTableIOConverter.class
														.toString()));
					}
				} else if (ArrayUtils.contains(TRANSFER, key)) {
					Transfer transfer = Transfer.transferForString(value);
					newVal.setTransferType(transfer);
				} else if (ArrayUtils.contains(SOURCE, key)) {
					newVal.setSource(value);
				}
			}
		}
		return newVal;
	}

}
