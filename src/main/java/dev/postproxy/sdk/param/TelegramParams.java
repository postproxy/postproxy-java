package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.TelegramFormat;
import dev.postproxy.sdk.model.TelegramParseMode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TelegramParams(
        @JsonProperty("format") TelegramFormat format,
        @JsonProperty("chat_id") String chatId,
        @JsonProperty("parse_mode") TelegramParseMode parseMode,
        @JsonProperty("disable_link_preview") Boolean disableLinkPreview,
        @JsonProperty("disable_notification") Boolean disableNotification
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TelegramFormat format;
        private String chatId;
        private TelegramParseMode parseMode;
        private Boolean disableLinkPreview;
        private Boolean disableNotification;

        public Builder format(TelegramFormat format) { this.format = format; return this; }
        public Builder chatId(String chatId) { this.chatId = chatId; return this; }
        public Builder parseMode(TelegramParseMode parseMode) { this.parseMode = parseMode; return this; }
        public Builder disableLinkPreview(Boolean v) { this.disableLinkPreview = v; return this; }
        public Builder disableNotification(Boolean v) { this.disableNotification = v; return this; }

        public TelegramParams build() {
            return new TelegramParams(format, chatId, parseMode, disableLinkPreview, disableNotification);
        }
    }
}
