package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ThreadChild(
        @JsonProperty("id") String id,
        @JsonProperty("body") String body,
        @JsonProperty("media") List<Media> media,
        /** Each thread item is delivered separately; this shows whether it went out. */
        @JsonProperty("platforms") List<PlatformResult> platforms
) {}
