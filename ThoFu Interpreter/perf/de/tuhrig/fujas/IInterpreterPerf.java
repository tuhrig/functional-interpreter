package de.tuhrig.fujas;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bb.util.Benchmark;
import de.tuhrig.fujas.Interpreter;
import de.tuhrig.fujas.interfaces.IInterpreter;

public class IInterpreterPerf {

	/**
	 * The tested IInterpreter instanze
	 */
	private IInterpreter interpreter;

	@Before
	public void reset() {

		interpreter = new Interpreter();
	}

	@Test
	public void plus() throws Exception {

		Callable<String> task = new Callable<String>() {

			public String call() {

				return interpreter.execute("(+ 1 2)");
			}
		};
		
		Benchmark benchmark = new Benchmark(task);
	
		System.out.println(benchmark.toStringFull());
		
		PerformanceStore.store("performance - simpleAddition.csv", benchmark);
		
		Assert.assertTrue(benchmark.getMean() < 0.0006);
	}
	
	@Test
	public void quicksort() throws Exception {

		String a = "(define mylength (lambda (L) (if (eq? L null) 0 (+ (mylength   (rest L)) 1))))";
		String b = "(define (append2 l1 l2) (if (eq? l1 null) l2 (cons (first l1) (append2 (rest l1) l2))))";
		String c = "(define (append3 a b c) (append2 a (append2 b c)))";
		String d = "(define (myfilter pred list) (if (eq? list null) null (if (pred (first list)) (cons (first list) (myfilter pred (rest list))) (myfilter pred (rest list)))))";

		String e = 
				"(define (quicksort list) " + 
						"(if (<= (mylength list) 1) " + "list " +
						// else
						"(let ((pivot (first list))) " + 
							"(append3 " + "(quicksort (myfilter (lambda (x) (< x pivot)) list)) " + 
							"(myfilter  (lambda (x) (eq? x pivot)) list) " + 
							"(quicksort (myfilter  (lambda (x) (> x pivot)) list))))))";

		Assert.assertEquals("<Lambda: mylength>", interpreter.execute(a));
		Assert.assertEquals("<Lambda: append2>", interpreter.execute(b));
		Assert.assertEquals("<Lambda: append3>", interpreter.execute(c));
		Assert.assertEquals("<Lambda: myfilter>", interpreter.execute(d));
		Assert.assertEquals("<Lambda: quicksort>", interpreter.execute(e));

		Callable<String> task = new Callable<String>() {

			public String call() {

				return interpreter.execute("(quicksort '(1 8 7 5 3 6 2 9 0 4))");
			}
		};
		
		Benchmark benchmark = new Benchmark(task);
	
		System.out.println(benchmark.toStringFull());
		
		PerformanceStore.store("performance - quicksort.csv", benchmark);
		
		Assert.assertTrue(benchmark.getMean() < 0.9);
	}
	
	@Test
	public void factorial() throws Exception {

		String algorithem = "(define fac (lambda (number) (if (eq? 0 number) 1 (* number (fac (- number 1))))))";

		Assert.assertEquals("<Lambda: fac>", interpreter.execute(algorithem));

		Callable<String> task = new Callable<String>() {

			public String call() {

				return interpreter.execute("(fac 8)");
			}
		};
		
		Benchmark benchmark = new Benchmark(task);
	
		System.out.println(benchmark.toStringFull());
		
		PerformanceStore.store("performance - factorial.csv", benchmark);
		
		Assert.assertTrue(benchmark.getMean() < 0.02);
	}
}