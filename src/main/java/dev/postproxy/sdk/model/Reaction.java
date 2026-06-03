package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Reaction(
        @JsonProperty("sender_external_id") String senderExternalId,
        @JsonProperty("emoji") String emoji,
        @JsonProperty("reaction") String reaction,
        @JsonProperty("at") String at
) {}
