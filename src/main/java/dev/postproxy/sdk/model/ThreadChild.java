package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ThreadChild(
        @JsonProperty("id") String id,
        @JsonProperty("body") String body,
        @JsonProperty("media") List<Media> media
) {}
