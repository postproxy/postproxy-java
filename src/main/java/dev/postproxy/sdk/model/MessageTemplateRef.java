package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** The WhatsApp template a message was sent from. */
public record MessageTemplateRef(
        @JsonProperty("id") String id,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("name") String name,
        @JsonProperty("language") String language,
        @JsonProperty("variables") List<String> variables
) {}
