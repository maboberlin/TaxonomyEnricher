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
package de.bitsandbook.textkernel.TaxonomyEnricher.io.file;

import java.nio.charset.Charset;

public class TaxonomyFile {

	public String fileName;

	public TaxonomyFileType fileType;

	public Charset encoding;

	public TaxonomyFile(String fileName, TaxonomyFileType fileType,
			Charset encoding) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.encoding = encoding;
	}

	@Override
	public String toString() {
		return "TaxonomyFile [fileName=" + fileName + ", fileType=" + fileType
				+ ", encoding=" + encoding + "]";
	}

}
