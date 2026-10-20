package dev.postproxy.sdk.exception;

import java.util.Map;

/** 429. The platform is rate-limiting; retry after {@link #getRetryAfter()} seconds when known. */
public class RateLimitException extends PostProxyException {

    private final Integer retryAfter;

    public RateLimitException(String message, Map<String, Object> response) {
        this(message, response, null);
    }

    public RateLimitException(String message, Map<String, Object> response, Integer retryAfter) {
        super(message, 429, response);
        this.retryAfter = retryAfter;
    }

    /** Seconds from the {@code Retry-After} header, or null when it was absent or not a number. */
    public Integer getRetryAfter() {
        return retryAfter;
    }
}
