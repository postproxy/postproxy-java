package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

/** The chat carried on {@code referral.received}. */
public record WebhookChatData(
        @JsonProperty("id") String id,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("external_placement_id") String externalPlacementId,
        @JsonProperty("group") boolean group,
        @JsonProperty("participant_external_id") String participantExternalId,
        @JsonProperty("participant_username") String participantUsername,
        @JsonProperty("participant_name") String participantName,
        @JsonProperty("last_inbound_at") String lastInboundAt,
        @JsonProperty("last_outbound_at") String lastOutboundAt,
        @JsonProperty("last_message_at") String lastMessageAt
) {}
