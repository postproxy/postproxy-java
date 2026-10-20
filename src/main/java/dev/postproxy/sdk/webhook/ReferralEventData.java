package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Payload of {@code referral.received}. {@code ctwaClid} is the click-to-WhatsApp ad click id. */
public record ReferralEventData(
        @JsonProperty("chat") WebhookChatData chat,
        @JsonProperty("ref") String ref,
        @JsonProperty("source") String source,
        @JsonProperty("type") String type,
        @JsonProperty("ctwa_clid") String ctwaClid,
        @JsonProperty("occurred_at") String occurredAt
) {}
