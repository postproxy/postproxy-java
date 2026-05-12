package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramConnectionResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("profile") SyncProfile profile,
        @JsonProperty("next_step") String nextStep
) {}
