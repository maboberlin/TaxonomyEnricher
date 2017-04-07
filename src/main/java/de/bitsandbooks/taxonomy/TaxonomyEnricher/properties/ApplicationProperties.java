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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.properties;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.springframework.stereotype.Component;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.file.TaxonomyFile;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.file.TaxonomyFileType;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.log.Logger4TaxonomyEnricher;

/**
 * Properties for file locations, data directory location, encoding.
 * @author mabo
 *
 */
@Component
public class ApplicationProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7926405706139037027L;

	// ----------------- PROPERTIES FILE DATA

	private static final Path FILE = Paths.get(".", "config", "taxonomyapplication.properties");

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	// --------------------- PROPERTY KEYS

	private static final String DIRECTORY_KEY = "taxonomyfilesdirectory";
	private static final String TAXONOMYS_FILES_KEY = "taxonomyfiles";
	private static final String TAXONOMY_TYPES_KEY = "filetypes";
	private static final String TAXONOMY_ENCODINGS_KEY = "fileencodings";
	private static final String OUTPUT_FILE_KEY = "outputfile";
	private static final String OUTPUT_TYPE_KEY = "outputfiletype";
	private static final String OUTPUT_ENCODING_KEY = "outputfileencoding";
	private static final String INPUT_FILE_KEY = "inputfile";
	private static final String INPUT_TYPE_KEY = "inputfiletype";
	private static final String INPUT_ENCODING_KEY = "inputfileencoding";

	// ---------------------- ATTRIBUTES

	private List<TaxonomyFile> taxonomyList;

	private TaxonomyFile codeTable;

	private TaxonomyFile outputFile;

	// --------------------- COSNTRUCTOR

	public ApplicationProperties() {
		try {
			Reader reader = Files.newBufferedReader(FILE, ENCODING);
			load(reader);
		} catch (IOException e) {
			Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Error loading application properties: %s",
					ApplicationProperties.class.toString(), e.toString()));
		}
	}

	// -------------------------- GETTER AND SETTER

	public TaxonomyFile[] getTaxonomyList() {
		if (taxonomyList == null) {
			initFileList();
		}
		return taxonomyList.toArray(new TaxonomyFile[taxonomyList.size()]);
	}

	public void setTaxonomyList(List<TaxonomyFile> taxonomyList) {
		this.taxonomyList = taxonomyList;
	}

	public TaxonomyFile getCodeTable() {
		if (codeTable == null) {
			codeTable = getTaxonomyFile(getProperty(INPUT_FILE_KEY), getProperty(INPUT_TYPE_KEY),
					getProperty(INPUT_ENCODING_KEY));
		}
		return codeTable;
	}

	public void setCodeTable(TaxonomyFile codeTable) {
		this.codeTable = codeTable;
	}

	public TaxonomyFile getOutputFile() {
		if (outputFile == null) {
			outputFile = getTaxonomyFile(getProperty(OUTPUT_FILE_KEY), getProperty(OUTPUT_TYPE_KEY),
					getProperty(OUTPUT_ENCODING_KEY));
		}
		return outputFile;
	}

	public void setOutputFile(TaxonomyFile outputFile) {
		this.outputFile = outputFile;
	}

	// -------------------------- DEFAULT INITIALIZER

	private void initFileList() {
		taxonomyList = new Vector<TaxonomyFile>();
		String filesString = getProperty(TAXONOMYS_FILES_KEY);
		String typesString = getProperty(TAXONOMY_TYPES_KEY);
		String encodingsString = getProperty(TAXONOMY_ENCODINGS_KEY);
		String[] files = filesString.split("\\s*\\|\\s*");
		String[] types = typesString.split("\\s*,\\s*");
		String[] encodings = encodingsString.split("\\s*,\\s*");
		if ((!(files.length == types.length && types.length == encodings.length)) || files.length == 0) {
			Logger4TaxonomyEnricher.LOGGER
					.warn(String.format("[%s] Incorrect number of parameters for taxonomy file data in: %s",
							ApplicationProperties.class.toString(), FILE.toAbsolutePath().toString()));
			return;
		}
		for (int i = 0; i < files.length; i++) {
			TaxonomyFile newFile = getTaxonomyFile(files[i], types[i], encodings[i]);
			taxonomyList.add(newFile);
		}
	}

	private TaxonomyFile getTaxonomyFile(String filename, String fileType, String encoding) {
		String directory = getProperty(DIRECTORY_KEY);
		Path p = Paths.get(directory, filename);
		TaxonomyFileType type = TaxonomyFileType.valueOf(fileType);
		Charset cs = Charset.forName(encoding);
		TaxonomyFile newFile = new TaxonomyFile(p.toAbsolutePath().toString(), type, cs);
		return newFile;
	}

}
