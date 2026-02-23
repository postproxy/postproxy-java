package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SuccessResponse(
        @JsonProperty("success") boolean success
) {}
