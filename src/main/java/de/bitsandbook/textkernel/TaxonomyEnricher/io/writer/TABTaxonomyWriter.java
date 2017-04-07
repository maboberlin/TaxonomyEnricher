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
package de.bitsandbook.textkernel.TaxonomyEnricher.io.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.bitsandbook.textkernel.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.CodeTable;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;

public class TABTaxonomyWriter implements TaxonomyWriter {

	@Override
	public void writeTaxonomy(CodeTable codeTable, String filename, Charset encoding) {
		Path path = Paths.get(filename);
		try (BufferedWriter writer = Files.newBufferedWriter(path, encoding, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			Set<Map.Entry<TaxonomyCode, Set<Synonym>>> entries = codeTable.getAllTaxonomyCodes();
			String line;
			for (Entry<TaxonomyCode, Set<Synonym>> entry : entries) {
				TaxonomyCode key = entry.getKey();
				Set<Synonym> value = entry.getValue();
				line = key.getCodeId() + "\t" + key.getDescription() + System.lineSeparator();
				writer.write(line);
				for (Synonym synonym : value) {
					line = String.valueOf('-') + "\t" + synonym.getText() + System.lineSeparator();
					writer.write(line);
				}
			}
			writer.flush();
		} catch (IOException e) {
			Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Error while writing taxonomy to: %s!", TABTaxonomyWriter.class.toString(), filename));
		}

		

	}

}
