package de.tuhrig.thofu;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.tuhrig.thofu.interfaces.IInterpreter;

public class IInterpreterTest {

	/**
	 * The tested IInterpreter instance
	 */
	private IInterpreter interpreter;

	@Before
	public void reset() {

		interpreter = new Interpreter();
	}

	/**
	 * POSITIVE TESTS
	 * 
	 * Positive tests are common tests that test a basic functionality that
	 * should work. Therefore they are very short, but many. They cover most of
	 * the test cases.
	 */

	@Test
	public void empty() {

		Assert.assertEquals("'(1)", interpreter.execute("'(1)"));
	}

	@Test
	public void plus() {

		Assert.assertEquals("1", interpreter.execute("(+ 1)"));
		Assert.assertEquals("2", interpreter.execute("(+ 1 1)"));
		Assert.assertEquals("6", interpreter.execute("(+ 5 1)"));
		Assert.assertEquals("4", interpreter.execute("(+ (+ 1 2) 1)"));
		Assert.assertEquals("4", interpreter.execute("(+ (+ 1 1) (+ 1 1))"));
		Assert.assertEquals("15", interpreter.execute("(+ 1 2 3 4 5)"));
		Assert.assertEquals("15", interpreter.execute("(+ 1 (+ 2 (+ 3 4 5)))"));
	}

	@Test
	public void minus() {

		Assert.assertEquals("8", interpreter.execute("(- 9 1)"));
		Assert.assertEquals("8", interpreter.execute("(- 9 (- 1))"));
		Assert.assertEquals("7", interpreter.execute("(- 9 1 1)"));
		Assert.assertEquals("8", interpreter.execute("(- 9 (- 1))"));
		Assert.assertEquals("5", interpreter.execute("(- 9 1 1 1 1)"));
		Assert.assertEquals("7", interpreter.execute("(- 9 (- 1 (- 1 1 1)))"));
	}

	@Test
	public void multiply() {

		Assert.assertEquals("9", interpreter.execute("(* 3 3)"));
		Assert.assertEquals("0", interpreter.execute("(* 0 0)"));
		Assert.assertEquals("0", interpreter.execute("(* 1 0)"));
		Assert.assertEquals("0", interpreter.execute("(* 1 0 1)"));
		Assert.assertEquals("1", interpreter.execute("(* 1 1 1)"));
		Assert.assertEquals("5", interpreter.execute("(* 1 5 1)"));
	}

	@Test
	public void devide() {

		Assert.assertEquals("2", interpreter.execute("(/ 4 2)"));
		Assert.assertEquals("0", interpreter.execute("(/ 0 2)"));
		Assert.assertEquals("4", interpreter.execute("(/ 8 2)"));
		Assert.assertEquals("2.5", interpreter.execute("(/ 5 2)"));
		Assert.assertEquals("8", interpreter.execute("(/ 16 (/ 4 2))"));
		Assert.assertEquals("4", interpreter.execute("(/ 16 (/ 4 (/ 2 2)))"));
	}

	@Test
	public void remainder() {

		Assert.assertEquals("0", interpreter.execute("(% 4 2)"));
		Assert.assertEquals("0", interpreter.execute("(% 0 2)"));
		Assert.assertEquals("2", interpreter.execute("(% 8 3)"));
		Assert.assertEquals("1", interpreter.execute("(% 5 2)"));
		Assert.assertEquals("1", interpreter.execute("(% 17 (% 5 3))"));
	}

	@Test
	public void mix() {

		Assert.assertEquals("7", interpreter.execute("(+ 1 (+ 2 (- 5 1)))"));
		Assert.assertEquals("7", interpreter.execute("(+ 1 (+ 2 (* 1 4)))"));
		Assert.assertEquals("-1", interpreter.execute("(+ 1 (- 2 (* 1 4)))"));
		Assert.assertEquals("-1", interpreter.execute("(+ 1 (- 2 (/ 12 3)))"));
		Assert.assertEquals("-1", interpreter.execute("(* 1 (+ 1 (- 2 (/ 12 3))))"));
		Assert.assertEquals("0", interpreter.execute("(+ 1 (* 1 (+ 1 (- 2 (/ 12 3)))))"));
	}

	@Test
	public void greaterThan() {

		Assert.assertEquals("true", interpreter.execute("(> 4 2)"));
		Assert.assertEquals("false", interpreter.execute("(> 2 4)"));
		Assert.assertEquals("true", interpreter.execute("(> 4 (+ 1 2))"));
		Assert.assertEquals("false", interpreter.execute("(> 2 (- 4 1))"));
	}

	@Test
	public void lessThan() {

		Assert.assertEquals("true", interpreter.execute("(< 2 4)"));
		Assert.assertEquals("false", interpreter.execute("(< 4 2)"));
		Assert.assertEquals("true", interpreter.execute("(< 2 (+ 4 5))"));
		Assert.assertEquals("false", interpreter.execute("(< (+ 1 4) (+ 2 1))"));
	}

	@Test
	public void greaterEqualsThan() {

		Assert.assertEquals("true", interpreter.execute("(>= 4 2)"));
		Assert.assertEquals("false", interpreter.execute("(>= 2 4)"));
		Assert.assertEquals("true", interpreter.execute("(>= 4 (+ 1 2))"));
		Assert.assertEquals("false", interpreter.execute("(>= 2 (- 4 1))"));
		Assert.assertEquals("true", interpreter.execute("(>= 2 2)"));
	}

	@Test
	public void lessEqualsThan() {

		Assert.assertEquals("true", interpreter.execute("(<= 2 4)"));
		Assert.assertEquals("false", interpreter.execute("(<= 4 2)"));
		Assert.assertEquals("true", interpreter.execute("(<= 2 (+ 4 5))"));
		Assert.assertEquals("false", interpreter.execute("(<= (+ 1 4) (+ 2 1))"));
		Assert.assertEquals("true", interpreter.execute("(>= 2 2)"));
	}

	@Test
	public void equals() {

		Assert.assertEquals("true", interpreter.execute("(eq? 2 2)"));
		Assert.assertEquals("false", interpreter.execute("(eq? 4 2)"));
		Assert.assertEquals("true", interpreter.execute("(eq? (+ 0 2) 2)"));
		Assert.assertEquals("false", interpreter.execute("(eq? (- 10 4) 2)"));
	}

	@Test
	public void equalsOperations() {
		
		Assert.assertEquals("<Operation: +>", interpreter.execute("(define x +)"));
		Assert.assertEquals("<Operation: +>", interpreter.execute("(define y +)"));
		Assert.assertEquals("true", interpreter.execute("(eq? x y)"));
		Assert.assertEquals("true", interpreter.execute("(eq? y x)"));
		Assert.assertEquals("true", interpreter.execute("(eq? + x)"));
		Assert.assertEquals("true", interpreter.execute("(eq? y +)"));
	}
	
	@Test
	public void equalsSymbols() {

		Assert.assertEquals("true", interpreter.execute("(eq? 'x 'x)"));
		Assert.assertEquals("false", interpreter.execute("(eq? 'y 'x)"));
	}
	
	@Test
	public void equalsNull() {

		Assert.assertEquals("true", interpreter.execute("(eq? null null)"));
	}
	
	@Test
	public void notEquals() {

		Assert.assertEquals("false", interpreter.execute("(nq? 2 2)"));
		Assert.assertEquals("true", interpreter.execute("(nq? 4 2)"));
		Assert.assertEquals("false", interpreter.execute("(nq? (+ 0 2) 2)"));
		Assert.assertEquals("true", interpreter.execute("(nq? (- 10 4) 2)"));
	}
	
	@Test
	public void varSet() {

		Assert.assertEquals("1000", interpreter.execute("(define x 1000)"));
		Assert.assertEquals("2000", interpreter.execute("(define x (+ 1000 1000))"));
	}

	@Test
	public void varReading() {

		Assert.assertEquals("3.141", interpreter.execute("(print pi)"));
	}

	@Test
	public void varUseing() {

		Assert.assertEquals("4.141", interpreter.execute("(+ 1 pi)"));
	}

	@Test
	public void buildInLambdas() {

		Assert.assertEquals("20", interpreter.execute("(double 10)"));
		Assert.assertEquals("100", interpreter.execute("(sqr 10)"));
		Assert.assertEquals("2", interpreter.execute("(inc 1)"));
		Assert.assertEquals("6", interpreter.execute("(inc 5)"));
	}

	@Test
	public void defineVariable() {

		Assert.assertEquals("10", interpreter.execute("(define var 10)"));
		Assert.assertEquals("10", interpreter.execute("(print var)"));
	}

	@Test
	public void defineLambda() {

		Assert.assertEquals("<Lambda: dec1>", interpreter.execute("(define dec1 (lambda (n) (- n 1)))"));
		Assert.assertEquals("3", interpreter.execute("(dec1 4)"));
		Assert.assertEquals("7", interpreter.execute("(dec1 8)"));
		Assert.assertEquals("-1", interpreter.execute("(dec1 0)"));
	}

	@Test
	public void defineLambdaWithShortcut1() {

		Assert.assertEquals("<Lambda: dec2>", interpreter.execute("(define (dec2 (n)) (- n 1))"));
		Assert.assertEquals("3", interpreter.execute("(dec2 4)"));
		Assert.assertEquals("7", interpreter.execute("(dec2 8)"));
		Assert.assertEquals("-1", interpreter.execute("(dec2 0)"));
	}

	@Test
	public void defineLambdaWithShortcut2() {

		// one argument (a list)
		Assert.assertEquals("<Lambda: add>", interpreter.execute("(define (add (n r)) (+ n r))"));
		Assert.assertEquals("8", interpreter.execute("(add 4 4)"));

		// many arguments (like a list)
		Assert.assertEquals("<Lambda: add2>", interpreter.execute("(define (add2 n r) (+ n r))"));
		Assert.assertEquals("8", interpreter.execute("(add2 4 4)"));
	}

	@Test
	public void set() {

		Assert.assertEquals("1", interpreter.execute("(define var1 1)"));
		Assert.assertEquals("2", interpreter.execute("(set! var1 2)"));
		Assert.assertEquals("var2 is undefined", interpreter.execute("(set! var2 2)"));
	}

	@Test
	public void unonymesLambda() {

		Assert.assertEquals("2", interpreter.execute("((lambda (x) (+ x x)) 1)"));
		Assert.assertEquals("4", interpreter.execute("((lambda (x) (+ x x)) ((lambda (x) (+ x x)) 1))"));
	}

	@Test
	public void lambdaInLambda() {

		Assert.assertEquals("<Lambda: incinc>", interpreter.execute("(define (incinc (n)) (inc (inc n)))"));
		Assert.assertEquals("6", interpreter.execute("(incinc 4 )"));
	}

	@Test
	public void mixedLambda1() {

		String algorithem = "(define test (lambda (number) (+ number number)))";

		Assert.assertEquals("<Lambda: test>", interpreter.execute(algorithem));
		Assert.assertEquals("6", interpreter.execute("(test 3)"));
	}

	@Test
	public void mixedLambda2() {

		String algorithem = "(define test (lambda (number) (+ number (+ 0 number))))";

		Assert.assertEquals("<Lambda: test>", interpreter.execute(algorithem));
		Assert.assertEquals("6", interpreter.execute("(test 3)"));
	}

	@Test
	public void defineLambdaExampleFromRedmine() {

		// This is an example from the redmine wiki:
		String example1 = "(define plus1 (lambda (arg) (+ arg 1)))";
		String example2 = "(define (plus1 arg) (+ arg 1))";

		Interpreter interpreter1 = new Interpreter();
		Interpreter interpreter2 = new Interpreter();

		Object object1 = interpreter1.execute(example1);
		Assert.assertEquals("<Lambda: plus1>", object1);
		Assert.assertEquals("5", interpreter1.execute("(plus1 4)"));

		Object object2 = interpreter2.execute(example2);
		Assert.assertEquals("<Lambda: plus1>", object2);
		Assert.assertEquals("5", interpreter1.execute("(plus1 4)"));
	}

	@Test
	public void ifExpression() {

		Assert.assertEquals("\"a\"", interpreter.execute("(if true \"a\" \"b\")"));
		Assert.assertEquals("\"b\"", interpreter.execute("(if false \"a\" \"b\")"));
	}

	@Test
	public void ifExpressionWithList() {

		Assert.assertEquals("'(1 2)", interpreter.execute("(if true '(1 2) '(3 4))"));
	}

	@Test
	public void ifExpressionWithRest() {

		Assert.assertEquals("'(1 2)", interpreter.execute("(if (eq? (rest (cons 1 null)) null) '(1 2) '(3 4))"));
	}

	@Test
	public void ifExpressionWithOperation() {

		Assert.assertEquals("3", interpreter.execute("(if true (+ 1 2) (+ 3 4))"));
		Assert.assertEquals("7", interpreter.execute("(if false (+ 1 2) (+ 3 4))"));
	}

	@Test
	public void ifExpressionWithOperator() {

		Assert.assertEquals("\"a\"", interpreter.execute("(if (> 10 5) \"a\" \"b\")"));
		Assert.assertEquals("\"b\"", interpreter.execute("(if (> (+ 1 1) (- 100 1)) \"a\" \"b\")"));
	}

	@Test
	public void ifExpressionWithDefine() {

		Assert.assertEquals("true", interpreter.execute("(define wahr true)"));
		Assert.assertEquals("\"a\"", interpreter.execute("(if wahr \"a\" \"b\")"));
	}

	@Test
	public void ifExpressionNested() {

		Assert.assertEquals("\"a\"", interpreter.execute("(if (> 2 1) (if (> 3 (+ 1 1)) \"a\" \"c\") \"b\")"));
	}

	@Test
	public void ifExpressionAsResult() {

		Assert.assertEquals("2", interpreter.execute("(+ 1 (if (> 2 1) (if (> 3 (+ 1 1)) 1 3) 2))"));
		Assert.assertEquals("2", interpreter.execute("(+ (if (> 2 1) (if (> 3 (+ 1 1)) 1 3) 2) 1)"));
	}

	@Test
	public void invertBoolean() {

		Assert.assertEquals("false", interpreter.execute("(! true)"));
		Assert.assertEquals("true", interpreter.execute("(! false)"));
	}

	@Test
	public void or() {

		Assert.assertEquals("true", interpreter.execute("(|| true true)"));
		Assert.assertEquals("true", interpreter.execute("(|| true false)"));
		Assert.assertEquals("true", interpreter.execute("(|| false true)"));
		Assert.assertEquals("false", interpreter.execute("(|| false false)"));
	}

	@Test
	public void and() {

		Assert.assertEquals("true", interpreter.execute("(&& true true)"));
		Assert.assertEquals("false", interpreter.execute("(&& true false)"));
		Assert.assertEquals("false", interpreter.execute("(&& false false)"));
	}

	@Test
	public void executeNegativeNumbers() {

		Assert.assertEquals("5", interpreter.execute("(+ -1 6)"));
		Assert.assertEquals("6", interpreter.execute("(* -1 -6)"));
	}

	@Test
	public void multiLineInput() {

		Assert.assertEquals("5", interpreter.execute("(+ -1 6)"));
		Assert.assertEquals("5", interpreter.execute("(+ \n -1 \n \n 6 \n)"));
	}

	@Test
	public void singleLineComment() {

		String singleLineComment;

		singleLineComment = "(+ \n" + "-1 \n" + "; comment \n" + "6\n" + ")";

		Assert.assertEquals("5", interpreter.execute(singleLineComment));

		singleLineComment = "(+ \n" + "-1 \n" + ";; comment \n" + "6\n" + ")";

		Assert.assertEquals("5", interpreter.execute(singleLineComment));

		singleLineComment = "(+ \n" + "-1 ; comment \n" + "6\n" + ")";

		Assert.assertEquals("5", interpreter.execute(singleLineComment));
	}

	@Test
	public void print() {

		Assert.assertEquals("\"aaa\"", interpreter.execute("(print \"aaa\")"));
		Assert.assertEquals("2", interpreter.execute("(print (+ 1 1))"));
		Assert.assertEquals("3.141", interpreter.execute("(print pi)"));
		Assert.assertEquals("<Operation: +>", interpreter.execute("(print +)"));
	}

	@Test
	public void stringInQuotes() {

		Assert.assertEquals("\"a a a\"", interpreter.execute("(print \"a a a\")"));
	}

	@Test
	public void cons() {

		Assert.assertEquals("'(1 . 2)", interpreter.execute("(cons 1 2)"));
		Assert.assertEquals("'(1)", interpreter.execute("(cons 1 null)"));
		Assert.assertEquals("'(() . 1)", interpreter.execute("(cons null 1)"));
		Assert.assertEquals("'((1 . 2) . 3)", interpreter.execute("(cons (cons 1 2) 3)"));
	}

	@Test
	public void touchingBrakets() {

		Assert.assertEquals("<Lambda: append>", interpreter.execute("(define (append l1 l2) (if (eq? l1 null) l2 (cons (first l1) (append (rest l1) l2))))"));
		Assert.assertEquals("'(1 2 3 4)", interpreter.execute("(append '(1 2) '(3 4))"));
	}

	@Test
	public void first() {

		Assert.assertEquals("1", interpreter.execute("(first (cons 1 2))"));
		Assert.assertEquals("'(1 . 2)", interpreter.execute("(first (cons (cons 1 2) null))"));
	}

	@Test
	public void rest() {

		Assert.assertEquals("'(null)", interpreter.execute("(rest (cons 1 null))"));
		Assert.assertEquals("'(2)", interpreter.execute("(rest '(1 2))"));
		Assert.assertEquals("'(2 3)", interpreter.execute("(rest '(1 2 3))"));
	}

	@Test
	public void let() {

		// outter variable to use
		interpreter.execute("(define rrr 5)");

		// outter variable to override
		interpreter.execute("(define kkk 5)");

		// use innner variable
		Assert.assertEquals("2", interpreter.execute("(let ((xxx 1)) (+ xxx 1))"));

		// use outter variable
		Assert.assertEquals("6", interpreter.execute("(let ((mmm 1)) (+ rrr mmm))"));

		// overwrite outter variable
		Assert.assertEquals("2", interpreter.execute("(let ((kkk 1)) (+ kkk 1))"));

		// inner is not present in outter
		Assert.assertEquals("[symbol not found] - symbol xxx can't be resolved", interpreter.execute("(+ xxx 1)"));
	}

	@Test
	public void begin() {

		Assert.assertEquals("\"hallo\"", interpreter.execute("(begin (print \"hallo\"))"));
		Assert.assertEquals("\"one\"\"two\"", interpreter.execute("(begin (print \"one\") (print \"two\"))"));
		Assert.assertEquals("2", interpreter.execute("(begin (define aaa 1) (+ aaa aaa))"));
	}

	@Test
	public void renameFunctions() {

		Assert.assertEquals("<Operation: +>", interpreter.execute("(define ppp +)"));
		Assert.assertEquals("10", interpreter.execute("(ppp 3 7)"));
	}

	@Test
	public void quotedSymbole() {

		Assert.assertEquals("'aaa", interpreter.execute("(print 'aaa)"));
		Assert.assertEquals("'+", interpreter.execute("(print '+)"));
	}

	@Test
	public void quotedExpression() {

		Assert.assertEquals("'(+ 3 4)", interpreter.execute("(print '(+ 3 4))"));
	}

	@Test
	public void lambdaWithMultipleExpressionBody() {

		String command = "((lambda (a b) (print \"x\") (print \"y\")) 2 3)";

		Assert.assertEquals("\"x\"\"y\"", interpreter.execute(command));
	}

	@Test
	public void deinfeLambdaWithMultipleExpressionBody() {

		String command = "(define foo (lambda (a b) (print \"x\") (print \"y\")))";

		Assert.assertEquals("<Lambda: foo>", interpreter.execute(command));
		Assert.assertEquals("\"x\"\"y\"", interpreter.execute("(foo 5 2)"));

		command = "(define (foo a b) (print \"x\") (print \"y\"))";

		Assert.assertEquals("<Lambda: foo>", interpreter.execute(command));
		Assert.assertEquals("\"x\"\"y\"", interpreter.execute("(foo 5 2)"));
	}

	@Test
	public void cascadedLambda() {

		Assert.assertEquals("<Lambda: closure>", interpreter.execute("(define closure (lambda (a) (lambda (b) (lambda (c) (+ a b c)))))"));
		Assert.assertEquals("15", interpreter.execute("(((closure 5) 8) 2)"));
	}

	@Test
	public void closure1() {

		Assert.assertEquals("1", interpreter.execute("(define a 1)"));
		Assert.assertEquals("<Lambda: cl>", interpreter.execute("(define cl (lambda (a) (+ a 1)))"));
		Assert.assertEquals("2", interpreter.execute("(cl a)"));
		Assert.assertEquals("2", interpreter.execute("(define a 2)"));
		Assert.assertEquals("3", interpreter.execute("(cl a)"));
	}
	
	@Test
	public void closure2() {
		
		Assert.assertEquals("1000", interpreter.execute("(define a 1000)"));
		Assert.assertEquals("<Lambda: c>", interpreter.execute("(define c (lambda (a) (lambda (b) (+ a b))))"));
		Assert.assertEquals("<Lambda: x>", interpreter.execute("(define x (c 10))"));
		Assert.assertEquals("30", interpreter.execute("(x 20)"));
	}
	
	@Test
	public void load() {

		Assert.assertEquals("8", interpreter.execute("(load \"test/test.txt\")"));
		Assert.assertEquals("8", interpreter.execute("(print test)"));
	}
	
	@Test
	public void tryblock() {

		Assert.assertEquals("\"ups\"", interpreter.execute("(try (/ 42 0) (e (print \"ups\")))"));
		Assert.assertEquals("java.lang.ArithmeticException: Division by zero", interpreter.execute("(try (/ 42 0) (e (print e)))"));
		Assert.assertEquals("21", interpreter.execute("(try (/ 42 2) (e (print \"ups\")))"));
	}
	
	@Test
	public void change() {

		Assert.assertEquals("<Lambda: changer>", interpreter.execute("(define (changer) (set! a 1))"));
		Assert.assertEquals("0", interpreter.execute("(define a 0)"));
		Assert.assertEquals("1", interpreter.execute("(changer)"));
		Assert.assertEquals("1", interpreter.execute("(print a)"));
	}

	/**
	 * NEGATIVE TESTS
	 * 
	 * Negative tests are test where something should go wrong. For example an
	 * exception should be thrown or a special message should be returned.
	 */

	@Test
	public void wrongNumberOfArguments() {

		Assert.assertEquals("[wrong number of arguments] - expected 1, but were 2 [args = (1, 1)]", interpreter.execute("(inc 1 1)"));
		Assert.assertEquals("[wrong number of arguments] - expected 2, but were 1 [args = (1)]", interpreter.execute("(nq? 1)"));
	}

	@Test
	public void symbolNotFound() {

		Assert.assertEquals("[symbol not found] - symbol xxx can't be resolved", interpreter.execute("(+ xxx 1)"));
	}

	@Test
	public void isPair() {

		Assert.assertEquals("true", interpreter.execute("(pair? '(1))"));
		Assert.assertEquals("true", interpreter.execute("(pair? '(1 2 3))"));
		Assert.assertEquals("true", interpreter.execute("(pair? (cons 1 2))"));

		Assert.assertEquals("false", interpreter.execute("(pair? 7)"));
		Assert.assertEquals("false", interpreter.execute("(pair? 'a)"));
		Assert.assertEquals("false", interpreter.execute("(pair? (+ 1 2))"));
	}

	/**
	 * ALGORITHEM TESTS
	 * 
	 * Algorithem tests are tests with complicated commands, e.g. a quicksort.
	 * This tests help to bring up more subtle problems.
	 */

	@Test
	public void factorial() {

		String algorithem = "(define fac (lambda (number) (if (eq? 0 number) 1 (* number (fac (- number 1))))))";

		Assert.assertEquals("<Lambda: fac>", interpreter.execute(algorithem));

		Assert.assertEquals("1", interpreter.execute("(fac 1)"));
		Assert.assertEquals("6", interpreter.execute("(fac 3)"));
	}

	@Test
	public void append() {

		String algorithem = "(define append (lambda (L1 L2) (if (eq? null L1) L2 (cons (first L1) (append (rest L1) L2)))))";

		Assert.assertEquals("<Lambda: append>", interpreter.execute(algorithem));
		Assert.assertEquals("'(1 2 3 4)", interpreter.execute("(append '(1 2) '(3 4))"));

		Assert.assertEquals("'(1 2 5 6 3 4)", interpreter.execute("(append (append '(1 2) '(5 6)) '(3 4))"));

		Assert.assertEquals("'(1 2 5 6 3 4)", interpreter.execute("(append '(1 2) (append '(5 6) '(3 4)))"));
	}

	@Test
	public void len() {

		String algorithem = "(define len (lambda (L) (if (eq? L null) 0 (+ (len (rest L)) 1))))";

		Assert.assertEquals("<Lambda: len>", interpreter.execute(algorithem));
		Assert.assertEquals("2", interpreter.execute("(len '(1 2))"));
	}

	@Test
	public void nth() {

		String algorithem = "(define nth (lambda (l n) (if (eq? n 1) (first l) (nth (rest l) (- n 1)))))";

		Assert.assertEquals("<Lambda: nth>", interpreter.execute(algorithem));
		Assert.assertEquals("7", interpreter.execute("(nth '(1 2 7 4) 3)"));
	}

	@Test
	public void last() {

		String algorithem = "(define (last L) (if (eq? null (rest L)) (first L) (last (rest L))))";

		Assert.assertEquals("<Lambda: last>", interpreter.execute(algorithem));
		Assert.assertEquals("4", interpreter.execute("(last '(1 2 7 4))"));
	}

	@Test
	public void map1() {

		String algorithem = "(define (map list func) (if  (eq? list null) null (cons (func (first list)) (map (rest list) func))))";

		Assert.assertEquals("<Lambda: map>", interpreter.execute(algorithem));
		Assert.assertEquals("'(3 4)", interpreter.execute("(map '(2 3) inc)"));
	}

	@Test
	public void map2() {

		String algorithem = "(define (map func list) (if  (eq? list null) null (cons (func (first list)) (map func (rest list)))))";

		Assert.assertEquals("<Lambda: map>", interpreter.execute(algorithem));
		Assert.assertEquals("'(3 4)", interpreter.execute("(map inc '(2 3))"));
	}

	@Test
	public void reduce() {

		String algorithem = "(define (reduce list op) (if (eq? null (rest list)) (first list) (op (first list) (reduce (rest list) op))))";

		Assert.assertEquals("<Lambda: reduce>", interpreter.execute(algorithem));
		Assert.assertEquals("15", interpreter.execute("(reduce '(1 2 3 4 5) +)"));
	}

	@Test
	public void filter() {

		String algorithem = 
				"(define (filter list pred) " + 
						"(if (eq? list null) " + 
							"null " + 
						"(if (pred (first list)) " + 
							"(cons (first list) (filter (rest list) pred)) " + 
						";else \n" + 
							"(filter (rest list) pred))))";

		Assert.assertEquals("<Lambda: filter>", interpreter.execute(algorithem));
		Assert.assertEquals("'(1)", interpreter.execute("(filter '(1 2 3 4 5) (lambda (x) (eq? x 1)))"));
	}

	@Test
	public void appendWithThreeElements1() {

		String append2 = "(define (append2 l1 l2) (if (eq? l1 null) l2 (cons (first l1) (append2 (rest l1) l2))))";
		String append3 = "(define (append3 a b c) (append2 (append2 a b) c))";

		Assert.assertEquals("<Lambda: append2>", interpreter.execute(append2));
		Assert.assertEquals("<Lambda: append3>", interpreter.execute(append3));

		Assert.assertEquals("'(1 4 2 3)", interpreter.execute("(append3 '(1 4) '(2) '(3))"));
		Assert.assertEquals("'(1 2 3 4 5)", interpreter.execute("(append3 '(1 2) '(3) '(4 5))"));
		Assert.assertEquals("'(1 2 3 6 4 5)", interpreter.execute("(append3 '(1 2) '(3 6) '(4 5))"));

		// see racket: (append3 (cons (cons 1 (+ 1 1)) null) '(3) '(4 5)) -->
		// '((1 . 2) 3 4 5)
		Assert.assertEquals("'((1 . 2) 3 4 5)", interpreter.execute("(append3 (cons (cons 1 (+ 1 1)) null) '(3) '(4 5))"));
	}

	@Test
	public void appendWithThreeElements2() {

		String append2 = "(define (append2 l1 l2) (if (eq? l1 null) l2 (cons (first l1) (append2 (rest l1) l2))))";
		String append3 = "(define (append3 a b c) (append2 a (append2 b c)))";

		Assert.assertEquals("<Lambda: append2>", interpreter.execute(append2));
		Assert.assertEquals("<Lambda: append3>", interpreter.execute(append3));

		Assert.assertEquals("'(1 4 2 3)", interpreter.execute("(append3 '(1 4) '(2) '(3))"));
		Assert.assertEquals("'(1 2 3 4 5)", interpreter.execute("(append3 '(1 2) '(3) '(4 5))"));
		Assert.assertEquals("'(1 2 3 6 4 5)", interpreter.execute("(append3 '(1 2) '(3 6) '(4 5))"));

		// see racket: (append3 (cons (cons 1 (+ 1 1)) null) '(3) '(4 5)) -->
		// '((1 . 2) 3 4 5)
		Assert.assertEquals("'((1 . 2) 3 4 5)", interpreter.execute("(append3 (cons (cons 1 (+ 1 1)) null) '(3) '(4 5))"));
	}

	@Test
	public void appendBug() {

		String append2 = "(define (append2 l1 l2) (if (eq? l1 null) l2 (cons (first l1) (append2 (rest l1) l2))))";

		Assert.assertEquals("<Lambda: append2>", interpreter.execute(append2));
		Assert.assertEquals("'(4 5 (1 . 2))", interpreter.execute("(append2 '(4 5) (cons (cons 1 2) null) )"));
	}

	@Test
	public void mylength() {

		String command = "(define mylength (lambda (L) (if (eq? L null) 0 (+ (mylength   (rest L)) 1))))";

		Assert.assertEquals("<Lambda: mylength>", interpreter.execute(command));
		Assert.assertEquals("4", interpreter.execute("(mylength '(1 2 3 5))"));
	}

	@Test
	public void fib() {

		String command = "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))";

		Assert.assertEquals("<Lambda: fib>", interpreter.execute(command));
		Assert.assertEquals("8", interpreter.execute("(fib 6)"));
	}

	@Test(timeout = 2000)
	public void quickSort() {

		String a = "(define mylength (lambda (L) (if (eq? L null) 0 (+ (mylength   (rest L)) 1))))";
		String b = "(define (append2 l1 l2) (if (eq? l1 null) l2 (cons (first l1) (append2 (rest l1) l2))))";
		String c = "(define (append3 a b c) (append2 a (append2 b c)))";
		String d = "(define (myfilter pred list) (if (eq? list null) null (if (pred (first list)) (cons (first list) (myfilter pred (rest list))) (myfilter pred (rest list)))))";

		String e = "(define (quicksort list) " + "(if (<= (mylength list) 1) " + "list " +
		// else
				"(let ((pivot (first list))) " + "(append3 " + "(quicksort (myfilter (lambda (x) (< x pivot)) list)) " + "(myfilter  (lambda (x) (eq? x pivot)) list) " + "(quicksort (myfilter  (lambda (x) (> x pivot)) list))))))";

		Assert.assertEquals("<Lambda: mylength>", interpreter.execute(a));
		Assert.assertEquals("<Lambda: append2>", interpreter.execute(b));
		Assert.assertEquals("<Lambda: append3>", interpreter.execute(c));
		Assert.assertEquals("<Lambda: myfilter>", interpreter.execute(d));
		Assert.assertEquals("<Lambda: quicksort>", interpreter.execute(e));

		Assert.assertEquals("'(5 8)", interpreter.execute("(quicksort '(8 5))"));
		Assert.assertEquals("'(3 5 8)", interpreter.execute("(quicksort '(8 5 3))"));
		Assert.assertEquals("'(1 3 5 7 8)", interpreter.execute("(quicksort '(1 8 7 5 3))"));
	}

	/**
	 * SPECIAL TESTS
	 * 
	 * Special tests are more complex tests on lisp specific behavioure.
	 */

	@Test
	public void defineLambdaWithReturn() {

		String c1 = "(define (mm) 5)";
		String c2 = "(mm)";

		Assert.assertEquals("<Lambda: mm>", interpreter.execute(c1));
		Assert.assertEquals("5", interpreter.execute(c2));
	}

	@Test
	public void defineSimpleObject1() {

		String c1 = "(define (make-adder a) (define (adder x) (+ x a)) adder)";
		String c2 = "(define myAdder (make-adder 10))";
		String c3 = "(myAdder 20)";
		String c4 = "((make-adder 10) 20)";

		Assert.assertEquals("<Lambda: make-adder>", interpreter.execute(c1));
		Assert.assertEquals("<Lambda: myAdder>", interpreter.execute(c2));
		Assert.assertEquals("30", interpreter.execute(c3));
		Assert.assertEquals("30", interpreter.execute(c4));
	}

	@Test
	public void defineSimpleObject2() {

		String c1 = 
				"(define (make-adder a) " + 
				"(define (pr) (print \"prpr\")) " + 
				"(define (adder x) (pr) (print (+ x a))) " + 
					"adder)";
		
		String c2 = "(define myAdder (make-adder 10))";
		String c3 = "(myAdder 20)";
		String c4 = "((make-adder 10) 20)";

		Assert.assertEquals("<Lambda: make-adder>", interpreter.execute(c1));
		Assert.assertEquals("<Lambda: myAdder>", interpreter.execute(c2));
		Assert.assertEquals("\"prpr\"30", interpreter.execute(c3));
		Assert.assertEquals("\"prpr\"30", interpreter.execute(c4));
	}

	@Test
	public void defineComplexObject1() {

		String c1 = 
				  "(define (make-point x y) " 
				+ "(define (get-x) x) " 
				+ "(define (get-y) y) "
				+ "(define (set-x! newX) (set! x newX)) " 
				+ "(define (set-y! newY) (set! y newY)) " 
				+ "(define (area) (* x y)) " 
				+ "(define (error) (print \"mist\")) " 
				+ "(define (dispatch op) " 
					+ "(if (eq? op 'get-x)  get-x " 
					+ "(if (eq? op 'get-y)  get-y " 
					+ "(if (eq? op 'set-x!) set-x! " 
					+ "(if (eq? op 'set-y!) set-y! " 
					+ "(if (eq? op 'area)   area " 
						+ "error)))))) " 
				+ "dispatch)";
		
		String c2 = "(define myPoint (make-point 10 10))";
		String c3 = "(myPoint 'area)";
		String c4 = "((myPoint 'area))";

		Assert.assertEquals("<Lambda: make-point>", interpreter.execute(c1));
		Assert.assertEquals("<Lambda: myPoint>", interpreter.execute(c2));
		Assert.assertEquals("<Lambda: area>", interpreter.execute(c3));
		Assert.assertEquals("100", interpreter.execute(c4));
	}
	
	@Test
	public void defineComplexObject2() {

		String object = 
				"(define (make-point x y) " +
						 " (define (get-x) " +
						 "   x) " +
						 " (define (get-y) " +
						 "   y) " +
						 " (define (set-x! newX) " +
						 "   (set! x newX)) " +
						 " (define (set-y! newY) " +
						 "   (set! y newY)) " +
						 " (define (area) " +
						 "   (* x y)) " +
						 " (define (error) " +
						 "   (print \"mist\")) " +  
						 " (define (dispatch op) " +
						 "   (if (eq? op 'get-x) " +
						 "       get-x " +
						 "   (if (eq? op 'get-y) " +
						 "       get-y " +
						 "   (if (eq? op 'set-x!) " +
						 "       set-x! " +
						 "   (if (eq? op 'set-y!) " +
						 "       set-y! " +
						 "   (if (eq? op 'area) " +
						 "       area " +
						 "       error)))))) " +
						 " dispatch)";

		String c1 = "(define point (make-point 10 5))";
		String c2 = "((point 'get-y))";
		String c3 = "((point 'set-y!) 20)";
		String c4 = "((point 'get-y))";
		String c5 = "((point 'area))";

		Assert.assertEquals("<Lambda: make-point>", interpreter.execute(object));
		Assert.assertEquals("<Lambda: point>", interpreter.execute(c1));
		Assert.assertEquals("5", interpreter.execute(c2));
		Assert.assertEquals("20", interpreter.execute(c3));
		Assert.assertEquals("20", interpreter.execute(c4));
		Assert.assertEquals("200", interpreter.execute(c5));
	}
	
	
}