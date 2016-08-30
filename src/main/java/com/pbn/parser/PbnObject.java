package com.pbn.parser;

import java.util.ArrayList;
import java.util.List;

public class PbnObject {

	private String tag = null;
	private String value = null;
	private List<String> header = null;
	private List<Object> rows = null; // either table row or table header

	public PbnObject(String tag) {
		this.tag = tag;
	}

	public void addHeaderItem(String item) {
		if (item != null) {
			if (header == null) {
				header = new ArrayList<>();
			}
			header.add(item);
		}
	}

	public void addRow(Object row) {
		if (row != null) {
			if (rows == null) {
				rows = new ArrayList();
			}
			rows.add(row);
		}
	}

	public List<Object> rows() {
		return rows;
	}

	public String value() {
		return value;
	}

}
