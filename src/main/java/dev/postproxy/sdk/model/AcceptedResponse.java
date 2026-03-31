package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AcceptedResponse(
        @JsonProperty("accepted") boolean accepted
) {}
