package com.github.ixtf.rsocket.broker.util;

import java.util.Collection;
import java.util.List;

public interface IndexedMap<K, V, IDX> {

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *      {@code null} if this map contains no mapping for the key
     */
    V get(K key);

    /**
     * Associates the specified value and indexable with the specified key in this map.
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param indexable indexable to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *      <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    V put(K key, V value, IDX indexable);

    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *      <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    V remove(K key);

    /**
     * Returns the number of key-value mappings in this map.
     * @return the number of key-value mappings in this map
     */
    int size();

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    boolean isEmpty();

    /**
     * Removes all of the mappings from this map.
     */
    void clear();

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * @return a collection view of the values contained in this map
     */
    Collection<V> values();

    /**
     * Returns a {@link List} view of the values matching the indexable in this map.
     * @param indexable the indexable query parameter
     * @return a list view of the values matching the indexable in this map.
     */
    List<V> query(IDX indexable);
}
