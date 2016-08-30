package com.pbn.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;

public class MyTest {

	public static String inputText(String fileName) {
		Charset charset = Charset.forName("ISO-8859-1");
		if (fileName == null)
			return null;

		String uri = "/home/esa/Documents/pbn/" + fileName + ".pbn";

		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(uri), charset)) {

			StringBuilder builder = new StringBuilder("");

			while ((line = reader.readLine()) != null) {

				builder.append(line).append("\r\n");
			}

			line = builder.toString();

		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		return line;
	}

	public static void main(String[] args) {
		String input = "[Tag  	\"MyValue\" ] \r\n" + "c1 c2 c3\r\n" + "[]";
		// String line = "1 5 4 \"N-S\" 42.0 \"Riku Mattila - Patrik Eriksson\"
		// 28 \"H6 - Turun BK\" \"1349\" \"1263\" - -\r\n";
		// String mComment = "{ sldkj j \r\n" +
		// "x x x x x x x x\r\n" +
		// "yyy }\r\n"+
		// "x yx x\r\n" + " sldkj lkj \r\n" +
		// "x x x x x x x {x\r\n";
		// input = input + mComment + line;
		input = inputText("butler");
		// o(input);
		LParser parser = Parboiled.createParser(LParser.class);
		ReportingParseRunner<Object> runner = new ReportingParseRunner<>(parser.My());
		ParsingResult<Object> result = runner.run(input);

		String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);

		// o(parseTreePrintOut);

		o("\nmatched: " + result.matched + "\n");

		o("stack size: " + result.valueStack.size());

		for (int i = 0; i < result.valueStack.size(); i++) {
			// o("value: "+result.valueStack.peek(i));
		}

		if (!result.matched) {
			o("report buffer where error occurred:\n");
			ParseError error = result.parseErrors.get(0);

			String str = "";

			int i0 = error.getStartIndex();
			int i1 = error.getEndIndex();

			Position pos = error.getInputBuffer().getPosition(i0);

			int lines = Integer.min(3, error.getInputBuffer().getLineCount());

			String errContext = "";

			for (int i = 1; i <= lines; i++) {
				errContext += error.getInputBuffer().extractLine(i) + "\n";
			}

			o("error line(s): \n\n" + errContext + "\n");
			o("error >>>" + ("" + error.getInputBuffer().charAt(i0)) + "<<<" + " at line: " + pos.line + " and column: "
					+ pos.column);

		}
		// input = inputText("impcross");
		// o(input);
	}

	public static void o(Object s) {
		if (s == null)
			return;
		else
			System.out.println(s);
	}

}
