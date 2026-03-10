package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Queue(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("timezone") String timezone,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("jitter") int jitter,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("timeslots") List<Timeslot> timeslots,
        @JsonProperty("posts_count") int postsCount
) {}
