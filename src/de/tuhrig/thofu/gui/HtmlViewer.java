package de.tuhrig.thofu.gui;

import java.awt.Desktop;
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

class HtmlViewer extends JFrame implements HyperlinkListener {

	private static final long serialVersionUID = 1L;

	private JEditorPane htmlPane;
 
	private JScrollPane scrollPane;
	
	public HtmlViewer(String string) {
		
		String html = getHTML(string);
			
		htmlPane = new JEditorPane("text/html", html);
		htmlPane.setEditable(false);
		htmlPane.addHyperlinkListener(this);
		
		scrollPane = new JScrollPane(htmlPane);
	
		add(scrollPane);
		setTitle("About");
		setSize(800, 400);
		setLocationRelativeTo(null);
		scrollToTop();
		setVisible(true);
	}
	
	private void scrollToTop() {
		
		htmlPane.setCaretPosition(0);
		scrollPane.getVerticalScrollBar().setValue(0);
		scrollPane.repaint();		
	}
	
	public static String getHTML(String markdown) {
		
	    MarkdownProcessor processor = new MarkdownProcessor();
	    
	    return processor.markdown(markdown);
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