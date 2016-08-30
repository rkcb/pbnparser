package com.pbn.parser;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.pbn.builder.HeaderBuilder;
import com.pbn.builder.SectionBuilder;
import com.pbn.builder.TableBuilder;
import com.pbn.builder.ValueBuilder;

public class BuilderTest {

	/***
	 * Build string is valid
	 */

	@Test
	public void ValueTest() {
		ValueBuilder b = new ValueBuilder();
		assertTrue(b.build().equals("[Tag \"\"]\r\n"));
	}

	/***
	 * Build string is valid
	 */

	@Test
	public void ValueTest2() {
		ValueBuilder b = new ValueBuilder("Tag", "Name");
		assertTrue(b.build().equals("[Tag \"Name\"]\r\n"));
	}

	/***
	 * Build string is valid
	 */

	@Test
	public void HeaderTest1() throws Exception {
		HeaderBuilder b = new HeaderBuilder("Tag", "Name");
		assertTrue(b.build().equals("[Tag \"Name\"]\r\n"));
	}

	/***
	 * Build string is valid
	 */

	@Test
	public void HeaderTest2() throws Exception {
		HeaderBuilder b = new HeaderBuilder("Tag", "Col1", "Col2");
		assertTrue(b.build().equals("[Tag \"Col1; Col2\"]\r\n"));
	}

	/***
	 * Build string is valid
	 */
	@Test
	public void HeaderTest3() throws Exception {
		HeaderBuilder b = new HeaderBuilder("Tag", Arrays.asList("Col1", "Col2"));
		assertTrue(b.build().equals("[Tag \"Col1; Col2\"]\r\n"));
	}

	/***
	 * Build string is valid
	 */
	@Test
	public void SectionTest1() throws Exception {
		SectionBuilder b = new SectionBuilder("Tag", "Name");
		b.addRow(Arrays.asList("x y"));
		assertTrue(b.build().equals("[Tag \"Name\"]\r\n" + "x y\r\n"));
	}

	/***
	 * Build string is valid
	 */
	@Test
	public void SectionTest2() throws Exception {
		SectionBuilder b = new SectionBuilder("Tag", "Col1", "Col2");
		b.addRow(Arrays.asList("x y"));
		b.addRow(Arrays.asList("x"));
		assertTrue(b.build().equals("[Tag \"Col1; Col2\"]\r\n" + "x y\r\n" + "x\r\n"));
	}

	/***
	 * Row size does not match column number
	 */

	@Test(expected = Exception.class)
	public void TableTest1() throws Exception {
		TableBuilder b = new TableBuilder("Tag", "Col1", "Col2");
		b.addRow("x", "y");
		b.addRow("x");
		assertTrue(b.rowSize() == 2);
	}

	/***
	 * Build string is valid
	 */
	@Test
	public void TableTest2() throws Exception {
		TableBuilder b = new TableBuilder("Tag", "Col1", "Col2");
		b.addRow(Arrays.asList("x", "y"));
		b.addRow(Arrays.asList("x", "y"));
		assertTrue(b.build().equals("[Tag \"Col1; Col2\"]\r\n" + "x y\r\n" + "x y\r\n"));
	}

	/***
	 * Zero rows
	 */

	@Test(expected = Exception.class)
	public void TableTest3() throws Exception {
		TableBuilder b = new TableBuilder("Tag", "Col1", "Col2");
		b.build();
	}

	/***
	 * Column number different than row size
	 */

	@Test(expected = Exception.class)
	public void TableTest4() throws Exception {
		TableBuilder b = new TableBuilder("Tag", "Col1", "Col2");
		b.addRow(Arrays.asList("x"));
		b.build();
	}

	public void o(String s) {
		System.out.println(s);
	}
}
