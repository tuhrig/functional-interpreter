package de.tuhrig.fujas;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class Chart extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	public Chart(final List<File> files) throws IOException {

		super("Performance Charts");

		StatisticalCategoryDataset dataset = createDataset(files);

		CategoryAxis xAxis = new CategoryAxis("Run");

		ValueAxis yAxis = new NumberAxis("Mean execution time in seconds");

		CategoryItemRenderer renderer = new StatisticalBarRenderer();

		CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

		JFreeChart chart = new JFreeChart(plot);

		setContentPane(new ChartPanel(chart));
	}

	private StatisticalCategoryDataset createDataset(List<File> files) throws IOException {

		DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();

		for (File file : files) {

			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

			int count = 0;

			for (String line : lines) {

				String[] values = line.split(";");

				result.add(
						Double.parseDouble(values[1]), 
						Double.parseDouble(values[2]), 
						file.getName(), 
						String.valueOf(count++));
			}
		}

		return result;
	}

	public static void main(final String[] args) throws IOException {

		List<File> files = new ArrayList<>();
		
		files.add(new File("perf/performance - simpleAddition.csv"));
		files.add(new File("perf/performance - quicksort.csv"));
		files.add(new File("perf/performance - factorial.csv"));
		
		files.add(new File("perf/control - simpleAddition.csv"));
		files.add(new File("perf/control - quicksort.csv"));
		files.add(new File("perf/control - factorial.csv"));

		Chart demo = new Chart(files);

		demo.pack();
		demo.setVisible(true);
	}
}