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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.io;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.data.DataTableIF;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.data.TaxonomyBaseData;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.file.TaxonomyFile;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.file.TaxonomyFileType;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.parser.ParserMap;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.parser.TaxonomyParser;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.writer.TaxonomyWriter;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.writer.WriterMap;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.CodeTable;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.properties.ApplicationProperties;

@Component
public class IOController {

	// --------------------- ATTRIBUTES

	@Autowired
	private ParserMap parserMap;

	@Autowired
	private TaxonomyBaseData taxonomyData;

	@Autowired
	private List<DataTableIF> dataTables;

	@Autowired
	private ApplicationProperties props;

	// ------------------------ CONTROLLER METHODS

	/**
	 * Initialization of taxonomxy data. loads all taxonomy files from properties. loads all data and passes each attractor to base data class.
	 */
	public void initData() {
		TaxonomyFile[] fileList = props.getTaxonomyList();
		// init base data
		for (TaxonomyFile file : fileList) {
			List<Attractor> attractorList = loadTaxonomy(file);
			taxonomyData.addAttractor(attractorList, file.fileName, file.fileType);
		}
		// init extended base data
		taxonomyData.initExtendedData();
		// init data tables
		for (DataTableIF dataTable : dataTables) {
			dataTable.buildTable();
		}
	}

	/**
	 * loads and initializes code table from file.
	 * @return
	 */
	public CodeTable loadCodeTable() {
		TaxonomyFile codeTableFile = props.getCodeTable();
		List<Attractor> attractorList = loadTaxonomy(codeTableFile);
		CodeTable table = CodeTableIOConverter.convertAttractor2CodeTable(attractorList, codeTableFile.fileName);
		return table;
	}

	/**
	 * saves code table to file.
	 * @param table
	 */
	public void saveCodeTable(CodeTable table) {
		TaxonomyFile saveFile = props.getOutputFile();
		TaxonomyFileType type = saveFile.fileType;
		TaxonomyWriter writer = WriterMap.getInstance().get(type);
		writer.writeTaxonomy(table, saveFile.fileName, saveFile.encoding);

	}

	// --------------------------- HELPER METHODS

	/**
	 * Chooses for each taxonomy file an appropriate taxonomy parser. parses each taxonomy and returns a list of all attractors.
	 * @param taxFile file to parse
	 * @return
	 */
	private List<Attractor> loadTaxonomy(TaxonomyFile taxFile) {
		TaxonomyFileType fileType = taxFile.fileType;
		String fileName = taxFile.fileName;
		Charset encoding = taxFile.encoding;
		try {
			TaxonomyParser parser = parserMap.get(fileType);
			List<Attractor> attractorList = parser.parse(fileName, encoding);
			return attractorList;
		} catch (Exception e) {
			Logger4TaxonomyEnricher.LOGGER.error(String.format("[%s] Error while parsing taxonomy file: %s!",
					IOController.class.toString(), fileName), e);
			return null;
		}
	}

}
