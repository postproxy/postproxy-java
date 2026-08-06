package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A record of one post pull for a profile — the sync fired when the profile
 * connects, the recurring poll, or a backfill.
 *
 * <p>{@code postsImported} counts posts that were new and got created; it is
 * lower than {@code postsSeen} whenever the run re-read posts you already have.
 * {@code oldestPostedAt} is the publish date of the oldest post the run reached.
 */
public record PostSync(
        @JsonProperty("id") String id,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("kind") String kind,
        @JsonProperty("trigger") PostSyncTrigger trigger,
        @JsonProperty("status") PostSyncStatus status,
        @JsonProperty("started_at") String startedAt,
        @JsonProperty("completed_at") String completedAt,
        @JsonProperty("posts_seen") int postsSeen,
        @JsonProperty("posts_imported") int postsImported,
        @JsonProperty("backfill_from") String backfillFrom,
        @JsonProperty("oldest_posted_at") String oldestPostedAt,
        @JsonProperty("error") String error,
        @JsonProperty("created_at") String createdAt
) {}
