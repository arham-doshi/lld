package ratelimit;

public interface RateLimiter {
    boolean processRequest(String url);
    int requestRemaining(String url);
}
