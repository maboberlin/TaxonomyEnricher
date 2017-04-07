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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tartarus.snowball.SnowballProgram;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.data.TaxonomyBaseData;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.EnrichTypeFactory;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.EnrichTypeIF;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.criteria.EnrichCriteria;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.enrich.criteria.EnrichCriteriaHelper;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.log.Logger4TaxonomyEnricher;

@Configuration
public class EnrichProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1904852594901666511L;

	// ----------------- PROPERTIES FILE DATA

	private static final Path FILE = Paths.get(".", "config", "enrich.properties");

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	// --------------------- PROPERTY KEYS

	private static final String synonymattractorstringdiffcolumnthreshold = "synonymAttractorStringDiffColumnThreshold";
	private static final String enrichertype = "enricherType";
	private static final String stepnumber = "steps";
	private static final String enrichcriteriatype = "enrichCriteria";
	private static final String enrichcriteriavalue = "enrichValue";
	private static final String stemmerlanguagekey = "stemmerLanguage";
	private static final String lucenestringkey = "lucenestringdistance";
	private static final String usesynonyminfoordering = "useSynonymInfoOrdering";

	// ---------------------- ATTRIBUTES

	private Double synonymAttractorStringDiffColumnThreshold;

	private List<EnrichTypeIF> actualTypes;

	private EnrichCriteriaHelper criteria;

	private String luceneStringDistanceClassName;

	private Boolean useSynonymInfoOrdering;

	// ---------------------------- CONSTRUCTOR

	public EnrichProperties() {
		try {
			Reader reader = Files.newBufferedReader(FILE, ENCODING);
			load(reader);
		} catch (IOException e) {
			Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Error loading enrich properties: %s",
					ApplicationProperties.class.toString(), e.toString()));
		}
	}

	// ---------------------------- BEAN DEFINITION

	@Bean
	public SnowballProgram stemmer() {
		try {
			String stemmerLanguage = (String) get(stemmerlanguagekey);
			Class<?> cls2 = Class.forName("org.tartarus.snowball.ext." + stemmerLanguage + "Stemmer");
			SnowballProgram stemmer = (SnowballProgram) cls2.newInstance();
			return stemmer;
		} catch (Exception e) {
			Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Error while instantiating stemming class: %s!",
					TaxonomyBaseData.class.toString(),
					"org.tartarus.snowball.ext." + get(stemmerlanguagekey) + "Stemmer"), e);
			return null;
		}
	}

	// -------------------------- GETTER AND SETTER

	public double getSynonymAttractorStringDiffColumnThreshold() {
		if (synonymAttractorStringDiffColumnThreshold == null) {
			initSynonymAttractorStringDiffColumnThreshold();
		}
		return synonymAttractorStringDiffColumnThreshold;
	}

	public void setSynonymAttractorStringDiffColumnThreshold(double synonymAttractorStringDiffColumn) {
		this.synonymAttractorStringDiffColumnThreshold = synonymAttractorStringDiffColumn;
	}

	public List<EnrichTypeIF> getActualTypes() {
		if (actualTypes == null) {
			String typeKey = getProperty(enrichertype);
			setActualTypes(typeKey);
		}
		return actualTypes;
	}

	public void setActualTypes(String typeKey) {
		String[] typeKeys = typeKey.split("\\s*\\+\\s*");
		this.actualTypes = new ArrayList<>();
		for (String type : typeKeys) {
			EnrichTypeIF et = EnrichTypeFactory.getET(type);
			actualTypes.add(et);
		}
	}

	public EnrichCriteriaHelper getCriteria() {
		if (criteria == null) {
			initDefaultCriteria();
		}
		return criteria;
	}

	public void setCriteria(Map<String, String> criteriaMap) {
		String steps = criteriaMap.get(stepnumber);
		String synCriteriaType = criteriaMap.get(enrichcriteriatype);
		String synCriteriaValue = criteriaMap.get(enrichcriteriavalue);
		initCriteria(steps, synCriteriaType, synCriteriaValue);
	}

	public String getLuceneStringDistanceClassName() {
		if (luceneStringDistanceClassName == null) {
			luceneStringDistanceClassName = (String) get(lucenestringkey);
		}
		return luceneStringDistanceClassName;
	}

	public void setLuceneStringDistanceClassName(String luceneStringDistanceClassName) {
		this.luceneStringDistanceClassName = luceneStringDistanceClassName;
	}

	public boolean isUseSynonymInfoOrdering() {
		if (useSynonymInfoOrdering == null) {
			String val = getProperty(usesynonyminfoordering);
			useSynonymInfoOrdering = Boolean.parseBoolean(val);
		}
		return useSynonymInfoOrdering;
	}

	public void setUseSynonymInfoOrdering(boolean useSynonymInfoOrdering) {
		this.useSynonymInfoOrdering = useSynonymInfoOrdering;
	}

	// -------------------------- INITIALIZER

	private void initSynonymAttractorStringDiffColumnThreshold() {
		String threshold = getProperty(synonymattractorstringdiffcolumnthreshold);
		try {
			synonymAttractorStringDiffColumnThreshold = Double.parseDouble(threshold);
		} catch (NumberFormatException e) {
			Logger4TaxonomyEnricher.LOGGER
					.warn(String.format("[%s] Invalid number format in %s for: %s!", EnrichProperties.class.toString(),
							FILE.toAbsolutePath().toString(), synonymattractorstringdiffcolumnthreshold), e);
		}

	}

	private void initCriteria(String steps, String synCriteriaType, String synCriteriaValue) {
		if (criteria == null) {
			initDefaultCriteria();
		}
		if (steps != null) {
			try {
				int stepNumber = Integer.parseInt(steps);
				criteria.steps = stepNumber;
			} catch (NumberFormatException e) {
				Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Invalid number format for 'steps' value!",
						EnrichProperties.class.toString()), e);
			}
		}
		if (synCriteriaType != null) {
			EnrichCriteria synCrit = EnrichCriteria.valueOf(synCriteriaType);
			criteria.criteriaType = synCrit;
			if (synCriteriaValue != null) {
				criteria.criteriaValue = synCriteriaValue;
			}
		}
	}

	private void initDefaultCriteria() {
		try {
			String steps = getProperty(stepnumber);
			int stepNr = Integer.parseInt(steps);
			String synCriteriaType = getProperty(enrichcriteriatype);
			String synCriteriaValue = getProperty(enrichcriteriavalue);
			EnrichCriteria synCrit = EnrichCriteria.valueOf(synCriteriaType);
			criteria = new EnrichCriteriaHelper(stepNr, synCrit, synCriteriaValue);
		} catch (Exception e) {
			Logger4TaxonomyEnricher.LOGGER.warn(String.format("[%s] Error while initializing default criteria values!",
					EnrichProperties.class.toString()), e);
		}

	}

}
