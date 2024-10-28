package cacheWithTTL;

public interface Cache<K, V> {
    void put(K key, V value, long ttlMillis);



    V get(K key);
    boolean remove(K key);
    void cleanup();
    boolean containsKey(K key);
    void clearCache();
}
