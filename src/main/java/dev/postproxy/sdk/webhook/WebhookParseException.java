package dev.postproxy.sdk.webhook;

public class WebhookParseException extends RuntimeException {
    public WebhookParseException(String message) {
        super(message);
    }

    public WebhookParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
