package cacheWithTTL;

import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


public class InMemoryCache<K, V> implements Cache<K, V>{
    ConcurrentHashMap<K, CacheEntry<V>> map;
    private final ScheduledExecutorService cleanupExecutor;
    private final PriorityQueue<Pair<Long, K>> ttlQueue;
    private final ReentrantLock lock;

    public InMemoryCache() {
        this.map = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        this.ttlQueue = new PriorityQueue<>();
        this.lock = new ReentrantLock();
        scheduleCleanup();
    }

    private void scheduleCleanup(){
        this.cleanupExecutor.scheduleAtFixedRate(this::cleanup, 1,1, TimeUnit.MINUTES );
    }

    @Override
    public void put(K key, V value, long ttlMillis) {
        // null check here
        if(key == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        if(value == null){
            throw new IllegalArgumentException("Cache value cannot be null");
        }
        if( ttlMillis <= 0) {
            throw new IllegalArgumentException("TTL cannote be 0 or negative");
        }
        lock.lock();
        try {
            CacheEntry<V> oldValue = map.get(key);
            CacheEntry<V> newValue = new CacheEntry<>(value, ttlMillis);
            if(oldValue != null && oldValue.getExpirationTime() > newValue.getExpirationTime()) {
                throw new IllegalArgumentException("new value should only have expiration time more than previous value");
            }
            this.ttlQueue.add(new Pair<>(newValue.getExpirationTime(), key));
            map.put(key, newValue);
        }
        finally {
            lock.unlock();
        }


    }



    @Override
    public V get(K key) {
        lock.lock();
        try{
            CacheEntry<V> v = map.get(key);

            if(v == null) return null;
            if(v.isExpired()) {
                map.remove(key);
                return null;
            }
            return v.getValue();
        }
        finally {
            lock.unlock();
        }

    }

    @Override
    public boolean remove(K key) {

        return map.remove(key) != null;
    }

    @Override
    public void cleanup() {
        lock.lock();
        try{
            Long currentTime = System.currentTimeMillis();
            while(!this.ttlQueue.isEmpty()) {
                Pair<Long, K> current = this.ttlQueue.peek();
                if(current.first < currentTime) {
                    CacheEntry<V> v = map.get(current.second);
                    if(v != null && v.getExpirationTime() == current.first){
                        map.remove(current.second);
                    }

                    this.ttlQueue.remove();
                }
                else{
                    break;
                }
            }
        }
        finally {
            lock.unlock();
        }
        map.entrySet().removeIf(entry -> {
            return entry.getValue().isExpired();
        });
    }

    @Override
    public boolean containsKey(K key) {
        CacheEntry<V> v = map.get(key);
        if(v == null) {
            return false;
        }
        if(v.isExpired()){
            map.remove(key);
            return false;
        }
        return true;
    }

    @Override
    public void clearCache() {
        map.clear();
        ttlQueue.clear();
    }

    public void shutdown() {
        this.cleanupExecutor.shutdown();
    }


}
