package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YouTubePlaylistDeleted(
        @JsonProperty("deleted") Boolean deleted,
        @JsonProperty("playlist_id") String playlistId
) {}
