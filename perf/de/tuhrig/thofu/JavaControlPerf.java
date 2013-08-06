package de.tuhrig.thofu;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import bb.util.Benchmark;

public class JavaControlPerf {

	@Test
	public void plus() throws Exception {

		Callable<Integer> task = new Callable<Integer>() {

			public Integer call() {

				return 1 + 2;
			}
		};
		
		Benchmark benchmark = new Benchmark(task);
	
		System.out.println(benchmark.toStringFull());
		
		PerformanceStore.store("control - simpleAddition.csv", benchmark);
		
		Assert.assertTrue(benchmark.getMean() < 0.00006);
	}
	
	@Test
	public void quicksort() throws Exception {

		Callable<Integer[]> task = new Callable<Integer[]>() {

			public Integer[] call() {

				Integer[] array = new Integer[]{1, 8, 7, 5, 3, 6, 2, 9, 0, 4};
				
				Arrays.sort(array);
				
				return array;
			}
		};
		
		Benchmark benchmark = new Benchmark(task);
	
		System.out.println(benchmark.toStringFull());
		
		PerformanceStore.store("control - quicksort.csv", benchmark);
		
		Assert.assertTrue(benchmark.getMean() < 0.9);
	}
	
	@Test
	public void factorial() throws Exception {

		Callable<Integer> task = new Callable<Integer>() {

			public Integer call() {

				int fact = 1;
				
		        for (int i = 1; i <= 8; i++)
		            fact *= i;
		    
		        return fact;
			}
		};
		
		Benchmark benchmark = new Benchmark(task);
	
		System.out.println(benchmark.toStringFull());
		
		PerformanceStore.store("control - factorial.csv", benchmark);
		
		Assert.assertTrue(benchmark.getMean() < 0.02);
	}
}