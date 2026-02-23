package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteResponse(
        @JsonProperty("deleted") boolean deleted
) {}
