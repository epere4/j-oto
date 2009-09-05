/**
 * 
 */
package com.google.code.joto.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is based on {@link HashSet}, but instead of using object equality, it uses object identity (instead of
 * <code>equals</code>, it usess <code>==</code>).
 * @author epere4
 * @author liliana.nu
 * 
 */
public class IdentitySet<E> extends AbstractSet<E> implements Set<E>, Cloneable {

	private HashMap<Integer, E> map;

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public IdentitySet() {
		map = new HashMap<Integer, E>();
	}

	/**
	 * Constructs a new set containing the elements in the specified collection.
	 * The <tt>HashMap</tt> is created with default load factor (0.75) and an
	 * initial capacity sufficient to contain the elements in the specified
	 * collection.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this set.
	 * @throws NullPointerException
	 *             if the specified collection is null.
	 */
	public IdentitySet(Collection<? extends E> c) {
		map = new HashMap<Integer, E>(Math.max((int) (c.size() / .75f) + 1, 16));
		addAll(c);
	}

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
	 * the specified initial capacity and the specified load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the hash map.
	 * @param loadFactor
	 *            the load factor of the hash map.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero, or if the load
	 *             factor is nonpositive.
	 */
	public IdentitySet(int initialCapacity, float loadFactor) {
		map = new HashMap<Integer, E>(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
	 * the specified initial capacity and default load factor, which is
	 * <tt>0.75</tt>.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the hash table.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 */
	public IdentitySet(int initialCapacity) {
		map = new HashMap<Integer, E>(initialCapacity);
	}

	/**
	 * Returns an iterator over the elements in this set. The elements are
	 * returned in no particular order.
	 * 
	 * @return an Iterator over the elements in this set.
	 * @see ConcurrentModificationException
	 */
	public Iterator<E> iterator() {
		return map.values().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 * 
	 * @return the number of elements in this set (its cardinality).
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 * 
	 * @return <tt>true</tt> if this set contains no elements.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested.
	 * @return <tt>true</tt> if this set contains the specified element.
	 */
	public boolean contains(Object o) {
		return map.containsKey(getKeyForObject(o));
	}

	private Integer getKeyForObject(Object o) {
		return o == null ? null : System.identityHashCode(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param o
	 *            element to be added to this set.
	 * @return <tt>true</tt> if the set did not already contain the specified
	 *         element.
	 */
	public boolean add(E o) {
		return map.put(getKeyForObject(o), o) == null;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 * 
	 * @param o
	 *            object to be removed from this set, if present.
	 * @return <tt>true</tt> if the set contained the specified element.
	 */
	public boolean remove(Object o) {
		return map.remove(getKeyForObject(o)) == o;
	}

	/**
	 * Removes all of the elements from this set.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>IdentitySet</tt> instance: the elements
	 * themselves are not cloned.
	 * 
	 * @return a shallow copy of this set.
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			IdentitySet<E> newSet = (IdentitySet<E>) super.clone();
			newSet.map = (HashMap<Integer, E>) map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

}