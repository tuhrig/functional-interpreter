package de.tuhrig.thofu.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.tuhrig.thofu.Environment;

public class LTupel extends LList {

	private LObject first = LNull.NULL;

	private LObject last = LNull.NULL;

	public LTupel() {

		// empty default constructor
	}

	public boolean equals(Object o) {

		if (o instanceof LTupel)
			return first.equals(((LTupel) o).first) && last.equals(((LTupel) o).last);

		return false;
	}

	LTupel(LObject first, LObject last) {

		this.first = first;
		this.last = last;
	}

	public LObject getFirst() {

		return first;
	}

	public void setFirst(LObject first) {

		this.first = first;
	}

	public LObject getRest() {

		return last;
	}

	public void setLast(LObject last) {

		this.last = last;
	}

	public String toString() {

		return "'(" + toSimpleString().trim() + ")";
	}

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

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		return this;
	}

	/**
	 * 
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

	@Override
	public void clear() {

		first = LNull.NULL;
		last = LNull.NULL;
	}

	@Override
	public boolean contains(Object o) {
		
		if (!(first == LNull.NULL) && first.equals(o))
			return true;

		else if (last == LNull.NULL)
			return false;

		return ((LTupel) last).contains(o);
	}

	@Override
	public LObject get(int index) {

		if (index == 0)
			return first;

		if (!(last instanceof LTupel) && index == 1)
			return last;

		return ((LTupel) last).get(index - 1);
	}

	@Override
	public boolean isEmpty() {

		return first == LNull.NULL && last == LNull.NULL;
	}

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

	@Override
	public Object[] toArray() {

		Object[] array = new Object[this.size()];

		for (int i = 0; i < this.size(); i++)
			array[i] = this.get(i);

		return array;
	}
	
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