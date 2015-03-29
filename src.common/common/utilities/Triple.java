package common.utilities;

public class Triple<K, V, A> {
	private final K key;
	private final V value;
	private final A action;

	public Triple(K key, V value, A action) {
		this.key = key;
		this.value = value;
		this.action = action;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public A getAction() {
		return action;
	}

	public String toString() {
		return "(" + key.toString() + ", " + value.toString() + ", " + action.toString() + ")";
	}
}
