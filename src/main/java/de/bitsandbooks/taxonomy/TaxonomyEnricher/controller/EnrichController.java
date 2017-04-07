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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.EnrichHandler;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.EnrichTypeIF;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.criteria.EnrichCriteriaHelper;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.IOController;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.log.Logger4TaxonomyEnricher;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.CodeTable;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.model.TaxonomyCode;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.properties.EnrichProperties;

/**
 * offers methods for:<br>
 * - enrich taxonomy<br>
 * - undo enrichment<br>
 * - initialize taxonomy data<br>
 * - save taxonomy<br>
 * - printing usage help<br>
 * @author mabo
 *
 */
@RestController
public class EnrichController {

	// ---------------------- ATTRIBUTES

	private CodeTable codeTable;

	@Autowired
	private EnrichHandler handler;

	@Autowired
	private EnrichProperties properties;

	@Autowired
	private IOController ioController;

	// ----------------------------- CONTROLLER METHODS

	@RequestMapping(value = "/init", produces = "text/plain")
	public String initData() {
		long tim = System.currentTimeMillis();
		ioController.initData();
		codeTable = ioController.loadCodeTable();
		handler.setCodeTable(codeTable);
		tim = System.currentTimeMillis() - tim;
		return "Base data and code table initialized! (Elapsed time: " + tim + " ms)";
	}

	/**
	 * Enrichment method. Sets enricher type(s), enricher criteria. Performs enrichment for all selected codes.
	 * @param code
	 *            if code is null, all codes are enriched
	 * @param enricherType
	 *            if type is null, properties from enrich.properties are loaded
	 * @param criteriaMap
	 *            if map is null, properties from enrich.properties are loaded
	 */
	@RequestMapping(value = "/enrich", produces = "text/plain", method = RequestMethod.GET)
	public String enrich(@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "enricher", required = false) String enricherType,
			@RequestParam Map<String, String> allRequestParams) {
		long tim = System.currentTimeMillis();
		// handle parameters
		List<EnrichTypeIF> enrichTypeList = convertEnrichType(enricherType);
		EnrichCriteriaHelper criteria = convertCriteria(allRequestParams);
		// do enrich
		if (code != null) {
			TaxonomyCode taxCode = convertCode(code);
			handler.enrich(taxCode, criteria, enrichTypeList);
		} else {
			for (TaxonomyCode taxCode : codeTable.getAllCodes()) {
				handler.enrich(taxCode, criteria, enrichTypeList);
			}
		}
		// return code table string
		tim = System.currentTimeMillis() - tim;
		String responsString = codeTable.toResponseString() + "\r\n(Elapsed time : " + tim + " ms)";
		return responsString;
	}

	/**
	 * Undo enrichment for all selected codes.
	 * @param code if code is null, all codes are undone
	 */
	@RequestMapping(value = "/undo", produces = "text/plain", method = RequestMethod.GET)
	public String undoEnrichment(String code) {
		// undo enrich
		if (code != null) {
			TaxonomyCode taxCode = convertCode(code);
			handler.undo(taxCode);
		} else {
			for (TaxonomyCode taxCode : codeTable.getAllCodes()) {
				handler.undo(taxCode);
			}	
		}
		// return code table string
		String responsString = codeTable.toResponseString();
		return responsString;
	}

	@RequestMapping(value = "/help", produces = "text/plain")
	public String help() {
		try {
			InputStream inputStream = EnrichController.class.getResourceAsStream("/help.txt");
			String result = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")).lines()
					.collect(Collectors.joining("\r\n"));
			return result;
		} catch (UnsupportedEncodingException e) {
			Logger4TaxonomyEnricher.LOGGER
					.warn(String.format("[%s] Error while reading help file from /resources/help-txt!",
							EnrichController.class.toString()), e);
			return "Error while reading help file!";
		}
	}

	@RequestMapping(value = "/save", produces = "text/plain")
	public String save() {
		ioController.saveCodeTable(codeTable);
		return "Code table saved succesfully!";
	}

	// --------------------------- CONVERSION METHODS

	private TaxonomyCode convertCode(String code) {
		return codeTable.getTaxonomyCode4String(code);
	}

	private List<EnrichTypeIF> convertEnrichType(String enricherType) {
		if (enricherType != null && !enricherType.equals("")) {
			properties.setActualTypes(enricherType);
		}
		return properties.getActualTypes();
	}

	private EnrichCriteriaHelper convertCriteria(Map<String, String> criteriaMap) {
		if (criteriaMap != null && criteriaMap.size() > 0) {
			properties.setCriteria(criteriaMap);
		}
		return properties.getCriteria();
	}
}
