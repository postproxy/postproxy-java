package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LinkedIn only: a company page URL (or its vanity name) resolved to the URN you can @-mention in
 * a post body. {@code type} is {@code "organization"}.
 */
public record ResolvedMention(
        @JsonProperty("urn") String urn,
        @JsonProperty("type") String type,
        @JsonProperty("name") String name,
        @JsonProperty("vanity_name") String vanityName
) {}
