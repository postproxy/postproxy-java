package dev.postproxy.sdk.exception;

import java.util.Map;

public class PostProxyException extends RuntimeException {

    private final int statusCode;
    private final Map<String, Object> response;

    public PostProxyException(String message, int statusCode, Map<String, Object> response) {
        super(message);
        this.statusCode = statusCode;
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getResponse() {
        return response;
    }
}
