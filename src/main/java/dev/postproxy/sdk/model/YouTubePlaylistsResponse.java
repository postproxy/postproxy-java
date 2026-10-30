package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record YouTubePlaylistsResponse(
        @JsonProperty("data") List<YouTubePlaylist> data,
        // null on the last page.
        @JsonProperty("next_page_token") String nextPageToken
) {}
