package dev.postproxy.sdk.exception;

import java.util.Map;

/**
 * 403. The account is canceled or past due, or the platform refused the action for lack of
 * permission.
 */
public class ForbiddenException extends PostProxyException {

    public ForbiddenException(String message, Map<String, Object> response) {
        super(message, 403, response);
    }
}
