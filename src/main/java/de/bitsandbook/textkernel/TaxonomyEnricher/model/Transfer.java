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
package de.bitsandbook.textkernel.TaxonomyEnricher.model;

import org.apache.commons.lang.ArrayUtils;

public enum Transfer {
	
	MANUAL, ENRICH, AUTO_UPDATE;
	
	private static final String[] MANUAL_TEXT = {"manual"};
	private static final String[] AUTO_UPDATE_TEXT = {"auto-update"};
	private static final String[] ENRICH_TEXT = {"enrich"};
	
	public static Transfer transferForString(String text) {
		if (ArrayUtils.contains(MANUAL_TEXT, text))
			return MANUAL;
		else if (ArrayUtils.contains(AUTO_UPDATE_TEXT, text))
			return AUTO_UPDATE;
		else if (ArrayUtils.contains(ENRICH_TEXT, text))
			return ENRICH;
		else 
			return null;
	}

}
