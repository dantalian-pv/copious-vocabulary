package ru.dantalian.copvoc.suggester.api.model;

public class Pair<K, V> {

	private final K key;

	private final V value;

	private Pair(final K aKey, final V aValue) {
		key = aKey;
		value = aValue;
	}

	public static <K, V> Pair<K, V> of(final K aKey, final V aValue) {
		return new Pair<>(aKey, aValue);
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Pair {" + key + ": " + value + "}";
	}

}
