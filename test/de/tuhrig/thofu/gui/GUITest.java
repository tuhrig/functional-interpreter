package de.tuhrig.thofu.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Locale;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GUITest {

	private FrameFixture gui;

	private final KeyPressInfo enter = KeyPressInfo.keyCode(KeyEvent.VK_ENTER);
	
	private final KeyPressInfo completion = KeyPressInfo.keyCode(KeyEvent.VK_SPACE).modifiers(KeyEvent.CTRL_MASK);
	
	private final KeyPressInfo submit = KeyPressInfo.keyCode(KeyEvent.VK_ENTER).modifiers(KeyEvent.CTRL_MASK);
	
	@Before
	public void setUp() {

		ThoFuUi.gui = null;
		gui = new FrameFixture(ThoFuUi.instance());
		gui.show();
	}

	@After
	public void tearDown() {

		gui.cleanUp();
	}

	@Test
	public void menuBarIsPresent() {

		gui.menuItem("File").requireVisible();
		gui.menuItem("Edit").requireVisible();
		gui.menuItem("Run").requireVisible();
		gui.menuItem("More").requireVisible();
	}
	
	@Test
	public void replIsPresent() {

		gui.textBox("repl").requireText(
					"ThoFu Interpreter\n" +
					"Press CTRL + ENTER to submit a command\n" + 
					">> "
				);
	}

	@Test
	public void runTextInEditor() {

		Locale.setDefault(Locale.GERMAN);
		
		gui.panel("status").background().requireEqualTo(Color.GREEN);
	
		gui.menuItemWithPath("File", "New").click();
		gui.fileChooser().fileNameTextBox().enterText("test.txt");
		gui.fileChooser().approve();
		
		gui.panel("status").background().requireEqualTo(Color.RED);
		gui.textBox("editor").enterText("(define a 5)");
		gui.panel("status").background().requireEqualTo(Color.RED);
		gui.menuItemWithPath("Run", "Start Script").click();
		gui.panel("status").background().requireEqualTo(Color.GREEN);
	}
	
	@Test
	public void testAutoCompletionInEditor() {

		Locale.setDefault(Locale.GERMAN);
		
		gui.panel("status").background().requireEqualTo(Color.GREEN);
	
		gui.menuItemWithPath("File", "New").click();
		gui.fileChooser().fileNameTextBox().enterText("test.txt");
		gui.fileChooser().approve();
		
		gui.panel("status").background().requireEqualTo(Color.RED);
		
		gui.textBox("editor").enterText("(define abc 5)");

		gui.menuItemWithPath("Run", "Start Script").click();
		
		gui.panel("status").background().requireEqualTo(Color.GREEN);
		
		gui.textBox("editor").enterText("\n");
		gui.textBox("editor").enterText("(- 5 a");
		gui.textBox("editor").pressAndReleaseKey(completion);
		gui.textBox("editor").pressAndReleaseKey(enter);
		gui.textBox("editor").requireText("(define abc 5)\n(- 5 abc");
	}
	
	@Test
	public void testScriptTestCase1() {

		// define a variable in editor
		Locale.setDefault(Locale.GERMAN);
		
		gui.panel("status").background().requireEqualTo(Color.GREEN);
	
		gui.menuItemWithPath("File", "New").click();
		gui.fileChooser().fileNameTextBox().enterText("test.txt");
		gui.fileChooser().approve();
		
		gui.panel("status").background().requireEqualTo(Color.RED);
		
		gui.textBox("editor").enterText("(define abc 5");
		gui.panel("status").background().requireEqualTo(Color.RED);
		gui.menuItemWithPath("Run", "Start Script").click();
		gui.panel("status").background().requireEqualTo(Color.GREEN);
		
		// use variable in repl
		gui.textBox("repl").enterText("(- 5 a");
		gui.textBox("repl").pressAndReleaseKey(completion);
		gui.textBox("repl").pressAndReleaseKey(enter);
		endsWith(gui.textBox("repl").text(), "(- 5 abc)");
		gui.textBox("repl").enterText(")");
		gui.textBox("repl").pressAndReleaseKey(submit);	
		endsWith(gui.textBox("repl").text(), "0\nOut: \n>> ");
	}

	private void endsWith(String text, String string) {

		Assert.assertTrue(text.endsWith(string));
	}
}