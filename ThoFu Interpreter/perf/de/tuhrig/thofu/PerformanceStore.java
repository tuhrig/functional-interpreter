package de.tuhrig.thofu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import bb.util.Benchmark;

class PerformanceStore {

	static void store(String name, Benchmark benchmark) {

		try {
			
			File file = new File("perf/" + name);
		
			if(!file.exists())
				file.createNewFile();
		
			String content = new Date() + ";" + benchmark.getMean() + ";" + benchmark.getSd() + "\n";

			Files.write(file.toPath(), content.getBytes(), StandardOpenOption.APPEND);
		}
		catch (IOException e) {

			e.printStackTrace();
		}
	}
}