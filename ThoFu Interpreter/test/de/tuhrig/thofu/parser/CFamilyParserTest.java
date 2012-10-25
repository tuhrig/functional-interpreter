package de.tuhrig.thofu.parser;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.tuhrig.thofu.Interpreter;

public class CFamilyParserTest {

	private CFamilyParser parser;

	@Before
	public void reset() {

		this.parser = new CFamilyParser();
	}
	
	@Test
	public void twoOperantCalculation() {
		
//		LList list = parser.parse("1 + 1;");
//		
//		System.out.println(list + "-" + list.getClass());
//		
//		for(int i = 0; i < list.size(); i++)
//			System.out.println(list.get(i) + "-" + list.get(i).getClass());
		
		Assert.assertEquals("(+, 1, 2)", parser.parse("1 + 2;").toString());
		Assert.assertEquals("(-, 1, 2)", parser.parse("1 - 2;").toString());
		Assert.assertEquals("(*, 1, 2)", parser.parse("1 * 2;").toString());
		Assert.assertEquals("(/, 1, 2)", parser.parse("1 / 2;").toString());
		Assert.assertEquals("(%, 1, 2)", parser.parse("1 % 2;").toString());
		Assert.assertEquals("(<, 1, 2)", parser.parse("1 < 2;").toString());
		Assert.assertEquals("(<=, 1, 2)", parser.parse("1 <= 2;").toString());
		Assert.assertEquals("(>, 1, 2)", parser.parse("1 > 2;").toString());
		Assert.assertEquals("(>=, 1, 2)", parser.parse("1 >= 2;").toString());
	}
	
	@Test
	public void executeTest() {
		
		Interpreter i = new Interpreter();
		
		Assert.assertEquals("3", i.execute(parser.parse("1 + 2;")));
		Assert.assertEquals("3", i.execute(parser.parse("2 + 1;")));
		Assert.assertEquals("3", i.execute(parser.parse("1 + 1 + 1;")));
	}
	
	@Test
	public void longCalculationPlus() {
		
		Assert.assertEquals("(+, (+, 1, 2), 3)", parser.parse("1 + 2 + 3;").toString());
		Assert.assertEquals("(+, (+, (+, 1, 2), 3), 4)", parser.parse("1 + 2 + 3 + 4;").toString());
	}
	
	@Test
	public void longCalculationMinus() {
		
		Assert.assertEquals("(+, (-, 1, 2), 3)", parser.parse("1 - 2 + 3;").toString());
		Assert.assertEquals("(-, (-, 1, 2), 3)", parser.parse("1 - 2 - 3;").toString());
	}
	
	@Test
	public void longCalculationWithParanthesis() {
		
		Assert.assertEquals("(+, 1, (+, 2, 3))", parser.parse("1 + (2 + 3);").toString());
		Assert.assertEquals("(+, (+, 1, 2), 3)", parser.parse("(1 + 2) + 3;").toString());
		Assert.assertEquals("(+, (+, 2, 1), (+, 2, 3))", parser.parse("2 + 1 + (2 + 3);").toString());
	}
	
	@Test
	public void define() {
		
		Assert.assertEquals("(define, a, 1)", parser.parse("a = 1;").toString());
		Assert.assertEquals("(define, a, (+, 1, 2))", parser.parse("a = (1 + 2);").toString());
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
	}
	
	@Test
	public void methodWithSingleInstruction() {
		
		Assert.assertEquals("(lambda, (), (let, ((+, 1, 1))))", parser.parse("function() { 1 + 1; }").toString());
		Assert.assertEquals("(lambda, (x), (let, ((+, x, 1))))", parser.parse("function(x) { x + 1; }").toString());
		Assert.assertEquals("(lambda, (x, y), (let, ((+, x, y))))", parser.parse("function(x, y) { x + y; }").toString());
	
		// just to control if it is really really right ;)
		// Assert.assertEquals("(lambda, (x), (let, ((+, x, 1))))", new Parser().parse("(lambda (x) (let ((+ x 1))))").toString());
	}
	
	@Test
	public void methodWithMultipleInstruction() {
		
		Assert.assertEquals("(lambda, (x), (let, ((+, x, 1), (+, x, 1))))", parser.parse("function(x) { x + 1; x + 1; }").toString());
		Assert.assertEquals("(lambda, (x), (let, ((+, x, 1), (+, x, 1), (+, x, 1))))", parser.parse("function(x) { x + 1; x + 1; x + 1;}").toString());
		Assert.assertEquals("(lambda, (x), (let, ((+, x, 1), (+, x, 1), (+, x, 1), (+, x, 1))))", parser.parse("function(x) { x + 1; x + 1; x + 1; x + 1;}").toString());
		Assert.assertEquals("(lambda, (x), (let, ((+, x, 1), (+, x, 1), (+, x, 1), (+, x, 1), (+, x, 1))))", parser.parse("function(x) { x + 1; x + 1; x + 1; x + 1; x + 1;}").toString());
	}
	
	@Test
	public void methodAssignment() {
	
		Assert.assertEquals("(define, a, (lambda, (x), (let, ((+, x, 1)))))", parser.parse("a = function(x) { x + 1; }").toString());
	}
}
