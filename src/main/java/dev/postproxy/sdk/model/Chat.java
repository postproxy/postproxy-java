package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Chat(
        @JsonProperty("id") String id,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        /** The page / phone number the chat runs through. */
        @JsonProperty("external_placement_id") String externalPlacementId,
        /** WhatsApp group chat. */
        @JsonProperty("group") boolean group,
        @JsonProperty("participant_external_id") String participantExternalId,
        @JsonProperty("participant_username") String participantUsername,
        @JsonProperty("participant_name") String participantName,
        @JsonProperty("participant_avatar_url") String participantAvatarUrl,
        @JsonProperty("external_conversation_id") String externalConversationId,
        @JsonProperty("last_inbound_at") String lastInboundAt,
        @JsonProperty("last_outbound_at") String lastOutboundAt,
        @JsonProperty("last_message_at") String lastMessageAt,
        /**
         * Facebook and Instagram chats carry {@code thread_control} here once known
         * ({@code owner_app_id}, {@code expires_at}, {@code updated_at}).
         */
        @JsonProperty("metadata") Map<String, Object> metadata,
        @JsonProperty("archived") Boolean archived,
        /**
         * WhatsApp-only: whether the 24-hour customer service window is open. When it's closed
         * only a template can be sent.
         */
        @JsonProperty("within_messaging_window") Boolean withinMessagingWindow,
        @JsonProperty("created_at") String createdAt
) {}
