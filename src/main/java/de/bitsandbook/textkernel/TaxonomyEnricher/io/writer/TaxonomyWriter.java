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

import java.nio.charset.Charset;

import de.bitsandbook.textkernel.TaxonomyEnricher.model.CodeTable;

public interface TaxonomyWriter {
	
	public void writeTaxonomy(CodeTable codeTable, String filename, Charset encoding);

}
