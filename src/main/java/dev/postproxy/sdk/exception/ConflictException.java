package dev.postproxy.sdk.exception;

import java.util.Map;

/**
 * 409. Thrown for a duplicate submission ({@code response.get("duplicate_post_id")}),
 * a backfill that is already running ({@code response.get("profile_sync_id")}), or a
 * request whose {@code Idempotency-Key} is still in flight.
 */
public class ConflictException extends PostProxyException {

    public ConflictException(String message, Map<String, Object> response) {
        super(message, 409, response);
    }
}
