package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Webhook(
        @JsonProperty("id") String id,
        @JsonProperty("url") String url,
        @JsonProperty("events") List<String> events,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("description") String description,
        @JsonProperty("secret") String secret,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {}
