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
package de.bitsandbook.textkernel.TaxonomyEnricher.io.parser;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import de.bitsandbook.textkernel.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbook.textkernel.TaxonomyEnricher.io.tabmodel.TABAttractor;
import de.bitsandbook.textkernel.TaxonomyEnricher.io.tabmodel.TABCodeInstance;

public class TABTaxonomyParser implements TaxonomyParser {

	@Override
	public List<Attractor> parse(String filename, Charset cs) throws Exception {
		List<Attractor> result = new Vector<>();
		List<String> txt = Files.readAllLines(Paths.get(filename), cs);
		TABAttractor attractor = null;
		List<TABCodeInstance> allSynonyms = null;
		for (String line : txt) {
			String[] data = line.split("\t");
			if (data.length == 2) {
				if (!"-".equals(data[0])) {
					addAttractor(attractor, allSynonyms, result);
					allSynonyms = new Vector<>();
					attractor = new TABAttractor(data[0], data[1]);
				} else {
					TABCodeInstance synonym = new TABCodeInstance(data[1]);
					allSynonyms.add(synonym);
				}
			}
		}
		addAttractor(attractor, allSynonyms, result);
		return result;
	}

	private void addAttractor(TABAttractor attractor,
			List<TABCodeInstance> allSynonyms, List<Attractor> result) {
		if (attractor != null) {
			attractor.setSynonymList(allSynonyms);
			result.add(attractor);
		}
	}

}
