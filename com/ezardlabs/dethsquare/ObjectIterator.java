package com.ezardlabs.dethsquare;

import java.util.Iterator;

final class ObjectIterator<T> implements Iterator<T> {
	private final T[] objects;
	private int current = -1;

	public ObjectIterator(T[] objects) {
		this.objects = objects;
	}

	@Override
	public boolean hasNext() {
		return current >= objects.length - 1;
	}

	@Override
	public T next() {
		return objects[++current];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
