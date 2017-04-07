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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.log;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Logger4TaxonomyEnricher {
	
	public static final Logger LOGGER = Logger.getLogger("TaxonomyLogger");
	static {
		Path log4jFile = Paths.get(".", "config", "log4j.properties").toAbsolutePath().normalize(); 
		PropertyConfigurator.configure(log4jFile.toString());
	}
	
}
