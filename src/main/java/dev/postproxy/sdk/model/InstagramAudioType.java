package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** Kind of audio to search with {@code profiles().instagramAvailableAudio(...)}. */
public enum InstagramAudioType {
    MUSIC("music"),
    ORIGINAL_SOUND("original_sound");

    private final String value;

    InstagramAudioType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
