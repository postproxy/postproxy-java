package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Chat(
        @JsonProperty("id") String id,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("participant_external_id") String participantExternalId,
        @JsonProperty("participant_username") String participantUsername,
        @JsonProperty("participant_name") String participantName,
        @JsonProperty("participant_avatar_url") String participantAvatarUrl,
        @JsonProperty("external_conversation_id") String externalConversationId,
        @JsonProperty("last_inbound_at") String lastInboundAt,
        @JsonProperty("last_outbound_at") String lastOutboundAt,
        @JsonProperty("last_message_at") String lastMessageAt,
        @JsonProperty("metadata") Map<String, Object> metadata,
        @JsonProperty("archived") Boolean archived,
        @JsonProperty("created_at") String createdAt
) {}
