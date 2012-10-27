package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

class Log extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final EnhancedPatternLayout layout = new EnhancedPatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n %throwable{n}");

	private final JTextArea textArea = new JTextArea();
	
	public Log() {

		textArea.setRows(10);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		textArea.setEditable(false);

		WriterAppender appender = new WriterAppender() {

			public void append(LoggingEvent loggingEvent) {

				final String message = this.getLayout().format(loggingEvent);

				SwingUtilities.invokeLater(new Runnable() {

					public void run() {

						textArea.append(message);
						textArea.setCaretPosition(textArea.getDocument().getLength());
					}
				});
			}
		};

		appender.setLayout(layout);

		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(appender);

		this.setLayout(new BorderLayout());
		this.add(new JLabel("Log:"), BorderLayout.NORTH);
		this.add(new JScrollPane(textArea), BorderLayout.CENTER);
		this.add(new LogLevelSelector(), BorderLayout.SOUTH);
	}
	
	public void log(String message) {
		
		textArea.append(message);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}