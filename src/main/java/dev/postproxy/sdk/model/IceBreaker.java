package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Instagram DM ice breaker (FAQ prompt shown when a user opens a chat).
 */
public record IceBreaker(
        @JsonProperty("question") String question,
        @JsonProperty("payload") String payload
) {}
