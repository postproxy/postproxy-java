package dev.postproxy.sdk.exception;

import java.util.Map;

public class AuthenticationException extends PostProxyException {

    public AuthenticationException(String message, Map<String, Object> response) {
        super(message, 401, response);
    }
}
