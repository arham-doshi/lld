package cacheWithTTL;

public class Pair<K extends Comparable<K>, V> implements Comparable<Pair<K, V>> {
    K first;
    V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Pair<K, V> other) {
        return this.first.compareTo(other.first);
    }

    public K getFirst() {
        return first;
    }

    @Override
    public String toString() {
        return "(" + first +
                "," + second +
                ") ";
    }

    public void setFirst(K first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }
}
