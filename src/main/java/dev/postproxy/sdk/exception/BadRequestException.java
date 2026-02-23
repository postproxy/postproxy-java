package dev.postproxy.sdk.exception;

import java.util.Map;

public class BadRequestException extends PostProxyException {

    public BadRequestException(String message, Map<String, Object> response) {
        super(message, 400, response);
    }
}
