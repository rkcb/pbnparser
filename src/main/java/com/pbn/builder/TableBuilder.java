package com.pbn.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TableBuilder extends SectionBuilder {

	protected int rowSize; // all rows must have the same size

	public TableBuilder(String tag, List<String> cols) throws Exception {
		super(tag, cols);
		rowSize = cols.size();
	}

	public TableBuilder(String tag, String... columnNames) throws Exception {
		this(tag, Arrays.asList(columnNames));
	}

	/***
	 * addRow adds a row of values NOTE: row must not contain null values
	 */

	@Override
	public void addRow(List<String> row) throws Exception {
		Objects.requireNonNull(row);
		if (row.size() != rowSize) {
			throw new Exception("Invalid table row size");
		} else {
			rows.add(row);
		}
	}

	public void addRow(String... items) throws Exception {
		addRow(Arrays.asList(items));
	}

	@Override
	public String build() throws Exception {
		return super.build();
	}

	public int rowSize() {
		return rowSize;
	}

}
