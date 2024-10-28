package cacheWithTTL;

class CacheEntry< V> {

    private final V value;
    private final long expirationTime;

    public CacheEntry(V value, long ttlMillis) {

        this.value = value;
        this.expirationTime = System.currentTimeMillis() + ttlMillis;
    }


    public V getValue() {
        return value;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

}