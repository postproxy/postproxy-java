package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Set on inbound messages created by a tap on an interactive element you sent.
 *
 * <p>Derived from {@code platformData} rather than stored, so it also resolves for taps ingested
 * before PostProxy exposed this field. {@code kind} is one of {@code quick_reply}, {@code postback},
 * or {@code callback_query} — the last is Telegram, so this is not Meta-only even though the send
 * params are.
 */
public record TappedAction(
        @JsonProperty("kind") String kind,
        /** The payload you set on the quick reply, button, or ice breaker. */
        @JsonProperty("payload") String payload,
        /** The label the participant tapped. */
        @JsonProperty("title") String title
) {
    public static final String KIND_QUICK_REPLY = "quick_reply";
    public static final String KIND_POSTBACK = "postback";
    public static final String KIND_CALLBACK_QUERY = "callback_query";
}
