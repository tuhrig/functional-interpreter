package de.tuhrig.thofu;

import static de.tuhrig.thofu.Literal.NL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

/**
 * This class encapsulates some useful functions that are
 * used often, but that are small and don't have much in
 * common.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Util {

	/**
	 * @param file to read
	 * @return content of the file
	 * @throws IOException if the file can't be read
	 */
	public String read(File file) throws IOException {
		
		List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

		StringBuilder content = new StringBuilder();
		
		for (String line : lines) {
			
			content.append(line);
			content.append(NL);
		}
		
		return content.toString();
	}
	
	/**
	 * @param clazz to use for resource loading
	 * @param resource name to load
	 * @return the content of the resource as a String
	 */
	public String read(Class<?> clazz, String resource) {

		InputStream stream = clazz.getResourceAsStream(resource);
		
		Scanner scanner = new Scanner(stream);
	 
		StringBuilder content = new StringBuilder();
		
		while(scanner.hasNext()) {
			
			content.append(scanner.nextLine());
			content.append(NL);
		}
		
		scanner.close();
		
		return content.toString();
	}
}