package com.portingdeadmods.researchd.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;

//TODO: Add this to PDL
/**
 * A collection that maintains unique elements (like a Set) while providing
 * indexed access (like an ArrayList).
 *
 * @param <E> the type of elements in this collection
 */
public class UniqueArray<E> extends ArrayList<E> implements Set<E> {

	/**
	 * Creates an empty UniqueArray.
	 */
	public UniqueArray() {
		super();
	}

	/**
	 * Creates a UniqueArray containing the elements of the specified collection.
	 * Duplicate elements are only added once.
	 *
	 * @param c the collection whose elements are to be added to this UniqueArray
	 */
	public UniqueArray(Collection<? extends E> c) {
		super();
		addAll(c);
	}

	/**
	 * Creates a UniqueArray with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity of the UniqueArray
	 * @throws IllegalArgumentException if the specified initial capacity is negative
	 */
	public UniqueArray(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Appends the specified element to the end of this UniqueArray if it is not already present.
	 *
	 * @param e element to be appended to this UniqueArray
	 * @return true if the element was added, false if it was already present
	 */
	@Override
	public boolean add(E e) {
		if (contains(e)) {
			return false;
		}
		return super.add(e);
	}

	/**
	 * Inserts the specified element at the specified position in this UniqueArray
	 * if it is not already present.
	 *
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	@Override
	public void add(int index, E element) {
		if (contains(element)) {
			return;
		}
		super.add(index, element);
	}

	/**
	 * Adds all of the elements in the specified collection to this UniqueArray
	 * if they're not already present.
	 *
	 * @param c collection containing elements to be added to this UniqueArray
	 * @return true if this UniqueArray changed as a result of the call
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			if (add(e)) {
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Inserts all of the elements in the specified collection into this
	 * UniqueArray at the specified position, if they're not already present.
	 *
	 * @param index index at which to insert the first element
	 * @param c collection containing elements to be added to this UniqueArray
	 * @return true if this UniqueArray changed as a result of the call
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		// First filter out duplicates
		List<E> toAdd = new ArrayList<>();
		for (E e : c) {
			if (!contains(e)) {
				toAdd.add(e);
			}
		}

		// Add only if we have unique elements
		if (toAdd.isEmpty()) {
			return false;
		}

		return super.addAll(index, toAdd);
	}

	/**
	 * Replaces the element at the specified position in this UniqueArray with
	 * the specified element if it's not already present elsewhere in the array.
	 *
	 * @param index index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	@Override
	public E set(int index, E element) {
		// If it's the same element, just allow the replacement
		E current = get(index);
		if (Objects.equals(current, element)) {
			return super.set(index, element);
		}

		// Otherwise check if it exists elsewhere
		int existingIndex = indexOf(element);
		if (existingIndex >= 0 && existingIndex != index) {
			// Element already exists elsewhere, don't replace
			return current;
		}

		return super.set(index, element);
	}

	/**
	 * Returns an array containing all of the elements in this UniqueArray
	 * in proper sequence (from first to last element).
	 *
	 * @return an array containing all of the elements in this UniqueArray
	 */
	@Override
	public Object[] toArray() {
		return super.toArray();
	}

	/**
	 * Returns an array containing all of the elements in this UniqueArray
	 * in proper sequence (from first to last element); the runtime type of
	 * the returned array is that of the specified array.
	 *
	 * @param a the array into which the elements of this UniqueArray are to be stored
	 * @return an array containing the elements of this UniqueArray
	 * @throws ArrayStoreException if the runtime type of the specified array
	 *         is not a supertype of the runtime type of every element in this UniqueArray
	 * @throws NullPointerException if the specified array is null
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
	}
}
