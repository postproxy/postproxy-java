package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PlatformStats(
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") String platform,
        @JsonProperty("records") List<StatsRecord> records
) {}
