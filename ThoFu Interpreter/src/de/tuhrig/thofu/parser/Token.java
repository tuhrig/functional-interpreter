package de.tuhrig.thofu.parser;

/**
 * This class represents a token found during the lexical
 * analysis by the parser. A token contains the actual string
 * that was found as well as information about its position
 * to report errors to the user.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Token {

	private String word;
	
	private int line;
	
	private int position;
	
	public Token(String word) {

		this.word = word;
	}

	public Token() {

		// default
	}

	public String getWord() {
	
		return word;
	}

	public void setWord(String word) {
	
		this.word = word;
	}

	public int getLine() {
	
		return line;
	}

	public void setLine(int line) {
	
		this.line = line;
	}
	
	public int getPosition() {
	
		return position;
	}

	public void setPosition(int position) {
	
		this.position = position;
	}
	
	public boolean equals(Object o) {
		
		if(o instanceof Token)
			return word.equals(((Token) o).getWord());
		
		return word.equals(o);
	}
	
	public String information() {
		
		return "<" + word + ", " + line + ", " + position + ">";
	}
	
	public String toString() {
		
		return word;
	}
}