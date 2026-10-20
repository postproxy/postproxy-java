package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowerHistoryRecord(
        @JsonProperty("date") String date,
        @JsonProperty("followers_gained") Integer followersGained,
        @JsonProperty("followers_lost") Integer followersLost,
        @JsonProperty("followers_change") Integer followersChange,
        @JsonProperty("followers_total") Integer followersTotal
) {}
