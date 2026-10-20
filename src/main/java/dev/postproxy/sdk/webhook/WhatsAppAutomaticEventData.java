package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/** Payload of {@code whatsapp.automatic_event}. */
public record WhatsAppAutomaticEventData(
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("chat_id") String chatId,
        @JsonProperty("participant_external_id") String participantExternalId,
        @JsonProperty("external_message_id") String externalMessageId,
        @JsonProperty("event_name") String eventName,
        @JsonProperty("ctwa_clid") String ctwaClid,
        @JsonProperty("custom_data") Map<String, Object> customData
) {}
