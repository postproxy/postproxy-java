package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** Privacy of a board created with {@code profiles().createPinterestBoard(...)}. */
public enum PinterestBoardPrivacy {
    PUBLIC("PUBLIC"),
    PROTECTED("PROTECTED");

    private final String value;

    PinterestBoardPrivacy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
