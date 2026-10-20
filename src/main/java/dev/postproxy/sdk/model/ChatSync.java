package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A chat (DM) sync run, started by {@code profiles().backfillChats(...)} or on connect. */
public record ChatSync(
        @JsonProperty("id") String id,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("kind") String kind,
        @JsonProperty("trigger") PostSyncTrigger trigger,
        @JsonProperty("status") PostSyncStatus status,
        @JsonProperty("started_at") String startedAt,
        @JsonProperty("completed_at") String completedAt,
        @JsonProperty("chats_seen") int chatsSeen,
        @JsonProperty("messages_imported") int messagesImported,
        @JsonProperty("backfill_from") String backfillFrom,
        @JsonProperty("oldest_updated_at") String oldestUpdatedAt,
        @JsonProperty("error") String error,
        @JsonProperty("created_at") String createdAt
) {}
