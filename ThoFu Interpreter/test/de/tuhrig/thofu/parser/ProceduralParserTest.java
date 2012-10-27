package de.tuhrig.thofu.parser;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.tuhrig.thofu.Interpreter;

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
		
		Assert.assertEquals("(define, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("(define, a, (+, 1, (2)))", parser.parse("a = (1 + 2);").toString());
		Assert.assertEquals("(define, a, (+, 1, (2)))", parser.parse("a = 1 + 2;").toString());
	}
	
	@Test
	public void defineString() {
		
		Assert.assertEquals("(define, a, (test))", parser.parse("a = \"test\";").toString());
		
		Assert.assertEquals("test", execute("a = \"test\";"));
		Assert.assertEquals("test", execute("print(a);"));
	}
	
	@Test
	public void defineSymbol() {
		
		Assert.assertEquals("(define, a, (b))", parser.parse("a = b;").toString());
		
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("1", execute("b = a;"));
		Assert.assertEquals("1", execute("print(b);"));
	}
	
	@Test
	public void defineFunction() {
		
		Assert.assertEquals("(define, a, (lambda, (), (begin, ((print, (1))))))", parser.parse("a = function() { print(1); }").toString());
		
		Assert.assertEquals("<Lambda: a>", execute("a = function() { print(1); }"));
		Assert.assertEquals("1", execute("a;"));
		Assert.assertEquals("1", execute("a();"));
		
		Assert.assertEquals("(define, a, (lambda, (text), (begin, ((print, (text))))))", parser.parse("a = function(text) { print(text); }").toString());
		
		Assert.assertEquals("<Lambda: a>", execute("a = function(text) { print(text); }"));
		Assert.assertEquals("hallo", execute("a(\"hallo\");"));
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
		
		Assert.assertEquals("(lambda, (), (begin, ((+, 1, (1)))))", parser.parse("function() { 1 + 1; }").toString());
		Assert.assertEquals("(lambda, (x), (begin, ((+, x, (1)))))", parser.parse("function(x) { x + 1; }").toString());
		Assert.assertEquals("(lambda, (x, y), (begin, ((+, x, (y)))))", parser.parse("function(x, y) { x + y; }").toString());
	
		// just to control if it is really really right ;)
		// Assert.assertEquals("(lambda, (x), (let, ((+, x, 1))))", new Parser().parse("(lambda (x) (let ((+ x 1))))").toString());
	}
	
	@Test
	public void methodWithMultipleInstruction() {
		
		Assert.assertEquals("(lambda, (x), (begin, ((+, x, (1)), (+, x, (1)))))", parser.parse("function(x) { x + 1; x + 1; }").toString());
		Assert.assertEquals("(lambda, (x), (begin, ((+, x, (1)), (+, x, (1)), (+, x, (1)))))", parser.parse("function(x) { x + 1; x + 1; x + 1;}").toString());
		Assert.assertEquals("(lambda, (x), (begin, ((+, x, (1)), (+, x, (1)), (+, x, (1)), (+, x, (1)))))", parser.parse("function(x) { x + 1; x + 1; x + 1; x + 1;}").toString());
		Assert.assertEquals("(lambda, (x), (begin, ((+, x, (1)), (+, x, (1)), (+, x, (1)), (+, x, (1)), (+, x, (1)))))", parser.parse("function(x) { x + 1; x + 1; x + 1; x + 1; x + 1;}").toString());
	}
	
	@Test
	public void methodAssignment() {
	
		Assert.assertEquals("(define, a, (lambda, (x), (begin, ((+, x, (1))))))", parser.parse("a = function(x) { x + 1; }").toString());
	}
	
	@Test
	public void ifWithoutElse() {
		
		Assert.assertEquals("(if, (a), (begin, ((print, (true)))), ())", parser.parse("if(a) { print(true); }").toString());
		Assert.assertEquals("(if, (a), (begin, ((print, (1)), (print, (2)))), ())", parser.parse("if(a) { print(1); print(2); }").toString());
		Assert.assertEquals("(if, (a), (begin, ((print, (1)), (print, (2)), (print, (3)))), ())", parser.parse("if(a) { print(1); print(2); print(3); }").toString());
	}
	
	@Test
	public void ifWitElse() {
		
		Assert.assertEquals("(if, (a), (begin, ((print, (true)))), (begin, ((print, (false)))))", parser.parse("if(a) { print(true); } else { print(false); }").toString());
		Assert.assertEquals("(if, (>, 1, (5)), (begin, ((print, (true)))), (begin, ((print, (false)))))", parser.parse("if(1 > 5) { print(true); } else { print(false); }").toString());
		Assert.assertEquals("(if, (<, 1, (5)), (begin, ((print, (true)))), (begin, ((print, (false)))))", parser.parse("if(1 < 5) { print(true); } else { print(false); }").toString());
	}
	
	@Test
	public void plusPlusAfter() {
	
		Assert.assertEquals("(define, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (define, tmp, a), (set!, a, (+, a, 1)), (tmp))))", parser.parse("a++;").toString());
	
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("1", execute("a++;"));
		Assert.assertEquals("2", execute("a;"));
	}
	
	@Test
	public void plusPlusBefore() {
	
		Assert.assertEquals("(define, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (set!, a, (+, a, 1)), (a))))", parser.parse("++a;").toString());
	
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("2", execute("++a;"));
		Assert.assertEquals("2", execute("a;"));
	}
	
	@Test
	public void minusMinusAfter() {
	
		Assert.assertEquals("(define, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (define, tmp, a), (set!, a, (-, a, 1)), (tmp))))", parser.parse("a--;").toString());
	
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("1", execute("a--;"));
		Assert.assertEquals("0", execute("a;"));
	}
	
	@Test
	public void minusMinusBefore() {
	
		Assert.assertEquals("(define, a, (1))", parser.parse("a = 1;").toString());
		Assert.assertEquals("((lambda, (), (begin, (set!, a, (-, a, 1)), (a))))", parser.parse("--a;").toString());
	
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("0", execute("--a;"));
		Assert.assertEquals("0", execute("a;"));
	}
	
	@Test
	public void plusPlusMinusMinus() {
		
		Assert.assertEquals("1", execute("a = 1;"));
		Assert.assertEquals("5", execute("b = 4 + a++;"));
	}
	
	@Test
	public void forLoop() {
		
		Assert.assertEquals("(for, (define, i, (0)), (<, i, (5)), ((lambda, (), (begin, (define, tmp, i), (set!, i, (+, i, 1)), (tmp)))), (begin, ((print, (i)))))", parser.parse("for(i = 0; i < 5; i++) { print(i); }").toString());
		Assert.assertEquals("4", execute("for(i = 0; i < 5; i++) { print(i); }"));
	}
	
	@Test
	public void whileLoop() {
		
		Assert.assertEquals("0", execute("i = 0;"));
		Assert.assertEquals("4", execute("while(i < 5) { i++; print(i); }"));
	}
	
	@Test
	public void doLoop() {
		
		Assert.assertEquals("0", execute("i = 0;"));
		Assert.assertEquals("4", execute("do(i < 5) { i++; print(i); }"));
	}
}