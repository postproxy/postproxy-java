package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.BlueskyFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BlueskyParams(
        @JsonProperty("format") BlueskyFormat format
) {}
