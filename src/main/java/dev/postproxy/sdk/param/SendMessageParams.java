package dev.postproxy.sdk.param;

import dev.postproxy.sdk.model.MessageButton;
import dev.postproxy.sdk.model.MessageCard;
import dev.postproxy.sdk.model.QuickReply;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Parameters for sending a direct message.
 *
 * <p>{@code quickReplies}, {@code buttons}, and {@code card} are Facebook and Instagram only — they
 * return 422 on Telegram and Bluesky, where {@code replyMarkup} is the equivalent. They are sent on
 * the JSON path only, so pass {@code media} as hosted URLs rather than {@code mediaFiles} when
 * combining with an attachment.
 */
public record SendMessageParams(
        String body,
        List<String> media,
        List<Path> mediaFiles,
        String tag,
        String replyToExternalId,
        Map<String, Object> replyMarkup,
        List<QuickReply> quickReplies,
        List<MessageButton> buttons,
        /** Requires {@code buttons}. */
        MessageCard card,
        String profileGroupId,
        /** Optional Idempotency-Key: retrying with the same key replays the original response. */
        @com.fasterxml.jackson.annotation.JsonIgnore String idempotencyKey
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private List<String> media;
        private List<Path> mediaFiles;
        private String tag;
        private String replyToExternalId;
        private Map<String, Object> replyMarkup;
        private List<QuickReply> quickReplies;
        private List<MessageButton> buttons;
        private MessageCard card;
        private String profileGroupId;
        private String idempotencyKey;

        public Builder body(String body) { this.body = body; return this; }
        public Builder media(List<String> media) { this.media = media; return this; }
        public Builder mediaFiles(List<Path> mediaFiles) { this.mediaFiles = mediaFiles; return this; }
        public Builder tag(String tag) { this.tag = tag; return this; }
        public Builder replyToExternalId(String replyToExternalId) { this.replyToExternalId = replyToExternalId; return this; }
        public Builder replyMarkup(Map<String, Object> replyMarkup) { this.replyMarkup = replyMarkup; return this; }
        public Builder quickReplies(List<QuickReply> quickReplies) { this.quickReplies = quickReplies; return this; }
        public Builder buttons(List<MessageButton> buttons) { this.buttons = buttons; return this; }
        public Builder card(MessageCard card) { this.card = card; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }

        public SendMessageParams build() {
            return new SendMessageParams(body, media, mediaFiles, tag, replyToExternalId, replyMarkup,
                    quickReplies, buttons, card, profileGroupId, idempotencyKey);
        }
    }
}
