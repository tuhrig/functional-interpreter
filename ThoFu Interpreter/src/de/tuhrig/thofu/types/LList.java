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
	
	public LList(LObject... objects) {
		
		this.list = new ArrayList<>(0);
		
		for(LObject object: objects)
			this.list.add(object);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#evaluate(de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#sum(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject sum(LObject object) {
		
		final LList tmp = new LList();
		
		for(int i = 0; i < size(); i++)
			tmp.add(get(i));
		
		if(object instanceof LList) {
	
			LList objectAsList = (LList) object;
			
			for(int i = 0; i < objectAsList.size(); i++)
				tmp.add(objectAsList.get(i));
		}
		else {
			
			tmp.add(object);
		}
		
		return tmp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#compareTo(de.tuhrig.thofu.types.LObject)
	 */
	public int compareTo(LObject object) {
		
		if(object instanceof LList) {
			
			return Integer.compare(size(), ((LList) object).size());
		}
		
		throw new LException("Can't compare " + this + " and " + object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	@Override
	public String toString() {

		return list.toString().replace("[", "(").replace("]", ")");
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof LList)
			list.equals(((LList) o).list);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#hashCode()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add(LObject e) {

		return this.list.add(e);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, LObject element) {

		this.list.add(index, element);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends LObject> c) {

		return this.list.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends LObject> c) {

		return this.list.addAll(index, c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	@Override
	public void clear() {

		this.list.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {

		return this.list.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {

		return this.list.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	@Override
	public LObject get(int index) {

		if (index >= size())
			return LNull.NULL;

		return this.list.get(index);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object o) {

		return this.list.indexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty() {

		return this.list.isEmpty();
	}

	@Override
	public Iterator<LObject> iterator() {

		return this.list.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(Object o) {

		return this.list.lastIndexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<LObject> listIterator() {

		return this.list.listIterator();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<LObject> listIterator(int index) {

		return this.list.listIterator(index);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {

		return this.list.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	@Override
	public LObject remove(int index) {

		return this.list.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {

		return this.list.removeAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {

		return this.list.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public LObject set(int index, LObject element) {

		return this.list.set(index, element);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#size()
	 */
	@Override
	public int size() {

		return this.list.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public List<LObject> subList(int fromIndex, int toIndex) {

		LList subList = new LList();

		for (LObject o : list.subList(fromIndex, toIndex))
			subList.add(o);

		return subList;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray() {

		return this.list.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {

		return this.list.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#argrumentSize(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public int argrumentSize(LObject object) {

		return getFirst().argrumentSize(object) + 1;
	}
}