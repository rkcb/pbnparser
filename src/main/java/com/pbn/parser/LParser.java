package com.pbn.parser;


import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;


@BuildParseTree
public class LParser extends BaseParser<Object> {

	public static void o(String s){
		if (s == null) return;
		else
			System.out.println(s);
	}

	protected Rule Alignment(){
		return FirstOf(Sequence(Digits(), AnyOf("rlRL")), AnyOf("rlRL"));
	}

	protected Rule ColumnName(){
		return Sequence(WhiteSpace(), NameToken(), push(match()), Optional(ColumnSpec()));
	}

	protected Rule ColumnNames(){
		return Sequence(ColumnName(), ZeroOrMore(Sequence(';', ColumnName())), Optional(';'));
	}

	protected Rule ColumnSpec(){
		return Sequence('\\', ZeroOrMore(AnyOf(" \t")), Alignment());
	}

	protected Rule Comment(){
		return OneOrMore(FirstOf(LineComment(), MultiComment()));
	}

	protected Rule Digits(){
		return OneOrMore(CharRange('0', '9'));
	}

	protected Rule EmptyLine(){
		return Sequence(WhiteSpace(), "\r\n");
	}

	protected Rule Event(){
		return OneOrMore(FirstOf(Comment(), Table(), Value()));
	}

	protected Rule EventEnd(){
		return Sequence(LineEnd(), WhiteSpace());
	}

	protected Rule Events(){
		return Sequence(WhiteSpace(), OneOrMore(Sequence(Event(), EventEnd())), EOI);
	}

	@Override
	protected Rule fromStringLiteral(String string) {
		return string.endsWith(" ") ?
				Sequence(String(string.substring(0, string.length() - 1)), WhiteSpace()) :
					String(string);
	}

	protected Rule Header(){
		return Sequence('[', NameToken(), push(match()), Space(), '"', ColumnNames(), '"', WhiteSpace(), ']', LineEnd());
	}

	protected Rule Letter(){
		return FirstOf(
				CharRange('A', 'Z'),
				CharRange('a', 'z'));
	}

	protected Rule LineComment(){
		return OneOrMore(Sequence('%', ZeroOrMore(NoneOf("\r\n")), LineEnd()));
	}


	protected Rule LineEnd(){
		return FirstOf(Sequence(ZeroOrMore(AnyOf(" \t")), "\r\n", ZeroOrMore(AnyOf(" \t\f"))), EOI);
	}

	protected Rule MultiComment(){
		return Sequence( "{" , ZeroOrMore(NoneOf("}")), "}", LineEnd() );
	}

	protected Rule My(){
		return Events();
	}

	protected Rule NameToken(){
		return Sequence(Letter(),
				ZeroOrMore(FirstOf(Letter(), Digits(), '_')));
	}

	protected Rule SemiEmptyLine(){
		return Sequence(WhiteSpace(), "\r\n");
	}

	protected Rule Space(){
		return Sequence(ZeroOrMore(AnyOf("\t\f")), ' ', ZeroOrMore(AnyOf(" \t\f")));
	}

	// TODO: create empty table to the event
	protected Rule Table(){
		return Sequence(Header(), OneOrMore(Sequence(Optional(Comment()), TableRow())));
	}

	protected Rule TableNonString(){
		return Sequence(OneOrMore(NoneOf("{}[]\"\r\n\t\f ")), push(match()));
	}

	// TODO: store row values to the table in the stack
	protected Rule TableRow(){
		return Sequence(TableValue(), ZeroOrMore(Sequence(Space(), TableValue())), LineEnd(), new Action() {

			@Override
			public boolean run(Context context) {

				return true;
			}
		});
	}

	protected Rule TableString(){
		return Sequence('"', ZeroOrMore(NoneOf("\r\n\"[]")), push(match()), '"');
	}

	//	protected Rule Event(){
	//		return Sequence(WhiteSpace(), OneOrMore(FirstOf(Comment(), Table(), )));
	//	}

	protected Rule TableValue(){
		return FirstOf(TableNonString(), TableString());
	}

	protected Rule TagValue(){
		return ZeroOrMore(NoneOf("\r\n\""));
	}

	/***
	 * In PBN a tag value is of the form [<Tag name> "<Value>"]
	 * */

	protected Rule Value(){
		return Sequence('[', NameToken(), push(match()), Space(),
				'"', TagValue(), push(match()), '"', WhiteSpace(), ']', LineEnd());
	}

	Rule WhiteSpace() {
		return ZeroOrMore(AnyOf(" \t\f\r\n"));
	}








	//	protected Rule Row(){
	//		return Sequence(OneOrMany(FirstOf()));
	//	}

	//	protected Rule Table(){
	//		return Sequence(Header(), OneOrMany)
	//	}


	//	protected Rule Value(String string){
	//		 Var<Character> tag = new Var<Character>();
	//		return Sequence('[', Tag(string), '"', TagValue(string), '"', ']');
	//	}
	//

	//	protected Rule Values()

	//	Rule Value(){
	//		return Sequence('[', Tag(), TagValue(), ']');
	//	}
	//
	//	Rule Row(){
	//		return Sequence(OneOrMore(Item()));
	//	}








}
