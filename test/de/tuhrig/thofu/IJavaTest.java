package de.tuhrig.thofu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tuhrig.thofu.interfaces.IInterpreter;

public class IJavaTest {

	/**
	 * The tested IInterpreter instanze
	 */
	private IInterpreter interpreter;

	@Before
	public void reset() {

		interpreter = new Interpreter();
	}
	
	/**
	 * CLASS
	 */
	
	@Test
	public void object() {

		Assert.assertEquals("class java.lang.Object", interpreter.execute("(java.lang.Object.class)"));	
	}
	
	/**
	 * OBJECT
	 */
	
	@Test
	public void newObject() {

		Assert.assertEquals(true, interpreter.execute("(java.lang.Object.)").startsWith("java.lang.Object@"));	
	}
	
	@Test
	public void newSring() {

		Assert.assertEquals("test", interpreter.execute("(java.lang.String. \"test\")"));	
		Assert.assertEquals("test1", interpreter.execute("(define a (java.lang.String. \"test1\"))"));
		Assert.assertEquals("test2", interpreter.execute("(define b (java.lang.String. \"test2\"))"));
		Assert.assertEquals("test1", interpreter.execute("(print a)"));
		Assert.assertEquals("test2", interpreter.execute("(print b)"));
	}
	
	@Test
	public void newInteger() {

		Assert.assertEquals("1", interpreter.execute("(java.lang.Integer. \"1\")"));
		Assert.assertEquals("1", interpreter.execute("(java.lang.Integer. 1)"));
		Assert.assertEquals("2", interpreter.execute("(define a (java.lang.Integer. \"2\"))"));
		Assert.assertEquals("2", interpreter.execute("(define a (java.lang.Integer. 2))"));
		Assert.assertEquals("3", interpreter.execute("(define b (java.lang.Integer. \"3\"))"));
		Assert.assertEquals("2", interpreter.execute("(print a)"));
		Assert.assertEquals("3", interpreter.execute("(print b)"));
	}
	
	@Test
	public void newBoolean() {

		Assert.assertEquals("true", interpreter.execute("(java.lang.Boolean. true)"));	
		Assert.assertEquals("true", interpreter.execute("(define a (java.lang.Boolean. true))"));
		Assert.assertEquals("false", interpreter.execute("(define b (java.lang.Boolean. false))"));
		Assert.assertEquals("true", interpreter.execute("(print a)"));
		Assert.assertEquals("false", interpreter.execute("(print b)"));
	}
	
	@Test
	public void newDouble() {

		Assert.assertEquals("1.0", interpreter.execute("(java.lang.Double. \"1\")"));	
		Assert.assertEquals("1.0", interpreter.execute("(java.lang.Double. 1)"));
		Assert.assertEquals("2.5", interpreter.execute("(define a (java.lang.Double. \"2.5\"))"));
		Assert.assertEquals("3.5", interpreter.execute("(define b (java.lang.Double. \"3.5\"))"));
		Assert.assertEquals("2.5", interpreter.execute("(print a)"));
		Assert.assertEquals("3.5", interpreter.execute("(print b)"));
	}
	
	
	@Test
	public void newBigDezimal() {

		Assert.assertEquals("3", interpreter.execute("(define a (java.math.BigDecimal. \"3\"))"));
		Assert.assertEquals("3", interpreter.execute("(define a (java.math.BigDecimal. 3))"));
		Assert.assertEquals("3", interpreter.execute("(define b (java.lang.Integer. \"3\"))"));
		Assert.assertEquals("3", interpreter.execute("(define c (java.math.BigDecimal. b))"));
	}
	
	/**
	 * INSTANZE MEMBER
	 */
	
	@Test
	public void stringInvokeMethodsWithoutParameters() {

		Assert.assertEquals("abc", interpreter.execute("(define a (java.lang.String. \"abc\"))"));
		Assert.assertEquals("abc", interpreter.execute("(.toString a)"));	
		Assert.assertEquals("3", interpreter.execute("(.length a)"));	
		Assert.assertEquals("ABC", interpreter.execute("(.toUpperCase a)"));	
	}
	
	@Test
	public void stringInvokeMethodsWithParameters() {

		Assert.assertEquals("abc", interpreter.execute("(define a (java.lang.String. \"abc\"))"));
		
		// substring(int a)
		Assert.assertEquals("1", interpreter.execute("(define b (java.lang.Integer. \"1\"))"));
		Assert.assertEquals("bc", interpreter.execute("(.substring a b)"));
		
		// substring(int a, int b)
		Assert.assertEquals("1", interpreter.execute("(define b (java.lang.Integer. \"1\"))"));
		Assert.assertEquals("3", interpreter.execute("(define c (java.lang.Integer. \"3\"))"));
		Assert.assertEquals("bc", interpreter.execute("(.substring a b c)"));	
	}
	
	@Test
	public void voidMethod() {

		Assert.assertEquals("[]", interpreter.execute("(define a (java.util.ArrayList.))"));
		Assert.assertEquals("void", interpreter.execute("(.clear a)"));
	}
	
	/**
	 * STATIC MEMBER
	 */
	
	@Test
	public void mathRound() {

		Assert.assertEquals("2.123", interpreter.execute("(define a (java.lang.Double. \"2.123\"))"));
		Assert.assertEquals("2", interpreter.execute("(java.lang.Math.round a)"));
		
		Assert.assertEquals("2.0", interpreter.execute("(define a (java.lang.Double. \"2\"))"));
		Assert.assertEquals("8.0", interpreter.execute("(define b (java.lang.Double. \"8\"))"));
		Assert.assertEquals("256.0", interpreter.execute("(java.lang.Math.pow a b)"));
	}
	
	/**
	 * STATIC FIELD
	 */
	
	@Test
	public void mathPI() {

		Assert.assertEquals("3.141592653589793", interpreter.execute("(java.lang.Math.PI$)"));
		Assert.assertEquals("3.141592653589793115997963468544185161590576171875", interpreter.execute("(define a (java.math.BigDecimal. java.lang.Math.PI$))"));
	}
	
	/**
	 * IMPORT
	 */
	
	@Test
	public void importPackage() {

		Assert.assertEquals("true", interpreter.execute("(import \"java.lang.*\")"));
		Assert.assertEquals("test", interpreter.execute("(String. \"test\")"));	
	}
	
	@Test
	public void importClass() {

		Assert.assertEquals("true", interpreter.execute("(import \"java.lang.String\")"));
		Assert.assertEquals("test", interpreter.execute("(String. \"test\")"));	
	}
	
	/**
	 * INTERFACE
	 */
	
	@Test
	public void createInterface() {
		
		Assert.assertEquals("true", interpreter.execute("(import \"java.awt.event.*\")"));
		
		String obj = 
				"(define (obj name)" + 
				"		(define (actionPerformed e) (set! control 1))" + 
				"		(if (eq? name 'actionPerformed) actionPerformed error))";
		
		
		Assert.assertEquals("<Lambda: obj>", interpreter.execute(obj));
		Assert.assertEquals("<Proxy: java.awt.event.ActionListener>", interpreter.execute("(define int (interface ActionListener.class obj))"));
		
		interpreter.execute("(define control 0)");	
		interpreter.execute("(define o (java.lang.Object.))");	
		interpreter.execute("(define i (java.lang.Integer. \"0\"))");	
		interpreter.execute("(define s (java.lang.String. \"s\"))");	
		interpreter.execute("(define e (java.awt.event.ActionEvent. o i s))");	

		Assert.assertEquals("void", interpreter.execute("(.actionPerformed int e)"));
		Assert.assertEquals("1", interpreter.execute("(control)"));
	}
	
	@Test
	public void javaUi() {
		
		Assert.assertEquals("void", interpreter.execute("(load \"examples/Java API.txt\")"));
		Assert.assertEquals("true", interpreter.execute("(.isVisible win)"));
	}
}