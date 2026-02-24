package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PostStats(
        @JsonProperty("platforms") List<PlatformStats> platforms
) {}
