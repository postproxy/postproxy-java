package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Who controls a Facebook / Instagram conversation. Both {@code null} when the thread is idle. */
public record ThreadOwner(
        @JsonProperty("app_id") String appId,
        @JsonProperty("expires_at") String expiresAt
) {}
