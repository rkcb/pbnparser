package com.pbn.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HeaderBuilder extends ValueBuilder {

	/***
	 * header builds a header
	 *
	 * @param tag
	 *            pbn tag
	 * @param cols
	 *            column names with optional pbn parameters e.g. IMP\5R
	 * @throws Exception
	 */

	public static String header(String tag, List<String> cols) throws Exception {
		Objects.requireNonNull(cols);
		if (cols.size() < 1) {
			throw new Exception("Column names must not be empty");
		}
		ValueBuilder b = new ValueBuilder(tag, String.join("; ", cols));
		return b.build();
	}

	/***
	 * @param tag
	 *            pbn tag
	 * @param cols
	 *            contains the column labels and optional information of the
	 *            alignment and column width and the column order
	 *
	 * @throws Exception
	 */

	public HeaderBuilder(String tag, List<String> cols) throws Exception {

		Objects.requireNonNull(tag);
		Objects.requireNonNull(cols);

		if (cols.size() < 1) {
			throw new Exception("Column names must not be empty");
		}

		this.tag = tag;
		this.value = String.join("; ", cols);
	}

	/***
	 * @param tag
	 *            pbn tag
	 * @param cols
	 *            contains the column labels and optional information of the
	 *            alignment and column width and the column order
	 *
	 * @throws Exception
	 */

	public HeaderBuilder(String tag, String... cols) throws Exception {
		this(tag, Arrays.asList(cols));
	}

	/***
	 * build builds a pbn string representing a table header e.g. [ScoreTable
	 * "col1; ...; coln"]
	 */

	@Override
	public String build() {
		return super.build();
	}

}
