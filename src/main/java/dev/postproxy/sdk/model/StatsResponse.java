package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record StatsResponse(
        @JsonProperty("data") Map<String, PostStats> data
) {}
