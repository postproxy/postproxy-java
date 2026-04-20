package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorDetails(
        @JsonProperty("platform_error_code") String platformErrorCode,
        @JsonProperty("platform_error_subcode") String platformErrorSubcode,
        @JsonProperty("platform_error_message") String platformErrorMessage,
        @JsonProperty("postproxy_note") String postproxyNote
) {}
