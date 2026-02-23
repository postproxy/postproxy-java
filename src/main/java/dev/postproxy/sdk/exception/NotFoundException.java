package dev.postproxy.sdk.exception;

import java.util.Map;

public class NotFoundException extends PostProxyException {

    public NotFoundException(String message, Map<String, Object> response) {
        super(message, 404, response);
    }
}
