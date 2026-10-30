package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PinterestBoardDeleted(
        @JsonProperty("deleted") Boolean deleted,
        @JsonProperty("board_id") String boardId
) {}
