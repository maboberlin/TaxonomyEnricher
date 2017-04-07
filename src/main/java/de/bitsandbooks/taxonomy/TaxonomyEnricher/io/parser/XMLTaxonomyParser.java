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
package de.bitsandbooks.taxonomy.TaxonomyEnricher.io.parser;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.model.Attractor;
import de.bitsandbooks.taxonomy.TaxonomyEnricher.io.xmlmodel.XMLAttractor;

public class XMLTaxonomyParser implements TaxonomyParser {

	@Override
	public List<Attractor> parse(String filename, Charset cs) throws Exception {
		List<Attractor> result = new Vector<>();
		try (Reader xml = Files.newBufferedReader(Paths.get(filename), cs)) {
			XMLInputFactory staxFactory = XMLInputFactory.newInstance();
			XMLStreamReader staxReader = staxFactory.createXMLStreamReader(xml);
			XMLStreamReader filteredReader = staxFactory.createFilteredReader(
					staxReader, new MyStreamFilter());
			JAXBContext jaxbContext = JAXBContext
					.newInstance(XMLAttractor.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			while (filteredReader.hasNext()) {
				addAttractor(result, staxReader, filteredReader, unmarshaller);
			}
		}
		return result;
	}

	private void addAttractor(List<Attractor> result,
			XMLStreamReader staxReader, XMLStreamReader filteredReader,
			Unmarshaller unmarshaller) throws JAXBException, XMLStreamException {
		Object element = unmarshaller.unmarshal(staxReader, XMLAttractor.class);
		if (element instanceof JAXBElement
				&& ((JAXBElement<?>) element).getValue() != null) {
			element = ((JAXBElement<?>) element).getValue();
			XMLAttractor attractor = (XMLAttractor) element;
			result.add(attractor);
		}
		filteredReader.next();
	}

	// ---------------------- STREAM FILTER CLASS

	static class MyStreamFilter implements StreamFilter {
		@Override
		public boolean accept(XMLStreamReader reader) {
			return reader.isStartElement()
					&& "CodeRecord".equals(reader.getLocalName());
		}
	}

}
