package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result of accepting ({@code accepted}) or declining ({@code declined}) a collaboration invite. */
public record InstagramCollaborationInviteResult(
        @JsonProperty("media_id") String mediaId,
        @JsonProperty("accepted") Boolean accepted,
        @JsonProperty("declined") Boolean declined
) {}
