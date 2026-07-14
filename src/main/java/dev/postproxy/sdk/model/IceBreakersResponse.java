package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IceBreakersResponse(
        @JsonProperty("ice_breakers") List<IceBreaker> iceBreakers
) {}
