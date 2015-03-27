package common.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A two-way hash map.
 * 
 * @author Michael
 * 
 * @param <K>
 *            Type of the primitive of the mapping function
 * @param <V>
 *            Type of the image of the mapping function
 */
public class BijectiveMap<K, V> {

	private HashMap<K, V> map;

	public BijectiveMap() {
		map = new HashMap<K, V>();
	}

	public void put(K k, V v) {
		if (this.containsKey(k) || this.containsValue(v))
			try {
				throw new BijectiveMapException();
			} catch (BijectiveMapException e) {
				e.printStackTrace();
			}
		map.put(k, v);
	}

	public boolean containsKey(K k) {
		return map.containsKey(k);
	}

	public boolean containsValue(V v) {
		return map.containsValue(v);
	}

	public V getValue(K k) {
		return map.get(k);
	}

	public K getKey(V v) {
		Set<K> keySet = map.keySet();

		for (Iterator<K> iterator = keySet.iterator(); iterator.hasNext();) {
			K key = iterator.next();
			if (v.equals(this.getValue(key)))
				return key;
		}
		return null;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int size() {
		return map.size();
	}

	/**
	 * Removes the pair containing the given key from the map. Return the associated value or null, if not successful.
	 * 
	 * @param k
	 * @return
	 */
	public V removeKey(K k) {
		return map.remove(k);
	}

	/**
	 * Removes the pair containing the given value from the map. Return the associated key or null, if not successful.
	 * 
	 * @param v
	 * @return
	 */
	public K removeValue(V v) {
		Set<K> keySet = map.keySet();

		for (Iterator<K> iterator = keySet.iterator(); iterator.hasNext();) {
			K key = iterator.next();
			if (v.equals(this.getValue(key))) {
				map.remove(key);
				return key;
			}
		}
		return null;
	}

}
