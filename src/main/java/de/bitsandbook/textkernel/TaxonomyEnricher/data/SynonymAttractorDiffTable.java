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
package de.bitsandbook.textkernel.TaxonomyEnricher.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import de.bitsandbook.textkernel.TaxonomyEnricher.functions.SynonymAttractorDistance;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.Synonym;
import de.bitsandbook.textkernel.TaxonomyEnricher.model.TaxonomyCode;

/**
 * Synonym-Attractor Difference Table<br>
 * N x M matrix with n = synonyms.length and m = attractors.length.<br>
 * Each cell of the matrix holds a value for the difference between the synonym and the attractor (marked by its CodeId).
 * The value is calculated by the function of type T.
 * Lines and columns of the table can be calculated on demand, due to performance reasons.
 * @author mabo
 *
 * @param <T> Class of the function, which is used to calculate the difference between a synonym and an attractor.
 */
public abstract class SynonymAttractorDiffTable<T extends SynonymAttractorDistance> implements DataTableIF {

	// -------------------------- ATTRIBUTES

	@Autowired
	protected TaxonomyBaseData data;

	protected T function;

	protected Synonym[] synonyms;
	protected Map<Synonym, Integer> synonymRowMap;
	protected boolean[] synonymRowsCalculated;

	protected TaxonomyCode[] codes;
	protected Map<TaxonomyCode, Integer> codeColumnMap;
	protected boolean[] codeColumnsCalculated;
	protected boolean[] codeColumnsFetched;

	protected double[][] differences;

	// -------------------------- CONSTRUCTOR

	public SynonymAttractorDiffTable(T function) {
		this.function = function;
	}

	// ----------------------- GETTER AND SETTER

	public Synonym[] getSynonyms() {
		return synonyms;
	}

	public Synonym getSynonym(int ix) {
		return synonyms[ix];
	}

	public void setSynonyms(Synonym[] synonyms) {
		this.synonyms = synonyms;
	}

	public TaxonomyCode[] getCodes() {
		return codes;
	}

	public TaxonomyCode getCode(int ix) {
		return codes[ix];
	}

	public void setCodes(TaxonomyCode[] codes) {
		this.codes = codes;
	}

	public int getSynonymRow(Synonym synonym) {
		int res = synonymRowMap.get(synonym);
		return res;
	}

	public int getCodeColumn(TaxonomyCode code) {
		int res = codeColumnMap.get(code);
		return res;
	}

	public SynonymAttractorDistance getFunction() {
		return function;
	}

	public boolean isAlreadyFetchedColumn(int i) {
		return codeColumnsFetched[i];
	}

	// ---------------------- VALUE INITIALIZATION

	protected void initAttributes() {
		synonyms = data.getAllSynonyms();
		synonymRowMap = new HashMap<Synonym, Integer>();
		for (int i = 0; i < synonyms.length; i++) {
			synonymRowMap.put(synonyms[i], i);
		}
		synonymRowsCalculated = new boolean[synonyms.length];
		codes = data.getAllTaxonomyCodes();
		codeColumnMap = new HashMap<TaxonomyCode, Integer>();
		for (int i = 0; i < codes.length; i++) {
			codeColumnMap.put(codes[i], i);
		}
		codeColumnsCalculated = new boolean[codes.length];
		codeColumnsFetched = new boolean[codes.length];
		differences = new double[synonyms.length][codes.length];
	}

	/**
	 * Initializes all values of the table at once.
	 */
	protected void initAllValues() {
		IntStream.range(0, synonyms.length).parallel().forEach(synonymIx -> {
			IntStream.range(0, codes.length).forEach(codeIx -> {
				Synonym synonym = synonyms[synonymIx];
				List<Synonym> attractor = data.getAttractor(codes[codeIx]);
				double val = function.apply(synonym, attractor);
				differences[synonymIx][codeIx] = val;
			});
		});
		Arrays.fill(codeColumnsFetched, true);
		Arrays.fill(synonymRowsCalculated, true);
		Arrays.fill(codeColumnsCalculated, true);
	}

	/**
	 * Initializes a row on demand.
	 * @param synonym
	 * @return
	 */
	public double[] getSingleSynonymRow(Synonym synonym) {
		int synonymRow = synonymRowMap.get(synonym);
		// initialize synonym row if not done yet
		initSynonymRow(synonym, synonymRow);
		return differences[synonymRow];
	}

	private void initSynonymRow(Synonym synonym, int synonymRow) {
		if (!synonymRowsCalculated[synonymRow]) {
			for (int ix = 0; ix < codes.length; ix++) {
				List<Synonym> attractor = data.getAttractor(codes[ix]);
				double val = function.apply(synonym, attractor);
				differences[synonymRow][ix] = val;
			}
			synonymRowsCalculated[synonymRow] = true;
		}
	}

	/**
	 * Initializes a column on demand.
	 * @param code
	 * @return
	 */
	public double[] getSingleCodeColumn(TaxonomyCode code) {
		int codeColumn = codeColumnMap.get(code);
		// initialize code column if not done yet
		initCodeColumn(codeColumn);
		// prepare result
		double[] res = new double[synonyms.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = differences[i][codeColumn];
		}
		codeColumnsFetched[codeColumn] = true;
		return res;
	}

	private void initCodeColumn(int codeColumn) {
		if (!codeColumnsCalculated[codeColumn]) {
			List<Synonym> significantSynonyms = data.getMostSignificantSynonyms4Code(codes[codeColumn]);
			for (int ix = 0; ix < synonyms.length; ix++) {
				double val = 0.0d;
				for (Synonym signSynonym : significantSynonyms) {
					List<Synonym> list = Collections.singletonList(signSynonym);
					val += function.apply(synonyms[ix], list);
				}
				val = val / (double)significantSynonyms.size();
				differences[ix][codeColumn] = val;
			}
			codeColumnsCalculated[codeColumn] = true;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < synonyms.length; i++) {
			sb.append(i + "\t" + synonyms[i].getText() + System.lineSeparator());
		}
		sb.append(System.lineSeparator());
		for (int i = 0; i < codes.length; i++) {
			sb.append(i + "\t" + codes[i].getCodeId() + System.lineSeparator());
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}

}
