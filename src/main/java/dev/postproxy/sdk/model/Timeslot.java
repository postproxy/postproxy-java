package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Timeslot(
        @JsonProperty("id") int id,
        @JsonProperty("day") int day,
        @JsonProperty("time") String time
) {}
