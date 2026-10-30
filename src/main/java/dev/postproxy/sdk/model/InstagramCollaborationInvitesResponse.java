package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InstagramCollaborationInvitesResponse(
        @JsonProperty("collaboration_invites") List<InstagramCollaborationInvite> collaborationInvites,
        // null on the last page.
        @JsonProperty("next_cursor") String nextCursor
) {}
