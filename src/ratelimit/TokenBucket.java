package ratelimit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket implements RateLimiter {
    private final int bucketCapacity;
    private final AtomicInteger currentTokens;
    private final AtomicLong lastRefillTimestamp;
    private final int refillRate;  // tokens per second
    private final long refillTimeInMillis;

    public TokenBucket(int bucketCapacity, int refillRate) {
        this.bucketCapacity = bucketCapacity;
        this.currentTokens = new AtomicInteger(bucketCapacity);
        this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
        this.refillRate = refillRate;
        this.refillTimeInMillis = 1000; // 1 second
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long timePassed = now - lastRefillTimestamp.get();

        if (timePassed < refillTimeInMillis) {
            return;
        }

        int tokensToAdd = (int) ((timePassed / refillTimeInMillis) * refillRate);

        if (tokensToAdd > 0) {
            int currentValue = currentTokens.get();
            int newValue = Math.min(currentValue + tokensToAdd, bucketCapacity);
            currentTokens.set(newValue);
            lastRefillTimestamp.set(now);
        }
    }

    @Override
    public boolean processRequest(String url) {
        refill();  // First refill based on time elapsed
        while (true) {
            int currentValue = currentTokens.get();
            if (currentValue <= 0) {
                return false;
            }

            if (currentTokens.compareAndSet(currentValue, currentValue - 1)) {
                return true;
            }
            // If CAS failed, loop will continue
        }
    }

    @Override
    public int requestRemaining(String url) {
        refill();
        return currentTokens.get();
    }
}
