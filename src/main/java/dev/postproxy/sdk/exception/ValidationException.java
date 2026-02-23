package dev.postproxy.sdk.exception;

import java.util.Map;

public class ValidationException extends PostProxyException {

    public ValidationException(String message, Map<String, Object> response) {
        super(message, 422, response);
    }
}
