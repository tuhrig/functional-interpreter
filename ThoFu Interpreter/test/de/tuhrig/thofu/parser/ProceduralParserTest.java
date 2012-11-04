package de.tuhrig.thofu.parser;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.tuhrig.thofu.Interpreter;
import de.tuhrig.thofu.types.LException;

public class ProceduralParserTest {

	private ProceduralParser parser;

	Interpreter interpreter = new Interpreter();
	
	@Before
	public void reset() {

		this.parser = new ProceduralParser();
		
		this.interpreter = new Interpreter();
	}
	
	private String execute(String string) {

		return interpreter.execute(parser.parse(string));
	}
	
	private String print(String string) {

		execute(string);
		
		String content = interpreter.getStringBuilder().toString();
		
		interpreter.setStringBuilder(new StringBuilder());
		
		return content;
	}

	
	@Test
	public void twoOperantCalculation() {
		
//		LList list = parser.parse("1 + 1;");
//		
//		System.out.println(list + "-" + list.getClass());
//		
//		for(int i = 0; i < list.size(); i++)
//			System.out.println(list.get(i) + "-" + list.get(i).getClass());
		
		Assert.assertEquals("(+, 1, (2))", parser.parse("1 + 2;").toString());
		Assert.assertEquals("(-, 1, (2))", parser.parse("1 - 2;").toString());
		Assert.assertEquals("(*, 1, (2))", parser.parse("1 * 2;").toString());
		Assert.assertEquals("(/, 1, (2))", parser.parse("1 / 2;").toString());
		Assert.assertEquals("(%, 1, (2))", parser.parse("1 % 2;").toString());
		Assert.assertEquals("(<, 1, (2))", parser.parse("1 < 2;").toString());
		Assert.assertEquals("(<=, 1, (2))", parser.parse("1 <= 2;").toString());
		Assert.assertEquals("(>, 1, (2))", parser.parse("1 > 2;").toString());
		Assert.assertEquals("(>=, 1, (2))", parser.parse("1 >= 2;").toString());
	}
	
	@Test
	public void executeTest() {

		Assert.assertEquals("3", execute("1 + 2;"));
		Assert.assertEquals("3", execute("2 + 1;"));
		Assert.assertEquals("3", execute("1 + 1 + 1;"));
	}

	@Test
	public void longCalculationPlus() {
		
		Assert.assertEquals("(+, 1, (+, 2, (3)))", parser.parse("1 + 2 + 3;").toString());
		Assert.assertEquals("(+, 1, (+, 2, (+, 3, (4))))", parser.parse("1 + 2 + 3 + 4;").toString());
	}
	
	@Test
	public void longCalculationMinus() {
		
		Assert.assertEquals("(-, 1, (+, 2, (3)))", parser.parse("1 - 2 + 3;").toString());
		Assert.assertEquals("(-, 1, (-, 2, (3)))", parser.parse("1 - 2 - 3;").toString());
	}
	
	@Test
	public void longCalculationWithParanthesis() {
		
		Assert.assertEquals("(+, 1, (+, 2, (3)))", parser.parse("1 + (2 + 3);").toString());
		Assert.assertEquals("(+, (+, 1, (2)), (3))", parser.parse("(1 + 2) + 3;").toString());
		Assert.assertEquals("(+, (+, 2, (1)), (+, 2, (3)))", parser.parse("(2 + 1) + (2 + 3);").toString());
	}
	
	@Test
	public void defineNumber() {
		
		Assert.assertEquals("(define, a, '(null))", parser.parse("var a;").toString());
		Assert.assertEquals("(define, a, (1))", parser.parse("var a = 1;").toString());
		Assert.assertEquals("(define, a, (+, 1, (2)))", parser.parse("var a = (1 + 2);").toString());
		Assert.assertEquals("(define, a, (+, 1, (2)))", parser.parse("var a = 1 + 2;").toString());
	}
	
	@Test
	public void assignNumber() {
		
		Assert.assertEquals("(set!, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("(set!, a, (+, 1, (2)))", parser.parse("a = (1 + 2);").toString());
		Assert.assertEquals("(set!, a, (+, 1, (2)))", parser.parse("a = 1 + 2;").toString());
	}
	
	@Test
	public void assignString() {
		
		Assert.assertEquals("(set!, a, (test))", parser.parse("a = \"test\";").toString());
	
		execute("var a;");
		Assert.assertEquals("test", execute("a = \"test\";"));
		Assert.assertEquals("test", execute("print(a);"));
	}
	
	@Test
	public void assignSymbol() {
		
		Assert.assertEquals("(set!, a, (b))", parser.parse("a = b;").toString());
		
		execute("var a;");
		execute("var b;");
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("1", execute("b = a;"));
		Assert.assertEquals("1", execute("print(b);"));
	}
	
	@Test
	public void assignFunction() {
		
		Assert.assertEquals("(set!, a, (lambda, (), (begin, (print, (1)))))", parser.parse("a = function() { print(1); }").toString());
		
		Assert.assertEquals("<Lambda: a>", execute("var a = function() { print(1); }"));
		Assert.assertEquals("1", execute("a;"));
		Assert.assertEquals("1", execute("a();"));
		
		Assert.assertEquals("(set!, a, (lambda, (text), (begin, (print, (text)))))", parser.parse("a = function(text) { print(text); }").toString());
		
		Assert.assertEquals("<Lambda: unnamed lambda>", execute("a = function(text) { print(text); }"));
		Assert.assertEquals("hallo", execute("a(\"hallo\");"));
	}
	
	@Test
	public void complexFunction() {
		
		String command = 
			"var a = function(){ " + 
			
				"print(9); " + 
				"print(2); " + 
	
				"var b = 1; " + 
	
				"for(var a = 0; a < 5; a++) { " + 
					
				"	print(3); " + 
				"} " + 
			"}";
		
		Assert.assertEquals("<Lambda: a>", execute(command));
		Assert.assertEquals("9233333", print("a();"));
	}

	@Test
	public void methodCallSimple() {
		
		Assert.assertEquals("(add, (1), (2))", parser.parse("add(1, 2);").toString());
		Assert.assertEquals("(print, (1))", parser.parse("print(1);").toString());
	}
	
	@Test
	public void methodCallInMethodCall() {
		
		Assert.assertEquals("(add, (add, (1), (1)), (2))", parser.parse("add(add(1, 1), 2);").toString());
		Assert.assertEquals("(add, (2), (add, (1), (1)))", parser.parse("add(2, add(1, 1));").toString());
		Assert.assertEquals("(add, (add, (2), (2)), (add, (1), (1)))", parser.parse("add(add(2, 2), add(1, 1));").toString());
	}
	
	@Test
	public void methodCallChain() {
		
		Assert.assertEquals("(print, (get))", parser.parse("get().print();").toString());
		Assert.assertEquals("(print, (add, (1), (2)))", parser.parse("add(1, 2).print();").toString());
		Assert.assertEquals("(print, (add, (1), (2)), (x))", parser.parse("add(1, 2).print(x);").toString());
	}
	
	@Test
	public void methodWithSingleInstruction() {
		
		Assert.assertEquals("(lambda, (), (begin, (+, 1, (1))))", parser.parse("function() { 1 + 1; }").toString());
		Assert.assertEquals("(lambda, (x), (begin, (+, x, (1))))", parser.parse("function(x) { x + 1; }").toString());
		Assert.assertEquals("(lambda, (x, y), (begin, (+, x, (y))))", parser.parse("function(x, y) { x + y; }").toString());
	
		// just to control if it is really really right ;)
		// Assert.assertEquals("(lambda, (x), (let, ((+, x, 1))))", new Parser().parse("(lambda (x) (let ((+ x 1))))").toString());
	}
	
	@Test
	public void methodWithMultipleInstruction() {
		
		Assert.assertEquals("(lambda, (x), (begin, (+, x, (1)), (+, x, (1))))", parser.parse("function(x) { x + 1; x + 1; }").toString());
		Assert.assertEquals("(lambda, (x), (begin, (+, x, (1)), (+, x, (1)), (+, x, (1))))", parser.parse("function(x) { x + 1; x + 1; x + 1;}").toString());
		Assert.assertEquals("(lambda, (x), (begin, (+, x, (1)), (+, x, (1)), (+, x, (1)), (+, x, (1))))", parser.parse("function(x) { x + 1; x + 1; x + 1; x + 1;}").toString());
		Assert.assertEquals("(lambda, (x), (begin, (+, x, (1)), (+, x, (1)), (+, x, (1)), (+, x, (1)), (+, x, (1))))", parser.parse("function(x) { x + 1; x + 1; x + 1; x + 1; x + 1;}").toString());
	}
	
	@Test
	public void methodAssignment() {
	
		Assert.assertEquals("(set!, a, (lambda, (x), (begin, (+, x, (1)))))", parser.parse("a = function(x) { x + 1; }").toString());
	}
	
	@Test
	public void ifWithoutElse() {
		
		Assert.assertEquals("(if, (a), (begin, (print, (true))), ())", parser.parse("if(a) { print(true); }").toString());
		Assert.assertEquals("(if, (a), (begin, (print, (1)), (print, (2))), ())", parser.parse("if(a) { print(1); print(2); }").toString());
		Assert.assertEquals("(if, (a), (begin, (print, (1)), (print, (2)), (print, (3))), ())", parser.parse("if(a) { print(1); print(2); print(3); }").toString());
	}
	
	@Test
	public void ifWitElse() {
		
		Assert.assertEquals("(if, (a), (begin, (print, (true))), (begin, (print, (false))))", parser.parse("if(a) { print(true); } else { print(false); }").toString());
		Assert.assertEquals("(if, (>, 1, (5)), (begin, (print, (true))), (begin, (print, (false))))", parser.parse("if(1 > 5) { print(true); } else { print(false); }").toString());
		Assert.assertEquals("(if, (<, 1, (5)), (begin, (print, (true))), (begin, (print, (false))))", parser.parse("if(1 < 5) { print(true); } else { print(false); }").toString());
	}
	
	@Test
	public void plusPlusAfter() {
	
		Assert.assertEquals("(set!, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (define, tmp, a), (set!, a, (+, a, 1)), (tmp))))", parser.parse("a++;").toString());
	
		Assert.assertEquals("1", execute("var a = 1;"));
		Assert.assertEquals("1", execute("a++;"));
		Assert.assertEquals("2", execute("a;"));
	}
	
	@Test
	public void plusPlusBefore() {
	
		Assert.assertEquals("(set!, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (set!, a, (+, a, 1)), (a))))", parser.parse("++a;").toString());
	
		Assert.assertEquals("1", execute("var a = 1;"));
		Assert.assertEquals("2", execute("++a;"));
		Assert.assertEquals("2", execute("a;"));
	}
	
	@Test
	public void minusMinusAfter() {
	
		Assert.assertEquals("(set!, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (define, tmp, a), (set!, a, (-, a, 1)), (tmp))))", parser.parse("a--;").toString());
	
		Assert.assertEquals("1", execute("var a = 1;"));
		Assert.assertEquals("1", execute("a--;"));
		Assert.assertEquals("0", execute("a;"));
	}
	
	@Test
	public void minusMinusBefore() {
	
		Assert.assertEquals("(set!, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (set!, a, (-, a, 1)), (a))))", parser.parse("--a;").toString());
	
		Assert.assertEquals("1", execute("var a = 1;"));
		Assert.assertEquals("0", execute("--a;"));
		Assert.assertEquals("0", execute("a;"));
	}
	
	@Test
	public void plusPlusMinusMinus() {
		
		Assert.assertEquals("1", execute("var a = 1;"));
		Assert.assertEquals("5", execute("var b = 4 + a++;"));
	}
	
	@Test
	public void forLoop() {
		
		Assert.assertEquals("(for, (define, i, (0)), (<, i, (5)), ((lambda, (), (begin, (define, tmp, i), (set!, i, (+, i, 1)), (tmp)))), (begin, (print, (i))))", parser.parse("for(var i = 0; i < 5; i++) { print(i); }").toString());
		Assert.assertEquals("4", execute("for(var i = 0; i < 5; i++) { print(i); }"));
	}
	
	@Test
	public void whileLoop() {
		
		Assert.assertEquals("0", execute("var i = 0;"));
		Assert.assertEquals("5", execute("while(i < 5) { i++; print(i); }"));
	}
	
	@Test
	public void doLoop() {
		
		Assert.assertEquals("0", execute("var i = 0;"));
		Assert.assertEquals("5", execute("do(i < 5) { i++; print(i); }"));
	}
	
	@Test
	public void validation() {
		
		try {
			
			parser.validate("var a = 3");
		}
		catch(LException e) {
			
			Assert.assertEquals("Missing termination character", e.getMessage());
			
			return;
		}
		
		Assert.fail();
	}
	
	/**
	 * HELPER
	 */
	
	@Test
	public void reduce() {
		
		List<Object> tokens = null;
		
		tokens = parser.toTokens(parser.format("var i = 0;"));
		Assert.assertEquals("[var, i, =, 0, ;]", parser.reduce(tokens).toString());
		
		tokens = parser.toTokens(parser.format("var i = 0; var r = 1;"));
		Assert.assertEquals("[var, i, =, 0, ;]", parser.reduce(tokens).toString());
		
		tokens = parser.toTokens(parser.format("function m(){ test; }"));
		Assert.assertEquals("[function, m, (, ), {, test, ;, }]", parser.reduce(tokens).toString());
		
		tokens = parser.toTokens(parser.format("var t = 5; function m(){ test; }"));
		Assert.assertEquals("[var, t, =, 5, ;]", parser.reduce(tokens).toString());
		
		tokens = parser.toTokens(parser.format("function m(){ test; } var t = 5;"));
		Assert.assertEquals("[function, m, (, ), {, test, ;, }]", parser.reduce(tokens).toString());
		
		tokens = parser.toTokens(parser.format("function m(){ test; { test; } } var t = 5;"));
		Assert.assertEquals("[function, m, (, ), {, test, ;, {, test, ;, }, }]", parser.reduce(tokens).toString());
	}
}