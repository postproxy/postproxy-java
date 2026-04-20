package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DeleteOnPlatformResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("deleting") List<DeletingPlatform> deleting
) {}
