package de.tuhrig.thofu.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.tuhrig.thofu.Environment;

/**
 * Represents a LISP-like tupel.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LTupel extends LList {

	private LObject first = LNull.NULL;

	private LObject last = LNull.NULL;

	public LTupel() {

		// empty default constructor
	}
	
	public LTupel(LObject first, LObject last) {

		this.first = first;
		this.last = last;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof LTupel)
			return first.equals(((LTupel) o).first) && last.equals(((LTupel) o).last);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#getFirst()
	 */
	@Override
	public LObject getFirst() {

		return first;
	}

	/**
	 * @param object to set at first
	 */
	public void setFirst(LObject object) {

		this.first = object;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#getRest()
	 */
	@Override
	public LObject getRest() {

		return last;
	}

	/**
	 * @param object to set as last
	 */
	public void setLast(LObject object) {

		this.last = object;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#toString()
	 */
	@Override
	public String toString() {

		return "'(" + toSimpleString().trim() + ")";
	}

	/**
	 * This method formats the tupel in a pretty nice way. Therefore, 
	 * different cases are tested. 
	 * 
	 * @return pretty formatted string representation of the tupel
	 */
	private String toSimpleString() {

		if (!(first == LNull.NULL) && first instanceof LTupel && !(last == LNull.NULL) && last instanceof LTupel)
			return "(" + ((LTupel) first).toSimpleString() + ") " + ((LTupel) last).toSimpleString();

		if (!(first == LNull.NULL) && !(last == LNull.NULL) && last instanceof LTupel)
			return first + " " + ((LTupel) last).toSimpleString();

		if (first == LNull.NULL && !(last == LNull.NULL) && last instanceof LTupel)
			return "() " + ((LTupel) last).toSimpleString();

		if (!(first == LNull.NULL) && first instanceof LTupel && !(last == LNull.NULL))
			return "(" + ((LTupel) first).toSimpleString() + ") . " + last;

		if (!(first == LNull.NULL) && first instanceof LTupel && last == LNull.NULL)
			return "(" + ((LTupel) first).toSimpleString() + ")";

		if (!(first == LNull.NULL) && last == LNull.NULL)
			return first.toString();

		if (first == LNull.NULL && !(last == LNull.NULL))
			return "() . " + last;

		if (!(first == LNull.NULL) && !(last == LNull.NULL))
			return first + " . " + last;

		// the last missing case: if(first == null && last == null)
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#evaluate(de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#add(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public boolean add(LObject e) {

		if (first == LNull.NULL) {

			first = e;
		}
		else if (last == LNull.NULL && (e instanceof LTupel) == true) {

			last = e;
		}
		else if (last == LNull.NULL && (e instanceof LTupel) == false) {

			last = new LTupel(e, LNull.NULL);
		}
		else if (last instanceof LTupel) {

			((LTupel) last).add(e);
		}
		else {

			last = new LTupel(last, LNull.NULL);

			((LTupel) last).add(e);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#clear()
	 */
	@Override
	public void clear() {

		first = LNull.NULL;
		last = LNull.NULL;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		
		if (!(first == LNull.NULL) && first.equals(o))
			return true;

		else if (last == LNull.NULL)
			return false;

		return ((LTupel) last).contains(o);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#get(int)
	 */
	@Override
	public LObject get(int index) {

		if (index == 0)
			return first;

		if (!(last instanceof LTupel) && index == 1)
			return last;

		return ((LTupel) last).get(index - 1);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#isEmpty()
	 */
	@Override
	public boolean isEmpty() {

		return first == LNull.NULL && last == LNull.NULL;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#size()
	 */
	@Override
	public int size() {

		if (!(first == LNull.NULL) && last == LNull.NULL) {
			
			return 1;
		}
		else if (first == LNull.NULL && last == LNull.NULL) {
	
			return 0;
		}
		else if (last instanceof LTupel) {
		
			return 1 + ((LTupel) last).size();
		}
		else {

			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#toArray()
	 */
	@Override
	public Object[] toArray() {

		Object[] array = new Object[this.size()];

		for (int i = 0; i < this.size(); i++)
			array[i] = this.get(i);

		return array;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object o) {

		if(first.equals(o))
			return 0;
		
		if(last.equals(o))
			return 1;
		
		if(!(last == LNull.NULL) && last instanceof LTupel)
			return 1 + ((LTupel) last).indexOf(o);
		
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LList#subList(int, int)
	 */
	@Override
	public List<LObject> subList(int fromIndex, int toIndex) {

		LTupel tmp = new LTupel();

		for (int i = 0; i < size(); i++) {

			if (i >= fromIndex && i < toIndex)
				tmp.add(this.get(i));
		}

		return tmp;
	}
	
	/**
	 * 
	 */

	@Override
	public void add(int index, LObject element) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public boolean addAll(Collection<? extends LObject> c) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public boolean addAll(int index, Collection<? extends LObject> c) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public Iterator<LObject> iterator() {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public int lastIndexOf(Object o) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public ListIterator<LObject> listIterator() {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public ListIterator<LObject> listIterator(int index) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public boolean remove(Object o) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public LObject remove(int index) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public LObject set(int index, LObject element) {

		throw new RuntimeException("not implemented in LispTupel");
	}

	@Override
	public <T> T[] toArray(T[] a) {

		throw new RuntimeException("not implemented in LispTupel");
	}
}