package de.tuhrig.fujas;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.tuhrig.fujas.Parser;
import de.tuhrig.fujas.types.LBoolean;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LList;
import de.tuhrig.fujas.types.LNull;
import de.tuhrig.fujas.types.LNumber;
import de.tuhrig.fujas.types.LString;
import de.tuhrig.fujas.types.LSymbol;

public class ParserTest {

	private Parser parser;

	@Before
	public void reset() {

		this.parser = new Parser();
	}

	@Test
	public void parse() {

		Assert.assertEquals("'()", parser.parse("'()").toString());
		Assert.assertEquals("'(1)", parser.parse("'(1)").toString());
		Assert.assertEquals("'(())", parser.parse("'(())").toString());
		Assert.assertEquals("(+, 1, 1)", parser.parse("(+ 1 1)").toString());
		Assert.assertEquals("(+, 1, 2)", parser.parse("(+ 1 2)").toString());
		Assert.assertEquals("(+, 1, 2, 3, 4, 5)", parser.parse("(+ 1 2 3 4 5)").toString());
		Assert.assertEquals("(+, (+, 1, 2), 1)", parser.parse("(+ (+ 1 2) 1)").toString());
		Assert.assertEquals("(plus, (inc, 1, 2), 1)", parser.parse("(plus (inc 1 2) 1)").toString());
		Assert.assertEquals("(plus, (+, (inc, 2), 2), 1)", parser.parse("(plus (+ (inc 2) 2) 1)").toString());
	}

	@Test
	public void testSingleLineCommentPattern() {

		String singleLineComment = "(+ \n" + "-1 \n" + "; comment \n" + "6\n" + ")";

		String singleLineWithoutComment = "(+ \n" + "-1 \n" + "6\n" + ")";

		singleLineComment = Parser.SINGLE_LINE_COMMENT.matcher(singleLineComment).replaceAll("");

		Assert.assertEquals(singleLineWithoutComment, singleLineComment);
	}

	@Test
	public void testMultiLineCommentPattern() {

		String multiLineComment = "(+ \n" + "-1 \n" + "#| \n " + "	comment \n" + "|# \n " + "6\n" + ")";

		String multiLineWithoutComment = "(+ \n" + "-1 \n" + " \n " + "6\n" + ")";
		
		multiLineComment = Parser.MULTI_LINE_COMMENT.matcher(multiLineComment).replaceAll("");

		Assert.assertEquals(multiLineWithoutComment, multiLineComment);
	}
	
	@Test
	public void parseTypes() {

		LList list1;

		// Boolean
		list1 = parser.parse("(true)");
		Assert.assertTrue(list1.size() == 1);
		Assert.assertTrue(list1.get(0) == LBoolean.TRUE);

		list1 = parser.parse("(false)");
		Assert.assertTrue(list1.size() == 1);
		Assert.assertTrue(list1.get(0) == LBoolean.FALSE);

		// String
		list1 = parser.parse("(\"aaa\")");
		Assert.assertTrue(list1.size() == 1);
		Assert.assertTrue(list1.get(0) instanceof LString);

		// Number
		list1 = parser.parse("(1)");
		Assert.assertTrue(list1.size() == 1);
		Assert.assertTrue(list1.get(0) instanceof LNumber);

		// List
		list1 = parser.parse("(())");
		Assert.assertTrue(list1.size() == 1);
		Assert.assertTrue(list1.get(0) instanceof List);

		// Null
		list1 = parser.parse("(null)");
		Assert.assertTrue(list1.size() == 1);
		Assert.assertTrue(list1.get(0) instanceof LNull);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void parseTypesNested() {

		// first list
		LList list1 = parser.parse("(plus (+ (inc 2) 2) 1)");

		Assert.assertTrue(list1.size() == 3);

		Assert.assertTrue(list1.get(0) instanceof LSymbol);
		Assert.assertTrue(list1.get(1) instanceof List);
		Assert.assertTrue(list1.get(2) instanceof LNumber);

		// second list
		List<Object> list2 = (List<Object>) list1.get(1);

		Assert.assertTrue(list2.size() == 3);

		Assert.assertTrue(list2.get(0) instanceof LSymbol);
		Assert.assertTrue(list2.get(1) instanceof List);
		Assert.assertTrue(list2.get(2) instanceof LNumber);

		// third list
		List<Object> list3 = (List<Object>) list2.get(1);

		Assert.assertTrue(list3.size() == 2);

		Assert.assertTrue(list3.get(0) instanceof LSymbol);
		Assert.assertTrue(list3.get(1) instanceof LNumber);
	}
	
	@Test
	public void singleLineComment() {

		String singleLineComment;

		singleLineComment = "(+ \n" + "-1 \n" + "; comment \n" + "6\n" + ")";

		Assert.assertEquals(" ( +  -1  6  ) ", parser.format(singleLineComment));

		singleLineComment = "(+ \n" + "-1 \n" + ";; comment \n" + "6\n" + ")";

		Assert.assertEquals(" ( +  -1  6  ) ", parser.format(singleLineComment));

		singleLineComment = "(+ \n" + "-1 ; comment \n" + "6\n" + ")";

		Assert.assertEquals(" ( +  -1 6  ) ", parser.format(singleLineComment));
	}
	
	@Test
	public void missingInitialOpeningParenthesis() {
		
		try {
			
			parser.validate("+ a b)");
		}
		catch (LException e) {

			Assert.assertEquals("[parenthesis exception] - missing initial opening parenthesis: + a b)", e.getMessage());
			return;
		}
		
		Assert.fail();
	}
	
	@Test
	public void missingFinalClosingParenthesis() {
		
		try {
			
			parser.validate("(+ a b");
		}
		catch (LException e) {

			Assert.assertEquals("[parenthesis exception] - missing final closing parenthesis: (+ a b", e.getMessage());
			return;
		}
		
		Assert.fail();
	}
	
	@Test
	public void missingClosingParenthesis() {
		
		try {
			
			parser.validate("(+ (a b)");
		}
		catch (LException e) {

			Assert.assertEquals("[parenthesis exception] - missing closing parenthesis", e.getMessage());
			return;
		}
		
		Assert.fail();
	}
	
	@Test
	public void missingOpeningParenthesisNearIndex() {
		
		try {
			
			parser.validate("(+ (a b))(+ 1 2)");
		}
		catch (LException e) {

			Assert.assertEquals("[parenthesis exception] - missing opening parenthesis near index 8", e.getMessage());
			return;
		}
		
		Assert.fail();
	}
}