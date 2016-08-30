package com.pbn.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/***
 * SectionBuilder builds pbn from a section header and data rows of different
 * sizes
 */

public class SectionBuilder {

	protected String tag;
	protected String header = null; // need not be a table header
	protected List<List<String>> rows = new ArrayList<>();

	/***
	 * SectionBuilder builds a pbn section whose rows may be different in size
	 * NOTE: header mapping: col1, col2, ..., coln => col1; col2; ...; coln
	 */

	public SectionBuilder(String tag, List<String> cols) throws Exception {
		Objects.requireNonNull(tag);
		this.tag = tag;
		header = HeaderBuilder.header(tag, cols);
	}

	public SectionBuilder(String tag, String... cols) throws Exception {
		this(tag, Arrays.asList(cols));
	}

	/***
	 * addRow adds a row of values NOTE: row must not contain a null value
	 */

	public void addRow(List<String> row) throws Exception {
		Objects.requireNonNull(row);
		if (row.size() < 1) {
			throw new Exception("Section row must have at least one element");
		} else {
			rows.add(row);
		}
	}

	public String build() throws Exception {
		if (rows.size() < 1) {
			throw new Exception("Section must have at least one row");
		}

		StringBuilder b = new StringBuilder(header);
		for (List<String> r : rows) {
			b.append(String.join(" ", r));
			b.append("\r\n");
		}

		return b.toString();

	}

}
