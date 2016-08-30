package com.pbn.builder;

import java.util.Objects;

public class ValueBuilder {

	protected String begin = "[";
	protected String tag = "Tag";
	protected String space = " ";
	protected String lQuote = "\"";
	protected String rQuote = "\"";
	protected String end = "]\r\n";
	protected String value = "";

	public ValueBuilder() {
	}

	public ValueBuilder(String tag, String value) {
		Objects.requireNonNull(tag);
		Objects.requireNonNull(value);

		this.tag = tag;
		this.value = value;
	}

	/***
	 * builds a value line, e.g. [Tag \"\"]
	 */

	public String build() {
		StringBuilder b = new StringBuilder();

		b.append(begin);
		b.append(tag);
		b.append(space);
		b.append(lQuote);
		b.append(value);
		b.append(rQuote);
		b.append(end);

		return b.toString();
	}

}
