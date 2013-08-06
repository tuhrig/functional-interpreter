package de.tuhrig.thofu.gui;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.tuhrig.thofu.types.LList;

/**
 * The history singleton stores the executed commands in a 
 * list.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class History {

	public static final int limit = 100;
	
	private static int count = 0;

	private Map<Identifier, LList> history = new LinkedHashMap<>(limit);	

	public final static History instance = new History();
	
	private History() {
		
		// singleton
	}
	
	public void add(LList tokens, Date started, Date ended) {

		history.put(new Identifier(started, ended), tokens);
		
		if(size() >= limit) {

			history.remove(new Identifier(count - limit + 1));
			
//			history = new LinkedHashMap<>(limit);
		}
	}

	public int size() {

		return history.size();
	}

	public Entry<Identifier, LList> get(int index) {

		@SuppressWarnings("unchecked")
		Entry<Identifier, LList> entry = (Entry<Identifier, LList>) history.entrySet().toArray()[index];
		
		return entry;
	}
	
	/**
	 * An Identifier objects identifies an executed command as well as
	 * holds additional information about it (e.g. the start time of
	 * the command).
	 * 
	 * @author Thomas Uhrig (tuhrig.de)
	 */
	public class Identifier {

		public final int number;
		public final Date started;
		public final Date ended;
		
		public Identifier(Date started, Date ended) {

			this.number = ++count;	
			this.started = started;
			this.ended = ended;
		}
			
		public Identifier(int index) {

			this.number = index;
			this.started = null;
			this.ended = null;
		}
		
		public boolean equals(Object object) {

			if(object instanceof Identifier) {
	
				return number == ((Identifier) object).number;
			}
			
			return false;
		}
		
		public int hashCode() {
			
			return System.identityHashCode(number);
		}

		public long getElapsedTime() {
			
			return ended.getTime() - started.getTime();
		}
	}
}