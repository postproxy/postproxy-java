package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An Instagram account to tag in a post.
 *
 * <p>Images require {@code x} and {@code y} (floats 0.0-1.0 from the top-left
 * corner); reels and video slides are tagged by username only — Instagram
 * ignores coordinates there. {@code mediaIndex} picks the carousel slide
 * (0-based, defaults to 0).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstagramUserTag(
        @JsonProperty("username") String username,
        @JsonProperty("x") Double x,
        @JsonProperty("y") Double y,
        @JsonProperty("media_index") Integer mediaIndex
) {
    public static InstagramUserTag of(String username, double x, double y) {
        return new InstagramUserTag(username, x, y, null);
    }

    public static InstagramUserTag of(String username, double x, double y, int mediaIndex) {
        return new InstagramUserTag(username, x, y, mediaIndex);
    }

    /** Reels and video slides are tagged by username only. */
    public static InstagramUserTag of(String username) {
        return new InstagramUserTag(username, null, null, null);
    }

    public static InstagramUserTag of(String username, int mediaIndex) {
        return new InstagramUserTag(username, null, null, mediaIndex);
    }
}
