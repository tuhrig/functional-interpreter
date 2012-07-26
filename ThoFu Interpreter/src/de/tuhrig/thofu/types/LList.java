package de.tuhrig.thofu.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.tuhrig.thofu.Environment;

/**
 * Represents a list.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LList extends LObject implements List<LObject> { // LNode {

	private final ArrayList<LObject> list;

	/**
	 * Creates a new list.
	 */
	public LList() {

		this.list = new ArrayList<>(0);
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		LObject first = this.getFirst();

		if (first instanceof List) {
			
			LObject object = first.run(environment, this.getRest());

			return object.run(environment, this.getRest());
		}

		// e.g. evaluate a LSymbol and get the operation behind it
		if(first instanceof LSymbol) {

			first = first.run(environment, null);
		}

		// e.g. evaluate this operation now
		return first.run(environment, this.getRest());
	}

	@Override
	public String toString() {

		return list.toString().replace("[", "(").replace("]", ")");
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof LList)
			list.equals(((LList) o).list);

		return false;
	}

	@Override
	public int hashCode() {

		return System.identityHashCode(this);
	}

	/**
	 * @return first object in the list
	 */
	public LObject getFirst() {

		LObject first = this.get(0);

		if (first == null)
			return LNull.NULL;

		return first;
	}

	/**
	 * @return all objects except of the first one
	 */
	public LObject getRest() {

		LObject rest = (LObject) this.subList(1, this.size());

		if (rest == null)
			return LNull.NULL;

		return rest;
	}

	/**
	 * List<LObject> Interface
	 */

	@Override
	public boolean add(LObject e) {

		return this.list.add(e);
	}

	@Override
	public void add(int index, LObject element) {

		this.list.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends LObject> c) {

		return this.list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends LObject> c) {

		return this.list.addAll(index, c);
	}

	@Override
	public void clear() {

		this.list.clear();
	}

	@Override
	public boolean contains(Object o) {

		return this.list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		return this.list.containsAll(c);
	}

	@Override
	public LObject get(int index) {

		if (index >= size())
			return LNull.NULL;

		return this.list.get(index);
	}

	@Override
	public int indexOf(Object o) {

		return this.list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {

		return this.list.isEmpty();
	}

	@Override
	public Iterator<LObject> iterator() {

		return this.list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {

		return this.list.lastIndexOf(o);
	}

	@Override
	public ListIterator<LObject> listIterator() {

		return this.list.listIterator();
	}

	@Override
	public ListIterator<LObject> listIterator(int index) {

		return this.list.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {

		return this.list.remove(o);
	}

	@Override
	public LObject remove(int index) {

		return this.list.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		return this.list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		return this.list.retainAll(c);
	}

	@Override
	public LObject set(int index, LObject element) {

		return this.list.set(index, element);
	}

	@Override
	public int size() {

		return this.list.size();
	}

	@Override
	public List<LObject> subList(int fromIndex, int toIndex) {

		LList subList = new LList();

		for (LObject o : list.subList(fromIndex, toIndex))
			subList.add(o);

		return subList;
	}

	@Override
	public Object[] toArray() {

		return this.list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {

		return this.list.toArray(a);
	}

	@Override
	public int argrumentSize(LObject object) {

//		return size();
		
		return getFirst().argrumentSize(object) + 1;
	}
}