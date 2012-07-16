package de.tuhrig.fujas.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import com.petebevin.markdown.MarkdownProcessor;

import de.tuhrig.fujas.Parser;

class HTMLViewer extends JFrame implements HyperlinkListener {

	private static final long serialVersionUID = 1L;
	
	private JEditorPane htmlPane;
 
	HTMLViewer(File file) {
		
		try {
			
			htmlPane = new JEditorPane("text/html", getHTML(new Parser().read(file)));
		}
		catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Can't access " + file, "Error", JOptionPane.ERROR_MESSAGE);		
		}

		htmlPane.setEditable(false);
		
		htmlPane.addHyperlinkListener(this);

		add(new JScrollPane(htmlPane));

		setTitle("About");
		setSize(800, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static String getHTML(String markdown) {
	    MarkdownProcessor markdown_processor = new MarkdownProcessor();
	    return markdown_processor.markdown(markdown);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {

		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			
			JEditorPane pane = (JEditorPane) e.getSource();
			
			if (e instanceof HTMLFrameHyperlinkEvent) {
				
				HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
				
				HTMLDocument doc = (HTMLDocument) pane.getDocument();
				
				doc.processHTMLFrameHyperlinkEvent(evt);
			}
			else {

				try {
					
					Desktop.getDesktop().browse(new URI(e.getDescription()));
				}
				catch (IOException | URISyntaxException e1) {

					JOptionPane.showMessageDialog(this, "Can't access " + e.getURL(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}