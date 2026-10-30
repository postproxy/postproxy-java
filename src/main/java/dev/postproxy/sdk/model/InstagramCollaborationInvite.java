package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A post from another account that invited this Instagram profile as a collaborator. */
public record InstagramCollaborationInvite(
        @JsonProperty("media_id") String mediaId,
        @JsonProperty("media_owner_username") String mediaOwnerUsername,
        @JsonProperty("caption") String caption,
        // Left out by Instagram for videos with copyrighted or licensed audio.
        @JsonProperty("media_url") String mediaUrl
) {}
