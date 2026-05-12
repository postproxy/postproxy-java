package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TelegramParseMode {
    HTML("HTML"),
    MARKDOWN_V2("MarkdownV2");

    private final String value;

    TelegramParseMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
